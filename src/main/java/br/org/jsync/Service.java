package br.org.jsync;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.extensions.filetransfer.FileTransfer;
import rocks.xmpp.extensions.filetransfer.FileTransferManager;
import rocks.xmpp.extensions.filetransfer.FileTransferOfferEvent;
import rocks.xmpp.extensions.filetransfer.FileTransferStatusEvent;
import br.org.jsync.MessageListFilesResponse.MessageListFilesResponseCallback;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Service {

	private static Service instance;
	private Properties prop;
	XmppClient xmppClient;
	FileTransferManager fileTransferManager;
	String remote;
	String localFolderHome;
	private String server;
	private String username;
	private String secret;
	
	MessageListFilesResponseCallback filesResponseCallback;
	
	private Service() {
		prop = new Properties();
		try {
			prop.load(new FileReader(new File("jsync.cfg")));
			
			server = prop.getProperty("server");
			remote = prop.getProperty("remote");
			username = prop.getProperty("username");
			secret = prop.getProperty("secret");
			
			xmppClient = XmppClient.create(server);
			
			xmppClient.addInboundMessageListener(e -> {
				try {
				
					String msg = e.getMessage().getBody();
				
					System.out.println(msg);
				
					switch (getMessage(msg)) {
						case JSyncMessage.LIST_REQ:
							listLocalFiles();
							break;
						case JSyncMessage.LIST_RES:
							receivRemoteFilesList(msg);
							break;
						case JSyncMessage.UPDATE_REQ:
							uploadFiles();
							break;							
						default:
							break;
					}
					
				
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
				
			});
			
			FileTransferManager fileTransferManager = xmppClient.getManager(FileTransferManager.class);
			/** receive files */
			fileTransferManager.addFileTransferOfferListener(e -> {
			    try {
			    	
			    	FileTransferOfferEvent ev = e;
			    	
			        FileTransfer fileTransfer = e.accept(Paths.get(localFolderHome + File.separator + ev.getName())).getResult();
			        
			        fileTransfer.addFileTransferStatusListener(event -> {
			        	FileTransferStatusEvent evloc = event;
			        	
			        	System.out.println(evloc.getBytesTransferred());
			        	System.out.println(evloc.getStatus().name());
			        });
			        
			        fileTransfer.transfer();
			        
			    } catch (Exception e1) {
			        e1.printStackTrace();
			    }
			});			
			
			xmppClient.connect();
			xmppClient.login(username, secret, "home");
			
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public synchronized static Service get() {
		
		if(instance == null)
			instance = new Service();
		
		return instance;
	}
	
	public void updateLocalFiles() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		this.sendMessage(mapper.writeValueAsString(new MessageUpdateFilesRequest()));
	}
	
	public void uploadFiles() throws XmppException, IOException {
		ArrayList<FileInfo> list = FileManager.get().listFileInfo();
		transfer(0, list);
		
	}
	
	public void transfer(int index, ArrayList<FileInfo> list) throws XmppException, IOException {
		
		 FileInfo fileInfo = new FileInfo();
		 
		 if(index >= list.size())
			 return;
		 
		 if(fileInfo.type == FileInfoType.FILE) {
			 
			 File file = new File(localFolderHome + File.separator + fileInfo.name);
			 
			 FileTransfer fileTransfer = fileTransferManager.offerFile(Paths.get(localFolderHome + File.separator + fileInfo.name), "File", 
					 Jid.of(remote + "@"+ server + "/home"), 60000).getResult();
	
			 fileTransfer.addFileTransferStatusListener(e -> {
				 	FileTransferStatusEvent ev = e;
				    try {
						updateProgress(e.getBytesTransferred(), file.length(), index, list);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
			 });
			 
			 
		 } else {
			 transfer(index+1, list);
		 }
	}
	
	
	private void updateProgress(long bytesTransferred, long length, int index, ArrayList<FileInfo> list) throws XmppException, IOException {
		// TODO Auto-generated method stub
		if(bytesTransferred >= length) {
			transfer(index+1, list);
		}
	}

	public void listRemoteFiles(MessageListFilesResponseCallback callback) throws JsonProcessingException {
		this.filesResponseCallback = callback;
		
		ObjectMapper mapper = new ObjectMapper();
		
		this.sendMessage(mapper.writeValueAsString(new MessageListFilesRequest()));
	}

	public void receivRemoteFilesList(String msg) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		MessageListFilesResponse message = mapper.readValue(msg, MessageListFilesResponse.class);
		filesResponseCallback.receiveList(message.list);
	}	
	
	public void sendMessage(String msg) {
		xmppClient.send(new Message(Jid.of(remote + "@" + server), Message.Type.CHAT, msg));
	}
	
	public String getMessage(String message) throws JsonParseException, JsonMappingException, IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		return mapper.readValue(message, HashMap.class).get("name").toString();
	}
	
	public void listLocalFiles() throws JsonGenerationException, JsonMappingException, IOException {
		
		ArrayList<FileInfo> files = FileManager.get().listFileInfo();
    	
		MessageListFilesResponse message = new MessageListFilesResponse();
		message.setList(files);
		
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	
    	ObjectMapper mapper = new ObjectMapper();
    	String msg = "ERROR";
    	
		mapper.writeValue(out, message);
		msg = out.toString("UTF-8");
		
		sendMessage(msg);
	}

}
