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
package org.michalzubkowicz.mailagent.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;

/**
 *
 * @author Robert Skubij <rober@skubij.pl>
 */
public class BouncySign {

    private static BouncySign bouncySignInstance = null;

    private String key;
    private String keyinstance = "PKCS12";  // Default #PKCS
    private String keypassword;
    private String certalias;

    private  SMIMESignedGenerator genn;
    private  PrivateKey privatekey;
    private  CertStore certscrls;
    private  X509Certificate certtosign;
    private  KeyStore keystore;
    
    private BouncySign(){
        
    }

    private void prepareSinger() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, InvalidAlgorithmParameterException, NoSuchProviderException, CertStoreException, SMIMEException{
        
        Security.addProvider(new BouncyCastleProvider());

        keystore = KeyStore.getInstance(bouncySignInstance.getKeyinstance());

        keystore.load(new FileInputStream(bouncySignInstance.getKey()), bouncySignInstance.getKeypassword().toCharArray());

        List<Certificate> certList = new ArrayList<>();

        Enumeration<String> aliases = keystore.aliases();

        while (aliases.hasMoreElements()) {

            String alias = aliases.nextElement();

            Certificate cert = keystore.getCertificate(alias);

            if (cert != null) {
                certList.add(cert);
            }

        }

        privatekey = (PrivateKey) keystore.getKey(certalias, keypassword.toCharArray());

        certtosign = (X509Certificate) keystore.getCertificate(certalias);

        certscrls = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList), "BC");

        genn = new SMIMESignedGenerator();

        genn.addSigner(privatekey, (X509Certificate) certtosign, SMIMESignedGenerator.DIGEST_SHA1);

        genn.addCertificatesAndCRLs(certscrls);
    }
    
    public static BouncySign makeBouncy(String key, String password, String keyinstance, String certalias) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, InvalidAlgorithmParameterException, NoSuchProviderException, CertStoreException, SMIMEException {

        if (bouncySignInstance != null) {
            return bouncySignInstance;
        }

        bouncySignInstance = new BouncySign();
        bouncySignInstance.setKey(key);
        bouncySignInstance.setKeypassword(password);
        bouncySignInstance.setKeyinstance(keyinstance);
        bouncySignInstance.setCertalias(certalias);
        bouncySignInstance.prepareSinger();

        return bouncySignInstance;

    }

    public SMIMESignedGenerator getGenn() {
        return genn;
    }

    public void setGenn(SMIMESignedGenerator genn) {
        this.genn = genn;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyinstance() {
        return keyinstance;
    }

    public void setKeyinstance(String keyinstance) {
        this.keyinstance = keyinstance;
    }

    public String getKeypassword() {
        return keypassword;
    }

    public void setKeypassword(String keypassword) {
        this.keypassword = keypassword;
    }

    public String getCertalias() {
        return certalias;
    }

    public void setCertalias(String certalias) {
        this.certalias = certalias;
    }

}
