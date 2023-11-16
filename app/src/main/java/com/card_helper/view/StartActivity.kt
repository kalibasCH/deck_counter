package com.card_helper.view

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.card_helper.R
import com.card_helper.databinding.StartActivityBinding
import com.card_helper.view.interfaces.ViewsObserverInterface
import com.card_helper.viewModel.MenuActivityViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

const val HEART_STRING = "HEART"
const val DIAMOND_STRING = "DIAMOND"
const val CLUB_STRING = "CLUB"
const val SPADE_STRING = "SPADE"

object CreatedDeckInStartGame

val createdDeckInStartGame = mutableListOf<Pair<String, Int>>()

object CardsForDelete

var cardsForDelete = mutableListOf<Pair<String, Int>>()

object DeletedCards

var deletedCards = mutableListOf<Pair<String, Int>>()

@AndroidEntryPoint
class StartActivity : AppCompatActivity(), ViewsObserverInterface {

    private lateinit var viewBinding: StartActivityBinding

    private lateinit var viewModel: MenuActivityViewModel

    private lateinit var textViewNumberOfCardsInDeck: TextView

    private lateinit var buttonHeart: FrameLayout
    private lateinit var buttonSpade: FrameLayout
    private lateinit var buttonClub: FrameLayout
    private lateinit var buttonDiamond: FrameLayout

    private lateinit var highCardHearts: ImageView
    private lateinit var highCardSpades: ImageView
    private lateinit var highCardClubs: ImageView
    private lateinit var highCardDiamonds: ImageView

    private lateinit var suitToCardMapping: Map<String, ImageView>
    private lateinit var buttonToSuitMapping: Map<FrameLayout, String>

    private lateinit var coordinatorLayout: CoordinatorLayout

    private val fragmentManager: FragmentManager = supportFragmentManager
    private val fragmentDeckHolder = FragmentDeckHolder.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = StartActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initializeViews()
        initializeClickListeners()

        observeViewModel()

        deletedCards.clear()
        cardsForDelete.clear()
        createdDeckInStartGame.clear()

        val numberOfCardsToCreate = intent.getIntExtra(Keys.INTENT_KEY_NUMBER_CARDS, 52)
        viewModel.createDeck(numberOfCardsToCreate)
    }

    override fun initializeViews() {
        viewModel = ViewModelProvider(this)[MenuActivityViewModel::class.java]

        textViewNumberOfCardsInDeck = viewBinding.textNumberCardsInDeck
        buttonHeart = viewBinding.buttonHeart
        buttonSpade = viewBinding.buttonSpade
        buttonClub = viewBinding.buttonClub
        buttonDiamond = viewBinding.buttonDiamond
        highCardHearts = viewBinding.highCardHearts
        highCardSpades = viewBinding.highCardSpades
        highCardClubs = viewBinding.highCardClubs
        highCardDiamonds = viewBinding.highCardDiamonds
        coordinatorLayout = viewBinding.coordinatorLayout

        suitToCardMapping = mapOf(
            HEART_STRING to highCardHearts,
            SPADE_STRING to highCardSpades,
            CLUB_STRING to highCardClubs,
            DIAMOND_STRING to highCardDiamonds
        )

        buttonToSuitMapping = mapOf(
            buttonHeart to HEART_STRING,
            buttonSpade to SPADE_STRING,
            buttonClub to CLUB_STRING,
            buttonDiamond to DIAMOND_STRING
        )
    }

    override fun initializeClickListeners() {
        setOnClickForInflateSuitInFragment()
    }

    private fun setOnClickForInflateSuitInFragment() {
        buttonToSuitMapping.forEach { (button, suit) ->
            button.setOnClickListener {
                if (deletedCards.containsAll(createdDeckInStartGame.filter { it.first == suit })) {
                    Toast.makeText(this, R.string.allCardsIsDeleted, Toast.LENGTH_SHORT).show()
                } else {
                    hideViews(buttonSpade, buttonDiamond, buttonClub, buttonHeart)
                    replaceFragment(suit)
                }
            }
        }
    }

    override fun observeViewModel() {
        viewModel.numberOfCardsInDeck.observe(this) { numberOfCardsInDeck ->
            textViewNumberOfCardsInDeck.text = numberOfCardsInDeck.toString()
        }
        viewModel.cardsWithMaxStrong.observe(this) { cards ->
            viewStrongerCards(cards)
        }
        viewModel.createdDeckInStartGame.observe(this) { createdDeck ->
            createdDeck.forEach { card ->
                createdDeckInStartGame.add(card)
            }
        }
    }

    private fun viewStrongerCards(cards: List<Pair<String, Int?>>) {
        cards.forEach { card ->
            val suit = card.first
            if (card.second != null) {
                val strong = card.second
                suitToCardMapping[suit]?.setImageResource(
                    resources.getIdentifier(
                        "${suit.lowercase()}s_${strong}",
                        "drawable",
                        packageName
                    )
                )
            } else {
                suitToCardMapping[suit]?.setImageResource(
                    resources.getIdentifier("none_40_white", "drawable", packageName)
                )
            }
        }
    }

    private fun replaceFragment(suit: String) {
        val bundle = Bundle()
        bundle.putString(Keys.BUNDLE_KEY_FOR_SUIT_NAME, suit)
        fragmentDeckHolder.arguments = bundle
        fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in, 0)
            .replace(viewBinding.fragmentHolder.id, fragmentDeckHolder)
            .commit()
    }

    private fun hideViews(vararg views: View) {
        views.forEach { it.visibility = View.GONE }
    }

    private fun showViews(vararg views: View) {
        views.forEach { it.visibility = View.VISIBLE }
    }

    private fun showSnackBar() {
        val snackBar = Snackbar.make(
            coordinatorLayout,
            R.string.cardsIsDeleted,
            Snackbar.LENGTH_LONG
        )
            .setAction(R.string.cancelDeletion) {
                viewModel.addCardsToDeck(cardsForDelete)
                val toast =
                    Toast.makeText(this, R.string.previousDeletionCancelled, Toast.LENGTH_SHORT)
                toast.show()
                deletedCards.removeAll(cardsForDelete)
                cardsForDelete.clear()
            }
        snackBar.show()
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(viewBinding.fragmentHolder.id)
        if (currentFragment != null) {
            showViews(buttonClub, buttonDiamond, buttonSpade, buttonHeart)
            supportFragmentManager
                .beginTransaction()
                .remove(currentFragment)
                .commit()
            viewModel.defineCardsWithMaxStrong()
            if (cardsForDelete.isNotEmpty() && deletedCards.containsAll(cardsForDelete)) {
                showSnackBar()
            }
        } else {
            super.onBackPressed()
        }
    }
}