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

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.searchbox.vastgui.anim.CircularRevealAnim
import com.searchbox.vastgui.anim.CircularRevealAnim.AnimListener
import com.searchbox.vastgui.databinding.FragmentKeywordBinding
import com.searchbox.vastgui.db.KeywordDB
import com.searchbox.vastgui.db.KeywordDao
import com.searchbox.vastgui.entity.Keyword
import com.searchbox.vastgui.utils.KeyBoardUtils
import kotlinx.coroutines.launch

/**
 * [KeywordFragment]
 *
 * @since 1.1.1
 */
open class KeywordFragment : DialogFragment(R.layout.fragment_keyword),
    DialogInterface.OnKeyListener,
    ViewTreeObserver.OnPreDrawListener,
    AnimListener {

    private lateinit var mSearchKeyword: TextInputEditText
    private lateinit var mSearchIcon: ShapeableImageView
    private lateinit var mCircularRevealAnim: CircularRevealAnim
    protected lateinit var mKeywordRv: RecyclerView
    protected var mBinding: FragmentKeywordBinding? = null
    protected lateinit var mKeywordFragmentAdapter: KeywordFragmentAdapter
    protected lateinit var mKeywordDao: KeywordDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogStyle)
        mKeywordDao = Room
            .databaseBuilder(requireContext(), KeywordDB::class.java, "KeywordDao")
            .build()
            .keywordDao()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentKeywordBinding.bind(view)
        initView(mBinding!!)
        initData()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        // 清空输入框中可能保存的信息
        // https://blog.csdn.net/Jason_HD/article/details/82688228
        if (mSearchKeyword.text?.isEmpty() == false) {
            mSearchKeyword.setText("")
        }
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        // KeywordFragment 的宽
        val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
        requireDialog().window?.apply {
            setLayout(width, WindowManager.LayoutParams.MATCH_PARENT)
            setGravity(Gravity.TOP)
            // 取消过渡动画 , 使 KeywordFragment 的出现更加平滑
            setWindowAnimations(R.style.DialogEmptyAnimation)
        }

        mSearchKeyword.doAfterTextChanged { editable ->
            val keyword = editable?.toString() ?: return@doAfterTextChanged
            if (mKeywordFragmentAdapter.itemCount > 0) {
                mKeywordRv.removeAllViews()
                mKeywordFragmentAdapter.clearAll()
            }
            if (keyword.isEmpty()) {
                lifecycleScope.launch {
                    mKeywordDao.queryAllHistoryKeyword().forEach {
                        mKeywordFragmentAdapter.addHistory(it)
                    }
                }
            } else {
                onKeywordChange(keyword)
            }
        }
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }

    /**
     * 监听键盘按键。
     *
     * @since 1.1.1
     */
    final override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            hideAnim()
        } else if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
            search()
        }
        return false
    }

    /**
     * 监听搜索键绘制时。
     *
     * @since 1.1.1
     */
    final override fun onPreDraw(): Boolean {
        mSearchIcon.viewTreeObserver.removeOnPreDrawListener(this)
        mCircularRevealAnim.show(mSearchIcon, requireView())
        return true
    }

    final override fun onHideAnimationEnd() {
        dismiss()
    }

    final override fun onShowAnimationEnd() {
        if (isVisible) {
            KeyBoardUtils.openKeyboard(requireContext(), mSearchKeyword)
        }
    }

    /**
     * 显示Fragment，防止多次打开导致崩溃。
     *
     * @since 1.1.1
     */
    fun showFragment(fragmentManager: FragmentManager, tag: String?) {
        if (!this.isAdded) {
            this.show(fragmentManager, tag)
        }
    }

    /**
     * 点击搜索按钮 [mSearchIcon] 时触发。
     *
     * @param keyword 搜索的关键词。
     * @since 1.1.1
     */
    protected open fun onSearchClick(keyword: String) {

    }

    /**
     * 当搜索框 [mSearchKeyword] 内容改变时触发。默认
     * 实现是通过 [keyword] 从数据库中查询历史数据。
     *
     * @param keyword 搜索的关键词。
     * @since 1.1.1
     */
    protected open fun onKeywordChange(keyword: String) {
        lifecycleScope.launch {
            mKeywordDao.queryAllHistoryKeyword().forEach {
                mKeywordFragmentAdapter.addHistory(it)
            }
        }
    }

    /**
     * @since 1.1.1
     */
    private fun initView(binding: FragmentKeywordBinding) {
        mSearchKeyword = binding.searchKeyword
        mKeywordRv = binding.keywordRv
        // 实例化动画效果并监听
        CircularRevealAnim().apply {
            mCircularRevealAnim = this
            mCircularRevealAnim.setAnimListener(this@KeywordFragment)
        }
        // 键盘按键监听
        mSearchIcon = binding.searchIcon
        mSearchIcon.apply {
            viewTreeObserver.addOnPreDrawListener(this@KeywordFragment)
            setOnClickListener {
                search()
            }
        }
        // 键盘按键监听
        requireDialog().setOnKeyListener(this)
        // 初始化 mKeywordRv
        mKeywordFragmentAdapter = KeywordFragmentAdapter(requireContext())
        mKeywordRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mKeywordFragmentAdapter
            layoutAnimation = LayoutAnimationController(
                AnimationUtils.loadAnimation(requireContext(), R.anim.rv_anim)
            ).apply {
                order = LayoutAnimationController.ORDER_NORMAL
                delay = 0.2f
            }
        }
        binding.isEmpty = mKeywordFragmentAdapter.itemCount > 0
        binding.searchBack.setOnClickListener {
            hideAnim()
        }
        binding.searchOutside.setOnClickListener {
            hideAnim()
        }
        binding.keywordClean.setOnClickListener {
            lifecycleScope.launch {
                if (mKeywordFragmentAdapter.itemCount > 0) {
                    mKeywordRv.removeAllViews()
                    mKeywordFragmentAdapter.clearAll()
                }
                mKeywordDao.deleteAllHistoryKeyword()
            }
        }
    }

    /**
     * 初始化，从数据库中加载历史数据。
     *
     * @since 1.1.1
     */
    private fun initData() {
        if (mKeywordFragmentAdapter.itemCount > 0) {
            mKeywordRv.removeAllViews()
            mKeywordFragmentAdapter.clearAll()
        }
        lifecycleScope.launch {
            mKeywordDao.queryAllHistoryKeyword().forEach {
                mKeywordFragmentAdapter.addHistory(it)
            }
        }
    }

    /**
     * [KeywordFragment] 隐藏动画。
     *
     * @since 1.1.1
     */
    private fun hideAnim() {
        mCircularRevealAnim.hide(mSearchIcon, requireView())
        KeyBoardUtils.closeKeyboard(requireContext(), mSearchKeyword)
    }

    /**
     * 点击搜索按钮。
     *
     * @since 1.1.1
     */
    private fun search() {
        val searchKey = mSearchKeyword.text ?: return
        lifecycleScope.launch {
            mKeywordDao.insertHistoryKeyword(Keyword(keyword = searchKey.toString()))
            onSearchClick(searchKey.toString())
            hideAnim()
        }
    }

    /**
     * @since 1.1.1
     */
    protected inner class KeywordFragmentAdapter(context: Context) : KeywordAdapter(context) {
        override fun onHistoryClick(keyword: String) {
            onSearchClick(keyword)
            hideAnim()
        }

        override fun onHistoryDelete(keyword: Keyword) {
            lifecycleScope.launch {
                mKeywordDao.deleteHistoryKeyword(keyword)
                mKeywordFragmentAdapter.removeHistory(keyword)
            }
        }

        override fun onCurrentClick(keyword: Keyword) {
            lifecycleScope.launch {
                mKeywordDao.insertHistoryKeyword(keyword)
                onSearchClick(keyword.keyword)
                hideAnim()
            }
        }
    }

    companion object {
        @JvmField
        val TAG: String = this::class.java.simpleName
    }
}