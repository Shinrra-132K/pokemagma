package co.edu.unbosque.pokemagma.dto;

import java.util.Objects;

public class PokedexEntryDTO {
	
	private Long id;
	private Long playerId;
	private Integer speciesId;
	private boolean seen;
	private boolean caught;
	
	public PokedexEntryDTO() {
		// TODO Auto-generated constructor stub
	}

	public PokedexEntryDTO(Long id, Long playerId, Integer speciesId, boolean seen, boolean caught) {
		super();
		this.id = id;
		this.playerId = playerId;
		this.speciesId = speciesId;
		this.seen = seen;
		this.caught = caught;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public Integer getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(Integer speciesId) {
		this.speciesId = speciesId;
	}

	public boolean isSeen() {
		return seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	public boolean isCaught() {
		return caught;
	}

	public void setCaught(boolean caught) {
		this.caught = caught;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Boolean.valueOf(caught), id, playerId, Boolean.valueOf(seen), speciesId);
	}

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

	@Override
	public String toString() {
		return "PokedexEntryDTO [id=" + id + ", playerId=" + playerId + ", speciesId=" + speciesId + ", seen=" + seen
				+ ", caught=" + caught + "]";
	}
	
	

}
