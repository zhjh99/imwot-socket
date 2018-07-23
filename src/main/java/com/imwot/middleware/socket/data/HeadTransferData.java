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
package com.imwot.middleware.socket.data;

import com.imwot.middleware.socket.utils.ByteConvert;
import com.imwot.middleware.socket.utils.DataUtils;

/**
 * 传输数据(前面9个字节数据)
 *
 * @author    jinhong zhou
 */
public class HeadTransferData {

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
	 * @return 属性 type
	 */
	public int getType() {
		return type;
	}

	/**
	 * 设置属性 type 值
	 */
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
	 * 设置属性 commandLength 值
	 */
	public void setCommandLength(int commandLength) {
		this.commandLength = commandLength;
	}

	/**
	 * @return 属性 dataLength
	 */
	public int getDataLength() {
		return dataLength;
	}

	/**
	 * 设置属性 dataLength 值
	 */
	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	/**
	 * 
	 * 把头部信息转为二进制
	 *
	 * @return
	 * @throws Exception 
	 * byte[]
	 * @exception/throws
	 */
	public byte[] toByte() throws Exception {
		byte[] orderLengthByte = DataUtils.toLittleEndian(commandLength);
		byte[] dataLengthByte = DataUtils.toLittleEndian(dataLength);

		ByteConvert bc = new ByteConvert();
		bc.append((byte) type);
		bc.append(orderLengthByte);
		bc.append(dataLengthByte);
		return bc.toArray();
	}
}

