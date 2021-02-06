// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();

// Get references to the cloud functions from different files
const getNearbyParkingLots = require('./getNearbyParkingLots');
const cleanupUser = require('./cleanupUser');
const updateEmail = require('./updateEmail');

// Export them
exports.getNearbyParkingLots = getNearbyParkingLots.getNearbyParkingLots;
exports.cleanupUser = cleanupUser.cleanupUser;
exports.updateEmail = updateEmail.updateEmail;

/* TODO: Complete after administrator's front-end is done.
exports.notifyAdminnistrator = functions.firestore
    .document('feedback/{docId}')
    .onWrite((change, context) => {
        // TODO: Send notification to the administrator's system
    });
*/
