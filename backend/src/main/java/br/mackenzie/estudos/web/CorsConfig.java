package br.mackenzie.estudos.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Só é preciso se o front estiver hospedado em outro endereço (ex.: Vercel).
// Servido pelo próprio back-end, o navegador não aplica CORS
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] origens;

    public CorsConfig(@Value("${app.cors.origens:}") String[] origens) {
        this.origens = origens;
    }

    @Override
    public void addCorsMappings(CorsRegistry registro) {
        if (origens.length == 0 || origens[0].isBlank()) {
            return;
        }
        registro.addMapping("/api/**").allowedOrigins(origens).allowedMethods("GET", "POST", "DELETE");
    }
}
