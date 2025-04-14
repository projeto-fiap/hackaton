package tech.fiap.hackaton.internal.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.fiap.hackaton.internal.repository.PersonRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletePersonImplTest {

	@Mock
	private PersonRepository personRepository;

	@InjectMocks
	private DeletePersonImpl deletePerson;

	@Test
	void deletePerson_ShouldDeleteWhenPersonExists() {
		// Arrange
		Long id = 1L;
		when(personRepository.existsById(id)).thenReturn(true);
		doNothing().when(personRepository).deleteById(id);

		// Act & Assert
		assertDoesNotThrow(() -> deletePerson.deletePerson(id));

		// Verify
		verify(personRepository, times(1)).existsById(id);
		verify(personRepository, times(1)).deleteById(id);
	}

	@Test
	void deletePerson_ShouldThrowExceptionWhenPersonNotExists() {
		// Arrange
		Long id = 99L;
		when(personRepository.existsById(id)).thenReturn(false);

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, () -> deletePerson.deletePerson(id));

		assertEquals("Pessoa não encontrada", exception.getMessage());

		// Verify
		verify(personRepository, times(1)).existsById(id);
		verify(personRepository, never()).deleteById(any());
	}

	@Test
	void deletePerson_ShouldVerifyCorrectIdIsUsed() {
		// Arrange
		Long expectedId = 5L;
		when(personRepository.existsById(expectedId)).thenReturn(true);
		doNothing().when(personRepository).deleteById(expectedId);

		// Act
		deletePerson.deletePerson(expectedId);

		// Verify
		verify(personRepository).existsById(expectedId);
		verify(personRepository).deleteById(expectedId);
	}

}