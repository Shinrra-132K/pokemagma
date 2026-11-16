package co.edu.unbosque.pokemagma.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import co.edu.unbosque.pokemagma.dto.PokedexEntryDTO;
import co.edu.unbosque.pokemagma.model.PokedexEntry;
import co.edu.unbosque.pokemagma.repository.PokedexEntryRepository;

/**
 * Servicio que gestiona las especies vistas y capturadas por los jugadores
 */
public class PokedexEntryService implements CRUDOperation<PokedexEntryDTO> {

	/**
	 * Repositorio de entradas de la pokedex
	 */
	@Autowired
	private PokedexEntryRepository pokedexRepo;
	
	/**
	 * Componente para convertir entre entidad y DTO
	 */
	@Autowired
	private ModelMapper modelMapper;
	
	/**
	 * Constructor vacio del servicio
	 */
	public PokedexEntryService() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Crea una entrada si esa especie aun no aparece en la pokedex del jugador
	 * 
	 * @param data DTO con el jugador, la especie y sus estados
	 * @return 0 si se creo, 1 si la entrada ya existe
	 */
	@Override
	public int create(PokedexEntryDTO data) {
		Optional<PokedexEntry> found = pokedexRepo.findByPlayerIdAndSpeciesId(data.getPlayerId(), data.getSpeciesId());
		if (found.isPresent()) {
			return 1;
		}
		
		PokedexEntry entity =  modelMapper.map(data, PokedexEntry.class);
		pokedexRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todas las entradas y las convierte a DTO
	 * 
	 * @return lista de entradas de la pokedex
	 */
	@Override
	public List<PokedexEntryDTO> getAll() {
		List<PokedexEntry> entityList = pokedexRepo.findAll();
		List<PokedexEntryDTO> dtoList = new ArrayList<>();
		
		entityList.forEach((entity) -> {
			dtoList.add(modelMapper.map(entity, PokedexEntryDTO.class));
		});
		
		return dtoList;
	}

	/**
	 * Actualiza el jugador, la especie y los estados de la entrada
	 * 
	 * @param id identificador de la entrada
	 * @param newData DTO con los nuevos datos
	 * @return 0 si se aactualizo, 1 si esa especie ya tiene otra entrada para el jugador, 2 si la entrada no existe
	 */
	@Override
	public int updateById(Long id, PokedexEntryDTO newData) {
		Optional<PokedexEntry> found = pokedexRepo.findById(id);
		Optional<PokedexEntry> entryFound = pokedexRepo.findByPlayerIdAndSpeciesId(newData.getId(), newData.getSpeciesId());
		
		if (!found.isPresent()) {
			return 2;
		}
		if (entryFound.isPresent() && !entryFound.get().getId().equals(id)) {
			return 1;
		}
		
		PokedexEntry entity = found.get();
		entity.setPlayerId(newData.getPlayerId());
		entity.setSpeciesId(newData.getSpeciesId());
		entity.setSeen(newData.isSeen());
		entity.setCaught(newData.isCaught());
		
		pokedexRepo.save(entity);
		return 0;
	}

	/**
	 * Elimina una entrada por su ID
	 * 
	 * @param id identificador de la entrada
	 * @return 0 si se elimino, 1 si no existe
	 */
	@Override
	public int deleteById(Long id) {
		Optional<PokedexEntry> found = pokedexRepo.findById(id);
		
		if (found.isPresent()) {
			pokedexRepo.delete(found.get());
			return 0;
		} else {
			return 1;
		}
		
	}

	/**
	 * Cuenta las entradas de la pokedex almacenadas
	 * 
	 * @return cantidad de entradas
	 */
	@Override
	public Long count() {
		return pokedexRepo.count();
	}

	/**
	 * Comprueba si existe una entrada con el id indicado
	 * 
	 * @param id identificador de la entrada
	 * @return true si existe 
	 */
	@Override
	public boolean exist(Long id) {
		return pokedexRepo.existsById(id);
	}
	
	/**
	 * Busca una entrada por su ID
	 * 
	 * @param id identificador de la entrada
	 * @return DTO de la entrada o null si no existe
	 */
	public PokedexEntryDTO getById(Long id) {
		Optional<PokedexEntry> found = pokedexRepo.findById(id);
		
		if (found.isPresent()) {
			return modelMapper.map(found.get(), PokedexEntryDTO.class);
		} else {
			return null;
		}
	}
	
	/**
	 * Obtiene las entradas de la pokedex de un jugador
	 * 
	 * @param playerId indentificador del jugador
	 * @return lista de entradas del jugador
	 */
	public List<PokedexEntryDTO> getByPlayerId(Long playerId) {
		List<PokedexEntry> entityList = pokedexRepo.findAllByPlayerId(playerId);
		List<PokedexEntryDTO> dtoList = new ArrayList<>();
		
		entityList.forEach((entity) -> {
			dtoList.add(modelMapper.map(entity, PokedexEntryDTO.class));
		});
		
		return dtoList;
	}
	
	/**
	 * Cuenta cuantas especies han capturado un jugador
	 * 
	 * @param playerId identificador del jugador
	 * @return cantidad de especies capturadas
	 */
	public Long countCaughtByPlayerId(Long playerId) {
		return pokedexRepo.countByPlayerIdAndCaughtTrue(playerId);
	}

}
