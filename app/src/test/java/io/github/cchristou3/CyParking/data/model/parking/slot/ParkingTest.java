package io.github.cchristou3.CyParking.data.model.parking.slot;

import org.junit.Test;

import io.github.cchristou3.CyParking.data.model.parking.Parking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link Parking} class.
 */
public class ParkingTest {

    private static final Parking.Coordinates COORDS = new Parking.Coordinates(1, 1);
    private static final int ID = 496;

    @Test
    public void Parking_initializesCorrectAttributes() {
        // When parking object gets initialized
        Parking parking = new Parking(COORDS, ID) {
            public String generateUniqueId() {
                return null;
            }
        };
        // Then getters should return the same values
        assertTrue(
                parking.getCoordinates().equals(COORDS)
                        && parking.getParkingId() == ID
                        && parking.getCoordinates().getLongitude() == COORDS.getLongitude()
                        && parking.getCoordinates().getLatitude() == COORDS.getLatitude()
        );
    }

    @Test
    public void toString_returnsExpectedString() {
        // When parking object gets initialized
        Parking parking = new Parking(COORDS, ID) {
            public String generateUniqueId() {
                return null;
            }
        };
        // Then
        assertEquals("Id: " + ID + ", " + "coordinates: { "
                + "latitude:" + COORDS.getLatitude()
                + ", longitude:" + COORDS.getLongitude()
                + " }", parking.toString());
    }
}