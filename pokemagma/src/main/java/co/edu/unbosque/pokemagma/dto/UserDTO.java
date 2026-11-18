package co.edu.unbosque.pokemagma.dto;

import java.time.Instant;
import java.util.Objects;

import co.edu.unbosque.pokemagma.model.User.Role;

/**
 * DTO para los usuarios
 * 
 * Representa los datos de la entidad que se transfiere entre las capas de la
 * aplicacion
 */
public class UserDTO {

	/**
	 * Identificador unico de ususario
	 */
	private Long id;

	/**
	 * Nombre de usuario
	 */
	private String username;

	/**
	 * Correo electronico del usuario
	 */
	private String email;

	/**
	 * Contraseña recibida o codificada dependiendo de laoperacion
	 */
	private String password;

	/**
	 * Rol del usuario
	 */
	private Role role;

	/**
	 * Estado de expiracion de la cuenta
	 */
	private boolean accountNonExpired;

	/**
	 * Estado de bloqueo de la cuenta
	 */
	private boolean accountNonLocked;

	/**
	 * Estado de expiracion de las credenciales
	 */
	private boolean credentialsNonExpired;

	/**
	 * Estado de habilitacion de la cuenta
	 */
	private boolean enable;

	/**
	 * Estado de verificacion del correo
	 */
	private boolean emailVerified;

	/**
	 * Fecha de registro
	 */
	private Instant registeredAt;

	/**
	 * Constructor vacio
	 */
	public UserDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor con los datos basicos del usuario
	 * 
	 * @param username nombre del usuario
	 * @param email    correo electronico
	 * @param password contraseña
	 */
	public UserDTO(String username, String email, String password) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
	}

	/** @return identificador del usuario */
	public Long getId() {
		return id;
	}

	/** @param id identificador del usuario */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return nombre de usuario */
	public String getUsername() {
		return username;
	}

	/** @param username nombre de usuario */
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

	/** @return contraseña */
	public String getPassword() {
		return password;
	}

	/** @param password contraseña */
	public void setPassword(String password) {
		this.password = password;
	}

	/** @return rol */
	public Role getRole() {
		return role;
	}

	/** @param role rol del usuario */
	public void setRole(Role role) {
		this.role = role;
	}

	/** @return true si la cuenta no ha expirado */
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	/** @param value estado de expiración de la cuenta */
	public void setAccountNonExpired(boolean value) {
		accountNonExpired = value;
	}

	/** @return true si la cuenta no está bloqueada */
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	/** @param value estado de bloqueo de la cuenta */
	public void setAccountNonLocked(boolean value) {
		accountNonLocked = value;
	}

	/** @return true si las credenciales no han expirado */
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	/** @param value estado de expiración de las credenciales */
	public void setCredentialsNonExpired(boolean value) {
		credentialsNonExpired = value;
	}

	/** @return true si la cuenta está habilitada */
	public boolean isEnabled() {
		return enable;
	}

	/** @param enabled estado de habilitación */
	public void setEnabled(boolean enabled) {
		this.enable = enabled;
	}

	/** @return true si el correo está verificado */
	public boolean isEmailVerified() {
		return emailVerified;
	}

	/** @param emailVerified estado de verificación */
	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	/** @return fecha de registro */
	public Instant getRegisteredAt() {
		return registeredAt;
	}

	/** @param registeredAt fecha de registro */
	public void setRegisteredAt(Instant registeredAt) {
		this.registeredAt = registeredAt;
	}

	/**
	 * Calcula el hash del DTO a partir de sus campos
	 * 
	 * @return codigo hash
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Boolean.valueOf(accountNonExpired), Boolean.valueOf(accountNonLocked),
				Boolean.valueOf(credentialsNonExpired), email, Boolean.valueOf(emailVerified), Boolean.valueOf(enable),
				id, password, registeredAt, role, username);
	}

	/**
	 * Compara este DTO con otro por sus campos
	 * 
	 * @param obj objeto que se compara
	 * @return true si los campos coinciden
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDTO other = (UserDTO) obj;
		return accountNonExpired == other.accountNonExpired && accountNonLocked == other.accountNonLocked
				&& credentialsNonExpired == other.credentialsNonExpired && Objects.equals(email, other.email)
				&& emailVerified == other.emailVerified && enable == other.enable && Objects.equals(id, other.id)
				&& Objects.equals(password, other.password) && Objects.equals(registeredAt, other.registeredAt)
				&& role == other.role && Objects.equals(username, other.username);
	}

	/**
	 * Devuelve la representacion textual del DTO
	 * 
	 * @return texto con sus datos (sin contener la contraseña)
	 */
	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", username=" + username + ", email=" + email + ", role=" + role
				+ ", accountNonExpired=" + accountNonExpired + ", accountNonLocked=" + accountNonLocked
				+ ", credentialsNonExpired=" + credentialsNonExpired + ", enable=" + enable + ", emailVerified="
				+ emailVerified + ", registeredAt=" + registeredAt + "]";
	}

}
