package com.filab.open.search.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import kr.co.shineware.nlp.komoran.core.Komoran;

import org.springframework.beans.factory.annotation.Autowired;

public class AnalDefaultServiceImpl implements AnalDefaultService{
	
	public static String Dic_path;
	public static String Key_path;
	public static String amidicpath;
	public static Komoran komoran;
	
	public static String keyFilepath;
	
	public String getAmidicpath() {
		return amidicpath;
	}	
	public void setAmidicpath(String amidicpath) {
		this.amidicpath = amidicpath;
	}
	
	public String getKeyFilepath() {
		return keyFilepath;
	}	
	public void setKeyFilepath(String keyFilepath) {
		this.keyFilepath = keyFilepath;
	}
	
	
	@Autowired
	public void setDicMap() {
		
		Dic_path = System.getProperty("defDicBase.pathDic");
		loadDicInit(Dic_path);
		//Key_path = System.getProperty("defKeyFileBase.pathKeyFile");
		//loadKeyFile();
		
	}
	
	
	public static HashMap<String, String> keyFileCro = null;
		
	
	public HashMap<String, String> loadKeyFile() {	
		if(keyFilepath == null || keyFilepath.equals("null")){
			keyFilepath = System.getProperty("defKeyFileBase.pathKeyFile").toString();
		}
		
		if(keyFileCro == null) { 			
			try {
				 readData();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{			
			keyFileCro.clear(); 	
			try {
				readData();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return keyFileCro;
	}
	
	public void readData() throws Exception{
		if(keyFilepath == null || keyFilepath.equals("null")){
			keyFilepath = System.getProperty("defKeyFileBase.pathKeyFile").toString();
		}
		
		if(keyFileCro==null) keyFileCro = new HashMap<String, String>();
		BufferedReader redirectionFile = new BufferedReader(new InputStreamReader(new FileInputStream(keyFilepath),"euc-kr"));
		String fileLine;
		while ((fileLine = redirectionFile.readLine()) != null) {
			if(fileLine != null){
				keyFileCro.put("filabkey", fileLine.toString());
				System.out.println(fileLine);
			}
		}
		redirectionFile.close();
	}
	
	public String getCodeString(String resourceKey){
		if(keyFileCro==null) return "";
		String re_str = "";
		
						try{
							re_str = keyFileCro.get(resourceKey.toString().trim()).toString();
						}catch(Exception e){
							
						}
			
		return re_str;
	}
	
	
	public void loadDicInit(String paths){	
		
		if(amidicpath == null || amidicpath.equals("null")){
			amidicpath = System.getProperty("defDicBase.pathDic").toString();
		}
		System.out.println("사전 데이타 처리 경로-->"+amidicpath);
		komoran = null;
		if(komoran == null) { 
			try {
				komoran = new Komoran(amidicpath+"/models_full");
				//komoran.setFWDic("user_data/fwd.user");
				//komoran.setUserDic("user_data/dic.user");
				
				komoran.setFWDic(amidicpath+"/user_data/fwd.user");
				komoran.setUserDic(amidicpath+"/user_data/dic.user");
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
}

