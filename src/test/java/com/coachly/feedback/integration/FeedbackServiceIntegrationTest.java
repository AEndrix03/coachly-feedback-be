package com.coachly.feedback.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FeedbackServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("feedback")
            .withUsername("feedback")
            .withPassword("feedback");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    ObjectMapper objectMapper;

    @LocalServerPort
    int port;

    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void shouldCreateFeatureVoteAndRemoveVote() throws Exception {
        HttpResponse<String> createResponse = request(
                "POST",
                "/api/v1/feature-requests",
                Map.of(
                        "title", "New timer",
                        "description", "Need interval timer",
                        "category", "UX",
                        "platformTarget", "android",
                        "moduleKey", "workout"
                ),
                UUID.randomUUID(),
                "USER"
        );
        assertEquals(HttpStatus.OK.value(), createResponse.statusCode());

        JsonNode createJson = objectMapper.readTree(createResponse.body());
        String featureId = createJson.get("data").get("id").asText();

        HttpResponse<String> voteResponse = request(
                "POST",
                "/api/v1/feature-requests/" + featureId + "/vote",
                Map.of("voteType", "UP"),
                UUID.randomUUID(),
                "USER"
        );
        assertEquals(HttpStatus.OK.value(), voteResponse.statusCode());

        HttpResponse<String> removeVoteResponse = request(
                "DELETE",
                "/api/v1/feature-requests/" + featureId + "/vote",
                null,
                UUID.randomUUID(),
                "USER"
        );
        assertEquals(HttpStatus.OK.value(), removeVoteResponse.statusCode());
    }

    @Test
    void shouldRejectDuplicatePollResponse() throws Exception {
        HttpResponse<String> createPollResponse = request(
                "POST",
                "/api/v1/admin/polls",
                Map.of(
                        "title", "Roadmap",
                        "description", "What next?",
                        "type", "SINGLE_CHOICE",
                        "multipleChoice", false,
                        "anonymousResults", true,
                        "opensAt", Instant.now().minusSeconds(60).toString(),
                        "closesAt", Instant.now().plusSeconds(3600).toString(),
                        "options", List.of("A", "B")
                ),
                UUID.randomUUID(),
                "ADMIN"
        );
        assertEquals(HttpStatus.OK.value(), createPollResponse.statusCode());

        JsonNode pollJson = objectMapper.readTree(createPollResponse.body());
        String pollId = pollJson.get("data").get("id").asText();
        String optionId = pollJson.get("data").get("options").get(0).get("id").asText();

        HttpResponse<String> publish = request(
                "PATCH",
                "/api/v1/admin/polls/" + pollId + "/publish",
                null,
                UUID.randomUUID(),
                "ADMIN"
        );
        assertEquals(HttpStatus.OK.value(), publish.statusCode());

        UUID userId = UUID.randomUUID();
        HttpResponse<String> first = request(
                "POST",
                "/api/v1/polls/" + pollId + "/responses",
                Map.of("optionIds", List.of(optionId)),
                userId,
                "USER"
        );
        assertEquals(HttpStatus.OK.value(), first.statusCode());

        HttpResponse<String> duplicate = request(
                "POST",
                "/api/v1/polls/" + pollId + "/responses",
                Map.of("optionIds", List.of(optionId)),
                userId,
                "USER"
        );
        assertEquals(HttpStatus.CONFLICT.value(), duplicate.statusCode());
    }

    @Test
    void shouldProvideFeedbackSummary() {
        UUID targetId = UUID.randomUUID();
        HttpResponse<String> create = requestUnchecked(
                "POST",
                "/api/v1/feedback",
                Map.of(
                        "type", "REVIEW",
                        "title", "Great",
                        "body", "Works",
                        "ratingValue", 5,
                        "category", "general",
                        "targetType", "FEATURE",
                        "targetId", targetId
                ),
                UUID.randomUUID(),
                "USER"
        );
        assertEquals(HttpStatus.OK.value(), create.statusCode());

        HttpResponse<String> summary = requestUnchecked(
                "GET",
                "/api/v1/feedback/summary?targetType=FEATURE&targetId=" + targetId,
                null,
                UUID.randomUUID(),
                "USER"
        );
        assertEquals(HttpStatus.OK.value(), summary.statusCode());
    }

    private HttpResponse<String> request(String method, String path, Object payload, UUID userId, String role) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url(path)))
                .header("X-User-Id", userId.toString())
                .header("X-User-Role", role);

        if (payload != null) {
            builder.header("Content-Type", "application/json");
            builder.method(method, HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> requestUnchecked(String method, String path, Object payload, UUID userId, String role) {
        try {
            return request(method, path, payload, userId, role);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
