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

package com.searchbox.vastgui.anim

import android.animation.Animator
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import kotlin.math.hypot

/**
 * @since 1.1.1
 */
class CircularRevealAnim {

    private var mListener: AnimListener? = null
    private var mRange: Pair<Float, Float> = Pair(0f, 0f)
    private lateinit var mAnimator: Animator

    /**
     * @since 1.1.1
     */
    private fun actionOtherVisible(isShow: Boolean, triggerView: View, animView: View) {
        val tvLocation = IntArray(2).apply {
            triggerView.getLocationInWindow(this)
        }
        val tvX = tvLocation[0] + triggerView.width / 2f
        val tvY = tvLocation[1] + triggerView.height / 2f

        val avLocation = IntArray(2).apply {
            animView.getLocationInWindow(this)
        }
        val avX = avLocation[0] + animView.width / 2f
        val avY = avLocation[1] + animView.height / 2f

        val rippleW = if (tvX < avX) animView.width - tvX else tvX - avLocation[0]
        val rippleH = if (tvY < avY) animView.height - tvY else tvY - avLocation[1]
        val radius = hypot(rippleW, rippleH)

        mRange = if (isShow) Pair(0f, radius) else Pair(radius, 0f)

        animView.visibility = View.VISIBLE
        mAnimator = ViewAnimationUtils
            .createCircularReveal(animView, tvX.toInt(), tvY.toInt(), mRange.first, mRange.second)
            .apply {
                interpolator = DecelerateInterpolator()
                duration = DURATION
                doOnEnd {
                    if (isShow) {
                        animView.visibility = View.VISIBLE
                        mListener?.onShowAnimationEnd()
                    } else {
                        animView.visibility = View.GONE
                        mListener?.onHideAnimationEnd()
                    }
                }
            }
        mAnimator.start()
    }

    /**
     * 显示动画。
     *
     * @since 1.1.1
     */
    fun show(triggerView: View, showView: View) {
        actionOtherVisible(true, triggerView, showView)
    }

    /**
     * 隐藏动画。
     *
     * @since 1.1.1
     */
    fun hide(triggerView: View, hideView: View) {
        actionOtherVisible(false, triggerView, hideView)
    }

    /**
     * @since 1.1.1
     */
    fun setAnimListener(listener: AnimListener?) {
        mListener = listener
    }

    interface AnimListener {
        /**
         * 搜索框动画隐藏完毕时回调。
         *
         * @since 1.1.1
         */
        fun onHideAnimationEnd()

        /**
         * 搜索框动画显示完毕时调用。
         *
         * @since 1.1.1
         */
        fun onShowAnimationEnd()
    }

    companion object {
        const val DURATION: Long = 300L
    }
}