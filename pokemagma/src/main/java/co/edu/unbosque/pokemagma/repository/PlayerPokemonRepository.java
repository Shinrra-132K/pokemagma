package co.edu.unbosque.pokemagma.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.pokemagma.model.PlayerPokemon;

/** Consultas de Pokémon individuales pertenecientes a los jugadores. */
public interface PlayerPokemonRepository extends JpaRepository<PlayerPokemon, Long> {

	 /**
     * Obtiene los Pokémon de un jugador, ordenados por identificador ascendente.
     *
     * @param playerId identificador del jugador propietario
     * @return lista de Pokémon del jugador ordenada por ID
     */
    List<PlayerPokemon> findAllByPlayerIdOrderByIdAsc(Long playerId);

    /**
     * Busca un Pokémon por su identificador y verifica que pertenezca
     * al jugador indicado.
     *
     * @param id       identificador del Pokémon
     * @param playerId identificador del jugador propietario
     * @return un Optional con el Pokémon si existe y pertenece al jugador;
     *         vacío si no se encuentra
     */
    Optional<PlayerPokemon> findByIdAndPlayerId(Long id, Long playerId);

    /**
     * Cuenta los Pokémon que pertenecen a un jugador.
     *
     * @param playerId identificador del jugador
     * @return cantidad de Pokémon del jugador
     */
    long countByPlayerId(Long playerId);
}

