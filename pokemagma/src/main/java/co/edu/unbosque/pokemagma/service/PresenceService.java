package co.edu.unbosque.pokemagma.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import co.edu.unbosque.pokemagma.dto.UserPresenceDTO;
import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.repository.UserRepository;

/**
 * Registra la última actividad de cada jugador y calcula su estado de conexión.
 */
@Service
public class PresenceService {

	/**
	 * Tiempo máximo desde el último heartbeat para considerar activo al jugador.
	 */
	private static final Duration ONLINE_WINDOW = Duration.ofSeconds(30);

	/** Mapa concurrente de identificadores de usuario a su último heartbeat. */
	private final ConcurrentMap<Long, Instant> lastSeenByUser = new ConcurrentHashMap<>();

	/** Repositorio usado para obtener las cuentas de jugador. */
	private final UserRepository userRepository;

	/**
	 * Crea el servicio de presencia.
	 *
	 * @param userRepository repositorio de usuarios
	 */
	public PresenceService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	/**
	 * Registra la hora actual como última actividad del usuario.
	 *
	 * @param userId identificador del usuario autenticado
	 */
	public void heartbeat(Long userId) {
		lastSeenByUser.put(userId, Instant.now());
	}

	/**
	 * Obtiene todos los jugadores y calcula si siguen dentro del tiempo de actividad.
	 *
	 * @return lista de jugadores con su estado y la hora del último heartbeat
	 */
	public List<UserPresenceDTO> getAllPlayerPresence() {
		List<UserPresenceDTO> presenceList = new ArrayList<>();
		Instant now = Instant.now();
		Instant onlineThreshold = now.minus(ONLINE_WINDOW);

		for (User user : userRepository.findAll()) {
			if (user.getRole() != User.Role.PLAYER) {
				continue;
			}

			Instant lastSeenAt = lastSeenByUser.get(user.getId());
			boolean recentlySeen = lastSeenAt != null && !lastSeenAt.isBefore(onlineThreshold);
			boolean active = recentlySeen && user.isEnabled() && user.isEmailVerified();

			String status;
			if (active) {
				status = "ACTIVE";
			} else {
				status = "DISCONNECTED";
			}

			UserPresenceDTO presence = new UserPresenceDTO(user.getId(), user.getUsername(), status, lastSeenAt);

			presenceList.add(presence);
		}

		return presenceList;
	}

}
