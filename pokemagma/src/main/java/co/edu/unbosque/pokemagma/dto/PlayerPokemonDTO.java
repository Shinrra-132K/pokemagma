package co.edu.unbosque.pokemagma.dto;

/** DTO con el estado público de un Pokémon capturado por el jugador. */
public class PlayerPokemonDTO {

	/**
	 * Identificador del Pokémon capturado.
	 */
	private Long id;

	/**
	 * Identificador de especie según PokeAPI.
	 */
	private Integer speciesId;

	/**
	 * Apodo asignado al Pokémon.
	 */
	private String nickname;

	/**
	 * Nivel actual del Pokémon.
	 */
	private int level;

	/**
	 * Experiencia acumulada.
	 */
	private int experience;

	/**
	 * Puntos de salud actuales.
	 */
	private int currentHp;

	/**
	 * Puntos de salud máximos.
	 */
	private int maxHp;

	/**
	 * Estadística de ataque.
	 */
	private int attack;

	/**
	 * Estadística de defensa.
	 */
	private int defense;

	/**
	 * Estadística de velocidad.
	 */
	private int speed;

	/**
	 * Constructor vacio
	 */
	public PlayerPokemonDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Obtiene Identificador del Pokémon capturado.
	 * 
	 * @return Identificador del Pokémon capturado.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Actualiza Identificador del Pokémon capturado.
	 * 
	 * @param value nuevo valor de Identificador del Pokémon capturado.
	 */
	public void setId(Long value) {
		this.id = value;
	}

	/**
	 * Obtiene Identificador de especie según PokeAPI.
	 * 
	 * @return Identificador de especie según PokeAPI.
	 */
	public Integer getSpeciesId() {
		return speciesId;
	}

	/**
	 * Actualiza Identificador de especie según PokeAPI.
	 * 
	 * @param value nuevo valor de Identificador de especie según PokeAPI.
	 */
	public void setSpeciesId(Integer value) {
		this.speciesId = value;
	}

	/**
	 * Obtiene Apodo asignado al Pokémon.
	 * 
	 * @return Apodo asignado al Pokémon.
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Actualiza Apodo asignado al Pokémon.
	 * 
	 * @param value nuevo valor de Apodo asignado al Pokémon.
	 */
	public void setNickname(String value) {
		this.nickname = value;
	}

	/**
	 * Obtiene Nivel actual del Pokémon.
	 * 
	 * @return Nivel actual del Pokémon.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Actualiza Nivel actual del Pokémon.
	 * 
	 * @param value nuevo valor de Nivel actual del Pokémon.
	 */
	public void setLevel(int value) {
		this.level = value;
	}

	/**
	 * Obtiene Experiencia acumulada.
	 * 
	 * @return Experiencia acumulada.
	 */
	public int getExperience() {
		return experience;
	}

	/**
	 * Actualiza Experiencia acumulada.
	 * 
	 * @param value nuevo valor de Experiencia acumulada.
	 */
	public void setExperience(int value) {
		this.experience = value;
	}

	/**
	 * Obtiene Puntos de salud actuales.
	 * 
	 * @return Puntos de salud actuales.
	 */
	public int getCurrentHp() {
		return currentHp;
	}

	/**
	 * Actualiza Puntos de salud actuales.
	 * 
	 * @param value nuevo valor de Puntos de salud actuales.
	 */
	public void setCurrentHp(int value) {
		this.currentHp = value;
	}

	/**
	 * Obtiene Puntos de salud máximos.
	 * 
	 * @return Puntos de salud máximos.
	 */
	public int getMaxHp() {
		return maxHp;
	}

	/**
	 * Actualiza Puntos de salud máximos.
	 * 
	 * @param value nuevo valor de Puntos de salud máximos.
	 */
	public void setMaxHp(int value) {
		this.maxHp = value;
	}

	/**
	 * Obtiene Estadística de ataque.
	 * 
	 * @return Estadística de ataque.
	 */
	public int getAttack() {
		return attack;
	}

	/**
	 * Actualiza Estadística de ataque.
	 * 
	 * @param value nuevo valor de Estadística de ataque.
	 */
	public void setAttack(int value) {
		this.attack = value;
	}

	/**
	 * Obtiene Estadística de defensa.
	 * 
	 * @return Estadística de defensa.
	 */
	public int getDefense() {
		return defense;
	}

	/**
	 * Actualiza Estadística de defensa.
	 * 
	 * @param value nuevo valor de Estadística de defensa.
	 */
	public void setDefense(int value) {
		this.defense = value;
	}

	/**
	 * Obtiene Estadística de velocidad.
	 * 
	 * @return Estadística de velocidad.
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Actualiza Estadística de velocidad.
	 * 
	 * @param value nuevo valor de Estadística de velocidad.
	 */
	public void setSpeed(int value) {
		this.speed = value;
	}
}
