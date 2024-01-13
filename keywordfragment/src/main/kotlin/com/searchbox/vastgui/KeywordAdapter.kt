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

package com.searchbox.vastgui

import android.annotation.SuppressLint
import android.content.Context
import com.ave.vastgui.adapter.VastBindAdapter
import com.ave.vastgui.adapter.base.ItemWrapper
import com.searchbox.vastgui.entity.Keyword

/**
 * [KeywordAdapter]
 *
 * @since 1.1.1
 */
abstract class KeywordAdapter(context: Context) :
    VastBindAdapter<Keyword>(context, BR.keyword), OnKeyWordClickListener {

    /**
     * 向展示列表中插入历史关键字。
     *
     * @since 1.1.1
     */
    fun addHistory(keyword: Keyword) {
        val index = itemCount
        mDataSource.add(index, ItemWrapper(keyword, R.layout.item_history_keyword, clickListener = { _, _, wrapper ->
            onHistoryClick(wrapper.getData().keyword)
        }).apply {
            addOnItemChildClickListener(R.id.ihDelete) { _, _, wrapper ->
                onHistoryDelete(wrapper.getData())
            }
        })
        notifyItemChanged(index)
    }

    /**
     * 向展示列表中删除历史关键字。
     *
     * @since 1.1.1
     */
    fun removeHistory(keyword: Keyword): Boolean {
        val index = mDataSource.indexOfFirst { it.getData().keyword == keyword.keyword }
        if (-1 == index) return false
        notifyItemChanged(index)
        mDataSource.removeAt(index)
        return true
    }

    /**
     * 向列表中插入新的关键字。
     *
     * @since 1.1.1
     */
    fun addCurrent(keyword: Keyword) {
        val index = itemCount
        mDataSource.add(
            index,
            ItemWrapper(keyword, R.layout.item_current_keyword, clickListener = { _, _, wrapper ->
                onCurrentClick(wrapper.getData())
            })
        )
        notifyItemChanged(index)
    }

    /**
     * @since 1.1.1
     */
    @SuppressLint("NotifyDataSetChanged")
    fun clearAll() {
        mDataSource.clear()
        notifyDataSetChanged()
    }

}