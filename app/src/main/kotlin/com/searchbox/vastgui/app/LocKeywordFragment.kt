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

package com.searchbox.vastgui.app

import androidx.lifecycle.lifecycleScope
import com.ave.vastgui.tools.network.request.create
import com.searchbox.vastgui.KeywordFragment
import com.searchbox.vastgui.app.net.ApiRequestBuilder
import com.searchbox.vastgui.app.net.ApiService
import com.searchbox.vastgui.entity.Keyword
import kotlinx.coroutines.launch

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2024/1/8 17:58

class LocKeywordFragment : KeywordFragment() {

    private var mOnSearchClickListener: OnSearchClickListener? = null

    override fun onSearchClick(keyword: String) {
        mOnSearchClickListener?.onSearchClick(keyword)
    }

    override fun onKeywordChange(keyword: String) {
        if (mKeywordFragmentAdapter.itemCount > 0) {
            mKeywordRv.removeAllViews()
            mKeywordFragmentAdapter.clearAll()
        }
        lifecycleScope.launch {
            ApiRequestBuilder()
                .create(ApiService::class.java)
                .getGeoCityLookup(keyword, "")
                .takeIf { it.code == "200" }?.location?.forEach {
                    mKeywordFragmentAdapter.addCurrent(Keyword(keyword = it.name))
                }
        }
    }

    fun setOnSearchClickListener(listener: OnSearchClickListener) {
        mOnSearchClickListener = listener
    }

    fun interface OnSearchClickListener {
        fun onSearchClick(keyword: String)
    }

}