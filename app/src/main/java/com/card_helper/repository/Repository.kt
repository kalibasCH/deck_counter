package com.card_helper.repository

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

enum class Suits {
    HEART,
    DIAMOND,
    CLUB,
    SPADE
}

@Singleton
class Deck @Inject constructor() {

    fun create(numberOfCardsToCreate: Int) {
        clearDeck()
        val startStrong = when (numberOfCardsToCreate) {
            52 -> 2
            36 -> 6
            else -> throw IllegalArgumentException("Invalid numberOfCardsToCreate: $numberOfCardsToCreate")
        }
        Suits.values().forEach { suit ->
            for (strong in startStrong..14) {
                addCard(Card(suit, strong))
            }
        }
    }

    fun deleteCards(vararg cards: Card) {
        cards.forEach { card ->
            deck[card.suit]?.remove(card)
        }
    }

    fun getDeck(): Map<Suits, List<Card>> {
        return deck.mapValues {
            it.value.toList()
        }
    }

    fun addCards(cards: List<Pair<String, Int>>) {
        cards.forEach { card ->
            addCard(Card(Suits.valueOf(card.first.uppercase()), card.second))
        }
    }

    private val deck: MutableMap<Suits, MutableList<Card>> = mutableMapOf(
        Suits.HEART to mutableListOf(),
        Suits.DIAMOND to mutableListOf(),
        Suits.CLUB to mutableListOf(),
        Suits.SPADE to mutableListOf(),
    )

    private fun clearDeck() {
        for (list in deck.values) {
            list.clear()
        }
    }

    private fun addCard(card: Card) {
        deck[card.suit]?.add(card)
        Log.d("add_card", "${card.suit}-${card.strong}")
    }
}

data class Card(val suit: Suits, val strong: Int)

@Singleton
class Repository @Inject constructor(private val deck: Deck) {

    fun createDeck(numberOfCardsToCreate: Int): List<Pair<String, Int>> {
        deck.create(numberOfCardsToCreate)
        val newDeck = deck.getDeck().flatMap { (suit, cards) ->
            cards.map { Pair(suit.name, it.strong) }

        }
        return newDeck
    }

    fun getNumberOfCardsInDeck(): Int {
        var size = 0
        deck.getDeck().values.forEach { list ->
            size += list.size
        }
        return size
    }

    fun getCardsWithMaxStrong(): List<Pair<String, Int?>> {
        return deck.getDeck().map { (suit, cards) ->
            suit.name to cards.maxByOrNull { it.strong }?.strong
        }
    }

    fun getCardsToWorkBySuit(suitName: String): List<Pair<String, Int>> {
        return deck.getDeck()[Suits.valueOf(suitName.uppercase())]?.map { card ->
            Pair(
                card.suit.name.uppercase(),
                card.strong
            )
        } ?: emptyList()
    }

    fun deleteCards(suitStrongPair: List<Pair<String, Int>>) {
        suitStrongPair.forEach { pair ->
            val cardForDelete = Card(Suits.valueOf(pair.first.uppercase()), pair.second)
            deck.deleteCards(cardForDelete)
        }
    }

    fun addCards(cards: List<Pair<String, Int>>) {
        deck.addCards(cards)
    }
}
