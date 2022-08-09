# image-tagging-demo
HEB Take home test

## Overview ##
Build a HTTP REST API in Java for a service that ingests user images,
analyzes them for object detection, and returns the enhanced content. It should implement the
following specification

## API Specification ##
* `GET /images`
  * Returns HTTP 200 OK with a JSON response containing all image metadata.
* `GET /images?objects="dog,cat"`
  * Returns a HTTP 200 OK with a JSON response body containing only images that have the detected objects specified in the query parameter.
* `GET /images/{imageId}`
  * Returns HTTP 200 OK with a JSON response containing image metadata for the specified image.
* `POST /images`
  * Send a JSON request body including an image file or URL, an optional label for the image, and an optional field to enable object detection.
  * Returns a HTTP 200 OK with a JSON response body including the image data, its label (generate one if the user did not provide it), its identifier provided by the persistent
  
  ## Object Detection ##
  This API uses Imagga's tagging API for image detection (https://docs.imagga.com/#tags). For the sake of simplicity, the number of tags returned is limited to 10 and the confidence must be over 40.
  The API key (stored in application.properties as `apiAuth`) is formatted as the value of the authorization header after the credentials have been Base64 encoded.
  Example: `apiAuth=Basic <IMAGGA_AUTH_VALUE_HERE>`
  
  ## Framework and Database ##
  This application is built using Spring Boot and using the H2 in-memory database to allow for rapid development and prototyping.
  
  ## TODO ##
  * Enhance REST API consumption by creating POJOs for the JSON received by Imagga.
  * Change H2 database to MySQL to have more permanent storage.
  * Enhance error handling while calling third party API.
  * Unit testing
