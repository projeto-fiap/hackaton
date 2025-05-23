package tech.fiap.hackaton.internal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "O nome não pode estar em branco")
	@Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
	private String nome;

	@NotBlank(message = "O CPF não pode estar em branco")
	@Pattern(regexp = "\\d{11}", message = "O CPF deve conter 14 dígitos numéricos")
	@Column(unique = true, nullable = false, length = 14)
	private String cpf;

	@NotBlank(message = "A senha não pode estar em branco")
	@Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
	private String senha;

	@NotBlank(message = "O e-mail não pode estar em branco")
	@Email(message = "O e-mail deve ser válido")
	@Column(unique = true, nullable = false)
	private String email;

	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Video> videos = new ArrayList<>();

}
