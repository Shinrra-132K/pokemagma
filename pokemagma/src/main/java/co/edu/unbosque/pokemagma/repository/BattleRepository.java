package co.edu.unbosque.pokemagma.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.pokemagma.model.Battle;
import co.edu.unbosque.pokemagma.model.Battle.Status;

/** Consultas de combates guardados y pertenecientes a cada jugador. */
public interface BattleRepository extends JpaRepository<Battle, Long> {

    /** Busca un combate solo si pertenece al jugador indicado. */
    Optional<Battle> findByIdAndPlayerId(Long id, Long playerId);

    /** Busca el combate activo más reciente de un jugador. */
    Optional<Battle> findFirstByPlayerIdAndStatusOrderByStartedAtDesc(
            Long playerId, Status status);

    /** Comprueba si el jugador ya tiene un combate con el estado indicado. */
    boolean existsByPlayerIdAndStatus(Long playerId, Status status);
}

