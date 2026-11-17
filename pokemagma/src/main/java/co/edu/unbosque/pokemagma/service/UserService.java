package co.edu.unbosque.pokemagma.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.pokemagma.dto.UserDTO;
import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.repository.UserRepository;

/**
 * Servicio que gestiona las operaciones de las cuentas de ususario
 * 
 * Implementa las operaciones CRUD definidas en CRUDOperation y proporciona
 * metodos para consultar usuarios y validar sus credenciales
 */
@Service
public class UserService implements CRUDOperation<UserDTO> {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Constructor vacio del servicio
	 */
	public UserService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Crea un usuario y codifica su contraseña
	 * 
	 * @param data DTO con los datos del usuario
	 * @return 0 si se creo, 1 si el nombre ta existe, 2 si el corroe ya existe
	 */
	@Override
	public int create(UserDTO data) {
		if (userRepo.existsByUsername(data.getUsername())) {
			return 1;
		}

		if (userRepo.existsByEmail(data.getEmail())) {
			return 2;
		}

		User entity = modelMapper.map(data, User.class);
		entity.setPassword(passwordEncoder.encode(data.getPassword()));
		entity.setRole(User.Role.PLAYER);
		entity.setEmailVerified(false);

		userRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todos los usuarios
	 * 
	 * @return lista de ususarios
	 */
	@Override
	public List<UserDTO> getAll() {

		List<User> entityList = userRepo.findAll();
		List<UserDTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {

			UserDTO dto = modelMapper.map(entity, UserDTO.class);
			dtoList.add(dto);
		});

		return dtoList;
	}

	/**
	 * Actualiza un usuario existente
	 *
	 * @param id      identificador del usuario
	 * @param newData DTO con los datos nuevos
	 * @return 0 si se actualizo, 1 si el nombre esta ocupado, 2 si el corroe esta
	 *         ocupado, 3 si no existe
	 */
	@Override
	public int updateById(Long id, UserDTO newData) {
		Optional<User> found = userRepo.findById(id);
		Optional<User> newFound = userRepo.findByUsername(newData.getUsername());

		if (found.isPresent() && !newFound.isPresent()) {
			User temp = found.get();
			temp.setUsername(newData.getUsername());
			temp.setPassword(passwordEncoder.encode(newData.getPassword()));
			userRepo.save(temp);
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
	 * Cuenta los ususarios registrados
	 */
	@Override
	public Long count() {
		return userRepo.count();
	}

	/**
	 * Comprueba si existe un usuario por ID
	 * 
	 * @param id identificador del usuario
	 * @return true si existe
	 */
	@Override
	public boolean exist(Long id) {
		return userRepo.existsById(id);
	}

	/**
	 * Busca un usuasrio por su ID y lo convierte en DTO
	 * 
	 * @param id identificador del usuario
	 * @return DTO del susario sin contraseña o null si no existe
	 */
	public UserDTO getById(Long id) {
		Optional<User> found = userRepo.findById(id);
		if (found.isPresent()) {
			return modelMapper.map(found.get(), UserDTO.class);
		} else {
			return null;
		}
	}

	/**
	 * Comprueba si el nombre de ususario de una cuenta ya esta registrado
	 * 
	 * @param newUser usuario que se desea comprobar
	 * @return true si el nombre ya esta registrado
	 */
	public boolean findUsernameAlreadyTaken(User newUser) {
		Optional<User> found = userRepo.findByUsername(newUser.getUsername());
		if (found.isPresent()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Comprueba si un nombre de ususario ya esta registrado
	 * 
	 * @param username nombre de ususario
	 * @return true si esta registrado
	 */
	public boolean findUsernameAlreadyTaken(String username) {
		Optional<User> found = userRepo.findByUsername(username);
		return found.isPresent();
	}

	/**
	 * Elimina un usuario por su ID
	 * 
	 * @param id identificador del usuario
	 * @return 0 si se elimino, 1 si no existe
	 */
	@Override
	public int deleteById(Long id) {
		Optional<User> found = userRepo.findById(id);
		if (found.isPresent()) {
			userRepo.delete(found.get());
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * Comprueba si la contraseña recibida coincide con la contraseña almacenada
	 * 
	 * @param username nombre de ususario
	 * @param password contraseña recibida
	 * @return 0 si las credenciales coinciden, 1 si son incorrectos
	 */
	public int validateCredentials(String username, String password) {
		Optional<User> userOpt = userRepo.findByUsername(username);

		if (userOpt.isPresent()) {
			User user = userOpt.get();
			if (passwordEncoder.matches(password, user.getPassword())) {
			return 0;
			}
		}

		return 1;
	}

	/**
	 * Comprueba si un correo ya esta registrado
	 * 
	 * @param email correo electronico que se busca
	 * @return true si esta registrado
	 */
	public boolean findEmailAlreadyTaken(String email) {
		Optional<User> found = userRepo.findByEmail(email);
		return found.isPresent();
	}

}
