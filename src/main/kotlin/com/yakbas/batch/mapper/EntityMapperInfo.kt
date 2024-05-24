package com.yakbas.batch.mapper

import kotlin.reflect.KClass

data class EntityMapperInfo<T : Any>(
    val tableName: String,
    val idPropertyName: String,
    val entityClass: KClass<T>,
    val mapper: EntityMapper<T>
)
