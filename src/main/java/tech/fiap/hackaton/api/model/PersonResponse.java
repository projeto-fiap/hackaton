package tech.fiap.hackaton.api.model;

public record PersonResponse(Long id,
                             String nome,
                             String cpf,
                             String email
                            ) {
}
