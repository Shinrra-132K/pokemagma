package co.edu.unbosque.pokemagma.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Entidad que representa un Pokémon capturado por un jugador. */
@Entity
@Table(name = "player_pokemon")
public class PlayerPokemon {

	/**
	 * Identificador generado del Pokémon.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Identificador del jugador propietario.
	 */
	@Column(name = "player_id", nullable = false)
	private Long playerId;

	/**
	 * Identificador de especie según PokeAPI.
	 */
	@Column(name = "species_id", nullable = false)
	private Integer speciesId;

	/**
	 * Apodo asignado al Pokémon.
	 */
	private String nickname;

	/**
	 * Nivel actual del Pokémon.
	 */
	@Column(nullable = false)
	private int level;

	/**
	 * Experiencia acumulada por el Pokémon.
	 */
	@Column(nullable = false)
	private int experience;

	/**
	 * Puntos de salud actuales.
	 */
	@Column(name = "current_hp", nullable = false)
	private int currentHp;

	/**
	 * Puntos de salud máximos.
	 */
	@Column(name = "max_hp", nullable = false)
	private int maxHp;

	/**
	 * Estadística de ataque.
	 */
	@Column(nullable = false)
	private int attack;

	/**
	 * Estadística de defensa.
	 */
	@Column(nullable = false)
	private int defense;

	/**
	 * Estadística de velocidad.
	 */
	@Column(nullable = false)
	private int speed;

	/**
	 * Constructor vacio
	 */
	public PlayerPokemon() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Crea un Pokémon para un jugador. Inicializa sus puntos de salud actuales con
	 * el máximo indicado.
	 *
	 * @param playerId   identificador del jugador propietario
	 * @param speciesId  identificador de especie según PokeAPI
	 * @param nickname   apodo del Pokémon; puede ser null
	 * @param level      nivel inicial
	 * @param experience experiencia inicial acumulada
	 * @param maxHp      puntos de salud máximos e iniciales
	 * @param attack     estadística de ataque
	 * @param defense    estadística de defensa
	 * @param speed      estadística de velocidad
	 */
	public PlayerPokemon(Long playerId, Integer speciesId, String nickname, int level, int experience, int maxHp,
			int attack, int defense, int speed) {
		this.playerId = playerId;
		this.speciesId = speciesId;
		this.nickname = nickname;
		this.level = level;
		this.experience = experience;
		this.currentHp = maxHp; // Añade esta asignación.
		this.maxHp = maxHp;
		this.attack = attack;
		this.defense = defense;
		this.speed = speed;
	}

	/**
	 * Obtiene Identificador generado del Pokémon.
	 * 
	 * @return Identificador generado del Pokémon.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Actualiza Identificador generado del Pokémon.
	 * 
	 * @param id nuevo valor de Identificador generado del Pokémon.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene Identificador del jugador propietario.
	 * 
	 * @return Identificador del jugador propietario.
	 */
	public Long getPlayerId() {
		return playerId;
	}

	/**
	 * Actualiza Identificador del jugador propietario.
	 * 
	 * @param playerId nuevo valor de Identificador del jugador propietario.
	 */
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
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
	 * @param speciesId nuevo valor de Identificador de especie según PokeAPI.
	 */
	public void setSpeciesId(Integer speciesId) {
		this.speciesId = speciesId;
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
	 * @param nickname nuevo valor de Apodo asignado al Pokémon.
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
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
	 * @param level nuevo valor de Nivel actual del Pokémon.
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Obtiene Experiencia acumulada por el Pokémon.
	 * 
	 * @return Experiencia acumulada por el Pokémon.
	 */
	public int getExperience() {
		return experience;
	}

	/**
	 * Actualiza Experiencia acumulada por el Pokémon.
	 * 
	 * @param experience nuevo valor de Experiencia acumulada por el Pokémon.
	 */
	public void setExperience(int experience) {
		this.experience = experience;
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
	 * @param currentHp nuevo valor de Puntos de salud actuales.
	 */
	public void setCurrentHp(int currentHp) {
		this.currentHp = currentHp;
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
	 * @param maxHp nuevo valor de Puntos de salud máximos.
	 */
	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
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
	 * @param attack nuevo valor de Estadística de ataque.
	 */
	public void setAttack(int attack) {
		this.attack = attack;
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
	 * @param defense nuevo valor de Estadística de defensa.
	 */
	public void setDefense(int defense) {
		this.defense = defense;
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
	 * @param speed nuevo valor de Estadística de velocidad.
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * Calcula el código hash de este Pokémon.
	 * 
	 * @return respuesta generada
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/**
	 * Compara este Pokémon con otro objeto.
	 * 
	 * @param obj objeto con el que se compara este Pokémon
	 * @return respuesta generada
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		PlayerPokemon other = (PlayerPokemon) obj;
		return Objects.equals(id, other.id);
	}

}
