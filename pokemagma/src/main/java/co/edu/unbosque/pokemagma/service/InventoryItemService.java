package co.edu.unbosque.pokemagma.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.pokemagma.dto.InventoryItemDTO;
import co.edu.unbosque.pokemagma.model.InventoryItem;
import co.edu.unbosque.pokemagma.repository.InventoryItemRepository;

/**
 * Servicio que gestiona los objetos y cantidades del inventario de los
 * jugadores
 */
@Service
public class InventoryItemService implements CRUDOperation<InventoryItemDTO> {

	/**
	 * Repositorio de objetos del inventario
	 */
	@Autowired
	private InventoryItemRepository inventoryRepo;

	/**
	 * Componente para convertir entre entidad y DTO
	 */
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Constructor vacio del servicio
	 */
	public InventoryItemService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Crea una fila de inventario si el jugador aun no tiene ese objeto
	 * 
	 * @param data DTO con el jugador, el codigo del objeto y la cantidad
	 * @return 0 si se creo, 1 si el objeto ya existe para ese jugador
	 */
	@Override
	public int create(InventoryItemDTO data) {
		Optional<InventoryItem> found = inventoryRepo.findByPlayerIdAndItemCode(data.getPlayerId(), data.getItemCode());
		if (found.isPresent()) {
			return 1;
		}

		InventoryItem entity = modelMapper.map(data, InventoryItem.class);
		inventoryRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todas las filas de inventario y las convierte a DTO
	 * 
	 * @return lista de objetos de inventario
	 */
	@Override
	public List<InventoryItemDTO> getAll() {
		List<InventoryItem> entityList = inventoryRepo.findAll();
		List<InventoryItemDTO> dtoList = new ArrayList<>();

		entityList.forEach((entity) -> {
			dtoList.add(modelMapper.map(entity, InventoryItemDTO.class));
		});
		return dtoList;
	}

	/**
	 * Actualiza el jugador, el codigo de objeto y la cantidad de una fila
	 * 
	 * @param id      identificador de la fila
	 * @param newData DTO con los nuevos datos
	 * @return 0 si se actualizo, 1 si ya existe esa combinacion de jugador y
	 *         objeto, 2 si la fila no existe
	 */
	@Override
	public int updateById(Long id, InventoryItemDTO newData) {
		Optional<InventoryItem> found = inventoryRepo.findById(id);
		Optional<InventoryItem> itemFound = inventoryRepo.findByPlayerIdAndItemCode(newData.getPlayerId(),
				newData.getItemCode());

		if (!found.isPresent()) {
			return 2;
		}
		if (itemFound.isPresent() && !itemFound.get().getId().equals(id)) {
			return 1;
		}

		InventoryItem entity = found.get();
		entity.setPlayerId(newData.getId());
		entity.setItemCode(newData.getItemCode());
		entity.setQuantity(newData.getQuantity());

		inventoryRepo.save(entity);
		return 0;

	}

	/**
	 * Elimina una fila de inventario por su ID
	 * 
	 * @param id identyificador de la fila
	 * @return 0 si se elimino, 1 si no existe
	 */
	@Override
	public int deleteById(Long id) {
		Optional<InventoryItem> found = inventoryRepo.findById(id);

		if (found.isPresent()) {
			inventoryRepo.delete(found.get());
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * Cuenta las filas de inventario almacenadas
	 * 
	 * @return cantida de filas
	 */
	@Override
	public Long count() {
		return inventoryRepo.count();
	}

	/**
	 * Comprueba si existe una fila de inventario con el ID indicado
	 * 
	 * @param id identificador de la fila
	 * @return true si existe
	 */
	@Override
	public boolean exist(Long id) {
		return inventoryRepo.existsById(id);
	}
	
	/**
	 * Busca una fila de inventario por si ID
	 * 
	 * @param id identificador de la fila
	 * @return DTO de la fila o null si no existe
	 */
	public InventoryItemDTO getById(Long id) {
		Optional<InventoryItem> found = inventoryRepo.findById(id);
		
		if (found.isPresent()) {
			 return modelMapper.map(found.get(), InventoryItemDTO.class);
		} else {
			return null;
		}
	}
	
	/**
	 * Obtiene los objetos del inventario de un jugador
	 * 
	 * @param playerId identificador del jugador
	 * @return lista de objetos pertenecientes al jugador
	 */
	public List<InventoryItemDTO> getByPlayerId(Long playerId) {
		List<InventoryItem> entityList = inventoryRepo.findAllByPlayerId(playerId);
		List<InventoryItemDTO> dtoList = new ArrayList<>();
		
		entityList.forEach((entity) -> {
			dtoList.add(modelMapper.map(entity, InventoryItemDTO.class));
		});
		
		return dtoList;
		
	}

}
