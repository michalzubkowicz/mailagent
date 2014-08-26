/*
 * Copyright (c) 2014. Robert Skubij <robert@skubij.pl>
 *
 * This file is part of some open source application.
 *
 * Some open source application is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * Some open source application is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * with this appliction.  If not, see <http://www.gnu.org/licenses/>.
 *  *
 * @license GPL-3.0+ <http://spdx.org/licenses/GPL-3.0+>
 */
package org.michalzubkowicz.mailagent;

import org.bouncycastle.mail.smime.SMIMEException;
import org.michalzubkowicz.mailagent.tools.BouncySign;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateException;
import java.util.Properties;

/**
 * Created by Robert Skubij <robert@skubij.pl> on 24.08.14.
 */
public class SignedEmail extends Email {

    private final BouncySign mailSigner;

    public SignedEmail(Properties properties) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, InvalidAlgorithmParameterException, NoSuchProviderException, CertStoreException, SMIMEException {

        super(properties);

        String key = properties.getProperty("bouncy-key");
        String keyinstance = properties.getProperty("bouncy-keyinstance");
        String password = properties.getProperty("bouncy-password");
        String certalias = properties.getProperty("bouncy-certalias");

        mailSigner = BouncySign.makeBouncy(key, password, keyinstance, certalias);

    }

    @Override
    public void send() throws MessagingException,NoSuchAlgorithmException,  NoSuchProviderException, SMIMEException {
            MimeBodyPart bodyPartToSign = new MimeBodyPart();
            bodyPartToSign.setContent(multipart);
            MimeMultipart mp = mailSigner.getGenn().generate(bodyPartToSign, "BC");
            message.setContent(mp);
            Transport.send(message);
    }

}
