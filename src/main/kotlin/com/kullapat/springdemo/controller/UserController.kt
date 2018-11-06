package com.kullapat.springdemo.controller

import com.kullapat.springdemo.controller.resource.UserResource
import com.kullapat.springdemo.model.User
import com.kullapat.springdemo.service.UserService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    @PostMapping()
    fun createUser(@RequestBody userResource: UserResource): Mono<User> {
        return userService.createUser(userResource.toUser())
    }

    @GetMapping
    fun getUsers(): Flux<User> {
        return userService.getUsers()
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): Mono<User> {
        return userService.getUserById(id)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: String, @RequestBody userResource: UserResource): Mono<User> {
        return userService.updateUser(id, userResource.toUser())
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: String): Mono<Void> {
        return userService.deleteUser(id)
    }
}