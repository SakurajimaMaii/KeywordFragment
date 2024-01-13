/*
 * Copyright 2024 VastGui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.searchbox.vastgui.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.searchbox.vastgui.entity.Keyword

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/12/21

/**
 * @since 1.1.1
 */
@Dao
interface KeywordDao {

    /**
     * @since 1.1.1
     */
    @Query("select * from Keyword order by id desc")
    suspend fun queryAllHistoryKeyword(): List<Keyword>

    /**
     * 根据 [keyword] 查询对应的历史信息。
     *
     * @since 1.1.1
     */
    @Query("select * from Keyword where keyword like '%'||:keyword||'%'")
    suspend fun queryHistoryKeyword(keyword: String): List<Keyword>

    /**
     * @since 1.1.1
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryKeyword(keyword: Keyword)

    /**
     * @since 1.1.1
     */
    @Delete
    suspend fun deleteHistoryKeyword(keyword: Keyword)

    /**
     * @since 1.1.1
     */
    @Query("delete from Keyword")
    suspend fun deleteAllHistoryKeyword()

}