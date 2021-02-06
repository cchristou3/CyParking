// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');

// Import helper functions and constants.
const helpers = require('./helpers');
const constants = require('./constants');

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