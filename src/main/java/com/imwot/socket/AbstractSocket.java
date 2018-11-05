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

import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.imwot.socket.data.HeadTransferData;
import com.imwot.socket.data.TransferData;
import com.imwot.socket.utils.ByteConvert;
import com.imwot.socket.utils.DataUtils;
import com.imwot.socket.utils.ObjectUtils;

/**
 * socket抽象方法,会被服务器端和客户端同时继承
 *
 * @author jinhong zhou
 */
public abstract class AbstractSocket extends AbstractLog {

	/**
	 * Socket(NIO通道)
	 */
	protected SocketChannel socketChannel;

	/**
	 * 默认数据大小
	 */
	protected final int DEFAULT_SIZE = 24455;

	/**
	 * 读取头部后面剩余数据缓冲用(在此创建,可以重复使用)
	 */
	private ByteBuffer remainningData = ByteBuffer.allocate(DEFAULT_SIZE);

	/**
	 * 多路复用器(注册写消息)
	 */
	Selector writeSelector = null;

	/**
	 * 多路复用器(注册读消息)
	 */
	Selector readSelector = null;

	/**
	 * 
	 * 关闭连接
	 * 
	 * @return boolean
	 * @exception/throws
	 */
	public abstract boolean close();

	/**
	 * 
	 * 接收数据(外部调用方法)
	 * 
	 * @return
	 * @throws Exception
	 *             TransferData
	 * @exception/throws
	 */
	public TransferData receive() throws Exception {
		TransferData data = null;
		ByteConvert bc = new ByteConvert();

		HeadTransferData headTransferData = readHead(bc);
		if (headTransferData != null) {
			data = new TransferData();
			readRemainingData(headTransferData, bc);

			data = DataUtils.toTransferData(bc.toArray());
			bc = null;
		}

		return data;
	}

	/**
	 * 
	 * 读取头部信息
	 * 
	 * @param bc
	 * @return
	 * @throws Exception
	 *             HeadTransferData
	 * @exception/throws
	 */
	private HeadTransferData readHead(ByteConvert bc) throws Exception {
		ByteBuffer headData = ByteBuffer.allocate(9);

		int readLength = socketChannel.read(headData);

		if (readLength == 0) {
			// 客户端无数据传输
			return null;
		}

		if (readLength == -1) {
			throw new IOException("readLength=-1,IO错误或客户端断开");
		}

		if (readLength < 9) {
			throw new IOException("读取头部错误,readLength<9");
		}
		bc.append(headData.array());
		HeadTransferData headTransferData = DataUtils.toHeadTransferData(headData.array());
		int dataLength = headTransferData.getCommandLength() + headTransferData.getDataLength();
		if (dataLength < 0) {
			throw new IOException("读取头部错误,dataLength<0");
		}

		headData.clear();
		headData = null;
		return headTransferData;
	}

	/**
	 * 
	 * 读取头部后面剩余部分数据
	 * 
	 * @param headTransferData
	 * @param bc
	 * @throws Exception
	 *             void
	 * @exception/throws
	 */
	private void readRemainingData(HeadTransferData headTransferData, ByteConvert bc) throws Exception {
		int dataLength = headTransferData.getCommandLength() + headTransferData.getDataLength();
		int receiveTotal = 0;

		int timeOutNum = 0;
		while (receiveTotal < dataLength) {
			remainningData.clear();
			int readLength = socketChannel.read(remainningData);
			timeOutNum++;

			if (readLength == -1) {
				throw new IOException("readLength=-1,IO错误或客户端断开");
			} else if (readLength == 0) {
				if (readSelector.select(3000) == 0) {
					if (timeOutNum > 10) {
						throw new IOException("读取数据超时30秒");
					}
				} else {
					timeOutNum--;
				}
			} else {
				timeOutNum = 0;
				receiveTotal = receiveTotal + readLength;
				byte[] footDataTmp = remainningData.array();
				bc.append(footDataTmp, readLength);
			}
		}
	}

	/**
	 * 
	 * 发送数据(外部调用方法)
	 * 
	 * @param data
	 * @throws Exception
	 *             void
	 * @exception/throws
	 */
	public void send(TransferData sendData) throws Exception {
		byte[] data = sendData.toByte();
		sendingProcess(ByteBuffer.wrap(data));
		data = null;
	}

	/**
	 * 
	 * 发送数据(如果一次发送不成功，则继续发送剩余部分，如果发送长度为0，则启用消息注册，直到等待到可写，然后继续发送)
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 *             long
	 * @exception/throws
	 */
	protected long sendingProcess(ByteBuffer data) throws Exception {
		int sendTotal = 0;

		if (data != null && data.array().length != 0) {
			int timeOutNum = 0;

			while (data.hasRemaining()) {
				int writeLength = socketChannel.write(data);
				timeOutNum++;

				if (writeLength < 0) {
					throw new EOFException();
				}

				sendTotal = sendTotal + writeLength;

				if (writeLength == 0) {
					if (writeSelector.select(3000) == 0) {
						if (timeOutNum > 10) {
							throw new IOException("发送数据超时30秒");
						}
					} else {
						timeOutNum--;
					}
				} else {
					timeOutNum = 0;
				}
			}
		}
		return sendTotal;
	}

	/**
	 * 
	 * 通过ZIP算法压缩将对象转换成字节数组
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 *             byte[]
	 * @exception/throws
	 */
	public byte[] toByteByZIP(Serializable object) throws Exception {
		return ObjectUtils.toByteByZIP(object);
	}

	/**
	 * 
	 * ZIP算法压缩将字节数组转换成对象
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 *             Object
	 * @exception/throws
	 */
	public Object toObjectByZIP(byte[] bytes) throws Exception {
		return ObjectUtils.toObjectByZIP(bytes);
	}

	/**
	 * 
	 * 通过ZIP算法"解压"字符串
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 *             String
	 * @exception/throws
	 */
	public String toStringByZIP(byte[] bytes) throws Exception {
		return ObjectUtils.toStringByGZIP(bytes);
	}

	/**
	 * 
	 * 通过ZIP算法"压缩"字符串
	 * 
	 * @param txt
	 * @return
	 * @throws Exception
	 *             byte[]
	 * @exception/throws
	 */
	public byte[] toByteByZIP(String txt) throws Exception {
		return ObjectUtils.toByteByGZIP(txt);
	}

	/**
	 * 
	 * 获取对方SocketAddress
	 * 
	 * @return SocketAddress
	 * @exception/throws
	 */
	public InetSocketAddress getRemoteAddress() {
		InetSocketAddress result = null;
		try {
			result = (InetSocketAddress) this.socketChannel.getRemoteAddress();
		} catch (Exception e) {
			log.warn(null, e);
		}
		return result;
	}
}