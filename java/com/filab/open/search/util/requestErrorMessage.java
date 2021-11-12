
package com.filab.open.search.util;

import org.json.simple.JSONObject;




import com.filab.open.search.command.TotalContentsRequest;
import com.filab.open.search.command.TotalWordRequest;

import net.sf.json.JSONException;


public class requestErrorMessage {
	
	

	
	public boolean isInvalidTotal(TotalContentsRequest totalContentsRequest,JSONObject sendJsonObject) throws JSONException {
		if(totalContentsRequest == null) {
			sendJsonObject.put("RESULT_CODE", "SER_1001");
			sendJsonObject.put("RESULT_MSG", "No results were found for your search");
			//status.setResErrDetail("RecType 값이 null이거나 null String");
			return false;
		}else {
			System.out.println(totalContentsRequest.getQuery());
			if(totalContentsRequest.getQuery() == null || totalContentsRequest.getQuery().equals("")) {
				sendJsonObject.put("RESULT_CODE", "SER_1002");
				sendJsonObject.put("RESULT_MSG", "There are no search queries");				
				return false;
			}
			
			
		}
		return true;
	}
	
	public boolean isInvalidAuto(TotalWordRequest totalContentsRequest,JSONObject sendJsonObject) throws JSONException {
		if(totalContentsRequest == null) {
			sendJsonObject.put("RESULT_CODE", "SER_1001");
			sendJsonObject.put("RESULT_MSG", "No results were found for your search");
			//status.setResErrDetail("RecType 값이 null이거나 null String");
			return false;
		}else {
			if(totalContentsRequest.getQuery() == null || totalContentsRequest.getQuery().equals("")) {
				sendJsonObject.put("RESULT_CODE", "SER_1002");
				sendJsonObject.put("RESULT_MSG", "There are no search queries");				
				return false;
			}
			/*
			if(totalContentsRequest.getProduct_id() == null || totalContentsRequest.getProduct_id().equals("")) {
				sendJsonObject.put("RESULT_CODE", "SER_1003");
				sendJsonObject.put("RESULT_MSG", "There is no product code information");				
				return false;
			}
			*/
			
		}
		return true;
	}
	
	
}
