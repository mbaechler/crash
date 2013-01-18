/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2011-2012  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package org.crsh.guice;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Proxy;
import java.net.URL;

import org.apache.commons.net.telnet.TelnetClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.StringContains;
import org.junit.runner.RunWith;

import sun.net.www.protocol.http.HttpURLConnection;

@RunWith(Arquillian.class)
public class TestCrashGuiceSupportWithServletIntegration {

	private TelnetClient telnetClient;


	@Before
	public void setup() {
		telnetClient = new TelnetClient();
	}

	@After
	public void teardown() throws IOException {
		telnetClient.disconnect();
	}
	
	@Test
	@RunAsClient
	public void testCrashPrintPropertyAfterAJump(@ArquillianResource URL baseURL) throws IOException {
		HttpURLConnection httpURLConnection = new HttpURLConnection(new URL(baseURL.toExternalForm() + "?howHigh=5"), Proxy.NO_PROXY);
		httpURLConnection.connect();
		httpURLConnection.getContent();
		
		telnetClient.connect("localhost", 5000);

		InputStream in = telnetClient.getInputStream();
		PrintStream out = new PrintStream(telnetClient.getOutputStream());
		TelnetHelper.readUntil("% ", in);
		out.println("guice print -p counter org.crsh.guice.SampleService");
		out.flush();
		String response = TelnetHelper.readUntil("% ", in);
		Assert.assertThat(response, StringContains.containsString("{5=1}"));
	}
	

	@Deployment
	public static WebArchive createDeployment() {
		return GuiceTestWebAppArchive.buildInstance();
	}
}
