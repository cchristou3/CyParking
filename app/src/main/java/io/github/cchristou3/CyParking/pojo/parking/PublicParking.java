package io.github.cchristou3.CyParking.pojo.parking;

import java.util.HashMap;

/**
 * purpose: POJO to be used to transfer and receive data
 * via activities / fragments and HTTPS requests.
 * This is a Subclass of Parking.
 *
 * @author Charalambos Christou
 * @version 2.0 29/10/20
 */
public class PublicParking extends Parking {
    // TODO: fill out with attributes of a public parking

    public PublicParking() {
    }

    public PublicParking(HashMap<String, Double> mCoordinates, int mParkingID) {
        super(mCoordinates, mParkingID);
    }
}
