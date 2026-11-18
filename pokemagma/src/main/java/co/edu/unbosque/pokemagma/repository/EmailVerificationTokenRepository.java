package co.edu.unbosque.pokemagma.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.pokemagma.model.EmailVerificationToken;
import co.edu.unbosque.pokemagma.model.User;

/**
 * Repositorio para consultar y persistir tokens de verificacion
 */
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

	/**
	 * Busca el token de verificación asociado a un usuario.
	 *
	 * @param user usuario asociado al token
	 * @return un Optional con el token si existe; vacío si no se encuentra
	 */
	Optional<EmailVerificationToken> findByUser(User user);

}
