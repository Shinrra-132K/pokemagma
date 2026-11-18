package co.edu.unbosque.pokemagma.model;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Representa la cuenta en el sistema
 * 
 * Implementa UserDetails para que Spring Security para cargar la cuenta,
 * revisar su estado y obtener sus permisos.
 */

@Entity
@Table(name = "useraccount")
public class User implements UserDetails {

	/**
	 * Identificador de la clase
	 */
	private static final long serialVersionUID = 1L;

	// *Nombre de usuario uncio utilizado para iniciar sesion*/
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Nombre de usuario unico utilizado para iniciar sesion */
	@Column(nullable = false, unique = true)
	private String username;

	/* Correo electronico asociado a la cuenta */
	@Column(nullable = false, unique = true)
	private String email;

	/* Contraseña codificada */
	@Column(nullable = false)
	private String password;

	/* Rol que determina los permisos de la cuenta */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	/* Indica si la cuenta no ha expirado */
	@Column(nullable = false)
	private boolean accountNonExpired;

	/* Indica si la cuenta no esta bloqueada */
	@Column(nullable = false)
	private boolean accountNonLocked;

	/* Indica si las credenciales no han expirado */
	@Column(nullable = false)
	private boolean credentialsNonExpired;

	/* Indica si la cuenta esta habilitada */
	@Column(nullable = false)
	private boolean enabled;

	/* Indica si se verifico el correo electronico */
	@Column(nullable = false)
	private boolean emailVerified;

	/* Fecha y hora del registro de la cuenta */
	@Column(updatable = false)
	private Instant registeredAt;

	/* Roles disponibles de las cuentas */
	public enum Role {
		/* Jugador normal y administrador del sistema */
		PLAYER, ADMIN
	}

	/*
	 * Constructor que inicializa los indicadores de cuenta y asigna el rol del
	 * jugador
	 */
	public User() {
		accountNonExpired = true;
		accountNonLocked = true;
		credentialsNonExpired = true;
		enabled = true;
		emailVerified = false;
		role = Role.PLAYER;
		registeredAt = Instant.now();
	}

	/**
	 * Crea una cuenta de jugador con sus credenciales.
	 * 
	 * @param username nombre de usuario
	 * @param email    correo electronico
	 * @param password contraseña codificada
	 */
	public User(String username, String email, String password) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
	}

	/**
	 * Obtiene los roles asignados al usuario Implementacion del metodo de la
	 * interfaz UserDetails
	 * 
	 * @return Coleccion de autoridades del usuario con el prefijo "ROLE_"
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	/** @return identificador de la cuenta */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador de la cuenta.
	 *
	 * @param id identificador de la cuenta
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el nombre de usuario.
	 *
	 * @return nombre de usuario
	 */
	@Override
	public String getUsername() {
		return username;
	}

	/**
	 * Establece el nombre de usuario.
	 *
	 * @param username nombre de usuario
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Obtiene el correo electrónico asociado a la cuenta.
	 *
	 * @return correo electrónico
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Establece el correo electrónico asociado a la cuenta.
	 *
	 * @param email correo electrónico
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Obtiene la contraseña almacenada.
	 *
	 * @return contraseña, que debería estar codificada
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/**
	 * Establece la contraseña de la cuenta.
	 *
	 * @param password contraseña codificada
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Obtiene el rol de la cuenta.
	 *
	 * @return rol del usuario
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * Establece el rol de la cuenta.
	 *
	 * @param role rol que se asignará
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	/**
	 * Indica si la cuenta no ha expirado.
	 *
	 * @return true si la cuenta no ha expirado
	 */
	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	/**
	 * Establece el estado de expiración de la cuenta.
	 *
	 * @param accountNonExpired true si la cuenta no ha expirado
	 */
	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	/**
	 * Indica si la cuenta no está bloqueada.
	 *
	 * @return true si la cuenta no está bloqueada
	 */
	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	/**
	 * Establece el estado de bloqueo de la cuenta.
	 *
	 * @param accountNonLocked true si la cuenta no está bloqueada
	 */
	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	/**
	 * Indica si las credenciales no han expirado.
	 *
	 * @return true si las credenciales no han expirado
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	/**
	 * Establece el estado de expiración de las credenciales.
	 *
	 * @param credentialsNonExpired true si las credenciales no han expirado
	 */
	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	/**
	 * Indica si la cuenta está habilitada.
	 *
	 * @return true si la cuenta está habilitada
	 */
	public boolean isEnabled() {
		return enabled && emailVerified;
	}

	/**
	 * Establece si la cuenta está habilitada.
	 *
	 * @param enabled true para habilitar la cuenta
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Indica si el correo electrónico fue verificado.
	 *
	 * @return true si el correo fue verificado
	 */
	public boolean isEmailVerified() {
		return emailVerified;
	}

	/**
	 * Establece el estado de verificación del correo.
	 *
	 * @param emailVerified true si el correo fue verificado
	 */
	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	/**
	 * Obtiene la fecha y hora de registro.
	 *
	 * @return instante de registro
	 */
	public Instant getRegisteredAt() {
		return registeredAt;
	}

	/**
	 * Establece la fecha y hora de registro.
	 *
	 * @param registeredAt instante de registro
	 */
	public void setRegisteredAt(Instant registeredAt) {
		this.registeredAt = registeredAt;
	}

	/**
	 * Marca el correo electrónico de la cuenta como verificado.
	 */
	public void verifyEmail() {
		emailVerified = true;
	}

	/**
	 * Devuelve un hash durante la signacion del ID por JPA
	 * 
	 * @return codigo hash de la clase
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/**
	 * Compara cuentas por su identificador persistido
	 * 
	 * @param obj objeto que se compara
	 * @return true si corresponde a la misma instancia o al mismo ID persistido
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(id, other.id);
	}

	/**
	 * Devuelve la reprsentacion de las variables
	 * 
	 * @return texto con datos no sensibles de la cuenta
	 */
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email + ", role=" + role + ", emailVerified="
				+ emailVerified + ", registeredAt=" + registeredAt + "]";
	}

}
