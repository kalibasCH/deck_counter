package com.card_helper.view

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.card_helper.R
import com.card_helper.databinding.FragmentFragmentDeckHolderBinding
import com.card_helper.view.anim.Animation
import com.card_helper.view.interfaces.ViewsObserverInterface
import com.card_helper.viewModel.FragmentDeckHolderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentDeckHolder : Fragment(), ViewsObserverInterface {

    private var binding: FragmentFragmentDeckHolderBinding? = null

    companion object {
        fun newInstance() = FragmentDeckHolder()
    }

    private lateinit var viewModel: FragmentDeckHolderViewModel

    private lateinit var buttonDelete: ImageButton

    private lateinit var cardHoldersInView: Array<ImageView?>

    private lateinit var workSuit: String

    private val animation by lazy { Animation() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFragmentDeckHolderBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[FragmentDeckHolderViewModel::class.java]
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()

        workSuit = arguments?.getString(Keys.BUNDLE_KEY_FOR_SUIT_NAME)
            ?: let {
                Toast.makeText(
                    requireContext(),
                    "No suit in argument!",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

        viewModel.defineCardsToWorkBySuit(workSuit)

        observeViewModel()
        initializeClickListeners()
        cardsForDelete.clear()

        animation.animateTranslationYFrom0(buttonDelete)
    }

    override fun observeViewModel() {
        viewModel.cardsBySuit.observe(viewLifecycleOwner) { cards ->
            postImageInViewHolder(cards, workSuit)
        }
    }

    override fun initializeViews() {
        buttonDelete = binding!!.buttonDelete

        cardHoldersInView = arrayOf(
            binding!!.cardStrong2,
            binding!!.cardStrong3,
            binding!!.cardStrong4,
            binding!!.cardStrong5,
            binding!!.cardStrong6,
            binding!!.cardStrong7,
            binding!!.cardStrong8,
            binding!!.cardStrong9,
            binding!!.cardStrong10,
            binding!!.cardStrong11,
            binding!!.cardStrong12,
            binding!!.cardStrong13,
            binding!!.cardStrong14
        )
    }

    override fun initializeClickListeners() {

        buttonDelete.setOnClickListener {
            if (cardsForDelete.isNotEmpty()) {
                viewModel.deleteCards(cardsForDelete)
                cardHoldersInView.forEach {
                    it!!.scaleX = 1F
                    it.scaleY = 1F
                }
                deletedCards.addAll(cardsForDelete)
                requireActivity().onBackPressed()
            } else {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.noCardsSelected),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        cardHoldersInView.forEachIndexed { index, imageView ->

            imageView?.let {
                it.setOnClickListener {
                    val cardToDelete = Pair(workSuit, index + 2)
                    if (cardsForDelete.contains(cardToDelete)) {
                        imageView.scaleX = 1F
                        imageView.scaleY = 1F

                        val animationImageView = imageView.tag as? ObjectAnimator
                        animationImageView?.cancel()

                        cardsForDelete.remove(cardToDelete)
                    } else if (deletedCards.contains(cardToDelete)) {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.cardAlreadyDeleted),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        imageView.scaleX = 0.7F
                        imageView.scaleY = 0.7F
                        animation.animateShiverCard(imageView)
                        cardsForDelete.add(cardToDelete)
                    }
                }
            }
        }
    }

    private fun postImageInViewHolder(cardsForView: List<Pair<String, Int>>, suit: String) {
        cardHoldersInView.forEachIndexed { index, imageView ->
            val card = Pair(suit, index + 2)
            val idImage = getCardImageById(suit.lowercase(), card.second)
            when (card) {
                in cardsForView -> {
                    imageView!!.setImageResource(
                        getCardImageById(
                            suit.lowercase(),
                            card.second
                        )
                    )
                }

                in deletedCards -> {
                    imageView.let {
                        it?.alpha = 0.4F
                        it?.setImageResource(idImage)
                    }
                }

                else -> imageView.let {
                    it?.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun getCardImageById(suit: String, strong: Int): Int {
        return resources.getIdentifier(
            "${suit}s_${strong}",
            "drawable",
            requireActivity().packageName
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}