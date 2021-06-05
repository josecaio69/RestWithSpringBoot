package br.com.erudio.security.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import br.com.erudio.exception.InvalidJwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtTokenProvider {

	@Value("${security.jwt.token.secret-key:secret}")
	private String secretKey = "secret";

	@Value("${security.jwt.token.expire-lenght:3600000}")
	private long validityInMilliseconds = 3600000; /* Equilavele a UMA hora, que é tempo de duração do TOKEN */

	@Autowired
	private UserDetailsService userDetailsService;

	@PostConstruct
	public void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	/* Abaixo vamos criar de fato o token de segurança */

	public String createToken(String userName, List<String> roles) {
		/* Recebemos o nome do usuario e a lista de papeis do mesmo */

		Claims claims = Jwts.claims().setSubject(userName);
		claims.put("roles", roles);
		/*
		 * Calculando o tempo de validade do tokem, a data atual + o tempo de validade
		 * maximo do token definido na variavel validityInMilliseconds
		 */
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);
		
		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(validity)
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}
	
	public Authentication getAuthentication(String token) {
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUserName(token));
		
		return new UsernamePasswordAuthenticationToken(userDetails, "" ,userDetails.getAuthorities());
	}

	private String getUserName(String token) {
		/*Aqui ele pega o Jwts seta o valor da secret
		 * pega o token a ser decodificado 
		 * retorna o body e pega o sub, que é o username*/
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}
	
	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Athorization");
		
		if(bearerToken != null && bearerToken.startsWith("Bearer "))
			return bearerToken.substring(7, bearerToken.length());
		return null;
	}
	
	/*Abaixo vamos validar o token de segurança*/
	
	public boolean validateToken(String token ) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			if(claims.getBody().getExpiration().before(new Date())) {
				
				return false;
			}
			return true;
		} catch (Exception e) {
			throw new InvalidJwtAuthenticationException("Expired or invalid token");
		}
	}
}
