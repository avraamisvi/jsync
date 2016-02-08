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
			top.add(new FileInfoTreeNode(fileInfo));
		}
		
		treeRemote.setModel(new DefaultTreeModel(top));
		this.pack();
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
