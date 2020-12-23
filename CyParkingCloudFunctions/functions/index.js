// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');


// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();

//const cors = require('cors')({ origin: true }); TODO install cors dependency

// Import helper functions and constants.
const helpers = require('./helpers');
const constants = require('./constants')

// Still facing the same problem: https://dev.to/kodekage/understanding-node-error-errhttpheaderssent-19af
exports.filterLocations = functions.https.onRequest(async (req, res) => {
    // ref: https://www.endyourif.com/cant-set-headers-after-they-are-sent/
    res.setHeader('Content-Type', 'application/json');
    if (req.method === 'GET') {
        if (!req.query.latitude || !req.query.longitude) { // if at least one of the parameters is empty
            return res.status(422).send('Missing one or both parameters: latitude, longitude');
        }
        const userLatitude = req.query.latitude;
        const userLongitude = req.query.longitude;
        // Get all private parking into Cloud Firestore using the Firebase Admin SDK.
        var filteredReadResult = [];
        await admin.firestore().collection(constants.PRIVATE_PARKING)
            .get()
            .then(
                function (querySnapshot) {
                    querySnapshot.forEach(function (doc) {
                        if (doc.exists) {
                            //console.log(doc.id, " => ", doc.data().slotOfferList);                            
                            if (helpers.nearbyUser(doc.data(), userLatitude, userLongitude)) {
                                filteredReadResult.push(doc.data());
                            }
                        }
                    });
                }
            ).catch((e) => { return res.status(500).send('Something went wrong on the server! ' + e); })

        // Send the filtered parking back to the user.
        return res.json(filteredReadResult);
    }
    return res.status(403).send('Forbidden!');
})
