package io.github.cchristou3.CyParking.ui.views.operator

import androidx.core.util.Consumer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.cchristou3.CyParking.R
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.SlotOffer
import io.github.cchristou3.CyParking.apiClient.remote.repository.SlotOfferRepository
import io.github.cchristou3.CyParking.utils.Utility
import io.github.cchristou3.CyParking.utils.Utility.contains

/**
 * Purpose:
 *
 * Data persistence when configuration changes.
 * Used when the operator is viewing/updating his/her parking lot offers.
 *
 * @author Charalambos Christou
 * @version 1.0 14/08/21
 */
class SlotOfferViewModel : ViewModel() {

    private val mSlotOffersState = MutableLiveData<SlotOfferFragment.SlotOffersArgs>()

    private val mSlotOfferRepository by lazy { SlotOfferRepository() }

    private var mSlotOfferToBeAdded = SlotOffer()

    private val mButtonsState = MutableLiveData(false)

    private var mLotDocumentId: String? = null

    var lotDocumentId = mLotDocumentId
    set(value) = kotlin.run { mLotDocumentId = value }

    val buttonsState: LiveData<Boolean>
        get() = mButtonsState

    val slotOffersState: LiveData<SlotOfferFragment.SlotOffersArgs>
        get() = mSlotOffersState

    fun updateSlotOfferToBeAdded(attribute: SlotOffer.SlotOfferAttribute, value: Float?) {
        val newValue = value ?: 1.toFloat()
        val slotOffer = mSlotOfferToBeAdded
        when (attribute) {
            SlotOffer.SlotOfferAttribute.PRICE -> {
                slotOffer.price = newValue
            }
            SlotOffer.SlotOfferAttribute.DURATION -> {
                slotOffer.duration = newValue
            }
        }
        // 0.0F is set by default to unassigned float variables
        // If both are assigned then enable the button
        mButtonsState.value = slotOffer.duration != 0.0F && slotOffer.price != 0.0F
    }

    fun updateSlotOffers(args: SlotOfferFragment.SlotOffersArgs) = mSlotOffersState.run { mSlotOffersState.value = args }

    /**
     * Updates the slot offers of the given parking lot with the specified ones.
     *
     * @param slotOffersArgs an object containing the new slot offers and a flag
     * that indicates whether to update them or not in the DB.
     */
    fun updateSlotOfferListInDb(slotOffersArgs: SlotOfferFragment.SlotOffersArgs) {
        if (slotOffersArgs.shouldUpdate && !mLotDocumentId.isNullOrEmpty()) {
            mSlotOfferRepository.updateSlotOfferList(slotOffersArgs.list, mLotDocumentId!!)
        }
    }

    fun createSlotOffer(displayToast: Consumer<Int>) {
        // Check if it already exists
        if (contains(mSlotOffersState.value?.list ?: listOf(), mSlotOfferToBeAdded)) {
            displayToast.accept(R.string.slot_offer_already_exist)
            // TODO: 24/01/2021 Animate color to that item
            return
        }

        val list: MutableList<SlotOffer> = Utility.cloneList(mSlotOffersState.value?.list
                ?: listOf())
        mSlotOfferToBeAdded.let {
            list.add(it)
            mSlotOffersState.value = SlotOfferFragment.SlotOffersArgs(list, shouldUpdate = true)
            // Create a new instance of slot offer
            mSlotOfferToBeAdded = SlotOffer(it)
            // Display message to user
            displayToast.accept(R.string.item_added)
        }
    }
}