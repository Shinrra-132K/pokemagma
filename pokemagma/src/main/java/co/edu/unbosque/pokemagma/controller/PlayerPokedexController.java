package co.edu.unbosque.pokemagma.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.pokemagma.dto.PokedexEntryDTO;
import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.service.PokedexEntryService;
/**
 * Controlador de consulta de la Pokédex del jugador autenticado.
 */
@RestController
@RequestMapping("/player/pokedex")
public class PlayerPokedexController {

	private final PokedexEntryService pokedexService;
	
	/**
     * Crea el controlador de Pokédex.
     *
     * @param pokedexService servicio que consulta las entradas de la Pokédex
     */
	public PlayerPokedexController(PokedexEntryService pokedexService) {
		this.pokedexService = pokedexService;
	}

	/**
	 * Devuelve las especies vistas o capturadas por el usuario autenticado.
	 *
	 * @param currentUser usuario obtenido del JWT
	 * @return entradas de Pokédex o 204 si aún no hay entradas
	 */
	@GetMapping("/getall")
	public ResponseEntity<List<PokedexEntryDTO>> getAll(@AuthenticationPrincipal User currentUser) {

		List<PokedexEntryDTO> entries = pokedexService.getByPlayerId(currentUser.getId());

		if (entries.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(entries);
	}

	/**
	 * Cuenta cuántas especies ha capturado el usuario autenticado.
	 *
	 * @param currentUser usuario obtenido del JWT
	 * @return cantidad de especies capturadas
	 */
	@GetMapping("/caught/count")
	public ResponseEntity<Long> countCaught(@AuthenticationPrincipal User currentUser) {

		return ResponseEntity.ok(pokedexService.countCaughtByPlayerId(currentUser.getId()));
	}

}

