package tech.fiap.hackaton.internal.usecase;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginImplTest {

	@Mock
	private PersonRepository personRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private LoginImpl loginService;

	private Person validPerson;

	private final String validEmail = "usuario@teste.com";

	private final String validPassword = "senha123";

	private final String encodedPassword = "$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4HkJ6QY2J1J4rR3VtVJ7iJ8lY1OW";

	@BeforeEach
	void setUp() {
		validPerson = new Person();
		validPerson.setEmail(validEmail);
		validPerson.setSenha(encodedPassword);
	}

	@Test
	void login_ShouldReturnTokenWhenCredentialsAreValid() {
		// Arrange
		when(personRepository.findByEmail(validEmail)).thenReturn(Optional.of(validPerson));
		when(passwordEncoder.matches(validPassword, encodedPassword)).thenReturn(true);

		// Act
		String token = loginService.login(validEmail, validPassword);

		// Assert
		assertNotNull(token);
		assertFalse(token.isEmpty());

		// Verificar se o token é válido
		JwtParser parser = Jwts.parserBuilder()
				.setSigningKey(Keys.hmacShaKeyFor("suaChaveSecretaMuitoSeguraEComplexa1234567890".getBytes())).build();

		Jws<Claims> claims = parser.parseClaimsJws(token);
		assertEquals(validEmail, claims.getBody().getSubject());
		assertEquals("USER", claims.getBody().get("roles"));

		verify(personRepository, times(1)).findByEmail(validEmail);
		verify(passwordEncoder, times(1)).matches(validPassword, encodedPassword);
	}

	@Test
	void login_ShouldThrowExceptionWhenUserNotFound() {
		// Arrange
		when(personRepository.findByEmail(anyString())).thenReturn(Optional.empty());

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> loginService.login("email@inexistente.com", "qualquersenha"));

		assertEquals("Credenciais inválidas", exception.getMessage());
		verify(personRepository, times(1)).findByEmail("email@inexistente.com");
		verify(passwordEncoder, never()).matches(anyString(), anyString());
	}

	@Test
	void login_ShouldThrowExceptionWhenPasswordIsInvalid() {
		// Arrange
		when(personRepository.findByEmail(validEmail)).thenReturn(Optional.of(validPerson));
		when(passwordEncoder.matches("senhaerrada", encodedPassword)).thenReturn(false);

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> loginService.login(validEmail, "senhaerrada"));

		assertEquals("Credenciais inválidas", exception.getMessage());
		verify(personRepository, times(1)).findByEmail(validEmail);
		verify(passwordEncoder, times(1)).matches("senhaerrada", encodedPassword);
	}

	@Test
	void login_ShouldGenerateTokenWithCorrectExpiration() {
		// Arrange
		when(personRepository.findByEmail(validEmail)).thenReturn(Optional.of(validPerson));
		when(passwordEncoder.matches(validPassword, encodedPassword)).thenReturn(true);

		// Act
		String token = loginService.login(validEmail, validPassword);

		// Assert
		JwtParser parser = Jwts.parserBuilder()
				.setSigningKey(Keys.hmacShaKeyFor("suaChaveSecretaMuitoSeguraEComplexa1234567890".getBytes())).build();

		Claims claims = parser.parseClaimsJws(token).getBody();
		Date expiration = claims.getExpiration();
		Date issuedAt = claims.getIssuedAt();

		// Verifica se a expiração é 20 minutos após a emissão (1200000 ms = 20 minutos)
		assertEquals(1200000, expiration.getTime() - issuedAt.getTime(), 1000); // Margem
																				// de 1
																				// segundo
	}

}