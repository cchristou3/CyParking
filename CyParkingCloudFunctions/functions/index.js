// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');


// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();

const cors = require('cors')({ origin: true }); // Required for HTTP functions

// Import helper functions and constants.
const helpers = require('./helpers');
const constants = require('./constants')

/**
 * When invoked, queries all parking lots from the database and filters them
 * based on the distance between the parking lot and the user.
 * It then returns the filtered list to the client.
 */
exports.filterLocations = functions.https.onCall(async (data, context) => {
    console.log('Data => ' + data)
    console.log('Context => ' + context)
    if (!data.latitude || !data.longitude) { // if at least one of the parameters is empty
        // Throwing an HttpsError so that the client gets the error details.
        throw new functions.https.HttpsError('invalid-argument', 'Missing one or both parameters: latitude, longitude');
    }
    const userLatitude = data.latitude;
    const userLongitude = data.longitude;
    // Get all private parking into Cloud Firestore using the Firebase Admin SDK.
    var filteredReadResult = [];
    await admin.firestore().collection(constants.PARKING_LOTS)
        .get()
        .then(
            function (querySnapshot) {
                querySnapshot.forEach(function (doc) {
                    if (doc.exists) {
                        console.log(doc.id, " => ", doc.data());
                        if (helpers.nearbyUser(doc.data(), userLatitude, userLongitude)) {
                            filteredReadResult.push(doc.data());
                        }
                    }
                });
            }
        ).catch((e) => { throw new functions.https.HttpsError('internal', "Oops something went wrong: " + e) })

    console.log(filteredReadResult)

    // Send the filtered parking back to the user.
    return JSON.stringify(filteredReadResult);
})

/**
 * When a user deletes their account, clean up after them.
 */
exports.cleanupUser = functions.auth.user().onDelete(async (user) => {
    console.log(user.email + ' is about to get deleted')
    // Delete any feedback messages related to this user    
    admin.firestore().collection(constants.FEEDBACK)
        .where(constants.EMAIL, constants.EQUALS, user.email).get()
        .then((querySnapshot) => {
            querySnapshot.docs.forEach((doc) => {
                console.log("Delete message: " + JSON.stringify(doc.data()))
                doc.ref.delete();
            })
        })
        .catch(function (error) {
            console.log("Error getting documents: ", error);
        });

    // Delete any bookings that the user created
    admin.firestore().collection(constants.BOOKINGS)
        .where(constants.BOOKING_USER_ID, constants.EQUALS, user.uid).get()
        .then((querySnapshot) => {
            querySnapshot.docs.forEach((doc) => {
                console.log("User booking: " + JSON.stringify(doc.data()))
                doc.ref.delete();
            })
        })
        .catch(function (error) {
            console.log("Error getting documents: ", error);
        });

    // Delete any bookings that the user's lot was booked.
    admin.firestore().collection(constants.BOOKINGS)
        .where(constants.OPERATOR_ID, constants.EQUALS, user.uid).get()
        .then((querySnapshot) => {
            querySnapshot.docs.forEach((doc) => {
                console.log("Owner booking: " + JSON.stringify(doc.data()))
                doc.ref.delete();
            })
        })
        .catch(function (error) {
            console.log("Error getting documents: ", error);
        });


    // // Delete any parking lots related to this user
    admin.firestore().collection(constants.PARKING_LOTS).where(constants.OPERATOR_ID, constants.EQUALS, user.uid).get()
        .then((querySnapshot) => {
            querySnapshot.docs.forEach((doc) => {
                console.log("Lot owner message: " + JSON.stringify(doc.data()))
                doc.ref.delete();
            })
        })
        .catch(function (error) {
            console.log("Error getting documents: ", error);
        });

    admin.firestore().collection(constants.USERS).doc(user.uid).delete()
        .then(function (writeResult) {
            console.log('Result: ' + writeResult)
        });
    console.log(user.email + ' got deleted')
    return;
});

/* TODO: Complete after administrator's front-end is done.
exports.notifyAdminnistrator = functions.firestore
    .document('feedback/{docId}')
    .onWrite((change, context) => {
        // TODO: Send notification to the administrator's system
    });
*/
