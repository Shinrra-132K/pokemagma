package co.edu.unbosque.pokemagma.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declara beans compartidos por los componentes de la aplicación.
 */
@Configuration
public class AppConfig {
	
	/**
     * Crea el mapeador usado para convertir entre entidades y DTO.
     *
     * @return instancia de {@link ModelMapper} administrada por Spring
     */
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
