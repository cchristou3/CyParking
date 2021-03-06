// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();

// Get references to the cloud functions from different files
const getNearbyParkingLots = require('./non-payment/getNearbyParkingLots');
const cleanupUser = require('./non-payment/cleanupUser');
const updateEmail = require('./non-payment/updateEmail');

const stripeFunctions = require('./stripe/index');

// Export them

// Functions related to the application's data, exluding payments.
exports.getNearbyParkingLots = getNearbyParkingLots.getNearbyParkingLots;
const cleanupUserFromApplication = cleanupUser.cleanupUser;
exports.updateEmail = updateEmail.updateEmail;

// Functions related to Stripe
const cleanupUserFromStripe = stripeFunctions.cleanupUser;
exports.createEphemeralKey = stripeFunctions.createEphemeralKey;
exports.createStripeCustomer = stripeFunctions.createStripeCustomer;
exports.createStripePayment = stripeFunctions.createStripePayment;
exports.handleWebhookEvents = stripeFunctions.handleWebhookEvents;

exports.cleanUpUser = functions.auth.user().onDelete(async (user) => {
    cleanupUserFromApplication(user);
    cleanupUserFromStripe(user);
    return;
});


/* TODO: Complete after administrator's front-end is done.
exports.notifyAdminnistrator = functions.firestore
    .document('feedback/{docId}')
    .onWrite((change, context) => {
        // TODO: Send notification to the administrator's system
    });
*/
