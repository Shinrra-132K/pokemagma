package co.edu.unbosque.pokemagma.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.pokemagma.dto.RegisterRequestDTO;
import co.edu.unbosque.pokemagma.dto.UserDTO;
import co.edu.unbosque.pokemagma.exception.EmailAlreadyRegisteredException;
import co.edu.unbosque.pokemagma.exception.EmailNotValidException;
import co.edu.unbosque.pokemagma.exception.EmailNotVerifiedException;
import co.edu.unbosque.pokemagma.exception.InvalidCredentialException;
import co.edu.unbosque.pokemagma.exception.PasswordNotValidException;
import co.edu.unbosque.pokemagma.exception.UsernameAlreadyTakenException;
import co.edu.unbosque.pokemagma.exception.UsernameNotValidException;
import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.repository.UserRepository;
import co.edu.unbosque.pokemagma.security.JwtUtil;
import co.edu.unbosque.pokemagma.service.EmailVerificationService;
import co.edu.unbosque.pokemagma.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Controlador de registro, verificacion de correo e inicio de sesion.
 * 
 * Las rutas bajo a /auth son publicas segun la configuracion de seguridad, el
 * inicio de sesiondevuelve un JWT solamente cuando el correo de la cuenta esta
 * verificado.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

	/**
	 * Datos requeridos para verificar una dirección de correo.
	 *
	 * @param email dirección de correo registrada
	 * @param code  código de verificación de seis dígitos
	 */
	public record VerifyRequest(@NotBlank String email, @NotBlank @Pattern(regexp = "\\d{6}") String code) {
	}

	/**
	 * Datos requeridos para solicitar el reenvío del código de verificación.
	 *
	 * @param email dirección de correo registrada
	 */
	public record ResendRequest(@NotBlank String email) {
	}

	/**
	 * Credenciales requeridas para iniciar sesión.
	 *
	 * @param username nombre de usuario
	 * @param password contraseña
	 */
	public record LoginRequest(@NotBlank String username, @NotBlank String password) {
	}

	private final UserService userService;
	private final UserRepository userRepo;
	private final EmailVerificationService verificationService;
	private final JwtUtil jwtUtil;

	/**
	 * Crea el controlador de autenticación.
	 *
	 * @param userService         servicio de gestión de usuarios
	 * @param userRepo            repositorio para buscar la cuenta autenticada
	 * @param verificationService servicio de verificación de correo
	 * @param jwtUtil             componente que genera tokens JWT
	 */
	public AuthController(UserService userService, UserRepository userRepo,
			EmailVerificationService verificationService, JwtUtil jwtUtil) {
		this.userService = userService;
		this.userRepo = userRepo;
		this.verificationService = verificationService;
		this.jwtUtil = jwtUtil;
	}

	/**
	 * Registra una cuenta de jugador y solicita el envío del código de
	 * verificación.
	 *
	 * @param request datos de registro
	 * @return mensaje de resultado, 201 si la cuenta fue creada, 409 si el nombre
	 *         de usuario o el correo ya están registrados
	 */
	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequestDTO request)
			throws UsernameNotValidException, EmailNotValidException, PasswordNotValidException,
			UsernameAlreadyTakenException, EmailAlreadyRegisteredException {

		UserDTO user = new UserDTO();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());

		userService.validateRegistrationData(user);

		int result = userService.create(user);

		if (result == 1) {
			throw new UsernameAlreadyTakenException();
		}

		if (result == 2) {
			throw new EmailAlreadyRegisteredException();
		}

		if (result != 0) {
			return ResponseEntity.internalServerError().body(Map.of("message", "No se pudo crear la cuenta."));
		}

		try {
			verificationService.resend(user.getEmail());
		} catch (MailException exception) {
			return ResponseEntity.status(HttpStatus.CREATED).body(
					Map.of("message", "Cuenta creada, pero no se pudo enviar el código. " + "Usa /auth/resend-code"));
		}

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Map.of("message", "Cuenta creada. Revisa tu correo e ingresa el código " + "de 6 dígitos."));
	}

	/**
	 * Comprueba el código de verificación enviado al correo.
	 *
	 * @param req dirección de correo y código recibido
	 * @return mensaje indicando el resultado de la verificación
	 */
	@PostMapping("/verify-email")
	public ResponseEntity<Map<String, String>> verify(@Valid @RequestBody VerifyRequest req) {
		return switch (verificationService.verify(req.email(), req.code())) {
		case 0 -> ResponseEntity.ok(Map.of("message", "Correo verificado. Ya puedes iniciar sesion."));
		case 2 ->
			ResponseEntity.status(HttpStatus.GONE).body(Map.of("message", "El codigo expiro. Solicita uno nuevo."));
		case 3 -> ResponseEntity.badRequest().body(Map.of("message", "Codigo incorrecto"));
		case 4 -> ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
				.body(Map.of("message", "Demasiados intentos. Solicita un codigo nuevo."));
		default -> ResponseEntity.badRequest().body(Map.of("message", "Codigo o correo invalido"));
		};
	}

	/**
	 * Solicita el envío de un nuevo código de verificación.
	 *
	 * @param req dirección de correo a la que se enviará el código
	 * @return mensaje genérico para no revelar si el correo está registrado
	 */
	@PostMapping("/resend-code")
	public ResponseEntity<Map<String, String>> resend(@Valid @RequestBody ResendRequest req) {
		try {
			verificationService.resend(req.email());
		} catch (MailException e) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
					.body(Map.of("message", "No se pudo enviar el correo. Intenta mas tarde."));
		}
		return ResponseEntity.ok(Map.of("message", "Si el correo existe y no esta verificado, se envio un codigo."));
	}

	/**
	 * Valida las credenciales y genera un JWT para una cuenta verificada.
	 *
	 * @param req nombre de usuario y contraseña
	 * @return token Bearer si la autenticación fue exitosa; 401 si las credenciales
	 *         son incorrectas; 403 si falta verificar el correo
	 */
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest req)
			throws InvalidCredentialException, EmailNotVerifiedException {
		if (userService.validateCredentials(req.username(), req.password()) != 0) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Credenciales invalidas"));
		}
		User user = userRepo.findByUsername(req.username()).orElseThrow();

		if (!user.isEmailVerified()) {
			throw new EmailNotVerifiedException();
		}
		return ResponseEntity.ok(Map.of("token", jwtUtil.generateToken(user), "type", "Bearer"));
	}

}
