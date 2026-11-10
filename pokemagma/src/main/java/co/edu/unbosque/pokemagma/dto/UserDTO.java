package co.edu.unbosque.pokemagma.dto;

import java.time.Instant;
import java.util.Objects;

import co.edu.unbosque.pokemagma.model.User.Role;

public class UserDTO {
	
	private Long id;
	private String username;
	private String email;
	private String password;
	private Role role;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enable;
	private boolean emailVerified;
	private Instant registeredAt;
	
	public UserDTO() {
		// TODO Auto-generated constructor stub
	}

	public UserDTO(String username, String email, String password) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public UserDTO(String username, String email, String password, Role role) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
	}

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

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
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

	@Override
	public int hashCode() {
		return Objects.hash(Boolean.valueOf(accountNonExpired), Boolean.valueOf(accountNonLocked),
				Boolean.valueOf(credentialsNonExpired), email, Boolean.valueOf(emailVerified), Boolean.valueOf(enable),
				id, password, registeredAt, role, username);
	}

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

	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", username=" + username + ", email=" + email + ", role=" + role
				+ ", accountNonExpired=" + accountNonExpired + ", accountNonLocked=" + accountNonLocked
				+ ", credentialsNonExpired=" + credentialsNonExpired + ", enable=" + enable + ", emailVerified="
				+ emailVerified + ", registeredAt=" + registeredAt + "]";
	}
	
	

}
