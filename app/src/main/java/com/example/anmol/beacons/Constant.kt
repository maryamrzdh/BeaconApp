package com.example.anmol.beacons

// Solace PubSub+ Broker Options

// Fill in your Solace Cloud PubSub+ Broker's 'MQTT Host' and 'Password' options.
// This information can be found under:
// https://console.solace.cloud/services/ -> <your-service> -> 'Connect' -> 'MQTT'
const val SOLACE_CLIENT_USER_NAME = "root@193.151.140.107"
const val SOLACE_CLIENT_PASSWORD = "rezareza"
//const val SOLACE_MQTT_HOST = "tcp://PLACEHOLDER_SERVICE_ID.messaging.solace.cloud:1883"
//const val SOLACE_MQTT_HOST = "https://mosquitto.org"
const val SOLACE_MQTT_HOST = "https://193.151.140.107:8883/"

// Other options
const val SOLACE_CONNECTION_TIMEOUT = 3
const val SOLACE_CONNECTION_KEEP_ALIVE_INTERVAL = 60
const val SOLACE_CONNECTION_CLEAN_SESSION = true
const val SOLACE_CONNECTION_RECONNECT = true