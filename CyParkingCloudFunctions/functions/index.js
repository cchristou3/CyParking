// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');


// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();

//const cors = require('cors')({ origin: true }); TODO install cors dependency

const helpers = require('./helpers');

// Still facing the same problem: https://dev.to/kodekage/understanding-node-error-errhttpheaderssent-19af
exports.filterLocations = functions.https.onRequest(async (req, res) => {
    // ref: https://www.endyourif.com/cant-set-headers-after-they-are-sent/
    res.setHeader('Content-Type', 'application/json');
    if (req.method === 'GET') {
        if (!req.query.latitude || !req.query.longitude) { // if at least of of the parameters is empty
            return res.status(422).send('Missing one or both parameters: latitude, longitude');
        }
        const userLatitude = req.query.latitude;
        const userLongitude = req.query.longitude;
        // Get all private parking into Cloud Firestore using the Firebase Admin SDK.
        var filteredReadResult = [];
        const readResult = await admin.firestore().collection('private_parking')
            .get()
            .then(
                function (querySnapshot) {
                    querySnapshot.forEach(function (doc) {
                        const parking = helpers.privateParkingConverter.fromFirestore(doc);                        
                        if (helpers.nearbyUser(parking, userLatitude, userLongitude)) {
                            filteredReadResult.push(parking);
                        }
                    });
                }
            ).catch((e) => { return res.status(500).send('Something went wrong on the server! ' + e); })

        var filteredReadResultInJSON = filteredReadResult;
        // Send the filtered parking back to the user.
        return res.json(filteredReadResultInJSON);
    }
    return res.status(403).send('Forbidden!');
})
