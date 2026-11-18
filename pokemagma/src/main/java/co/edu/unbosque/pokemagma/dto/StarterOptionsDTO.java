package co.edu.unbosque.pokemagma.dto;

/**
 * Tres opciones iniciales: una especie de cada una de las primeras
 * generaciones.
 */
public class StarterOptionsDTO {
    
	/**
     * Opción aleatoria de la generación I.
     */
	private Integer generationOneSpeciesId;
    
	/**
     * Opción aleatoria de la generación II.
     */
	private Integer generationTwoSpeciesId;
    
	/**
     * Opción aleatoria de la generación III.
     */
	private Integer generationThreeSpeciesId;
    
	/**
     * Indica si el jugador ya eligió su inicial.
     */
	private boolean selected;

	/**
	 * Constructor vacio
	 */
	public StarterOptionsDTO() {
		// TODO Auto-generated constructor stub
	}

	 /**
     * Crea la respuesta con las tres opciones y el estado de selección.
     *
     * @param genOne especie de generación I
     * @param genTwo especie de generación II
     * @param genThree especie de generación III
     * @param selected indica si el jugador ya seleccionó una especie
     */
	public StarterOptionsDTO(Integer genOne, Integer genTwo, Integer genThree, boolean selected) {
		this.generationOneSpeciesId = genOne;
		this.generationTwoSpeciesId = genTwo;
		this.generationThreeSpeciesId = genThree;
		this.selected = selected;
	}

    /**
     * Obtiene Opción aleatoria de la generación I.
     * @return Opción aleatoria de la generación I.
     */
	public Integer getGenerationOneSpeciesId() {
		return generationOneSpeciesId;
	}

    /**
     * Actualiza Opción aleatoria de la generación I.
     * @param value nuevo valor de Opción aleatoria de la generación I.
     */
	public void setGenerationOneSpeciesId(Integer value) {
		this.generationOneSpeciesId = value;
	}

    /**
     * Obtiene Opción aleatoria de la generación II.
     * @return Opción aleatoria de la generación II.
     */
	public Integer getGenerationTwoSpeciesId() {
		return generationTwoSpeciesId;
	}

    /**
     * Actualiza Opción aleatoria de la generación II.
     * @param value nuevo valor de Opción aleatoria de la generación II.
     */
	public void setGenerationTwoSpeciesId(Integer value) {
		this.generationTwoSpeciesId = value;
	}

    /**
     * Obtiene Opción aleatoria de la generación III.
     * @return Opción aleatoria de la generación III.
     */
	public Integer getGenerationThreeSpeciesId() {
		return generationThreeSpeciesId;
	}

    /**
     * Actualiza Opción aleatoria de la generación III.
     * @param value nuevo valor de Opción aleatoria de la generación III.
     */
	public void setGenerationThreeSpeciesId(Integer value) {
		this.generationThreeSpeciesId = value;
	}

    /**
     * Obtiene Indica si el jugador ya eligió su inicial.
     * @return Indica si el jugador ya eligió su inicial.
     */
	public boolean isSelected() {
		return selected;
	}

    /**
     * Actualiza Indica si el jugador ya eligió su inicial.
     * @param selected nuevo valor de Indica si el jugador ya eligió su inicial.
     */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}

