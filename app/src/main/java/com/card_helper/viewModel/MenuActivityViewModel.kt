package com.card_helper.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.card_helper.model.CardHelperModel
import com.card_helper.repository.DeckRepositoryAccess
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MenuActivityViewModel @Inject constructor(
    repository: DeckRepositoryAccess
) : ViewModel() {
    private val cardHelperModel: CardHelperModel = CardHelperModel(repository)

    val createdDeckInStartGame: LiveData<List<Pair<String, Int>>> = cardHelperModel.deckInStartGame

    val numberOfCardsInDeck: LiveData<Int> = cardHelperModel.numberOfCardsInDeck

    val cardsWithMaxStrong: LiveData<List<Pair<String, Int?>>> = cardHelperModel.cardsWithMaxStrong

    fun createDeck(numberOfCardsToCreate: Int) {
        cardHelperModel.createDeck(numberOfCardsToCreate)
    }

    fun defineCardsWithMaxStrong() {
        cardHelperModel.defineCardsWithMaxStrong()
    }

    fun addCardsToDeck(cards: List<Pair<String, Int>>) {
        cardHelperModel.addCards(cards)
    }
}