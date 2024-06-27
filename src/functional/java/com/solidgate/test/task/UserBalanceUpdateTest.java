package com.solidgate.test.task;

import com.solidgate.test.task.config.CustomPostgreSqlContainer;
import com.solidgate.test.task.model.User;
import com.solidgate.test.task.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@Testcontainers
@ActiveProfiles("local")
@SpringBootTest(classes = SolidgateTestTaskApplication.class, webEnvironment = RANDOM_PORT)
public class UserBalanceUpdateTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = CustomPostgreSqlContainer.getInstance();

    @LocalServerPort
    private Integer port;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void updateUserBalances_twentyUsers() {
        Map<Integer, Integer> requestBody = TestObjects.mapWithTwentyUsers();

        given()
            .body(requestBody)
            .when()
            .header(new Header("Content-Type", "application/json"))
            .put("/users/balance")
            .then()
            .statusCode(200);

        assertTrue(assertUserBalances(requestBody.keySet(), requestBody));
    }

    private boolean assertUserBalances(Collection<Integer> userIds, Map<Integer, Integer> requestBody) {
        List<User> users = userRepository.findByIds(userIds);
        for (User user : users) {
            if (!Objects.equals(user.getBalance(), requestBody.get(user.getId()))) {
                return false;
            }
        }
        return true;
    }
}
