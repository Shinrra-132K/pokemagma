package co.edu.unbosque.pokemagma.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.pokemagma.exception.GameRuleException;
import co.edu.unbosque.pokemagma.exception.QuantityNotValidException;
import co.edu.unbosque.pokemagma.exception.ResourceNotFoundException;
import co.edu.unbosque.pokemagma.model.InventoryItem;
import co.edu.unbosque.pokemagma.model.PlayerPokemon;
import co.edu.unbosque.pokemagma.repository.InventoryItemRepository;
import co.edu.unbosque.pokemagma.repository.PlayerPokemonRepository;

/** Servicio que valida, entrega y consume objetos del inventario. */
@Service
public class ItemService {

	/** Código usado para identificar una Poké Ball en el inventario. */
	public static final String POKEBALL = "POKEBALL";

	/** Código usado para identificar una poción en el inventario. */
	public static final String POTION = "POTION";

	private static final int POTION_HEAL = 20;

	/**
	 * Repositorio para consultar y modificar el inventario.
	 */
	private final InventoryItemRepository inventoryRepository;
	/**
	 * Repositorio para consultar y guardar los Pokémon del jugador.
	 */
	private final PlayerPokemonRepository pokemonRepository;

	/**
	 * Construye el servicio con los repositorios necesarios.
	 *
	 * @param inventoryRepository repositorio del inventario
	 * @param pokemonRepository   repositorio de los Pokémon del jugador
	 */
	public ItemService(InventoryItemRepository inventoryRepository, PlayerPokemonRepository pokemonRepository) {
		this.inventoryRepository = inventoryRepository;
		this.pokemonRepository = pokemonRepository;
	}

	/**
	 * Añade unidades de un objeto al inventario del jugador.
	 * 
	 * @param playerId identificador del jugador
	 * @param itemCode código del objeto
	 * @param amount   cantidad solicitada
	 * @throws QuantityNotValidException si la operación no puede completarse
	 */
	@Transactional(rollbackFor = Exception.class)
	public void grantItem(Long playerId, String itemCode, int amount) throws QuantityNotValidException {
		if (amount <= 0) {
			throw new QuantityNotValidException();
		}

		InventoryItem item = inventoryRepository.findByPlayerIdAndItemCode(playerId, itemCode)
				.orElseGet(() -> new InventoryItem(playerId, itemCode, 0));
		item.add(amount);
		inventoryRepository.save(item);
	}

	/**
	 * Consume una Poké Ball disponible del jugador.
	 * 
	 * @param playerId identificador del jugador
	 * @throws QuantityNotValidException si la operación no puede completarse
	 */
	@Transactional(rollbackFor = Exception.class)
	public void consumePokeball(Long playerId) throws QuantityNotValidException {
		consume(playerId, POKEBALL);
	}

	/**
	 * Consume una poción y restaura salud a un Pokémon propio.
	 * 
	 * @param playerId  identificador del jugador
	 * @param pokemonId identificador del Pokémon
	 * @return respuesta generada
	 */
	@Transactional(rollbackFor = Exception.class)
	public PlayerPokemon usePotion(Long playerId, Long pokemonId)
			throws ResourceNotFoundException, QuantityNotValidException, GameRuleException {
		PlayerPokemon pokemon = pokemonRepository.findByIdAndPlayerId(pokemonId, playerId)
				.orElseThrow(() -> new ResourceNotFoundException());

		if (pokemon.getCurrentHp() >= pokemon.getMaxHp()) {
			throw new GameRuleException();
		}

		consume(playerId, POTION);
		pokemon.setCurrentHp(Math.min(pokemon.getMaxHp(), pokemon.getCurrentHp() + POTION_HEAL));
		return pokemonRepository.save(pokemon);
	}

	/**
	 * Descuenta del inventario la cantidad de objetos utilizada.
	 * 
	 * @param playerId identificador del jugador
	 * @param itemCode código del objeto
	 * @throws QuantityNotValidException si la operación no puede completarse
	 */
	private void consume(Long playerId, String itemCode) throws QuantityNotValidException {
		InventoryItem item = inventoryRepository.findByPlayerIdAndItemCode(playerId, itemCode)
				.orElseThrow(() -> new QuantityNotValidException());
		item.remove(1);
		inventoryRepository.save(item);
	}
}
