// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
import * as functions from 'firebase-functions';

// The Firebase Admin SDK to access Cloud Firestore.
import * as admin from 'firebase-admin';
admin.initializeApp();

// Import helper functions and constants.
import { deleteDocs, logError, nearbyUser } from './helpers';
import { PARKING_LOTS, FEEDBACK, EMAIL, BOOKINGS, BOOKING_USER_ID, OPERATOR_ID, USERS, EQUALS }
    from './constants';

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
    await admin.firestore().collection(PARKING_LOTS)
        .get()
        .then(
            (querySnapshot) => {
                querySnapshot.forEach(function (doc) {
                    if (doc.exists) {
                        console.log(doc.id, " => ", doc.data());
                        if (nearbyUser(doc.data(), userLatitude, userLongitude)) {
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
    admin.firestore().collection(FEEDBACK)
        .where(EMAIL, EQUALS, user.email).get()
        .then((querySnapshot) => deleteDocs(querySnapshot))
        .catch((error) => logError(error, FEEDBACK));

    // Delete any bookings that the user created
    admin.firestore().collection(BOOKINGS)
        .where(BOOKING_USER_ID, EQUALS, user.uid).get()
        .then((querySnapshot) => deleteDocs(querySnapshot))
        .catch((error) => logError(error, BOOKINGS + ' issuer'));

    // Delete any bookings that the user's lot was booked.
    admin.firestore().collection(BOOKINGS)
        .where(OPERATOR_ID, EQUALS, user.uid).get()
        .then((querySnapshot) => deleteDocs(querySnapshot))
        .catch((error) => logError(error, BOOKINGS + ' owner'));


    // // Delete any parking lots related to this user
    admin.firestore().collection(PARKING_LOTS).where(OPERATOR_ID, EQUALS, user.uid).get()
        .then((querySnapshot) => deleteDocs(querySnapshot))
        .catch((error) => logError(error, PARKING_LOTS));

    admin.firestore().collection(USERS).doc(user.uid).delete()
        .then(function (writeResult) {
            console.log('Result: ' + writeResult)
        })
        .catch((error) => logError(error, USER));

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
