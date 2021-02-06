// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');

// Import helper functions and constants.
const helpers = require('./helpers');
const constants = require('./constants');


exports.getNearbyParkingLots = functions.https.onCall(async (data, context) => {
    console.log('Data => ' + data)
    console.log('Context => ' + context)
    if (!data.latitude || !data.longitude) { // if at least one of the parameters is empty
        // Throwing an HttpsError so that the client gets the error details.
        throw new functions.https.HttpsError('invalid-argument', 'Missing one or both parameters: latitude, longitude');
    }
    const userLatitude = data.latitude;
    const userLongitude = data.longitude;
    // Get all private parking into Cloud Firestore using the Firebase Admin SDK.
    var nearbyParkingLotDocIds = [];
    await admin.firestore().collection(constants.PARKING_LOTS)
        .get()
        .then(
            (querySnapshot) => {
                querySnapshot.forEach(function (doc) {
                    if (doc.exists) {
                        console.log(doc.id, " => ", doc.data());
                        if (helpers.nearbyUser(doc.data(), userLatitude, userLongitude)) {
                            nearbyParkingLotDocIds.push(doc.id);
                        }
                    }
                });
            }
        ).catch((e) => {
            helpers.logError(e, 'getNearbyParkingLots')
            throw new functions.https.HttpsError('internal', "Internal server error: " + e)
        })

    console.log(nearbyParkingLotDocIds) // Todo: Document function

    // Send the filtered parking back to the user.
    return JSON.stringify(nearbyParkingLotDocIds);
});