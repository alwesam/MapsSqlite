package com.vandevsam.sharespot.data;

public class MyGroupObj {
	private long id;
	private String name;
	private String description;
	private String type;
	private String status;

	public MyGroupObj() {
	}

	public MyGroupObj(String name, String description, String type) {
		this.setName(name);
		this.setDescription(description);
		this.setType(type);
	}

	public MyGroupObj(String name, String description, String type,
			String status) {
		this.setName(name);
		this.setDescription(description);
		this.setType(type);
		this.setStatus(status);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
