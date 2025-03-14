package tech.fiap.hackaton.internal.dto;

import lombok.Data;

@Data
public class PersonDTO {
    private String nome;
    private String cpf;
    private String senha;
    private String email;
}
