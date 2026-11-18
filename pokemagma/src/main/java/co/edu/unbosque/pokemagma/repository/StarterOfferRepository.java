package co.edu.unbosque.pokemagma.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.pokemagma.model.StarterOffer;

/** Persistencia de las ofertas iniciales de cada jugador. */
public interface StarterOfferRepository extends JpaRepository<StarterOffer, Long> {

	/**
	 * Busca la oferta inicial de un jugador.
	 *
	 * @param playerId identificador del jugador propietario
	 * @return un Optional con la oferta si existe; vacío si no se encuentra
	 */
	Optional<StarterOffer> findByPlayerId(Long playerId);
}
