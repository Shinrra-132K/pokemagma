package co.edu.unbosque.pokemagma.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Representa el progreso de un jugador para una especie de pokemon en la
 * pokedex
 */
@Entity
@Table(name = "pokedex_entry", uniqueConstraints = @UniqueConstraint(name = "pokedex_player_species", columnNames = {
		"player_id", "species_id" }))
public class PokedexEntry {

	/**
	 * Identificador unico de la entrada
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Id del jugador dueño de la entrada
	 */
	@Column(name = "player_id", nullable = false)
	private Long playerId;

	/**
	 * Id de especie de pokemon, segun el catalogo usado por el juego
	 */
	@Column(name = "species_id", nullable = false)
	private Integer speciesId;

	/**
	 * Indica si el jugador ha visto esta especie
	 */
	@Column(nullable = false)
	private boolean seen;

	/**
	 * Indica si el jugador ma capturado a esta especie
	 */
	@Column(nullable = false)
	private boolean caught;

	/**
	 * Constructor vacio
	 */
	public PokedexEntry() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Crea uan entrada de pokedex marcada inicialmente como vista
	 * 
	 * @param playerId ID del jugador
	 * @param speciesId Id de especie de pokemon
	 */
	public PokedexEntry(Long playerId, Integer speciesId) {
		super();
		this.playerId = playerId;
		this.speciesId = speciesId;
		this.seen = true;
		this.caught = false;
	}
	
	/**
	 * 
	 * getters y setters de las variables
	 */

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

	/**
	 * Marca la especie como vista
	 */
	public void markSeen() {
		seen = true;
	}

	/**
	 * Marca la especie como vista y capturada
	 */
	public void markCaught() {
		seen = true;
		caught = true;
	}
	

	/**
	 * Devuelve un hash durante la asigancion del ID
	 * 
	 * @return codigo hash de la clase
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Boolean.valueOf(caught), id, playerId, Boolean.valueOf(seen), speciesId);
	}

	/**
	 * Compara entradas por su ID
	 * 
	 * @param obj objeto que se compara
	 * @return true si ambas entradas tiene el mismo ID
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PokedexEntry other = (PokedexEntry) obj;
		return caught == other.caught && Objects.equals(id, other.id) && Objects.equals(playerId, other.playerId)
				&& seen == other.seen && Objects.equals(speciesId, other.speciesId);
	}

	/**
	 * Devuelve una representacion textual de las variables
	 * 
	 * @return texto con los datos de la entrada
	 */
	@Override
	public String toString() {
		return "PokedexEntry [playerId=" + playerId + ", speciesId=" + speciesId + ", seen=" + seen + ", caught="
				+ caught + "]";
	}

}
