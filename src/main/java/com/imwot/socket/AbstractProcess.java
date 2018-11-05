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
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.imwot.socket.data.TransferData;

/**
 * 〈一句话功能简述〉
 *
 * @author jinhong zhou
 */
public abstract class AbstractProcess extends AbstractSocket implements IProcess {
	protected final static String success = "ok";
	protected final static String error = "no";
	protected ICallBack callBack;

	public AbstractProcess(Object o, ICallBack callBack) {
		this.callBack = callBack;
	}

	@Override
	public void transfer() throws Exception {
		// 接收
		TransferData receiveData = this.receive();
		if (receiveData.getType() == CmdFactory.closeCmd.getType() && receiveData.getCommand().equals(CmdFactory.closeCmd.getCommand())) {
			this.close();
			log.info("socket close");
		} else {
			TransferData backData = handle(receiveData);
			if (null != backData) {
				// 返回
				this.send(backData);
			}
		}
	}

	protected abstract TransferData handle(TransferData receiveData) throws Exception;

	public void call(SocketChannel socketChannel, Selector writeSelector, Selector readSelector) throws Exception {
		this.socketChannel = socketChannel;
		this.writeSelector = writeSelector;
		this.readSelector = readSelector;
		try {
			transfer();
		} catch (IOException ioe) {
			log.info("socket closed");
			close();
		} catch (Exception e) {
			log.warn(null, e);
			close();
		}
	}

	/**
	 * 此方法覆盖父类的方法
	 * 
	 * @see com.imwot.socket.quekua.iTransfer.socket.AbstractSocket#close()
	 */
	@Override
	public boolean close() {
		this.socketChannel = null;
		this.writeSelector = null;
		this.readSelector = null;
		return callBack.close();
	}

}
