package co.edu.unbosque.pokemagma.controller;

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
import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.service.PlayerProfileService;

/**
 * Controlador para consultar y modificar el perfil del jugador autenticado.
 *
 * El ID del usuario se obtiene del JWT; el cliente no puede elegir a qué cuenta
 * pertenece el perfil.
 */
@RestController
@RequestMapping("/player/profile")
public class PlayerProfileController {

	private final PlayerProfileService profileService;

	/**
     * Crea el controlador del perfil.
     *
     * @param profileService servicio que gestiona los perfiles
     */
	public PlayerProfileController(PlayerProfileService profileService) {
		super();
		this.profileService = profileService;
	}

	/**
     * Busca el perfil asociado al jugador autenticado.
     *
     * @param currentUser usuario del contexto de seguridad
     * @return perfil encontrado o 404 si aún no existe
     */
	@GetMapping("/get")
	public ResponseEntity<PlayerProfileDTO> get(@AuthenticationPrincipal User currentUser) {
		PlayerProfileDTO profile = profileService.getByUserId(currentUser.getId());

		if (profile == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(profile);
	}

	/**
	 * Crea el perfil del usuario autenticado.
	 *
	 * @param currentUser usuario obtenido del contexto de seguridad
	 * @param request     género y apariencia seleccionados
	 * @return 201 si se creó; 409 si ya tenía perfil
	 */
	@PostMapping(path = "/createjson", consumes = "application/json")
	public ResponseEntity<?> create(@AuthenticationPrincipal User currentUser, @RequestBody PlayerProfileDTO request) {

		request.setUserId(currentUser.getId());
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
	 * Actualiza género y apariencia del perfil propio.
	 *
	 * @param currentUser usuario obtenido del contexto de seguridad
	 * @param request     nuevos datos del perfil
	 * @return 204 si se actualizó; 404 si no existe
	 */
	@PutMapping(path = "/updatejson", consumes = "application/json")
	public ResponseEntity<?> update(@AuthenticationPrincipal User currentUser, @RequestBody PlayerProfileDTO request) {

		PlayerProfileDTO existing = profileService.getByUserId(currentUser.getId());

		if (existing == null) {
			return ResponseEntity.notFound().build();
		}

		request.setUserId(currentUser.getId());
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

