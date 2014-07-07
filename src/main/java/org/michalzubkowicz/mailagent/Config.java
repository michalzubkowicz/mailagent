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

import java.util.Properties;


public class Config {
    private String host="localhost";
    private String user;
    private String password;
    private Integer port=25;

    public Config() {

    }

    public Config(String user,String password) {
        this.user=user;
        this.password=password;
    }

    public Config(String host,String user,String password, Integer port) {
        this.host=host;
        this.user=user;
        this.password=password;
        this.port=port;
    }



    public Properties getProperties() {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        try {
            properties.setProperty("mail.smtp.user", user);
            properties.setProperty("mail.smtp.password", password);
            properties.put("mail.smtp.auth", "true");
        } catch(NullPointerException e) {

        }

        switch(port) {
            case 587: properties.put("mail.smtp.starttls.enable", "true");
            case 465: {
                properties.put("mail.smtp.socketFactory.port", "465");
                properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
            }
        }

        return properties;
    }


}
