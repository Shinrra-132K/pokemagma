package co.edu.unbosque.pokemagma.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		SecurityScheme bearerScheme = new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")
				.bearerFormat("JWT").description("Ingresa el token JWT obtenido en /auth/login.");

		return new OpenAPI().info(new Info().title("Pokemagma API").version("1.0").description("""
				API del backend del juego Pokemagma.
				Incluye autenticación, perfiles, equipo Pokémon,
				exploración, combates, inventario, Pokédex y auditoría.
				""")).components(new Components().addSecuritySchemes("bearerAuth", bearerScheme));
	}

}
