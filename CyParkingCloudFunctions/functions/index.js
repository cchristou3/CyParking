// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();

// Import helper functions and constants.
const helpers = require('./helpers');
const constants = require('./constants');

// todo 17/01/21 https://firebase.google.com/docs/functions/organize-functions

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
            (querySnapshot) => {
                querySnapshot.forEach(function (doc) {
                    if (doc.exists) {
                        console.log(doc.id, " => ", doc.data());
                        if (helpers.nearbyUser(doc.data(), userLatitude, userLongitude)) {
                            filteredReadResult.push(doc.data());
                        }
                    }
                });
            }
        ).catch((e) => {
            helpers.logError(e, 'filterLocations')
            throw new functions.https.HttpsError('internal', "Internal server error: " + e)
        })

    console.log(filteredReadResult)

    // Send the filtered parking back to the user.
    return JSON.stringify(filteredReadResult);
})

/**
 * When a user deletes their account, clean up after them.
 */
exports.cleanupUser = functions.auth.user().onDelete(async (user) => {
    console.log(user.email + ' is about to get deleted')
    // TODO: Put all in a batch

    var batch = admin.firestore().batch();

    // Delete any feedback messages related to this user    
    await admin.firestore().collection(constants.FEEDBACK)
        .where(constants.EMAIL, constants.EQUALS, user.email).get()
        .then((querySnapshot) => helpers.deleteDocs(querySnapshot, batch))
        .catch((error) => helpers.logError(error, constants.FEEDBACK));

    // Delete any bookings that the user created
    await admin.firestore().collection(constants.BOOKINGS)
        .where(constants.BOOKING_USER_ID, constants.EQUALS, user.uid).get()
        .then((querySnapshot) => helpers.deleteDocs(querySnapshot, batch))
        .catch((error) => helpers.logError(error, constants.BOOKINGS + ' issuer'));

    // Delete any bookings that the user's lot was booked.
    await admin.firestore().collection(constants.BOOKINGS)
        .where(constants.OPERATOR_ID, constants.EQUALS, user.uid).get()
        .then((querySnapshot) => helpers.deleteDocs(querySnapshot, batch))
        .catch((error) => helpers.logError(error, constants.BOOKINGS + ' owner'));

    // // Delete any parking lots related to this user
    await admin.firestore().collection(constants.PARKING_LOTS).where(constants.OPERATOR_ID, constants.EQUALS, user.uid).get()
        .then((querySnapshot) => helpers.deleteDocs(querySnapshot, batch))
        .catch((error) => helpers.logError(error, constants.PARKING_LOTS));

    batch.delete(admin.firestore().collection(constants.USERS).doc(user.uid))

    batch.commit().then((result) => {
        console.log(user.email + ' got deleted from ' + result.toString())
    })
    return;
});

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

/**
 * Updates the email of the user in the database.
 */
exports.updateEmail = functions.https.onCall(async (data, context) => {
    console.log('Data => ' + data)
    console.log('Context => ' + context)
    // Create a write batch
    var batch = admin.firestore().batch();

    // Update all feedback messages that have the given oldEmail
    // with the newEmail
    await admin.firestore().collection(constants.FEEDBACK)
        .where(constants.EMAIL, constants.EQUALS, data.oldEmail)
        .get()
        .then((querySnapshot) => {
            querySnapshot.docs.forEach((doc) => {
                batch.update(doc.ref, { email: data.newEmail })
            })
        })

    // Update the user's email from the USERS node
    batch.update(
        admin.firestore().collection(constants.USERS).doc(data.userId),
        { email: data.newEmail }
    )

    // Commit the batch
    batch.commit().then((result) => {
        console.log('Email update successful! result: ' + result)
    })
    return;
})

/* TODO: Complete after administrator's front-end is done.
exports.notifyAdminnistrator = functions.firestore
    .document('feedback/{docId}')
    .onWrite((change, context) => {
        // TODO: Send notification to the administrator's system
    });
*/
