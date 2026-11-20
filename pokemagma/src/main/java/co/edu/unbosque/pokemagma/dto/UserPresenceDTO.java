package co.edu.unbosque.pokemagma.dto;

import java.time.Instant;

/**
 * DTO con el estado de conexión de un jugador para la auditoría.
 */
public class UserPresenceDTO {

	/** Identificador del usuario. */
	private Long userId;

	/** Nombre de usuario del jugador. */
	private String username;

	/** Estado de conexión: ACTIVE o DISCONNECTED. */
	private String status;

	/** Fecha y hora del último heartbeat, o null si nunca se recibió uno. */
	private Instant lastSeenAt;

	/**
	 * Constructor vacío requerido para deserializar o construir el DTO.
	 */
	public UserPresenceDTO() {

	}

	/**
	 * Crea el DTO con el estado de conexión del jugador.
	 *
	 * @param userId identificador del usuario
	 * @param username nombre de usuario
	 * @param status estado de conexión
	 * @param lastSeenAt fecha y hora del último heartbeat
	 */
	public UserPresenceDTO(Long userId, String username, String status, Instant lastSeenAt) {
		super();
		this.userId = userId;
		this.username = username;
		this.status = status;
		this.lastSeenAt = lastSeenAt;
	}

	/**
	 * @return identificador del usuario
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId identificador del usuario
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return nombre de usuario
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username nombre de usuario
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return estado de conexión
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status estado de conexión
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return fecha y hora del último heartbeat, o null si no se ha recibido
	 */
	public Instant getLastSeenAt() {
		return lastSeenAt;
	}

	/**
	 * @param lastSeenAt fecha y hora del último heartbeat
	 */
	public void setLastSeenAt(Instant lastSeenAt) {
		this.lastSeenAt = lastSeenAt;
	}

}
