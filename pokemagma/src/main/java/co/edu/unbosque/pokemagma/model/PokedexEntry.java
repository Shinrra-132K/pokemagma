package co.edu.unbosque.pokemagma.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "pokedex_entry", uniqueConstraints = @UniqueConstraint(name = "pokedex_player_species", columnNames = {"player_id","species_id"}))
public class PokedexEntry {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "player_id", nullable = false)
	private Long playerId;
	
	@Column(name = "species_id", nullable = false)
	private Integer speciesId;
	
	@Column(nullable = false)
	private boolean seen;
	
	@Column(nullable = false)
	private boolean caught;
	
	public PokedexEntry() {
		// TODO Auto-generated constructor stub
	}

	public PokedexEntry(Long playerId, Integer speciesId) {
		super();
		this.playerId = playerId;
		this.speciesId = speciesId;
		this.seen = true;
		this.caught = false;
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
	
	public void markSeen() {
		seen = true;
	}
	
	public void markCaught() {
		seen = true;
		caught = true;
	}

	@Override
	public String toString() {
		return "PokedexEntry [playerId=" + playerId + ", speciesId=" + speciesId + ", seen=" + seen + ", caught="
				+ caught + "]";
	}
	
	
	
	

}
