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
package com.imwot;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.imwot.socket.AbstractLog;
import com.imwot.socket.SocketServer;
import com.imwot.socket.conf.Config;
import com.imwot.socket.conf.ConfigList;
import com.imwot.socket.conf.XmlConfiguration;

/**
 * 启动(n个)传输服务器
 *
 * @author jinhong zhou
 */
public class Transfer extends AbstractLog {

	/**
	 * 配置列表
	 */
	private ConfigList configList;

	/**
	 * 
	 * 传入xml配置文件，可以启动(n个)传输服务器
	 * 
	 * @param xml
	 * @throws Exception
	 * 
	 */
	public Transfer(String xml) throws Exception {
		this.configList = new XmlConfiguration().parse(xml);
	}

	/**
	 * 
	 * 传入ConfigList配置，可以启动(n个)传输服务器
	 * 
	 * @param configList
	 * 
	 */
	public Transfer(ConfigList configList) {
		this.configList = configList;
	}

	/**
	 * 
	 * 传入Config配置，可以启动一个传输服务器
	 * 
	 * @param config
	 * 
	 */
	public Transfer(Config config) {
		this.configList = new ConfigList();
		List<Config> list = new ArrayList<Config>();
		list.add(config);
		configList.setList(list);
	}

	/**
	 * 
	 * 启动传输服务器
	 * 
	 * @throws Exception
	 *             void
	 * @exception/throws
	 */
	public void startServer() throws Exception {
		check(configList);
		for (int x = 0; x < configList.getList().size(); x++) {
			Config config = configList.getList().get(x);

			log.info("启动监听服务器(" + config.getName() + ")......");
			SocketServer server = new SocketServer(config);
			server.start();
		}
	}

	/**
	 * 
	 * 初步检查配置是否合理
	 * 
	 * @param configList
	 *            void
	 * @exception/throws
	 */
	private void check(ConfigList configList) {
		boolean error = false;
		String errorMsg = null;
		List<Config> list = configList.getList();
		for (int x = 0; x < list.size(); x++) {
			Config config = list.get(x);
			if (config.getPort() <= 0) {
				error = true;
				errorMsg = "端口设置错误";
				break;
			} else if (config.getPoolSize() <= 0 || config.getPoolSize() > 2000) {
				error = true;
				errorMsg = "线程数设置错误,请设置为1到2000之间";
				break;
			} else if (StringUtils.isEmpty(config.getClazz())) {
				error = true;
				errorMsg = "class接口实现设置错误";
				break;
			}
		}

		if (error) {
			throw new RuntimeException("服务器参数错误-" + errorMsg);
		}
	}
}
