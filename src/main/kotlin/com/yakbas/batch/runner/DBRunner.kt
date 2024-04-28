package com.yakbas.batch.runner

import com.yakbas.batch.entity.User
import com.yakbas.batch.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DBRunner(private val userRepository: UserRepository) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val numOfEntries = 10000L
        val count = userRepository.count()
        val missing = numOfEntries - count
        if (missing > 0) (0 until missing.toInt()).map {
            User(
                id = null,
                firstName = "John $it",
                lastName = "Doe $it",
                email = "john.doe@mail.com",
                fullName = null // set the fullName to null so that batch job can update it.
            )
        }.let { userRepository.saveAll(it) }

        userRepository.prepareBatch()
    }
}
