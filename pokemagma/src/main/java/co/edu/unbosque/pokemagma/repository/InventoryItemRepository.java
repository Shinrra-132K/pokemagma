package co.edu.unbosque.pokemagma.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.pokemagma.model.InventoryItem;

/**
 * Repositorio para los objetos del inventario de los jugadores
 */
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

	/**
	 * Obtiene todos los objetos pertenecientes a un jugador.
	 * 
	 * @param playerId identificador del jugador
	 * @return lista de objetos del inventario
	 */
	public List<InventoryItem> findAllByPlayerId(Long playerId);

	/**
	 * Busca un objeto especifico del inventario del jugador
	 * 
	 * @param playerId identificador del jugador
	 * @param itemCode codigo del objeto
	 * @return objeto encontrado o vacio si no existe
	 */
	public Optional<InventoryItem> findByPlayerIdAndItemCode(Long playerId, String itemCode);

	/**
	 * Comprueba si el jugador ya tiene una fila para este objeto
	 * 
	 * @param playerId identificador del jugador
	 * @param itemCode codigo del objeto
	 * @return true si la fila existe
	 */
	public boolean existsByPlayerIdAndItemCode(Long playerId, String itemCode);

}
