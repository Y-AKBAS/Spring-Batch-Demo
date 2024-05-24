package com.yakbas.batch.mapper

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

private typealias SimpleClassName = String

object MapperInfoCache {

    private val cache = ConcurrentHashMap<SimpleClassName, EntityMapperInfo<*>>()

    fun <T : Any> getForEntity(entityClass: KClass<T>): EntityMapperInfo<T> {
        return cache.getOrPut(entityClass.simpleName) {
            val anno =
                requireNotNull(entityClass.findAnnotation<Table>()) { "${entityClass.simpleName} is not a table!" }
            val tableName = anno.value.ifEmpty { anno.name }
            require(tableName.isNotBlank()) { "Found no table name for ${entityClass.simpleName}" }
            val idProperty = requireNotNull(
                entityClass.memberProperties.find { prop ->
                    prop.javaField!!.annotations.find { it is Id } != null
                }
            )
            val rowMapper = EntityMapper(entityClass)
            EntityMapperInfo(tableName, idProperty.name, entityClass, rowMapper)
        } as EntityMapperInfo<T>
    }
}
