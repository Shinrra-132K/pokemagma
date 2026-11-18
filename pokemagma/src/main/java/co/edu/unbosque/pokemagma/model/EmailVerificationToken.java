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
 * Almacena el token temporal usado para verificar el correo de un usuario
 */
@Entity
@Table(name = "email_verification_token")
public class EmailVerificationToken {

	/**
	 * Identificador generado del token
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Valor unico del token de verificacion
	 */
	@Column(nullable = false)
	private String token;

	/**
	 * Usuario propietario del token
	 */
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	/**
	 * Fecha y hora de expiracion del token
	 */
	@Column(updatable = false)
	private Instant expiresAt;

	/**
	 * Cantidad de intentos para poder usar el token
	 */
	@Column(nullable = false)
	private int attemps;

	/**
	 * Constructor vacio
	 */
	public EmailVerificationToken() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Crea un token de verificación asociado a un usuario y con una fecha de
	 * expiración determinada.
	 *
	 * @param token     valor del token de verificación
	 * @param user      usuario propietario del token
	 * @param expiresAt fecha y hora en que expira el token
	 */
	public EmailVerificationToken(String token, User user, Instant expiresAt) {
		super();
		this.token = token;
		this.user = user;
		this.expiresAt = expiresAt;
	}

	 /**
     * Obtiene el identificador del token.
     *
     * @return identificador del token
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador del token.
     *
     * @param id identificador del token
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el valor del token de verificación.
     *
     * @return valor del token
     */
    public String getToken() {
        return token;
    }

    /**
     * Establece el valor del token de verificación.
     *
     * @param token valor del token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Obtiene el usuario propietario del token.
     *
     * @return usuario asociado
     */
    public User getUser() {
        return user;
    }

    /**
     * Establece el usuario propietario del token.
     *
     * @param user usuario asociado
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Obtiene la fecha y hora de expiración del token.
     *
     * @return instante de expiración
     */
    public Instant getExpiresAt() {
        return expiresAt;
    }

    /**
     * Establece la fecha y hora de expiración del token.
     *
     * @param expiresAt instante de expiración
     */
    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * Obtiene la cantidad de intentos realizados para usar el token.
     *
     * @return cantidad de intentos
     */
    public int getAttemps() {
        return attemps;
    }

    /**
     * Establece la cantidad de intentos realizados para usar el token.
     *
     * @param attemps cantidad de intentos
     */
    public void setAttemps(int attemps) {
        this.attemps = attemps;
    }

}
