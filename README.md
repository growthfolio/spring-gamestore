# Spring GameStore

> **Projeto de estudo baseado em atividades de formação e experimentos posteriores.**
> Não é uma plataforma de e-commerce em produção.

Este repositório foi usado para praticar desenvolvimento de uma API com Java e
Spring Boot a partir do domínio simples de uma loja de jogos.

## Assuntos praticados

O código inclui ou explora temas como:

- controllers e APIs REST;
- Spring Data JPA e Hibernate;
- relacionamentos entre entidades;
- autenticação/autorização com JWT;
- validação de entrada;
- tratamento de exceções;
- migrations com Flyway;
- documentação com OpenAPI/Swagger;
- Docker para ambiente local.

Esses itens descrevem o conteúdo do projeto. Eles não significam que a aplicação
tenha sido operada como e-commerce real, submetida a auditoria de segurança ou
validada para carga de produção.

## Execução

O projeto requer Java, Maven e banco de dados compatível com a configuração atual.
Antes de executar, revise `application.properties`, migrations e variáveis de
ambiente do repositório.

Exemplo:

```bash
git clone https://github.com/growthfolio/spring-gamestore.git
cd spring-gamestore
./mvnw test
./mvnw spring-boot:run
```

## Contexto

Mantenho este código público como registro de aprendizado. Minha experiência
profissional atual e meus projetos em desenvolvimento estão centralizados em:

- https://felipemacedo.me
- https://github.com/felipemacedo1
