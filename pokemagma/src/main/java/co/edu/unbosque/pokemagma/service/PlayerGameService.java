package co.edu.unbosque.pokemagma.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.pokemagma.dto.BattleDTO;
import co.edu.unbosque.pokemagma.dto.EncounterDTO;
import co.edu.unbosque.pokemagma.dto.ExploreRequestDTO;
import co.edu.unbosque.pokemagma.dto.PlayerPokemonDTO;
import co.edu.unbosque.pokemagma.dto.StarterOptionsDTO;
import co.edu.unbosque.pokemagma.exception.ExternalServiceException;
import co.edu.unbosque.pokemagma.exception.GameRuleException;
import co.edu.unbosque.pokemagma.exception.QuantityNotValidException;
import co.edu.unbosque.pokemagma.exception.ResourceNotFoundException;
import co.edu.unbosque.pokemagma.model.Battle;
import co.edu.unbosque.pokemagma.model.GameEvent;
import co.edu.unbosque.pokemagma.model.InventoryItem;
import co.edu.unbosque.pokemagma.model.PokedexEntry;
import co.edu.unbosque.pokemagma.model.PlayerPokemon;
import co.edu.unbosque.pokemagma.model.StarterOffer;
import co.edu.unbosque.pokemagma.repository.BattleRepository;
import co.edu.unbosque.pokemagma.repository.GameEventRepository;
import co.edu.unbosque.pokemagma.repository.InventoryItemRepository;
import co.edu.unbosque.pokemagma.repository.PokedexEntryRepository;
import co.edu.unbosque.pokemagma.repository.PlayerPokemonRepository;
import co.edu.unbosque.pokemagma.repository.StarterOfferRepository;
import co.edu.unbosque.pokemagma.dto.GameEventDTO;

/**
 * Coordina la selección inicial, encuentros, combates y captura del jugador.
 */
@Service
public class PlayerGameService {

	private static final int STARTER_LEVEL = 5;
	private static final int MIN_WILD_LEVEL = 2;
	private static final int MAX_WILD_LEVEL = 5;
	private static final int FIRST_GENERATION_MIN = 1;
	private static final int FIRST_GENERATION_MAX = 151;
	private static final int SECOND_GENERATION_MIN = 152;
	private static final int SECOND_GENERATION_MAX = 251;
	private static final int THIRD_GENERATION_MIN = 252;
	private static final int THIRD_GENERATION_MAX = 386;

	/**
	 * Repositorio para consultar y guardar los Pokémon del jugador.
	 */
	private final PlayerPokemonRepository pokemonRepository;
	/**
	 * Repositorio para consultar y guardar los combates.
	 */
	private final BattleRepository battleRepository;
	/**
	 * Repositorio que conserva la oferta inicial asignada al jugador.
	 */
	private final StarterOfferRepository starterOfferRepository;
	/**
	 * Repositorio para actualizar los registros de la Pokédex.
	 */
	private final PokedexEntryRepository pokedexRepository;
	/**
	 * Repositorio para consultar y modificar el inventario.
	 */
	private final InventoryItemRepository inventoryRepository;
	/**
	 * Repositorio para registrar los eventos de auditoría del juego.
	 */
	private final GameEventRepository eventRepository;
	/**
	 * Servicio que consulta los datos de Pokémon.
	 */
	private final PokemonService pokemonService;
	/**
	 * Servicio que administra los objetos del jugador.
	 */
	private final ItemService itemService;

	/**
	 * Probabilidad, entre cero y uno, de encontrar un Pokémon al explorar.
	 */
	@Value("${pokemagma.game.encounter-chance:0.20}")
	private double encounterChance;

	/**
	 * Construye el servicio con los repositorios y servicios requeridos.
	 *
	 * @param pokemonRepository      repositorio de los Pokémon del jugador
	 * @param battleRepository       repositorio de los combates
	 * @param starterOfferRepository repositorio de las ofertas iniciales
	 * @param pokedexRepository      repositorio de la Pokédex
	 * @param inventoryRepository    repositorio del inventario
	 * @param eventRepository        repositorio de eventos
	 * @param pokemonService         servicio de consulta de especies Pokémon
	 * @param itemService            servicio de gestión de objetos
	 */
	public PlayerGameService(PlayerPokemonRepository pokemonRepository, BattleRepository battleRepository,
			StarterOfferRepository starterOfferRepository, PokedexEntryRepository pokedexRepository,
			InventoryItemRepository inventoryRepository, GameEventRepository eventRepository,
			PokemonService pokemonService, ItemService itemService) {
		this.pokemonRepository = pokemonRepository;
		this.battleRepository = battleRepository;
		this.starterOfferRepository = starterOfferRepository;
		this.pokedexRepository = pokedexRepository;
		this.inventoryRepository = inventoryRepository;
		this.eventRepository = eventRepository;
		this.pokemonService = pokemonService;
		this.itemService = itemService;
	}

	/**
	 * Devuelve la oferta inicial, creando una sola opción aleatoria por generación.
	 */
	@Transactional(rollbackFor = Exception.class)
	public StarterOptionsDTO getStarterOptions(Long playerId) throws GameRuleException {
		if (pokemonRepository.countByPlayerId(playerId) > 0) {
			throw new GameRuleException();
		}

		StarterOffer offer = starterOfferRepository.findByPlayerId(playerId)
				.orElseGet(() -> starterOfferRepository
						.save(new StarterOffer(playerId, randomId(FIRST_GENERATION_MIN, FIRST_GENERATION_MAX),
								randomId(SECOND_GENERATION_MIN, SECOND_GENERATION_MAX),
								randomId(THIRD_GENERATION_MIN, THIRD_GENERATION_MAX))));

		return new StarterOptionsDTO(offer.getGenerationOneSpeciesId(), offer.getGenerationTwoSpeciesId(),
				offer.getGenerationThreeSpeciesId(), offer.getSelectedSpeciesId() != null);
	}

	/**
	 * Guarda la elección del Pokémon inicial y entrega los objetos de inicio.
	 * 
	 * @param playerId  identificador del jugador
	 * @param speciesId identificador de especie
	 * @return respuesta generada
	 */
	@Transactional(rollbackFor = Exception.class)
	public PlayerPokemonDTO selectStarter(Long playerId, Integer speciesId)
			throws ResourceNotFoundException, GameRuleException, ExternalServiceException, QuantityNotValidException {
		if (pokemonRepository.countByPlayerId(playerId) > 0) {
			throw new GameRuleException();
		}

		StarterOffer offer = starterOfferRepository.findByPlayerId(playerId)
				.orElseThrow(() -> new ResourceNotFoundException());
		if (offer.getSelectedSpeciesId() != null) {
			throw new GameRuleException();
		}
		if (!offer.contains(speciesId)) {
			throw new GameRuleException();
		}

		PokemonService.PokemonData data = pokemonService.getPokemonData(speciesId);
		PlayerPokemon starter = pokemonRepository.save(createPokemon(playerId, data, STARTER_LEVEL, null));

		offer.setSelectedSpeciesId(speciesId);
		starterOfferRepository.save(offer);
		itemService.grantItem(playerId, ItemService.POKEBALL, 5);
		itemService.grantItem(playerId, ItemService.POTION, 3);
		markCaught(playerId, speciesId);
		logEvent(playerId, GameEvent.EventType.STARTER_SELECTED, "Eligió " + data.name() + " como Pokémon inicial");
		logEvent(playerId, GameEvent.EventType.ITEM_ACQUIRED, "Recibió 5 Poké Balls y 3 pociones iniciales");

		return toPokemonDTO(starter);
	}

	/**
	 * Comprueba si un arbusto produce un encuentro y crea el combate si
	 * corresponde.
	 */
	@Transactional(rollbackFor = Exception.class)
	public EncounterDTO explore(Long playerId, ExploreRequestDTO request)
			throws ResourceNotFoundException, GameRuleException, ExternalServiceException {
		if (request == null || blank(request.getMapId()) || blank(request.getBushId())) {
			throw new GameRuleException();
		}
		if (pokemonRepository.countByPlayerId(playerId) == 0) {
			throw new GameRuleException();
		}
		if (battleRepository.existsByPlayerIdAndStatus(playerId, Battle.Status.ACTIVE)) {
			throw new GameRuleException();
		}
		if (encounterChance < 0 || encounterChance > 1) {
			throw new GameRuleException();
		}
		if (ThreadLocalRandom.current().nextDouble() >= encounterChance) {
			return new EncounterDTO(false, null, null, null, 0);
		}

		List<PlayerPokemon> team = pokemonRepository.findAllByPlayerIdOrderByIdAsc(playerId);
		PlayerPokemon activePokemon = null;

		for (PlayerPokemon pokemon : team) {
			if (pokemon.getCurrentHp() > 0) {
				activePokemon = pokemon;
				break;
			}
		}

		if (activePokemon == null) {
			throw new GameRuleException();

		}

		int speciesId = randomId(1, THIRD_GENERATION_MAX);
		int level = ThreadLocalRandom.current().nextInt(MIN_WILD_LEVEL, MAX_WILD_LEVEL + 1);
		PokemonService.PokemonData wild = pokemonService.getPokemonData(speciesId);
		PlayerPokemon wildStats = createPokemon(playerId, wild, level, null);

		Battle battle = new Battle(playerId, activePokemon.getId(), speciesId, wild.name(), level, wildStats.getMaxHp(),
				wildStats.getAttack(), wildStats.getDefense());
		battle = battleRepository.save(battle);

		markSeen(playerId, speciesId);
		logEvent(playerId, GameEvent.EventType.WILD_ENCOUNTER,
				"Encuentro salvaje en " + request.getMapId() + ", arbusto " + request.getBushId() + ": " + wild.name());

		return new EncounterDTO(true, battle.getId(), speciesId, wild.name(), level);
	}

	/**
	 * Obtiene el estado de un combate perteneciente al jugador.
	 * 
	 * @param playerId identificador del jugador
	 * @param battleId identificador del combate
	 * @return battle
	 * @throws ResourceNotFoundException si la operación no puede completarse
	 */
	@Transactional(readOnly = true)
	public BattleDTO getBattle(Long playerId, Long battleId) throws ResourceNotFoundException {
		Battle battle = findBattle(playerId, battleId);
		PlayerPokemon playerPokemon = findBattlePokemon(playerId, battle);
		return toBattleDTO(battle, playerPokemon);
	}

	/**
	 * Ejecuta un ataque básico y la respuesta del Pokémon salvaje.
	 * 
	 * @param playerId identificador del jugador
	 * @param battleId identificador del combate
	 * @return respuesta generada
	 */
	@Transactional(rollbackFor = Exception.class)
	public BattleDTO attack(Long playerId, Long battleId)
			throws ResourceNotFoundException, GameRuleException, QuantityNotValidException {
		Battle battle = activeBattle(playerId, battleId);
		PlayerPokemon pokemon = findBattlePokemon(playerId, battle);
		int damage = calculateDamage(pokemon.getAttack(), battle.getWildDefense());
		battle.setWildCurrentHp(Math.max(0, battle.getWildCurrentHp() - damage));

		if (battle.getWildCurrentHp() == 0) {
			battle.finish(Battle.Status.WON);
			awardExperience(pokemon, battle.getWildLevel() * 10);
			String rewardCode = ThreadLocalRandom.current().nextBoolean() ? ItemService.POTION : ItemService.POKEBALL;
			itemService.grantItem(playerId, rewardCode, 1);
			logEvent(playerId, GameEvent.EventType.BATTLE, "Victoria contra " + battle.getWildName());
			logEvent(playerId, GameEvent.EventType.ITEM_ACQUIRED, "Recibió 1 " + rewardCode + " como recompensa");
		} else {
			wildCounterAttack(battle, pokemon);
		}

		battle.setTurnNumber(battle.getTurnNumber() + 1);
		battleRepository.save(battle);
		pokemonRepository.save(pokemon);
		return toBattleDTO(battle, pokemon);
	}

	/**
	 * Consume una Poké Ball e intenta capturar al Pokémon salvaje.
	 * 
	 * @param playerId identificador del jugador
	 * @param battleId identificador del combate
	 * @return respuesta generada
	 */
	@Transactional(rollbackFor = Exception.class)
	public BattleDTO capture(Long playerId, Long battleId)
			throws ResourceNotFoundException, GameRuleException, QuantityNotValidException, ExternalServiceException {
		Battle battle = activeBattle(playerId, battleId);
		PlayerPokemon activePokemon = findBattlePokemon(playerId, battle);
		itemService.consumePokeball(playerId);

		double missingHealth = 1.0 - (double) battle.getWildCurrentHp() / battle.getWildMaxHp();
		double captureChance = Math.min(0.90, 0.15 + 0.75 * missingHealth);
		boolean caught = ThreadLocalRandom.current().nextDouble() < captureChance;

		if (caught) {
			PokemonService.PokemonData data = pokemonService.getPokemonData(battle.getWildSpeciesId());
			PlayerPokemon captured = createPokemon(playerId, data, battle.getWildLevel(), null);
			pokemonRepository.save(captured);
			markCaught(playerId, battle.getWildSpeciesId());
			battle.finish(Battle.Status.CAPTURED);
			logEvent(playerId, GameEvent.EventType.POKEMON_CAUGHT,
					"Capturó a " + battle.getWildName() + " nivel " + battle.getWildLevel());
		} else {
			wildCounterAttack(battle, activePokemon);
		}

		battle.setTurnNumber(battle.getTurnNumber() + 1);
		battleRepository.save(battle);
		pokemonRepository.save(activePokemon);
		return toBattleDTO(battle, activePokemon);
	}

	/**
	 * Finaliza el combate activo por huida.
	 * 
	 * @param playerId identificador del jugador
	 * @param battleId identificador del combate
	 * @return respuesta generada
	 * @throws ResourceNotFoundException si la operación no puede completarse
	 * @throws GameRuleException         si la operación no puede completarse
	 */
	@Transactional(rollbackFor = Exception.class)
	public BattleDTO run(Long playerId, Long battleId) throws ResourceNotFoundException, GameRuleException {
		Battle battle = activeBattle(playerId, battleId);
		PlayerPokemon pokemon = findBattlePokemon(playerId, battle);
		battle.finish(Battle.Status.FLED);
		battleRepository.save(battle);
		logEvent(playerId, GameEvent.EventType.BATTLE, "Huyó del combate contra " + battle.getWildName());
		return toBattleDTO(battle, pokemon);
	}

	/**
	 * Valida y aplica un objeto sobre un Pokémon del jugador.
	 * 
	 * @param playerId  identificador del jugador
	 * @param itemCode  código del objeto
	 * @param pokemonId identificador del Pokémon
	 * @return respuesta generada
	 */
	@Transactional(rollbackFor = Exception.class)
	public PlayerPokemonDTO useItem(Long playerId, String itemCode, Long pokemonId)
			throws ResourceNotFoundException, QuantityNotValidException, GameRuleException {
		if (!ItemService.POTION.equals(itemCode)) {
			throw new GameRuleException();
		}
		PlayerPokemon pokemon = itemService.usePotion(playerId, pokemonId);
		logEvent(playerId, GameEvent.EventType.ITEM_USED, "Usó una poción en el Pokémon " + pokemon.getId());
		return toPokemonDTO(pokemon);
	}

	/**
	 * Obtiene los Pokémon capturados por el jugador.
	 * 
	 * @param playerId identificador del jugador
	 * @return team
	 */
	@Transactional(readOnly = true)
	public List<PlayerPokemonDTO> getTeam(Long playerId) {
		return pokemonRepository.findAllByPlayerIdOrderByIdAsc(playerId).stream().map(this::toPokemonDTO).toList();
	}

	/**
	 * Busca el combate activo del jugador.
	 * 
	 * @param playerId identificador del jugador
	 * @param battleId identificador del combate
	 * @throws ResourceNotFoundException si la operación no puede completarse
	 * @throws GameRuleException         si la operación no puede completarse
	 */
	private Battle activeBattle(Long playerId, Long battleId) throws ResourceNotFoundException, GameRuleException {
		Battle battle = findBattle(playerId, battleId);
		if (battle.getStatus() != Battle.Status.ACTIVE) {
			throw new GameRuleException();
		}
		return battle;
	}

	/**
	 * Busca el combate activo que corresponde al jugador.
	 * 
	 * @param playerId identificador del jugador
	 * @param battleId identificador del combate
	 * @throws ResourceNotFoundException si la operación no puede completarse
	 */
	private Battle findBattle(Long playerId, Long battleId) throws ResourceNotFoundException {
		return battleRepository.findByIdAndPlayerId(battleId, playerId)
				.orElseThrow(() -> new ResourceNotFoundException());
	}

	/**
	 * Obtiene el Pokémon salvaje asociado al combate.
	 * 
	 * @param playerId identificador del jugador
	 * @param battle   combate que se convertirá a DTO
	 * @throws ResourceNotFoundException si la operación no puede completarse
	 */
	private PlayerPokemon findBattlePokemon(Long playerId, Battle battle) throws ResourceNotFoundException {
		return pokemonRepository.findByIdAndPlayerId(battle.getPlayerPokemonId(), playerId)
				.orElseThrow(() -> new ResourceNotFoundException());
	}

	/**
	 * Aplica el ataque de respuesta del Pokémon salvaje.
	 * 
	 * @param battle  combate que se convertirá a DTO
	 * @param pokemon Pokémon que se convertirá o validará
	 */
	private void wildCounterAttack(Battle battle, PlayerPokemon pokemon) {
		int damage = calculateDamage(battle.getWildAttack(), pokemon.getDefense());
		pokemon.setCurrentHp(Math.max(0, pokemon.getCurrentHp() - damage));
		if (pokemon.getCurrentHp() == 0) {
			battle.finish(Battle.Status.LOST);
			logEvent(battle.getPlayerId(), GameEvent.EventType.BATTLE, "Derrota contra " + battle.getWildName());
		}
	}

	/**
	 * Calcula el daño del ataque usando ataque y defensa.
	 * 
	 * @param attack  estadística de ataque del Pokémon que golpea
	 * @param defense estadística de defensa del Pokémon que recibe el golpe
	 */
	private int calculateDamage(int attack, int defense) {
		return Math.max(1, (attack * 10) / (Math.max(1, defense) + 10));
	}

	/**
	 * Otorga experiencia al Pokémon que participó en el combate.
	 * 
	 * @param pokemon Pokémon que se convertirá o validará
	 * @param amount  cantidad solicitada
	 */
	private void awardExperience(PlayerPokemon pokemon, int amount) {
		pokemon.setExperience(pokemon.getExperience() + amount);
		while (pokemon.getLevel() < 100 && pokemon.getExperience() >= pokemon.getLevel() * 100) {
			pokemon.setExperience(pokemon.getExperience() - pokemon.getLevel() * 100);
			pokemon.setLevel(pokemon.getLevel() + 1);
			pokemon.setMaxHp(pokemon.getMaxHp() + 2);
			pokemon.setCurrentHp(pokemon.getCurrentHp() + 2);
			pokemon.setAttack(pokemon.getAttack() + 1);
			pokemon.setDefense(pokemon.getDefense() + 1);
			pokemon.setSpeed(pokemon.getSpeed() + 1);
		}
	}

	/**
	 * Crea un Pokémon del jugador con las estadísticas iniciales calculadas.
	 * 
	 * @param playerId identificador del jugador
	 * @param data     datos de especie obtenidos del servicio Pokémon
	 * @param level    nivel inicial del Pokémon
	 * @param nickname apodo elegido para el Pokémon
	 */
	private PlayerPokemon createPokemon(Long playerId, PokemonService.PokemonData data, int level, String nickname) {
		int maxHp = Math.max(10, data.baseHp() / 2 + level * 3 + 10);
		int attack = Math.max(1, data.baseAttack() / 2 + level);
		int defense = Math.max(1, data.baseDefense() / 2 + level);
		int speed = Math.max(1, data.baseSpeed() / 2 + level);
		return new PlayerPokemon(playerId, data.id(), nickname, level, 0, maxHp, attack, defense, speed);
	}

	/**
	 * Registra la especie como vista en la Pokédex.
	 * 
	 * @param playerId  identificador del jugador
	 * @param speciesId identificador de especie
	 */
	private void markSeen(Long playerId, Integer speciesId) {
		PokedexEntry entry = pokedexRepository.findByPlayerIdAndSpeciesId(playerId, speciesId)
				.orElseGet(() -> new PokedexEntry(playerId, speciesId));
		entry.markSeen();
		pokedexRepository.save(entry);
	}

	/**
	 * Registra la especie como capturada en la Pokédex.
	 * 
	 * @param playerId  identificador del jugador
	 * @param speciesId identificador de especie
	 */
	private void markCaught(Long playerId, Integer speciesId) {
		PokedexEntry entry = pokedexRepository.findByPlayerIdAndSpeciesId(playerId, speciesId)
				.orElseGet(() -> new PokedexEntry(playerId, speciesId));
		entry.markCaught();
		pokedexRepository.save(entry);
	}

	/**
	 * Guarda un evento de auditoría asociado al jugador.
	 * 
	 * @param playerId    identificador del jugador
	 * @param type        tipo de evento que se registrará
	 * @param description detalle del evento para la auditoría
	 */
	private void logEvent(Long playerId, GameEvent.EventType type, String description) {
		GameEvent event = new GameEvent(playerId, type, description);
		eventRepository.save(event);
	}

	/**
	 * Convierte la entidad del Pokémon a su DTO de respuesta.
	 * 
	 * @param pokemon Pokémon que se convertirá o validará
	 */
	private PlayerPokemonDTO toPokemonDTO(PlayerPokemon pokemon) {
		PlayerPokemonDTO dto = new PlayerPokemonDTO();
		dto.setId(pokemon.getId());
		dto.setSpeciesId(pokemon.getSpeciesId());
		dto.setNickname(pokemon.getNickname());
		dto.setLevel(pokemon.getLevel());
		dto.setExperience(pokemon.getExperience());
		dto.setCurrentHp(pokemon.getCurrentHp());
		dto.setMaxHp(pokemon.getMaxHp());
		dto.setAttack(pokemon.getAttack());
		dto.setDefense(pokemon.getDefense());
		dto.setSpeed(pokemon.getSpeed());
		return dto;
	}

	/**
	 * Convierte el combate y los datos del Pokémon a su DTO de respuesta.
	 * 
	 * @param battle  combate que se convertirá a DTO
	 * @param pokemon Pokémon que se convertirá o validará
	 */
	private BattleDTO toBattleDTO(Battle battle, PlayerPokemon pokemon) {
		BattleDTO dto = new BattleDTO();
		dto.setId(battle.getId());
		dto.setPlayerPokemonId(pokemon.getId());
		dto.setWildSpeciesId(battle.getWildSpeciesId());
		dto.setWildName(battle.getWildName());
		dto.setWildLevel(battle.getWildLevel());
		dto.setWildCurrentHp(battle.getWildCurrentHp());
		dto.setWildMaxHp(battle.getWildMaxHp());
		dto.setPlayerCurrentHp(pokemon.getCurrentHp());
		dto.setPlayerMaxHp(pokemon.getMaxHp());
		dto.setStatus(battle.getStatus());
		dto.setTurnNumber(battle.getTurnNumber());
		dto.setStartedAt(battle.getStartedAt());
		dto.setFinishedAt(battle.getFinishedAt());
		return dto;
	}

	/**
	 * Genera un identificador aleatorio dentro del intervalo indicado.
	 * 
	 * @param min          límite inferior del intervalo
	 * @param maxInclusive max inclusive
	 */
	private int randomId(int min, int maxInclusive) {
		return ThreadLocalRandom.current().nextInt(min, maxInclusive + 1);
	}

	/**
	 * Comprueba si el texto es nulo o está vacío.
	 * 
	 * @param value texto que se va a comprobar
	 */
	private boolean blank(String value) {
		return value == null || value.isBlank();
	}
}
