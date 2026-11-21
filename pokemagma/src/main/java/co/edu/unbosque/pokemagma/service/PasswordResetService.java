package co.edu.unbosque.pokemagma.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.pokemagma.exception.PasswordNotValidException;
import co.edu.unbosque.pokemagma.model.PasswordResetToken;
import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.repository.PasswordResetTokenRepository;
import co.edu.unbosque.pokemagma.repository.UserRepository;
import jakarta.transaction.Transactional;

/**
 * Gestiona la generación, envío y validación de códigos de restablecimiento.
 */
@Service
public class PasswordResetService {

	/** Tiempo de validez del código enviado. */
	private static final Duration CODE_TTL = Duration.ofMinutes(10);
	/** Tiempo mínimo entre solicitudes de códigos. */
	private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
	/** Cantidad máxima de intentos para ingresar el código. */
	private static final int MAX_ATTEMPTS = 5;

	/** Generador seguro para crear códigos aleatorios. */
	private final SecureRandom random = new SecureRandom();
	/** Repositorio de códigos de restablecimiento. */
	private final PasswordResetTokenRepository tokenRepository;
	/** Repositorio de cuentas de usuario. */
	private final UserRepository userRepository;
	/** Codificador usado para guardar y validar el código. */
	private final PasswordEncoder passwordEncoder;
	/** Servicio que envía los correos. */
	private final EmailService emailService;
	/** Servicio que actualiza la contraseña de la cuenta. */
	private final UserService userService;

	/**
	 * Crea el servicio de restablecimiento de contraseña.
	 *
	 * @param tokenRepository repositorio de códigos
	 * @param userRepository  repositorio de cuentas
	 * @param passwordEncoder codificador y verificador del código
	 * @param emailService    servicio de correo
	 * @param userService     servicio que actualiza la contraseña
	 */
	public PasswordResetService(PasswordResetTokenRepository tokenRepository, UserRepository userRepository,
			PasswordEncoder passwordEncoder, EmailService emailService, UserService userService) {
		super();
		this.tokenRepository = tokenRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
		this.userService = userService;
	}

	/**
	 * Genera y envía un código si existe la cuenta y pasó el tiempo de espera.
	 *
	 * @param email correo asociado a la cuenta
	 */
	@Transactional
	public void requestCode(String email) {
		Optional<User> userFound = userRepository.findByEmail(email);

		if (!userFound.isPresent()) {
			return;
		}

		User user = userFound.get();
		Instant now = Instant.now();
		Optional<PasswordResetToken> tokenFound = tokenRepository.findByUser(user);
		PasswordResetToken token;

		if (tokenFound.isPresent()) {
			token = tokenFound.get();

			if (token.getSentAt() != null && token.getSentAt().plus(RESEND_COOLDOWN).isAfter(now)) {
				return;
			}
		} else {
			token = new PasswordResetToken();
			token.setUser(user);
		}

		String code = String.format("%06d", random.nextInt(1_000_000));

		token.setCodeHash(passwordEncoder.encode(code));
		token.setSentAt(now);
		token.setExpiresAt(now.plus(CODE_TTL));
		token.setAttemps(0);
		tokenRepository.save(token);

		emailService.sendPasswordResetCode(user.getEmail(), user.getUsername(), code);
	}

	/**
	 * Valida el código y cambia la contraseña si corresponde.
	 *
	 * @param email       correo asociado a la cuenta
	 * @param code        código recibido por correo
	 * @param newPassword contraseña nueva
	 * @return 0 si se actualizó; 1 si el correo o el código no son válidos; 2 si el
	 *         código venció; 3 si se agotaron los intentos
	 * @throws PasswordNotValidException si la contraseña incumple los requisitos
	 */
	@Transactional
	public int resetPassword(String email, String code, String newPassword) throws PasswordNotValidException {
		if (newPassword == null
				|| !newPassword.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)" + "(?=.*[^\\p{L}\\p{N}\\s]).{8,15}")) {
			throw new PasswordNotValidException();
		}

		if (code == null || !code.matches("\\d{6}")) {
			return 1;
		}

		Optional<User> userFound = userRepository.findByEmail(email);

		if (!userFound.isPresent()) {
			return 1;
		}

		User user = userFound.get();
		Optional<PasswordResetToken> tokenFound = tokenRepository.findByUser(user);

		if (!tokenFound.isPresent()) {
			return 1;
		}

		PasswordResetToken token = tokenFound.get();
		Instant now = Instant.now();

		if (token.getExpiresAt() == null || !token.getExpiresAt().isAfter(now)) {
			tokenRepository.delete(token);
			return 2;
		}

		if (token.getAttemps() >= MAX_ATTEMPTS) {
			return 3;
		}

		if (!passwordEncoder.matches(code, token.getCodeHash())) {
			token.setAttemps(token.getAttemps() + 1);
			tokenRepository.save(token);
			return 1;
		}

		userService.updatePassword(user, newPassword);
		tokenRepository.delete(token);

		return 0;
	}

}
