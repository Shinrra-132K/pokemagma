package co.edu.unbosque.pokemagma.model;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

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
 * Implementa {@UserDetails} para que Spring Security para cargar
 * la cuenta, revisar su estado y obtener sus permisos.
 */

@Entity
@Table(name = "useraccount")
//implements userdetails
public class User {

	//*Nombre de usuario uncio utilizado para iniciar sesion*/
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**Nombre de usuario unico utilizado para iniciar sesion*/
	@Column(nullable = false, unique = true)
	private String username;

	/*Correo electronico asociado a la cuenta*/
	@Column(nullable = false, unique = true)
	private String email;

	/*Contraseña codificada*/
	@Column(nullable = false)
	private String password;

	/*Rol que determina los permisos de la cuenta*/
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	/*Indica si la cuenta no ha expirado*/
	@Column(nullable = false)
	private boolean accountNonExpired;

	/*Indica si la cuenta no esta bloqueada*/
	@Column(nullable = false)
	private boolean accountNonLocked;

	/*Indica si las credenciales no han expirado*/
	@Column(nullable = false)
	private boolean credentialsNonExpired;

	/*Indica si la cuenta esta habilitada*/
	@Column(nullable = false)
	private boolean enabled;

	/*Indica si se verifico el correo electronico*/
	@Column(nullable = false)
	private boolean emailVerified;

	/*Fecha y hora del registro de la cuenta*/
	@Column(nullable = false, updatable = false)
	private Instant registeredAt;

	/*Roles disponibles de las cuentas*/
	public enum Role {
		/*Jugador normal y administrador del sistema*/
		PLAYER, ADMIN
	}

	/*
	 * Constructor que inicializa los indicadores de cuenta y asigna el rol del jugador*/
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
	 * @param email correo electronico
	 * @param password contraseña codificada
	 */
	public User(String username, String email, String password) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
	}
	
	/* pendiente spring security
	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    */

	
	/**
	 * getters y setters de todas las variables
	 * 
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public Instant getRegisteredAt() {
		return registeredAt;
	}

	public void setRegisteredAt(Instant registeredAt) {
		this.registeredAt = registeredAt;
	}

	/**
	 * Marca como verificado el correo electronico de la cuenta
	 */
	public void verifyEmail() {
		emailVerified = true;
	}

	
	/**
	 * Devuelve un hash durante la signacion del ID por JPA
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
