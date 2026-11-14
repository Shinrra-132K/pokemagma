package co.edu.unbosque.pokemagma.dto;

import java.util.Objects;

/**
 * DTO para transferir el profreso de una especie en la pokedex
 */
public class PokedexEntryDTO {
	/**
	 * Identificador de la entrada persistida
	 */
	private Long id;
	/**
	 * Identificador del jugador propietario
	 */
	private Long playerId;
	/**
	 * Identificador de la especie pokemon
	 */
	private Integer speciesId;
	/**
	 * Indica si la especie fue vista
	 */
	private boolean seen;
	/**
	 * Indica si la especie fue capturada
	 */
	private boolean caught;

	/**
	 * Constructor vacio
	 */
	public PokedexEntryDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor con todos los campos
	 * 
	 * @param id        identificador de la entrada
	 * @param playerId  identificador del jugador
	 * @param speciesId identificador de la especie
	 * @param seen      estado de avistamiento
	 * @param caught    estado de captura
	 */
	public PokedexEntryDTO(Long id, Long playerId, Integer speciesId, boolean seen, boolean caught) {
		super();
		this.id = id;
		this.playerId = playerId;
		this.speciesId = speciesId;
		this.seen = seen;
		this.caught = caught;
	}

	/** @return identificador de la entrada */
	public Long getId() {
		return id;
	}

	/** @param id identificador de la entrada */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return ID del jugador */
	public Long getPlayerId() {
		return playerId;
	}

	/** @param playerId ID del jugador */
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	/** @return ID de especie */
	public Integer getSpeciesId() {
		return speciesId;
	}

	/** @param speciesId ID de especie */
	public void setSpeciesId(Integer speciesId) {
		this.speciesId = speciesId;
	}

	/** @return true si fue vista */
	public boolean isSeen() {
		return seen;
	}

	/** @param seen estado de avistamiento */
	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	/** @return true si fue capturada */
	public boolean isCaught() {
		return caught;
	}

	/** @param caught estado de captura */
	public void setCaught(boolean caught) {
		this.caught = caught;
	}
	

	/**
	 * @return codigo hash del DTO
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Boolean.valueOf(caught), id, playerId, Boolean.valueOf(seen), speciesId);
	}

	/**
	 * Compara este DTO con otro por sus campos
	 * 
	 * @param obj objeto que compara
	 * @return true si los campos coinciden
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PokedexEntryDTO other = (PokedexEntryDTO) obj;
		return caught == other.caught && Objects.equals(id, other.id) && Objects.equals(playerId, other.playerId)
				&& seen == other.seen && Objects.equals(speciesId, other.speciesId);
	}

	/**
	 * @return representacion en texto del DTO
	 */
	@Override
	public String toString() {
		return "PokedexEntryDTO [id=" + id + ", playerId=" + playerId + ", speciesId=" + speciesId + ", seen=" + seen
				+ ", caught=" + caught + "]";
	}

}
