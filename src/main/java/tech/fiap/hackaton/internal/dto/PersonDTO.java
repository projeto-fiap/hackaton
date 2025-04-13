package tech.fiap.hackaton.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {

	private String nome;

	private String cpf;

	private String senha;

	private String email;

}
