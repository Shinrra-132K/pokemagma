package co.edu.unbosque.pokemagma.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.repository.UserRepository;

@Configuration
public class LoadDatabase {
	
	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
	
	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		
		return args -> {
            String username = "teammagmaadmin";
            String email = "juegosfps1@gmail.com";
            String password = "m1MikYuu2026$$+";

            if (userRepository.findByUsername(username).isPresent()) {
                log.info("El administrador ya existe; se omite su creación.");
                return;
            }

            if (userRepository.findByEmail(email).isPresent()) {
                throw new IllegalStateException(
                        "El correo configurado para el administrador ya pertenece a otra cuenta.");
            }

            User admin = new User();
            admin.setUsername(username);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(User.Role.ADMIN);
            admin.setEmailVerified(true);
            admin.setEnabled(true);
            admin.setAccountNonExpired(true);
            admin.setAccountNonLocked(true);
            admin.setCredentialsNonExpired(true);

            userRepository.save(admin);
            log.info("Se creó la cuenta de administrador.");
        };
		
	}

}
