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

A list of commonly used resources that I find helpful are listed in the acknowledgements.

### Built With
This section should list any major frameworks that you built your project using. Leave any add-ons/plugins for the acknowledgements section. Here are a few examples.
* [Android Java](https://developer.android.com/)
* [Firebase](https://firebase.google.com/)
* [Node.js](https://nodejs.org/en/)



<!-- GETTING STARTED -->
## Getting Started

This is an example of how you may give instructions on setting up your project locally.
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

<!-- USAGE EXAMPLES -->
## Usage

Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources.

_For more examples, please refer to the [Documentation](https://example.com)_



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

Distributed under the MIT License. See `LICENSE` for more information.



<!-- CONTACT -->
## Contact

Charalambos Christou - cchristou3@uclan.ac.uk

Project Link: [https://github.com/cchristou3/CyParking](https://github.com/cchristou3/CyParking)



<!-- ACKNOWLEDGEMENTS -->
## Acknowledgements
* [Volley](https://github.com/google/volley)
* [Gson](https://github.com/google/gson)
* [EventBus](https://github.com/greenrobot/EventBus)
* [Java Code Conventions](http://www.edparrish.net/common/javadoc.html#:~:text=How%20To%20Document%20and%20Organize%20Your%20Java%20Code,placement%20of%20curly%20braces.%20...%20More%20items...%20)
* [Cloud Firestore](https://firebase.google.com/docs/firestore/quickstart#:~:text=%20Create%20a%20Cloud%20Firestore%20database%20%201,getting%20started%20with...%204%20Click%20Done.%20More%20)
* [README Template](https://github.com/othneildrew/Best-README-Template)

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/forks/cchristou3/CyParking
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
