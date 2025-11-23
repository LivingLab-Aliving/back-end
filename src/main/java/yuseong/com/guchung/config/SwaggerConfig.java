package yuseong.com.guchung.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("유성구청 프로그램 API")
                .description("유성구청 교육 프로그램 신청 및 관리 서비스 API 명세서")
                .version("1.0.0");
    }
}