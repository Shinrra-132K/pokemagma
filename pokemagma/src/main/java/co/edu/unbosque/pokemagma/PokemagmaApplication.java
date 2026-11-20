package co.edu.unbosque.pokemagma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada de la aplicación Pokemagma con Spring Boot. */
@SpringBootApplication
public class PokemagmaApplication {

	/**
	 * Inicia la aplicación.
	 *
	 * @param args argumentos recibidos al ejecutar la aplicación
	 */
	public static void main(String[] args) {
		SpringApplication.run(PokemagmaApplication.class, args);
	}

}
