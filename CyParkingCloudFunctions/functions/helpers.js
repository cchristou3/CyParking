const constants = require('./constants')

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
// Helper Functions
// Firestore data converter
module.exports.privateParkingConverter = {
    toFirestore: function (privateParking) {
        return {
            Coordinates: privateParking.Coordinates,
            ParkingID: privateParking.ParkingID,
            PricingList: privateParking.PricingList,
            Capacity: privateParking.Capacity,
            AvailableSpaces: privateParking.AvailableSpaces,
            CapacityForDisabled: privateParking.CapacityForDisabled,
            AvailableSpacesForDisabled: privateParking.AvailableSpacesForDisabled,
            OpeningHours: privateParking.OpeningHours
        }
    },
    fromFirestore: function (snapshot, options) {
        const data = snapshot.data(options);
        return {
            Coordinates: data.Coordinates,
            ParkingID: data.ParkingID,
            PricingList: data.PricingList,
            Capacity: data.Capacity,
            AvailableSpaces: data.AvailableSpaces,
            CapacityForDisabled: data.CapacityForDisabled,
            AvailableSpacesForDisabled: data.AvailableSpacesForDisabled,
            OpeningHours: data.OpeningHours
        };
    }
}

// Determines whether the given location is inside the range
const nearbyUser = function (parking, userLatitude, userLongitude) {
    // User's lat lng
    var lat1 = userLatitude
    var lon1 = userLongitude;

    // Parking's lat lng
    var lat2 = parking.Coordinates.latitude;
    var lon2 = parking.Coordinates.longitude;
    
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
}
module.exports.nearbyUser = nearbyUser;