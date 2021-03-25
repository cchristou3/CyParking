<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]



<!-- PROJECT LOGO -->
<p align="center">
  <h3 align="center">CyParking</h3>

  <p align="center">
    An awesome application which automates parking finding and operating!
    <br/>
    ·
    <a href="https://github.com/cchristou3/CyParking/issues">Report Bug</a>
    ·
    <a href="https://github.com/cchristou3/CyParking/issues">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
## Table of Contents

* [About the Project](#about-the-project)
  * [Features](#features)
  * [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Usage](#usage)
* [Roadmap](#roadmap)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)
* [Acknowledgements](#acknowledgements)



<!-- ABOUT THE PROJECT -->
## About Myself

The project illustrates the computing concepts I've been taught and the knowledge I've acquired throughout my four
year journey at UCLan Cyprus. This project is my actual final year thesis project.

## About The Project
There are many great parking applications worldwide, however, I didn't find one that works on Cyprus so I created this one. 
I want to create a parking application so amazing that it'll be your go-to when looking for parking.

Here's why:
* Your time should be focused on creating something amazing. A project that solves a problem and helps others
* You shouldn't be doing the same tasks over and over like looking for small coins for parking
* You should element DRY principles to the rest of your life :smile:

Of course, the application cannot be deployed yet. So I'll be adding more features in the near future. You may also suggest changes by forking this repo and creating a pull request or opening an issue.

### Features
The application has the following features so far:
* Book a parking slot (and pay via credit card) for a specific period [User]
* View the details of a booking [User]
* Cancel a booking [User]
* View all available private parking locations within a range [User]
* View for each locations its available parking spaces [User]
* Sign in/up/out [Everyone]
* Update name/email/password [Everyone]
* Send Feedback [Everyone]
* Register Parking Lot [Operator]
* Manually update lot availability [Operator]
* Update lot availability and booking status when scanning QR Codes [Operator]

Note: Operators have the same access rights as Users. In this application context, a User is a any user that is logged in. Any non-logged in user
is refered to a an anonymous user or guest.
A list of commonly used resources that I find helpful are listed in the acknowledgements.

### Built With
* [Android Java & Kotlin](https://developer.android.com/)
* [Firebase](https://firebase.google.com/)
* [Node.js](https://nodejs.org/en/)



<!-- GETTING STARTED -->
## Getting Started
To get a local copy up and running follow these simple example steps.

### Prerequisites

* [Node.js](https://developer.android.com/)

* npm
```sh
npm install npm@latest -g
```
* Firebase Tools Package
```sh
npm install -g firebase-tools
```

* Logging into Firebase
```sh
firebase login
```

* Creating a new Stripe Account by clicking on the following link [https://stripe.com/en-gb-cy](https://stripe.com/en-gb-cy)

### Installation

1. Get a free Google Maps API Key by following the guidelines in [https://developers.google.com/maps/documentation/android-sdk/start](https://developers.google.com/maps/documentation/android-sdk/start)

2. Follow the steps on [https://firebase.google.com/docs/android/setup](https://firebase.google.com/docs/android/setup) to set up a Firebase project

3. Clone the repo
```sh
git clone https://github.com/cchristou3/CyParking.git
```

4. Enter your Google Maps API key in `google_maps_api.xml` 
```sh
<string name="google_maps_key"...>[Put your api key here]</string>
```

5. Clean and Rebuild the project using the following command to create the project's ViewBinding classes
```sh
./gradlew clean :assembleDebug
```

6. Initialize the Firebase Cloud Functions via
```sh
firebase init functions
```

7. Install dependencies locally by running 
```sh
cd functions; npm install; cd -
```

8. Ensure that there is only one top-level index.js (this project's one)


9. Deploy your project using 
```sh 
cd functions; npm run deploy; cd - 
```

10. Add your Stripe API Secret Key to your Cloud Functions environment configuration:
```sh
firebase functions:config:set stripe.secret=<YOUR STRIPE SECRET KEY>
```

11. Run the following command to open the Firebase console
```sh 
firebase open functions
```

12. Copy the URL for the handleWebhookEvents functions (e.g. https://region-project-name.cloudfunctions.net/handleWebhookEvents)

13. Create a new webhook endpoint with the URL in the [Stripe Dashboard](https://dashboard.stripe.com/dashboard)

14. Copy the signing secret (whsec_xxx) and add it to Firebase config:
```sh 
firebase functions:config:set stripe.webhooksecret=<YOUR WEBHOOK SECRET>
```

15. Redeploy the handleWebhookEvents function: 
```sh 
firebase deploy --only functions:handleWebhookEvents
```

_For any difficulties in setting up the webhook please refer to [Firebase mobile payments: Android](https://www.youtube.com/watch?v=nw7rOijQKo8&t=120s) at 45:35
and [Firebase mobile payments: Android & iOS with Cloud Functions for Firebase](https://github.com/stripe-samples/firebase-mobile-payments)_
<!-- USAGE EXAMPLES -->
## Usage

Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources.

_For more examples, please refer to the [Documentation](https://github.com/cchristou3/CyParking/blob/main/README.md)_



<!-- ROADMAP -->
## Roadmap

See the [open issues](https://github.com/cchristou3/CyParking/issues) for a list of proposed features (and known issues).



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



<!-- LICENSE -->
## License

No License added yet!
<!-- Distributed under the MIT License. See `LICENSE` for more information. -->



<!-- CONTACT -->
## Contact

Charalambos Christou - cchristou3@uclan.ac.uk

Project Link: [https://github.com/cchristou3/CyParking](https://github.com/cchristou3/CyParking)



<!-- ACKNOWLEDGEMENTS -->
## Acknowledgements
* [Volley](https://github.com/google/volley)
* [Gson](https://github.com/google/gson)
* [Java Code Conventions](http://www.edparrish.net/common/javadoc.html#:~:text=How%20To%20Document%20and%20Organize%20Your%20Java%20Code,placement%20of%20curly%20braces.%20...%20More%20items...%20)
* [Cloud Firestore](https://firebase.google.com/docs/firestore/quickstart#:~:text=%20Create%20a%20Cloud%20Firestore%20database%20%201,getting%20started%20with...%204%20Click%20Done.%20More%20)
* [README Template](https://github.com/othneildrew/Best-README-Template)

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/badge/contributors-2-blue
[contributors-url]: https://github.com/cchristou3/CyParking/graphs/contributors

[forks-shield]: https://img.shields.io/github/forks/cchristou3/CyParking
[forks-url]: https://github.com/cchristou3/CyParking/network/members

[stars-shield]: https://img.shields.io/github/stars/cchristou3/CyParking
[stars-url]: https://github.com/cchristou3/CyParking/stargazers

[issues-shield]: https://img.shields.io/github/issues/cchristou3/CyParking
[issues-url]: https://github.com/cchristou3/CyParking/issues

[license-shield]: https://img.shields.io/github/license/cchristou3/CyParking
[license-url]: https://github.com/othneildrew/Best-README-Template/blob/master/LICENSE.txt

[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=flat-square&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/cchristou1998/
