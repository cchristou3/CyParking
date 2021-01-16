const constants = require('./constants')

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
// Helper Functions


const deleteAll = function (querySnapshot) {
    console.log("Object: "+JSON.stringify(querySnapshot))
    querySnapshot.forEach(function (doc) {
        // doc.data() is never undefined for query doc snapshots
        console.log(doc)
        doc.ref.delete();
    });
}
module.exports.deleteAll = deleteAll;


// Determines whether the given location is inside the range
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

        return (d <= constants.MAXIMUM_DISTANCE_FROM_USER);
    } catch (error) {
        return false;
    }
}
module.exports.nearbyUser = nearbyUser;