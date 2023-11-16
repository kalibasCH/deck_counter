package com.card_helper.model.interfaces

interface DeckRepositoryInterface {
    fun createDeck(numberOfCardsToCreate: Int): List<Pair<String, Int>>
    fun getNumberOfCardsInDeck(): Int
    fun getCardsWithMaxStrong(): List<Pair<String, Int?>>
    fun fetchCardsToWorkBySuit(suitName: String): List<Pair<String, Int>>
    fun deleteCards(cards: List<Pair<String, Int>>)
    fun addCards(cards: List<Pair<String, Int>>)
}