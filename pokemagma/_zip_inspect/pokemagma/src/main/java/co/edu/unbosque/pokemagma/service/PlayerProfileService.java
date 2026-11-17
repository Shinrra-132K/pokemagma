package co.edu.unbosque.pokemagma.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.pokemagma.dto.PlayerProfileDTO;
import co.edu.unbosque.pokemagma.model.PlayerProfile;
import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.repository.PlayerProfileRepository;
import co.edu.unbosque.pokemagma.repository.UserRepository;

/**
 * Servicio que gestiona los perfiles de entrenador asociados a las cuentas
 */
@Service
public class PlayerProfileService implements CRUDOperation<PlayerProfileDTO> {
	
	/**
	 * Repositorio de perfiles de jugador
	 */
	@Autowired
	private PlayerProfileRepository profileRepo;
	
	/**
	 * Repositorio para buscar la cuenta relacionada con el perfil
	 */
	@Autowired
	private UserRepository userRepo;
	
	/**
	 * Mapeador para convertir entre entidades PlayerProfile y DTOs PlayerProfileDTO
	 */
	@Autowired
	private ModelMapper modelMapper;
	
	/**
	 * Constructor vacio del servico
	 */
	public PlayerProfileService() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Crea un perfil para el usuario indicado
	 * 
	 * @param data DTO con el ususario, genero y apariencia
	 * @return 0 si se creo, 1 si el ususario ya cuenta con un perfil, 2 si el usuario no existe
	 */
	@Override
	public int create(PlayerProfileDTO data) {
		Optional<User> userFound = userRepo.findById(data.getId());
		if (!userFound.isPresent()) {
			return 2;
		}
		Optional<PlayerProfile> profileFound = profileRepo.findByUser_Id(data.getUserId());
		if (profileFound.isPresent()) {
			return 1;
		}
		
		PlayerProfile entity = new PlayerProfile(userFound.get(), data.getGender(), data.getTrainerAppearance());
		profileRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todos los perfiles y los convierte a DTO
	 * 
	 * @return lista de perfiles
	 */
	@Override
	public List<PlayerProfileDTO> getAll() {
		List<PlayerProfile> entityList = profileRepo.findAll();
		List<PlayerProfileDTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {
			PlayerProfileDTO dto = modelMapper.map(entity, PlayerProfileDTO.class);
			dtoList.add(dto);
		});
		
		return dtoList;
	}

	/**
	 * Actualiza el ususario asociado, el genero y la apariencia del perfil
	 * 
	 * @param id identificador del perfil
	 * @param newData DTO con los nuevos datos
	 * @return 0 si se actualizo, 1 si el usuario ya tiene otro perfil, 2 si el perfil no existe, si el ususario no existe
	 */
	@Override
	public int updateById(Long id, PlayerProfileDTO newData) {
		Optional<PlayerProfile> found = profileRepo.findById(id);
		Optional<PlayerProfile> newFound = profileRepo.findByUser_Id(newData.getId());
		
		if (found.isPresent() && newFound.isPresent() && newFound.get().getId().equals(id)) {
			
			PlayerProfile temp = found.get();
			PlayerProfile updatedData = modelMapper.map(newData, PlayerProfile.class);
			
			temp.setGender(updatedData.getGender());
			temp.setTrainerApparance(newData.getTrainerAppearance());
			
			profileRepo.save(temp);
			return 0;
		}
		
		if (found.isPresent() && newFound.isPresent()) {
			return 1;
		}
		if (!found.isPresent()) {
			return 2;
		} else {
			return 3;
		}
		
	}

	/**
	 * Elimina un perfil por su ID
	 * 
	 * @param id identificador del perfil
	 * @return 0 si se elimino, 1 si no existe
	 */
	@Override
	public int deleteById(Long id) {
		Optional<PlayerProfile> found = profileRepo.findById(id);
		if (found.isPresent()) {
			profileRepo.delete(found.get());
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * Cuenta los perfiles almacenados
	 * 
	 * @return cantidad de perfiles
	 */
	@Override
	public Long count() {
		return profileRepo.count();
	}

	/**
	 * Comprueba si existe un perfil con el ID indicado
	 * 
	 * @param id identificador del perfil
	 * @return true si existe
	 */
	@Override
	public boolean exist(Long id) {
		return profileRepo.existsById(id);
	}
	
	/**
	 * Busca un perfil por su ID
	 * 
	 * @param id identificador del perfil
	 * @return DTO del perfil
	 */
	public PlayerProfileDTO getById(Long id) {
		Optional<PlayerProfile> found = profileRepo.findById(id);
		
		if (found.isPresent()) {
			return modelMapper.map(found.get(), PlayerProfileDTO.class);
		} else {
			return null;
		}
	}
	
	/**
	 * Busca el perfil asociado a una cuenta de usuario
	 * 
	 * @param userId identificador del usuario
	 * @returnDTO del perfil
	 */
	public PlayerProfileDTO getByUserId(Long userId) {
		Optional<PlayerProfile> found = profileRepo.findById(userId);
		if (found.isPresent()) {
			return modelMapper.map(found.get(), PlayerProfileDTO.class);
		} else {
			return null;
		}
	}

}
