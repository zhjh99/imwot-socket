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

import com.imwot.socket.data.TransferData;

/**
 * 〈一句话功能简述〉
 *
 * @author jinhong zhou
 */
public class ServerServiceImp extends AbstractProcess {

	protected Gloable o;

	public ServerServiceImp(Object o, ICallBack callBack) {
		super(o, callBack);
		this.o = (Gloable) o;
	}

	@Override
	protected TransferData handle(TransferData receiveData) throws Exception {
		TransferData send = new TransferData();

		byte[] data = receiveData.getData();
		String receiveString = new String(data);

		// this.o.addCount();
		// System.out.println(Thread.currentThread().getName() + "接受到数据:" +
		// receiveString + ",Count:" + this.o.getCount());
		System.out.println(Thread.currentThread().getName() + "接受到数据:" + receiveString);

		try {
			for (long x = 0; x < Long.MAX_VALUE; x++) {

			}
			System.out.println("计算完成");
		} catch (Exception e) {
			// TODO: handle exception
		}
		send.setData("success".getBytes());
		return send;
	}

}
