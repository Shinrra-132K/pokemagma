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
import co.edu.unbosque.pokemagma.exception.EmailNotVerifiedException;
import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.repository.UserRepository;
import co.edu.unbosque.pokemagma.security.JwtUtil;
import co.edu.unbosque.pokemagma.service.EmailVerificationService;
import co.edu.unbosque.pokemagma.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Endpoints publicos (/auth/**, ya permitidos en SecurityConfig)
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	public record VerifyRequest(@NotBlank String email, @NotBlank @Pattern(regexp = "\\d{6}") String code) {}
	public record ResendRequest(@NotBlank String email) {}
	public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

	private final UserService userService;
	private final UserRepository userRepo;
	private final EmailVerificationService verificationService;
	private final JwtUtil jwtUtil;

	public AuthController(UserService userService, UserRepository userRepo,
			EmailVerificationService verificationService, JwtUtil jwtUtil) {
		this.userService = userService;
		this.userRepo = userRepo;
		this.verificationService = verificationService;
		this.jwtUtil = jwtUtil;
	}

	/** 1) Registro: crea la cuenta (sin verificar) y envia el codigo. NO devuelve JWT. */
	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequestDTO req) {
		UserDTO dto = new UserDTO();
		dto.setUsername(req.getUsername());
		dto.setEmail(req.getEmail());
		dto.setPassword(req.getPassword());

		int result = userService.create(dto);
		if (result == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "El nombre de usuario ya existe"));
		}
		if (result == 2) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "El correo ya esta registrado"));
		}


		try {
			verificationService.resend(dto.getEmail());
		} catch (MailException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message",
					"Cuenta creada, pero no se pudo enviar el codigo. Usa /auth/resend-code"));
		}
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Map.of("message", "Cuenta creada. Revisa tu correo e ingresa el codigo de 6 digitos."));
	}

	/** 2) Valida el codigo ingresado por el usuario. */
	@PostMapping("/verify-email")
	public ResponseEntity<Map<String, String>> verify(@Valid @RequestBody VerifyRequest req) {
		return switch (verificationService.verify(req.email(), req.code())) {
		case 0 -> ResponseEntity.ok(Map.of("message", "Correo verificado. Ya puedes iniciar sesion."));
		case 2 -> ResponseEntity.status(HttpStatus.GONE).body(Map.of("message", "El codigo expiro. Solicita uno nuevo."));
		case 3 -> ResponseEntity.badRequest().body(Map.of("message", "Codigo incorrecto"));
		case 4 -> ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
				.body(Map.of("message", "Demasiados intentos. Solicita un codigo nuevo."));
		default -> ResponseEntity.badRequest().body(Map.of("message", "Codigo o correo invalido"));
		};
	}

	/** 3) Reenvia un codigo nuevo. Respuesta generica para no revelar que correos existen. */
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

	/** 4) Login: solo entrega el JWT si el correo esta verificado. */
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest req) {
		if (userService.validateCredentials(req.username(), req.password()) != 0) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Credenciales invalidas"));
		}
		User user = userRepo.findByUsername(req.username()).orElseThrow();

		if (!user.isEmailVerified()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
					Map.of("code", "EMAIL_NOT_VERIFIED", "message", new EmailNotVerifiedException().getMessage()));
		}
		return ResponseEntity.ok(Map.of("token", jwtUtil.generateToken(user), "type", "Bearer"));
	}

}
