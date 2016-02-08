package br.org.jsync;

import java.util.HashMap;
import java.util.List;

public class FileInfoListParser {

	public HashMap<String, Long> getSizeMap(List<FileInfo> list) {
		HashMap<String, Long> ret = new HashMap<String, Long>();
		
		for (FileInfo fileInfo : list) {
			if(fileInfo.type==FileInfoType.DIR) {
				putIntoSizeMap(ret, fileInfo.files);
			} else {
				ret.put( fileInfo.path + "/" + fileInfo.name, fileInfo.size);
			}
		}
		
		return ret;
	}
	
	public void putIntoSizeMap(HashMap<String, Long> map, List<FileInfo> list) {
		
		for (FileInfo fileInfo : list) {
			if(fileInfo.type==FileInfoType.DIR) {
				putIntoSizeMap(map, fileInfo.files);
			} else {
				map.put( fileInfo.path + "/" + fileInfo.name, fileInfo.size);
			}
		}
		
	}
	
}
