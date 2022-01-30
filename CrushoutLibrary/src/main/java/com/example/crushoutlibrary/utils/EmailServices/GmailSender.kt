package com.example.crushoutlibrary.utils.EmailServices

import android.util.Log
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.Security
import java.util.*
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class GmailSender(private val user: String, private val password: String) : Authenticator() {
    private val mailhost = "smtp.gmail.com"
    private val session: Session

    companion object {
        init {
            Security.addProvider(JsseProvider())
        }
    }

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(user, password)
    }

    @Synchronized
    fun sendMail(subject: String?, body: String, sender: String?, recipients: String) {
        try {
            val message = MimeMessage(session)
            val handler = DataHandler(ByteArrayDataSource(body.toByteArray(), "text/plain"))
            message.sender = InternetAddress(sender)
            message.subject = subject
            message.dataHandler = handler
            if (recipients.indexOf(',') > 0) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients))
            } else {
                message.setRecipient(Message.RecipientType.TO,  InternetAddress(recipients))
            }
            Transport.send(message)
        } catch (e: Exception) {
            Log.e("my_debuger", "sendMail Exception: $e")
        }
    }

    inner class ByteArrayDataSource : DataSource {
        var data: ByteArray
        var type: String? = null

        constructor(data: ByteArray, type: String?) {
            this.data = data
            this.type = type
        }

        constructor(data: ByteArray) {
            this.data = data
        }

        /**
         * This method returns an `InputStream` representing
         * the data and throws the appropriate exception if it can
         * not do so.  Note that a new `InputStream` object must be
         * returned each time this method is called, and the stream must be
         * positioned at the beginning of the data.
         *
         * @return an InputStream
         */
        override fun getInputStream(): InputStream {
            return ByteArrayInputStream(data)
        }

        /**
         * This method returns an `OutputStream` where the
         * data can be written and throws the appropriate exception if it can
         * not do so.  Note that a new `OutputStream` object must
         * be returned each time this method is called, and the stream must
         * be positioned at the location the data is to be written.
         *
         * @return an OutputStream
         */
        override fun getOutputStream(): OutputStream {
            throw IOException("Not Supported");
        }

        /**
         * This method returns the MIME type of the data in the form of a
         * string. It should always return a valid type. It is suggested
         * that getContentType return "application/octet-stream" if the
         * DataSource implementation can not determine the data type.
         *
         * @return the MIME Type
         */
        override fun getContentType(): String {
            return type ?: "application/octet-stream"
        }

        /**
         * Return the *name* of this object where the name of the object
         * is dependant on the nature of the underlying objects. DataSources
         * encapsulating files may choose to return the filename of the object.
         * (Typically this would be the last component of the filename, not an
         * entire pathname.)
         *
         * @return the name of the object.
         */
        override fun getName(): String {
            return "ByteArrayDataSource"
        }


    }

    init {
        val props = Properties()
        props.setProperty("mail.transport.protocol", "smtp")
        props.setProperty("mail.host", mailhost)
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.port", "465")
        props.put("mail.smtp.socketFactory.port", "465")
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory")
        props.put("mail.smtp.socketFactory.fallback", "false")
        props.setProperty("mail.smtp.quitwait", "false")
        session = Session.getDefaultInstance(props, this)
    }
}