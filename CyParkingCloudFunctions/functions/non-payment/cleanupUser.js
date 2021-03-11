// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');

// Import helper functions and constants.
const helpers = require('../helpers');
const constants = require('../constants');

/**
 * When a user deletes their account, clean up after them.
 */
exports.cleanupUser = functions.auth.user().onDelete(async (user) => {
    console.log(user.email + ' is about to get deleted')    

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