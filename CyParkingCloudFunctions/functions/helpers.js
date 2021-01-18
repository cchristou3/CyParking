
const MAXIMUM_DISTANCE_FROM_USER = require('./constants').MAXIMUM_DISTANCE_FROM_USER;

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

/**
 * Deletes all documents of the given `QuerySnapShot`.
 * 
 * @param {FirebaseFirestore.QuerySnapshot<FirebaseFirestore.DocumentData>} querySnapshot 
 * A `QuerySnapShot` that contains zero or more `QueryDocumentSnapshot` objects.
 * @param {FirebaseFirestore.WriteBatch} batch 
 */
const deleteDocs = (querySnapshot, batch) => {
    querySnapshot.docs.forEach((doc) => {
        console.log("Delete document: " + JSON.stringify(doc.data()))
        batch.delete(doc.ref)
    })
    return batch;
}
module.exports.deleteDocs = deleteDocs;


/**
 * Logs the error. Logged errors can be found in 
 * Firebase console -> Fucntions -> Logs.
 * @param error An error.
 * @param title The title associated with the error.
 */
const logError = (error, title) => {
    console.error(title + ': ', error)
}
module.exports.logError = logError;


/**
 * Determines whether the given location is inside the range.
 * 
 * @param parking A parking lot object.
 * @param userLatitude The user's latitude.
 * @param userLongitude The user's longitude.
 */
const nearbyUser = function (parking, userLatitude, userLongitude) {
    try {
        // User's lat lng
        var lat1 = userLatitude
        var lon1 = userLongitude;

        // Parking's lat lng
        var lat2 = parking.coordinates.latitude;
        var lon2 = parking.coordinates.longitude;

        // Calculate the distance between the two points (User and current parking)
        // Reference: http://www.movable-type.co.uk/scripts/latlong.html
        var R = 6371e3; // metres
        var φ1 = lat1 * Math.PI / 180; // φ, λ in radians
        var φ2 = lat2 * Math.PI / 180;
        var Δφ = (lat2 - lat1) * Math.PI / 180;
        var Δλ = (lon2 - lon1) * Math.PI / 180;

        var a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
            Math.cos(φ1) * Math.cos(φ2) *
            Math.sin(Δλ / 2) * Math.sin(Δλ / 2);

        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        var d = R * c; // d is the total distance in metres

        return (d <= MAXIMUM_DISTANCE_FROM_USER);
    } catch (error) {
        return false;
    }
}
module.exports.nearbyUser = nearbyUser;