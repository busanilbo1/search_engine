
package com.filab.open.search.command;

public class ReloadRequest {
	private String clientCount;
	private String filePath;
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getClientCount() {
		return clientCount;
	}

	public void setClientCount(String clientCount) {
		this.clientCount = clientCount;
	}
}
