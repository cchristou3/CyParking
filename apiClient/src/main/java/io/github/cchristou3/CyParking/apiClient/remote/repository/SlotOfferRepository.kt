package io.github.cchristou3.CyParking.apiClient.remote.repository

import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.SlotOffer

/**
 * Purpose: <p>contain all methods to update the list of slot offers of a specific parking lot.</p>
 *
 * @author Charalambos Christou
 * @since 1.0 30/08/21
 */
class SlotOfferRepository : DataSourceRepository.ParkingLotHandler {

    /**
     * Updates the slot offers of the given parking lot with the specified ones.
     *
     * @param list the new slot offers
     * @param lotDocumentId the reference of the lot in the DB
     */
    fun updateSlotOfferList(list: List<SlotOffer>, lotDocumentId: String) {
        parkingLotsRef.document(lotDocumentId)
                .update(DataSourceRepository.ParkingLotHandler.SLOT_OFFERS_LIST, list)
    }
}