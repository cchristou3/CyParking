package io.github.cchristou3.CyParking.ui.views.operator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import io.github.cchristou3.CyParking.R
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.SlotOffer
import io.github.cchristou3.CyParking.data.interfaces.Navigable
import io.github.cchristou3.CyParking.databinding.FragmentSlotOfferBinding
import io.github.cchristou3.CyParking.ui.components.BaseFragment
import io.github.cchristou3.CyParking.ui.components.NavigatorFragment
import io.github.cchristou3.CyParking.ui.components.NonEmptyItemTouchHelper
import io.github.cchristou3.CyParking.ui.views.parking.lots.register.SlotOfferAdapter
import io.github.cchristou3.CyParking.ui.views.parking.lots.register.SlotOffersDiffCallback
import org.jetbrains.annotations.Contract
import java.util.*

/**
 * A fragment representing a list of the parking lot's owner's slot offers.
 * The user may add or remove any number of items as long as there is minimum one left.
 *
 * @author Charalambos Christou
 * @since 1.0 30/08/21
 */
class SlotOfferFragment : NavigatorFragment<FragmentSlotOfferBinding>() {

    private val mConcatAdapter: ConcatAdapter by lazy { initializeAdapters() }

    private val mSlotOfferAdapter: SlotOfferAdapter by lazy { mConcatAdapter.adapters.find { it is SlotOfferAdapter } as SlotOfferAdapter }

    private val mSlotOfferHeaderAdapter: SlotOfferHeaderAdapter by lazy { mConcatAdapter.adapters.find { it is SlotOfferHeaderAdapter } as SlotOfferHeaderAdapter }

    private val mViewModel: SlotOfferViewModel by lazy { ViewModelProvider(this).get(SlotOfferViewModel::class.java) }

    /**
     * Returns an instance of [ItemTouchHelper].
     * onSwipeLeft: Remove the item from the list
     * and notify the adapter.
     *
     * @return An instance of [ItemTouchHelper].
     */
    @get:Contract(" -> new")
    private val itemTouchHelper: ItemTouchHelper
        get() = ItemTouchHelper(NonEmptyItemTouchHelper(
                object : NonEmptyItemTouchHelper.Swipeable {
                    override fun onNoMoreSwipes() =
                            globalStateViewModel.updateToastMessage(R.string.lot_slot_offer_error)

                    override fun onSwipeLeft(itemPosition: Int) {
                        globalStateViewModel.updateToastMessage(R.string.item_removed)
                        // Access the current list - with a new reference
                        val currentOffers = mViewModel.slotOffersState.value?.list?.toMutableList()
                        // Remove the slot from the list
                        currentOffers?.removeAt(itemPosition)
                        // Update the adapter's list
                        mViewModel.updateSlotOffers(SlotOffersArgs(currentOffers.orEmpty(), true))
                    }
                }, resources, R.id.slot_offer_item_cv
        ))

    /**
     * Called to do initial creation of a fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (mViewModel.slotOffersState.value != null) return

        arguments?.apply {
            val offers = this.getParcelableArray("lot_slot_offers")
            val documentId = this.getString("lot_document_id")
            if (!(offers != null && documentId != null)) return@apply
            mViewModel.lotDocumentId = documentId
            mViewModel.updateSlotOffers(
                    SlotOffersArgs(offers.map { it as SlotOffer }, false)
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
        = super.onCreateView(FragmentSlotOfferBinding.inflate(inflater), R.string.slot_offers_label)


    /**
     * Called immediately after [.onCreateView]
     * has returned, but before any saved state has been restored in to the view.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.slotOffersState.observe(viewLifecycleOwner) {
            // Update the presented list
            mSlotOfferAdapter.submitList(it.list)
            mViewModel.updateSlotOfferListInDb(it)
        }
        mViewModel.buttonsState.observe(viewLifecycleOwner) {
            mConcatAdapter.notifyItemChanged(0, it)
        }
    }

    private fun initializeAdapters(): ConcatAdapter {
        val slotOfferAdapter = SlotOfferAdapter(SlotOffersDiffCallback(), itemTouchHelper)

        val manager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.fragmentSlotOfferListRv.layoutManager = manager
        binding.fragmentSlotOfferListRv.setHasFixedSize(true)

        val mergeAdapter = ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(true).setStableIdMode(ConcatAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS).build(),
                SlotOfferHeaderAdapter(
                        { mViewModel.updateSlotOfferToBeAdded(SlotOffer.SlotOfferAttribute.DURATION, it) },
                        { mViewModel.updateSlotOfferToBeAdded(SlotOffer.SlotOfferAttribute.PRICE, it) }
                ) {
                    Toast.makeText(requireContext(), " onAddButtonClicked ", Toast.LENGTH_SHORT).show();
                    mViewModel.createSlotOffer(globalStateViewModel::updateToastMessage)
                }, slotOfferAdapter)

        binding.fragmentSlotOfferListRv.adapter = mergeAdapter
        return mergeAdapter
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment].
     */
    override fun toAuthenticator() {
        navigateTo(
                SlotOfferFragmentDirections.actionNavSlotOffersToNavAuthenticator()
        )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.ViewBookingsFragment].
     */
    override fun toBookings() {
        navigateTo(
                SlotOfferFragmentDirections.actionNavSlotOffersToNavViewBookings()
        )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [AccountFragment].
     */
    override fun toAccount() {
        navigateTo(
                SlotOfferFragmentDirections.actionNavSlotOffersToNavAccount()
        )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment].
     */
    override fun toFeedback() {
        navigateTo(
                SlotOfferFragmentDirections.actionNavSlotOffersToNavFeedback()
        )
    }

    /**
     * Navigates from the current Fragment subclass to the
     * [HomeFragment].
     */
    override fun toHome() {
        navigateTo(
                SlotOfferFragmentDirections.actionNavSlotOffersToNavHome()
        )
    }

    data class SlotOffersArgs(val list: List<SlotOffer>, val shouldUpdate: Boolean)
}