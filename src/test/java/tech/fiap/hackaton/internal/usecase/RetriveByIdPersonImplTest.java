package tech.fiap.hackaton.internal.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.fiap.hackaton.api.model.PersonResponse;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetriveByIdPersonImplTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private RetriveByIdPersonImpl retriveByIdPerson;

    @Test
    void getPersonById_ShouldReturnPersonResponseWhenPersonExists() {
        // Arrange
        Long id = 1L;
        Person person = new Person();
        person.setId(id);
        person.setNome("João Silva");
        person.setCpf("123.456.789-00");
        person.setEmail("joao@example.com");

        when(personRepository.findById(id)).thenReturn(Optional.of(person));

        // Act
        PersonResponse result = retriveByIdPerson.getPersonById(id);

        // Assert
        assertNotNull(result);
        assertEquals(person.getId(), result.id());
        assertEquals(person.getNome(), result.nome());
        assertEquals(person.getCpf(), result.cpf());
        assertEquals(person.getEmail(), result.email());

        verify(personRepository, times(1)).findById(id);
    }

    @Test
    void getPersonById_ShouldThrowExceptionWhenPersonNotFound() {
        // Arrange
        Long id = 99L;
        when(personRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> retriveByIdPerson.getPersonById(id));

        assertEquals("Pessoa não encontrada", exception.getMessage());
        verify(personRepository, times(1)).findById(id);
    }

    @Test
    void getPersonById_ShouldMapAllFieldsCorrectly() {
        // Arrange
        Long id = 2L;
        Person person = new Person();
        person.setId(id);
        person.setNome("Maria Souza");
        person.setCpf("987.654.321-00");
        person.setEmail("maria@example.com");

        when(personRepository.findById(id)).thenReturn(Optional.of(person));

        // Act
        PersonResponse result = retriveByIdPerson.getPersonById(id);

        // Assert
        assertAll(
                () -> assertEquals(id, result.id()),
                () -> assertEquals("Maria Souza", result.nome()),
                () -> assertEquals("987.654.321-00", result.cpf()),
                () -> assertEquals("maria@example.com", result.email())
        );
    }

    @Test
    void getPersonById_ShouldUseCorrectIdForSearch() {
        // Arrange
        Long expectedId = 5L;
        Person person = new Person();
        person.setId(expectedId);
        person.setNome("Carlos Oliveira");
        person.setCpf("456.123.789-00");
        person.setEmail("carlos@example.com");

        when(personRepository.findById(expectedId)).thenReturn(Optional.of(person));

        // Act
        PersonResponse result = retriveByIdPerson.getPersonById(expectedId);

        // Assert
        assertEquals(expectedId, result.id());
        verify(personRepository).findById(expectedId);
    }
}