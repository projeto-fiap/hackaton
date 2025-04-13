package tech.fiap.hackaton.internal.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.fiap.hackaton.api.model.PersonResponse;
import tech.fiap.hackaton.internal.dto.PersonDTO;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePersonImplTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private UpdatePersonImpl updatePerson;

    @Test
    void updatePerson_ShouldUpdateAndReturnUpdatedPerson() {
        // Arrange
        Long id = 1L;
        Person existingPerson = new Person();
        existingPerson.setId(id);
        existingPerson.setNome("Nome Antigo");
        existingPerson.setCpf("111.111.111-11");
        existingPerson.setSenha("senhaAntiga");
        existingPerson.setEmail("antigo@email.com");

        PersonDTO updateData = new PersonDTO();
        updateData.setNome("Novo Nome");
        updateData.setCpf("222.222.222-22");
        updateData.setSenha("novaSenha");
        updateData.setEmail("novo@email.com");

        when(personRepository.findById(id)).thenReturn(Optional.of(existingPerson));
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PersonResponse result = updatePerson.updatePerson(id, updateData);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals(updateData.getNome(), result.nome());
        assertEquals(updateData.getCpf(), result.cpf());
        assertEquals(updateData.getEmail(), result.email());

        verify(personRepository, times(1)).findById(id);
        verify(personRepository, times(1)).save(existingPerson);
    }

    @Test
    void updatePerson_ShouldThrowExceptionWhenPersonNotFound() {
        // Arrange
        Long id = 99L;
        PersonDTO updateData = new PersonDTO();
        when(personRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> updatePerson.updatePerson(id, updateData));

        assertEquals("Pessoa não encontrada", exception.getMessage());
        verify(personRepository, times(1)).findById(id);
        verify(personRepository, never()).save(any());
    }

    @Test
    void updatePerson_ShouldUpdateAllFieldsCorrectly() {
        // Arrange
        Long id = 2L;
        Person existingPerson = new Person();
        existingPerson.setId(id);

        PersonDTO updateData = new PersonDTO();
        updateData.setNome("Nome Completo");
        updateData.setCpf("333.333.333-33");
        updateData.setSenha("senhaSegura");
        updateData.setEmail("email@atualizado.com");

        when(personRepository.findById(id)).thenReturn(Optional.of(existingPerson));
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PersonResponse result = updatePerson.updatePerson(id, updateData);

        // Assert
        assertAll(
                () -> assertEquals(updateData.getNome(), result.nome()),
                () -> assertEquals(updateData.getCpf(), result.cpf()),
                () -> assertEquals(updateData.getEmail(), result.email()),
                () -> assertEquals(id, result.id())
        );
    }

    @Test
    void updatePerson_ShouldVerifyFieldUpdatesBeforeSaving() {
        // Arrange
        Long id = 3L;
        Person existingPerson = new Person();
        existingPerson.setId(id);

        PersonDTO updateData = new PersonDTO();
        updateData.setNome("Nome Teste");
        updateData.setCpf("444.444.444-44");
        updateData.setSenha("senhaTeste");
        updateData.setEmail("teste@email.com");

        when(personRepository.findById(id)).thenReturn(Optional.of(existingPerson));
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> {
            Person savedPerson = invocation.getArgument(0);
            assertEquals(updateData.getNome(), savedPerson.getNome());
            assertEquals(updateData.getCpf(), savedPerson.getCpf());
            assertEquals(updateData.getSenha(), savedPerson.getSenha());
            assertEquals(updateData.getEmail(), savedPerson.getEmail());
            return savedPerson;
        });

        // Act
        updatePerson.updatePerson(id, updateData);

        // Assert (verifications are done in the mock)
    }
}