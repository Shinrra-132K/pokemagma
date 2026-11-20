package co.edu.unbosque.pokemagma.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidad que almacena temporalmente el código de restablecimiento de una
 * cuenta.
 */
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

	/** Identificador generado del registro. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Hash del código enviado al usuario. */
	@Column(nullable = false)
	private String codeHash;

	/** Usuario propietario del código. */
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	/** Fecha y hora en que se envió el código. */
	@Column(nullable = false)
	private Instant sentAt;

	/** Fecha y hora en que vence el código. */
	@Column(nullable = false)
	private Instant expiresAt;

	/** Cantidad de intentos fallidos de validación. */
	@Column(nullable = false)
	private int attemps;

	/**
	 * Constructor vacío requerido por JPA.
	 */
	public PasswordResetToken() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return identificador del registro
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id identificador del registro
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return hash del código
	 */
	public String getCodeHash() {
		return codeHash;
	}

	/**
	 * @param codeHash hash del código
	 */
	public void setCodeHash(String codeHash) {
		this.codeHash = codeHash;
	}

	/**
	 * @return usuario propietario del código
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user usuario propietario del código
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return instante de vencimiento
	 */
	public Instant getExpiresAt() {
		return expiresAt;
	}

	/**
	 * @param expiresAt instante de vencimiento
	 */
	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	/**
	 * @return cantidad de intentos fallidos
	 */
	public int getAttemps() {
		return attemps;
	}

	/**
	 * @param attemps cantidad de intentos fallidos
	 */
	public void setAttemps(int attemps) {
		this.attemps = attemps;
	}

	/**
	 * @return instante en que se envió el código
	 */
	public Instant getSentAt() {
		return sentAt;
	}

	/**
	 * @param sentAt instante en que se envió el código
	 */
	public void setSentAt(Instant sentAt) {
		this.sentAt = sentAt;
	}

}
