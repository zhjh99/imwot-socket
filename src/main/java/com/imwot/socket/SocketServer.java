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
package com.imwot.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imwot.socket.conf.Config;

/**
 * 〈一句话功能简述〉
 *
 * @author jinhong zhou
 */
public class SocketServer extends Thread {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * ServerSocket(NIO通道)
	 */
	private ServerSocketChannel serverSocketChannel;

	/**
	 * 多路复用器(注册消息)
	 */
	private Selector selector;

	/**
	 * 处理线程列表
	 */
	private ArrayList<ServerWorker> handlerList;

	/**
	 * 连接队列
	 */
	private BlockingQueue<SocketChannel> socketChannelQueue;

	/**
	 * 传输服务器配置
	 */
	private Config config;

	private static String newConnectionInfo = "有新连接";

	/**
	 * 是否启动
	 */
	private boolean isRunning = false;

	public SocketServer(Config config) throws Exception {
		this.config = config;
		handlerList = new ArrayList<ServerWorker>();
		socketChannelQueue = new LinkedBlockingQueue<SocketChannel>(config.getPoolSize() * 10);
		
		for (int i = 0; i < config.getPoolSize(); i++) {
			ServerWorker worker = new ServerWorker(config, socketChannelQueue);
			Thread thread = new Thread(worker);
			thread.start();
			handlerList.add(worker);
		}
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initServerSocketChannel();
	}

	/**
	 * 
	 * 初始化传输服务器
	 * 
	 * @throws Exception
	 *             void
	 * @exception/throws
	 */
	private void initServerSocketChannel() throws Exception {
		selector = Selector.open();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);

		serverSocketChannel.socket().bind(new InetSocketAddress(config.getPort()));
		logger.info("监听服务器(" + config.getName() + ")启动成功：" + "端口:" + config.getPort() + ",处理线程数:" + config.getPoolSize() + ",处理类:" + config.getClazz());

		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	/**
	 * 处理客户端连接
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		this.isRunning = true;
		while (isRunning) {
			try {
				selector.select(3000);
				Set<SelectionKey> readyKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = readyKeys.iterator();
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					iterator.remove();

					try {
						if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
							ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
							SocketChannel socketChannel = serverSocketChannel.accept();
							if (logger.isDebugEnabled()) {
								logger.info(newConnectionInfo);
							}
							socketChannelQueue.put(socketChannel);
						}
					} catch (IOException ex) {
						if (key != null) {
							logger.error("IO异常或客户端断开连接", ex);
							key.cancel();
							key.channel().close();
							key = null;
						}
					}
				}
			} catch (Exception e) {
				logger.warn(null, e);
			}
		}
	}
}