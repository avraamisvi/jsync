package br.org.jsync;

import java.util.List;

public class MessageUpdateFilesRequest extends JSyncMessage {
	
	List<FileInfo> knownFiles;
	
	public MessageUpdateFilesRequest() {
		super(JSyncMessage.UPDATE_REQ);
	}

	public List<FileInfo> getKnownFiles() {
		return knownFiles;
	}

	public void setKnownFiles(List<FileInfo> knownFiles) {
		this.knownFiles = knownFiles;
	}
	
}
