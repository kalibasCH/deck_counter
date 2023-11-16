package com.card_helper.view.anim

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.core.animation.addListener
import at.wirecube.additiveanimations.additive_animator.AdditiveAnimator
import com.card_helper.R

class Animation {

    fun animateCardsInMainMenu(
        firstImageHolder: ImageView,
        secondImageHolder: ImageView,
        thirdImageHolder: ImageView,
        fourthImageHolder: ImageView
    ) : AnimatorSet {

        val startDelay = 800L
        val duration = 800L

        val suitsTags = listOf(
            R.drawable.suit_blue_main,
            R.drawable.suit_red_main
        )
        val cardsTags = listOf(
            R.drawable.spades_14,
            R.drawable.hearts_14,
            R.drawable.clubs_14,
            R.drawable.diamonds_14
        )
        firstImageHolder.tag = suitsTags.random()
        secondImageHolder.tag = suitsTags.random()
        thirdImageHolder.tag = suitsTags.random()
        fourthImageHolder.tag = suitsTags.random()

        fun createTranslateAnimator(imageHolder: ImageView, direction: Float): ObjectAnimator {
            return ObjectAnimator.ofFloat(imageHolder, "translationX", 0f, direction * 270f, 0f)
                .apply {
                    this.duration = duration
                    this.startDelay = startDelay
                    interpolator = LinearInterpolator()
                }
        }

        fun createFlipAnimator(imageHolder: ImageView, suitImageResource: Int, cardImageResource: Int): AnimatorSet {

            val firstHalf = ObjectAnimator.ofFloat(imageHolder, "rotationY", 0f, 90f)
                .apply {
                    this.duration = duration / 2
                    interpolator = AccelerateInterpolator()
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationStart(animation)
                            if (imageHolder.tag in suitsTags) {
                                imageHolder.setImageResource(cardImageResource)
                                imageHolder.tag = cardImageResource
                            } else if (imageHolder.tag in cardsTags) {
                                imageHolder.setImageResource(suitImageResource)
                                imageHolder.tag = suitImageResource
                            }
                        }
                    })
                }

            val secondHalf = ObjectAnimator.ofFloat(imageHolder, "rotationY", -90f, 0f)
                .apply {
                    this.duration = duration / 2
                    interpolator = DecelerateInterpolator()
                }

            return AnimatorSet().apply {
                playSequentially(firstHalf, secondHalf)
            }
        }

        val translateImagesRight1 = createTranslateAnimator(firstImageHolder, 1.0f)
        val translateImagesLeft1 = createTranslateAnimator(secondImageHolder, -1.0f)
        val translateImagesRight2 = createTranslateAnimator(thirdImageHolder, 1.0f)
        val translateImagesLeft2 = createTranslateAnimator(fourthImageHolder, -1.0f)

        val translateImagesRight3 = createTranslateAnimator(secondImageHolder, 1.0f).apply {
            this.startDelay = 50L
        }
        val translateImagesLeft3 = createTranslateAnimator(thirdImageHolder, -1.0f).apply {
            this.startDelay = 50L
        }

        val flipCard1 = createFlipAnimator(firstImageHolder, R.drawable.suit_blue_main, R.drawable.spades_14)
        val flipCard2 = createFlipAnimator(secondImageHolder, R.drawable.suit_red_main, R.drawable.hearts_14)
        val flipCard3 = createFlipAnimator(thirdImageHolder, R.drawable.suit_blue_main, R.drawable.clubs_14)
        val flipCard4 = createFlipAnimator(fourthImageHolder, R.drawable.suit_red_main, R.drawable.diamonds_14)

        return AnimatorSet().apply {
            play(translateImagesRight1).with(translateImagesLeft1).with(translateImagesRight2)
                .with(translateImagesLeft2)
            play(translateImagesRight3).with(translateImagesLeft3).after(translateImagesLeft2)
            play(flipCard1).with(flipCard2).with(flipCard3).with(flipCard4).after(translateImagesLeft3)
                .after(translateImagesLeft3)
            start()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    start()
                }
            })
        }
    }


    fun animateTranslationYFrom0(view: View) {
        view.alpha = 0f
        view.translationY = 500f
        AdditiveAnimator()
            .setDuration(600)
            .targets(view).translationY(0f)
            .alpha(1f)
            .start()
    }

    fun animateShiverCard(view: View) {
        val animatorForCards = ObjectAnimator.ofFloat(
            view,
            "rotation",
            5f,
            -5f,
        )
        animatorForCards.interpolator = LinearInterpolator()
        animatorForCards.duration = 500
        animatorForCards.repeatCount = ObjectAnimator.INFINITE
        animatorForCards.repeatMode = ObjectAnimator.REVERSE

        animatorForCards.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)
                view.rotation = 0f
            }
        })
        animatorForCards.start()
        view.tag = animatorForCards
    }
}
