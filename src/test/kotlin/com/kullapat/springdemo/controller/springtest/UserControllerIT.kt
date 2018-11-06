package com.kullapat.springdemo.controller.springtest

import com.kullapat.springdemo.controller.resource.UserResource
import com.kullapat.springdemo.model.User
import com.kullapat.springdemo.repository.UserRepository
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain all`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIT {
    @Autowired
    lateinit var client: WebTestClient

    @Autowired
    lateinit var userRepository: UserRepository

    @AfterEach
    fun `cleanup`() {
        userRepository.deleteAll().block()
    }

    @Test
    fun `create user, should return a new user`() {
        val result = client.post()
                .uri("/api/v1/users")
                .syncBody(UserResource(firstName = "Kullapat", lastName = "T"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody(User::class.java)
                .returnResult()
                .responseBody!!

        result.firstName `should be equal to` "Kullapat"
        result.lastName `should be equal to` "T"
    }

    @Test
    fun `get users, should return list of users`() {
        val user1 = User("001", firstName = "First 1", lastName = "Last 2")
        val user2 = User("002", firstName = "First 1", lastName = "Last 2")
        userRepository.saveAll(listOf(user1, user2)).collectList().block()

        val result = client.get()
                .uri("/api/v1/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBodyList(User::class.java)
                .returnResult()
                .responseBody!!

        result.size `should be equal to` 2
        result `should contain all` listOf(user1, user2)
    }

    @Test
    fun `get user by ID, should return a user`() {
        val user1 = User("001", firstName = "First 1", lastName = "Last 2")
        val user2 = User("002", firstName = "First 1", lastName = "Last 2")
        userRepository.saveAll(listOf(user1, user2)).collectList().block()

        val result = client.get()
                .uri("/api/v1/users/${user2.id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody(User::class.java)
                .returnResult()
                .responseBody!!

        result.firstName `should be equal to` user2.firstName
        result.lastName `should be equal to` user2.lastName
    }

    @Test
    fun `update user, should return an updated user`() {
        val user1 = User("001", firstName = "First 1", lastName = "Last 2")
        val user2 = User("002", firstName = "First 1", lastName = "Last 2")
        userRepository.saveAll(listOf(user1, user2)).collectList().block()

        val result = client.put()
                .uri("/api/v1/users/${user2.id}")
                .syncBody(UserResource(firstName = "Kullapat", lastName = "T"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody(User::class.java)
                .returnResult()
                .responseBody!!

        result.id!! `should be equal to` user2.id!!
        result.firstName `should be equal to` "Kullapat"
        result.lastName `should be equal to` "T"
    }

    @Test
    fun `delete user, should return success`() {
        val user1 = User("001", firstName = "First 1", lastName = "Last 2")
        val user2 = User("002", firstName = "First 1", lastName = "Last 2")
        userRepository.saveAll(listOf(user1, user2)).collectList().block()

        client.delete()
                .uri("/api/v1/users/${user2.id}")
                .exchange()
                .expectStatus().isOk
    }
}