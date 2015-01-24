package com.vandevsam.sharespot.data;

public class MyMarkerObj {
	private long id;
	private String title;
	private String snippet;
	private String position;
	
	private String group;
	
	private String status;
	
	public MyMarkerObj(){
	}
	
	public MyMarkerObj (long id, String title, String snippet, String position, String status){
		this.setId(id);
		this.setTitle(title);
		this.setSnippet(snippet);
		this.setPosition(position);	
		this.setStatus(status);	
	}
	
	public MyMarkerObj (long id, String title, String snippet, String position){
		this.setId(id);
		this.setTitle(title);
		this.setSnippet(snippet);
		this.setPosition(position);		
	}
	
	//*****ADD a new field in markers table: group
	public MyMarkerObj (String title, String snippet, String position, String group, String status){
		this.setTitle(title);
		this.setSnippet(snippet);
		this.setPosition(position);	
		this.setGroup(group);
		this.setStatus(status);
	}
	//*****************
	
	public MyMarkerObj (String title, String snippet, String position, String status){
		this.setTitle(title);
		this.setSnippet(snippet);
		this.setPosition(position);	
		this.setStatus(status);
	}
	
	public MyMarkerObj (String title, String snippet, String position){
		this.setTitle(title);
		this.setSnippet(snippet);
		this.setPosition(position);		
	}
	
	//so as to access data based on position (coordinates) only
	public MyMarkerObj (String position){	
		this.setPosition(position);		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}	
	
	//**********
	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	//**************
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

}
