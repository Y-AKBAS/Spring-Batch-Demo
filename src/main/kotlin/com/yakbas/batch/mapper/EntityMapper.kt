package com.yakbas.batch.mapper

import com.yakbas.batch.entity.User
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.db.MySql
import org.jetbrains.kotlinx.dataframe.io.readResultSet
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.JdbcUtils
import java.sql.ResultSet
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

class EntityMapper<T : Any>(private val entityClass: KClass<T>) : RowMapper<T> {

    private val constructor = requireNotNull(entityClass.primaryConstructor) {
        "${entityClass.simpleName} has no primary constructor!"
    }
    private val params = createParams(constructor)
    private val columnAndParamNames = createColumnAndParamNames()
    private val columnIndexes = ConcurrentHashMap<Int, Int>()

    override fun mapRow(rs: ResultSet, rowNum: Int): T {
        check(params.size == rs.metaData.columnCount) { "Inconsistent ResultSet and Entity. Prop size: ${params.size}, Column count: ${rs.metaData.columnCount}" }
        val result = Array<Any?>(params.size) { null }
        params.entries.forEachIndexed { i, (name, param) ->
            val index = columnIndexes.getOrPut(i) { rs.findColumn(name) }
            result[i] = JdbcUtils.getResultSetValue(rs, index, param.type.jvmErasure.java)
        }

        return constructor.call(*result)
    }

    fun toParams(instance: T): List<Any?> {
        val memberProperties = entityClass.memberProperties
        return memberProperties.fold(LinkedList<Any?>()) { list, prop ->
            list.also { it.add(prop.getter.call(instance)) }
        }
    }

    fun toParamMap(instance: T): Map<String, Any?> {
        return instance::class.memberProperties.fold(mutableMapOf()) { map, prop ->
            val value = prop.getter.call(instance)
            map[prop.name] = value
            map
        }
    }

    fun getSnakeCaseNames() = this.columnAndParamNames

    private fun createParams(constructor: KFunction<T>): LinkedHashMap<String, KParameter> {
        val valueParams = constructor.valueParameters
        return valueParams.associateByTo(LinkedHashMap(valueParams.size)) {
            JdbcUtils.convertPropertyNameToUnderscoreName(it.name!!)
        }
    }

    private fun createColumnAndParamNames(): Map<String, String> {
        return this.entityClass.memberProperties.fold(mutableMapOf()) { map, prop ->
            map.also { it[prop.name] = JdbcUtils.convertPropertyNameToUnderscoreName(prop.name) }
        }
    }
}


class DataFrameResultSetExtractor : ResultSetExtractor<DataFrame<User>> {
    override fun extractData(rs: ResultSet): DataFrame<User> {
        return DataFrame.readResultSet(rs, MySql) as DataFrame<User>
    }
}
