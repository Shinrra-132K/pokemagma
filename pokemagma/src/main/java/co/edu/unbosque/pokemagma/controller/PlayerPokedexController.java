package co.edu.unbosque.pokemagma.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.pokemagma.dto.PokedexEntryDTO;
import co.edu.unbosque.pokemagma.exception.ResourceNotFoundException;
import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.service.PokedexEntryService;
import co.edu.unbosque.pokemagma.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * Controlador de consulta de la Pokédex del jugador autenticado.
 */
@RestController
@RequestMapping("/player/pokedex")
@SecurityRequirement(name = "bearerAuth")
public class PlayerPokedexController {

	private final PokedexEntryService pokedexService;
	private final UserService userService;

	/**
	 * Crea el controlador de Pokédex.
	 *
	 * @param pokedexService servicio que consulta las entradas de la Pokédex
	 */
	public PlayerPokedexController(PokedexEntryService pokedexService, UserService userService) {
		super();
		this.pokedexService = pokedexService;
		this.userService = userService;
	}

	/**
	 * Obtiene las especies que el jugador autenticado ha visto o capturado.
	 *
	 * @param principal identidad del jugador autenticado
	 * @return entradas de la Pokédex, o respuesta 204 si aún no hay entradas
	 * @throws ResourceNotFoundException si no se encuentra la cuenta autenticada
	 */
	@GetMapping("/getall")
	public ResponseEntity<List<PokedexEntryDTO>> getAll(Principal principal) throws ResourceNotFoundException {

		Long playerId = userService.findIdByUsername(principal.getName());
		List<PokedexEntryDTO> entries = pokedexService.getByPlayerId(playerId);

		if (entries.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(entries);
	}

	/**
	 * Cuenta las especies que el jugador autenticado ha capturado.
	 *
	 * @param principal identidad del jugador autenticado
	 * @return cantidad de especies capturadas
	 * @throws ResourceNotFoundException si no se encuentra la cuenta autenticada
	 */
	@GetMapping("/caught/count")
	public ResponseEntity<Long> countCaught(Principal principal) throws ResourceNotFoundException {
		Long playerId = userService.findIdByUsername(principal.getName());
		return ResponseEntity.ok(pokedexService.countCaughtByPlayerId(playerId));
	}

}
