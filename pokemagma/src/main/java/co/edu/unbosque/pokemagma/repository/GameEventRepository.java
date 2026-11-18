package co.edu.unbosque.pokemagma.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.pokemagma.model.GameEvent;

/**
 * Repositorio para consultar y persistir eventos de auditoria del juego.
 */
public interface GameEventRepository extends JpaRepository<GameEvent, Long> {

	/**
	 * Obtiene los eventos de un jugador del mas reciente al mas antiguo
	 * 
	 * @param playerId identificador del jugador
	 * @return eventos ordenados por fecha descendente
	 */
	public List<GameEvent> findAllByPlayerIdOrderByCreatedAtDesc(Long playerId);

	/**
	 * Obtiene los eventos desde el mas reciente hasta el mas antiguo
	 * @return eventos ordenados por fechas descendentes
	 */
	public List<GameEvent> findAllByOrderByCreatedAtDesc();

}
