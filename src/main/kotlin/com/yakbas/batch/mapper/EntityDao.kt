package com.yakbas.batch.mapper

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.jdbc.core.simple.JdbcClient
import kotlin.reflect.KClass

class EntityDao(private val properties: DataSourceProperties) {

    val jdbcClient = buildJdbcClient()

    inline fun <reified T : Any, ID> findByIdOrNull(id: ID): T? {
        val extractorInfo = getMapperInfo(T::class)
        val sql = StatementBuilder.findById(extractorInfo.tableName, extractorInfo.idPropertyName)
        val entity = jdbcClient.sql(sql)
            .param(id)
            .query(extractorInfo.mapper)
            .optional()
            .orElse(null)

        return entity
    }

    inline fun <reified T : Any> find(sql: String, params: List<*>): List<T> {
        val extractorInfo = getMapperInfo(T::class)
        val entities = jdbcClient.sql(sql)
            .params(params)
            .query(extractorInfo.mapper)
            .list()

        return entities
    }

    inline fun <reified T : Any> findAll(): List<T> {
        val mapperInfo = getMapperInfo(T::class)
        val sql = StatementBuilder.findAll(mapperInfo.tableName)
        val entities: List<T> = jdbcClient.sql(sql)
            .query(mapperInfo.mapper)
            .list()

        return entities
    }

    inline fun <reified T : Any> updateOne(entity: T): Int {
        val mapperInfo = getMapperInfo(T::class)
        val sql = StatementBuilder.update(T::class)
        val params = mapperInfo.mapper.toParamMap(entity)
        val updateCount = jdbcClient.sql(sql)
            .params(params)
            .update()

        return updateCount
    }

    fun <T : Any> getMapperInfo(entityClass: KClass<T>): EntityMapperInfo<T> {
        return MapperInfoCache.getForEntity(entityClass)
    }

    private fun buildJdbcClient(): JdbcClient {
        val dataSource = DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .url(properties.url)
            .username(properties.userName)
            .password(properties.password)
            .driverClassName(properties.driverClassName)
            .build()

        return JdbcClient.create(dataSource)
    }
}
