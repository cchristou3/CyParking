package io.github.cchristou3.CyParking.data.model.parking.slot;

import io.github.cchristou3.CyParking.data.model.parking.Parking;

/**
 * Purpose:<p> POJO to be used to transfer and receive data
 * via activities / fragments and HTTP requests.
 * This is a Subclass of Parking.</p>
 *
 * @author Charalambos Christou
 * @version 3.0 20/01/21
 */
public class PublicParking extends Parking {

    // TODO: fill out with attributes of a public parking

    /**
     * Public Constructor.
     * Initialize all the attributes of the class with the given arguments.
     *
     * @param coordinates The lot's to be booked coordinates
     * @param parkingID   The lot's id.
     */
    public PublicParking(Coordinates coordinates, int parkingID) {
        super(coordinates, parkingID);
    }


    /**
     * To be overridden by its subclasses
     * It should combine the class' attribute values to create a unique id for the object.
     */
    @Override
    public String generateUniqueId() {
        return null;
    }
}
