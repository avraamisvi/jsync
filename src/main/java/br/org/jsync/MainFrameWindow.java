package br.org.jsync;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import br.org.jsync.PropDialogWindow.PropDialogEvents;

public class MainFrameWindow extends MainFrame {

	FileInfoTreeNode top;
	
	public MainFrameWindow(MainFrameController controller) {
		super(controller);
		configure(controller);
	}
	
	public void updateFileList(List<FileInfo> list) {
		
		top = new FileInfoTreeNode(new FileInfo("Remote Files", "", FileInfoType.DIR, 0));
		
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
	
	private void configure(MainFrameController controller) {
		// TODO Auto-generated constructor stub
				top = new FileInfoTreeNode(new FileInfo("Remote Files", "", FileInfoType.DIR, 0));
				treeRemote.setModel(new DefaultTreeModel(top));
				listLog.setModel(new DefaultListModel<String>());
				
				addWindowListener(new WindowListener() {
					
					@Override
					public void windowOpened(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void windowIconified(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void windowDeiconified(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void windowDeactivated(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void windowClosing(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void windowClosed(WindowEvent e) {
						// TODO Auto-generated method stub
						controller.close();
					}
					
					@Override
					public void windowActivated(WindowEvent e) {
						// TODO Auto-generated method stub
						
					}
				});		
				
				mnitmConfiguration.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						new PropDialogWindow(MainFrameWindow.this, new PropDialogEvents() {
							
							@Override
							public void onSave(Properties prop) {
								try {
									prop.store(new FileOutputStream(new File(Constants.CFG_FILE)), "");
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							@Override
							public void onLoad(Properties prop) {
								try {
									prop.load(new FileInputStream(new File(Constants.CFG_FILE)));
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).setVisible(true);
					}
				});
				
				mnitmExit.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						MainFrameWindow.this.dispatchEvent(new WindowEvent(MainFrameWindow.this, WindowEvent.WINDOW_CLOSING));
					}
				});
	}
}
