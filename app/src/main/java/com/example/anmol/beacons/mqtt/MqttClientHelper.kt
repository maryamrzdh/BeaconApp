package com.example.anmol.beacons.mqtt

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.anmol.beacons.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.io.InputStream
import java.security.KeyStore
import java.security.Security
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MqttClientHelper(context: Context?) {

    companion object {
        const val TAG = "MqttClientHelper"
    }

    var mqttAndroidClient: MqttAndroidClient
    val serverUri = SOLACE_MQTT_HOST
    private val clientId: String = MqttClient.generateClientId()
//    private val clientId: String = MqttAsyncClient.generateClientId()
//var persistence: MemoryPersistence? = MemoryPersistence()

    fun setCallback(callback: MqttCallbackExtended?) {
        mqttAndroidClient.setCallback(callback)
    }

    init {
        mqttAndroidClient = MqttAndroidClient(context, serverUri, clientId)
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.w(TAG, s)
            }

            override fun connectionLost(throwable: Throwable) {}
            @Throws(Exception::class)
            override fun messageArrived(
                topic: String,
                mqttMessage: MqttMessage
            ) {
                Log.w(TAG, mqttMessage.toString())
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {}
        })
        connect(context)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun connect(context:Context?) {

//        // SSL/TLS Setup
//// Get the BKS Keystore type required by Android
//        // SSL/TLS Setup
//// Get the BKS Keystore type required by Android
//        val trustStore: KeyStore = KeyStore.getInstance("BKS")
//// Read the BKS file we generated (droidstore.bks)
//// Read the BKS file we generated (droidstore.bks)
//        val input: InputStream = context.getResources().openRawResource(R.raw.droidstore)
//        trustStore.load(input, null)
//
//        val tmf: TrustManagerFactory =
//            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
//        tmf.init(trustStore)
//
//        val sslCtx: SSLContext = SSLContext.getInstance("TLS")
//        sslCtx.init(null, tmf.trustManagers, null)


        Security.addProvider(BouncyCastleProvider())

// Load CAs from an InputStream
        val cf = CertificateFactory.getInstance("X.509")
        val input: InputStream = context?.getResources()?.openRawResource(R.raw.ca)!!

//        var file = File("ca.crt");
//        var caIn = BufferedInputStream( FileInputStream(file))
        var ca :Certificate

        var sslCtx: SSLContext? = null
        try {
            // Convert crt to Certificate Java Object
            ca = cf.generateCertificate(input)

            // Create a KeyStore containing our trusted CAs
            val keyStoreType: String = KeyStore.getDefaultType()
            val keyStore: KeyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null, null)
            keyStore.setCertificateEntry("ca", ca)


            // Create a TrustManager that trusts the CAs in our KeyStore
            val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
            val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm)
            tmf.init(keyStore)

            val kmf: KeyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            kmf.init(keyStore, "rezareza".toCharArray())
            sslCtx = SSLContext.getInstance("TLS")
            sslCtx.init(kmf.keyManagers, tmf.trustManagers, null)
        }
        catch(e:Exception) {
            Log.e(TAG, "Error generating the certificate: $e")
        }
        finally {
            input.close()
        }





        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = SOLACE_CONNECTION_RECONNECT
        mqttConnectOptions.isCleanSession = SOLACE_CONNECTION_CLEAN_SESSION
//        mqttConnectOptions.userName = SOLACE_CLIENT_USER_NAME
//        mqttConnectOptions.password = SOLACE_CLIENT_PASSWORD.toCharArray()
        mqttConnectOptions.connectionTimeout = SOLACE_CONNECTION_TIMEOUT
        mqttConnectOptions.keepAliveInterval = SOLACE_CONNECTION_KEEP_ALIVE_INTERVAL

        val caCrtFile: InputStream = context?.getResources()?.openRawResource(R.raw.ca)!!
        val crtFile: InputStream = context?.getResources()?.openRawResource(R.raw.cert)!!
        val keyFile: InputStream = context?.getResources()?.openRawResource(R.raw.key)!!
//        mqttConnectOptions.socketFactory = getSSLSocketFactory(null, "")
//        mqttConnectOptions.socketFactory = sslCtx?.socketFactory

        mqttConnectOptions.socketFactory = getSocketFactory(caCrtFile, crtFile, keyFile, "")
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    val disconnectedBufferOptions =
                        DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    Log.w(TAG, "Failed to connect to: $serverUri ; $exception")
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    fun subscribe(subscriptionTopic: String, qos: Int = 0) {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.w(TAG, "Subscribed to topic '$subscriptionTopic'")
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    Log.w(TAG, "Subscription to topic '$subscriptionTopic' failed!")
                }
            })
        } catch (ex: MqttException) {
            System.err.println("Exception whilst subscribing to topic '$subscriptionTopic'")
            ex.printStackTrace()
        }
    }

    fun publish(topic: String, msg: String, qos: Int = 0) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            mqttAndroidClient.publish(topic, message.payload, qos, false)
            Log.d(TAG, "Message published to topic `$topic`: $msg")
        } catch (e: MqttException) {
            Log.d(TAG, "Error Publishing to $topic: " + e.message)
            e.printStackTrace()
        }

    }

    fun isConnected() : Boolean {
        return mqttAndroidClient.isConnected
    }

    fun destroy() {
        mqttAndroidClient.unregisterResources()
        mqttAndroidClient.disconnect()
    }
}