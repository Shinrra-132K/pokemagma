package co.edu.unbosque.pokemagma.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.pokemagma.exception.ResourceNotFoundException;
import co.edu.unbosque.pokemagma.service.PresenceService;
import co.edu.unbosque.pokemagma.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * Recibe los heartbeats de actividad de los jugadores autenticados.
 */
@RestController
@RequestMapping("/player/presence")
@SecurityRequirement(name = "bearerAuth")
public class PlayerPresenceController {

	/** Servicio que registra la actividad de los jugadores. */
	private final PresenceService presenceService;

	/** Servicio que obtiene el identificador del usuario autenticado. */
	private final UserService userService;

	/**
	 * Crea el controlador de presencia.
	 *
	 * @param precenseService servicio que registra los heartbeats
	 * @param userService servicio que busca la cuenta autenticada
	 */
	public PlayerPresenceController(PresenceService precenseService, UserService userService) {
		super();
		this.presenceService = precenseService;
		this.userService = userService;
	}

	/**
	 * Registra la actividad del usuario autenticado.
	 *
	 * @param principal identidad recibida del contexto de seguridad
	 * @return respuesta 204 si se registró el heartbeat
	 * @throws ResourceNotFoundException si no se encuentra la cuenta autenticada
	 */
	@PostMapping("/heartbeat")
	public ResponseEntity<Void> heartbeat(Principal principal) throws ResourceNotFoundException {
		Long userId = userService.findIdByUsername(principal.getName());
		presenceService.heartbeat(userId);
		return ResponseEntity.noContent().build();
	}

}
