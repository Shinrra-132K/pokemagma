package co.edu.unbosque.pokemagma.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.pokemagma.model.PasswordResetToken;
import co.edu.unbosque.pokemagma.model.User;

/**
 * Repositorio para consultar y persistir códigos de restablecimiento.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

	/**
	 * Busca el código de restablecimiento asociado a un usuario.
	 *
	 * @param user usuario propietario del código
	 * @return código encontrado, o un Optional vacío si no existe
	 */
	Optional<PasswordResetToken> findByUser(User user);

}
