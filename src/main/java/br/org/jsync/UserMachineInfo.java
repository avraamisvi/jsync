package br.org.jsync;

public class UserMachineInfo {
	public String ip;
	public String secret;
	public String user;
	public String id;
	public String port;

	public String getIp() {
		return ip;
	}

	public UserMachineInfo setIp(String ip) {
		this.ip = ip;
		return this;
	}

	public String getSecret() {
		return secret;
	}

	public UserMachineInfo setSecret(String secret) {
		this.secret = secret;
		return this;
	}

	public String getUser() {
		return user;
		
	}

	public UserMachineInfo setUser(String user) {
		this.user = user;
		return this;
	}

	public String getId() {
		return id;
	}

	public UserMachineInfo setId(String id) {
		this.id = id;
		return this;
	}

	public String getPort() {
		return port;
	}

	public UserMachineInfo setPort(String port) {
		this.port = port;
		
		return this;
	}
}