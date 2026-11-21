package co.edu.unbosque.pokemagma.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.pokemagma.dto.PlayerProfileDTO;
import co.edu.unbosque.pokemagma.exception.ResourceNotFoundException;
import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.service.PlayerProfileService;
import co.edu.unbosque.pokemagma.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * Controlador para consultar y modificar el perfil del jugador autenticado.
 *
 * El ID del usuario se obtiene del JWT; el cliente no puede elegir a qué cuenta
 * pertenece el perfil.
 */
@RestController
@RequestMapping("/player/profile")
@SecurityRequirement(name = "bearerAuth")
public class PlayerProfileController {

	private final PlayerProfileService profileService;
	private final UserService userService;

	/**
	 * Crea el controlador del perfil.
	 *
	 * @param profileService servicio que gestiona los perfiles
	 */
	public PlayerProfileController(PlayerProfileService profileService, UserService userService) {
		super();
		this.profileService = profileService;
		this.userService = userService;
	}

	/**
	 * Obtiene el perfil del jugador autenticado.
	 *
	 * @param principal identidad del jugador autenticado
	 * @return perfil del jugador, o respuesta 404 si todavía no existe
	 * @throws ResourceNotFoundException si no se encuentra la cuenta autenticada
	 */
	@GetMapping("/get")
	public ResponseEntity<PlayerProfileDTO> get(Principal principal) throws ResourceNotFoundException {
		Long playerId = userService.findIdByUsername(principal.getName());
		PlayerProfileDTO profile = profileService.getByUserId(playerId);

		if (profile == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(profile);
	}

	/**
	 * Crea el perfil del jugador autenticado usando los datos recibidos.
	 *
	 * @param principal identidad del jugador autenticado
	 * @param request   datos iniciales del perfil
	 * @return respuesta 201 si se creó, 409 si ya existe un perfil o 400 si no se
	 *         pudo crear
	 * @throws ResourceNotFoundException si no se encuentra la cuenta autenticada
	 */
	@PostMapping(path = "/createjson", consumes = "application/json")
	public ResponseEntity<String> create(Principal principal, @RequestBody PlayerProfileDTO request)
			throws ResourceNotFoundException {

		Long playerId = userService.findIdByUsername(principal.getName());
		request.setUserId(playerId);
		int result = profileService.create(request);

		if (result == 0) {
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
		if (result == 1) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("El jugador ya tiene un perfil");
		}
		return ResponseEntity.badRequest().body("No se encontró la cuenta del jugador");
	}

	/**
	 * Actualiza el género y la apariencia del perfil del jugador autenticado.
	 *
	 * @param principal identidad del jugador autenticado
	 * @param request   nuevos datos del perfil
	 * @return respuesta 204 si se actualizó, 404 si el perfil no existe o 400 si no
	 *         se pudo actualizar
	 * @throws ResourceNotFoundException si no se encuentra la cuenta autenticada
	 */
	@PutMapping(path = "/updatejson", consumes = "application/json")
	public ResponseEntity<String> update(Principal principal, @RequestBody PlayerProfileDTO request)
			throws ResourceNotFoundException {

		Long playerId = userService.findIdByUsername(principal.getName());
		PlayerProfileDTO existing = profileService.getByUserId(playerId);

		if (existing == null) {
			return ResponseEntity.notFound().build();
		}

		request.setUserId(playerId);
		int result = profileService.updateById(existing.getId(), request);

		if (result == 0) {
			return ResponseEntity.noContent().build();
		}
		if (result == 2) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.badRequest().body("No se pudo actualizar el perfil");
	}

}
