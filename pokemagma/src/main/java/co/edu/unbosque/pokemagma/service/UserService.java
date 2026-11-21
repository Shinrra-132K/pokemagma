package co.edu.unbosque.pokemagma.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.pokemagma.dto.UserDTO;
import co.edu.unbosque.pokemagma.exception.EmailNotValidException;
import co.edu.unbosque.pokemagma.exception.PasswordNotValidException;
import co.edu.unbosque.pokemagma.exception.ResourceNotFoundException;
import co.edu.unbosque.pokemagma.exception.UsernameNotValidException;
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
		entity.setAccountNonExpired(true);
		entity.setAccountNonLocked(true);
		entity.setCredentialsNonExpired(true);
		entity.setEnabled(true);

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
	 * Actualiza los datos de un usuario existente.
	 *
	 * @param id      identificador del usuario que se actualizará
	 * @param newData DTO con el nuevo nombre, correo y contraseña
	 * @return 0 si se actualizó; 1 si el nombre pertenece a otro usuario; 2 si el
	 *         correo pertenece a otro usuario; 3 si el usuario no existe
	 */
	@Override
	public int updateById(Long id, UserDTO newData) {
		Optional<User> found = userRepo.findById(id);
		Optional<User> newFound = userRepo.findByUsername(newData.getUsername());
		Optional<User> emailFound = userRepo.findByEmail(newData.getEmail());

		if (found.isPresent() && !belongsToAnotherUser(newFound, id) && !belongsToAnotherUser(emailFound, id)) {
			User temp = found.get();
			temp.setUsername(newData.getUsername());
			temp.setEmail(newData.getEmail());

			if (newData.getPassword() != null && !newData.getPassword().isBlank()) {
				temp.setPassword(passwordEncoder.encode(newData.getPassword()));
			}

			userRepo.save(temp);
			return 0;
		}

		if (found.isPresent() && belongsToAnotherUser(newFound, id)) {
			return 1;
		}

		if (found.isPresent() && belongsToAnotherUser(emailFound, id)) {
			return 2;
		}

		if (!found.isPresent()) {
			return 3;
		} else {
			return 3;
		}
	}

	/**
	 * Comprueba si el nombre o correo encontrado pertenece a otra cuenta.
	 *
	 * @param found usuario encontrado por nombre o correo
	 * @param id    identificador del usuario que se está actualizando
	 * @return true si pertenece a otro usuario; false si no existe o es el mismo
	 */
	public boolean belongsToAnotherUser(Optional<User> found, Long id) {
		if (!found.isPresent()) {
			return false;
		}

		return !found.get().getId().equals(id);
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
	 * Busca una cuenta por su identificador y la convierte en DTO.
	 *
	 * @param id identificador de la cuenta
	 * @return DTO de la cuenta sin contraseña, o null si no existe
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
	 * Comprueba las credenciales y el estado de la cuenta.
	 *
	 * @param username nombre de usuario
	 * @param password contraseña recibida sin codificar
	 * @return 0 si las credenciales son válidas y la cuenta está habilitada, 1 si
	 *         el usuario no existe o la contraseña no coincide, 2 si el correo aún
	 *         no está verificado, 3 si la cuenta está deshabilitada, expirada o
	 *         bloqueada
	 */
	public int validateCredentials(String username, String password) {
		Optional<User> userOpt = userRepo.findByUsername(username);
		if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
			return 1;
		}

		User user = userOpt.get();

		if (!user.isEmailVerified()) {
			return 2;
		}

		if (!user.isEnabled() || !user.isAccountNonExpired() || !user.isAccountNonLocked()
				|| !user.isCredentialsNonExpired()) {
			return 3;
		}

		return 0;
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

	/**
	 * Valida el formato de los datos antes de registrar la cuenta.
	 *
	 * @param data datos de usuario recibidos para el registro
	 * @throws UsernameNotValidException si el nombre no cumple el formato
	 * @throws EmailNotValidException    si el correo no tiene un formato válido
	 * @throws PasswordNotValidException si la contraseña no cumple los requisitos
	 */
	public void validateRegistrationData(UserDTO data)
			throws UsernameNotValidException, EmailNotValidException, PasswordNotValidException {
		String username = data.getUsername();
		String email = data.getEmail();
		String password = data.getPassword();

		if (username == null || !username.matches("(?=.*\\d)(?=.*[^\\p{L}\\p{N}\\s])\\S{15}")) {
			throw new UsernameNotValidException();
		}

		if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			throw new EmailNotValidException();
		}

		if (password == null
				|| !password.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)" + "(?=.*[^\\p{L}\\p{N}\\s]).{8,15}")) {
			throw new PasswordNotValidException();
		}
	}

	/**
	 * Busca el identificador del usuario asociado al nombre de usuario.
	 *
	 * @param username nombre del usuario autenticado
	 * @return identificador del usuario
	 * @throws ResourceNotFoundException si el usuario no existe
	 */
	public Long findIdByUsername(String username) throws ResourceNotFoundException {
		return userRepo.findByUsername(username).map(User::getId).orElseThrow(() -> new ResourceNotFoundException());
	}

	/**
	 * Actualiza la contraseña de una cuenta y la guarda codificada.
	 *
	 * @param user        cuenta cuya contraseña se actualizará
	 * @param newPassword contraseña nueva sin codificar
	 */
	public void updatePassword(User user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepo.save(user);
	}

}
