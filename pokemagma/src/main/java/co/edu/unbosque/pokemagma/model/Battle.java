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

/** Entidad que conserva el estado de un combate contra un Pokémon salvaje. */
@Entity
@Table(name = "battle")
public class Battle {

	/** Estados posibles de un combate. */
	public enum Status {
		ACTIVE, WON, LOST, FLED, CAPTURED
	}

	/**
	 * 
	 * Identificador generado del combate.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 
	 * Identificador del jugador participante.
	 */
	@Column(name = "player_id", nullable = false)
	private Long playerId;

	/**
	 * 
	 * Identificador del Pokémon activo del jugador.
	 */
	@Column(name = "player_pokemon_id", nullable = false)
	private Long playerPokemonId;

	/**
	 * 
	 * Identificador de especie del Pokémon salvaje.
	 */
	@Column(name = "wild_species_id", nullable = false)
	private Integer wildSpeciesId;

	/**
	 * 
	 * Nombre del Pokémon salvaje.
	 */
	@Column(name = "wild_name", nullable = false)
	private String wildName;

	/**
	 * 
	 * Nivel del Pokémon salvaje.
	 */
	@Column(name = "wild_level", nullable = false)
	private int wildLevel;

	/**
	 * 
	 * Puntos de salud actuales del Pokémon salvaje.
	 */
	@Column(name = "wild_current_hp", nullable = false)
	private int wildCurrentHp;

	/**
	 * 
	 * Puntos de salud máximos del Pokémon salvaje.
	 */
	@Column(name = "wild_max_hp", nullable = false)
	private int wildMaxHp;

	/**
	 * 
	 * Estadística de ataque del Pokémon salvaje.
	 */
	@Column(name = "wild_attack", nullable = false)
	private int wildAttack;

	/**
	 * 
	 * Estadística de defensa del Pokémon salvaje.
	 */
	@Column(name = "wild_defense", nullable = false)
	private int wildDefense;

	/**
	 * 
	 * Estado actual del combate.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	/**
	 * 
	 * Número del turno actual.
	 */
	@Column(name = "turn_number", nullable = false)
	private int turnNumber;

	/**
	 * 
	 * Fecha y hora de inicio del combate.
	 */
	@Column(name = "started_at", nullable = false, updatable = false)
	private Instant startedAt;

	/**
	 * 
	 * Fecha y hora de finalización del combate.
	 */
	@Column(name = "finished_at")
	private Instant finishedAt;

	/**
	 * Constructor vacio
	 */
	public Battle() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Crea un combate contra un pokemon salvaje, el combate inicia con la salud del
	 * salvaje llena, en estado actvio y en el primer turno
	 * 
	 * @param playerId
	 * @param playerPokemonId
	 * @param wildSpeciesId
	 * @param wildName
	 * @param wildLevel
	 * @param wildMaxHp
	 * @param wildAttack
	 * @param wildDefense
	 */
	public Battle(Long playerId, Long playerPokemonId, Integer wildSpeciesId, String wildName, int wildLevel,
			int wildMaxHp, int wildAttack, int wildDefense) {
		this.playerId = playerId;
		this.playerPokemonId = playerPokemonId;
		this.wildSpeciesId = wildSpeciesId;
		this.wildName = wildName;
		this.wildLevel = wildLevel;
		this.wildCurrentHp = wildMaxHp;
		this.wildMaxHp = wildMaxHp;
		this.wildAttack = wildAttack;
		this.wildDefense = wildDefense;
		this.status = Status.ACTIVE;
		this.turnNumber = 1;
		this.startedAt = Instant.now();
	}

	/**
	 * Finaliza el combate y registra su estado.
	 * 
	 * @param finalStatus final status
	 */
	public void finish(Status finalStatus) {
		this.status = finalStatus;
		this.finishedAt = Instant.now();
	}

	/**
	 * Obtiene Identificador generado del combate.
	 * 
	 * @return Identificador generado del combate.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Actualiza Identificador generado del combate.
	 * 
	 * @param id nuevo valor de Identificador generado del combate.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene Identificador del jugador participante.
	 * 
	 * @return Identificador del jugador participante.
	 */
	public Long getPlayerId() {
		return playerId;
	}

	/**
	 * Actualiza Identificador del jugador participante.
	 * 
	 * @param playerId nuevo valor de Identificador del jugador participante.
	 */
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	/**
	 * Obtiene Identificador del Pokémon activo del jugador.
	 * 
	 * @return Identificador del Pokémon activo del jugador.
	 */
	public Long getPlayerPokemonId() {
		return playerPokemonId;
	}

	/**
	 * Actualiza Identificador del Pokémon activo del jugador.
	 * 
	 * @param playerPokemonId nuevo valor de Identificador del Pokémon activo del
	 *                        jugador.
	 */
	public void setPlayerPokemonId(Long playerPokemonId) {
		this.playerPokemonId = playerPokemonId;
	}

	/**
	 * Obtiene Identificador de especie del Pokémon salvaje.
	 * 
	 * @return Identificador de especie del Pokémon salvaje.
	 */
	public Integer getWildSpeciesId() {
		return wildSpeciesId;
	}

	/**
	 * Actualiza Identificador de especie del Pokémon salvaje.
	 * 
	 * @param wildSpeciesId nuevo valor de Identificador de especie del Pokémon
	 *                      salvaje.
	 */
	public void setWildSpeciesId(Integer wildSpeciesId) {
		this.wildSpeciesId = wildSpeciesId;
	}

	/**
	 * Obtiene Nombre del Pokémon salvaje.
	 * 
	 * @return Nombre del Pokémon salvaje.
	 */
	public String getWildName() {
		return wildName;
	}

	/**
	 * Actualiza Nombre del Pokémon salvaje.
	 * 
	 * @param wildName nuevo valor de Nombre del Pokémon salvaje.
	 */
	public void setWildName(String wildName) {
		this.wildName = wildName;
	}

	/**
	 * Obtiene Nivel del Pokémon salvaje.
	 * 
	 * @return Nivel del Pokémon salvaje.
	 */
	public int getWildLevel() {
		return wildLevel;
	}

	/**
	 * Actualiza Nivel del Pokémon salvaje.
	 * 
	 * @param wildLevel nuevo valor de Nivel del Pokémon salvaje.
	 */
	public void setWildLevel(int wildLevel) {
		this.wildLevel = wildLevel;
	}

	/**
	 * Obtiene Puntos de salud actuales del Pokémon salvaje.
	 * 
	 * @return Puntos de salud actuales del Pokémon salvaje.
	 */
	public int getWildCurrentHp() {
		return wildCurrentHp;
	}

	/**
	 * Actualiza Puntos de salud actuales del Pokémon salvaje.
	 * 
	 * @param wildCurrentHp nuevo valor de Puntos de salud actuales del Pokémon
	 *                      salvaje.
	 */
	public void setWildCurrentHp(int wildCurrentHp) {
		this.wildCurrentHp = wildCurrentHp;
	}

	/**
	 * Obtiene Puntos de salud máximos del Pokémon salvaje.
	 * 
	 * @return Puntos de salud máximos del Pokémon salvaje.
	 */
	public int getWildMaxHp() {
		return wildMaxHp;
	}

	/**
	 * Actualiza Puntos de salud máximos del Pokémon salvaje.
	 * 
	 * @param wildMaxHp nuevo valor de Puntos de salud máximos del Pokémon salvaje.
	 */
	public void setWildMaxHp(int wildMaxHp) {
		this.wildMaxHp = wildMaxHp;
	}

	/**
	 * Obtiene Estadística de ataque del Pokémon salvaje.
	 * 
	 * @return Estadística de ataque del Pokémon salvaje.
	 */
	public int getWildAttack() {
		return wildAttack;
	}

	/**
	 * Actualiza Estadística de ataque del Pokémon salvaje.
	 * 
	 * @param wildAttack nuevo valor de Estadística de ataque del Pokémon salvaje.
	 */
	public void setWildAttack(int wildAttack) {
		this.wildAttack = wildAttack;
	}

	/**
	 * Obtiene Estadística de defensa del Pokémon salvaje.
	 * 
	 * @return Estadística de defensa del Pokémon salvaje.
	 */
	public int getWildDefense() {
		return wildDefense;
	}

	/**
	 * Actualiza Estadística de defensa del Pokémon salvaje.
	 * 
	 * @param wildDefense nuevo valor de Estadística de defensa del Pokémon salvaje.
	 */
	public void setWildDefense(int wildDefense) {
		this.wildDefense = wildDefense;
	}

	/**
	 * Obtiene Estado actual del combate.
	 * 
	 * @return Estado actual del combate.
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Actualiza Estado actual del combate.
	 * 
	 * @param status nuevo valor de Estado actual del combate.
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * Obtiene Número del turno actual.
	 * 
	 * @return Número del turno actual.
	 */
	public int getTurnNumber() {
		return turnNumber;
	}

	/**
	 * Actualiza Número del turno actual.
	 * 
	 * @param turnNumber nuevo valor de Número del turno actual.
	 */
	public void setTurnNumber(int turnNumber) {
		this.turnNumber = turnNumber;
	}

	/**
	 * Obtiene Fecha y hora de inicio del combate.
	 * 
	 * @return Fecha y hora de inicio del combate.
	 */
	public Instant getStartedAt() {
		return startedAt;
	}

	/**
	 * Actualiza Fecha y hora de inicio del combate.
	 * 
	 * @param startedAt nuevo valor de Fecha y hora de inicio del combate.
	 */
	public void setStartedAt(Instant startedAt) {
		this.startedAt = startedAt;
	}

	/**
	 * Obtiene Fecha y hora de finalización del combate.
	 * 
	 * @return Fecha y hora de finalización del combate.
	 */
	public Instant getFinishedAt() {
		return finishedAt;
	}

	/**
	 * Actualiza Fecha y hora de finalización del combate.
	 * 
	 * @param finishedAt nuevo valor de Fecha y hora de finalización del combate.
	 */
	public void setFinishedAt(Instant finishedAt) {
		this.finishedAt = finishedAt;
	}

	/**
	 * Calcula el código hash de esta batalla.
	 * 
	 * @return respuesta generada
	 */
	@Override
	public int hashCode() {
		return Objects.hash(finishedAt, id, playerId, playerPokemonId, startedAt, status, Integer.valueOf(turnNumber),
				Integer.valueOf(wildAttack), Integer.valueOf(wildCurrentHp), Integer.valueOf(wildDefense),
				Integer.valueOf(wildLevel), Integer.valueOf(wildMaxHp), wildName, wildSpeciesId);
	}

	/**
	 * Compara esta batalla con otro objeto.
	 * 
	 * @param obj objeto con el que se compara esta batalla
	 * @return respuesta generada
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Battle other = (Battle) obj;
		return Objects.equals(finishedAt, other.finishedAt) && Objects.equals(id, other.id)
				&& Objects.equals(playerId, other.playerId) && Objects.equals(playerPokemonId, other.playerPokemonId)
				&& Objects.equals(startedAt, other.startedAt) && status == other.status
				&& turnNumber == other.turnNumber && wildAttack == other.wildAttack
				&& wildCurrentHp == other.wildCurrentHp && wildDefense == other.wildDefense
				&& wildLevel == other.wildLevel && wildMaxHp == other.wildMaxHp
				&& Objects.equals(wildName, other.wildName) && Objects.equals(wildSpeciesId, other.wildSpeciesId);
	}

}
