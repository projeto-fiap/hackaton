Feature: Criação de Pessoa

  Scenario: Criar pessoa com dados válidos
    Given Um novo cadastro de pessoa com dados válidos
    When Eu solicito a criação desta pessoa
    Then A pessoa deve ser criada com sucesso
    And A senha deve ser criptografada

  Scenario: Tentar criar pessoa com CPF existente
    Given Um cadastro de pessoa com CPF já existente
    When Eu solicito a criação desta pessoa
    Then Deve retornar um erro informando que o CPF já existe

  Scenario: Tentar criar pessoa com Email existente
    Given Um cadastro de pessoa com Email já existente
    When Eu solicito a criação desta pessoa
    Then Deve retornar um erro informando que o Email já existe