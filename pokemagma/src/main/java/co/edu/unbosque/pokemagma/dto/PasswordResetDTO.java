package co.edu.unbosque.pokemagma.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Datos para validar el código y establecer una nueva contraseña.
 */
public class PasswordResetDTO {

	/** Correo asociado a la cuenta. */
	@NotBlank
	@Email
	private String email;

	/** Código de restablecimiento recibido por correo. */
	@NotBlank
	private String code;

	/** Nueva contraseña que se desea establecer. */
	@NotBlank
	private String newPassword;

	/**
	 * Constructor vacío requerido para recibir la solicitud JSON.
	 */
	public PasswordResetDTO() {
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

	/**
	 * @return código de restablecimiento
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code código de restablecimiento
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return nueva contraseña
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * @param newPassword nueva contraseña
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
