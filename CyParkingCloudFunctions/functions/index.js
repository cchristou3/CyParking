// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');


// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();

const cors = require('cors')({ origin: true }); // Required for HTTP functions

// Import helper functions and constants.
const helpers = require('./helpers');
const constants = require('./constants')

// curl --header "Content-Type: application/json" \
//   --request POST \
//   --data '{"latitude":"30","longitude":"29"}' \
//   http://localhost:5001/cyparking-537e0/us-central1/filterLocations
//   https://us-central1-cyparking-537e0.cloudfunctions.net/filterLocations

// Still facing the same problem: https://dev.to/kodekage/understanding-node-error-errhttpheaderssent-19af
exports.filterLocations = functions.https.onCall(async (data, context) => {
    console.log('Data => ' + data)
    console.log('Context => ' + context)
    // ref: https://www.endyourif.com/cant-set-headers-after-they-are-sent/
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
                    //throw new functions.https.HttpsError('internal', "Oops something went wrong: " + doc)
                    if (doc.exists) {
                        console.log(doc.id, " => ", doc.data());
                        if (helpers.nearbyUser(doc.data(), userLatitude, userLongitude)) {
                            filteredReadResult.push(doc.data());
                        }
                    }
                });
            }
        ).catch((e) => { throw new functions.https.HttpsError('internal', "Oops something went wrong: " + e) })

    //throw new functions.https.HttpsError('internal', "Oops something: " + filteredReadResult)

    console.log(filteredReadResult)

    // Send the filtered parking back to the user.
    return JSON.stringify(filteredReadResult);
})

/* TODO: Complete after administrator's front-end is done.
exports.notifyAdminnistrator = functions.firestore
    .document('feedback/{docId}')
    .onWrite((change, context) => {
        // TODO: Send notification to the administrator's system
    });
*/
