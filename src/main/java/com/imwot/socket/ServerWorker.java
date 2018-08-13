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

import java.lang.reflect.Constructor;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

import com.imwot.socket.conf.Config;

/**
 * 〈一句话功能简述〉
 *
 * @author jinhong zhou
 */
public class ServerWorker extends AbstractLog implements Runnable {

	/**
	 * 处理接口
	 */
	private AbstractProcess transfer;

	/**
	 * 多路复用器(注册读消息)
	 */
	private Selector readSelector;

	/**
	 * 多路复用器(注册写消息)
	 */
	private Selector writeSelector;

	/**
	 * Socket(NIO通道)
	 */
	private SocketChannel clientChannel;

	/**
	 * 读事件key
	 */
	private SelectionKey readKey;

	/**
	 * 写事件key
	 */
	private SelectionKey writeKey;

	/**
	 * 传输服务器配置
	 */
	private Config config;

	/**
	 * 连接队列
	 */
	private BlockingQueue<SocketChannel> socketChannelQueue;

	public ServerWorker(Config config, BlockingQueue<SocketChannel> socketChannelQueue, Object o) throws Exception {
		this.config = config;
		this.socketChannelQueue = socketChannelQueue;

		Class<?> clazz = Class.forName(this.config.getClazz());
		Constructor<?> constructor = clazz.getConstructor(Object.class, ICallBack.class);
		constructor.setAccessible(true);
		transfer = (AbstractProcess) constructor.newInstance(o, callBack);
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (this.clientChannel == null || (this.clientChannel != null && this.clientChannel.socket().isClosed())) {
					addNewSocketChannel();
				}
				process();
			}
		} catch (Exception e) {
			log.warn(null, e);
		}
	}

	/**
	 * 
	 * 传输处理
	 * 
	 * void
	 * 
	 * @exception/throws
	 */
	private void process() {
		try {
			readSelector.select(3000);
			try {
				if ((readKey.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
					transfer.call(this.clientChannel, writeSelector, readSelector);
				}
			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.warn("IO异常或客户端断开连接");
				}
				closeSocket();
			}
		} catch (Exception e) {
			log.warn(null, e);
		}
	}

	/**
	 * 
	 * 处理新的连接
	 * 
	 * void
	 * 
	 * @exception/throws
	 */
	private void addNewSocketChannel() {
		try {
			this.clientChannel = socketChannelQueue.take();
			if (clientChannel != null) {
				try {
					if (readKey != null) {
						readKey.cancel();
					}
					if (writeKey != null) {
						writeKey.cancel();
					}
				} catch (Exception e) {
					log.warn(null, e);
				}

				readSelector = Selector.open();
				writeSelector = Selector.open();

				clientChannel.configureBlocking(false);
				readKey = clientChannel.register(readSelector, SelectionKey.OP_READ);

			}
		} catch (Exception e) {
			log.warn(null, e);
		}
	}

	/**
	 * 
	 * 关闭
	 * 
	 * @return boolean
	 * @exception/throws
	 */
	public boolean closeSocket() {
		boolean result = false;
		try {
			if (readKey != null) {
				readKey.cancel();
				readKey = null;
				readSelector.selectNow();
			}
			if (writeKey != null) {
				writeKey.cancel();
				writeKey = null;
				writeSelector.selectNow();
			}
			if (clientChannel != null) {
				clientChannel.socket().close();
				clientChannel.close();
				clientChannel = null;
			}

			result = true;
			if (log.isDebugEnabled()) {
				log.info("关闭连接");
			}
		} catch (Exception e) {
			log.warn(null, e);
		}

		return result;
	}

	/**
	 * 关闭socket
	 */
	private ICallBack callBack = new ICallBack() {
		@Override
		public boolean close() {
			return closeSocket();
		}
	};

	/**
	 * @return 属性 callBack
	 */
	public ICallBack getCallBack() {
		return callBack;
	}
}