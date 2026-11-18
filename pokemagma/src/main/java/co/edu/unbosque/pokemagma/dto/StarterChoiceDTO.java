package co.edu.unbosque.pokemagma.dto;

/** DTO con la especie inicial que eligió el jugador. */
public class StarterChoiceDTO {

    /**
     * Identificador de la especie elegida.
     */
	private Integer speciesId;

	/**
	 * Constructor vacio 
	 */
	public StarterChoiceDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
     * Crea una solicitud con la especie inicial elegida.
     *
     * @param speciesId identificador de especie
     */
	public StarterChoiceDTO(Integer speciesId) {
		this.speciesId = speciesId;
	}

    /**
     * Obtiene Identificador de la especie elegida.
     * @return Identificador de la especie elegida.
     */
	public Integer getSpeciesId() {
		return speciesId;
	}

    /**
     * Actualiza Identificador de la especie elegida.
     * @param speciesId nuevo valor de Identificador de la especie elegida.
     */
	public void setSpeciesId(Integer speciesId) {
		this.speciesId = speciesId;
	}
}


