package co.edu.unbosque.pokemagma.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centraliza el manejo de excepciones propias y las convierte en respuestas
 * HTTP.
 */
@RestControllerAdvice
public class ExceptionHandler {

	/**
	 * Maneja los recursos que no se encontraron.
	 *
	 * @param exception excepción recibida
	 * @return respuesta 404 con el mensaje del error
	 */
	@org.springframework.web.bind.annotation.ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException exception) {
		return response(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	/**
	 * Maneja cantidades inválidas o unidades de objetos no disponibles.
	 *
	 * @param exception excepción recibida
	 * @return respuesta 400 con el mensaje del error
	 */
	@org.springframework.web.bind.annotation.ExceptionHandler(QuantityNotValidException.class)
	public ResponseEntity<Map<String, String>> handleInvalidQuantity(QuantityNotValidException exception) {
		return response(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	/**
	 * Maneja acciones incompatibles con el estado actual del juego.
	 *
	 * @param exception excepción recibida
	 * @return respuesta 409 con el mensaje del error
	 */
	@org.springframework.web.bind.annotation.ExceptionHandler(GameRuleException.class)
	public ResponseEntity<Map<String, String>> handleInvalidGameAction(GameRuleException exception) {
		return response(HttpStatus.CONFLICT, exception.getMessage());
	}

	/**
	 * Maneja errores al consultar un servicio externo.
	 *
	 * @param exception excepción recibida
	 * @return respuesta 503 con el mensaje del error
	 */
	@org.springframework.web.bind.annotation.ExceptionHandler(ExternalServiceException.class)
	public ResponseEntity<Map<String, String>> handleExternalServiceFailure(ExternalServiceException exception) {
		return response(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
	}

	/**
	 * Maneja correos que ya pertenecen a otra cuenta.
	 *
	 * @param exception excepción recibida
	 * @return respuesta 409 con el mensaje del error
	 */
	@org.springframework.web.bind.annotation.ExceptionHandler(EmailAlreadyRegisteredException.class)
	public ResponseEntity<Map<String, String>> handleEmailAlreadyRegistered(EmailAlreadyRegisteredException exception) {
		return response(HttpStatus.CONFLICT, exception.getMessage());
	}

	/**
	 * Maneja correos con formato inválido.
	 *
	 * @param exception excepción recibida
	 * @return respuesta 400 con el mensaje del error
	 */
	@org.springframework.web.bind.annotation.ExceptionHandler(EmailNotValidException.class)
	public ResponseEntity<Map<String, String>> handleEmailNotValid(EmailNotValidException exception) {
		return response(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	/**
	 * Maneja intentos de acceso con el correo aún sin verificar.
	 *
	 * @param exception excepción recibida
	 * @return respuesta 403 con el mensaje del error
	 */
	@org.springframework.web.bind.annotation.ExceptionHandler(EmailNotVerifiedException.class)
	public ResponseEntity<Map<String, String>> handleEmailNotVerified(EmailNotVerifiedException exception) {
		return response(HttpStatus.FORBIDDEN, exception.getMessage());
	}

	/**
	 * Maneja perfiles sin género requerido.
	 *
	 * @param exception excepción recibida
	 * @return respuesta 400 con el mensaje del error
	 */
	@org.springframework.web.bind.annotation.ExceptionHandler(GenderNotValidException.class)
	public ResponseEntity<Map<String, String>> handleGenderNotValid(GenderNotValidException exception) {
		return response(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	/**
	 * Maneja nombres de usuario o contraseñas incorrectos durante el login.
	 *
	 * @param exception excepción recibida
	 * @return respuesta 401 con el mensaje del error
	 */
	@org.springframework.web.bind.annotation.ExceptionHandler(InvalidCredentialException.class)
	public ResponseEntity<Map<String, String>> handleInvalidCredentials(InvalidCredentialException exception) {
		return response(HttpStatus.UNAUTHORIZED, exception.getMessage());
	}

	/**
	 * Maneja contraseñas que incumplen los requisitos de registro.
	 *
	 * @param exception excepción recibida
	 * @return respuesta 400 con el mensaje del error
	 */
	@org.springframework.web.bind.annotation.ExceptionHandler(PasswordNotValidException.class)
	public ResponseEntity<Map<String, String>> handlePasswordNotValid(PasswordNotValidException exception) {
		return response(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	/**
	 * Maneja nombres de usuario que ya están registrados.
	 *
	 * @param exception excepción recibida
	 * @return respuesta 409 con el mensaje del error
	 */
	@org.springframework.web.bind.annotation.ExceptionHandler(UsernameAlreadyTakenException.class)
	public ResponseEntity<Map<String, String>> handleUsernameAlreadyTaken(UsernameAlreadyTakenException exception) {
		return response(HttpStatus.CONFLICT, exception.getMessage());
	}

	/**
	 * Maneja nombres de usuario que incumplen las reglas de formato.
	 *
	 * @param exception excepción recibida
	 * @return respuesta 400 con el mensaje del error
	 */
	@org.springframework.web.bind.annotation.ExceptionHandler(UsernameNotValidException.class)
	public ResponseEntity<Map<String, String>> handleUsernameNotValid(UsernameNotValidException exception) {
		return response(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	/**
	 * Construye el cuerpo estándar de respuesta.
	 *
	 * @param status  estado HTTP que se devolverá
	 * @param message mensaje del error
	 * @return respuesta que contiene la propiedad JSON {@code message}
	 */
	private ResponseEntity<Map<String, String>> response(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(Map.of("message", message));
	}
}
