package com.yakbas.batch.mapper

import com.yakbas.batch.entity.User


private val dataSourceProperties = DataSourceProperties(
    url = "jdbc:mysql://localhost:3306/customer_data",
    driverClassName = "com.mysql.cj.jdbc.Driver",
    userName = "root",
    password = "root"
)

private val entityDao = EntityDao(dataSourceProperties)

fun main() {

    val user = entityDao.findByIdOrNull<User, Int>(id = 100)
    val newUser = user!!.copy(firstName = "Abuzer", lastName = "Kadayif")
    val isUpdated = entityDao.updateOne(newUser)
    val updatedUser = entityDao.findByIdOrNull<User, Int>(id = 100)
    println(updatedUser)
    //val users = entityDao.findAll<User>()
    //val partialUsers = entityDao.find<User>("Select * from users where id > 5000", emptyList<Any>())

}