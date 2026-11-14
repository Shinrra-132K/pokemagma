package co.edu.unbosque.pokemagma.dto;

import java.util.Objects;

import co.edu.unbosque.pokemagma.model.PlayerProfile.Gender;

public class RegisterRequestDTO {

	/** Nombre de usuario solicitado para la cuenta. */
	private String username;

	/** Correo electrónico que se asociará a la cuenta. */
	private String email;

	/** Contraseña recibida para crear la cuenta. */
	private String password;

	/** Género elegido para determinar la apariencia inicial del entrenador. */
	private Gender gender;

	/** Constructor vacío requerido para recibir el DTO desde la API. */
	public RegisterRequestDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Crea una solicitud de registro con todos sus datos.
	 *
	 * @param username nombre de usuario
	 * @param email    correo electrónico
	 * @param password contraseña
	 * @param gender   género seleccionado
	 */
	public RegisterRequestDTO(String username, String email, String password, Gender gender) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.gender = gender;
	}

	/** @return nombre de usuario solicitado */
	public String getUsername() {
		return username;
	}

	/** @param username nombre de usuario solicitado */
	public void setUsername(String username) {
		this.username = username;
	}

	/** @return correo electrónico */
	public String getEmail() {
		return email;
	}

	/** @param email correo electrónico */
	public void setEmail(String email) {
		this.email = email;
	}

	/** @return contraseña recibida */
	public String getPassword() {
		return password;
	}

	/** @param password contraseña recibida */
	public void setPassword(String password) {
		this.password = password;
	}

	/** @return género seleccionado */
	public Gender getGender() {
		return gender;
	}

	/** @param gender género seleccionado */
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	/**
	 * Calcula el código hash a partir de los campos del DTO.
	 *
	 * @return código hash
	 */
	@Override
	public int hashCode() {
		return Objects.hash(email, gender, password, username);
	}

	/**
	 * Compara esta solicitud de registro con otro objeto.
	 *
	 * @param obj objeto que se comparará
	 * @return true si ambos objetos tienen los mismos datos
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegisterRequestDTO other = (RegisterRequestDTO) obj;
		return Objects.equals(email, other.email) && gender == other.gender && Objects.equals(password, other.password)
				&& Objects.equals(username, other.username);
	}

	/**
	 * Devuelve la representacion textual del DTO
	 * 
	 * @return texto con sus datos sin revelar la contraseña
	 */
	@Override
	public String toString() {
		return "RegisterRequestDTO [username=" + username + ", email=" + email + ", gender="
				+ gender + "]";
	}
	
	

}
