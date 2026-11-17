package co.edu.unbosque.pokemagma.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import co.edu.unbosque.pokemagma.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * Genera, lee y valida tokens JWT
 */
@Component
public class JwtUtil {

	/** Duración del token: 24 horas en milisegundos. */
	private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

	/** Clave secreta codificada en Base64, leída desde la configuración. */
	@Value("${jwt.secret}")
	private String secret;

	/**
	 * Convierte la clave configurada en una clave de firma.
	 *
	 * @return clave para firmar y verificar tokens
	 */
	private Key getSigningKey() {
		byte[] keyBytes = secret.getBytes();
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * Extrae el nombre de usuario del token.
	 *
	 * @param token token JWT
	 * @return nombre de usuario asociado al token
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Extrae la fecha de expiración del token.
	 *
	 * @param token token JWT
	 * @return fecha de expiración
	 */
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * Extrae un dato específico de los claims del token.
	 *
	 * @param <T>            tipo del dato extraído
	 * @param token          token JWT
	 * @param claimsResolver función que obtiene el dato deseado
	 * @return dato extraído
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Lee y verifica los claims del token.
	 *
	 * @param token token JWT
	 * @return claims del token
	 */
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	/**
	 * Comprueba si el token expiró.
	 *
	 * @param token token JWT
	 * @return true si expiró; false en caso contrario
	 */
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	/**
	 * Genera un token JWT para la cuenta autenticada.
	 *
	 * @param userDetails datos de la cuenta autenticada
	 * @return token JWT firmado
	 */
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("authorities",
				userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

		return createToken(claims, userDetails.getUsername());
	}

	/**
	 * Crea y firma un token JWT.
	 *
	 * @param claims  información adicional del token
	 * @param subject nombre de usuario asociado al token
	 * @return token JWT firmado
	 */
	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	/**
	 * Comprueba que el token pertenezca al usuario y no haya expirado.
	 *
	 * @param token       token JWT
	 * @param userDetails datos de la cuenta
	 * @return true si el token es válido
	 */
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);

		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

}
