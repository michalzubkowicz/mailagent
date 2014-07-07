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

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import junit.framework.TestCase;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


public class EmailTest extends TestCase {
    private SimpleSmtpServer server = SimpleSmtpServer.start(9999);
    private Email e;

    public void setUp() throws Exception {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.port", "9999");
        e = new Email(properties);
        super.setUp();

    }

    public void testSend() throws Exception {
        String emailTo = "testemail@dupa.com";
        e.addCC(emailTo);
        e.addBCC(emailTo);
        String emailFrom = "testemail@dupa.com";
        e.addBCC(emailFrom);
        e.addAttachmentInline("<ajaxloader>",getClass().getResource("/ajax-loader.gif").getPath());
        List<String> attachments = new ArrayList<String>();
        attachments.add(getClass().getResource("/blank.png").getPath());
        attachments.add(getClass().getResource("/śźćłóżąę.png").getPath());
        e.send(emailTo, emailFrom,"test email","<b>Test body</b> See this cool pic:<img src=\"cid:ajaxloader\" alt=\"\">",attachments);
        e.setFrom("testemail@dupa.com","MZU");
        server.stop();
        assertTrue(server.getReceivedEmailSize() == 1);
        Iterator emailIter = server.getReceivedEmail();
        SmtpMessage email = (SmtpMessage)emailIter.next();
        assertTrue(email.getHeaderValue("Subject").equals("test email"));
        assertTrue(email.getHeaderValue("From").contains("MZU"));
        //System.out.println(email.getBody().toString());
        assertTrue(email.getBody().contains("<ajaxloader>Content-Disposition: inline"));
        assertTrue(email.getBody().contains("Content-Type: image/png; name=blank.pngContent-Transfer-Encoding: base64Content-Disposition: attachment; filename=blank.png"));
        assertTrue(email.getBody().contains(URLEncoder.encode("śźćłóżąę.png","UTF-8")));

        assertTrue(email.getBody().contains("Test body"));
    }
}
