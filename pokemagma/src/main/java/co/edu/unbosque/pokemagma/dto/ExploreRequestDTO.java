package co.edu.unbosque.pokemagma.dto;

/**
 * Datos que identifican el mapa y el arbusto explorados por el jugador.
 */
public class ExploreRequestDTO {

	/**
	 * Identificador del mapa recorrido.
	 */
	private String mapId;

	/**
	 * Identificador del arbusto atravesado.
	 */
	private String bushId;

	/**
	 * Constructor vacio
	 */
	public ExploreRequestDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Crea una solicitud de exploración.
	 *
	 * @param mapId  identificador del mapa
	 * @param bushId identificador del arbusto
	 */
	public ExploreRequestDTO(String mapId, String bushId) {
		this.mapId = mapId;
		this.bushId = bushId;
	}

	/**
	 * Obtiene Identificador del mapa recorrido.
	 * 
	 * @return Identificador del mapa recorrido.
	 */
	public String getMapId() {
		return mapId;
	}

	/**
	 * Actualiza Identificador del mapa recorrido.
	 * 
	 * @param mapId nuevo valor de Identificador del mapa recorrido.
	 */
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	/**
	 * Obtiene Identificador del arbusto atravesado.
	 * 
	 * @return Identificador del arbusto atravesado.
	 */
	public String getBushId() {
		return bushId;
	}

	/**
	 * Actualiza Identificador del arbusto atravesado.
	 * 
	 * @param bushId nuevo valor de Identificador del arbusto atravesado.
	 */
	public void setBushId(String bushId) {
		this.bushId = bushId;
	}
}
