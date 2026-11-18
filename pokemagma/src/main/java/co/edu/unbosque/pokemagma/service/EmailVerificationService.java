package co.edu.unbosque.pokemagma.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.pokemagma.model.EmailVerificationToken;
import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.repository.EmailVerificationTokenRepository;
import co.edu.unbosque.pokemagma.repository.UserRepository;
import jakarta.transaction.Transactional;

/**
 * Gestiona el envío, reenvío y validación de los códigos de verificación del
 * correo electrónico.
 */
@Service
public class EmailVerificationService {

	/**
	 * Tiempo de vida del codigo enviado
	 */
	private static final Duration CODE_TTL = Duration.ofMinutes(10);
	private static final int MAX_ATTEMPTS = 5;
	private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);

	private final SecureRandom random = new SecureRandom();
	private final EmailVerificationTokenRepository tokenRepo;
	private final UserRepository userRepo;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;

	/**
	 * Crea el servicio de verificación de correo.
	 *
	 * @param tokenRepo    repositorio de códigos de verificación
	 * @param userRepo     repositorio de usuarios
	 * @param emailService servicio que envía los correos
	 */
	public EmailVerificationService(EmailVerificationTokenRepository tokenRepo, UserRepository userRepo,
			EmailService emailService, PasswordEncoder passwordEncoder) {
		super();
		this.tokenRepo = tokenRepo;
		this.userRepo = userRepo;
		this.emailService = emailService;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Genera, guarda y envía un nuevo código al usuario.
	 *
	 * Si ya existe un código para el usuario, lo reemplaza. El código expira
	 * después del período definido en CODE_TTL
	 *
	 * @param user cuenta a la que se enviará el código
	 */
	@Transactional
	public void sendCode(User user) {
		String code = String.format("%06d", random.nextInt(1_000_000));

		EmailVerificationToken t = tokenRepo.findByUser(user).orElseGet(EmailVerificationToken::new);
		t.setUser(user);
		t.setToken(passwordEncoder.encode(code));
		t.setExpiresAt(Instant.now().plus(CODE_TTL));
		t.setAttemps(0);
		tokenRepo.save(t);

		emailService.sendVerificationCode(user.getEmail(), user.getUsername(), code);
	}

	/**
	 * Reenvía el código de verificación a una dirección de correo.
	 *
	 * @param email correo al que se solicita enviar el código
	 * @return 0 si se envió; 1 si no existe una cuenta con ese correo; 2 si el
	 *         correo ya está verificado
	 */
	@Transactional
	public int resend(String email) {
		Optional<User> found = userRepo.findByEmail(email);

		if (found.isEmpty()) {
			return 1;
		}

		User user = found.get();

		if (user.isEmailVerified()) {
			return 2;
		}

		Optional<EmailVerificationToken> existing = tokenRepo.findByUser(user);

		if (existing.isPresent() && existing.get().getExpiresAt() != null) {
			Instant sentAt = existing.get().getExpiresAt().minus(CODE_TTL);
			Instant nextAllowedAt = sentAt.plus(RESEND_COOLDOWN);

			if (nextAllowedAt.isAfter(Instant.now())) {
				return 5;
			}
		}

		sendCode(user);
		return 0;
	}

	/**
	 * Comprueba el código recibido y verifica el correo si coincide.
	 *
	 * @param email correo asociado a la cuenta
	 * @param code  código de seis dígitos recibido
	 * @return 0 si el correo quedó verificado o ya estaba verificado; 1 si no se
	 *         encontró la cuenta o el código; 2 si el código expiró; 3 si el código
	 *         no coincide; 4 si se alcanzó el máximo de intentos
	 */
	@Transactional
	public int verify(String email, String code) {
		Optional<User> found = userRepo.findByEmail(email);

		if (found.isEmpty()) {
			return 1;
		}

		User user = found.get();

		if (user.isEmailVerified()) {
			return 0;
		}

		Optional<EmailVerificationToken> tokenFound = tokenRepo.findByUser(user);

		if (tokenFound.isEmpty()) {
			return 1;
		}

		EmailVerificationToken token = tokenFound.get();

		if (token.getExpiresAt() == null || !token.getExpiresAt().isAfter(Instant.now())) {
			tokenRepo.delete(token);
			return 2;
		}

		if (token.getAttemps() >= MAX_ATTEMPTS) {
			return 4;
		}

		if (!passwordEncoder.matches(code, token.getToken())) {
			token.setAttemps(token.getAttemps() + 1);
			return 3;
		}

		user.verifyEmail();
		userRepo.save(user);
		tokenRepo.delete(token);

		return 0;
	}

}
