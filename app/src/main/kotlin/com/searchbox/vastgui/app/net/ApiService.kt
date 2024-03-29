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

package com.searchbox.vastgui.app.net

import com.searchbox.vastgui.app.model.ApiDestination
import retrofit2.http.GET
import retrofit2.http.Query

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/12/26

interface ApiService {
    /**
     * 通过接口获取地理信息
     */
    @GET("/v2/city/lookup")
    suspend fun getGeoCityLookup(
        @Query("location") location: String,
        @Query("key") key: String
    ): ApiDestination
}