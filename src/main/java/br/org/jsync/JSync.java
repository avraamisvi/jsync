package br.org.jsync;

import java.util.List;

import javax.swing.DefaultListModel;

import rocks.xmpp.core.XmppException;
import br.org.jsync.MessageListFilesResponse.MessageListFilesResponseCallback;
import br.org.jsync.Service.EventsListerner;

import com.fasterxml.jackson.core.JsonProcessingException;


public class JSync {

	public static void main(String[] args) throws JsonProcessingException {
		
		/** init*/
		Service service = Service.get();
		
		MainFrameWindow jframe = new MainFrameWindow(new MainFrameController() {
			
			@Override
			public void update(MainFrame frame) {
				try {
					Service.get().updateLocalFiles();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void refresh(MainFrame frame) {
				try {
					Service.get().listRemoteFiles(new MessageListFilesResponseCallback() {
						
						@Override
						public void receiveList(List<FileInfo> list) {
							((MainFrameWindow) frame).updateFileList(list);
						}
					});
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void close() {
				try {
					Service.get().close();
				} catch (XmppException e) {
					e.printStackTrace();
				}
			}
		});
		
		service.setEventsListerner(new EventsListerner() {
			
			@Override
			public void update(String eventDescription) {
				((DefaultListModel<String>)jframe.listLog.getModel()).addElement(eventDescription);
			}
		});
		
		jframe.setTitle("Connected - Version ALPHA 0.0.1");
		jframe.setVisible(true);
	}
	
}
