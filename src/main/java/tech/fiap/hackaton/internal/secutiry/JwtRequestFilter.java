package tech.fiap.hackaton.internal.secutiry;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	private final String SECRET_KEY = "suaChaveSecretaMuitoSeguraEComplexa1234567890";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = request.getHeader("Authorization");

		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
			try {
				Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())).build()
						.parseClaimsJws(token).getBody();

				String email = claims.getSubject();
				String rolesClaim = claims.get("roles", String.class);
				List<String> roles = Arrays.asList(rolesClaim.split(","));

				System.out.println("Email: " + email);
				System.out.println("Roles: " + roles);

				if (email != null) {
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null,
							roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
									.collect(Collectors.toList()));
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
			catch (Exception e) {
				SecurityContextHolder.clearContext();
			}
		}

		filterChain.doFilter(request, response);
	}

}