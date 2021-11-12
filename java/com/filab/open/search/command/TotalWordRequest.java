
package com.filab.open.search.command;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;

import com.filab.open.search.util.KcubeUtils;
import com.filab.open.search.util.ObjectUtils;


public class TotalWordRequest implements SearchRequest {
	
	private String query;
	private String startCount;
	private String listCount;
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String squery) {
		this.query = squery;
	}
	
	public String getStartCount() {
		return startCount;
	}
	public void setStartCount(String sstartCount) {
		this.startCount = sstartCount;
	}
	
	public String getListCount() {
		return listCount;
	}
	public void setListCount(String slistCount) {
		this.listCount = slistCount;
	}
	
	
public String toQueryString(String uriEncoding) throws UnsupportedEncodingException {
		
		
		
		
		return "?query=" + URLEncoder.encode(StringUtils.defaultString(query, ""), uriEncoding);
	}


public String toRequestQueryString() throws UnsupportedEncodingException {
	
	
	String request_str = "?query=" + query;
		
		return request_str;
}

public void log(PrintWriter writer) {		
	
	writer.println("Request query"+ StringUtils.defaultString(query, ""));
	//writer.println("\tsearchWord=" + StringUtils.defaultString(searchWord, ""));
	
}

public String getParameterString() {
	return ObjectUtils.toString(this);
}

}
