package co.edu.unbosque.pokemagma.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.pokemagma.model.PokedexEntry;

/**
 * Repositorio para las entradas de la pokedex de los jugadores
 */
public interface PokedexEntryRepository extends JpaRepository<PokedexEntry, Long> {
	
	/**
	 * Busca todas las entradas de pokedex de un jugador
	 * 
	 * @param id identificador del jugador
	 * @return lista de entradas
	 */
	public List<PokedexEntry> findAllByPlayerId(Long id);
	
	/**
	 * Busca la entrada de una especie en la pokedex de un jugador
	 * 
	 * @param playerId identificador del jugador
	 * @param speciesId identificador de la especie pokemon
	 * @return entrada encontrada o vacio si no existe
	 */
	public Optional<PokedexEntry> findByPlayerIdAndSpeciesId(Long playerId, Integer speciesId);
	
	/**
	 * Comprueba si una especie ya tiene una entrada para este jugador
	 * 
	 * @param playerId identificador del jugador
	 * @param speciesId identificador de la especie pokemon
	 * @return
	 */
	public boolean existByPlayerIdAndSpeciesId(Long playerId, Integer speciesId);
	
	/**
	 * Cuenta cuantas especies capturo el jugador
	 * 
	 * @param playerId identificador del jugador
	 * @return cantidad de entradas capturadas
	 */
	public Long countByPlayerIdAndCaughtTrue(Long playerId);
	

}
