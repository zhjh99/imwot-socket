/**
 [The "BSD license"]
 Copyright (c) 2013-2018 jinhong zhou (周金红)
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.imwot.middleware.socket.conf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 传输文件配置解析
 *
 * @author jinhong zhou
 */
public class XmlConfiguration {

	/**
	 * 
	 * 解析配置文件
	 * 
	 * @param xmlName
	 * @return
	 * @throws Exception
	 *             ConfigList
	 * @exception/throws
	 */
	public ConfigList parse(String xmlName) throws Exception {
		ConfigList configList = new ConfigList();
		List<Config> conList = new ArrayList<Config>();
		Document doc = readXMLFile(new File(xmlName));
		NodeList list = doc.getElementsByTagName("config");

		for (int i = 0; i < list.getLength(); i++) {
			Element element = (Element) list.item(i);

			String name = element.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
			String port = element.getElementsByTagName("port").item(0).getFirstChild().getNodeValue();
			String poolSize = element.getElementsByTagName("poolSize").item(0).getFirstChild().getNodeValue();
			String clazz = element.getElementsByTagName("clazz").item(0).getFirstChild().getNodeValue();

			Config config = new Config();
			config.setName(name);
			config.setPort(Integer.parseInt(port));
			config.setPoolSize(Integer.parseInt(poolSize));
			config.setClazz(clazz);

			conList.add(config);

		}

		configList.setList(conList);
		return configList;
	}

	/**
	 * 
	 * 文件转document
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 *             Document
	 * @exception/throws
	 */
	private Document readXMLFile(File file) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(file);
	}
}
