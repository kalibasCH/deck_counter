package com.card_helper.repository

import com.card_helper.model.interfaces.DeckRepositoryInterface
import javax.inject.Inject

class DeckRepositoryAccess @Inject constructor(
    private val repository: Repository
): DeckRepositoryInterface {

    override fun createDeck(numberOfCardsToCreate: Int): List<Pair<String, Int>> {
        return repository.createDeck(numberOfCardsToCreate)
    }

    override fun getNumberOfCardsInDeck(): Int {
        return repository.getNumberOfCardsInDeck()
    }

    override fun getCardsWithMaxStrong(): List<Pair<String, Int?>> {
        return repository.getCardsWithMaxStrong()
    }

    override fun fetchCardsToWorkBySuit(suitName: String): List<Pair<String, Int>> {
        return repository.getCardsToWorkBySuit(suitName)
    }

    override fun deleteCards(cards: List<Pair<String, Int>>) {
        repository.deleteCards(cards)
    }

    override fun addCards(cards: List<Pair<String, Int>>) {
        repository.addCards(cards)
    }
}
