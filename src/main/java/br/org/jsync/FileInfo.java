package br.org.jsync;

import java.util.ArrayList;
import java.util.List;

public class FileInfo {

	String name;
	String path;
	FileInfoType type;
	long size;
	
	List<FileInfo> files;
	
	public FileInfo() {
		files = new ArrayList<FileInfo>();
	}
	
	public FileInfo(String name, String path, FileInfoType type, long size) {
		this();
		this.name = name;
		this.type = type;
		this.path = path;
		this.size = size;
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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return path + ":" + name + ":" + type;
	}
}
