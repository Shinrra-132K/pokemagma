package co.edu.unbosque.pokemagma.dto;

/**
 * Resultado de explorar un arbusto.
 *
 * Cuando hay encuentro, contiene los datos del Pokémon salvaje y el
 * identificador del combate.
 */
public class EncounterDTO {

	/**
	 * Indica si se produjo un encuentro.
	 */
	private boolean encounter;
	
	/** 
	 * Identificador del combate creado, si hubo encuentro.
	 */
	private Long battleId;
	
	/** 
	 * Identificador de la especie encontrada.
	 */
	private Integer speciesId;
	
	/** 
	 * Nombre del Pokémon encontrado.
	 */
	private String pokemonName;
	
	/** 
	 * Nivel del Pokémon encontrado.
	 */
	private int level;

	/**
	 * Constructor vacio
	 */
	public EncounterDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
     * Crea el resultado de una exploración.
     *
     * @param encounter indica si hubo encuentro
     * @param battleId identificador del combate, si se creó
     * @param speciesId identificador de especie encontrada
     * @param pokemonName nombre del Pokémon encontrado
     * @param level nivel del Pokémon encontrado
     */
	public EncounterDTO(boolean encounter, Long battleId, Integer speciesId, String pokemonName, int level) {
		this.encounter = encounter;
		this.battleId = battleId;
		this.speciesId = speciesId;
		this.pokemonName = pokemonName;
		this.level = level;
	}

	/**
	 * Obtiene Indica si se produjo un encuentro.
	 * 
	 * @return Indica si se produjo un encuentro.
	 */
	public boolean isEncounter() {
		return encounter;
	}

	/**
	 * Actualiza Indica si se produjo un encuentro.
	 * 
	 * @param encounter nuevo valor de Indica si se produjo un encuentro.
	 */
	public void setEncounter(boolean encounter) {
		this.encounter = encounter;
	}

	/**
	 * Obtiene Identificador del combate creado, si hubo encuentro.
	 * 
	 * @return Identificador del combate creado, si hubo encuentro.
	 */
	public Long getBattleId() {
		return battleId;
	}

	/**
	 * Actualiza Identificador del combate creado, si hubo encuentro.
	 * 
	 * @param battleId nuevo valor de Identificador del combate creado, si hubo
	 *                 encuentro.
	 */
	public void setBattleId(Long battleId) {
		this.battleId = battleId;
	}

	/**
	 * Obtiene Identificador de la especie encontrada.
	 * 
	 * @return Identificador de la especie encontrada.
	 */
	public Integer getSpeciesId() {
		return speciesId;
	}

	/**
	 * Actualiza Identificador de la especie encontrada.
	 * 
	 * @param speciesId nuevo valor de Identificador de la especie encontrada.
	 */
	public void setSpeciesId(Integer speciesId) {
		this.speciesId = speciesId;
	}

	/**
	 * Obtiene Nombre del Pokémon encontrado.
	 * 
	 * @return Nombre del Pokémon encontrado.
	 */
	public String getPokemonName() {
		return pokemonName;
	}

	/**
	 * Actualiza Nombre del Pokémon encontrado.
	 * 
	 * @param pokemonName nuevo valor de Nombre del Pokémon encontrado.
	 */
	public void setPokemonName(String pokemonName) {
		this.pokemonName = pokemonName;
	}

	/**
	 * Obtiene Nivel del Pokémon encontrado.
	 * 
	 * @return Nivel del Pokémon encontrado.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Actualiza Nivel del Pokémon encontrado.
	 * 
	 * @param level nuevo valor de Nivel del Pokémon encontrado.
	 */
	public void setLevel(int level) {
		this.level = level;
	}
}
