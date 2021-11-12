
package com.filab.open.search.command;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.filab.open.search.util.KcubeUtils;
import com.filab.open.search.util.ObjectUtils;

public class TotalContentsRequest implements SearchRequest {

	
	private String query;
	private String collection;
	private String startDate;
	private String endDate;
	private String sort;
	private String sfield;
	private String startCount;
	private String listCount;
	private String writer;
	private String userkey;
	
	
	public void setUserkey(String suserkey) {
		this.userkey = suserkey;
	}
	
	public String getUserkey() {
		return userkey;
	}
	
	
	public void setWriter(String swriter) {
		this.writer = swriter;
	}
	
	public String getWriter() {
		return writer;
	}
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String squery) {
		this.query = squery;
	}
	
	public String getCollection() {
		return collection;
	}
	public void setCollection(String scollection) {
		this.collection = scollection;
	}
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String sstartDate) {
		this.startDate = sstartDate;
	}
	
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String sendDate) {
		this.endDate = sendDate;
	}
	
	public String getSort() {
		return sort;
	}
	public void setSort(String ssort) {
		this.sort = ssort;
	}
	
	public String getSfield() {
		return sfield;
	}
	public void setSfield(String ssfield) {
		this.sfield = ssfield;
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
	
	
	
public String toRequestQueryString() throws UnsupportedEncodingException {
		
		
		String request_str = "?query=" + StringUtils.defaultString(query, "")
				+ "&collection=" + StringUtils.defaultString(collection, "")
				+ "&startDate=" +StringUtils.defaultString(startDate, "")
				+ "&endDate=" + StringUtils.defaultString(endDate, "")
				+ "&sort=" + StringUtils.defaultString(sort, "")
				+ "&sfield=" + StringUtils.defaultString(sfield, "")
				+ "&startCount=" + StringUtils.defaultString(startCount, "")
				+ "&writer=" + StringUtils.defaultString(writer, "")
				+ "&userkey=" + StringUtils.defaultString(userkey, "")
				
				+ "&listCount=" + StringUtils.defaultString(listCount, "");
				
				
		
		return request_str;
}

	public String toQueryString(String uriEncoding) throws UnsupportedEncodingException {
		
		return "?query=" + URLEncoder.encode(StringUtils.defaultString(query, ""), uriEncoding)
				+ "&collection=" + URLEncoder.encode(StringUtils.defaultString(collection, ""), uriEncoding)
				+ "&startDate=" + URLEncoder.encode(StringUtils.defaultString(startDate, ""), uriEncoding)
				+ "&sort=" + URLEncoder.encode(StringUtils.defaultString(sort, ""), uriEncoding)
				+ "&endDate=" + URLEncoder.encode(StringUtils.defaultString(endDate, ""), uriEncoding)
				+ "&sfield=" + URLEncoder.encode(StringUtils.defaultString(sfield, ""), uriEncoding)
				+ "&startCount=" + URLEncoder.encode(StringUtils.defaultString(startCount, ""), uriEncoding)
				+ "&writer=" + URLEncoder.encode(StringUtils.defaultString(writer, ""), uriEncoding)
				+ "&userkey=" + URLEncoder.encode(StringUtils.defaultString(userkey, ""), uriEncoding)
				
				+ "&listCount=" + URLEncoder.encode(StringUtils.defaultString(listCount, ""), uriEncoding);
		
		
			
	}
	

	public static String StringReplaceSpace(String str){ 
		String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
		str =str.replaceAll(match, "%20"); 
		return str; 
	}
	
	
	
	public static String getSTRFilter2(String str){ 	 
		 
			  String str_imsi   = "";
			  try{
				  String []filter_word ={"\\+"};
				  for(int i=0;i<filter_word.length;i++){ 
				   //while(str.indexOf(filter_word[i]) >= 0){ 
					  str_imsi = str.replaceAll(filter_word[i],"%20"); 
					  str = str_imsi; 
				   //} 
				  } 
			  }catch(Exception e){
				  
			  }		
			  try{
				  str = str.replaceAll("\\%2B", "%20");
			  }catch(Exception e){
				  
			  }	
			return str; 
		 } 
	
	public void log(PrintWriter writer) {
		
	}
	
	public String getParameterString() {
		return ObjectUtils.toString(this);
	}
	
}
