package com.moviefeedback.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.gateway.discovery.locator.enabled=false"
})
class CorsConfigurationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void corsPreflight_ForMoviesRoute_ShouldBeAllowedWithSingleOrigin() {
        webTestClient.options()
                .uri("/api/movies")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "content-type")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://localhost:3000");
    }

    @Test
    void corsPreflight_ForFeedbackRoute_ShouldBeAllowedWithSingleOrigin() {
        webTestClient.options()
                .uri("/api/feedback")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "content-type")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://localhost:3000");
    }

    @Test
    void corsPreflight_WithoutOriginConfigured_StillPermitsAnyOrigin() {
        webTestClient.options()
                .uri("/api/movies")
                .header("Origin", "http://some-other-host:5000")
                .header("Access-Control-Request-Method", "DELETE")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://some-other-host:5000");
    }
}
