package com.card_helper.view

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.card_helper.R
import com.card_helper.view.anim.Animation
import com.card_helper.view.interfaces.MainViewsInterface
import com.google.android.material.switchmaterial.SwitchMaterial

object AppStart

var isAppStart: Boolean? = null

object Keys {
    const val BUNDLE_KEY_FOR_SUIT_NAME = "key_for_suit_name"
    const val INTENT_KEY_NUMBER_CARDS = "key_number_cards_to_create"
}

class MainMenuActivity : AppCompatActivity(), MainViewsInterface {
    private lateinit var linearLayout: LinearLayout
    private lateinit var buttonStart: Button
    private lateinit var switchDeck: SwitchMaterial

    private lateinit var suitsImageHolders: List<ImageView>

    private var numberOfCardsInDeckForCreate = 52

    private val animation by lazy { Animation() }

    private var animationInMainMenu: AnimatorSet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_activity)
        initializeViews()
        initializeClickListeners()
        animationInMainMenu = animation.animateCardsInMainMenu(
            suitsImageHolders[0],
            suitsImageHolders[1],
            suitsImageHolders[2],
            suitsImageHolders[3]
        )
    }

    override fun onDestroy() {
        animationInMainMenu!!.cancel()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        moveTaskToBack(true)
        return true
    }

    override fun initializeViews() {
        linearLayout = findViewById(R.id.linear_layout_id)
        buttonStart = findViewById(R.id.button_start)
        switchDeck = findViewById(R.id.switch_deck)

        suitsImageHolders = listOf(
            findViewById(R.id.image_suit_blue_1),
            findViewById(R.id.image_suit_red_1),
            findViewById(R.id.image_suit_blue_2),
            findViewById(R.id.image_suit_red_2)
        )
    }

    override fun initializeClickListeners() {
        buttonStart.setOnClickListener {
            isAppStart = true
            val intent = Intent(this@MainMenuActivity, StartActivity::class.java)
            intent.putExtra(Keys.INTENT_KEY_NUMBER_CARDS, numberOfCardsInDeckForCreate)
            startActivity(intent)
        }
        switchDeck.setOnCheckedChangeListener { _, isChecked ->
            numberOfCardsInDeckForCreate = if (isChecked) {
                36
            } else {
                52
            }
        }
    }
}