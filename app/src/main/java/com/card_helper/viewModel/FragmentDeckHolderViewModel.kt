package com.card_helper.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.card_helper.model.CardHelperModel
import com.card_helper.repository.DeckRepositoryAccess
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FragmentDeckHolderViewModel @Inject constructor(repository: DeckRepositoryAccess) : ViewModel() {

    private val cardHelperModel : CardHelperModel = CardHelperModel(repository)

    private val _cardsBySuit = cardHelperModel.cardsBySuit
    val cardsBySuit: LiveData<List<Pair<String, Int>>> = _cardsBySuit

    fun defineCardsToWorkBySuit(suitName: String) {
        cardHelperModel.fetchCardsToWorkBySuit(suitName)
    }
    fun deleteCards(cards: List<Pair<String, Int>>) {
        cardHelperModel.deleteCards(cards)
    }
}