package tech.fiap.hackaton.internal.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginImplTest {

	@Mock
	private PersonRepository personRepository;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private LoginImpl loginService;

	private final String testEmail = "test@example.com";
	private final String testPassword = "password123";
	private final String testToken = "test-access-token";

	@BeforeEach
	void setUp() {
		// Configura valores mockados para evitar chamadas reais de rede
		ReflectionTestUtils.setField(loginService, "baseUrl", "http://test-keycloak");
		ReflectionTestUtils.setField(loginService, "realm", "test-realm");
	}

	@Test
	void login_ShouldThrowExceptionWhenUserNotFound() {
		// Arrange
		when(personRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(RuntimeException.class, () -> loginService.login(testEmail, testPassword));
		verify(personRepository).findByEmail(testEmail);
		verifyNoInteractions(restTemplate);
	}


}