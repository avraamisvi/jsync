package br.org.jsync;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	
	private String targetResource;
	private String localFolderHome;
	private String server;
	private String username;
	private String secret;
	private String resource;
	private String targetUsername;
	
	MessageListFilesResponseCallback filesResponseCallback;
	ServiceListerner eventsListerner;
	
	List<FileInfo> remoteFiles;
	long lastRefreshFiles = 0L;
	
	private Service() {
		prop = new Properties();
		try {
			prop.load(new FileReader(new File("jsync.cfg")));
			
			server = prop.getProperty("server");
			username = prop.getProperty("local.username");
			secret = prop.getProperty("local.secret");
			localFolderHome = prop.getProperty("local.home");
			resource = prop.getProperty("local.resource");
			
			targetResource = prop.getProperty("target.resource");
			targetUsername = prop.getProperty("target.username");
			
			xmppClient = XmppClient.create(server);
			
			xmppClient.addInboundMessageListener(e -> {
				try {
				
					String msg = e.getMessage().getBody();
				
					System.out.println(msg);
				
					switch (getMessage(msg)) {
						case JSyncMessage.LIST_REQ:
							updateServiceListener("List Files");
							listLocalFiles();
							break;
						case JSyncMessage.LIST_RES:
							updateServiceListener("Receiving Files List");
							receivRemoteFilesList(msg);
							break;
						case JSyncMessage.UPDATE_REQ:
							updateServiceListener("Update request");
							uploadFiles(msg);
							break;							
						default:
							break;
					}
					
				
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
				
			});
			
			fileTransferManager = xmppClient.getManager(FileTransferManager.class);
			/** receive files */
			fileTransferManager.addFileTransferOfferListener(e -> {
			    try {
			    	
			    	FileTransferOfferEvent ev = e;
			    	String fileName = null;
			    	
			    	
			    	if(!ev.getDescription().isEmpty()) {
			    		Files.createDirectories(Paths.get(localFolderHome + File.separator + ev.getDescription()));
			    		fileName = localFolderHome + File.separator + ev.getDescription() + File.separator + ev.getName();
			    	} else {
			    		fileName = localFolderHome + File.separator + ev.getName();
			    	}
			    	
			    	updateServiceListener("Receiving File:" + fileName);
			    	
			        FileTransfer fileTransfer = e.accept(Paths.get(fileName)).getResult();
			        
			        fileTransfer.addFileTransferStatusListener(event -> {
			        	FileTransferStatusEvent evloc = event;
			        	
			        	updateServiceListener("Downloading bytes transfered: " + evloc.getBytesTransferred());
//			        	System.out.println(evloc.getBytesTransferred());
//			        	System.out.println(evloc.getStatus().name());
			        });
			        
			        fileTransfer.transfer();
			        
			    } catch (Exception e1) {
			        e1.printStackTrace();
			    }
			});			
			
			xmppClient.connect();
			xmppClient.login(username, secret, resource);
			
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public synchronized static Service get() {
		
		if(instance == null)
			instance = new Service();
		
		return instance;
	}
	
	public void setServiceListerner(ServiceListerner eventsListerner) {
		this.eventsListerner = eventsListerner;
	}
	
	protected void updateServiceListener(String event) {
		if(eventsListerner != null) {
			eventsListerner.update(event);
		}
	}
	
	protected void updateErrorServiceListener(String event) {
		if(eventsListerner != null) {
			eventsListerner.error(event);
		}
	}	
	
	public void updateLocalFiles() throws JsonProcessingException {
		
		long time = System.currentTimeMillis();
		
		if(time - lastRefreshFiles > 60000) {
			updateErrorServiceListener("Please refresh remote files first.");
			return;
		}
		
		ObjectMapper mapper = new ObjectMapper();
		
		MessageUpdateFilesRequest msgUp = new MessageUpdateFilesRequest();
		msgUp.setKnownFiles(FileManager.get().listFileInfo());
		
		this.sendMessage(mapper.writeValueAsString(msgUp));
	}
	
	public void uploadFiles(String msg) throws XmppException, IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		MessageUpdateFilesRequest message = mapper.readValue(msg, MessageUpdateFilesRequest.class);
		
		ArrayList<FileInfo> list = FileManager.get().listFileInfo();
		
		HashMap<String, Long> map = new FileInfoListParser().getSizeMap(message.knownFiles);
		
		transfer(0, list, map);
		
		updateServiceListener("Upload finished");
		
	}
	
	public void transfer(int index, List<FileInfo> list, HashMap<String, Long> remoteKnownFiles) throws XmppException, IOException {
		
		 
		 if(index >= list.size())
			 return;
		 
		 FileInfo fileInfo = list.get(index);
		 
		 boolean contains = remoteKnownFiles.containsKey(fileInfo.path + "/" + fileInfo.name);
		 
		 long remoteSize = 0L;
		 long localSize = fileInfo.size;
		 
		 if(contains)
			 remoteSize = remoteKnownFiles.get(fileInfo.path + "/" + fileInfo.name);
		 
		 boolean sameSize = localSize == remoteSize;
		 
		 System.out.println(fileInfo.name + " same:" + sameSize);
		 
		 if(!sameSize) {
			 if(fileInfo.type == FileInfoType.FILE)
				 updateServiceListener("Sending: " + fileInfo.name);
		 }
		 
		 if(fileInfo.type == FileInfoType.FILE && (localSize != remoteSize)) {
			 
			 String fileName;
			 
			 if(fileInfo.path != null && !fileInfo.path.isEmpty()) {
				 fileName = localFolderHome + File.separator + fileInfo.path + File.separator + fileInfo.name;
			 } else {
				 fileName = localFolderHome + File.separator + fileInfo.name;
			 }
			 
			 File file = new File(fileName);
			 
			 FileTransfer fileTransfer = fileTransferManager.offerFile(Paths.get(fileName), fileInfo.path, 
					 Jid.of(targetUsername + "@"+ server + "/" + targetResource), 60000).getResult();
	
			 fileTransfer.addFileTransferStatusListener(e -> {
				 	FileTransferStatusEvent ev = e;
				    try {
				    	System.out.println(ev.getStatus().name());
						updateProgress(e.getBytesTransferred(), file.length(), index, list, remoteKnownFiles);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
			 });

			fileTransfer.transfer();
			 
		 } else {
			 
			 if(fileInfo.type == FileInfoType.DIR) {
				 transfer(0, fileInfo.files, remoteKnownFiles);/** transfer dir */
			 }
			 
			 transfer(index+1, list, remoteKnownFiles);/** next */
		 }
	}
	
	
	private void updateProgress(long bytesTransferred, long length, int index, List<FileInfo> list, HashMap<String, Long> remoteKnownFiles) throws XmppException, IOException {
		
		System.out.println("enviando:" + bytesTransferred + " len: " + length);
		
		if(bytesTransferred >= length) {
			transfer(index+1, list, remoteKnownFiles);
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
		
		lastRefreshFiles = System.currentTimeMillis();
		
		remoteFiles = message.list;
		
		filesResponseCallback.receiveList(remoteFiles);
	}	
	
	public void sendMessage(String msg) {
		xmppClient.send(new Message(Jid.of(targetUsername + "@" + server + "/" + targetResource), Message.Type.CHAT, msg));
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

	public static interface ServiceListerner {
		public void update(String eventDescription);
		public void error(String message);
	}

	public void close() throws XmppException {
		xmppClient.close();
	}
}
