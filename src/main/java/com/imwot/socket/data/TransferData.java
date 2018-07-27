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
package com.imwot.socket.data;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import com.imwot.Charset;
import com.imwot.socket.utils.ByteConvert;
import com.imwot.socket.utils.DataUtils;

/**
 * 传输数据封装,包括接收、发送数据
 *
 * @author jinhong zhou
 */
public class TransferData implements Serializable {

	/**
	 * 属性serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 类型
	 */
	private int type;

	/**
	 * 命令长度
	 */
	private int commandLength;

	/**
	 * 数据长度
	 */
	private int dataLength;

	/**
	 * 命令
	 */
	private String command = "";

	/**
	 * 数据
	 */
	private byte[] data;

	public TransferData() {

	}

	public TransferData(int type, String command, byte[] data) {
		this.setType(type);
		this.setCommand(command);
		this.setData(data);
	}

	public TransferData(int type, String command) {
		this.setType(type);
		this.setCommand(command);
		this.setData(null);
	}

	/**
	 * @return the dataLength
	 */
	public int getDataLength() {
		return dataLength;
	}

	/**
	 * @return 属性 command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * 设置属性 command 值
	 * 
	 * @throws Exception
	 */
	public void setCommand(String command) {
		this.command = command;
		if (command == null) {
			this.commandLength = 0;
		} else {
			try {
				this.commandLength = command.getBytes(Charset.UTF8).length;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
		if (data == null) {
			this.dataLength = 0;
		} else {
			this.dataLength = data.length;
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return 属性 commandLength
	 */
	public int getCommandLength() {
		return commandLength;
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * 
	 * 把TransferData转成byte[]
	 * 
	 * @return
	 * @throws Exception
	 *             byte[]
	 * @exception/throws
	 */
	public byte[] toByte() throws Exception {
		byte[] commandLengthByte = DataUtils.toLittleEndian(commandLength);
		byte[] dataLengthByte = DataUtils.toLittleEndian(dataLength);
		byte[] dataByte = data;
		byte[] commandByte = null;
		if (command != null) {
			commandByte = command.getBytes(Charset.UTF8);
		}

		ByteConvert bc = new ByteConvert();
		bc.append((byte) type);
		bc.append(commandLengthByte);
		bc.append(dataLengthByte);
		bc.append(commandByte);
		bc.append(dataByte);

		return bc.toArray();
	}

}
