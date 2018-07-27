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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.imwot.Charset;

/**
 * 〈一句话功能简述〉
 *
 * @author jinhong zhou
 */
public class ObjectUtils {

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
	public static byte[] toByteByZIP(Serializable object) throws Exception {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ZipOutputStream gzipStream = new ZipOutputStream(byteStream);
		ZipEntry entry = new ZipEntry("");
		gzipStream.putNextEntry(entry);
		ObjectOutputStream objectStream = new ObjectOutputStream(gzipStream);
		objectStream.writeObject(object);
		objectStream.flush();
		objectStream.close();
		gzipStream.close();
		return byteStream.toByteArray();
	}

	/**
	 * 
	 * 通过GZIP算法压缩将对象转换成字节数组
	 *
	 * @param object
	 * @return
	 * @throws Exception
	 *             byte[]
	 * @exception/throws
	 */
	public static byte[] toByteByGZIP(Serializable object) throws Exception {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream);
		ObjectOutputStream objectStream = new ObjectOutputStream(gzipStream);
		objectStream.writeObject(object);
		objectStream.flush();
		objectStream.close();
		gzipStream.close();
		return byteStream.toByteArray();
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
	public static Object toObjectByZIP(byte[] bytes) throws Exception {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
		ZipInputStream gzipStream = new ZipInputStream(byteStream);
		ObjectInputStream objectStream = new ObjectInputStream(gzipStream);
		Object object = objectStream.readObject();
		objectStream.close();
		gzipStream.close();
		return object;
	}

	/**
	 * 
	 * 通过ZIP算法解压字符串
	 * 
	 * @param txt
	 * @return
	 * @throws Exception
	 *             byte[]
	 * @exception/throws
	 */
	public static String toStringByGZIP(byte[] data) throws Exception {
		String result = null;
		byte[] bytes = toByteByZIP(data);
		result = new String(bytes, Charset.UTF8);
		return result;
	}
}
