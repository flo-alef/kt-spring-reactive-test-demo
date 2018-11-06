package com.kullapat.springdemo.service

import com.kullapat.springdemo.model.User
import com.kullapat.springdemo.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UserService(private val userRepository: UserRepository) {
    fun createUser(user: User): Mono<User> {
        return userRepository.save(user)
    }

    fun getUsers(): Flux<User> {
        return userRepository.findAll()
    }

    fun getUserById(id: String): Mono<User> {
        return userRepository.findById(id)
    }

    fun updateUser(id: String, user: User): Mono<User> {
        return userRepository.findById(id).flatMap {
            val updatedUser = it.copy(id = id, firstName = user.firstName, lastName = user.lastName)
            userRepository.save(updatedUser)
        }
    }

    fun deleteUser(id: String): Mono<Void> {
        return userRepository.findById(id).flatMap {
            userRepository.delete(it)
        }
    }
}
