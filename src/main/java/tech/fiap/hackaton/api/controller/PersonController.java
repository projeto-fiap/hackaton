package tech.fiap.hackaton.api.controller;


import tech.fiap.hackaton.api.model.PersonResponse;
import tech.fiap.hackaton.api.usecase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.fiap.hackaton.internal.dto.PersonDTO;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final CreatePerson createPersonUseCase;
    private final Login loginUseCase;
    private final RetriveAllPerson retriveAllPersonUseCase;
    private final DeletePerson deletePersonUseCase;
    private final UpdatePerson updatePersonUseCase;
    private final RetriveByIdPerson retriveByIdPersonUseCase;

    public PersonController(CreatePerson createPersonUseCase, Login loginUseCase, RetriveAllPerson retriveAllPersonUseCase, DeletePerson deletePersonUseCase, UpdatePerson updatePersonUseCase, RetriveByIdPerson retriveByIdPersonUseCase) {
        this.createPersonUseCase = createPersonUseCase;
        this.loginUseCase = loginUseCase;
        this.retriveAllPersonUseCase = retriveAllPersonUseCase;
        this.deletePersonUseCase = deletePersonUseCase;
        this.updatePersonUseCase = updatePersonUseCase;
        this.retriveByIdPersonUseCase = retriveByIdPersonUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<PersonResponse> registerPerson(@RequestBody PersonDTO personDTO) {
        PersonResponse personResponse = createPersonUseCase.createPerson(personDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(personResponse);
    }

    @GetMapping("/login")
    public String login(@RequestHeader("Authorization") String authorizationHeader) {
        String[] parts = authorizationHeader.split(" ");
        if (parts.length == 2 && parts[0].equalsIgnoreCase("Basic")) {
            String decoded = new String(java.util.Base64.getDecoder().decode(parts[1]));
            String[] credentials = decoded.split(":");
            if (credentials.length == 2) {
                String email = credentials[0];
                String senha = credentials[1];
                return loginUseCase.login(email, senha);
            }
        }
        throw new RuntimeException("Credenciais inválidas");
    }

    @GetMapping
    public List<PersonResponse> getAllPersons() {
        return retriveAllPersonUseCase.getAllPersons();
    }

    @GetMapping("/{id}")
    public PersonResponse getPersonById(@PathVariable Long id) {
        return retriveByIdPersonUseCase.getPersonById(id);
    }

    @PutMapping("/{id}")
    public PersonResponse updatePerson(@PathVariable Long id, @RequestBody PersonDTO personDTO) {
        return updatePersonUseCase.updatePerson(id, personDTO);
    }

    @DeleteMapping("/{id}")
    public void deletePerson(@PathVariable Long id) {
        deletePersonUseCase.deletePerson(id);
    }
}
