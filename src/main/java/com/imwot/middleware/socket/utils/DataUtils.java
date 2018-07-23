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
package com.imwot.middleware.socket.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.imwot.middleware.socket.data.HeadTransferData;
import com.imwot.middleware.socket.data.TransferData;

/**
 * 数据处理工具类
 *
 * @author jinhong zhou
 */
public class DataUtils {
	/**
	 * 
	 * byte数组转int
	 * 
	 * @param bytes
	 * @return int
	 * @exception/throws
	 */
	public static int getLittleEndianInt(byte[] bytes) {
		int b0 = bytes[0] & 0xFF;
		int b1 = bytes[1] & 0xFF;
		int b2 = bytes[2] & 0xFF;
		int b3 = bytes[3] & 0xFF;
		return b0 + (b1 << 8) + (b2 << 16) + (b3 << 24);
	}

	/**
	 * 
	 * int转byte数组
	 * 
	 * @param i
	 * @return byte[]
	 * @exception/throws
	 */
	public static byte[] toLittleEndian(int i) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) i;
		bytes[1] = (byte) (i >> 8);
		bytes[2] = (byte) (i >> 16);
		bytes[3] = (byte) (i >> 24);
		return bytes;
	}

	/**
	 * 
	 * 把完整的字节数组转成TransferData
	 * 
	 * @param receiveData
	 * @return TransferData
	 * @exception/throws
	 */
	public static TransferData toTransferData(byte[] receiveData) {
		TransferData transferData = new TransferData();
		try {
			int type = -1;
			int commandLength = -1;
			int dataLength = -1;
			String command = null;
			byte[] dataByte = null;

			byte typeByte = receiveData[0];
			type = (int) typeByte;

			byte[] lengthByte = ByteConvert.cut(receiveData, 1, 4);
			commandLength = DataUtils.getLittleEndianInt(lengthByte);

			lengthByte = ByteConvert.cut(receiveData, 5, 4);
			dataLength = DataUtils.getLittleEndianInt(lengthByte);

			byte[] commandByte = ByteConvert.cut(receiveData, 9, commandLength);
			command = commandByte.length == 0 ? "" : new String(commandByte, "UTF-8");

			dataByte = ByteConvert.cut(receiveData, 9 + commandLength, dataLength);

			transferData.setType(type);
			transferData.setCommand(command);
			transferData.setData(dataByte);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return transferData;
	}

	/**
	 * 
	 * 把字节数组前9个字节转换为HeadTransferData
	 * 
	 * @param receiveData
	 * @return HeadTransferData
	 * @exception/throws
	 */
	public static HeadTransferData toHeadTransferData(byte[] receiveData) {
		HeadTransferData headTransferData = new HeadTransferData();
		try {
			int type = -1;
			int commandLength = -1;
			int dataLength = -1;

			byte typeByte = receiveData[0];
			type = (int) typeByte;

			byte[] lengthByte = new byte[4];
			System.arraycopy(receiveData, 1, lengthByte, 0, 4);
			commandLength = DataUtils.getLittleEndianInt(lengthByte);

			System.arraycopy(receiveData, 5, lengthByte, 0, 4);
			dataLength = DataUtils.getLittleEndianInt(lengthByte);

			headTransferData.setType(type);
			headTransferData.setCommandLength(commandLength);
			headTransferData.setDataLength(dataLength);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return headTransferData;
	}

	/**
	 * 
	 * 两个byte数组合并
	 * 
	 * @param byte_1
	 * @param byte_2
	 * @return byte[]
	 * @exception/throws
	 */
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}

	/**
	 * 
	 * 计算两个正整数，需要执行的次数
	 * 
	 * @param total
	 * @param length
	 * @return int
	 * @exception/throws
	 */
	public static int getTimes(int total, int length) {
		int times = 0;
		if (total > 0 && length > 0) {
			if (total < length) {
				times = 1;
			} else {
				times = total / length;
				if (total % length != 0) {
					times++;
				}
			}
		}
		return times;
	}

	/**
	 * 
	 * 对象转数组
	 * 
	 * @param obj
	 * @return byte[]
	 * @exception/throws
	 */
	public static byte[] toByteArray(Object obj) {
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();
			oos.close();
			bos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return bytes;
	}

	/**
	 * 
	 * 数组转对象
	 * 
	 * @param bytes
	 * @return Object
	 * @exception/throws
	 */
	public static Object toObject(byte[] bytes) {
		Object obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			obj = ois.readObject();
			ois.close();
			bis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return obj;
	}
}
