# WienerScreener - https://wiener.world
This is an Android application that takes a picture and utilizes a neural network to determine whether or not the picture is of a hot dog.

## How it works
We trained a neural network using TensorFlow machine learning algorithms with thousands of images defined as either 'wiener' or 'not wiener' until it could successfully identify hot dogs.

An Android application was created so users could take photos and have them evaluated for hot dog status using the data from the trained neural network.

To accomplish this, a web server was created using Node.js and Docker to handle user requests and process uploaded images.

The domain https://wiener.world was purchased in order to establish a proper user experience.
ImageMagick was then used to overlay a response image to the client that displays 'wiener' or 'not wiener'.

All of the files in the server directory were created in a Linux Virtual Machine environment, which we then uploaded into a Google Cloud server, which currently hosts and runs all of the back-end functionalities.

## Built With
* [TensorFlow](https://www.tensorflow.org/) - an open-source machine learning framework with python libraries

* [Google Cloud Platform Compute Engine](https://cloud.google.com/) - virtual machines/servers running in Google data centers

* [Android Studio](https://developer.android.com/studio/index.html) - the official integrated development environment (IDE) for Google's Android operating system in which the Java code for the Android application was written.

* [Docker](https://www.docker.com/) - an open-source project for automating the deployment of applications as portable, self-sufficient containers that can run on the cloud or on-premises.

* [Node.js](https://nodejs.org/en/) - a JavaScript runtime built on [Chrome's V8 JavaScript engine](https://developers.google.com/v8/), specifically the [Express framework](https://expressjs.com/) to create a web server on the Google Cloud server that would handle the front-end application requests.

* [ImageMagick](https://www.imagemagick.org/script/index.php) - an open-source image manipulation tool that was used to create overlays displaying the wiener status of a photo.

* [Caddy](https://caddyserver.com/) - a web server and proxy which automatically generates https certificates
