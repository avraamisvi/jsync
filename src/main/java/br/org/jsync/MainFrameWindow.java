package br.org.jsync;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class MainFrameWindow extends MainFrame {

	FileInfoTreeNode top;
	
	public MainFrameWindow(MainFrameController controller) {
		super(controller);
		// TODO Auto-generated constructor stub
		top = new FileInfoTreeNode(new FileInfo("Remote Files", "", FileInfoType.DIR));
		treeRemote.setModel(new DefaultTreeModel(top));
	}
	
	public void updateFileList(List<FileInfo> list) {
		
		top = new FileInfoTreeNode(new FileInfo("Remote Files", "", FileInfoType.DIR));
		
		for (FileInfo fileInfo : list) {
			
			FileInfoTreeNode node = new FileInfoTreeNode(fileInfo);
			top.add(node);

			if(fileInfo.type == FileInfoType.DIR) {
				updateFileList(node, fileInfo.files);
			}
		}
		
		treeRemote.setModel(new DefaultTreeModel(top));
		this.pack();
	}
	
	public void updateFileList(FileInfoTreeNode parent, List<FileInfo> list) {
		
		for (FileInfo fileInfo : list) {
			
			FileInfoTreeNode node = new FileInfoTreeNode(fileInfo);
			parent.add(node);
			
			if(fileInfo.type == FileInfoType.DIR) {
				updateFileList(node, fileInfo.files);
			}
		}
		
	}	
	
	
	class FileInfoTreeNode extends DefaultMutableTreeNode {
		
		FileInfoType type;
		
		public FileInfoTreeNode(FileInfo file) {
			super(file.name);
			// TODO Auto-generated constructor stub
			type = file.type;
		}

		@Override
		public boolean isLeaf() {
			return type.equals(FileInfoType.FILE);
		}
	}
}
