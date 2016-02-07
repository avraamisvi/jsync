package br.org.jsync;

import java.util.List;

public class MessageListFilesResponse extends JSyncMessage {
	
	List<FileInfo> list;
	
	public MessageListFilesResponse() {
		super(JSyncMessage.LIST_RES);
	}

	public List<FileInfo> getList() {
		return list;
	}

	public void setList(List<FileInfo> list) {
		this.list = list;
	}
	
	public static interface MessageListFilesResponseCallback {
		public void receiveList(List<FileInfo> list);
	}
}
