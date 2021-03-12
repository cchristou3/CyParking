package io.github.cchristou3.CyParking.apiClient.utils

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class UtilsTest {

    @Test
    fun getDistanceApart_inSamePosition_returnsZero() {
        // Given
        val latLng1 = LatLng(1.00002, 1.00002)
        val latLng2 = LatLng(1.00002, 1.00002)

        // When
        val output = getDistanceApart(latLng1, latLng2)
        // Then
        assertEquals(0.0, output, 0.0)
    }

    @Test
    fun getDistanceApart_outOfRange_returnsNoZero() {
        // Given
        val latLng1 = LatLng(2.00002, 2.00002)
        val latLng2 = LatLng(1.00002, 1.00002)
        // When
        val output = getDistanceApart(latLng1, latLng2)
        // Then
        assertNotEquals(0.0, output, 0.0)
    }
}