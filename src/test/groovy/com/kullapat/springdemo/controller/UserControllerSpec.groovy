package com.kullapat.springdemo.controller

import com.kullapat.springdemo.controller.resource.UserResource
import com.kullapat.springdemo.model.User
import com.kullapat.springdemo.repository.UserRepository
import io.restassured.http.ContentType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerSpec extends Specification {
    @LocalServerPort
    def port = 0

    @Autowired
    UserRepository userRepository

    def cleanup() {
        userRepository.deleteAll().block()
    }

    def "create user, should return a new user"() {
        expect:
        given().contentType(ContentType.JSON)
                .body(new UserResource(firstName, lastName))
                .when()
                .post("http://localhost:$port/api/v1/users/")
                .then()
                .statusCode(200)
                .body("firstName", equalTo(firstName))
                .body("lastName", equalTo(lastName))

        where:
        firstName   | lastName
        "Kullapat"  | "Theera-angkananon"
        "ชื่อจริง"     | "นามสกุล"
        "فرستنم"    | "لستنمي"
        "!@#%^&*()" | "!@#%^&*()123456789"
    }

    def "get users, should return list of users"() {
        given:
        User user1 = new User("001", "KT-001", "Last-KT-001")
        User user2 = new User("002", "KT-002", "Last-KT-002")
        userRepository.saveAll(Arrays.asList(user1, user2)).collectList().block()

        when:
        def result = given().when().get("http://localhost:$port/api/v1/users")

        then:
        result.then()
                .statusCode(200)
                .body("", hasSize(2))
                .body("[0].id", equalTo(user1.id))
                .body("[0].firstName", equalTo(user1.firstName))
                .body("[0].lastName", equalTo(user1.lastName))
                .body("[1].id", equalTo(user2.id))
                .body("[1].firstName", equalTo(user2.firstName))
                .body("[1].lastName", equalTo(user2.lastName))
    }

    def "get user by ID, should return a user"() {
        given:
        User user1 = new User("001", "KT-001", "Last-KT-001")
        User user2 = new User("002", "KT-002", "Last-KT-002")
        userRepository.saveAll(Arrays.asList(user1, user2)).collectList().block()

        when:
        def result = given()
                        .pathParam("id", user2.id)
                        .when().get("http://localhost:$port/api/v1/users/{id}")

        then:
        result.then()
                .statusCode(200)
                .body("id", equalTo(user2.id))
                .body("firstName", equalTo(user2.firstName))
                .body("lastName", equalTo(user2.lastName))
    }

    def "update user, should return an updated user"() {
        given:
        User user1 = new User("001", "KT-001", "Last-KT-001")
        User user2 = new User("002", "KT-002", "Last-KT-002")
        userRepository.saveAll(Arrays.asList(user1, user2)).collectList().block()

        when:
        def result = given().contentType(ContentType.JSON)
                            .body(new UserResource(firstName, lastName))
                            .pathParam("id", user2.id)
                            .when()
                            .put("http://localhost:$port/api/v1/users/{id}")

        then:
        result.then()
                .statusCode(200)
                .body("id", equalTo(user2.id))
                .body("firstName", equalTo(firstName))
                .body("lastName", equalTo(lastName))

        where:
        firstName   | lastName
        "Kullapat"  | "Theera-angkananon"
        "ชื่อจริง"     | "นามสกุล"
        "فرستنم"    | "لستنمي"
        "!@#%^&*()" | "!@#%^&*()123456789"
    }

    def "delete user, should return success"() {
        given:
        User user1 = new User("001", "KT-001", "Last-KT-001")
        User user2 = new User("002", "KT-002", "Last-KT-002")
        userRepository.saveAll(Arrays.asList(user1, user2)).collectList().block()

        when:
        def result = given()
                .pathParam("id", user2.id)
                .when()
                .delete("http://localhost:$port/api/v1/users/{id}")

        then:
        result.then().statusCode(200)
    }
}
