/*
 * Copyright (c) 2013. Michał Zubkowicz (michal.zubkowicz@gmail.com)
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

import org.jsoup.Jsoup;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;


public class Email {
    protected final Message message;
    protected Multipart multipart = new MimeMultipart("mixed");

    public Email(Properties properties) {

        Session session;
        if(Boolean.valueOf((String) properties.get("mail.smtp.auth"))) {
            final String username = (String) properties.get("mail.smtp.user");
            final String password = (String) properties.get("mail.smtp.password");
            session = Session.getDefaultInstance(properties,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });
        } else {
            session = Session.getDefaultInstance(properties);
        }
        message = new MimeMessage(session);
    }


    public void setFrom(String email) throws MessagingException {
        message.setFrom(new InternetAddress(email));
    }

    public void setFrom(String email, String name) throws MessagingException,UnsupportedEncodingException {
        message.setFrom(new InternetAddress(email, name));
    }

    public void addTo(String email) throws MessagingException  {
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
    }

    public void addCC(String email) throws MessagingException  {
        message.addRecipient(Message.RecipientType.CC, new InternetAddress(email));
    }

    public void addBCC(String email) throws MessagingException  {
        message.addRecipient(Message.RecipientType.BCC, new InternetAddress(email));
    }

    public void addReplyTo(String email) throws MessagingException  {
        message.setReplyTo(new javax.mail.Address[]
                {
                        new javax.mail.internet.InternetAddress(email)
                });
    }

    public void setSubject(String subject) throws MessagingException {
        message.setSubject(subject);
    }

    public void setHtmlBody(String body)  throws MessagingException {
        message.setText(Jsoup.parse(body).text());
        message.setContent(body, "text/html; charset=utf-8");
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(body, "text/html; charset=utf-8");
        multipart.addBodyPart(messageBodyPart);
    }

    public void addAttachment(String path) throws IOException, MessagingException {
        MimeBodyPart mbp = new MimeBodyPart();
        mbp.attachFile(URLDecoder.decode(path,"UTF-8"));
        Path p = Paths.get(URLDecoder.decode(path,"UTF-8"));
        mbp.setFileName(URLEncoder.encode(p.getFileName().toString(),"UTF-8"));
        multipart.addBodyPart(mbp);
    }

    public void addAttachmentInline(String contentid, String path) throws MessagingException {
        if(!contentid.startsWith("<")) contentid="<"+contentid;
        if(!contentid.endsWith(">")) contentid=contentid+">";
        BodyPart mbp = new MimeBodyPart();
        FileDataSource fileDs = new FileDataSource(path);
        mbp.setDataHandler(new DataHandler(fileDs));
        mbp.setHeader("Content-ID", contentid);
        mbp.setDisposition(MimeBodyPart.INLINE);
        multipart.addBodyPart(mbp);
    }


    public void addAttachments(List<String> al) throws IOException, MessagingException {
        for(String a : al) {
            this.addAttachment(a);
        }
    }

    public void send(String to, String from, String subject, String htmlbody) throws MessagingException, IOException {
        this.setFrom(from);
        this.addTo(to);
        this.setSubject(subject);
        this.setHtmlBody(htmlbody);
        this.send();
    }

    public void send(String to, String from, String subject, String htmlbody, List<String> attachmentList) throws MessagingException, IOException {
        this.setFrom(from);
        this.addTo(to);
        this.setSubject(subject);
        this.setHtmlBody(htmlbody);
        this.addAttachments(attachmentList);
        this.send();
    }

    public void send()  throws MessagingException {
        message.setContent(multipart);
        Transport.send(message);
    }
}
