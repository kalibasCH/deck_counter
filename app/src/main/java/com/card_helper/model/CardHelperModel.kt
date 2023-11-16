package com.card_helper.model

import androidx.lifecycle.MutableLiveData
import com.card_helper.model.interfaces.DeckRepositoryInterface

class CardHelperModel(private val deckRepositoryInterface: DeckRepositoryInterface) {

    val deckInStartGame: MutableLiveData<List<Pair<String, Int>>> = MutableLiveData()
    val numberOfCardsInDeck: MutableLiveData<Int> = MutableLiveData()
    val cardsWithMaxStrong: MutableLiveData<List<Pair<String, Int?>>> = MutableLiveData()
    val cardsBySuit: MutableLiveData<List<Pair<String, Int>>> = MutableLiveData()

    fun createDeck(numberOfCardsToCreate: Int) {
        deckInStartGame.postValue(deckRepositoryInterface.createDeck(numberOfCardsToCreate))
        defineCardsWithMaxStrong()
        updateNumberOfCardsInDeck()
    }

    fun deleteCards(cards: List<Pair<String, Int>>) {
        deckRepositoryInterface.deleteCards(cards)
        updateCardsBySuit(cards[0].first)
        updateNumberOfCardsInDeck()
    }

    fun fetchCardsToWorkBySuit(suit: String) {
        updateCardsBySuit(suit)
    }

    fun defineCardsWithMaxStrong() {
        cardsWithMaxStrong.postValue(deckRepositoryInterface.getCardsWithMaxStrong())
        updateNumberOfCardsInDeck()
    }

    fun addCards(cards: List<Pair<String, Int>>) {
        deckRepositoryInterface.addCards(cards)
        defineCardsWithMaxStrong()
    }

    private fun updateCardsBySuit(suitName: String) {
        cardsBySuit.postValue(deckRepositoryInterface.fetchCardsToWorkBySuit(suitName))
    }

    private fun updateNumberOfCardsInDeck() {
        val numCards = deckRepositoryInterface.getNumberOfCardsInDeck()
        numberOfCardsInDeck.postValue(numCards)
    }
}
