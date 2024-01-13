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

import com.searchbox.vastgui.entity.Keyword

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2024/1/8 12:37

/**
 * [KeywordAdapter] 的回调函数。
 */
sealed interface OnKeyWordClickListener {
    /**
     * 点击历史关键字的回调函数。
     *
     * @param keyword 对应 [Keyword.keyword]
     * @since 1.1.1
     */
    fun onHistoryClick(keyword: String)

    /**
     * 点击历史关键字删除按钮的回调函数。
     *
     * @param keyword 将要被删除的历史关键字。
     * @since 1.1.1
     */
    fun onHistoryDelete(keyword: Keyword)

    /**
     * 点击关键字回调函数。
     *
     * @param keyword 将要跳转的目的地。
     * @since 1.1.1
     */
    fun onCurrentClick(keyword: Keyword)
}