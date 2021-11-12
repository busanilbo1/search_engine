
package com.filab.open.search.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.filab.open.search.command.TotalContentsRequest;
import com.filab.open.search.command.TotalWordRequest;
import com.filab.open.search.service.SearchService;
import com.filab.open.search.util.Chosung;
import com.filab.open.search.util.ConvertEntoKo;
import com.filab.open.search.util.KcubeUtils;
import com.filab.open.search.util.requestErrorMessage;

@Controller
public class TotalWordController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private SearchService searchService;
	
	public static ConvertEntoKo hanToEngKey = ConvertEntoKo.getInstance();
	
	private static String JSON_ENCODING = "UTF-8";
	
	// ㄱ      ㄲ      ㄴ      ㄷ      ㄸ      ㄹ      ㅁ      ㅂ      ㅃ      ㅅ      ㅆ      ㅇ      ㅈ      ㅉ      ㅊ      ㅋ      ㅌ      ㅍ      ㅎ
	public static char[] ChoSung   = { 0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145, 0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };
    // ㅏ      ㅐ      ㅑ      ㅒ      ㅓ      ㅔ      ㅕ      ㅖ      ㅗ      ㅘ      ㅙ      ㅚ      ㅛ      ㅜ      ㅝ      ㅞ      ㅟ      ㅠ      ㅡ      ㅢ      ㅣ
	public static char[] JwungSung = { 0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158, 0x3159, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162, 0x3163 };
    // ㄱ      ㄲ      ㄳ      ㄴ      ㄵ      ㄶ      ㄷ      ㄹ      ㄺ      ㄻ      ㄼ      ㄽ      ㄾ      ㄿ      ㅀ      ㅁ      ㅂ      ㅄ      ㅅ      ㅆ      ㅇ      ㅈ      ㅊ      ㅋ      ㅌ      ㅍ      ㅎ
	public static char[] JongSung  = { 0x0000,      0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a, 0x313b, 0x313c, 0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145, 0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };

	@Autowired
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
    
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping("/autoComplete")	
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String searchAutoComplete(@ModelAttribute TotalWordRequest totalWordRequest,@RequestHeader HttpHeaders headers,HttpServletRequest req) throws Exception {
		
		//System.out.println(curRequest.getParameterString());
		long startTime = System.currentTimeMillis();
		System.out.println("[REQUEST TotalWordRequest]"+totalWordRequest.toRequestQueryString());
		
		String szJsonData = "";
		
		try {
				
			
			   Enumeration headerNames = req.getHeaderNames();
		        while(headerNames.hasMoreElements()) {
		            String name = (String)headerNames.nextElement();
		            String value = req.getHeader(name);
		            System.out.println(name + " : " + value);
		        }		        
			
		    
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		JSONObject ret_str = new JSONObject();
		
		 String search_server_url = System.getProperty("autoCompleteServerUrl");
		 JSONObject sendJsonObject = new JSONObject();
		 
		 requestErrorMessage reqinValidate = new requestErrorMessage();
			boolean reqInValidates = reqinValidate.isInvalidAuto(totalWordRequest,sendJsonObject);
		
			
			if(!reqInValidates){
				
				List<Object> cs_listData = new ArrayList<Object>();
				
				 sendJsonObject.put("total", 0);
				 sendJsonObject.put("errorCode", sendJsonObject.get("RESULT_CODE").toString());
				 sendJsonObject.put("errorMessage", sendJsonObject.get("RESULT_MSG").toString());
				 sendJsonObject.put("CONTENTS_LIST", cs_listData);
				 
				 cs_listData = null;
				szJsonData = sendJsonObject.toString();
	        	try {
					
							szJsonData = KcubeUtils.charSetConvertEuckr(szJsonData);
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
	        	
			}else {
				
				try {
					String url = "";
					String query_str = "";
					
					query_str = queryMap(totalWordRequest,"Y");
					
					query_str = getSTRFilter(query_str);
					
					
					String start_cnt = "0";
					String size_cnt = "10";
					if(totalWordRequest.getStartCount() != null && totalWordRequest.getStartCount().trim().length() > 0) {
						start_cnt = totalWordRequest.getStartCount();
					}
					
					if(totalWordRequest.getListCount() != null && totalWordRequest.getListCount().trim().length() > 0) {
						size_cnt = totalWordRequest.getListCount();
					}
					
					
					//url = 	search_server_url+"q=("+query_str+")&pretty=true&from="+start_cnt+"&size="+size_cnt;
					
					//url = 	search_server_url+"pretty=true&from="+start_cnt+"&size="+size_cnt+"&sort=_score:desc,score:desc";
					
					try {
						String[] query_exp = totalWordRequest.getQuery().split("[ ]");
						
						if(query_exp.length > 1) {
							url = 	search_server_url+"pretty=true&from="+start_cnt+"&size="+size_cnt;
						}else {
							url = 	search_server_url+"pretty=true&from="+start_cnt+"&size="+size_cnt;
						}
					}catch(Exception e) {
						url = 	search_server_url+"pretty=true&from="+start_cnt+"&size="+size_cnt;
					}
					
					url = 	search_server_url+"pretty=true&from="+start_cnt+"&size="+size_cnt;
					System.out.println("AUTO_SEARCH_URL --->"+url);
					//ret_str = elSearch(url);
					
					ret_str = elSearch2(url,query_str,"Y");
					
					if(ret_str != null) {
						String total_search_num = "0";
			        	JSONObject hits = (JSONObject) ret_str.get("hits");
			        	
			        	total_search_num = hits.get("total").toString();
			        	System.out.println("AUTO_SEARCH_QUERY|"+totalWordRequest.getQuery()+"&& AUTO_SEARCH_TOTAL|"+total_search_num);
			        	
			        	// 검색결과가 없을 경우 초성검색처리를 한다.
			        	/*
			        	 query_str = queryMap(totalWordRequest,"N");
			        	 */
			        
			        	try {
			        		int total_search_s = Integer.valueOf(total_search_num).intValue();
			        		
			        		if(total_search_s == 0) {
			        			query_str = queryMap(totalWordRequest,"N");
			        			
			        			//url = 	search_server_url+"q=("+query_str+")&pretty=true&from="+start_cnt+"&size="+size_cnt;
								
								//System.out.println("Re url--->"+url);
								//ret_str = elSearch(url);
								
								ret_str = elSearch2(url,query_str,"N");
								
								
								total_search_num = "0";
					        	hits = (JSONObject) ret_str.get("hits");
					        	
					        	total_search_num = hits.get("total").toString();
					        	//System.out.println("Re total_search_num--->"+total_search_num);
					        	
					        	
			        		}
			        	}catch(Exception e) {
			        		
			        	}
			        	
			        	JSONArray varsArray = (JSONArray) hits.get("hits");
			        	
			        	JSONParser SQ_search_parser = new JSONParser();
			        	
			        	List<Object> cs_listData = new ArrayList<Object>();
			        	
			        	sendJsonObject.put("total", total_search_num);
			        	
			        	sendJsonObject.put("result_code", "200");
			        	sendJsonObject.put("result_message", "success");
			        	
			        	int high_cnt = 0;
			        	String f_hight_str = "";
			        	String f_hight_str_none = "";
			        	double pre_score = 0.000000;
			        	
			        	
			        	for(int i=0;i<varsArray.size();i++) {
			        		JSONObject Sjson_arr2 = new JSONObject();
			        		
			        		Sjson_arr2 = (JSONObject) SQ_search_parser.parse(varsArray.get(i).toString());
			        		
			        		
			        		JSONObject json_arr3 = new JSONObject();
							JSONParser SQ_search_parser2 = new JSONParser();
							json_arr3 = (JSONObject) SQ_search_parser2.parse(Sjson_arr2.get("_source").toString());
							
							
							JSONObject json_arr_highlight = new JSONObject();
							JSONParser highlight_parser = new JSONParser();
							try{ json_arr_highlight = (JSONObject) highlight_parser.parse(Sjson_arr2.get("highlight").toString()); }catch(Exception e) {}
							
							
							String    keyword_highlight = "";
							try{ keyword_highlight = json_arr_highlight.get("relword").toString().trim(); }catch(Exception e) {keyword_highlight = "";}
							
							if(keyword_highlight != null && keyword_highlight.trim().length() > 0) {
								high_cnt++;
								f_hight_str =keyword_highlight; 
								break;
								
							}
							
							String    relwordchosung_highlight = "";
							try{ relwordchosung_highlight = json_arr_highlight.get("relwordchosung").toString().trim(); }catch(Exception e) {relwordchosung_highlight = "";}
							if(relwordchosung_highlight != null && relwordchosung_highlight.trim().length() > 0) {
								//high_cnt++;
								f_hight_str_none =relwordchosung_highlight; 
								break;
								
							}
							
							
			        	}
			        	
			        	for(int i=0;i<varsArray.size();i++) {
			        		
			        		//JSONObject resultData = (JSONObject) varsArray.get(i);
			        		
			        		JSONObject Sjson_arr2 = new JSONObject();
			        		
			        		Sjson_arr2 = (JSONObject) SQ_search_parser.parse(varsArray.get(i).toString());
			        		
			        		
			        		JSONObject json_arr3 = new JSONObject();
							JSONParser SQ_search_parser2 = new JSONParser();
							json_arr3 = (JSONObject) SQ_search_parser2.parse(Sjson_arr2.get("_source").toString());
							
							
							JSONObject json_arr_highlight = new JSONObject();
							JSONParser highlight_parser = new JSONParser();
							try{ json_arr_highlight = (JSONObject) highlight_parser.parse(Sjson_arr2.get("highlight").toString()); }catch(Exception e) {}
							
							
							
							String    keyword_highlight = "";
							try{ keyword_highlight = json_arr_highlight.get("relword").toString().trim(); }catch(Exception e) {keyword_highlight = "";}
							
							//System.out.println("keyword_highlight 1--->"+keyword_highlight);
							try {
								keyword_highlight = keyword_highlight.replaceAll("<\\\\/b>","</b>");
							}catch(Exception ee) {
								
							}
							//System.out.println("keyword_highlight 2--->"+keyword_highlight);
							String    relwordchosung_highlight = "";
							try{ relwordchosung_highlight = json_arr_highlight.get("relwordchosung").toString().trim(); }catch(Exception e) {relwordchosung_highlight = "";}
							//System.out.println("relwordchosung_highlight 1-->"+relwordchosung_highlight);
							try {
								relwordchosung_highlight = relwordchosung_highlight.replaceAll("<\\\\/b>","</b>");
							}catch(Exception ee) {
								
							}
							
							//System.out.println("relwordchosung_highlight 2--->"+relwordchosung_highlight);
							
							String    keyword = "";
							 try{ keyword = json_arr3.get("relword").toString().trim(); }catch(Exception e) {keyword = "";}
							 
							// System.out.println("keyword 1--->"+keyword);
							 try {
								 keyword = keyword.replaceAll("<\\\\/b>","</b>");
								}catch(Exception ee) {
									
								}
							
							 
							 String    relwordjaso_highlight = "";
							 try{ 
								 relwordjaso_highlight = json_arr_highlight.get("relwordjaso").toString().trim();
								 relwordjaso_highlight = relwordjaso_highlight.replaceAll("<\\\\/b>","</b>");
								// gethanglejohap(relwordjaso);
							 }catch(Exception e) {relwordjaso_highlight = "";}
							 
							
							// System.out.println("relwordchosung 2--->"+relwordchosung);
							 LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
							
							//// System.out.println("keyword_highlight 2--->"+keyword_highlight);
							// System.out.println("relwordchosung_highlight 2--->"+relwordchosung_highlight);
							// System.out.println("relwordjaso_highlight 2--->"+relwordjaso_highlight);
							 /*
							 if(keyword_highlight != null && keyword_highlight.trim().length() > 0) {
								// System.out.println("relwordchosung_highlight--->"+relwordchosung_highlight);
							 }else {
								 
								 String keyword_re = "";
								 if(relwordchosung_highlight != null && relwordchosung_highlight.trim().length() > 0) {
									 
									 String[] relwordchosung_highlight_exp = relwordchosung_highlight.split("[ ]");
									 String[] keyword_exp = keyword.split("[ ]");
									 for(int j=0;j<relwordchosung_highlight_exp.length;j++) {
										 String comma_str = " ";
										 if(j==0) {
											 comma_str = "";
										 }
										 if(relwordchosung_highlight_exp[j] != null) {
											 if(relwordchosung_highlight_exp[j].toString().contains("<b>")) {
												 
												 keyword_re = keyword_re + comma_str +"<b>"+keyword_exp[j].toString()+"</b>";
											 }else {
												 keyword_re = keyword_re + comma_str +keyword_exp[j].toString();
											 }
										 }
										 
									 }
									 
									 if(keyword_re != null && keyword_re.length() > 0) {
										 keyword_highlight = keyword_re;
									 }
									 
									// System.out.println("keyword_re 2--->"+keyword_re);
									 
								 }else if(relwordjaso_highlight != null && relwordjaso_highlight.trim().length() > 0) {
									 keyword_re = "";
									 //relwordjaso_highlight
									 String[] relwordchosung_highlight_exp = relwordjaso_highlight.split("[ ]");
									 String[] keyword_exp = keyword.split("[ ]");
									 for(int j=0;j<relwordchosung_highlight_exp.length;j++) {
										 String comma_str = " ";
										 if(j==0) {
											 comma_str = "";
										 }
										 if(relwordchosung_highlight_exp[j] != null) {
											 if(relwordchosung_highlight_exp[j].toString().contains("<b>")) {
												 
												 keyword_re = keyword_re + comma_str +"<b>"+keyword_exp[j].toString()+"</b>";
											 }else {
												 keyword_re = keyword_re + comma_str +keyword_exp[j].toString();
											 }
										 }
										 
									 }
									 
									 if(keyword_re != null && keyword_re.length() > 0) {
										 keyword_highlight = keyword_re;
									 }
									 
								 }
							 }
							 
							 */
							 //f_hight_str_none
							 /*
							 if(high_cnt <= 0) {
								 if(relwordchosung_highlight != null && relwordchosung_highlight.trim().length() > 0) {
									 try {
										 String[] keyword_exp = null;
										 if(keyword_highlight != null && keyword_highlight.trim().length() > 0) {
											  
										 }else {
											 
											 String[] keyword_exp_f = null;
											 String bold_str = "";
											 keyword_exp_f = f_hight_str_none.split("[ ]");
											 for(int j=0;j<keyword_exp_f.length;j++) {
												 if(keyword_exp_f[j] != null) {
													 if(keyword_exp_f[j].toString().contains("<b>")) {
														 bold_str = keyword_exp_f[j].toString();
														 break;
													 }
												 }
											 }
											// System.out.println("bold_str--->"+bold_str);
											 
											 keyword_exp = keyword.split("[ ]");
											 
											//System.out.println("3--->"+keyword);
											//System.out.println("3--->"+relwordchosung_highlight);
											
											if(bold_str != null && bold_str.toString().length() > 0) {
												// System.out.println("bold_str--->"+bold_str);
												 bold_str = bold_str.replaceAll("[<b>]", "");
												 bold_str = bold_str.replaceAll("[</b>]", "");
												// bold_str = bold_str.replaceAll("[\\\\]", "");
												 
												 bold_str = getSTRFilterK4(bold_str);
												 
												 
												//System.out.println("bold_str--->"+bold_str);
												 String[] keywordlist_exp = null;
												 String[] keyword_exp_s = null;
												 String re_bold_str = "";
												 String sp_str = " ";
												 keywordlist_exp = relwordchosung.split("[ ]");
												 keyword_exp_s = keyword.split("[ ]");
												 for(int j=0;j<keywordlist_exp.length;j++) {
													 if(j==0) {
														 sp_str="";
													 }
													 if(keywordlist_exp[j] != null) {
														 if(keywordlist_exp[j].toString().contains(bold_str)) {
															 re_bold_str =  re_bold_str+ " <b>"+ keyword_exp_s[j].toString()+"</b> ";
															
														 }else {
															 re_bold_str =  re_bold_str+  " "+keyword_exp_s[j].toString();
														 }
													 }
												 }
												 
												// System.out.println("re_bold_str--->"+re_bold_str);
												 if(re_bold_str != null && re_bold_str.trim().length() > 0) {
													 keyword_highlight = re_bold_str;
												 }
											 }else {
											
													
													 String[] keyword_exp2 = relwordchosung_highlight.split("[ ]");
													 String add_str = "";
													 String sp_str = " ";
													 for(int j=0;j<keyword_exp2.length;j++) {
														 
														 if(j==0) {
															 sp_str="";
														 }
														 if(keyword_exp2[j] != null) {
															 if(keyword_exp2[j].toString().contains("<b>")) {
																// System.out.println("2--->"+keyword_exp[j].toString());
																 if(keyword_exp2[j].toString().contains("<b>")) {
																	 
																	  
																	 add_str = add_str + sp_str+ "<b>"+keyword_exp[j].toString()+"</b>";
																 }else {
																	 add_str = add_str + sp_str+ keyword_exp[j].toString();
																 }
															 }else {
																 //	System.out.println("2--->"+keyword_exp[j].toString());
																	 add_str = add_str + sp_str+ keyword_exp[j].toString();
																 
															 }
														 }
													 }
													
													 
													// System.out.println("3--->"+add_str);
													 if(keyword_highlight.trim().length() <= 0) {
														 keyword_highlight = add_str;
													 }
											 
											 }
										 }									 
										 
									 }catch(Exception e) {}
								 }
							 }else {
								// System.out.println("keyword_highlight--->"+keyword_highlight);
								
								 if(!keyword_highlight.contains("<b>")) {
									 
									 String[] keyword_exp = null;
									 String bold_str = "";
									 keyword_exp = f_hight_str.split("[ ]");
									 for(int j=0;j<keyword_exp.length;j++) {
										 if(keyword_exp[j] != null) {
											 if(keyword_exp[j].toString().contains("<b>")) {
												 bold_str = keyword_exp[j].toString();
												 break;
											 }
										 }
									 }
									 
									 
									 if(bold_str != null && bold_str.toString().length() > 0) {
										// System.out.println("bold_str--->"+bold_str);
										 bold_str = bold_str.replaceAll("[<b>]", "");
										 bold_str = bold_str.replaceAll("[</b>]", "");
										// bold_str = bold_str.replaceAll("[\\\\]", "");
										 
										 bold_str = getSTRFilterK4(bold_str);
										 
										 
										// System.out.println("bold_str--->"+bold_str);
										 String[] keywordlist_exp = null;
										 String re_bold_str = "";
										 String sp_str = " ";
										 keywordlist_exp = keyword.split("[ ]");
										 for(int j=0;j<keywordlist_exp.length;j++) {
											 if(j==0) {
												 sp_str="";
											 }
											 if(keywordlist_exp[j] != null) {
												 if(keywordlist_exp[j].toString().contains(bold_str)) {
													 re_bold_str =  re_bold_str+ " <b>"+ keywordlist_exp[j].toString()+"</b> ";
													
												 }else {
													 re_bold_str =  re_bold_str+  " "+keywordlist_exp[j].toString();
												 }
											 }
										 }
										 
										// System.out.println("re_bold_str--->"+re_bold_str);
										 if(re_bold_str != null && re_bold_str.trim().length() > 0) {
											 keyword_highlight = re_bold_str;
										 }
									 }
								 }else {
									 if(relwordchosung_highlight != null && relwordchosung_highlight.trim().length() > 0) {
										 try {
											 String[] keyword_exp = null;
											 if(keyword_highlight != null && keyword_highlight.trim().length() > 0) {
												  
											 }else {
												 keyword_exp = keyword.split("[ ]");
												 
												// System.out.println("3--->"+keyword);
												// System.out.println("3--->"+relwordchosung_highlight);
												 String[] keyword_exp2 = relwordchosung_highlight.split("[ ]");
												 String add_str = "";
												 String sp_str = " ";
												 for(int j=0;j<keyword_exp2.length;j++) {
													 
													 if(j==0) {
														 sp_str="";
													 }
													 if(keyword_exp2[j] != null) {
														 if(keyword_exp2[j].toString().contains("<b>")) {
															// System.out.println("2--->"+keyword_exp[j].toString());
															 if(keyword_exp2[j].toString().contains("<b>")) {
																 
																  
																 add_str = add_str + sp_str+ "<b>"+keyword_exp[j].toString()+"</b>";
															 }else {
																 add_str = add_str + sp_str+ keyword_exp[j].toString();
															 }
														 }else {
															 //	System.out.println("2--->"+keyword_exp[j].toString());
																 add_str = add_str + sp_str+ keyword_exp[j].toString();
															 
														 }
													 }
												 }
												 
												// System.out.println("3--->"+add_str);
												 if(keyword_highlight.trim().length() <= 0) {
													 keyword_highlight = add_str;
												 }
												 
												 
											 }
											
											 
										 }catch(Exception e) {}
									 }
								 }
								
								// f_hight_str
								 
							 }
								 
								 */
							
							  if(keyword_highlight != null && keyword_highlight.trim().length() > 0) {
								  
								 // keyword_highlight = keyword_highlight.replaceAll("[[]", "");
								  
									 try {
										 keyword_highlight = getSTRFilterK5(keyword_highlight);
									 }catch(Exception e) {
										 
									 }
								  m.put("keyword", keyword_highlight.trim());		
								  //System.out.println(" keyword_re--->"+keyword_highlight.trim());
							  }else {
								  m.put("keyword", keyword.trim());		
								 // System.out.println(" keyword--->"+keyword.trim());
							  }
					        		
								
								String lank_str = "";
								
								try {
									lank_str = Sjson_arr2.get("_score").toString();
									 m.put("score", lank_str);
								}catch(Exception e) {}	
								cs_listData.add(m);
							
								m = null;
								
								
								// System.out.println(" autocomplete--->"+keyword+"//"+lank_str);							 
									
								sendJsonObject.put("CONTENTS_LIST", cs_listData);
								
	
								
								
			        	}
			        	
			        	cs_listData=null;
			        	
			        	szJsonData = sendJsonObject.toString();
			        	try {
							
									szJsonData = KcubeUtils.charSetConvertEuckr(szJsonData);
								
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        	
			        	
					}else {
						List<Object> cs_listData = new ArrayList<Object>();
						
						 sendJsonObject.put("total", 0);
						 sendJsonObject.put("errorCode", "1001");
						 sendJsonObject.put("errorMessage", "검색결과 없음!");
						 sendJsonObject.put("CONTENTS_LIST", cs_listData);
						 
						 cs_listData = null;
						szJsonData = sendJsonObject.toString();
			        	try {
							
									szJsonData = KcubeUtils.charSetConvertEuckr(szJsonData);
								
						} catch (Exception e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					}
		        	
		        	
				}catch(Exception e) {
					List<Object> cs_listData = new ArrayList<Object>();
					
					 sendJsonObject.put("total", 0);
					 sendJsonObject.put("errorCode", "1001");
					 sendJsonObject.put("errorMessage", "검색결과 없음!");
					 sendJsonObject.put("CONTENTS_LIST", cs_listData);
					 
					 cs_listData = null;
					szJsonData = sendJsonObject.toString();
		        	try {
						
								szJsonData = KcubeUtils.charSetConvertEuckr(szJsonData);
							
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}
			
			}
		return szJsonData;
	}
	

	public static String queryMap(TotalWordRequest curRequest,String total_ny){
		String ret_str = "";
	
		String SearchField_str = "";
		
		String range_query = "";
		
		String sfield_str = "";
		
		
		String query_encode = "";
		String query_encode2 = "";
		String  ret_st = "";
		String  ret_st_jaso = "";
		
		//String  env_conv = "";
		
		String in_str = StringReplaceReNone2(curRequest.getQuery().trim().toLowerCase());
		//System.out.println("in_str:"+in_str);
	
		  ret_st = hangulToJaso(in_str);
       	  ret_st_jaso = hangulToJasoChosung(in_str);
       	
       	 
		   try {
				//query_encode = URLEncoder.encode(curRequest.getQuery(),"UTF-8");
			   
			   query_encode = StringReplaceReNone(curRequest.getQuery().trim().toLowerCase());
			   
			}catch(Exception e) {
				
			}
		   
		    String keyword_eng_conv = "";
	       	try {
	       		keyword_eng_conv = hanToEngKey.getHanKeyToEngKey(curRequest.getQuery().trim().toLowerCase());
	       	}catch(Exception e) {
	       		
	       	}
		   
		
		   
		   if(query_encode2.trim().length() <= 0) {
			   query_encode2 = query_encode;
		    }
		   
		   // ret_st = ret_st.replaceAll("[ ]", "");
		   // ret_st_jaso = ret_st_jaso.replaceAll("[ ]", "");
		    
		    //String ret_st_re =  "(relword:\""+ret_st+ "\")^1 OR ";
		    //String ret_st_jaso_re =  "(relword:\""+ret_st_jaso+ "\")^0.5 OR ";
			
		    String ret_st_re =  "((relwordjaso:\""+ret_st+"\")^2.0)|";
		    
		    if(ret_st_jaso.trim().length() <= 0) {
		    	ret_st_jaso = query_encode;
		    }
		    
		    String ret_st_jaso_re =  "((relwordchosung:"+ret_st_jaso+")^0.0001)|";
			
		    
		    if(ret_st.trim().length() <= 0) {
		    	ret_st_re = "";
		    }
		    
		    if(ret_st_jaso.trim().length() <= 0) {
		    	ret_st_jaso_re = "";
		    }
		    
		   // ret_st_jaso_re = "";
		   // ret_st_re = "";
			//sfield_str = sfield_str +" (relword:\""+query_encode+ "\")^1.5 OR (relword:"+query_encode+")^0.5";
			
			//sfield_str = "("+sfield_str+")";
		    
			
		    if(total_ny.equals("N")) {
		    	//sfield_str =  "((relword:\""+query_encode+"\")^100.0)|((relwordjaso:\""+ret_st.trim()+"\")^80.0)|((relwordchosung:\""+ret_st_jaso.trim()+ "\")^0.01)";
		    	sfield_str = "((relword2:"+ret_st+")^1)"+"((relword2:"+ret_st_jaso+")^1)";
		    }else {
		    	if(ret_st.trim().length() > 0) {
		    		if(ret_st_jaso.length() > 0) {
		    			if(ret_st.length() > 0) {
		    				if(keyword_eng_conv.length() > 0) {
		    					sfield_str =  "((relword:\""+query_encode+"\")^1.0)|((relwordjaso:\""+StringReplaceRe(ret_st.trim())+"\")^80.0)|((relwordchosung:\""+StringReplaceRe(ret_st_jaso.trim())+ "\")^1.0)|((eng_conv:\""+StringReplaceRe(ret_st_jaso.trim())+ "\")^1.0)";
		    				}else {
		    					sfield_str =  "((relword:\""+query_encode+"\")^1.0)|((relwordjaso:\""+StringReplaceRe(ret_st.trim())+"\")^80.0)|((relwordchosung:\""+StringReplaceRe(ret_st_jaso.trim())+ "\")^1.0)";
		    				}
		    			}else {
		    				if(keyword_eng_conv.length() > 0) {
		    					sfield_str =  "((relword:\""+query_encode+"\")^1.0)|((relwordchosung:\""+StringReplaceRe(ret_st_jaso.trim())+ "\")^1.0)|((eng_conv:\""+StringReplaceRe(ret_st_jaso.trim())+ "\")^1.0)";
		    				}else {
		    					sfield_str =  "((relword:\""+query_encode+"\")^1.0)|((relwordchosung:\""+StringReplaceRe(ret_st_jaso.trim())+ "\")^1.0)";
		    				}
		    					
		    			}
		    			
		    		}else {
		    			if(keyword_eng_conv.length() > 0) {
		    				sfield_str =  "((relword:\""+query_encode+"\")^1.0)|((relwordchosung:\""+StringReplaceRe(ret_st_jaso.trim())+ "\")^1.0)|((eng_conv:\""+StringReplaceRe(ret_st_jaso.trim())+ "\")^1.0)";
		    			}else {
		    				sfield_str =  "((relword:\""+query_encode+"\")^1.0)|((relwordchosung:\""+StringReplaceRe(ret_st_jaso.trim())+ "\")^1.0)";
		    			}
		    			//sfield_str =  "((relword:\""+query_encode+"\")^1.0)|((relwordjaso:\""+ret_st.trim().replaceAll("[ ]", "")+"\")^1)";
		    			
		    		}
		    		
		    	}else {
		    		if(keyword_eng_conv.length() > 0) {
		    			sfield_str =  "((relword:\""+query_encode+"\")^1.0)|((relwordchosung:\""+StringReplaceRe(ret_st_jaso.trim())+ "\")^1.0)|((eng_conv:\""+StringReplaceRe(ret_st_jaso.trim())+ "\")^1.0)";
		    		}else {
		    			sfield_str =  "((relword:\""+query_encode+"\")^100.0)|((relwordchosung:\""+StringReplaceRe(ret_st_jaso.trim())+ "\")^0.01)";
		    		}
		    		//sfield_str =  "((relword:\""+query_encode+"\")^1.0)|((relwordchosung:\""+ret_st_jaso.trim().replaceAll("[ ]", "")+"\")^1)";
		    		
		    	}
		    	
		    }
		    
			System.out.println("AUTO_SEARCH_QUERY == "+sfield_str);
			
		
			ret_str = sfield_str;
			
		
		//url = 	search_server_url+"q=(ART_TITLE:"+URLEncoder.encode(totalContentsRequest.getQuery(),"UTF-8")+")&pretty=true&from=0&size=10";
		
	
		return ret_str;
	}
	
	
	public JSONObject elSearch2(String y_url,String querystr,String calltype) throws IOException {
		JSONObject response = new JSONObject();
		
		
		try{
			//URL object=new URL("http://175.198.48.193:6300/news_alias/_search?pretty=true&from=0&size=10&sort=_score:desc,PRESSDATE:desc"); //테스트서버 용  port 기준 url 		
			
			URL object=new URL(y_url); //테스트서버 용  port 기준 url 
			
			HttpURLConnection httpcon = (HttpURLConnection) ((object.openConnection()));
			httpcon.setDoOutput(true);
			httpcon.setRequestProperty("Content-Type", "application/json");
			httpcon.setRequestProperty("Accept", "application/json");
			httpcon.setReadTimeout(5000);
			httpcon.setConnectTimeout(5000);
			httpcon.setRequestProperty("Keep-Alive", "timeout=3000, max=5000");
			
			httpcon.setRequestMethod("GET");
			httpcon.connect();
	    	
			
			JSONObject parent = new JSONObject();
			JSONObject term = new JSONObject();
			JSONObject query = new JSONObject();
			JSONObject fields = new JSONObject();
		
			JSONObject highlight = new JSONObject();
			JSONObject highlight_fields = new JSONObject();
			JSONObject highlight_fieldss = new JSONObject();
			
			SimpleDateFormat format1 = new SimpleDateFormat ( "yyyyMMddHHmmssS");				
			Date time = new Date();
			String time1 = format1.format(time);	
			
			querystr = querystr.replaceAll("%20", "+");
			term.put("query",querystr);
			
			if(calltype.equals("Y")) {
				highlight_fieldss.put("fragment_size",100);
				highlight_fieldss.put("number_of_fragments",1);
				highlight_fieldss.put("fragmenter","simple");
				highlight_fieldss.put("type","plain");
					
				
				highlight_fields.put("relword",highlight_fieldss);
				highlight_fields.put("relwordchosung",highlight_fieldss);
				highlight_fields.put("relwordjaso",highlight_fieldss);
				//	
				highlight.put("fields",highlight_fields);
				highlight.put("pre_tags","<b>");
				highlight.put("post_tags","</b>");
				parent.put("highlight",highlight);
			}
			
			query.put("query_string",term);
			parent.put("query",query);
			
			System.out.println(parent);
			
			
			byte[] outputBytes = parent.toJSONString().getBytes("UTF-8");
			//byte[] outputBytes = parent_s.getBytes("UTF-8");
			OutputStream os = httpcon.getOutputStream();
			os.write(outputBytes);
					
			//os.close(); 
		    
				StringBuilder sb = new StringBuilder();  
				int HttpResult = httpcon.getResponseCode(); 
				
				//System.out.println(httpcon.getResponseMessage()); 
				
				System.out.println("getResponseCode--->"+httpcon.getResponseCode()); 
				
				//System.out.println(httpcon.getHeaderFields());
				
				
				if (HttpResult == HttpURLConnection.HTTP_OK) {
				    BufferedReader br = new BufferedReader(
				            new InputStreamReader(httpcon.getInputStream(), "UTF-8"));
				    String line = null;  
				    while ((line = br.readLine()) != null) {  
				        sb.append(line + "\n");  
				    }
				    
				    br.close();
				    
				   //System.out.println(sb.toString()); 
				    
				    try{
			        	  Object obj = JSONValue.parse(sb.toString());			
			        	  response = (JSONObject) obj;	
						 // System.out.println(response);
			        	}catch(Exception e){
				        	
				        }
				    
				    
				} else {
				    System.out.println(httpcon.getResponseMessage()); 
				   
				} 
			
				parent = null;term = null;query = null;fields = null;
				highlight = null;highlight_fields = null;highlight_fieldss = null;
				
				httpcon.disconnect();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return response;
	}

	
	public static String gethanglejohap(String str){ 	 
		 
		  String str_imsi   = "";  	
		  List<Map<String, Integer>> list = new ArrayList<Map<String, Integer>>();
			String tempStr = str;
			String lastStr = "";
			
			tempStr =tempStr.replaceAll("<b>", "");
			tempStr =tempStr.replaceAll("</b>", "");
			
			//System.out.println(tempStr);
			
			String[] tempStr_exp = tempStr.split("[ ]");
			for(int a1 = 0 ; a1 < tempStr_exp.length;a1++)
			{
					
					for(int i = 0 ; i < tempStr_exp[a1].toString().length();i++)
					{
						Map<String, Integer> map = new HashMap<String, Integer>();
						char test = tempStr.charAt(i);
									
						if(test >= 0xAC00)
						{
							char uniVal = (char) (test - 0xAC00);
							
							char cho = (char) (((uniVal - (uniVal % 28))/28)/21);
							char jun = (char) (((uniVal - (uniVal % 28))/28)%21);
							char jon = (char) (uniVal %28);
							
		
							//System.out.println(""+test+"// 0x" + Integer.toHexString((char) test));
							
							//System.out.println(""+ ChoSung[cho]+"// 0x" + Integer.toHexString((char) cho) );
							//System.out.println(""+ JwungSung[jun]+"// 0x" + Integer.toHexString((char) jun) );
							if((char)jon != 0x0000)
							//System.out.println(""+ JongSung[jon]+"// 0x" + Integer.toHexString((char) jon) );
							
							
							map.put("cho", (int) cho);
							map.put("jun", (int) jun);
							map.put("jon", (int) jon);
							list.add(map);
						}
					}
					
					for(int i = 0; i < list.size() ; i++)
					{
						int a = (int)(list.get(i)).get("cho");
						int b = (int)(list.get(i)).get("jun");
						int c = (int)(list.get(i)).get("jon");
						
						char temp = (char)(0xAC00 + 28 * 21 *(a) + 28 * (b) + (c) );
						
						lastStr = lastStr.concat(String.valueOf(temp));			
						//System.out.println(""+ (char)(0xAC00 + 28 * 21 *(a) + 28 * (b) + (c) ));
						
					}
					
					//System.out.println(""+ lastStr);
			}
			return str_imsi; 

		 } 
	
	
	public static String getSTRFilterK4(String str){ 	 
		 
		  String str_imsi   = "";  	

			String []filter_word ={"\\(","\\)","\\[","\\]","\\~","\\^","\\&","\\*","\\+","\\=","\\|","\\\\","\\}","\\{","\\\"","\\'","\\:","\\;","\\.","\\?","\\/","\\■","\\★","\\♡","\\！","\\!","\\%","\\-"};

		  for(int i=0;i<filter_word.length;i++){ 
		   //while(str.indexOf(filter_word[i]) >= 0){ 
			  str_imsi = str.replaceAll(filter_word[i],""); 
			  str = str_imsi; 
		   //} 
		  } 
			//System.out.println("str :::::::-->"+str);
			return str; 

		 } 
	
	public static String getSTRFilterK5(String str){ 	 
		 
		  String str_imsi   = "";  	

			String []filter_word ={"\\[","\\]","\\\""};

		  for(int i=0;i<filter_word.length;i++){ 
		   //while(str.indexOf(filter_word[i]) >= 0){ 
			  str_imsi = str.replaceAll(filter_word[i],""); 
			  str = str_imsi; 
		   //} 
		  } 
			//System.out.println("str :::::::-->"+str);
			return str; 

		 } 
	
	private static String getSTRFilter(String str){ 	 
		 
		  String str_imsi   = "";  	
			String []filter_word ={"\\+"," "};

		  for(int i=0;i<filter_word.length;i++){ 
		   //while(str.indexOf(filter_word[i]) >= 0){ 
			  str_imsi = str.replaceAll(filter_word[i],"%20"); 
			  str = str_imsi; 
		   //} 
		  } 			
			return str; 
		 } 
	
	
public JSONObject elSearch(String y_url) throws IOException {
		JSONObject response = new JSONObject();
		
		String apiurl = y_url;
		System.out.println(apiurl);
		StringBuffer response2 = new StringBuffer();
		
		
		try{
			URL url = new URL(apiurl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setReadTimeout(5000);
			con.setConnectTimeout(5000);
			con.setRequestProperty("Content-Type", "application/json");
            
			/*
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
			
			String inputLine;
			
			while((inputLine = br.readLine()) != null) {
				response2.append(inputLine);
			}
			br.close();
			*/
			// System.out.println(response2);
			 
			InputStream test =  con.getInputStream();
        	InputStreamReader in = new InputStreamReader(test,"UTF-8");
        	try{
        	  Object obj = JSONValue.parse(in);			
        	  response = (JSONObject) obj;	
			 // System.out.println(response);
        	}catch(Exception e){
	        	
	        }
        	
        	
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return response;
	}

	public static String hangulToJaso(String s) { // 유니코드 한글 문자열을 입력 받음
	
	    int a, b, c; // 자소 버퍼: 초성/중성/종성 순
	    String result = "";
	
	    for (int i = 0; i < s.length(); i++) {
	        char ch = s.charAt(i);
	
	        if (ch >= 0xAC00 && ch <= 0xD7A3) { // "AC00:가" ~ "D7A3:힣" 에 속한 글자면 분해
	            c = ch - 0xAC00;
	            a = c / (21 * 28);
	            c = c % (21 * 28);
	            b = c / 28;
	            c = c % 28;
	
	            result = result + ChoSung[a] + JwungSung[b];
	
	            if (c != 0) {
	            	result = result + JongSung[c] ;
	            } // c가 0이 아니면, 즉 받침이 있으면
	            
	           // System.out.println("ch1-->"+ch+"//"+a+"//"+b+"//"+c);
	        } else {
	            result = result + ch;
	           // System.out.println("ch2-->"+ch);
	        }
	    }
	    return result;
	}
	
	public static String hangulToJasoChosung(String s) { // 유니코드 한글 문자열을 입력 받음
	
	    int a, b, c; // 자소 버퍼: 초성/중성/종성 순
	    String result = "";
	
	    for (int i = 0; i < s.length(); i++) {
	        char ch = s.charAt(i);
	
	        if (ch >= 0xAC00 && ch <= 0xD7A3) { // "AC00:가" ~ "D7A3:힣" 에 속한 글자면 분해
	            c = ch - 0xAC00;
	            a = c / (21 * 28);
	            c = c % (21 * 28);
	            b = c / 28;
	            c = c % 28;
	
	            result = result + ChoSung[a];
	
	           // if (c != 0) result = result + JongSung[c] ; // c가 0이 아니면, 즉 받침이 있으면
	        } else {
	            result = result + ch;
	        }
	    }
	    return result;
	}
	
		
	
	
	public static String StringReplaceRe(String str){       
	      String match = "[^\\uAC00-\\uD7A30-9a-zA-Z\\u3131-\\u3163\\\\s]";
	      str =str.replaceAll(match, "");
	      return str;
	   }
	
	public static String StringReplaceReNone(String str){       
	      String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
	      str =str.replaceAll(match, "");
	      return str;
	   }

	public static String StringReplaceReCho(String str){       
	      String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
	      str =str.replaceAll(match, "");
	      return str;
	   }
	
	
	public static String StringReplaceReNone2(String str){       
	      String match = "[^\uAC00-\uD7A30-9a-zA-Z\u3131-\u3163\\s]";
	      str =str.replaceAll(match, "");
	      return str;
	   }
	
	//\u3131\u3132\u3134\u3137\u3138\u3139\u3141\u3142\u3143\u3145\u3146,\u3147\u3148\u3149\u314a\u314b\u314c\u314d\u314e
}
