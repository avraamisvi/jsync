package br.org.jsync;

public class FileInfo {

	String name;
	FileInfoType type;
	
	public FileInfo() {
	}
	
	public FileInfo(String name, FileInfoType type) {
		super();
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
	
	@Override
	public String toString() {
		return name + ":" + type;
	}
}
