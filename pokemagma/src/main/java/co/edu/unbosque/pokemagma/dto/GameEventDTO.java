package co.edu.unbosque.pokemagma.dto;

import java.time.Instant;
import java.util.Objects;

import co.edu.unbosque.pokemagma.model.GameEvent.EventType;
/**
 * DTO para transferir un evento de auditoria del juego
 */
public class GameEventDTO {
	/**
	 * Identificador del evento
	 */
	private Long id;
	/**
	 * Identificador del jugador asociada al evento
	 */
	private Long playerId;
	/**
	 * Tipo de evento registrado
	 */
	private EventType type;
	/**
	 * Descripcion del evento
	 */
	private String description;
	/**
	 * Fecha y hora en que ocurrio el evento
	 */
	private Instant createdAt;
	
	/**
	 * 
	 */
	public GameEventDTO() {
		// TODO Auto-generated constructor stub
	}

	public GameEventDTO(Long id, Long playerId, EventType type, String description, Instant createdAt) {
		super();
		this.id = id;
		this.playerId = playerId;
		this.type = type;
		this.description = description;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdAt, description, id, playerId, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameEventDTO other = (GameEventDTO) obj;
		return Objects.equals(createdAt, other.createdAt) && Objects.equals(description, other.description)
				&& Objects.equals(id, other.id) && Objects.equals(playerId, other.playerId) && type == other.type;
	}

	@Override
	public String toString() {
		return "GameEventDTO [id=" + id + ", playerId=" + playerId + ", type=" + type + ", description=" + description
				+ ", createdAt=" + createdAt + "]";
	}
	
	

}
