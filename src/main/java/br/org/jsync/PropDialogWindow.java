package br.org.jsync;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;


public class PropDialogWindow extends PropDialog{

	PropDialogEvents eventsLit;
	
	public PropDialogWindow(Frame parent, PropDialogEvents eventsLit) {
		super(parent, true);
		this.eventsLit = eventsLit;
		
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PropDialogWindow.this.onSave();
			}
		});
		
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PropDialogWindow.this.setVisible(false);
			}
		});		
		
		this.onLoad();
	}

	public void onSave() {
		
		Properties prop = new Properties();
		prop.setProperty("server", txtLocalServer.getText());
		prop.setProperty("local.username", txtLocalUserName.getText());
		prop.setProperty("local.secret", txtLocalPassword.getText());
		prop.setProperty("local.home", txtLocalSharedFolder.getText());
		prop.setProperty("local.resource", txtLocalMachine.getText());
		prop.setProperty("target.resource", txtTargetMachine.getText());
		prop.setProperty("target.username", txtTargetUserName.getText());
		prop.setProperty("target.access.key", txtTargetAccessKey.getText());
		prop.setProperty("local.access.key", txtLocalAccesKey.getText());
		
		this.eventsLit.onSave(prop);
	}
	
	public void onLoad() {
		
		Properties prop = new Properties();
		this.eventsLit.onLoad(prop);
		
		txtLocalServer.setText(prop.getProperty("server"));
		txtLocalUserName.setText(prop.getProperty("local.username"));
		txtLocalPassword.setText(prop.getProperty("local.secret"));
		txtLocalSharedFolder.setText(prop.getProperty("local.home"));
		txtLocalMachine.setText(prop.getProperty("local.resource"));
		txtTargetMachine.setText(prop.getProperty("target.resource"));
		txtTargetUserName.setText(prop.getProperty("target.username"));
		txtTargetAccessKey.setText(prop.getProperty("target.access.key"));
		txtLocalAccesKey.setText(prop.getProperty("local.access.key"));
		
	}
	
	public static interface PropDialogEvents {
		public void onSave(Properties prop);
		public void onLoad(Properties prop);
	}
	
}
