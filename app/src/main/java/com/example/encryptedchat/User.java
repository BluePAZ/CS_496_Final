package com.example.encryptedchat;

public class User {
	private String username;
    private String keyValue;


    public String getKeyValue() {
        return keyValue;
    }
    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }
	public String getUsername(){
		return this.username;
	}
	public void setUsername(String s){
		this.username = s;
	}

}
