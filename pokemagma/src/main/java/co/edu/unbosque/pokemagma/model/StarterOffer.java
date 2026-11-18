package co.edu.unbosque.pokemagma.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Entidad que conserva una oferta inicial de una especie por generación. */
@Entity
@Table(name = "starter_offer", uniqueConstraints = @UniqueConstraint(name = "starter_offer_player_unique", columnNames = "player_id"))
public class StarterOffer {

	/**
	 * 
	 * Identificador generado de la oferta.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 
	 * Identificador del jugador propietario.
	 */
	@Column(name = "player_id", nullable = false, unique = true)
	private Long playerId;

	/**
	 * 
	 * Especie ofrecida de la generación I.
	 */
	@Column(name = "generation_one_species_id", nullable = false)
	private Integer generationOneSpeciesId;

	/**
	 * 
	 * Especie ofrecida de la generación II.
	 */
	@Column(name = "generation_two_species_id", nullable = false)
	private Integer generationTwoSpeciesId;

	/**
	 * 
	 * Especie ofrecida de la generación III.
	 */
	@Column(name = "generation_three_species_id", nullable = false)
	private Integer generationThreeSpeciesId;

	/**
	 * 
	 * Especie inicial elegida por el jugador.
	 */
	@Column(name = "selected_species_id")
	private Integer selectedSpeciesId;

	/**
	 * 
	 * Fecha y hora en que se generó la oferta.
	 */
	@Column(name = "offered_at", nullable = false, updatable = false)
	private Instant offeredAt;

	public StarterOffer() {
		// Constructor requerido por JPA.
	}

	public StarterOffer(Long playerId, Integer generationOneSpeciesId, Integer generationTwoSpeciesId,
			Integer generationThreeSpeciesId) {
		this.playerId = playerId;
		this.generationOneSpeciesId = generationOneSpeciesId;
		this.generationTwoSpeciesId = generationTwoSpeciesId;
		this.generationThreeSpeciesId = generationThreeSpeciesId;
		this.offeredAt = Instant.now();
	}

	/**
	 * Comprueba si la especie pertenece a la oferta inicial.
	 * 
	 * @param speciesId identificador de especie
	 * @return respuesta generada
	 */
	public boolean contains(Integer speciesId) {
		return speciesId != null && (speciesId.equals(generationOneSpeciesId)
				|| speciesId.equals(generationTwoSpeciesId) || speciesId.equals(generationThreeSpeciesId));
	}

	/**
	 * Obtiene Identificador generado de la oferta.
	 * 
	 * @return Identificador generado de la oferta.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Actualiza Identificador generado de la oferta.
	 * 
	 * @param id nuevo valor de Identificador generado de la oferta.
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
	 * Obtiene Especie ofrecida de la generación I.
	 * 
	 * @return Especie ofrecida de la generación I.
	 */
	public Integer getGenerationOneSpeciesId() {
		return generationOneSpeciesId;
	}

	/**
	 * Actualiza Especie ofrecida de la generación I.
	 * 
	 * @param id nuevo valor de Especie ofrecida de la generación I.
	 */
	public void setGenerationOneSpeciesId(Integer id) {
		this.generationOneSpeciesId = id;
	}

	/**
	 * Obtiene Especie ofrecida de la generación II.
	 * 
	 * @return Especie ofrecida de la generación II.
	 */
	public Integer getGenerationTwoSpeciesId() {
		return generationTwoSpeciesId;
	}

	/**
	 * Actualiza Especie ofrecida de la generación II.
	 * 
	 * @param id nuevo valor de Especie ofrecida de la generación II.
	 */
	public void setGenerationTwoSpeciesId(Integer id) {
		this.generationTwoSpeciesId = id;
	}

	/**
	 * Obtiene Especie ofrecida de la generación III.
	 * 
	 * @return Especie ofrecida de la generación III.
	 */
	public Integer getGenerationThreeSpeciesId() {
		return generationThreeSpeciesId;
	}

	/**
	 * Actualiza Especie ofrecida de la generación III.
	 * 
	 * @param id nuevo valor de Especie ofrecida de la generación III.
	 */
	public void setGenerationThreeSpeciesId(Integer id) {
		this.generationThreeSpeciesId = id;
	}

	/**
	 * Obtiene Especie inicial elegida por el jugador.
	 * 
	 * @return Especie inicial elegida por el jugador.
	 */
	public Integer getSelectedSpeciesId() {
		return selectedSpeciesId;
	}

	/**
	 * Actualiza Especie inicial elegida por el jugador.
	 * 
	 * @param selectedSpeciesId nuevo valor de Especie inicial elegida por el
	 *                          jugador.
	 */
	public void setSelectedSpeciesId(Integer selectedSpeciesId) {
		this.selectedSpeciesId = selectedSpeciesId;
	}

	/**
	 * Obtiene Fecha y hora en que se generó la oferta.
	 * 
	 * @return Fecha y hora en que se generó la oferta.
	 */
	public Instant getOfferedAt() {
		return offeredAt;
	}

	/**
	 * Actualiza Fecha y hora en que se generó la oferta.
	 * 
	 * @param offeredAt nuevo valor de Fecha y hora en que se generó la oferta.
	 */
	public void setOfferedAt(Instant offeredAt) {
		this.offeredAt = offeredAt;
	}
}
