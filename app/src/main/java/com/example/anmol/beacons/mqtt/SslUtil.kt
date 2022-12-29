package com.example.anmol.beacons.mqtt

import android.os.Build
import androidx.annotation.RequiresApi
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.x509.X509V2AttributeCertificate
import java.io.InputStream
import java.io.InputStreamReader
import java.security.KeyPair
import java.security.KeyStore
import java.security.Security
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory


object SslUtil {
    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun getSocketFactory(
//        caCrtFile: String?, crtFile: String?, keyFile: String?,
        caCrtFile: InputStream?, crtFile: InputStream?, keyFile: InputStream?,
        password: String
    ): SSLSocketFactory {
        Security.addProvider(BouncyCastleProvider())

        // load CA certificate
        var reader =
//            PEMParser(InputStreamReader(ByteArrayInputStream(Files.readAllBytes(Paths.get(caCrtFile)))))
            PEMParser(InputStreamReader(caCrtFile))
//        val caCert = reader.readObject() as X509Certificate
//        val caCert = X509V2AttributeCertificate((reader.readObject() as X509Certificate).encoded);

        val certFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
//        val `in`: InputStream = ByteArrayInputStream(certificateHolder.getEncoded())
        val caCert = certFactory.generateCertificate(caCrtFile) as X509Certificate
        reader.close()

        // load client certificate
        reader =
//            PEMParser(InputStreamReader(ByteArrayInputStream(Files.readAllBytes(Paths.get(crtFile)))))
            PEMParser(InputStreamReader(crtFile))
//        val cert = reader.readObject() as X509Certificate
//        val cert = certFactory.generateCertificate(crtFile) as X509Certificate


        val cert: X509Certificate
        try {
            cert = certFactory.generateCertificate(crtFile) as X509Certificate// error at this line
//            println("ca=" + (cert as X509Certificate).subjectDN)
//            val cert = certFactory.generateCertificate(crtFile)


        } finally {
//            caInput.close()
        }


        reader.close()

        // load client private key
        reader = PEMParser(
//            InputStreamReader(ByteArrayInputStream(Files.readAllBytes(Paths.get(keyFile))))
            InputStreamReader(keyFile)
//            ,PasswordFinder { password.toCharArray() }
        )
//        val key = reader.readObject() as KeyPair
        val pemParser = PEMParser(InputStreamReader(keyFile))
        val `object`: Any = pemParser.readObject()
        val converter: JcaPEMKeyConverter = JcaPEMKeyConverter().setProvider("BC")
        val key: KeyPair = converter.getKeyPair(`object` as PEMKeyPair)

        reader.close()

        // CA certificate is used to authenticate server
        val caKs = KeyStore.getInstance(KeyStore.getDefaultType())
        caKs.load(null, null)
        caKs.setCertificateEntry("ca-certificate", caCert)
        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(caKs)

        // client key and certificates are sent to server so it can authenticate us
        val ks = KeyStore.getInstance(KeyStore.getDefaultType())
        ks.load(null, null)
        ks.setCertificateEntry("certificate", cert)
        ks.setKeyEntry(
            "private-key",
            key.private,
            password.toCharArray(),
            arrayOf<Certificate>(cert)
        )
//        val kmf = KeyManagerFactory.getInstance("SunX509")

        val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        kmf.init(ks, password.toCharArray())

        // finally, create SSL socket factory
        val context = SSLContext.getInstance("TLSv1")
        context.init(kmf.keyManagers, tmf.trustManagers, null)
        return context.socketFactory
    }
}