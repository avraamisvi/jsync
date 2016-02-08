package br.org.jsync;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class FileManager {

	private static FileManager instance;
	private Properties prop;
	
	private FileManager() {
		prop = new Properties();
		try {
			prop.load(new FileReader(new File("jsync.cfg")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public synchronized static FileManager get() {
		
		if(instance == null)
			instance = new FileManager();
		
		return instance;
	}
	
	public ArrayList<FileInfo> listFileInfo() {
		
		String hmpath = prop.getProperty("home");
		File home = new File(hmpath);
		ArrayList<FileInfo> ret = new ArrayList<FileInfo>();
		
		for (String fileName : home.list()) {
			
			File f = new File(hmpath + File.separator + fileName);
			FileInfo finfo = new FileInfo(fileName, "", f.isDirectory()? FileInfoType.DIR : FileInfoType.FILE, f.length());
			
			ret.add(finfo);
			
			if(f.isDirectory()) {
				finfo.files = listFileInfo(f, fileName);
			}
		}
		
		return ret;
	}
	
	private ArrayList<FileInfo> listFileInfo(File dir, String relativePath) {
		
		
		ArrayList<FileInfo> ret = new ArrayList<FileInfo>();
		
		for (String fileName : dir.list()) {
			
			File f = new File(dir.getAbsolutePath() + File.separator + fileName);
			FileInfo finfo = new FileInfo(fileName, relativePath, f.isDirectory()? FileInfoType.DIR : FileInfoType.FILE, f.length());
			
			ret.add(finfo);
			
			if(f.isDirectory()) {
				finfo.files = listFileInfo(f, relativePath + "/" + fileName);
			}
		}
		
		return ret;
	}	
}
