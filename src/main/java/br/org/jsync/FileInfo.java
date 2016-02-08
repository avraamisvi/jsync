package br.org.jsync;

import java.util.ArrayList;
import java.util.List;

public class FileInfo {

	String name;
	String path;
	FileInfoType type;
	
	List<FileInfo> files;
	
	public FileInfo() {
		files = new ArrayList<FileInfo>();
	}
	
	public FileInfo(String name, String path, FileInfoType type) {
		this();
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public FileInfoType getType() {
		return type;
	}
	public void setType(FileInfoType type) {
		this.type = type;
	}
	
	public List<FileInfo> getFiles() {
		return files;
	}

	public void setFiles(List<FileInfo> files) {
		this.files = files;
	}

	@Override
	public String toString() {
		return path + ":" + name + ":" + type;
	}
}
