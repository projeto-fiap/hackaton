package tech.fiap.hackaton.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {

	private String email;

	private String senha;

}
