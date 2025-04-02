package tech.fiap.hackaton.api.usecase;

import tech.fiap.hackaton.internal.dto.PersonWithVideoDTO;

public interface GetUserByVideoHash {

	PersonWithVideoDTO findUserByVideoHash(String hashNome);

}
