
package com.filab.open.search.util;

import net.sf.json.JSONException;

import org.json.simple.JSONObject;


public class responseErrorMessage {
	public void getMessageCode(int messageCode,JSONObject sendJsonObject) throws JSONException  {
		
		sendJsonObject.put("RESULT_CODE", messageCode);
		if(messageCode == 8){
			//sendJsonObject.put("RESULT_MSG", messageStatusCode.M_RES_CODE_8);
		}
		
		
	}
}
