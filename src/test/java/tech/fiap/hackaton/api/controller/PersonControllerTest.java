package tech.fiap.hackaton.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tech.fiap.hackaton.api.model.PersonResponse;
import tech.fiap.hackaton.api.usecase.*;
import tech.fiap.hackaton.internal.dto.PersonDTO;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonControllerTest {

	@Mock
	private CreatePerson createPersonUseCase;

	@Mock
	private Login loginUseCase;

	@Mock
	private RetriveAllPerson retriveAllPersonUseCase;

	@Mock
	private DeletePerson deletePersonUseCase;

	@Mock
	private UpdatePerson updatePersonUseCase;

	@Mock
	private RetriveByIdPerson retriveByIdPersonUseCase;

	@InjectMocks
	private PersonController personController;

	private PersonDTO personDTO;

	private PersonResponse personResponse;

	@BeforeEach
	void setUp() {
		personDTO = new PersonDTO();
		personDTO.setNome("João Silva");
		personDTO.setCpf("123.456.789-00");
		personDTO.setEmail("joao@example.com");
		personDTO.setSenha("senha123");

		personResponse = new PersonResponse(1L, "João Silva", "123.456.789-00", "joao@example.com");
	}

	@Test
	void registerPerson_ShouldReturnCreatedResponse() {
		when(createPersonUseCase.createPerson(any(PersonDTO.class))).thenReturn(personResponse);

		ResponseEntity<PersonResponse> response = personController.registerPerson(personDTO);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(personResponse, response.getBody());
		verify(createPersonUseCase, times(1)).createPerson(personDTO);
	}

	@Test
	void login_WithValidBasicAuth_ShouldReturnToken() {
		String authHeader = "Basic am9hb0BleGFtcGxlLmNvbTpzZW5oYTEyMw=="; // joao@example.com:senha123
		when(loginUseCase.login("joao@example.com", "senha123")).thenReturn("token123");

		String result = personController.login(authHeader);

		assertEquals("token123", result);
		verify(loginUseCase, times(1)).login("joao@example.com", "senha123");
	}

	@Test
	void login_WithInvalidBasicAuthFormat_ShouldThrowException() {
		String authHeader = "InvalidHeader";

		assertThrows(RuntimeException.class, () -> personController.login(authHeader));
		verify(loginUseCase, never()).login(anyString(), anyString());
	}

	@Test
	void login_WithMalformedBasicAuth_ShouldThrowException() {
		String authHeader = "Basic invalidBase64";

		assertThrows(RuntimeException.class, () -> personController.login(authHeader));
		verify(loginUseCase, never()).login(anyString(), anyString());
	}

	@Test
	void getAllPersons_ShouldReturnListOfPersons() {
		List<PersonResponse> expectedList = Arrays.asList(
				new PersonResponse(1L, "João Silva", "123.456.789-00", "joao@example.com"),
				new PersonResponse(2L, "Maria Souza", "987.654.321-00", "maria@example.com"));
		when(retriveAllPersonUseCase.getAllPersons()).thenReturn(expectedList);

		List<PersonResponse> result = personController.getAllPersons();

		assertEquals(2, result.size());
		assertEquals(expectedList, result);
		verify(retriveAllPersonUseCase, times(1)).getAllPersons();
	}

	@Test
	void getPersonById_ShouldReturnPerson() {
		Long id = 1L;
		when(retriveByIdPersonUseCase.getPersonById(id)).thenReturn(personResponse);

		PersonResponse result = personController.getPersonById(id);

		assertEquals(personResponse, result);
		verify(retriveByIdPersonUseCase, times(1)).getPersonById(id);
	}

	@Test
	void updatePerson_ShouldReturnUpdatedPerson() {
		Long id = 1L;
		PersonDTO updatedDTO = new PersonDTO();
		updatedDTO.setNome("João Silva Atualizado");
		updatedDTO.setCpf("123.456.789-00");
		updatedDTO.setEmail("joao.novo@example.com");
		updatedDTO.setSenha("novaSenha123");

		PersonResponse updatedResponse = new PersonResponse(1L, "João Silva Atualizado", "123.456.789-00",
				"joao.novo@example.com");

		when(updatePersonUseCase.updatePerson(id, updatedDTO)).thenReturn(updatedResponse);

		PersonResponse result = personController.updatePerson(id, updatedDTO);

		assertEquals(updatedResponse, result);
		verify(updatePersonUseCase, times(1)).updatePerson(id, updatedDTO);
	}

	@Test
	void deletePerson_ShouldCallDeleteUseCase() {
		Long id = 1L;
		doNothing().when(deletePersonUseCase).deletePerson(id);

		personController.deletePerson(id);

		verify(deletePersonUseCase, times(1)).deletePerson(id);
	}

}