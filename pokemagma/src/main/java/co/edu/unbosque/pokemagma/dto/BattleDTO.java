package co.edu.unbosque.pokemagma.dto;

import java.time.Instant;

import co.edu.unbosque.pokemagma.model.Battle.Status;

/** DTO con el estado público de un combate Pokémon. */
public class BattleDTO {

	/**
	 * Identificador del combate.
	 */
	private Long id;

	/**
	 * Identificador del Pokémon activo del jugador.
	 */
	private Long playerPokemonId;

	/**
	 * Identificador de especie salvaje.
	 */
	private Integer wildSpeciesId;

	/**
	 * Nombre del Pokémon salvaje.
	 */
	private String wildName;

	/**
	 * Nivel del Pokémon salvaje.
	 */
	private int wildLevel;

	/**
	 * Salud actual del Pokémon salvaje.
	 */
	private int wildCurrentHp;

	/**
	 * Salud máxima del Pokémon salvaje.
	 */
	private int wildMaxHp;

	/**
	 * Salud actual del Pokémon del jugador.
	 */
	private int playerCurrentHp;

	/**
	 * Salud máxima del Pokémon del jugador.
	 */
	private int playerMaxHp;

	/**
	 * Estado actual del combate.
	 */
	private Status status;

	/**
	 * Número del turno actual.
	 */
	private int turnNumber;

	/**
	 * Fecha y hora de inicio del combate.
	 */
	private Instant startedAt;

	/**
	 * Fecha y hora de finalización del combate.
	 */
	private Instant finishedAt;

	/**
	 * Constructor vacio
	 */
	public BattleDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor que inicializa atributos
	 * 
	 * @param playerPokemonId identificador del pokemon del jugador
	 * @param wildSpeciesId   identificador de especies salvajes
	 * @param wildName        nombre del pokemon salvaje
	 * @param wildLevel       nivel del pokemon salvaje
	 * @param wildCurrentHp   salud actual del pokemon salvaje
	 * @param wildMaxHp       salud maxima del pokemon salvaje
	 * @param playerCurrentHp salud del pokemon del jugador
	 * @param playerMaxHp     salud maxima del pokemon del jugador
	 * @param status          estado actual del combate
	 * @param turnNumber      numero del turno actual
	 * @param startedAt       fecha y hora de inicio del combate
	 * @param finishedAt      fecha y hora de finalizacion del combate
	 */
	public BattleDTO(Long playerPokemonId, Integer wildSpeciesId, String wildName, int wildLevel, int wildCurrentHp,
			int wildMaxHp, int playerCurrentHp, int playerMaxHp, Status status, int turnNumber, Instant startedAt,
			Instant finishedAt) {
		super();
		this.playerPokemonId = playerPokemonId;
		this.wildSpeciesId = wildSpeciesId;
		this.wildName = wildName;
		this.wildLevel = wildLevel;
		this.wildCurrentHp = wildCurrentHp;
		this.wildMaxHp = wildMaxHp;
		this.playerCurrentHp = playerCurrentHp;
		this.playerMaxHp = playerMaxHp;
		this.status = status;
		this.turnNumber = turnNumber;
		this.startedAt = startedAt;
		this.finishedAt = finishedAt;
	}

	/**
	 * Obtiene Identificador del combate.
	 * 
	 * @return Identificador del combate.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Actualiza Identificador del combate.
	 * 
	 * @param id nuevo valor de Identificador del combate.
	 */
	public void setId(Long id) {
		this.id = id;
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
	 * @param value nuevo valor de Identificador del Pokémon activo del jugador.
	 */
	public void setPlayerPokemonId(Long value) {
		this.playerPokemonId = value;
	}

	/**
	 * Obtiene Identificador de especie salvaje.
	 * 
	 * @return Identificador de especie salvaje.
	 */
	public Integer getWildSpeciesId() {
		return wildSpeciesId;
	}

	/**
	 * Actualiza Identificador de especie salvaje.
	 * 
	 * @param value nuevo valor de Identificador de especie salvaje.
	 */
	public void setWildSpeciesId(Integer value) {
		this.wildSpeciesId = value;
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
	 * @param value nuevo valor de Nombre del Pokémon salvaje.
	 */
	public void setWildName(String value) {
		this.wildName = value;
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
	 * @param value nuevo valor de Nivel del Pokémon salvaje.
	 */
	public void setWildLevel(int value) {
		this.wildLevel = value;
	}

	/**
	 * Obtiene Salud actual del Pokémon salvaje.
	 * 
	 * @return Salud actual del Pokémon salvaje.
	 */
	public int getWildCurrentHp() {
		return wildCurrentHp;
	}

	/**
	 * Actualiza Salud actual del Pokémon salvaje.
	 * 
	 * @param value nuevo valor de Salud actual del Pokémon salvaje.
	 */
	public void setWildCurrentHp(int value) {
		this.wildCurrentHp = value;
	}

	/**
	 * Obtiene Salud máxima del Pokémon salvaje.
	 * 
	 * @return Salud máxima del Pokémon salvaje.
	 */
	public int getWildMaxHp() {
		return wildMaxHp;
	}

	/**
	 * Actualiza Salud máxima del Pokémon salvaje.
	 * 
	 * @param value nuevo valor de Salud máxima del Pokémon salvaje.
	 */
	public void setWildMaxHp(int value) {
		this.wildMaxHp = value;
	}

	/**
	 * Obtiene Salud actual del Pokémon del jugador.
	 * 
	 * @return Salud actual del Pokémon del jugador.
	 */
	public int getPlayerCurrentHp() {
		return playerCurrentHp;
	}

	/**
	 * Actualiza Salud actual del Pokémon del jugador.
	 * 
	 * @param value nuevo valor de Salud actual del Pokémon del jugador.
	 */
	public void setPlayerCurrentHp(int value) {
		this.playerCurrentHp = value;
	}

	/**
	 * Obtiene Salud máxima del Pokémon del jugador.
	 * 
	 * @return Salud máxima del Pokémon del jugador.
	 */
	public int getPlayerMaxHp() {
		return playerMaxHp;
	}

	/**
	 * Actualiza Salud máxima del Pokémon del jugador.
	 * 
	 * @param value nuevo valor de Salud máxima del Pokémon del jugador.
	 */
	public void setPlayerMaxHp(int value) {
		this.playerMaxHp = value;
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
	 * @param value nuevo valor de Número del turno actual.
	 */
	public void setTurnNumber(int value) {
		this.turnNumber = value;
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
	 * @param value nuevo valor de Fecha y hora de inicio del combate.
	 */
	public void setStartedAt(Instant value) {
		this.startedAt = value;
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
	 * @param value nuevo valor de Fecha y hora de finalización del combate.
	 */
	public void setFinishedAt(Instant value) {
		this.finishedAt = value;
	}
}
