package co.edu.unbosque.pokemagma.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.pokemagma.model.User;

/**
 * Repositorio para realizar operaciones de persistencia sobre usuarios. Hereda
 * las operaciones CRUD de JpaRepository y define las consultas adicionales por
 * nombre de usuario y correo electronico.
 */
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Busca un usuario por su nombre de usuario
	 * 
	 * @param username nombre de usuario buscado
	 * @returnusuario encontrado o vacio si este no existe
	 */
	public Optional<User> findByUsername(String username);

	/**
	 * Busca un usuario por correo sin distinguir mayusculas y minusculas
	 * 
	 * @param email correo que se desea buscar
	 * @return usuario encontrado o vacio si este no existe
	 */
	public Optional<User> findByEmail(String email);

	/**
	 * Comprueba si el nombre de ususario ya se encuentra registrado
	 * 
	 * @param username nombre de usuario
	 * @return true si existe
	 */
	public boolean existsByUsername(String username);

	/**
	 * Comprueba si el correo ya esta registrado sin distinguir entre mayusculas o
	 * minusculas
	 * 
	 * @param email correo del usuario
	 * @return true si existe
	 */
	public boolean existsByEmail(String email);

}
