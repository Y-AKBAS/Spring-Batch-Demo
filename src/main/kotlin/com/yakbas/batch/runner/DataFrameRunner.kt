package com.yakbas.batch.runner

import com.yakbas.batch.entity.User
import com.yakbas.batch.repository.UserRepository
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.head
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.size
import org.springframework.stereotype.Component
import kotlin.reflect.full.primaryConstructor

@Component
class DataFrameRunner(private val userRepository: UserRepository) {

    fun testDataFrame(){
        val frame = userRepository.readToDataFrame()

        val row = frame.first { it["first_name"] == "John 4" }
        println(row["last_name"])
        println(frame.size())
        println(frame.head())
        val constructor = User::class.primaryConstructor!!
        val list = frame.map { it: DataRow<User> ->
            constructor.call(*(it.values().toTypedArray()))
        }
        println(list.size)
    }
}