package br.org.jsync;

public class JSyncMessage {
	
	public static final String LIST_REQ = "LIST_REQ";
	public static final String LIST_RES = "LIST_RES";
	public static final String UPDATE_REQ = "UPDATE_REQ";
	
	String name;

	public JSyncMessage(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
