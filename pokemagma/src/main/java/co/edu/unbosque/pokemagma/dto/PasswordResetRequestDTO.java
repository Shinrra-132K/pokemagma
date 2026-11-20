package co.edu.unbosque.pokemagma.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Datos recibidos para solicitar un código de restablecimiento de contraseña.
 */
public class PasswordResetRequestDTO {

	/** Correo asociado a la cuenta que solicita el código. */
	@NotBlank
	@Email
	private String email;

	/**
	 * Constructor vacío requerido para recibir la solicitud JSON.
	 */
	public PasswordResetRequestDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return correo asociado a la cuenta
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email correo asociado a la cuenta
	 */
	public void setEmail(String email) {
		this.email = email;
	}

}
