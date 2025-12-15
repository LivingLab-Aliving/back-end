package yuseong.com.guchung.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement; // SecurityRequirement import 추가
import io.swagger.v3.oas.models.security.SecurityScheme; // SecurityScheme import 추가
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        final String securitySchemeName = "BearerAuth";

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .name("Authorization")
                                .in(SecurityScheme.In.HEADER)
                                .description("JWT Access Token을 입력해주세요. (Bearer 접두사 없이 토큰 값만)")
                        )
                )
                .security(java.util.List.of(new SecurityRequirement().addList(securitySchemeName)))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("유성구청 프로그램 API")
                .description("유성구청 교육 프로그램 신청 및 관리 서비스 API 명세서")
                .version("1.0.0");
    }
}