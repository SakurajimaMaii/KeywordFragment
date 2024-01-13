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

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.ave.vastgui.tools.network.request.Request2
import com.ave.vastgui.tools.network.request.getApi
import com.ave.vastgui.tools.viewbinding.viewBinding
import com.searchbox.vastgui.KeywordFragment
import com.searchbox.vastgui.app.databinding.ActivityMainBinding
import com.searchbox.vastgui.app.net.ApiRequestBuilder
import com.searchbox.vastgui.app.net.ApiService
import com.searchbox.vastgui.entity.Keyword
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(R.layout.activity_main),
    Toolbar.OnMenuItemClickListener {

    private val mBinding by viewBinding(ActivityMainBinding::bind)
    private val mLocKeywordFragment: LocKeywordFragment by lazy {
        LocKeywordFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.toolbar.apply {
            title = "SearchDialog"
            setSupportActionBar(this)
            setOnMenuItemClickListener(this@MainActivity)
        }

        mLocKeywordFragment.setOnSearchClickListener { keyword ->
            mBinding.searchInfo.text = keyword
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // 加载菜单文件
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        // 点击搜索按钮
        if (item.itemId == R.id.action_search) {
            mLocKeywordFragment.showFragment(supportFragmentManager, KeywordFragment.TAG)
        }
        return true
    }
}
