package co.edu.unbosque.pokemagma.dto;

/**
 * Datos para usar un objeto sobre un Pokémon del jugador.
 */
public class UseItemDTO {

    /**
     * Código del objeto que se desea usar.
     */
	private String itemCode;
    
	/**
     * Identificador del Pokémon objetivo.
     */
	private Long playerPokemonId;

	/**
	 * Constructor vacio
	 */
	public UseItemDTO() {
		// TODO Auto-generated constructor stub
	}

    /**
     * Obtiene Código del objeto que se desea usar.
     * @return Código del objeto que se desea usar.
     */
	public String getItemCode() {
		return itemCode;
	}

    /**
     * Actualiza Código del objeto que se desea usar.
     * @param itemCode nuevo valor de Código del objeto que se desea usar.
     */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

    /**
     * Obtiene Identificador del Pokémon objetivo.
     * @return Identificador del Pokémon objetivo.
     */
	public Long getPlayerPokemonId() {
		return playerPokemonId;
	}

    /**
     * Actualiza Identificador del Pokémon objetivo.
     * @param playerPokemonId nuevo valor de Identificador del Pokémon objetivo.
     */
	public void setPlayerPokemonId(Long playerPokemonId) {
		this.playerPokemonId = playerPokemonId;
	}
}


