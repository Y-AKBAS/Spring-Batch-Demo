package com.yakbas.batch.mapper

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

object StatementBuilder {

    fun findById(tableName: String, idPropertyName: String): String {
        return "SELECT * FROM $tableName WHERE $idPropertyName = ?"
    }

    fun findAll(tableName: String) = "SELECT * FROM $tableName"

    fun <T : Any> update(entityClass: KClass<T>) = buildString {
        val mapperInfo = MapperInfoCache.getForEntity(entityClass)
        val snakeCaseNames = mapperInfo.mapper.getSnakeCaseNames()
        append("UPDATE ${mapperInfo.tableName} SET ")
        val properties = entityClass.memberProperties.toList()
        for (i in properties.indices) {
            val prop = properties[i]
            if (prop.name == mapperInfo.idPropertyName) continue
            if (properties.size - 1 != i) {
                append("${snakeCaseNames[prop.name]} = :${prop.name}, ")
            } else {
                append("${snakeCaseNames[prop.name]} = :${prop.name} ")
            }
        }
        append("WHERE ${snakeCaseNames[mapperInfo.idPropertyName]} = :${mapperInfo.idPropertyName}")
    }

}