package co.edu.unbosque.pokemagma.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.pokemagma.dto.GameEventDTO;
import co.edu.unbosque.pokemagma.dto.UserDTO;
import co.edu.unbosque.pokemagma.dto.UserPresenceDTO;
import co.edu.unbosque.pokemagma.service.GameEventService;
import co.edu.unbosque.pokemagma.service.PresenceService;
import co.edu.unbosque.pokemagma.service.UserService;

/**
 * Controlador de consulta de auditoría para el administrador.
 *
 * Las rutas bajo {@code /admin} deben estar protegidas para el rol
 * administrativo en SecurityConfig
 */
@RestController
@RequestMapping("/admin/audit")
public class AdminAuditController {

	private final GameEventService eventService;
	private final UserService userService;
	private final PresenceService presenceService;

	/**
	 * Crea el controlador de auditoría.
	 *
	 * @param eventService    servicio de consulta de eventos
	 * @param userService     servicio de consulta de cuentas
	 * @param presenceService servicio que consulta la conexión de los jugadores
	 */
	public AdminAuditController(GameEventService eventService, UserService userService,
			PresenceService presenceService) {
		this.eventService = eventService;
		this.userService = userService;
		this.presenceService = presenceService;
	}

	/**
	 * Devuelve todos los eventos registrados.
	 *
	 * @return eventos o 204 si aún no hay eventos
	 */
	@GetMapping("/events/getall")
	public ResponseEntity<List<GameEventDTO>> getAllEvents() {
		List<GameEventDTO> events = eventService.getAll();

		if (events.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(events);
	}

	/**
	 * Busca un evento por su identificador.
	 *
	 * @param id identificador del evento
	 * @return evento o 404 si no existe
	 */
	@GetMapping("/events/getbyid/{id}")
	public ResponseEntity<GameEventDTO> getEventById(@PathVariable Long id) {
		GameEventDTO event = eventService.getById(id);

		if (event == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(event);
	}

	/**
	 * Devuelve los eventos de un jugador específico.
	 *
	 * @param playerId identificador del jugador
	 * @return eventos del jugador o 204 si no tiene eventos
	 */
	@GetMapping("/events/getbyplayer/{playerId}")
	public ResponseEntity<List<GameEventDTO>> getEventsByPlayer(@PathVariable Long playerId) {
		List<GameEventDTO> events = eventService.getByPlayerId(playerId);

		if (events.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(events);
	}

	/**
	 * Devuelve las cuentas que no están habilitadas.
	 *
	 * La contraseña se elimina de los DTO antes de responder. Según la
	 * implementación de User.isEnabled , una cuenta sin verificar también puede
	 * aparecer en este resultado
	 *
	 * @return cuentas no habilitadas o 204 si no hay ninguna
	 */
	@GetMapping("/users/disabled")
	public ResponseEntity<List<UserDTO>> getDisabledUsers() {
		List<UserDTO> users = userService.getAll().stream().filter(user -> !user.isEnabled()).toList();

		// UserDTO contiene contraseña; no se devuelve al cliente.
		users.forEach(user -> user.setPassword(null));

		if (users.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(users);
	}

	/**
	 * Cuenta las cuentas que no están habilitadas.
	 *
	 * @return objeto JSON con la propiedad {@code count}
	 */
	@GetMapping("/users/disabled/count")
	public ResponseEntity<Map<String, Long>> countDisabledUsers() {
		long count = userService.getAll().stream().filter(user -> !user.isEnabled()).count();

		return ResponseEntity.ok(Map.of("count", count));
	}

	/**
	 * Devuelve la conexión actual de los jugadores.
	 *
	 * @return jugadores activos o desconectados y la hora de su último heartbeat
	 */
	@GetMapping("/users/presence")
	public ResponseEntity<List<UserPresenceDTO>> getPlayerPresence() {
		return ResponseEntity.ok(presenceService.getAllPlayerPresence());
	}

}
