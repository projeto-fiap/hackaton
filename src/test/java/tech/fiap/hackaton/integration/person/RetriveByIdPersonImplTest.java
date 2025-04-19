package tech.fiap.hackaton.integration.person;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.fiap.hackaton.api.model.PersonResponse;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;
import tech.fiap.hackaton.internal.usecase.RetriveByIdPersonImpl;

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
    void getPersonById_shouldReturnPersonResponse_whenPersonExists() {
        // Arrange
        Long personId = 1L;
        Person mockPerson = new Person();
        mockPerson.setId(personId);
        mockPerson.setNome("João Silva");
        mockPerson.setCpf("12345678901");
        mockPerson.setEmail("joao@example.com");

        when(personRepository.findById(personId)).thenReturn(Optional.of(mockPerson));

        // Act
        PersonResponse response = retriveByIdPerson.getPersonById(personId);

        // Assert
        assertNotNull(response);
        assertEquals(personId, response.id());
        assertEquals("João Silva", response.nome());
        assertEquals("12345678901", response.cpf());
        assertEquals("joao@example.com", response.email());

        verify(personRepository, times(1)).findById(personId);
    }

    @Test
    void getPersonById_shouldThrowException_whenPersonNotFound() {
        // Arrange
        Long nonExistentId = 99L;
        when(personRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> retriveByIdPerson.getPersonById(nonExistentId));

        assertEquals("Pessoa não encontrada", exception.getMessage());
        verify(personRepository, times(1)).findById(nonExistentId);
    }
}
