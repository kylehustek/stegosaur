# Stegosaur

Welcome to Stegosaur!

Stegosaur is a proof-of-concept QR Code x Image steganography application built to run on Android.
The app allows users to generate QR codes from text using the Zxing library, and then allows the user to hide this QR code and its information into an image.
An image can then be run through the decoding algorithm to retrieve the hidden information.

This project was created over the course a three week development phase, and was created with no prior knowledge of Android, Kotlin, or android native Java libraries.
The original design for this app was to employ a server backend database to keep track of users and their encrypted and decrypted images. This element was later removed in development beacuse limiting the app to on-device storage was both more feasible for current development and longevity of support, as well as for keeping the data more secure by removing the vulnerabilities of networking.

This project is no longer being updated. 

Contributors:

Kyle Hustek - All Kotlin code and XML for Android app function

Nick Harvey - The Encode and Decode Java scripts

