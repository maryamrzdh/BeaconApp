package com.example.anmol.beacons.mqtt

import android.os.Build
import androidx.annotation.RequiresApi
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.eclipse.paho.client.mqttv3.MqttSecurityException
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.security.*
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*


@Throws(Exception::class)
fun getSocketFactory(
    caCrtFile: InputStream?, crtFile: InputStream?, keyFile: InputStream?,
    password: String
): SSLSocketFactory? {
    Security.addProvider(BouncyCastleProvider())

    // load CA certificate
    var caCert: X509Certificate? = null
    var bis = BufferedInputStream(caCrtFile)
    val cf: CertificateFactory = CertificateFactory.getInstance("X.509")


    while (bis.available() > 0) {
        caCert = cf.generateCertificate(bis) as X509Certificate
    }

    // load client certificate
    bis = BufferedInputStream(crtFile)
    var cert: X509Certificate? = null
    while (bis.available() > 0) {
        cert = cf.generateCertificate(bis) as X509Certificate
    }

    // load client private cert
    val pemParser = PEMParser(InputStreamReader(keyFile))
    val `object`: Any = pemParser.readObject()
    val converter: JcaPEMKeyConverter = JcaPEMKeyConverter().setProvider("BC")
    val key: KeyPair = converter.getKeyPair(`object` as PEMKeyPair)
    val caKs = KeyStore.getInstance(KeyStore.getDefaultType())
    caKs.load(null, null)
    caKs.setCertificateEntry("cert-certificate", caCert)
    val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    tmf.init(caKs)
    val ks = KeyStore.getInstance(KeyStore.getDefaultType())
    ks.load(null, null)
    ks.setCertificateEntry("certificate", cert)

//    ks.setKeyEntry(
//        "private-cert",
//        key.private,
//        password.toCharArray(),
//        arrayOf<java.security.cert.Certificate>(cert)
//    )
    val kmf: KeyManagerFactory =
        KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
    kmf.init(ks, password.toCharArray())
    val context = SSLContext.getInstance("TLSv1.2")
    context.init(kmf.getKeyManagers(), tmf.trustManagers, null)
    return context.socketFactory
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Throws(MqttSecurityException::class)
fun getSSLSocketFactory(keyStore: InputStream?, password: String): SSLSocketFactory? {
    return try {
        var ctx: SSLContext? = null
        var sslSockFactory: SSLSocketFactory? = null
        val ks: KeyStore
        ks = KeyStore.getInstance("PKCS12")
        ks.load(keyStore, password.toCharArray())
        val tmf = TrustManagerFactory.getInstance("X509")
        tmf.init(ks)
        val tm: Array<TrustManager> = tmf.trustManagers
        ctx = SSLContext.getInstance("TLS")
        ctx.init(null, tm, null)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
//            sslSockFactory = TLSSocketFactory(tm)
        } else {
            sslSockFactory = ctx.socketFactory
        }
        sslSockFactory
    }
//    catch (e: KeyStoreException) {
//        throw MqttSecurityException(e)
//    }

    catch (e: CertificateException) {
        throw MqttSecurityException(e)
    } catch (e: IOException) {
        throw MqttSecurityException(e)
    } catch (e: NoSuchAlgorithmException) {
        throw MqttSecurityException(e)
    } catch (e: KeyManagementException) {
        throw MqttSecurityException(e)
    }
}