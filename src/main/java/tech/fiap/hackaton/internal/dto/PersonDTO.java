package tech.fiap.hackaton.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PersonDTO {

	private String nome;

	private String cpf;

	private String senha;

	private String email;

}
