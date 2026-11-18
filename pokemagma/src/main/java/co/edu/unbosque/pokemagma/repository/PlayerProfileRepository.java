package co.edu.unbosque.pokemagma.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.pokemagma.model.PlayerProfile;

/**
 * Repositorio para los perfiles de los jugadores
 */
public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, Long> {
	
	/**
	 * Busca un perfil mediante el ID del usuario relacionado
	 * 
	 * @param userId identificador del usuario
	 * @return encontrado o vacio si este no existe
	 */
	public Optional<PlayerProfile> findByUser_Id(Long userId);
	
	/**
	 * Comprueba si el usuario ya tiene un perfil asociado
	 * 
	 * @param userId identificador del usuario 
	 * @return true si el perfil existe
	 */
	public boolean existsByUser_Id(Long userId);
	

}
