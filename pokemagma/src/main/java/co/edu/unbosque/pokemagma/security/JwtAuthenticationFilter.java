package co.edu.unbosque.pokemagma.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Lee el token Bearer de cada solicitud y establece la autenticacion si es
 * valido
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	/**
	 * Utilidad para operaciones con tokens JWT
	 */
	private final JwtUtil jwtUtil;

	/**
	 * Servicio para cargar los detalles del usuario
	 */
	private final UserDetailsService userDetailsService;

	/**
	 * Constructor que inicializa las dependencias necesarias para el filtro
	 * 
	 * @param jwtUtil            utilidad de operaciones con tokens JWT
	 * @param userDetailsService servicio para cargar los detalles del usuario
	 */
	public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Metodo principal del filtro que se ejecuta para cada solicitud HTTP. Extrae y
	 * valida el token JWT del encabezado de autorizacion. Si el token es valido,
	 * establece la autenticacion en el contexto de seguridad.
	 * 
	 * @param request     Solicitud HTTP entrante
	 * @param response    Respuesta HTTP saliente
	 * @param filterChain Cadena de filtros para continuar el procesamiento
	 * @throws ServletException Si ocurre un error durante el procesamiento del
	 *                          servlet
	 * @throws IOException      Si ocurre un error de entrada/salida
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String header = request.getHeader("Authorization");

		if (header != null && header.startsWith("Bearer ")
				&& SecurityContextHolder.getContext().getAuthentication() == null) {
			String jwt = header.substring(7); // quita "Bearer "
			try {
				String username = jwtUtil.extractUsername(jwt);
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				if (jwtUtil.validateToken(jwt, userDetails) && userDetails.isEnabled()) {
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities());
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			} catch (JwtException e) {
				// Token mal formado, firma invalida o expirado: sigue sin autenticar
			} catch (IllegalArgumentException e) {
				// Token vacio o nulo: sigue sin autenticar
			} catch (UsernameNotFoundException e) {
				// El usuario del token ya no existe: sigue sin autenticar
			}
		}
		filterChain.doFilter(request, response);
	}

}
