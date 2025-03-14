package tech.fiap.hackaton.api.controller;


import tech.fiap.hackaton.api.model.PersonResponse;
import tech.fiap.hackaton.api.usecase.CreatePersonUseCase;
import tech.fiap.hackaton.api.usecase.LoginUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.fiap.hackaton.internal.dto.PersonDTO;

import java.util.Map;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private final CreatePersonUseCase createPersonUseCase;
    private final LoginUseCase loginUseCase;

    public PersonController(CreatePersonUseCase createPersonUseCase, LoginUseCase loginUseCase) {
        this.createPersonUseCase = createPersonUseCase;
        this.loginUseCase = loginUseCase;
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
}
