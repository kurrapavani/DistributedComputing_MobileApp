# Build a hybrid mobile app that can capture an image, recognize and translate text using Tesseract OCR & Watson Language Translator

In this Code Pattern, we will create a hybrid mobile app using Apache Cordova and Node.js server application running on IBM Cloud Kubernetes service that uses Tesseract OCR to recognize text in images, Watson Language Translator to translate the recognized text  from the text. This mobile app translates the recognized text from the images captured or uploaded from the photo album.

When the reader has completed this Code Pattern, they will understand how to:

* Use the Cordova mobile framework to build and deploy mobile app.
* Create Node.js apps that capture, recognize and translate text using Watson services.
* Deploy Tesseract OCR on IBM Cloud Kubernetes service to recognize text and bind Watson service to cluster.
* Translate recognized text using Watson Language Translator.


## Flow
1. The user interacts with the mobile app and captures an image or selects an image from the photo album.
2. The image is passed to the Node.js server application that is running on IBM Cloud Kubernetes service which uses Tesseract OCR to recognize text in an image.
3. Node.js app uses Watson language translator service to translate the recognized text and Watson Natural Language Understanding to return the sentiment & emotion of the translated text.
4. Recognized text,translated language, result is returned to the mobile app for display.

## Included components
* [IBM Cloud Kubernetes Service](https://cloud.ibm.com/docs/containers/container_index.html): IBM Cloud Kubernetes Service manages highly available apps inside Docker containers and Kubernetes clusters on the IBM Cloud.
* [Watson Language Translator](https://www.ibm.com/watson/services/language-translator/): IBM Watson Language Translator is a service that enables you to dynamically translate news, patents or conversational documents.
*

## Featured technologies
* [Apache Cordova](https://cordova.apache.org/): An open-source mobile development framework to build hybrid mobile apps.
* [Node.js](https://nodejs.org/): An open-source JavaScript run-time environment for executing server-side JavaScript code.
* [Tesseract OCR](https://github.com/tesseract-ocr/): An open-source Optical Character Recognition(OCR) engine.


# Steps

This Code Pattern contains several pieces. The Node.js server application running on IBM Cloud Kubernetes service communicates with the Tesseract OCR, Watson Language Translator. Mobile application is built locally and runs on the Android/iOS phone.


1. [Clone the repo](#1-clone-the-repo)
2. [Run the server application in a container on IBM Cloud with Kubernetes](#3-run-the-server-application-in-a-container-on-ibm-cloud-with-kubernetes)
3. [Run the server application locally using docker](#4-run-the-server-application-locally-using-docker)
4. [Run the mobile application](#5-run-the-mobile-application)

>NOTE: Either run step 2 (deploying to IBM cloud using Kubernetes) or step 3 (running locally)

## 1. Clone the repo

Clone the `DistributedComputing_MobileApp` repo locally. In a terminal, run:

```
$ git clone (https://github.com/kedarSedai/DistributedComputing_MobileApp.git)
$ cd DistributedComputing_MobileApp
```

## 2. Run the server application in a container on IBM Cloud with Kubernetes

Steps below will help you to deploy the `DistributedComputing_MobileApp/server` application into a container running on IBM Cloud, using Kubernetes.

Install the [pre-requisites](https://github.com/IBM/container-service-getting-started-wt/tree/master/Lab%200) before you begin with the steps.

### Steps

* Follow the instructions to [Create a Kubernetes Cluster,Setup CLI, Setup Private registry and to set up your cluster environment](https://cloud.ibm.com/docs/containers/cs_tutorials.html#cs_cluster_tutorial).

* Set the Kubernetes environment to work with your cluster:

```
$ ibmcloud cs cluster-config <replace_with_your_cluster_name>
```

The output of this command will contain a KUBECONFIG environment variable that must be exported in order to set the context. Copy and paste the output in the terminal window. An example is:

```
$ export KUBECONFIG=/Users/VisusalVerse/.bluemix/plugins/container-service/clusters/<cluster_name>/kube-config-hou02-<cluster_name>.yml
```

* Add Language Translator to your cluster

Add the Language Translator to your IBM Cloud account by replacing with a name for your service instance.

```
$ ibmcloud service create language_translator lite <service_name>
```

* Bind the Language Translator to the default Kubernetes namespace for the cluster. Later, you can create your own namespaces to manage user access to Kubernetes resources, but for now, use the default namespace. Kubernetes namespaces are different from the registry namespace you created earlier. Replace cluster name and service instance name.

> NOTE: when services are bound you will see a secret name for each, take note of these as we'll need them later.

```
$ ibmcloud cs cluster-service-bind --cluster <cluster_name> --namespace default --service <language_translate_service_name>
```

Your cluster is configured and your local environment is ready for you to start deploying apps into the cluster.

* Build a Docker image that includes the app files from `DistributedComputing_MobileApp/server` directory, and push the image to the IBM Cloud Container Registry namespace that you created. Replace `<ibmcloud_container_registry_namespace>` with IBM Cloud Container Registry namespace.

```
$ docker build -t registry.ng.bluemix.net/<ibmcloud_container_registry_namespace>/watsontesseract:1 .
```

* Push the image to IBM Cloud Container registry

```
$ docker push registry.ng.bluemix.net/<ibmcloud_container_registry_namespace>/watsontesseract:1
```

* Update [`watson-lang-trans.yml`](server/watson-lang-trans.yml) with your specific values:

  * Replace `<namespace>` with your IBM Cloud Container Registry namespace
  * Replace `<binding-ocrlangtranslator>` with the secret name from the bound Language Translator service

* Run the configuration script.

```
$ kubectl apply -f watson-lang-trans.yml
```

* Get the public IP address by replacing the <cluster_name>. (Take a note of the Public IP address since it is required in the later steps) 

```
$ ibmcloud cs workers <cluster_name>
```

## 3. Run the server application locally using docker

### 3a. Create language translation with IBM Cloud

If you do not already have a IBM Cloud account, [sign up for IBM Cloud](https://cloud.ibm.com/registration).
Create the following services:

* [**Watson Language Translator**](https://cloud.ibm.com/catalog/services/language-translator)

Go to the `Service Credentials` tab for each service and save the `API Key` and `URL` of each service for later use.

### 3b. Copy the `env.sample` to `.env` and replace the `IAM API` key and the `URL` that you got when you created the Watson language translation service. From terminal run:

```
$ cp env.sample .env

# Copy this file to .env and replace the credentials with
# your own before starting the app.

LANGUAGE_TRANSLATOR_IAM_APIKEY=<use iam apikey here>
LANGUAGE_TRANSLATOR_URL=<use url here>

```	

### 3c. Go to `server` folder and run the docker build. 

* Build the `DistributedComputing_MobileApp-server`. From terminal run: 

```
$ cd server
$ docker build -t snap-translate-server .
```
* Run the docker image. This will run the server app on port 3000. From terminal run:
```
docker run --rm -it -p 3000:3000 DistributedComputing_MobileApp-server
```
* You can now access the `server` API using URL: `http://localhost:3000`

## 4. Run the mobile application

Steps below will help you to deploy the `DistributedComputing_MobileApp/mobile` mobile application.

### 1. Update config values for the Mobile App

Edit `mobile/www/config.json` and update the setting with the Public IP address and NODE PORT( nodePort from `DistributedComputing_MobileApp/server/watson-lang-trans.yml`) retrieved previously.

```
"SERVER_URL": "http://<replace_public_ip_address>:<replace_node_port>/uploadpic"
```

* **Accessing local API from mobile app**

To access the local API from the mobile app, we can use a tool called `ngrok`. you can install `ngrok` using the link: `https://ngrok.com/`

After the docker build is running you can now run the `ngrok` command from the place where you have installed it. If `ngrok` is installed at location: `/usr/local/ngrok`, from terminal run:

```
$ ./usr/local/ngrok http 3000
```

This will bind your local address to a public address at port 3000. Now you can replace the `SERVER_URL` to the `forwarding address` that can be retrieved from the screenshot above. As an example the `SERVER-URL` will now be:

```
"SERVER_URL": "http://b336a2d7.ngrok.io/uploadpic"
```

```

### 3. Add Android/iOS platform and plug-ins

Start by adding the Android/iOS platform as the target for your mobile app.

```
$ cd snap-and-translate/mobile
$ cordova platform add android
$ cordova platform add ios@5.0.0
```

Ensure that everything has been installed correctly:

```
$ cordova requirements
```

Finally, install the plugins required by the application:

```
$ cordova plugin add cordova-plugin-camera
$ cordova plugin add cordova-plugin-file-transfer
```

### 4. Build the App

Run the following command to build the project for all platforms:

```
$ cordova build
```

### 5. Run the App

Plug the mobile into your computer/laptop using USB cable and test the app directly by executing the command:

> Open the `TranslateIt.xcworkspace` from `mobile/platforms/ios` folder and code sign using Xcode.

```
$ cordova run android (if you have android device)
$ cordova run ios (if you have iOS device)
```

