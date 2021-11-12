
package com.filab.open.search.util;

import java.io.UnsupportedEncodingException;


public class KcubeUtils {
	
	
	public static String hexIntVal(String[] hdtype){
		int ret_int;
		int sum_int=0;
		String ret_str = "";
		for(int i=0;i<hdtype.length;i++){
			String hdtype_s = hdtype[i].toString().replaceFirst("^0x", "");
			
			sum_int = sum_int + Integer.parseInt(hdtype_s, 16);
			//System.out.println(hdtype[i].toString() +"ret_str===>"+hdtype_s+"::::::::::"+Integer.parseInt(hdtype_s, 16));
		}
		ret_int = sum_int;
		
		ret_str = String.format("%02X%n", ret_int);
		//System.out.println("ret_int===>"+ret_int+";;;;"+ret_str);
		return "0x"+ret_str.trim();
	}

	public static String isHdValueHdr(String appid,String stbVer) {
		
		String returnValue = "";
		/*
		 * SD:0x01,HD:0x02,FHD:0x04,FD:0x04,UHD:0x08,UD:0x08
		 */
		if(stbVer == null || stbVer.equals("")){
			stbVer = "0.0";
		}
		String[] hd_arr_A = {"0x01","0x02"}; 
		String[] hd_arr_B = {"0x01","0x02","0x04"}; 
		String[] hd_arr_C = {"0x01","0x02","0x04","0x08"}; 	   
		returnValue = hexIntVal(hd_arr_C);				
		return returnValue;
	}
	

	

	public static String charSetConvertEuckr(String convstr) throws UnsupportedEncodingException{
		String ret_str = "";
		try{
		ret_str = new String(convstr.getBytes(),"ISO-8859-1");
		
		}catch(Exception e){
			
		}
		
		return ret_str;
		
	}
	
	public static String charSetConvert(String convstr) throws UnsupportedEncodingException{
		String ret_str = "";
		try{
		ret_str = new String(convstr.getBytes(),"UTF-8");
		}catch(Exception e){
			
		}
		
		return ret_str;
		
	}
	/*
	public static String charSetConvertEuckr(String convstr) throws UnsupportedEncodingException{
		String ret_str = "";
		try{
		ret_str = new String(convstr.getBytes(),"EUC-KR");
		
		}catch(Exception e){
			
		}
		
		return ret_str;
		
	}
	*/
	
}
