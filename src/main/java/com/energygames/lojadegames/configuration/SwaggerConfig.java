package com.energygames.lojadegames.configuration;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI springEnergyGamesOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Projeto Energy Games")
						.description("Projeto Energy Games - Felipe Macedo\n\n" +
								"**🔐 Autenticação:**\n" +
								"- Esta API utiliza JWT (JSON Web Token) para autenticação\n" +
								"- Use o endpoint `/usuarios/logar` para obter seu token\n" +
								"- Clique no botão 🔓 **Authorize** acima e insira: `Bearer seu-token-aqui`\n\n" +
								"**Novidades:**\n" +
								"- ✅ Integração completa com IGDB API\n" +
								"- ✅ Importação automática de jogos\n" +
								"- ✅ Sincronização periódica de dados\n" +
								"- ✅ Endpoints administrativos para gerenciar importações\n\n" +
								"**Endpoints IGDB Admin (requer role ADMIN):**\n" +
								"- Importar jogo por ID\n" +
								"- Buscar jogos na IGDB\n" +
								"- Sincronizar produtos\n" +
								"- Estatísticas de importação")
						.version("v1.0.0")
						.license(new License()
								.name("Felipe Macedo")
								.url("https://github.com/felipemacedo1/"))
						.contact(new Contact()
								.name("Felipe Macedo")
								.url("https://github.com/felipemacedo1/")
								.email("conteudogeneration@generation.org")))
				.externalDocs(new ExternalDocumentation()
						.description("Github")
						.url("https://github.com/felipemacedo1/"))
				// Configuração de Segurança JWT
				.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
				.components(new Components()
						.addSecuritySchemes("Bearer Authentication",
								new SecurityScheme()
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")
										.description("Insira o token JWT obtido no endpoint /usuarios/logar.\n\n" +
												"Formato: Bearer {token}\n\n" +
												"Exemplo: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")));
	}

	@Bean
	public OpenApiCustomizer globalApiResponsesCustomizer() {
		return openApi -> {
			openApi.getPaths().values().forEach(pathItem ->
					pathItem.readOperations().forEach(operation -> {

						ApiResponses apiResponses = operation.getResponses();

						apiResponses.addApiResponse("200", createApiResponse("Sucesso!"));
						apiResponses.addApiResponse("201", createApiResponse("Objeto Persistido!"));
						apiResponses.addApiResponse("204", createApiResponse("Objeto Excluído!"));
						apiResponses.addApiResponse("400", createApiResponse("Erro na Requisição!"));
						apiResponses.addApiResponse("401", createApiResponse("Acesso Não Autorizado!"));
						apiResponses.addApiResponse("403", createApiResponse("Acesso Proibido!"));
						apiResponses.addApiResponse("404", createApiResponse("Objeto Não Encontrado!"));
						apiResponses.addApiResponse("500", createApiResponse("Erro na Aplicação!"));

					})
			);
		};
	}

	private final ApiResponse createApiResponse(String message) {
		return new ApiResponse().description(message);
	}
}