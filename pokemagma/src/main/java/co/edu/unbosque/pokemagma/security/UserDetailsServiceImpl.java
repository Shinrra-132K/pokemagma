package co.edu.unbosque.pokemagma.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import co.edu.unbosque.pokemagma.repository.UserRepository;

/**
 * Implementa el servicio de detalles de usuario para la autenticacion. Busca
 * las cuentas en el repositorio durante el proceso de autenbticacion
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	/**
	 * Repositorio usado para buscar cuentas por nombre de ususario
	 */
	private final UserRepository userRepository;

	/**
	 * Constructor que inicializa el repositorio de usuarios
	 * 
	 * @param userRepository El repositorio de ususarios a utilizar para las
	 *                       consultas
	 */
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Carga los detalles del usuario por su nombre de usuario.
	 * 
	 * @param username El nombre de usuario para buscar
	 * @return Los detalles del usuario encontrado
	 * @throws UsernameNotFoundException Si no se encuentra el usuario con el nombre
	 *                                   de usuario proporcionado
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("No se encontro el usuario: " + username));
	}

}
