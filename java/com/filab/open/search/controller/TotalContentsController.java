
package com.filab.open.search.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.filab.open.search.command.TotalContentsRequest;
import com.filab.open.search.service.AnalDefaultService;
import com.filab.open.search.service.AnalDefaultServiceImpl;
import com.filab.open.search.service.SearchService;
import com.filab.open.search.util.KcubeUtils;
import com.filab.open.search.util.requestErrorMessage;
import com.filab.open.search.util.responseErrorMessage;

import kr.co.shineware.nlp.komoran.model.KomoranResult;

import com.filab.open.search.util.ConvertENgToKor;
import kr.co.shineware.nlp.komoran.model.Token;

import textrank.CounterMap;
import textrank.pagerank.Node;
import textrank.pagerank.UndirectWeightedGraph;
import textrank.tagger.JiebaTagger;
import textrank.tagger.Term;
import util.compareArraySort;

import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;

import scala.collection.Seq;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
public class TotalContentsController {

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final Logger qavotingogger = LoggerFactory.getLogger(TotalContentsController.class);
	
	private static String JSON_ENCODING = "UTF-8";
	
	// ㄱ      ㄲ      ㄴ      ㄷ      ㄸ      ㄹ      ㅁ      ㅂ      ㅃ      ㅅ      ㅆ      ㅇ      ㅈ      ㅉ      ㅊ      ㅋ      ㅌ      ㅍ      ㅎ
	public static char[] ChoSung   = { 0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145, 0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };
    // ㅏ      ㅐ      ㅑ      ㅒ      ㅓ      ㅔ      ㅕ      ㅖ      ㅗ      ㅘ      ㅙ      ㅚ      ㅛ      ㅜ      ㅝ      ㅞ      ㅟ      ㅠ      ㅡ      ㅢ      ㅣ
	public static char[] JwungSung = { 0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158, 0x3159, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162, 0x3163 };
    // ㄱ      ㄲ      ㄳ      ㄴ      ㄵ      ㄶ      ㄷ      ㄹ      ㄺ      ㄻ      ㄼ      ㄽ      ㄾ      ㄿ      ㅀ      ㅁ      ㅂ      ㅄ      ㅅ      ㅆ      ㅇ      ㅈ      ㅊ      ㅋ      ㅌ      ㅍ      ㅎ
	public static char[] JongSung  = { 0,      0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a, 0x313b, 0x313c, 0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145, 0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };

   private AnalDefaultService analDefaultService;
	
	
	public AnalDefaultService getAnalDefaultService() {
		return analDefaultService;
	}
	
	@Autowired
	public void setAnalDefaultService(AnalDefaultService analDefaultService) {
		this.analDefaultService = analDefaultService;
	}
	
	private SearchService searchService;

	@Autowired
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping("/totalSearch")	
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String searchTotal(@ModelAttribute TotalContentsRequest totalContentsRequest,@RequestHeader HttpHeaders headers,HttpServletRequest req)  {
		String szJsonData = "";
		
		Map colorNames = new HashMap();
		
		 colorNames.put(10, "0.95");
		 colorNames.put(9, "0.95");
		 colorNames.put(8, "0.95");
		 colorNames.put(7, "0.95");
		 colorNames.put(6, "0.9");
		 colorNames.put(5, "0.9");
		 colorNames.put(4, "0.35");
		 colorNames.put(3, "0.3");
		 colorNames.put(2, "0.15");
		 
		try {
				
			
			   Enumeration headerNames = req.getHeaderNames();
		        while(headerNames.hasMoreElements()) {
		            String name = (String)headerNames.nextElement();
		            String value = req.getHeader(name);
		            //System.out.println(name + " : " + value);
		        }		        
			
		    
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		JSONObject ret_str = new JSONObject();
		
		 String search_server_url = System.getProperty("searchServerUrl");
		 JSONObject sendJsonObject = new JSONObject();
		 
	//	System.out.println(search_server_url);
		 
		 //http://rec.busan.com/api/usr/000e032103d101f2333ca929922aab5b_87427
		 
		 //사용자 key가 들어올 경우는 해당 API 호출해서 키워드를 넘겨 받아야 한다.
		 //넘겨 받은 키워드는 본래 사용자 질의문 분석 결과와 하나식 결합을 해서 검색 결과를 OR연산 할 수 있도록 한다.
		 
		 String req_query = "";
		 try {
			 req_query = totalContentsRequest.getQuery();
			 req_query.replaceAll(" ", "");
			 
			 req_query = StringReplaceReNone(req_query.trim());
			 if(req_query.length() <= 0) {
				 totalContentsRequest.setQuery("");
			 }
			} catch (Exception e3) {
				// TODO Auto-generated catch block
				//e3.printStackTrace();
			}
		 
		 
		 requestErrorMessage reqinValidate = new requestErrorMessage();
			boolean reqInValidates = reqinValidate.isInvalidTotal(totalContentsRequest,sendJsonObject);
			
			
			try {
				System.out.println("totalContentsRequest--->"+totalContentsRequest.toRequestQueryString());
			} catch (UnsupportedEncodingException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			
			
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
			   String keyword_eng_conv = "";
			   
			   try {
					
			       	try {
			       		//keyword_eng_conv = ConvertENgToKor.engToKor(totalContentsRequest.getQuery());
			       		
			       	 String[] in_str = totalContentsRequest.getQuery().split("[ ]");
					 String engtokor_str = "";
					 boolean eng_include_flag = false;
					 for(int i=0;i<in_str.length;i++) {
						 if(in_str[i] != null) {
							 
							 String word[] = in_str[i].toString().split(""); 
							 
							 String hangul_str = "";
							 String eng_str = "";
							 String merge_str = "";
							 boolean han_eng_flag = true;
							 for(int j=0; j<word.length; j++){
								 
								
									//System.out.println(word[i]+" : "+getWordType(word[i]));
									
									if(j==0) {
										if(ConvertENgToKor.getWordType(word[j]).equals("K")) {
											han_eng_flag = true;
										}
										if(ConvertENgToKor.getWordType(word[j]).equals("E")) {
											han_eng_flag = false;
										}
									}
									if(ConvertENgToKor.getWordType(word[j]).equals("K")) {
										hangul_str = hangul_str + word[j];
									}
									if(ConvertENgToKor.getWordType(word[j]).equals("E")) {
										eng_str = eng_str + word[j];
										
										eng_include_flag = true;
									}
									
							  }
							 
							 
							 
							 String in_word = "";
							 //영문만 입력 처리 한다.
							 String re_str = "";
							 if(eng_str != null && eng_str.trim().length() > 0) {
								 
							 
								  re_str = StringReplaceRe(ConvertENgToKor.engToKor(eng_str.toString()));
								 if(re_str != null && re_str.trim().length() > 0) {
									 re_str = re_str;
								 }else {
									 re_str = in_str[i].toString();
								 }
								 
								 
							 }
							 
							 if(han_eng_flag) { //처음 시작이 한글일 경우
								 merge_str = hangul_str+""+re_str;
							 }else {
								 merge_str = re_str+""+hangul_str;
							 }
						 	
						 
							 engtokor_str = engtokor_str + merge_str + " ";
							 
							 
						 }
						 
						 keyword_eng_conv = engtokor_str;
					 }
					 
					 if(!eng_include_flag) {
						 keyword_eng_conv = "";
					 }
					 
					 System.out.println(engtokor_str); 
					 
			       	}catch(Exception e) {
			       		
			       	}
			       	
				}catch(Exception e) {
					
				}
			   String in_query_str = "";
			   
			    if(keyword_eng_conv.trim().length() > 0){
			    	in_query_str = keyword_eng_conv;
			    }else {
			    	in_query_str = totalContentsRequest.getQuery();
			    }
			    
			     String input = StringReplace(in_query_str);
				KomoranResult analyzeResultList = AnalDefaultServiceImpl.komoran.analyze(input);
				List<Token> tokenList = analyzeResultList.getTokenList();
				HashMap  ngram_hash =  new HashMap();
				
				String  rs_str = "";
				int mo_s = 0;
				for (Token token : tokenList) {
				
				//	System.out.println("token.getMorph():"+token.getMorph()+"//"+token.getPos());
					if(token.getPos().toString().equalsIgnoreCase("NNG") 
							|| token.getPos().toString().equalsIgnoreCase("NNP")
							|| token.getPos().toString().equalsIgnoreCase("NNB")
							|| token.getPos().toString().equalsIgnoreCase("SL")
							|| token.getPos().toString().equalsIgnoreCase("SN")
							|| token.getPos().toString().equalsIgnoreCase("SH")
							|| token.getPos().toString().equalsIgnoreCase("XR")){
						
						String sp_str = " ";
						if(mo_s ==0) {
							sp_str = "";
						}
						rs_str = rs_str + sp_str+token.getMorph();
						
						mo_s++;
					}
					
				}
				
				String text = "";
				try {
					String[] rs_str_sp = rs_str.split("[ ]");
					// System.out.println("rs_str_sp.length--->"+rs_str_sp.length);
					if(rs_str_sp.length < 3) {
						CharSequence normalized = OpenKoreanTextProcessorJava.normalize(input);
						Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
						List<KoreanTokenJava> tokens_list = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);
						
						int mo_s2 = 0;		        
					    for(int i=0;i<tokens_list.size();i++) {
					    	//Foreign,Number
					    	if((tokens_list.get(i).getPos().toString().equalsIgnoreCase("Noun") || 
					    			tokens_list.get(i).getPos().toString().equalsIgnoreCase("Foreign")) && tokens_list.get(i).getText().toString().length() > 1){
					    		
					    		String sp_str = " ";
								if(mo_s2 ==0) {
									sp_str = "";
								}
								
					    	//	System.out.println(tokens_list.get(i).getText() + "//"+tokens_list.get(i).getPos());
					    		text = text + sp_str+ tokens_list.get(i).getText();
					    		
					    		mo_s2++;
					    	}
					    }
					}
				}catch(Exception e) {
					
				}
			    String in_strs = rs_str+" "+text;
			   // System.out.println("in_strs--->"+rs_str +"//"+text);
			    
			    
			    LinkedHashMap<String,String> query_map = new LinkedHashMap<String,String>();
				
				String[] q_sp_s = in_strs.split("[ ]");
				
				//System.out.println("q_sp_s--->"+q_sp_s.length);
				
				if(q_sp_s.length > 3) {
					for(int i=0;i<q_sp_s.length;i++) {
						if(q_sp_s[i] != null && q_sp_s[i].toString().trim().length() > 1 ) {
							query_map.put(q_sp_s[i].toString(), q_sp_s[i].toString());
						}
					}
				}else {
					for(int i=0;i<q_sp_s.length;i++) {
						if(q_sp_s[i] != null && q_sp_s[i].toString().trim().length() > 0 ) {
							
							//System.out.println("q_sp_s--->"+q_sp_s[i].toString());
							
							
							query_map.put(q_sp_s[i].toString(), q_sp_s[i].toString());
						}
					}
				}
				
				int c_i = 0;
				String dup_str = "";
				Set<String> keySet_u1 = query_map.keySet();
				  Iterator<String> iterator_u1 = keySet_u1.iterator();
				  while (iterator_u1.hasNext()) {
					  String comma_str = " ";
					  if(c_i == 0) {
						  comma_str = "";
					  }
					   String key = iterator_u1.next();
					   Object value = query_map.get(key);			   
					  
					   dup_str = dup_str + comma_str+key;
					   c_i++;
					   
				  }
				  
				  System.out.println("dup_str-->"+dup_str);
				  query_map.clear();
				  
			
				
				try {
				
					String url = "";
					
					String sort_str = "";
					
					//System.out.println(" totalContentsRequest.getSort()--->"+totalContentsRequest.getSort());
					
					if(totalContentsRequest.getSort() != null && totalContentsRequest.getSort().trim().length()>0) {
						//sort_str = "&sort=PRESSDATE:desc,hits.hits._score:desc";
						
						sort_str = "&sort=_score:desc,DOCID:desc";
						
						//PUBLISH_DATE/DESC
							if(totalContentsRequest.getSort().contains("PUBLISH_DATE") ) {
								//sort_str = "&sort=PRESSDATE:desc,ART_UPD_TIME:desc,_score:desc";
								sort_str = "&sort=DOCID:desc,ART_UPD_TIME:desc,_score:desc";
							}else {
								if(totalContentsRequest.getSort().contains("RANK") && totalContentsRequest.getSort().contains("DATE")) {
									
									sort_str = "&sort=DOCID:desc,_score:desc";
									
								}else {
									
									if(totalContentsRequest.getSort().contains("RANK")) {
										sort_str = "&sort=_score:desc,DOCID:desc";
										//sort_str = "";
									}
									
									if(totalContentsRequest.getSort().contains("DATE") && totalContentsRequest.getSort().contains("DESC")) {
										//sort_str = "&sort=PRESSDATE:desc,ART_UPD_TIME:desc,_score:desc";
										sort_str = "&sort=DOCID:desc,_score:desc";
									}
									
		
									if(totalContentsRequest.getSort().contains("DATE") && totalContentsRequest.getSort().contains("ASC")) {
									//	sort_str = "&sort=_score:desc,PRESSDATE:asc";
										sort_str = "&sort=DOCID:asc,_score:desc";
									}
									
								}
							}
							
						
					}else {
						sort_str = "&sort=_score:desc,DOCID:desc,ART_UPD_TIME:desc";
					}
					//Sort.RELEVANCE
					
					String start_cnt = "0";
					String size_cnt = "10";
					if(totalContentsRequest.getStartCount() != null && totalContentsRequest.getStartCount().trim().length() > 0) {
						start_cnt = totalContentsRequest.getStartCount();
					}
					
					if(totalContentsRequest.getListCount() != null && totalContentsRequest.getListCount().trim().length() > 0) {
						size_cnt = totalContentsRequest.getListCount();
					}
					
					url = 	search_server_url+"pretty=true&from="+start_cnt+"&size="+size_cnt+sort_str;	
					
					
					String query_str = "";
					String total_search_num = "0";
					JSONObject hits = new JSONObject();
					/*
					 1 : 사용자 key가 있고 키워드 응답값이 있을경우와 분리해서 치리해야 한다.
					 
					 */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					
					String[] dup_str_exp = totalContentsRequest.getQuery().trim().split("[ ]");
					
					String user_uuid = "";
					String keyword_list = "";
					try {
						user_uuid = totalContentsRequest.getUserkey();
						
					}catch(Exception e) {
						
					}
						  
					try {
						String keywordApiServerUrl = System.getProperty("keywordApiServerUrl");
						if(user_uuid != null && user_uuid.trim().length() > 0) {
							keyword_list = searchAiAPi(keywordApiServerUrl,user_uuid);
						}			  
					}catch(Exception e) {
								
					}
								  
					
					
					if(user_uuid != null && user_uuid.trim().length() > 0) {
						query_str =queryMapFirst(totalContentsRequest,"Y",keyword_eng_conv,StringReplace(totalContentsRequest.getQuery()),user_uuid,keyword_list);
						
						ret_str = elSearch2(url,query_str,totalContentsRequest);					
						
			        	 hits = (JSONObject) ret_str.get("hits");
			        	
			        	total_search_num = hits.get("total").toString();
			        	System.out.println("user_uuid queryMapFirst total num===>"+total_search_num);	
						
			        	int total_search_s = Integer.valueOf(total_search_num).intValue();			        		
		        		
		        		if(total_search_s == 0) {
		        			query_str = queryMap20211105(totalContentsRequest,"Y",keyword_eng_conv,dup_str,user_uuid,keyword_list);	
		        			
		        			 ret_str = elSearch2(url,query_str,totalContentsRequest);					
							
				        	 hits = (JSONObject) ret_str.get("hits");
				        	
				        	total_search_num = hits.get("total").toString();
				        	System.out.println("user_uuid queryMapSecond total num===>"+total_search_num);	
				        	
		        		}
		        		
					}else {
					//if(dup_str_exp.length < 2) {
						query_str =queryMapFirst(totalContentsRequest,"Y",keyword_eng_conv,StringReplace(totalContentsRequest.getQuery()),"","");
						
						ret_str = elSearch2(url,query_str,totalContentsRequest);					
						
			        	 hits = (JSONObject) ret_str.get("hits");
			        	
			        	total_search_num = hits.get("total").toString();
			        	System.out.println("queryMapFirst total num===>"+total_search_num);	
					
					}					

						        	
		        	
		        	try {
		        		int total_search_s = Integer.valueOf(total_search_num).intValue();			        		
		        		
		        		if(total_search_s == 0) {
		        			
							query_str = queryMap20211105(totalContentsRequest,"Y",keyword_eng_conv,dup_str,"","");		
							
							ret_str = elSearch2(url,query_str,totalContentsRequest);								
							total_search_num = "0";
				        	hits = (JSONObject) ret_str.get("hits");					        	
				        	total_search_num = hits.get("total").toString();
				        	//System.out.println("Re total_search_num--->"+total_search_num);
				        	int total_search_ss = Integer.valueOf(total_search_num).intValue();
				        	if(total_search_ss > 0) {
				        		System.out.println("TOTAL_SEARCH_RESULT|"+totalContentsRequest.getQuery()+"|"+total_search_num);
				        	}else {
				        		System.out.println("queryMapSecond total num===>"+total_search_num);
				        	}
				        	
		        		}else {
		        			dup_str = StringReplace(totalContentsRequest.getQuery());
		        			System.out.println("TOTAL_SEARCH_RESULT|"+totalContentsRequest.getQuery()+"|"+total_search_num);
		        		}
		        	}catch(Exception e) {
		        		
		        	}
		        	
		        	try {
		        		int total_search_s = Integer.valueOf(total_search_num).intValue();
		        		
		        		
		        		//검색 결과가 없을 경우는 rs_str 즉 1차 형태소 분석된 결과만 가지고 AND 검색을 처리한다.
		        		//그래도 없을 경우는 1차 분석된 정보중에서 2글자 이상만 모아서 or 연산을 처리한다. 			        		
		        		
		        		if(total_search_s == 0) {
		        			query_str = queryMapOR(totalContentsRequest,"N",keyword_eng_conv,dup_str);
		        			
		        			//url = 	search_server_url+"pretty=true&from="+start_cnt+"&size="+size_cnt+sort_str;			        			
							
							ret_str = elSearch2(url,query_str,totalContentsRequest);								
							total_search_num = "0";
				        	hits = (JSONObject) ret_str.get("hits");					        	
				        	total_search_num = hits.get("total").toString();
				        	//System.out.println("Re total_search_num--->"+total_search_num);
				        	System.out.println("RE_RESULT|"+totalContentsRequest.getQuery()+"&& RE_NUM|"+total_search_num);
				        	
		        		}
		        	}catch(Exception e) {
		        		
		        	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////			        	
			     
					//키워드 분석 결과가 3개 이상일 경우는 or 검색 전에 한번더 and 연산을 수행 한다.
					try {					
						
			        	
			        	
			        	//System.out.println("last dup_str--->"+dup_str); 	
			        	JSONArray varsArray = (JSONArray) hits.get("hits");
			        	
			        	JSONParser SQ_search_parser = new JSONParser();
			        	
			        	List<Object> cs_listData = new ArrayList<Object>();
			        	
			        	sendJsonObject.put("total", total_search_num);
			        	
			        	sendJsonObject.put("result_code", "200");
			        	sendJsonObject.put("result_message", "success");
			        	
			        	for(int i=0;i<varsArray.size();i++) {
			        		
			        		//JSONObject resultData = (JSONObject) varsArray.get(i);
			        		
			        		JSONObject Sjson_arr2 = new JSONObject();
			        		
			        		Sjson_arr2 = (JSONObject) SQ_search_parser.parse(varsArray.get(i).toString());
			        		
			        		
			        		JSONObject json_arr3 = new JSONObject();
							JSONParser SQ_search_parser2 = new JSONParser();
							json_arr3 = (JSONObject) SQ_search_parser2.parse(Sjson_arr2.get("_source").toString());
							//System.out.println(json_arr3.toJSONString());
							
							JSONObject json_arr_highlight = new JSONObject();
							JSONParser highlight_parser = new JSONParser();
							try{ json_arr_highlight = (JSONObject) highlight_parser.parse(Sjson_arr2.get("highlight").toString()); }catch(Exception e) {}
							
							
							//System.out.println(Sjson_arr2.get("highlight").toString());
							
			        		String    DOCID = "";String    DOCTYPE = "";String    TOTAL_ID = "";String    ARTICLE_ID = "";
			        		String    SERVICE_TIME = "";String    CONTENT_TYPE = "";String    PRESSDATE = "";String    PRESSCATEGORY = "";
			        		String    PRESSMYUN = "";String    ART_CRE_TIME = "";String    ART_UPD_TIME = "";String    SECTION_NAME = "";
			        		String    ART_THUMB = "";String    ART_TITLE = "";String    ART_SUBTITLE = "";String    ART_KWD = "";
			        		String    ART_CONTENT = "";String    REL_WORD_LIST = "";
			        		
			        		String    ART_TITLE_highlight = "";
			        		String    ART_CONTENT_highlight = "";
			        		String    ART_TITLE_highlight_ser = "";
			        		String    ART_CONTENT_highlight_ser = "";
			        		String    BY_LINE_LIST = "";
			        		String    SECTCHK = "";
			        		
			        		 try{ DOCID = json_arr3.get("DOCID").toString(); }catch(Exception e) {DOCID = "";}
			        		 try{ DOCTYPE = json_arr3.get("DOCTYPE").toString(); }catch(Exception e) {DOCTYPE = "";}
			        		 try{ TOTAL_ID = json_arr3.get("TOTAL_ID").toString(); }catch(Exception e) {TOTAL_ID = "";}
			        		 try{ ARTICLE_ID = json_arr3.get("ARTICLE_ID").toString(); }catch(Exception e) {ARTICLE_ID = "";}
			        		 try{ SERVICE_TIME = json_arr3.get("SERVICE_TIME").toString(); }catch(Exception e) {SERVICE_TIME = "";}
			        		 try{ CONTENT_TYPE = json_arr3.get("CONTENT_TYPE").toString(); }catch(Exception e) {CONTENT_TYPE = "";}
			        		 try{ PRESSDATE = json_arr3.get("PRESSDATE").toString(); }catch(Exception e) {PRESSDATE = "";}
			        		 try{ PRESSCATEGORY = json_arr3.get("PRESSCATEGORY").toString(); }catch(Exception e) {PRESSCATEGORY = "";}
			        		 try{ PRESSMYUN = json_arr3.get("PRESSMYUN").toString(); }catch(Exception e) {PRESSMYUN = "";}
			        		 try{ ART_CRE_TIME = json_arr3.get("ART_CRE_TIME").toString(); }catch(Exception e) {ART_CRE_TIME = "";}
			        		 try{ ART_SUBTITLE = json_arr3.get("ART_SUBTITLE").toString(); }catch(Exception e) {ART_SUBTITLE = "";}
			        		 try{ SECTION_NAME = json_arr3.get("SECTION_NAME").toString(); }catch(Exception e) {SECTION_NAME = "";}
			        		 try{ ART_THUMB = json_arr3.get("ART_THUMB").toString(); }catch(Exception e) {ART_THUMB = "";}
			        		 try{ ART_TITLE = json_arr3.get("ART_TITLE").toString(); }catch(Exception e) {ART_TITLE = "";}
			        		 try{ ART_SUBTITLE = json_arr3.get("ART_SUBTITLE").toString(); }catch(Exception e) {ART_SUBTITLE = "";}
			        		 try{ ART_KWD = json_arr3.get("ART_KWD").toString(); }catch(Exception e) {ART_KWD = "";}
			        		 try{ ART_CONTENT = json_arr3.get("ART_CONTENT").toString(); }catch(Exception e) {ART_CONTENT = "";}
			        		 try{ REL_WORD_LIST = json_arr3.get("REL_WORD_LIST").toString(); }catch(Exception e) {REL_WORD_LIST = "";}
			        		 
			        		 try{ BY_LINE_LIST = json_arr3.get("BY_LINE_LIST").toString(); }catch(Exception e) {BY_LINE_LIST = "";}
			        		 
			        		 try{ SECTCHK = json_arr3.get("SECTCHK").toString(); }catch(Exception e) {SECTCHK = "";}
			        		 
			        		 try{ 
			        			 
			        			// ART_TITLE_highlight = json_arr_highlight.get("ART_TITLE").toString();
			        			 
			        			 JSONArray c_Array = (JSONArray) json_arr_highlight.get("ART_TITLE");
			        		 
			        			 ART_TITLE_highlight = "";			        			 
			        			 for(int j=0;j<c_Array.size();j++) {			        				 
			        				// System.out.println(c_Array.get(j).toString());
			        				 ART_TITLE_highlight = ART_TITLE_highlight + c_Array.get(j).toString();			        				 
			        			 }
			        			 
			        		 }catch(Exception e) {ART_TITLE_highlight = "";}
			        		 try{
			        			 
			        			 JSONArray c_Array = (JSONArray) json_arr_highlight.get("ART_CONTENT");
			        			 
			        			// System.out.println("c_Array.size()--->"+c_Array.size());
			        			 ART_CONTENT_highlight = "";			        			 
			        			 for(int j=0;j<c_Array.size();j++) {			        				 
			        				// System.out.println(c_Array.get(j).toString());
			        				 ART_CONTENT_highlight = ART_CONTENT_highlight + c_Array.get(j).toString()+"...";
			        				 
			        			 }
			        			 
			        			// ART_CONTENT_highlight = json_arr_highlight.get("ART_CONTENT").toString(); 
			        		 
			        		 
			        		 }catch(Exception e) {ART_CONTENT_highlight = "";}
			        		 
			        		 
			        		 try{ 
			        			 
			        			// ART_TITLE_highlight = json_arr_highlight.get("ART_TITLE_SER").toString();
			        			 
			        			 JSONArray c_Array = (JSONArray) json_arr_highlight.get("ART_TITLE_SER");
			        		 
			        			 ART_TITLE_highlight_ser = "";			        			 
			        			 for(int j=0;j<c_Array.size();j++) {			        				 
			        				// System.out.println(c_Array.get(j).toString());
			        				 ART_TITLE_highlight_ser = ART_TITLE_highlight_ser + c_Array.get(j).toString();			        				 
			        			 }
			        			 
			        			// ART_TITLE_highlight_ser = json_arr_highlight.get("ART_TITLE_SER").toString();
			        			 
			        		 }catch(Exception e) {ART_TITLE_highlight_ser = "";}
			        		 
			        		 try{ 
			        			 
			        			 JSONArray c_Array = (JSONArray) json_arr_highlight.get("ART_CONTENT_SER");
			        			 for(int j=0;j<c_Array.size();j++) {
			        				 ART_CONTENT_highlight_ser = ART_CONTENT_highlight_ser + c_Array.get(j).toString()+"...";
			        			 }
			        			// ART_CONTENT_highlight_ser = json_arr_highlight.get("ART_CONTENT_SER").toString(); 
			        		 
			        		 
			        		 }catch(Exception e) {ART_CONTENT_highlight_ser = "";}
			        	
			        		 
			        		 String in_title = "";
			        		 String in_content = "";
			        		 if(ART_TITLE_highlight_ser.trim().length() > 0) {
			        			 
			        			 in_title=ART_TITLE_highlight_ser;
			        		 
			        		 }else if(ART_TITLE_highlight.trim().length() > 0) {
			        			 	in_title=ART_TITLE_highlight;
							 }else {
									 in_title=ART_TITLE;
							 }
			        		 
			        		 
			        		 if(ART_CONTENT_highlight_ser.trim().length() > 0) {
			        			 in_content=ART_CONTENT_highlight_ser;
			        		 }else if(ART_CONTENT_highlight.trim().length() > 0) {
			        			 in_content=ART_CONTENT_highlight;
							 }else {
									in_content=ART_CONTENT;
							 } 
			        		 
			        		
			        		 
			        		 
			        		 
			        		LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
					        	m.put("DOCID", DOCID);
								m.put("DOCTYPE", DOCTYPE);
								m.put("TOTAL_ID", TOTAL_ID);
								m.put("ARTICLE_ID", ARTICLE_ID);
								m.put("SERVICE_TIME", SERVICE_TIME);
								m.put("CONTENT_TYPE", CONTENT_TYPE);
								m.put("PRESSDATE", PRESSDATE);
								m.put("PRESSCATEGORY", PRESSCATEGORY);
								m.put("PRESSMYUN", PRESSMYUN);
								m.put("ART_CRE_TIME", ART_CRE_TIME);
								m.put("ART_UPD_TIME", ART_UPD_TIME);
								m.put("SECTION_NAME", SECTION_NAME);
								m.put("ART_THUMB", ART_THUMB);
								m.put("SECTCHK", SECTCHK);
								
								try {
									in_title = in_title.replaceAll("<\\\\/b>","</b>");
								}catch(Exception ee) {
									
								}
								
								try {
									
									
									in_title = in_title.replaceAll("\\\\u2018", "'");
									in_title = in_title.replaceAll("\\\\u2019", "'");
									in_title = in_title.replaceAll("\\\\u201D", "'");
									in_title = in_title.replaceAll("\\\\u2026", "'");
									in_title = in_title.replaceAll("\\\\u201C", "'");
									
									in_title = in_title.replaceAll("\\\\", "");
									//in_title = in_title.replaceAll("\\[", "");
									//in_title = in_title.replaceAll("\\]", "");
									in_title = in_title.replaceAll("\\\"", "");
									
									
									
										if(!totalContentsRequest.getSfield().equals("TITLE")) {
											//System.out.println("TITLE -- dup_str-->"+dup_str);
											String[] q_sp_ss = dup_str.split("[ ]");
											String rep_str_tmp = "";
											//System.out.println("TITLE -- q_sp_s.length-->"+q_sp_ss.length);
											int chk_cntss = 0;
											if(q_sp_ss.length > 3) {
												rep_str_tmp = "";
												for(int j=0;j<q_sp_ss.length;j++) {
													if(q_sp_ss[j] != null && q_sp_ss[j].toString().trim().length() > 1 ) {
														String pipe_str = "|";
														if(chk_cntss == 0) {
															pipe_str = "";
														}
														rep_str_tmp = rep_str_tmp + pipe_str+"("+q_sp_ss[j].toString()+")";
																
														chk_cntss++;
													}
												}
											}else {
												rep_str_tmp = "";
												for(int j=0;j<q_sp_ss.length;j++) {
													if(q_sp_ss[j] != null && q_sp_ss[j].toString().trim().length() > 1 ) {
														String pipe_str = "|";
														if(chk_cntss == 0) {
															pipe_str = "";
														}
														rep_str_tmp = rep_str_tmp + pipe_str+"("+q_sp_ss[j].toString()+")";
																
														chk_cntss++;
													}
												}
											}
											
											//System.out.println("TITLE rep_str_tmp-->"+rep_str_tmp);
											
											in_title = in_title.replaceAll(rep_str_tmp, "<b>$0</b>");
										}
								}catch(Exception ee) {
									//ee.printStackTrace();
								}
								
								
								m.put("ART_TITLE", in_title);
							
								
								try {
									
									
									ART_SUBTITLE = ART_SUBTITLE.replaceAll("\\\\u2018", "'");
									ART_SUBTITLE = ART_SUBTITLE.replaceAll("\\\\u2019", "'");
									ART_SUBTITLE = ART_SUBTITLE.replaceAll("\\\\u201D", "'");
									ART_SUBTITLE = ART_SUBTITLE.replaceAll("\\\\u2026", "'");
									ART_SUBTITLE = ART_SUBTITLE.replaceAll("\\\\u201C", "'");
									
									ART_SUBTITLE = ART_SUBTITLE.replaceAll("\\\\", "");
									//ART_SUBTITLE = ART_SUBTITLE.replaceAll("\\[", "");
									//ART_SUBTITLE = ART_SUBTITLE.replaceAll("\\]", "");
									ART_SUBTITLE = ART_SUBTITLE.replaceAll("\\\"", "");
									
								}catch(Exception ee) {
									
								}

								m.put("ART_SUBTITLE", ART_SUBTITLE);
								m.put("ART_KWD", ART_KWD);
								
								//<\/b>
								try {
									in_content = in_content.replaceAll("<\\\\/b>","</b>");
								}catch(Exception ee) {
									
								}
								
								try {
									
									
									in_content = in_content.replaceAll("\\\\u2018", "'");
									in_content = in_content.replaceAll("\\\\u2019", "'");
									in_content = in_content.replaceAll("\\\\u201D", "'");
									in_content = in_content.replaceAll("\\\\u2026", "'");
									in_content = in_content.replaceAll("\\\\u201C", "'");
									
									
									in_content = in_content.replaceAll("\\\\", "");
									//in_content = in_content.replaceAll("\\[", "");
									//in_content = in_content.replaceAll("\\]", "");
									//in_content = in_content.replaceAll("\\\"", "");
								//	in_content = in_content.replaceAll("]", "");
									//System.out.println(in_content);
									
									//in_content = in_content.replaceAll("\\\"", "");
									
																	
									if(!totalContentsRequest.getSfield().equals("TITLE")) {
										//System.out.println("dup_str-->"+dup_str);
										String[] q_sp_ss = dup_str.split("[ ]");
										String rep_str_tmp = "";
										//System.out.println("q_sp_s.length-->"+q_sp_ss.length);
										int chk_cntss = 0;
										
										String highlight_summary_str = "";
										
										if(q_sp_ss.length > 3) {
											rep_str_tmp = "";
											for(int j=0;j<q_sp_ss.length;j++) {
												if(q_sp_ss[j] != null && q_sp_ss[j].toString().trim().length() > 1 ) {
													String pipe_str = "|";
													if(chk_cntss == 0) {
														pipe_str = "";
													}
													rep_str_tmp = rep_str_tmp + pipe_str+"("+q_sp_ss[j].toString()+")";
															
													chk_cntss++;
													
													if(in_content.indexOf(q_sp_ss[j].toString()) > -1) {
														//System.out.println("in_content.indexOf(q_sp_ss[j].toString()-->"+in_content.indexOf(q_sp_ss[j].toString()));
														try {
															
															String r_a = String.valueOf(in_content.indexOf(q_sp_ss[j].toString()));
															r_a = r_a.replaceAll("-", "");
															int r_b = Integer.valueOf(r_a);
														
															String f_str = in_content.substring(0,in_content.indexOf(q_sp_ss[j].toString()));
															
															int f_str_n = r_b;
															int f_str_2 = in_content.toString().length();
															
															int sec_n = 0;
															
															String f_term_str = in_content.substring(0,r_b);
															sec_n = f_term_str.length();
															
															
															if(f_str_n > 100) { //처음 출연한 위치가 50 보다 클경우
														
																highlight_summary_str = highlight_summary_str + "..."+in_content.substring(sec_n-70, sec_n+70)+"...";
															}else {
																
																int mi_s = 0;
																try {
																	mi_s = f_str_2 - f_str_n;
																}catch(Exception e) {
																	
																}
																
																int last_n = 0;
																if(mi_s > 100) {
																	last_n = 100;
																}else {
																	last_n = mi_s;
																}
																//System.out.println("7 mi_s-->"+mi_s);
																
																highlight_summary_str = highlight_summary_str + in_content.substring(0, in_content.indexOf(q_sp_ss[j].toString())+last_n);
															}
															
														}catch(Exception e) {
															System.out.println(e.toString());
														}
													}
												}
											}
										}else {
											rep_str_tmp = "";
											for(int j=0;j<q_sp_ss.length;j++) {
												if(q_sp_ss[j] != null && q_sp_ss[j].toString().trim().length() > 1 ) {
													String pipe_str = "|";
													if(chk_cntss == 0) {
														pipe_str = "";
													}
													rep_str_tmp = rep_str_tmp + pipe_str+"("+q_sp_ss[j].toString()+")";
															
													chk_cntss++;
													if(in_content.indexOf(q_sp_ss[j].toString()) > 0) {
														//System.out.println("in_content.indexOf(q_sp_ss[j].toString()-->"+in_content.indexOf(q_sp_ss[j].toString()));
														try {
															
															String r_a = String.valueOf(in_content.indexOf(q_sp_ss[j].toString()));
															r_a = r_a.replaceAll("-", "");
															int r_b = Integer.valueOf(r_a);
														
															String f_str = in_content.substring(0,in_content.indexOf(q_sp_ss[j].toString()));
															
															int f_str_n = r_b;
															int f_str_2 = in_content.toString().length();
															
															int sec_n = 0;
															
															String f_term_str = in_content.substring(0,r_b);
															sec_n = f_term_str.length();
															
															
															if(f_str_n > 150) { //처음 출연한 위치가 50 보다 클경우
														
																highlight_summary_str = highlight_summary_str + "..."+in_content.substring(sec_n-70, sec_n+70)+"...";
															}else {
																
																int mi_s = 0;
																try {
																	mi_s = f_str_2 - f_str_n;
																}catch(Exception e) {
																	
																}
																
																int last_n = 0;
																if(mi_s > 150) {
																	last_n = 150;
																}else {
																	last_n = mi_s;
																}
																//System.out.println("7 mi_s-->"+mi_s);
																
																highlight_summary_str = highlight_summary_str + in_content.substring(0, in_content.indexOf(q_sp_ss[j].toString())+last_n);
															}
															
														}catch(Exception e) {
															System.out.println(e.toString());
														}
														
													}
												}
											}
										}
										
										//System.out.println("highlight_summary_str-->"+highlight_summary_str);
										if(highlight_summary_str.trim().length() > 0) {
											in_content = highlight_summary_str.replaceAll(rep_str_tmp, "<b>$0</b>");
										}else {
											in_content = in_content.replaceAll(rep_str_tmp, "<b>$0</b>");
										}
										
									}
									
									
								}catch(Exception ee) {
									System.out.println(ee);
								}

								
								m.put("ART_CONTENT", in_content);								
								m.put("REL_WORD_LIST", REL_WORD_LIST);													
								m.put("WRITER", BY_LINE_LIST);
								String lank_str = "";	
								try {
									lank_str = Sjson_arr2.get("_score").toString();		
								}catch(Exception e) {}									
								m.put("DOC_SCORE", lank_str);								
								cs_listData.add(m);							
								m = null;								
								sendJsonObject.put("CONTENTS_LIST", cs_listData);
							//System.out.println("cs_listData--->"+cs_listData);
			        		
			        	}
			        	
			        	cs_listData=null;
			        	szJsonData = sendJsonObject.toString();
			        	try {
							
									szJsonData = KcubeUtils.charSetConvertEuckr(szJsonData);
								
						} catch (Exception e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					}catch(Exception e) {
						//e.printStackTrace();
						List<Object> cs_listData = new ArrayList<Object>();
						
						 sendJsonObject.put("total", 0);
						 sendJsonObject.put("errorCode", sendJsonObject.get("RESULT_CODE").toString());
						 sendJsonObject.put("errorMessage", sendJsonObject.get("RESULT_MSG").toString());
						 sendJsonObject.put("CONTENTS_LIST", cs_listData);
						 
						 cs_listData = null;
						 
						  szJsonData = sendJsonObject.toString();
				        	try {
								
										szJsonData = KcubeUtils.charSetConvertEuckr(szJsonData);
									
							} catch (Exception e2) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
							}
				        	
					}
					
		        			        	
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
					
					List<Object> cs_listData = new ArrayList<Object>();
					
					 sendJsonObject.put("total", 0);
					 sendJsonObject.put("errorCode", "500");
					 sendJsonObject.put("errorMessage", "Internal Server Error! Search Server Error!");
					 sendJsonObject.put("CONTENTS_LIST", cs_listData);
					 
					 cs_listData = null;
					 
					  szJsonData = sendJsonObject.toString();
			        	try {
							
									szJsonData = KcubeUtils.charSetConvertEuckr(szJsonData);
								
						} catch (Exception e2) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
			        	
				}
		}
		return szJsonData;
	}
	
	
	public static String queryMapFirst(TotalContentsRequest curRequest,String total_ny,String keyword_eng_conv,String analQuery,String user_uuid,String keyword_list){
		String ret_str = "";
		System.out.println("queryMapFirst-->"+analQuery);
		String q_sp = curRequest.getQuery().trim();
		
		//기자검색조건시 : q=(ALL_CONTENT:"김부선" AND NOT PRESSCATEGORY:"11001000000000000000")&pretty=true&from=0&size=1000
		
		
		LinkedHashMap<String,String> query_map = new LinkedHashMap<String,String>();
		
		String[] q_sp_s = q_sp.split("[ ]");
		
			for(int i=0;i<q_sp_s.length;i++) {
				if(q_sp_s[i] != null && q_sp_s[i].toString().trim().length() > 0 ) {
					query_map.put(q_sp_s[i].toString(), q_sp_s[i].toString());
				}
			}
		
		
		int c_i = 0;
		String dup_str = "";
		Set<String> keySet_u1 = query_map.keySet();
		  Iterator<String> iterator_u1 = keySet_u1.iterator();
		  while (iterator_u1.hasNext()) {
			  String comma_str = " ";
			  if(c_i == 0) {
				  comma_str = "";
			  }
			   String key = iterator_u1.next();
			   Object value = query_map.get(key);			   
			  
			   dup_str = dup_str + comma_str+key;
			   c_i++;
			   
		  }
		  
		  System.out.println("dup_str-->"+dup_str);
		 
		  
		  /*
		   기존 사용자 질의 분석간은 기존처럼 AND 연산으로 수행하고, 추출된 키워드 기반의 경우는 or  즉 전체적으로는 (기존질의간 AND) ANd (추출된 키워드간 OR)
		   */
		  String uuid_query = "";
		  
		  if(user_uuid != null && user_uuid.trim().length() > 0) {
			  //keywordApiServerUrl
			  try {
				     JSONObject jsonObject = new JSONObject();
					 JSONParser jsonParser = new JSONParser();
					 JSONObject press_onject = new JSONObject();
					 String FILE_NAME = "{\"contextInfo\":"+keyword_list.toString()+"}";
					 
					 System.out.println( FILE_NAME);
					 
					 jsonObject = (JSONObject) jsonParser.parse(FILE_NAME);
					 JSONArray item_arr = null;
					 try{item_arr = (JSONArray)jsonObject.get("contextInfo");}catch(Exception e){}
					 JSONObject json_arr2 = new JSONObject();
					 int cnts = 0;
					 int add_ratio = item_arr.size() + 1;
					 String uuid_query_add = "";
					 for(int i=0;i<item_arr.size();i++) {
						 if(item_arr.get(i) != null) {
						 String space_str = " ";
							String and_str = " OR ";
							if(cnts == 0) {
								space_str = "";
								and_str = "";
							}
							
							String add_str = "";
							if(item_arr.get(i).toString().trim().length() > 1) {
								
								String query_map_str = "";
								try {
									query_map_str = query_map.get(item_arr.get(i).toString().trim());
								}catch(Exception e) {
									
								}
								
								if(query_map_str != null && query_map_str.trim().length() > 0) {
									
								}else {
									
									if(q_sp.toString().trim().indexOf(item_arr.get(i).toString().trim()) > -1) {	
									}else {
											if(item_arr.get(i).toString().trim().indexOf(" ") > -1) {
												int chts_2 = 0;
												String[] add_str_sp = item_arr.get(i).toString().trim().split("[ ]");
												for(int j=0;j<add_str_sp.length;j++) {
													if(add_str_sp[j] != null) {
														String and_str2 = " OR ";
														String space_str2 = " ";
														if(chts_2 == 0) {
															space_str2 = "";
															and_str2 = "";
														}
													
														uuid_query_add = uuid_query_add+space_str2+and_str2+"(ALL_CONTENT:\""+add_str_sp[j].toString().trim()+"\")^"+(add_ratio-i);
														
														chts_2++;
													}
												}
												
											}else {
												uuid_query = uuid_query+space_str+and_str+"(ALL_CONTENT:\""+item_arr.get(i).toString().trim()+"\")^"+(add_ratio-i);
											}
										
									}
								}
								
								cnts++;
							}
							
						 }
						
					 }
					 
					 if(uuid_query_add != null && uuid_query_add.trim().length() > 0) {
						 uuid_query = uuid_query + " OR "+uuid_query_add;
					 }
					 
			  }catch(Exception e) {
				  e.printStackTrace();
			  }
		  }
		  
		  query_map.clear();
		  System.out.println( uuid_query);
				String[] q_sp_s1 = dup_str.split("[ ]");
				String in_query = "";
				 int chk_a=0;
				 String in_str = "";
				   // if(q_sp_s.length > 3) {
				
					 if(curRequest.getSfield() != null && curRequest.getSfield().contains("TITLE") && curRequest.getSfield().contains("CONTENTS")) {
						 for(int i=0;i<q_sp_s1.length;i++) {
								String space_str = " ";
								String and_str = " AND ";
								if(i == 0) {
									space_str = "";
									and_str = "";
								}
								
								if( q_sp_s1[i].toString().length() > 0) {
									in_str = in_str + space_str + q_sp_s1[i].toString().trim();
									
									in_query = in_query+space_str+and_str+"(ALL_CONTENT:\""+q_sp_s1[i].toString().trim()+"\")";
									
								}
								
							}
					 }else if(curRequest.getSfield() != null && curRequest.getSfield().contains("TITLE")) {
						 for(int i=0;i<q_sp_s1.length;i++) {
								String space_str = " ";
								String and_str = " AND ";
								if(i == 0) {
									space_str = "";
									and_str = "";
								}
								
								if( q_sp_s1[i].toString().length() > 0) {
									in_str = in_str + space_str + q_sp_s1[i].toString().trim();
									
									in_query = in_query+space_str+and_str+"(ART_TITLE:\""+q_sp_s1[i].toString().trim()+"\")";
									
								}
								
							}
					 }else {
						 for(int i=0;i<q_sp_s1.length;i++) {
								String space_str = " ";
								String and_str = " AND ";
								if(i == 0) {
									space_str = "";
									and_str = "";
								}
								
								if( q_sp_s1[i].toString().length() > 0) {
									in_str = in_str + space_str + q_sp_s1[i].toString().trim();
									
									in_query = in_query+space_str+and_str+"(ALL_CONTENT:\""+q_sp_s1[i].toString().trim()+"\")";
									
								}
								
							}
					 }
						
			
			
				System.out.println("in_query-->"+in_query);
					
						
				if(keyword_eng_conv != null && keyword_eng_conv.length() > 0) {
					 q_sp = keyword_eng_conv;
				}else {
					 q_sp = curRequest.getQuery().toLowerCase();
				}	
				
				String range_query = "";
				
				if(curRequest.getStartDate() != null && !curRequest.getStartDate().equals("null")
						&& curRequest.getStartDate().toString().length() > 5){
					
					if(curRequest.getEndDate() != null && !curRequest.getEndDate().equals("null")
							&& curRequest.getEndDate().toString().length() > 5){
						range_query = " AND (PRESSDATE:["+curRequest.getStartDate()+ " TO "+curRequest.getEndDate()+"])";
					}else {
						range_query = " AND (PRESSDATE:["+curRequest.getStartDate()+ " TO "+curRequest.getStartDate()+"])";
					}		
					
				}
				
				
				if(uuid_query != null && uuid_query.trim().length() > 0) {
					ret_str = "(("+in_query+" AND ("+uuid_query+")"+range_query+"))";	
				}else {
					ret_str = "(("+in_query+range_query+"))";	
				}
				
		
		
		
		
			
		return ret_str;
	}
	
	public static String queryMap20211105(TotalContentsRequest curRequest,String total_ny,String keyword_eng_conv,String analQuery,String user_uuid,String keyword_list){
		String ret_str = "";
		System.out.println("queryMapSecond eng_conv-->"+keyword_eng_conv);
		String q_sp = analQuery;
		
		
		LinkedHashMap<String,String> query_map = new LinkedHashMap<String,String>();
		
		String[] q_sp_s = q_sp.split("[ ]");
		
			for(int i=0;i<q_sp_s.length;i++) {
				if(q_sp_s[i] != null && q_sp_s[i].toString().trim().length() > 0 ) {
					query_map.put(q_sp_s[i].toString(), q_sp_s[i].toString());
				}
			}
		
		
		int c_i = 0;
		String dup_str = "";
		Set<String> keySet_u1 = query_map.keySet();
		  Iterator<String> iterator_u1 = keySet_u1.iterator();
		  while (iterator_u1.hasNext()) {
			  String comma_str = " ";
			  if(c_i == 0) {
				  comma_str = "";
			  }
			   String key = iterator_u1.next();
			   Object value = query_map.get(key);			   
			  
			   dup_str = dup_str + comma_str+key;
			   c_i++;
			   
		  }
		  
		  System.out.println("queryMapSecond dup_str-->"+dup_str);
		  /*
		   기존 사용자 질의 분석간은 기존처럼 AND 연산으로 수행하고, 추출된 키워드 기반의 경우는 or  즉 전체적으로는 (기존질의간 AND) ANd (추출된 키워드간 OR)
		   */
		  String uuid_query = "";
		  
		  if(user_uuid != null && user_uuid.trim().length() > 0) {
			  
			  try {
				 
				     JSONObject jsonObject = new JSONObject();
					 JSONParser jsonParser = new JSONParser();
					 JSONObject press_onject = new JSONObject();
					 String FILE_NAME = "{\"contextInfo\":"+keyword_list.toString()+"}";
					 
					 System.out.println( FILE_NAME);
					 
					 jsonObject = (JSONObject) jsonParser.parse(FILE_NAME);
					 JSONArray item_arr = null;
					 try{item_arr = (JSONArray)jsonObject.get("contextInfo");}catch(Exception e){}
					 JSONObject json_arr2 = new JSONObject();
					 int cnts = 0;
					 int add_ratio = item_arr.size() + 1;
					 String uuid_query_add = "";
					 for(int i=0;i<item_arr.size();i++) {
						 if(item_arr.get(i) != null) {
						 String space_str = " ";
							String and_str = " OR ";
							if(cnts == 0) {
								space_str = "";
								and_str = "";
							}
							
							String add_str = "";
							if(item_arr.get(i).toString().trim().length() > 1) {
								
								String query_map_str = "";
								try {
									query_map_str = query_map.get(item_arr.get(i).toString().trim());
								}catch(Exception e) {
									
								}
								if(query_map_str != null && query_map_str.trim().length() > 0) {
								}else {
									if(q_sp.toString().trim().indexOf(item_arr.get(i).toString().trim()) > -1) {	
									}else {
											if(item_arr.get(i).toString().trim().indexOf(" ") > -1) {
												int chts_2 = 0;
												String[] add_str_sp = item_arr.get(i).toString().trim().split("[ ]");
												for(int j=0;j<add_str_sp.length;j++) {
													if(add_str_sp[j] != null) {
														String and_str2 = " OR ";
														String space_str2 = " ";
														if(chts_2 == 0) {
															space_str2 = "";
															and_str2 = "";
														}
													
														uuid_query_add = uuid_query_add+space_str2+and_str2+"(ALL_CONTENT:\""+add_str_sp[j].toString().trim()+"\")^"+(add_ratio-i);
														
														chts_2++;
													}
												}
												
											}else {
												uuid_query = uuid_query+space_str+and_str+"(ALL_CONTENT:\""+item_arr.get(i).toString().trim()+"\")^"+(add_ratio-i);
											}
										
									}
								}
								
								cnts++;
							}
							
						 }
						
					 }
					 
					 if(uuid_query_add != null && uuid_query_add.trim().length() > 0) {
						 uuid_query = uuid_query + " OR "+uuid_query_add;
					 }
					 
			  }catch(Exception e) {
				  e.printStackTrace();
			  }
		  }
		  
		  query_map.clear();
		  System.out.println( uuid_query);
		  
		  String[] q_sp_s1 = dup_str.split("[ ]");
		String in_query = "";
		 int chk_a=0;
		 String in_str = "";
		   // if(q_sp_s.length > 3) {
		
			 if(curRequest.getSfield() != null && curRequest.getSfield().contains("TITLE") && curRequest.getSfield().contains("CONTENTS")) {
				 for(int i=0;i<q_sp_s1.length;i++) {
						String space_str = " ";
						String and_str = " AND ";
						if(i == 0) {
							space_str = "";
							and_str = "";
						}
						
						if( q_sp_s1[i].toString().length() > 0) {
							in_str = in_str + space_str + q_sp_s1[i].toString().trim();
							
							in_query = in_query+space_str+and_str+"(ALL_CONTENT:\""+q_sp_s1[i].toString().trim()+"\")";
							
						}
						
					}
			 }else if(curRequest.getSfield() != null && curRequest.getSfield().contains("TITLE")) {
				 for(int i=0;i<q_sp_s1.length;i++) {
						String space_str = " ";
						String and_str = " AND ";
						if(i == 0) {
							space_str = "";
							and_str = "";
						}
						
						if( q_sp_s1[i].toString().length() > 0) {
							in_str = in_str + space_str + q_sp_s1[i].toString().trim();
							
							in_query = in_query+space_str+and_str+"(ART_TITLE:\""+q_sp_s1[i].toString().trim()+"\")";
							
						}
						
					}
			 }else {
				 for(int i=0;i<q_sp_s1.length;i++) {
						String space_str = " ";
						String and_str = " AND ";
						if(i == 0) {
							space_str = "";
							and_str = "";
						}
						
						if( q_sp_s1[i].toString().length() > 0) {
							in_str = in_str + space_str + q_sp_s1[i].toString().trim();
							
							in_query = in_query+space_str+and_str+"(ALL_CONTENT:\""+q_sp_s1[i].toString().trim()+"\")";
							
						}
						
					}
			 }
		
	
		System.out.println("queryMapSecond in_query-->"+in_query);
			
				
		if(keyword_eng_conv != null && keyword_eng_conv.length() > 0) {
			 q_sp = keyword_eng_conv;
		}else {
			 q_sp = curRequest.getQuery().toLowerCase();
		}	
		
		String range_query = "";
		if(curRequest.getStartDate() != null && !curRequest.getStartDate().equals("null")
				&& curRequest.getStartDate().toString().length() > 5){
			
			if(curRequest.getEndDate() != null && !curRequest.getEndDate().equals("null")
					&& curRequest.getEndDate().toString().length() > 5){
				range_query = " AND (PRESSDATE:["+curRequest.getStartDate()+ " TO "+curRequest.getEndDate()+"])";
			}else {
				range_query = " AND (PRESSDATE:["+curRequest.getStartDate()+ " TO "+curRequest.getStartDate()+"])";
			}		
			
		}
		
		
		if(uuid_query != null && uuid_query.trim().length() > 0) {
			//ret_str = "(("+in_query+" AND ("+uuid_query+")"+range_query+"))";
			
			if(curRequest.getSfield() != null && curRequest.getSfield().contains("TITLE")) {
				ret_str = "(("+in_query+" AND ("+uuid_query+")"+range_query+"))";	
			}else {
				ret_str = "((ALL_CONTENT:\""+q_sp+"\") OR ("+in_query+" AND ("+uuid_query+")"+"))"+range_query;	
			}
			
			
		}else {
			if(curRequest.getSfield() != null && curRequest.getSfield().contains("TITLE")) {
				ret_str = "(("+in_query+range_query+"))";	
			}else {
				ret_str = "((ALL_CONTENT:\""+q_sp+"\") OR ("+in_query+"))"+range_query;	
			}
		}
		
		
		
		
		
			
		return ret_str;
	}
	
	public static String queryMapOR(TotalContentsRequest curRequest,String total_ny,String keyword_eng_conv,String analQuery){
		String ret_str = "";
		System.out.println("keyword_eng_conv-->"+keyword_eng_conv);
		
		String q_sp = analQuery;
		
		LinkedHashMap<String,String> query_map = new LinkedHashMap<String,String>();
		
		String[] q_sp_s = q_sp.split("[ ]");
		for(int i=0;i<q_sp_s.length;i++) {
			if(q_sp_s[i] != null && q_sp_s[i].toString().trim().length() > 1 ) {
				query_map.put(q_sp_s[i].toString(), q_sp_s[i].toString());
			}
		}
		
		int c_i = 0;
		String dup_str = "";
		Set<String> keySet_u1 = query_map.keySet();
		  Iterator<String> iterator_u1 = keySet_u1.iterator();
		  while (iterator_u1.hasNext()) {
			  String comma_str = " ";
			  if(c_i == 0) {
				  comma_str = "";
			  }
			   String key = iterator_u1.next();
			   Object value = query_map.get(key);			   
			  
			   dup_str = dup_str + comma_str+key;
			   c_i++;
			   
		  }
		  
		  System.out.println("dup_str-->"+dup_str);
		  query_map.clear();
		  
		  String[] q_sp_s1 = dup_str.split("[ ]");
		String in_query = "";
		 int chk_a=0;
		 String in_str = "";
		   // if(q_sp_s.length > 3) {
		
		 if(curRequest.getSfield() != null && curRequest.getSfield().contains("TITLE") && curRequest.getSfield().contains("CONTENTS")) {
			 for(int i=0;i<q_sp_s1.length;i++) {
					String space_str = " ";
					String and_str = " OR ";
					if(i == 0) {
						space_str = "";
						and_str = "";
					}
					
					if( q_sp_s1[i].toString().length() > 0) {
						in_str = in_str + space_str + q_sp_s1[i].toString().trim();
						
						in_query = in_query+space_str+and_str+"(ALL_CONTENT:\""+q_sp_s1[i].toString().trim()+"\")";
						
					}
					
				}
		 }else if(curRequest.getSfield() != null && curRequest.getSfield().contains("TITLE")){
			 for(int i=0;i<q_sp_s1.length;i++) {
					String space_str = " ";
					String and_str = " OR ";
					if(i == 0) {
						space_str = "";
						and_str = "";
					}
					
					if( q_sp_s1[i].toString().length() > 0) {
						in_str = in_str + space_str + q_sp_s1[i].toString().trim();
						
						in_query = in_query+space_str+and_str+"(ART_TITLE:\""+q_sp_s1[i].toString().trim()+"\")";
						
					}
					
				}
		 }else {
			 for(int i=0;i<q_sp_s1.length;i++) {
					String space_str = " ";
					String and_str = " OR ";
					if(i == 0) {
						space_str = "";
						and_str = "";
					}
					
					if( q_sp_s1[i].toString().length() > 0) {
						in_str = in_str + space_str + q_sp_s1[i].toString().trim();
						
						in_query = in_query+space_str+and_str+"(ALL_CONTENT:\""+q_sp_s1[i].toString().trim()+"\")";
						
					}
					
				}
		 }
		
	
		System.out.println("in_query-->"+in_query);
			
				
		if(keyword_eng_conv != null && keyword_eng_conv.length() > 0) {
			 q_sp = keyword_eng_conv;
		}else {
			 q_sp = curRequest.getQuery().toLowerCase();
		}
		
		String range_query = "";
		if(curRequest.getStartDate() != null && !curRequest.getStartDate().equals("null")
				&& curRequest.getStartDate().toString().length() > 5){
			
			if(curRequest.getEndDate() != null && !curRequest.getEndDate().equals("null")
					&& curRequest.getEndDate().toString().length() > 5){
				range_query = " AND (PRESSDATE:["+curRequest.getStartDate()+ " TO "+curRequest.getEndDate()+"])";
			}else {
				range_query = " AND (PRESSDATE:["+curRequest.getStartDate()+ " TO "+curRequest.getStartDate()+"])";
			}		
			
		}
		
		ret_str = "(("+in_query+range_query+"))";
		
		return ret_str;
	}
	
	public static String queryMap(TotalContentsRequest curRequest,String total_ny,String keyword_eng_conv){
		String ret_str = "";
	
		String SearchField_str = "";		
		String range_query = "";		
		String sfield_str = "";	
		String query_encode = "";
		String query_encode_new = "";
		String in_str = "";
		String  ret_st = "";
		String  ret_st_jaso = "";
		
		System.out.println("keyword_eng_conv-->"+keyword_eng_conv);
       	
	//	String q_sp = curRequest.getQuery().toLowerCase() + " "+ keyword_eng_conv;
		String q_sp = "";
		
		if(keyword_eng_conv != null && keyword_eng_conv.length() > 0) {
			 q_sp = keyword_eng_conv;
		}else {
			 q_sp = curRequest.getQuery().toLowerCase();
		}
		
		
		q_sp = getSTRFilterK4(q_sp);
		//System.out.println("q_sp-->"+q_sp);
		q_sp = StringReplaceRe(q_sp);
		
		query_encode = q_sp;
		
		//System.out.println("in_query-->"+q_sp+":");
		
		//System.out.println("q_sp-->"+q_sp);
		String[] q_sp_s = q_sp.split("[ ]");
		
		String in_query = "";
		String in_query_body = "";
	    
		String in_query_title = "";
	
	    int chk_a=0;
	   // if(q_sp_s.length > 3) {
			for(int i=0;i<q_sp_s.length;i++) {
				String space_str = " ";
				if(chk_a == 0) {
					space_str = "";
				}
				if(q_sp_s[i] != null && q_sp_s[i].trim().length() > 0) {
					in_query = in_query+space_str+q_sp_s[i].toString();
					in_query_body = in_query_body+space_str+q_sp_s[i].toString()+"*";
					in_query_title = in_query_title+space_str+"*"+q_sp_s[i].toString()+"*";
					chk_a++;
				}
				
				if(chk_a >= 2) {
					break;
				}
			}
			
			
			chk_a = 0;
			   // if(q_sp_s.length > 3) {
			try {
			String new_str = "";
					for(int i=0;i<q_sp_s.length;i++) {
						String space_str = " ";
						if(chk_a == 0) {
							space_str = "";
						}
						if(q_sp_s[i] != null && q_sp_s[i].trim().length() > 0) {
							new_str = new_str+space_str+q_sp_s[i].toString();
							//System.out.println("chk_a-->"+chk_a+"//"+new_str);
							chk_a++;
						}
						
					}
					
					if(new_str != null && new_str.trim().length() > 0) {
						query_encode = new_str.trim();
					}
			}catch(Exception e) {
				
			}
			
			String ngram_str = "";
			String ngram_title_str = "";
			String ngram_title_ser_str = "";
			String ngram_title_ser_content = "";
			try {
				ngram_str = ngramString(query_encode);
				
				
				/*
				 ((ART_TITLE:"상반기") AND (ART_TITLE:"임대주택")) OR ((ART_CONTENT:"상반기") AND (ART_CONTENT:"임대주택"))
				 field_str-->(ART_TITLE:"상반기") AND (ART_TITLE:"임대주택") OR (ART_CONTENT:"상반기") AND (ART_CONTENT:"임대주택")
				 */
				
				 int chk_a2=0;
				   // if(q_sp_s.length > 3) {
				 String[] ngram_str_sp =ngram_str.split("[ ]"); 
				 double d_score = 1000.0;
				 double d_score2 = 0.0;
				 double d_score3 = 0.0;
						for(int i=0;i<ngram_str_sp.length;i++) {
							String space_str = "|";
							String space_str2 = "  ";
							if(chk_a2 == 0) {
								space_str = "";
								space_str2 = "";
							}
							if(ngram_str_sp[i] != null && ngram_str_sp[i].trim().length() > 0) {
								//ngram_title_str = ngram_title_str+space_str+"(ART_TITLE:\""+ngram_str_sp[i].toString()+"\")";			
								
								try {
									d_score2 = d_score-(i*300);
									d_score3 = d_score-(i*10);
								} catch(Exception e){}
								ngram_title_ser_str = ngram_title_ser_str+space_str+"(ART_TITLE_SER:\"*"+ngram_str_sp[i].toString()+"*\")^"+d_score3;						
								
								ngram_title_ser_content = ngram_title_ser_content+space_str+"(ART_CONTENT_SER:\""+ngram_str_sp[i].toString()+"*\")^"+d_score2;		
								chk_a2++;
							}
							
							if(chk_a2 >= 3) {
								break;
							}
						}
						
			}catch(Exception e) {
				
			}
	      
			System.out.println("ngram_str-->"+ngram_str);
			
			String field_str = "";
			String title_field_str = "";
			String title_field_str2 = "";
			String ART_TITLE_str = "";
			String ART_CONTENT_str = "";
			String ART_TITLE_SER_str = "";
			String ART_CONTENT_SER_str = "";
			
			String ART_TITLE_str2 = "";
			
			String ART_TITLE_SER_str2 = "";
			
			
					
			
			 int chk_a1=0;
			   // if(q_sp_s.length > 3) {
					for(int i=0;i<q_sp_s.length;i++) {
						String space_str = " AND ";
						String space_str2 = " OR ";
						if(chk_a1 == 0) {
							space_str = "";
							space_str2 = "";
						}
						if(q_sp_s[i] != null && q_sp_s[i].trim().length() > 0) {
							ART_TITLE_str = ART_TITLE_str+space_str+"(ART_TITLE:\""+q_sp_s[i].toString()+"\")";
							ART_CONTENT_str = ART_CONTENT_str+space_str+"(ART_CONTENT:\""+q_sp_s[i].toString()+"\")";
							
							ART_TITLE_SER_str = ART_TITLE_SER_str+space_str+"(ART_TITLE_SER:\"*"+q_sp_s[i].toString()+"*\")^1000000";
							ART_CONTENT_SER_str = ART_CONTENT_SER_str+space_str+"(ART_CONTENT_SER:\"*"+q_sp_s[i].toString()+"*\")^100";
							
							ART_TITLE_str2 = ART_TITLE_str2+space_str+"(ART_TITLE:"+q_sp_s[i].toString()+")";
							ART_TITLE_SER_str2 = ART_TITLE_SER_str2+space_str+"(ART_TITLE_SER:*"+q_sp_s[i].toString()+"*)^1000000";
							
							chk_a1++;
						}
						
						if(chk_a1 >= 2) {
							break;
						}
					}
					
					field_str = "(("+ART_TITLE_str+") OR ("+ART_CONTENT_str+"))|(("+ART_TITLE_SER_str+") OR ("+ART_CONTENT_SER_str+"))";
					title_field_str =  "(("+ART_TITLE_str+"))|(("+ART_TITLE_SER_str+"))";
					
					title_field_str2 =  "(("+ART_TITLE_str2+"))|(("+ART_TITLE_SER_str2+"))";
					
					if(ngram_str != null && ngram_str.trim().length() > 0) {
						title_field_str2 = "("+ngram_title_ser_str+")";
					}
	  //  }
		//System.out.println("field_str-->"+field_str);
		//System.out.println("in_query-->"+in_query);
       	try {
       		in_str = StringReplaceRe(in_query);
       		
       		//System.out.println("in_str-->"+in_str);
       		
       	  in_str = StringReplaceReNone(in_str);
      	  ret_st = hangulToJaso(in_str);
      	  ret_st_jaso = hangulToJasoChosung(in_str);
      	  ret_st = ret_st.replaceAll("[ ]", "");
 	      ret_st_jaso = ret_st_jaso.replaceAll("[ ]", "");
		}catch(Exception e) {
			
		}
       	
       		    
			try {
				
				
				query_encode_new = in_query.trim();
				
				
			}catch(Exception e) {
				
			}
		

			//ART_TITLE_JASO 자소단위 검색을 하기위한 필드
			  if(ret_st.trim().length() <= 0) {
				  ret_st = curRequest.getQuery();
				  
			    }
			    
			    if(ret_st_jaso.trim().length() <= 0) {
			    	ret_st_jaso = curRequest.getQuery();
			    }
			    
			    
			    try {
			    	//ret_st = URLEncoder.encode(ret_st,"UTF-8");
				}catch(Exception e) {
					
				}
			    
			    try {
			    	//ret_st_jaso = URLEncoder.encode(ret_st_jaso,"UTF-8");
				}catch(Exception e) {
					
				}
			    
			    
			    String ret_st_re =  "";
			    String ret_st_jaso_re =  " ";    
	    
			
					
		if(curRequest.getSfield() != null && curRequest.getSfield().trim().length()>0) {
		
			if(curRequest.getSfield().contains("TITLE") && curRequest.getSfield().contains("CONTENTS")) {
				
			
				if(total_ny.equals("Y")) {
					
			
					sfield_str = sfield_str + "(((ART_TITLE_SER:\""+query_encode+"\")^100000000000000000.5) AND ((ART_TITLE:\""+query_encode+"\")^100.5))";
					
					sfield_str = sfield_str + "|(((ART_CONTENT_SER:\""+query_encode_new+"\")^10000000000000000.5) AND ((ART_CONTENT:\""+query_encode_new+"\")^1.5))";
					
					sfield_str = sfield_str + "|(((ART_TITLE_SER:"+in_query_title.replaceAll("[ ]","+")+")^10) AND ((ART_TITLE:"+query_encode.replaceAll("[ ]","+")+")^1.05))";
					sfield_str = sfield_str + "|(((ART_CONTENT_SER:"+in_query_body.replaceAll("[ ]","+")+")^10) AND ((ART_CONTENT:"+query_encode.replaceAll("[ ]","+")+")^1.05))";
					
					
					if(in_str.trim().length() <= 0) {
						ret_st_jaso_re =  "((ART_TITLE_JASO:\""+ret_st_jaso+ "\")^0.00001)";
						ret_st_re =  "|((ART_TITLE_JASO:\""+ret_st+"\")^0.00001)";
						sfield_str = "("+ret_st_jaso_re+ret_st_re+")";
					}
					
					
				}else {
					
					String sfield_str2 =  "";
					
					/*
					sfield_str2 = sfield_str2 + "(((ART_TITLE_SER:\""+in_query_title+"\")^100000000000000000.5) AND ((ART_CONTENT_SER:\""+in_query_title+"\")^100.5))";
					
					sfield_str2 = sfield_str2 + "|(((ART_TITLE:\""+query_encode_new.replaceAll("[ ]","+")+"\")^1000000.5) AND ((ART_CONTENT:\""+query_encode_new.replaceAll("[ ]","+")+"\")^1.5))";
					
					//sfield_str2 = sfield_str2 + "|(((ART_TITLE_SER:"+in_query_title+")^1000000.5) OR ((ART_TITLE:"+query_encode+")^1.05))";
					
					sfield_str2 = sfield_str2 + "|(((ART_TITLE_SER:"+query_encode_new+")^1000000.5) AND ((ART_CONTENT_SER:"+query_encode_new+")^1.05))";
					
					sfield_str2 = sfield_str2 + "|(((ART_TITLE:"+query_encode_new+")^1000000.5) AND ((ART_CONTENT:"+query_encode_new+")^1.05))";
					*/
					
					if(ngram_str != null && ngram_str.trim().length() > 0) {
						sfield_str2 = "(("+ngram_title_ser_str+")|("+ngram_title_ser_content+"))";
						sfield_str = sfield_str2;
					}else {
						sfield_str2 = sfield_str2 + "(((ART_TITLE_SER:\""+in_query_title+"\")^100000000000000000.5) AND ((ART_CONTENT_SER:\""+in_query_title+"\")^100.5))";
						
						sfield_str2 = sfield_str2 + "|(((ART_TITLE:\""+query_encode_new.replaceAll("[ ]","+")+"\")^1000000.5) AND ((ART_CONTENT:\""+query_encode_new.replaceAll("[ ]","+")+"\")^1.5))";
						
						//sfield_str2 = sfield_str2 + "|(((ART_TITLE_SER:"+in_query_title+")^1000000.5) OR ((ART_TITLE:"+query_encode+")^1.05))";
						
						sfield_str2 = sfield_str2 + "|(((ART_TITLE_SER:"+query_encode_new+")^1000000.5) AND ((ART_CONTENT_SER:"+query_encode_new+")^1.05))";
						
						sfield_str2 = sfield_str2 + "|(((ART_TITLE:"+query_encode_new+")^1000000.5) AND ((ART_CONTENT:"+query_encode_new+")^1.05))";
						
						sfield_str = "("+field_str+")";
						
					}
					
					//sfield_str = "("+field_str+")";
					
				}
				
				
				
			}else if(curRequest.getSfield().contains("TITLE")) {
				
				if(total_ny.equals("Y")) {
					
					sfield_str = sfield_str + " ((ART_TITLE_SER:\""+in_query_title+ "\")^100000000000000000.5)|((ART_TITLE_SER:"+in_query_title.replaceAll("[ ]","+")+ ")^10000.5)";
					
					//sfield_str = sfield_str + "((ART_TITLE:\""+query_encode+ "\")^100.5 OR (ART_TITLE:"+query_encode+")^0.00005)";
					//sfield_str = sfield_str + " OR ((ART_TITLE_JASO:"+ret_st_jaso+")^0.00001) ";
					if(in_str.trim().length() <= 0) {
						ret_st_jaso_re =  "((ART_TITLE_JASO:\""+ret_st_jaso+ "\")^0.00001)";
						ret_st_re =  "|((ART_TITLE_JASO:\""+ret_st+ "\")^0.00001)";
						sfield_str = "("+ret_st_jaso_re+ret_st_re+")";
					}
				}else {
					
					String sfield_str2 =  " ((ART_TITLE_SER:\""+query_encode+ "\")^100000000000000000.5)|((ART_TITLE:\""+query_encode+"\")^10.5)|"
							+ "((ART_TITLE:"+query_encode_new.replaceAll("[ ]","+")+ ")^0.00005)|((ART_TITLE_SER:"+in_query_title.replaceAll("[ ]","+")+ ")^100000000000000000.5)";
					
					//ret_st_jaso_re =  "((ART_TITLE_JASO:\""+ret_st_jaso+ "\")^0.00001)";
					//ret_st_re =  "|((ART_TITLE_JASO:\""+ret_st+ "\")^0.00001)";
					if(in_str.trim().length() <= 0) {
						ret_st_jaso_re =  "|((ART_TITLE_JASO:\""+ret_st_jaso+ "\")^0.00001)";
						ret_st_re =  "|((ART_TITLE_JASO:\""+ret_st+ "\")^0.00001)";
						//sfield_str = "("+ret_st_jaso_re+ret_st_re+")";
					}
					
					sfield_str = "("+title_field_str2+ret_st_jaso_re+ret_st_re+")";
				}
				
			  }else if(curRequest.getSfield().contains("WRITER")) {
				
				if(total_ny.equals("Y")) {
					
					sfield_str = sfield_str + " ((BY_LINE_LIST:\""+query_encode+ "\")^1.0)";
					
					//sfield_str = sfield_str + "((ART_TITLE:\""+query_encode+ "\")^100.5 OR (ART_TITLE:"+query_encode+")^0.00005)";
					//sfield_str = sfield_str + " OR ((ART_TITLE_JASO:"+ret_st_jaso+")^0.00001) ";
					if(in_str.trim().length() <= 0) {
						ret_st_jaso_re =  "((BY_LINE_LIST:\""+ret_st_jaso+ "\")^0.00001)";
						ret_st_re =  "|((BY_LINE_LIST:\""+ret_st+ "\")^0.00001)";
						sfield_str = "("+ret_st_jaso_re+ret_st_re+")";
					}
				}else {
					ret_st_jaso_re =  "((BY_LINE_LIST:\""+ret_st_jaso+ "\")^0.00001)";
					ret_st_re =  "|((BY_LINE_LIST:\""+ret_st+ "\")^0.00001)";
					sfield_str = "("+ret_st_jaso_re+ret_st_re+")";
				}	
			//	sfield_str = sfield_str + "((ART_TITLE:\""+query_encode+ "\")^5.0)";
			}else if(curRequest.getSfield().contains("CONTENTS") && !curRequest.getSfield().contains("TITLE")) {
			
				if(total_ny.equals("Y")) {
					sfield_str = sfield_str + "((ART_CONTENT_SER:\""+query_encode+ "\")^100000000000000000.5)|((ART_CONTENT:\""+query_encode+ "\")^0.5)|"
							+ "((ART_CONTENT:"+query_encode_new.replaceAll("[ ]","+")+ ")^0.00005)|((ART_CONTENT_SER:"+in_query_body.replaceAll("[ ]","+")+ ")^100000000000000000.5)";
					
					//sfield_str = sfield_str + "((ART_CONTENT:\""+query_encode+ "\")^100.5 OR (ART_CONTENT:"+query_encode+")^0.00005)";
					if(in_str.trim().length() <= 0) {
						ret_st_jaso_re =  "((ART_TITLE_JASO:\""+ret_st_jaso+ "\")^0.00001)";
						ret_st_re =  "|((ART_TITLE_JASO:\""+ret_st+ "\")^0.00001)";
						sfield_str = "("+ret_st_jaso_re+ret_st_re+")";
					}
				}else {
					ret_st_jaso_re =  "((ART_TITLE_JASO:\""+ret_st_jaso+ "\")^0.00001)";
					ret_st_re =  "|((ART_TITLE_JASO:\""+ret_st+ "\")^0.00001)";
					sfield_str = "("+ret_st_jaso_re+ret_st_re+")";
				}
				
			}

			
		}else {
			if(total_ny.equals("Y")) {
				
				sfield_str = sfield_str + "(((ART_TITLE_SER:\""+query_encode+"\")^100000000000000000.5) AND ((ART_TITLE:\""+query_encode+"\")^100.5))";
				
				sfield_str = sfield_str + "|(((ART_CONTENT_SER:\""+query_encode_new+"\")^10000000000000000.5) AND ((ART_CONTENT:\""+query_encode_new+"\")^1.5))";
				
				sfield_str = sfield_str + "|(((ART_TITLE_SER:"+in_query_title.replaceAll("[ ]","+")+")^10) AND ((ART_TITLE:"+query_encode.replaceAll("[ ]","+")+")^1.05))";
				sfield_str = sfield_str + "|(((ART_CONTENT_SER:"+in_query_body.replaceAll("[ ]","+")+")^10) AND ((ART_CONTENT:"+query_encode.replaceAll("[ ]","+")+")^1.05))";
				
				
				if(in_str.trim().length() <= 0) {
					ret_st_jaso_re =  "((ART_TITLE_JASO:\""+ret_st_jaso+ "\")^0.00001)";
					ret_st_re =  "|((ART_TITLE_JASO:\""+ret_st+"\")^0.00001)";
					sfield_str = "("+ret_st_jaso_re+ret_st_re+")";
				}
			}else {
				ret_st_jaso_re =  "((ART_TITLE_JASO:\""+ret_st_jaso+"\")^0.00001)";
				ret_st_re =  "|((ART_TITLE_JASO:\""+ret_st+"\")^0.00001)";
				sfield_str = "("+ret_st_jaso_re+ret_st_re+")";
			}
			
		}
		
		
		
		if(curRequest.getStartDate() != null && !curRequest.getStartDate().equals("null")
				&& curRequest.getStartDate().toString().length() > 5){
			
			if(curRequest.getEndDate() != null && !curRequest.getEndDate().equals("null")
					&& curRequest.getEndDate().toString().length() > 5){
				range_query = " AND (PRESSDATE:["+curRequest.getStartDate()+ " TO "+curRequest.getEndDate()+"])";
			}else {
				range_query = " AND (PRESSDATE:["+curRequest.getStartDate()+ " TO "+curRequest.getStartDate()+"])";
			}		
			
		}
		
	
		
		ret_str = sfield_str + range_query;
		
		System.out.println("TOTAL_SEARCH_QUERY == "+ret_str);
		
		//url = 	search_server_url+"q=(ART_TITLE:"+URLEncoder.encode(totalContentsRequest.getQuery(),"UTF-8")+")&pretty=true&from=0&size=10";
		
	
		return ret_str;
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
			con.setReadTimeout(10000);
			con.setConnectTimeout(10000);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Keep-Alive", "timeout=10000, max=10000");
            
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


public static String searchAiAPi(String y_url,String uuid) throws IOException {
	String response = "";
	
	String apiurl = y_url+uuid;;
	System.out.println(apiurl);
	URL object=new URL(apiurl); //테스트서버 용  port 기준 url 
	
	HttpURLConnection httpcon = (HttpURLConnection) ((object.openConnection()));
	httpcon.setDoOutput(true);
	httpcon.setRequestProperty("Content-Type", "application/text");
	httpcon.setRequestProperty("Accept", "application/json");
	httpcon.setReadTimeout(1000);
	httpcon.setConnectTimeout(1000);	
	httpcon.setRequestProperty("Keep-Alive", "timeout=1000, max=1000");	
	httpcon.setRequestMethod("GET");
	httpcon.setDefaultUseCaches(true);	
	httpcon.connect();
	try{
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(),"UTF-8"));		
		String inputLine;
		
		while((inputLine = br.readLine()) != null) {
			sb.append(inputLine + "\n"); 
		}
		br.close();
		
		response = sb.toString();
		
	}catch(Exception e){
		e.printStackTrace();
	}
	
	httpcon.disconnect();
	return response;
}


public JSONObject elSearch2(String y_url,String querystr, TotalContentsRequest totalContentsRequest) throws IOException {
	JSONObject response = new JSONObject();
	
	
	try{
		//URL object=new URL("http://175.198.48.193:6300/news_alias/_search?pretty=true&from=0&size=10&sort=_score:desc,PRESSDATE:desc"); //테스트서버 용  port 기준 url 		
		System.out.println("y_url--->"+y_url);
		URL object=new URL(y_url); //테스트서버 용  port 기준 url 
		
		HttpURLConnection httpcon = (HttpURLConnection) ((object.openConnection()));
		httpcon.setDoOutput(true);
		httpcon.setRequestProperty("Content-Type", "application/json");
		httpcon.setRequestProperty("Accept", "application/json");
		httpcon.setReadTimeout(30000);
		httpcon.setConnectTimeout(30000);
		
		httpcon.setRequestProperty("Keep-Alive", "timeout=30000, max=100000");
		
		httpcon.setRequestMethod("GET");
		httpcon.setDefaultUseCaches(true);
		
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
		System.out.println("querystr--->"+querystr);
		querystr = querystr.replaceAll("%20", "+");
		term.put("query",querystr);
		
		highlight_fieldss.put("fragment_size",150);
		highlight_fieldss.put("number_of_fragments",3);
		highlight_fieldss.put("fragmenter","simple");
		highlight_fieldss.put("type","plain");
		
		if(totalContentsRequest.getSfield() != null) {
				if(totalContentsRequest.getSfield() != null && totalContentsRequest.getSfield().trim().length()>0) {
					
					if(totalContentsRequest.getSfield().contains("TITLE") && totalContentsRequest.getSfield().contains("CONTENTS")) {
					
						highlight_fields.put("ART_TITLE",highlight_fieldss);
						highlight_fields.put("ART_CONTENT",highlight_fieldss);
						//highlight_fields.put("ART_TITLE_SER",highlight_fieldss);
						//highlight_fields.put("ART_CONTENT_SER",highlight_fieldss);
						//highlight_fields.put("ART_TITLE_JASO",highlight_fieldss);
						
					}else if(totalContentsRequest.getSfield().contains("TITLE")) {
						highlight_fields.put("ART_TITLE",highlight_fieldss);
						//highlight_fields.put("ART_TITLE_SER",highlight_fieldss);
						//highlight_fields.put("ART_TITLE_JASO",highlight_fieldss);
					}else if(totalContentsRequest.getSfield().contains("CONTENTS") && !totalContentsRequest.getSfield().contains("TITLE")) {
						highlight_fields.put("ART_CONTENT",highlight_fieldss);
						//highlight_fields.put("ART_CONTENT_SER",highlight_fieldss);
						//highlight_fields.put("ART_TITLE_JASO",highlight_fieldss);
					}
					
				}else {
					highlight_fields.put("ART_TITLE",highlight_fieldss);
					highlight_fields.put("ART_CONTENT",highlight_fieldss);
					//highlight_fields.put("ART_TITLE_SER",highlight_fieldss);
					//highlight_fields.put("ART_CONTENT_SER",highlight_fieldss);
					//highlight_fields.put("ART_TITLE_JASO",highlight_fieldss);
					
				}
			}else {
				highlight_fields.put("ART_TITLE",highlight_fieldss);
				highlight_fields.put("ART_CONTENT",highlight_fieldss);
			}
		
		
		
		
		highlight.put("fields",highlight_fields);
		highlight.put("pre_tags","<b>");
		highlight.put("post_tags","</b>");
		
		query.put("query_string",term);
		parent.put("query",query);
		parent.put("highlight",highlight);
		
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
			    
			  // System.out.println(sb.toString()); 
			    
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
			
			os.flush();
			
			httpcon.disconnect();
			
			
	}catch(Exception e){
		e.printStackTrace();
	}
	
	
	return response;
}

	
public static String ngramString(String texts) { 
	String ret_str = "";
	LinkedHashMap   ngram_hash =  new LinkedHashMap ();
	try{
		  String[] title_exp = texts.split("[ ]");
		  
		  for(int i=0;i<title_exp.length;i++){
			  if(title_exp[i] != null){
				  String n_str = title_exp[i].toString();
				  if(n_str.length() <= 1){
					  if(n_str.trim().toString().length() > 0){
						  //System.out.println("1--->"+n_str);
						//  ngram_hash.put(n_str, n_str);
					  }
				  }else {
					  //System.out.println("2--->"+n_str);
					  if(n_str.toString().length() > 1){									  
						  String n_gr_1 = n_str.substring(0, 3);
						 // System.out.println("3-22->"+n_gr_1);
						  ngram_hash.put(n_gr_1, n_gr_1);
						  String n_gr_2 = "";
						  for(int j=3;j<n_str.length();j++){
							  String n_gr_str = "";
							  n_gr_str = n_str.substring(j, j+1);
							  if(j==3){
								  n_gr_2  = n_gr_2+n_gr_1+n_gr_str;
							  }else {
								  n_gr_2  = n_gr_2+n_gr_str;
							  }
							 // System.out.println("3--->"+n_gr_2);
							  ngram_hash.put(n_gr_2, n_gr_2);
						  }	
						  
						  
					  }else {									  
						  
					  }
				  }
				  
				  
			  }
		  }
	  }catch(Exception e){
		  
	  }	
	
	  Set<String> keySet_u1 = ngram_hash.keySet();
	  Iterator<String> iterator_u1 = keySet_u1.iterator();
	  while (iterator_u1.hasNext()) {
		   String key = iterator_u1.next();
		   Object value = ngram_hash.get(key);			   
		   //System.out.println(key+"//"+value);
		   ret_str = ret_str + key+" ";
	  }
	ngram_hash.clear();
	
	
	return ret_str.trim();
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
	
	            if (c != 0) result = result + JongSung[c] ; // c가 0이 아니면, 즉 받침이 있으면
	        } else {
	            result = result + ch;
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
	
	
	public static String getSTRFilterK4(String str){ 	 
		 
		  String str_imsi   = "";  	

			String []filter_word ={"\\(","\\)","\\[","\\]","\\~","\\^","\\&","\\*","\\+","\\=","\\|","\\\\","\\}","\\{","\\\"","\\'","\\:","\\;","\\.","\\?","\\/","\\■","\\★","\\♡","\\！","\\!","\\%","\\-"};

		  for(int i=0;i<filter_word.length;i++){ 
		   //while(str.indexOf(filter_word[i]) >= 0){ 
			  str_imsi = str.replaceAll(filter_word[i]," "); 
			  str = str_imsi; 
		   //} 
		  } 
			//System.out.println("str :::::::-->"+str);
			return str; 

		 } 
	
	 public static String getSTRFilterKSpace(String str){ 	 
		 
		  String str_imsi   = "";  	

			String []filter_word ={"\\(","\\)","\\[","\\]","\\~","\\^","\\&","\\*","\\+","\\=","\\|","\\\\","\\}","\\{","\\\"","\\'","\\:","\\;","\\.","\\?","\\/","\\■","\\★","\\♡","\\！","\\!","\\%","\\-","[ ]"};

		  for(int i=0;i<filter_word.length;i++){ 
		   //while(str.indexOf(filter_word[i]) >= 0){ 
			  str_imsi = str.replaceAll(filter_word[i],""); 
			  str = str_imsi; 
		   //} 
		  } 
			//System.out.println("str :::::::-->"+str);
			return str; 

		 } 
	
	
	public static String StringReplaceRe(String str){       
		   String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
	      str =str.replaceAll(match, " ");
	      return str;
	   }
	
	public static String StringReplaceReNone(String str){       
	      String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
	      str =str.replaceAll(match, "");
	      return str;
	   }

	public static String StringReplace(String str){       
	      String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z一-龥0-9a-zA-Z\\s]";
	      str =str.replaceAll(match, " ");
	      return str;
	   }
}
