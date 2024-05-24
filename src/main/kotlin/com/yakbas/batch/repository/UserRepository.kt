package com.yakbas.batch.repository

import com.yakbas.batch.entity.User
import com.yakbas.batch.mapper.DataFrameResultSetExtractor
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Long>, PagingAndSortingRepository<User, Long> {

    @Query("update ${User.TABLE_NAME} set full_name = NULL where full_name is not null")
    @Modifying
    fun prepareBatch(): Int

    @Query("select * from ${User.TABLE_NAME}", resultSetExtractorClass = DataFrameResultSetExtractor::class)
    fun readToDataFrame(): DataFrame<User>
}
