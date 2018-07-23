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
package com.imwot.middleware.socket;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 〈一句话功能简述〉
 *
 * @author jinhong zhou
 */
public class SocketClient extends AbstractSocket {
	/**
	 * 远程服务器ip
	 */
	private String ip;

	/**
	 * 远程服务器端口
	 */
	private int port;

	public SocketClient(String ip, int port) throws UnknownHostException, IOException {
		this.ip = ip;
		this.port = port;
		init();
	}

	/**
	 * 
	 * 初始化连接
	 * 
	 * @throws IOException
	 *             void
	 * @exception/throws
	 */
	private void init() throws IOException {
		this.socketChannel = SocketChannel.open(new InetSocketAddress(ip, port));
	}

	/**
	 * 此方法覆盖父类的方法
	 * 
	 * @see com.quekua.iTransfer.socket.AbstractSocket#close()
	 */
	@Override
	public boolean close() {
		boolean result = false;
		try {
			socketChannel.close();
			socketChannel = null;
			result = true;
		} catch (Exception e) {
			log.warn(null, e);
		}
		return result;
	}

	/**
	 * 
	 * 重新启动连接
	 * 
	 * void
	 * 
	 * @exception/throws
	 */
	public void restart() {
		try {
			if (socketChannel != null) {
				close();
			}
			Thread.sleep(100);
			init();
		} catch (Exception e) {
			log.warn(null, e);
		}
	}

	/**
	 * 此方法覆盖父类的方法
	 * 
	 * @see com.quekua.iTransfer.socket.AbstractSocket#sendingProcess(java.nio.ByteBuffer)
	 */
	protected long sendingProcess(ByteBuffer data) throws Exception {
		int sendTotal = 0;
		if (data != null && data.array() != null && data.array().length != 0) {
			while (data.hasRemaining()) {
				int writeLength = socketChannel.write(data);
				if (writeLength < 0) {
					throw new EOFException("IO错误或客户端断开");
				}
				sendTotal = sendTotal + writeLength;
			}
		}
		return sendTotal;
	}
}