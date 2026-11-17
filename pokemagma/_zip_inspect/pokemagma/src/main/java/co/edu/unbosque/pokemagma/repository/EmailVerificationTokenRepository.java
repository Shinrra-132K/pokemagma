package co.edu.unbosque.pokemagma.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.pokemagma.model.EmailVerificationToken;
import co.edu.unbosque.pokemagma.model.User;

/**
 * Repositorio para consultar y persistir tokens de verificacion
 */
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
	
	Optional<EmailVerificationToken> findByUser(User user);

}
