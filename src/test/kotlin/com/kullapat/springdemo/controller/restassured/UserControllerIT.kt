package com.kullapat.springdemo.controller.restassured

import com.kullapat.springdemo.controller.resource.UserResource
import com.kullapat.springdemo.model.User
import com.kullapat.springdemo.repository.UserRepository
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIT {

	@LocalServerPort
	var port: Int = 0

	@Autowired
	lateinit var userRepository: UserRepository

	@AfterEach
	fun cleanup() {
		userRepository.deleteAll().block()
	}

	@Test
	fun `create user, should return a new user`() {
		given().contentType(ContentType.JSON)
				.body(UserResource(firstName = "Kullapat", lastName = "T"))
				.`when`()
				.post("http://localhost:$port/api/v1/users")
				.then()
				.statusCode(200)
				.body("firstName", equalTo("Kullapat"))
				.body("lastName", equalTo("T"))
	}

	@Test
	fun `get users, should return list of users`() {
		val user1 = User("001", firstName = "First 1", lastName = "Last 2")
		val user2 = User("002", firstName = "First 1", lastName = "Last 2")
		userRepository.saveAll(listOf(user1, user2)).collectList().block()

		given().`when`()
				.get("http://localhost:$port/api/v1/users")
				.then()
				.statusCode(200)
				.body("", hasSize<User>(2))
				.body("[0].id", equalTo(user1.id))
				.body("[0].firstName", equalTo(user1.firstName))
				.body("[0].lastName", equalTo(user1.lastName))
				.body("[1].id", equalTo(user2.id))
				.body("[1].firstName", equalTo(user2.firstName))
				.body("[1].lastName", equalTo(user2.lastName))
	}

	@Test
	fun `get user by ID, should return a user`() {
		val user1 = User("001", firstName = "First 1", lastName = "Last 2")
		val user2 = User("002", firstName = "First 1", lastName = "Last 2")
		userRepository.saveAll(listOf(user1, user2)).collectList().block()

		given().pathParam("id", user2.id)
				.`when`()
				.get("http://localhost:$port/api/v1/users/{id}")
				.then()
				.statusCode(200)
				.body("id", equalTo(user2.id))
				.body("firstName", equalTo(user2.firstName))
				.body("lastName", equalTo(user2.lastName))
	}

	@Test
	fun `update user, should return an updated user`() {
		val user1 = User("001", firstName = "First 1", lastName = "Last 2")
		val user2 = User("002", firstName = "First 1", lastName = "Last 2")
		userRepository.saveAll(listOf(user1, user2)).collectList().block()

		given().contentType(ContentType.JSON)
				.body(UserResource(firstName = "Kullapat", lastName = "T"))
				.pathParam("id", user2.id)
				.`when`()
				.put("http://localhost:$port/api/v1/users/{id}")
				.then()
				.statusCode(200)
				.body("id", equalTo(user2.id))
				.body("firstName", equalTo("Kullapat"))
				.body("lastName", equalTo("T"))
	}

	@Test
	fun `delete user, should return success`() {
		val user1 = User("001", firstName = "First 1", lastName = "Last 2")
		val user2 = User("002", firstName = "First 1", lastName = "Last 2")
		userRepository.saveAll(listOf(user1, user2)).collectList().block()

		given().pathParam("id", user2.id)
				.`when`()
				.delete("http://localhost:$port/api/v1/users/{id}")
				.then()
				.statusCode(200)
	}
}
