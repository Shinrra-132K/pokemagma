package co.edu.unbosque.pokemagma.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configura la autenticacion y las reglas de acceso HTTP de la aplicacion
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/**
	 * Filtro de autenticacion JWT que procesa los tokens en las solicitudes
	 */
	private final JwtAuthenticationFilter jwtAuthFilter;

	/**
	 * Servicio que carga los detalles del usuario para la autenticacion
	 */
	private final UserDetailsService userDetailsService;

	/**
	 * Constructor que inicializa los componentes necesarios para la seguridad
	 * 
	 * @param jwtAuthFilter      Filtro que procesa tokens JWT
	 * @param userDetailsService Servicio para cargar detalles de usuarios
	 */
	public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
		super();
		this.jwtAuthFilter = jwtAuthFilter;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Configura la cadena de filtros de seguridad HTTP. Define las reglas de
	 * acceso, manejo de sesiones y filtros de autenticacion
	 * 
	 * @param http Configuracion de seguridad HTTP
	 * @return Cadena de filtros de seguridad configurada
	 * @throws Exception Si ocurre un error durante la configuracion
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// Integra CORS en Spring Security para que el navegador pueda acceder a la API
		// desde el origen del frontend definido en corsConfigurationSource().
		http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**").permitAll()
						.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/player/**")
						.hasAnyRole("PLAYER", "ADMIN").anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * Configura el proveedor de autenticacion. Establece el servicio de detalles de
	 * usuario y el codificador de contraseñas
	 * 
	 * @return Proveedor de autenticacion configurado
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	/**
	 * Configura el gestor de autenticacion
	 * 
	 * @param config Configuracion de autenticacion
	 * @return Gestor de autenticacion
	 * @throws Exception Si ocurre un error durante la configuracion
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	/**
	 * Configura el codificador de contraseñas. Utiliza BCrypt para el hash de
	 * contraseñas
	 * 
	 * @return Codificador de contraseñas BCrypt
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Define las solicitudes de otros origenes que el navegador puede enviar a la API.
	 *
	 * Actualmente permite el frontend local de Angular en http://localhost:4200,
	 * los metodos HTTP usados por la aplicacion y los encabezados JSON y Bearer JWT.
	 * No habilita credenciales de navegador, la autenticacion protegida
	 * utiliza el token JWT en el encabezado Authorization
	 *
	 * @return fuente de configuracion CORS aplicada a todas las rutas de la API
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowedOrigins(List.of("http://localhost:4200"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		config.setAllowCredentials(false);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return source;
	}

}
