/*
 * Copyright (c) 2014. Michał Zubkowicz (michal.zubkowicz@gmail.com)
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

import javax.mail.MessagingException;
import javax.mail.Transport;
import java.util.Properties;

/**
 * Created by Michal Zubkowicz (michal.zubkowicz@gmail.com) on 01.07.14.
 */
public class SignedEmail extends Email {

    public SignedEmail(Properties properties) {
        super(properties);
    }

    public void send()  throws MessagingException {
        message.setContent(multipart);
        Transport.send(message);
    }

}


