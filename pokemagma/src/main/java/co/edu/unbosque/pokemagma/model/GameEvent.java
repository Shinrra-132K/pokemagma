package co.edu.unbosque.pokemagma.model;

import java.time.Instant;
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
 * Registra un evento de juego o de sesion para la auditoria administrativa
 */
@Entity
@Table(name = "game_event")
public class GameEvent {

	/**
	 * Tipo de evento registrado por la auditoria
	 */
	public enum EventType {
		/**
		 * eventos de inicio de sesion, desconexion, pokemon capturado y evento de
		 * batalla
		 */
		LOGIN, LOGOUT, STARTER_SELECTED, WILD_ENCOUNTER, BATTLE, POKEMON_CAUGHT, ITEM_ACQUIRED, ITEM_USED,
		ACCOUNT_DISABLED
	}

	/**
	 * Identificador unico de evento
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Id del jugador que genero el evento, este puede ser nulo para eventos del
	 * sistema
	 */
	@Column(name = "player_id")
	private Long playerId;

	/**
	 * categoria del evento
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EventType type;

	/**
	 * Descripcion del evento
	 */
	@Column(nullable = false)
	private String description;

	/**
	 * Fecha y hora del evento
	 */
	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	/**
	 * C onstructor vacio
	 */
	public GameEvent() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Crea un evento de auditoria con la fecha y hora actual
	 * 
	 * @param playerId    ID del jugador asociado al evento
	 * @param type        tipo de evento
	 * @param description descripcion del evento
	 */
	public GameEvent(Long playerId, EventType type, String description) {
		super();
		this.playerId = playerId;
		this.type = type;
		this.description = description;
		this.createdAt = Instant.now();
	}

	/**
	 * Obtiene el identificador del evento.
	 *
	 * @return ID del evento
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador del evento.
	 *
	 * @param id identificador del evento
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el identificador del jugador asociado.
	 *
	 * @return ID del jugador, o null si es un evento del sistema
	 */
	public Long getPlayerId() {
		return playerId;
	}

	/**
	 * Establece el identificador del jugador asociado.
	 *
	 * @param playerId identificador del jugador
	 */
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	/**
	 * Obtiene la categoría del evento.
	 *
	 * @return tipo de evento
	 */
	public EventType getType() {
		return type;
	}

	/**
	 * Establece la categoría del evento.
	 *
	 * @param type tipo de evento
	 */
	public void setType(EventType type) {
		this.type = type;
	}

	/**
	 * Obtiene la descripción del evento.
	 *
	 * @return descripción registrada
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Establece la descripción del evento.
	 *
	 * @param description descripción que se registrará
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Obtiene la fecha y hora del evento.
	 *
	 * @return instante en que ocurrió el evento
	 */
	public Instant getCreatedAt() {
		return createdAt;
	}

	/**
	 * Establece la fecha y hora del evento.
	 *
	 * @param createdAt instante en que ocurrió el evento
	 */
	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * Devuelve un hash durante la asignacion de ID
	 * 
	 * @return codigo hash de la clase
	 */
	@Override
	public int hashCode() {
		return Objects.hash(createdAt, description, id, playerId, type);
	}

	/**
	 * Compara eventos por su ID
	 * 
	 * @param obj objeto que se compara
	 * @return true si ambos eventos tienen el mismo ID
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameEvent other = (GameEvent) obj;
		return Objects.equals(createdAt, other.createdAt) && Objects.equals(description, other.description)
				&& Objects.equals(id, other.id) && Objects.equals(playerId, other.playerId) && type == other.type;
	}

	/**
	 * Devuelve una representacion en texto del evento
	 * 
	 * @return texto con los datos del evento
	 */
	@Override
	public String toString() {
		return "GameEvent [id=" + id + ", playerId=" + playerId + ", type=" + type + ", description=" + description
				+ ", createdAt=" + createdAt + "]";
	}

}
