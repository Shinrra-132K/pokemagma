package co.edu.unbosque.pokemagma.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.pokemagma.dto.GameEventDTO;
import co.edu.unbosque.pokemagma.model.GameEvent;
import co.edu.unbosque.pokemagma.repository.GameEventRepository;

/**
 * Servicio que gestiona los eventos de sesion y juego usado para auditoria
 */
@Service
public class GameEventService implements CRUDOperation<GameEventDTO> {

	/**
	 * Repositorio de evntos de juego
	 */
	@Autowired
	private GameEventRepository gameRepo;

	/**
	 * Componente para convertir entre entidad y DTO
	 */
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Consutructor vacio del servicio
	 */
	public GameEventService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Registra un evento, si el DTO no contiene fecha este se le asiganra la fecha
	 * actual
	 * 
	 * @param data DTO con los datos del evento
	 * @return 0 cuando se guarda el evento
	 */
	@Override
	public int create(GameEventDTO data) {
		GameEvent entity = modelMapper.map(data, GameEvent.class);

		if (entity.getCreatedAt() == null) {
			entity.setCreatedAt(Instant.now());
		}

		gameRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todos los eventos y los convierte a DTO
	 * 
	 * @return lista de eventos
	 */
	@Override
	public List<GameEventDTO> getAll() {
		List<GameEvent> entityList = gameRepo.findAllByOrderByCreatedAtDesc();

		List<GameEventDTO> dtoList = new ArrayList<>();

		entityList.forEach(entity -> dtoList.add(modelMapper.map(entity, GameEventDTO.class)));

		return dtoList;
	}

	/**
	 * Actualiza los datos descriptivos del evento conservando su fecha original
	 * 
	 * @param id      identificador del evento
	 * @param newData DTO con los nuevos datos
	 * @return 0 si se actualizo, 1 si el evento no existe
	 */
	@Override
	public int updateById(Long id, GameEventDTO newData) {
		Optional<GameEvent> found = gameRepo.findById(id);

		if (!found.isPresent()) {
			return 1;
		}

		GameEvent entity = found.get();
		entity.setPlayerId(newData.getPlayerId());
		entity.setType(newData.getType());
		entity.setDescription(newData.getDescription());

		gameRepo.save(entity);
		return 0;
	}

	/**
	 * Elimina un evento por su ID
	 * 
	 * @param id identificador del evento
	 * @return 0 si se elimino, 1 si no existe
	 */
	@Override
	public int deleteById(Long id) {
		Optional<GameEvent> found = gameRepo.findById(id);

		if (found.isPresent()) {
			gameRepo.delete(found.get());
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * Cuenta los eventos almacenados
	 * 
	 * @return cantidad de eventos
	 */
	@Override
	public Long count() {
		return gameRepo.count();
	}

	/**
	 * Comprueba si existe un evento en el ID indicado
	 * 
	 * @param id identificador del evento
	 * @return true si existe
	 */
	@Override
	public boolean exist(Long id) {
		return gameRepo.existsById(id);
	}

	/**
	 * Busca un evento por su ID
	 * 
	 * @param id identificador del evento
	 * @return DTO del evento, null si no existe
	 */
	public GameEventDTO getById(Long id) {
		Optional<GameEvent> found = gameRepo.findById(id);

		if (found.isPresent()) {
			return modelMapper.map(found.get(), GameEventDTO.class);
		} else {
			return null;
		}
	}

	/**
	 * Obtiene los eventos de un jugador, del mas reciente al mas antiguo
	 * 
	 * @param playerId identificador del jugador
	 * @return lista ordenada de eventos del jugador
	 */
	public List<GameEventDTO> getByPlayerId(Long playerId) {
		List<GameEvent> entityList = gameRepo.findAllByPlayerIdOrderByCreatedAtDesc(playerId);
		List<GameEventDTO> dtoList = new ArrayList<>();

		entityList.forEach((entity) -> {
			dtoList.add(modelMapper.map(entity, GameEventDTO.class));
		});

		return dtoList;
	}

}
