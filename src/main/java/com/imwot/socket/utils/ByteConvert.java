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
package com.imwot.socket.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * 〈一句话功能简述〉
 *
 * @author    jinhong zhou
 */
public class ByteConvert {

	/**
	 * 缓冲区大小
	 */
	private static final int BUFFER_SIZE = 32768;
	
	/**
	 * 初始化缓冲区大小
	 */
	private byte buffer[] = new byte[BUFFER_SIZE];
	
	/**
	 * 索引位置
	 */
	private int index = 0;	
	
	/**
	 * 
	 * 获取byte[]数组
	 *
	 * @return 
	 * byte[]
	 * @exception/throws
	 */
	public byte[] toArray() {
		byte result[] = new byte[index];
		if(index>0){
			System.arraycopy(buffer, 0, result, 0, index);
			buffer = new byte[BUFFER_SIZE];
		}
		index = 0;
		return result;
	}
	
	/**
	 * 返回字符串
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return new String(this.toArray());
	}
	
	/**
	 * 返回字符串
	 *
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException 
	 * String
	 * @exception/throws
	 */
	public String toString(String charset) throws UnsupportedEncodingException{
		return new String(this.toArray(), charset);
	}

	/**
	 * 追加数据
	 *
	 * @param data 
	 * void
	 * @exception/throws
	 */
	public void append(byte data[]) {	
		
		// 如果数据为null则不追加
		if(data == null){
			return ;
		}
		
		int max = index + data.length;
		byte[] temp = new byte[max];
		if(index > 0){
			System.arraycopy(this.buffer, 0, temp, 0, this.buffer.length);
		}
		System.arraycopy(data, 0, temp, index, data.length);
		this.buffer = temp;
		index = max;
	}
	
	/**
	 * 追加数据
	 *
	 * @param data
	 * @param length 
	 * void
	 * @exception/throws
	 */
	public void append(byte[] data, int length){
		if(data == null || length <= 0){
			return ;
		}
		byte[] temp = new byte[length];
		System.arraycopy(data, 0, temp, 0, length);
		this.append(temp);		
	}
	
	/**
	 * 追加一个字节码
	 *
	 * @param b 
	 * void
	 * @exception/throws
	 */
	public void append(byte b){
		this.append(new byte[]{b});
	}

	/**
	 * 读取流数据
	 *
	 * @param in
	 * @param max
	 * @throws IOException 
	 * void
	 * @exception/throws
	 */
	public void read(InputStream in, int max, boolean isFileStream) throws IOException {		
		int len = 0;
		byte[] temp = new byte[max];
		int minLength = isFileStream ? 0 : max;
		while(true){			
			len = in.read(temp);					
			if(len > 0){
				this.append(temp, len);
			}
			
			if(len < minLength){
				break;
			}			
		}
	}	
	
	/**
	 * 读取流数据
	 *
	 * @param in
	 * @throws IOException 
	 * void
	 * @exception/throws
	 */
	public void read(InputStream in, boolean isFileStream) throws IOException {
		this.read(in, BUFFER_SIZE, isFileStream);
	}
	
	/**
	 * 读取流数据(默认为读取文件流)
	 *
	 * @param in
	 * @throws IOException 
	 * void
	 * @exception/throws
	 */
	public void read(InputStream in) throws IOException {
		this.read(in, BUFFER_SIZE, true);
	}	
	
	/**
	 * 截取Byte[]
	 *
	 * @param source
	 * @param startPos
	 * @param length
	 * @return 
	 * byte[]
	 * @exception/throws
	 */
	public static byte[] cut(byte[] source, int startPos, int length){
		byte[] temp = new byte[0];
		if(length > 0 && startPos<source.length){
			int max = length > (source.length - startPos) ? (source.length - startPos) : length;
			temp = new byte[max];
			System.arraycopy(source, startPos, temp, 0, max);			
		}
		return temp;
	}
	
	/**
	 * 清除数据
	 * 
	 * void
	 * @exception/throws
	 */
	public void clear(){
		index = 0;
		buffer = new byte[index];
	}
}