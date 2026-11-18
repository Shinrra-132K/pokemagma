package co.edu.unbosque.pokemagma.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.pokemagma.dto.BattleDTO;
import co.edu.unbosque.pokemagma.dto.EncounterDTO;
import co.edu.unbosque.pokemagma.dto.ExploreRequestDTO;
import co.edu.unbosque.pokemagma.dto.PlayerPokemonDTO;
import co.edu.unbosque.pokemagma.dto.StarterChoiceDTO;
import co.edu.unbosque.pokemagma.dto.StarterOptionsDTO;
import co.edu.unbosque.pokemagma.dto.UseItemDTO;
import co.edu.unbosque.pokemagma.exception.ExternalServiceException;
import co.edu.unbosque.pokemagma.exception.GameRuleException;
import co.edu.unbosque.pokemagma.exception.QuantityNotValidException;
import co.edu.unbosque.pokemagma.exception.ResourceNotFoundException;
import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.service.PlayerGameService;

/**
 * Controlador para las acciones principales de juego del jugador.
 *
 * El jugador se obtiene del contexto de seguridad mediante el JWT; el cliente
 * no puede indicar el identificador de otra cuenta.
 */
@RestController
@RequestMapping("/player/game")
public class PlayerGameController {

	/**
	 * Servicio que ejecuta las acciones de juego solicitadas por el jugador.
	 */
	private final PlayerGameService gameService;

	/**
	 * Crea el controlador de juego.
	 *
	 * @param gameService servicio que ejecuta las reglas de juego
	 */
	public PlayerGameController(PlayerGameService gameService) {
		this.gameService = gameService;
	}

	/**
	 * Obtiene la oferta inicial asignada al jugador.
	 *
	 * @param currentUser usuario autenticado
	 * @return opciones iniciales disponibles
	 * @throws GameRuleException si el jugador ya tiene Pokémon
	 */
	@GetMapping("/starters/getoptions")
	public ResponseEntity<StarterOptionsDTO> getStarterOptions(@AuthenticationPrincipal User currentUser)
			throws GameRuleException {
		return ResponseEntity.ok(gameService.getStarterOptions(currentUser.getId()));
	}

	/**
	 * Guarda la elección del Pokémon inicial y entrega los objetos de inicio.
	 *
	 * @param currentUser usuario autenticado
	 * @param request     especie elegida
	 * @return Pokémon inicial creado
	 * @throws ResourceNotFoundException si la oferta no existe
	 * @throws GameRuleException         si la especie no pertenece a la oferta o ya
	 *                                   fue elegida
	 * @throws ExternalServiceException  si falla la consulta de datos Pokémon
	 * @throws QuantityNotValidException si no se pueden entregar los objetos
	 *                                   iniciales
	 */
	@PostMapping("/starters/select")
	public ResponseEntity<PlayerPokemonDTO> selectStarter(@AuthenticationPrincipal User currentUser,
			@RequestBody StarterChoiceDTO request)
			throws ResourceNotFoundException, GameRuleException, ExternalServiceException, QuantityNotValidException {
		PlayerPokemonDTO pokemon = gameService.selectStarter(currentUser.getId(), request.getSpeciesId());
		return ResponseEntity.status(HttpStatus.CREATED).body(pokemon);
	}

	/**
	 * Procesa el paso por un arbusto y puede iniciar un encuentro salvaje.
	 *
	 * @param currentUser usuario autenticado
	 * @param request     identificadores del mapa y del arbusto
	 * @return resultado de la exploración y, si aplica, datos del encuentro
	 * @throws ResourceNotFoundException si falta un recurso requerido
	 * @throws GameRuleException         si el jugador no puede explorar en su
	 *                                   estado actual
	 * @throws ExternalServiceException  si falla la consulta de datos Pokémon
	 */
	@PostMapping("/explore")
	public ResponseEntity<EncounterDTO> explore(@AuthenticationPrincipal User currentUser,
			@RequestBody ExploreRequestDTO request)
			throws ResourceNotFoundException, GameRuleException, ExternalServiceException {
		return ResponseEntity.ok(gameService.explore(currentUser.getId(), request));
	}

	/**
	 * Obtiene el equipo Pokémon del jugador autenticado.
	 *
	 * @param currentUser usuario autenticado
	 * @return Pokémon del jugador o 204 si el equipo está vacío
	 */
	@GetMapping("/team/getall")
	public ResponseEntity<List<PlayerPokemonDTO>> getTeam(@AuthenticationPrincipal User currentUser) {
		List<PlayerPokemonDTO> team = gameService.getTeam(currentUser.getId());
		if (team.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(team);
	}

	/**
	 * Consulta el estado de un combate perteneciente al jugador.
	 *
	 * @param currentUser usuario autenticado
	 * @param battleId    identificador del combate
	 * @return estado actual del combate
	 * @throws ResourceNotFoundException si el combate no existe o pertenece a otro
	 *                                   jugador
	 */
	@GetMapping("/battles/getbyid/{battleId}")
	public ResponseEntity<BattleDTO> getBattle(@AuthenticationPrincipal User currentUser, @PathVariable Long battleId)
			throws ResourceNotFoundException {
		return ResponseEntity.ok(gameService.getBattle(currentUser.getId(), battleId));
	}

	/**
	 * Ejecuta un ataque básico y la respuesta del Pokémon salvaje.
	 *
	 * @param currentUser usuario autenticado
	 * @param battleId    identificador del combate
	 * @return estado actualizado del combate
	 * @throws ResourceNotFoundException si el combate no existe o no pertenece al
	 *                                   jugador
	 * @throws GameRuleException         si el combate no está activo
	 * @throws QuantityNotValidException si ocurre un error con una cantidad de
	 *                                   objetos
	 */
	@PostMapping("/battles/{battleId}/attack")
	public ResponseEntity<BattleDTO> attack(@AuthenticationPrincipal User currentUser, @PathVariable Long battleId)
			throws ResourceNotFoundException, GameRuleException, QuantityNotValidException {
		return ResponseEntity.ok(gameService.attack(currentUser.getId(), battleId));
	}

	/**
     * Consume una Poké Ball e intenta capturar al Pokémon salvaje.
     *
     * @param currentUser usuario autenticado
     * @param battleId identificador del combate
     * @return estado actualizado del combate
     * @throws ResourceNotFoundException si el combate no existe o no pertenece al jugador
     * @throws GameRuleException si el combate no está activo
     * @throws QuantityNotValidException si el jugador no tiene Poké Balls
     * @throws ExternalServiceException si falla la consulta de datos Pokémon
     */
	@PostMapping("/battles/{battleId}/capture")
	public ResponseEntity<BattleDTO> capture(@AuthenticationPrincipal User currentUser, @PathVariable Long battleId)
			throws ResourceNotFoundException, GameRuleException, QuantityNotValidException, ExternalServiceException {
		return ResponseEntity.ok(gameService.capture(currentUser.getId(), battleId));
	}

	/**
     * Finaliza el combate activo por huida.
     *
     * @param currentUser usuario autenticado
     * @param battleId identificador del combate
     * @return estado actualizado del combate
     * @throws ResourceNotFoundException si el combate no existe o no pertenece al jugador
     * @throws GameRuleException si el combate no está activo
     */
	@PostMapping("/battles/{battleId}/run")
	public ResponseEntity<BattleDTO> run(@AuthenticationPrincipal User currentUser, @PathVariable Long battleId)
			throws ResourceNotFoundException, GameRuleException {
		return ResponseEntity.ok(gameService.run(currentUser.getId(), battleId));
	}

	/**
     * Valida y aplica un objeto sobre un Pokémon del jugador.
     *
     * @param currentUser usuario autenticado
     * @param request código de objeto e identificador del Pokémon objetivo
     * @return Pokémon con sus datos actualizados
     * @throws ResourceNotFoundException si el Pokémon no existe o no pertenece al jugador
     * @throws QuantityNotValidException si no hay unidades disponibles del objeto
     * @throws GameRuleException si el objeto no se puede aplicar en el estado actual
     */
	@PostMapping("/items/use")
	public ResponseEntity<PlayerPokemonDTO> useItem(@AuthenticationPrincipal User currentUser,
			@RequestBody UseItemDTO request)
			throws ResourceNotFoundException, QuantityNotValidException, GameRuleException {
		return ResponseEntity
				.ok(gameService.useItem(currentUser.getId(), request.getItemCode(), request.getPlayerPokemonId()));
	}
}
