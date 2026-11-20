package co.edu.unbosque.pokemagma.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuración común de la aplicación. */
@Configuration
public class AppConfig {
	
	/**
	 * Crea el componente usado para mapear objetos.
	 *
	 * @return instancia de ModelMapper administrada por Spring
	 */
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
