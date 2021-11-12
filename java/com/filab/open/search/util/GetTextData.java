
package com.filab.open.search.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class GetTextData {
	static final String defaultCharSet = "euc-kr";

	int dataSize;
	int errSize;
	String itemSpliter = "|";

	BufferedReader textData = null;

	
	public GetTextData(){
		clear();
	}
	

	public void clear(){
		textData = null;

		dataSize = 0;
		errSize = 0;
	}
	

	
	public void setSpliter(String item){
		itemSpliter = item;
	}

	
	public void openFile(String fileName, String charSet){
		File dataFile = new File(fileName);

		try{		
			textData = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), charSet));
		} catch(UnsupportedEncodingException ee){
			try{
				textData = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), defaultCharSet));
			}catch(IOException e) {
				e.printStackTrace();
			}
		} catch(IOException e) {
		}
	}

	public void closeFile(){
		try{
			if( textData==null ) return;
			textData.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public String getAllData(){
		if( textData==null ) return null;

		StringBuffer data = new StringBuffer();
		
		try{
			String line = null;
			while( (line=textData.readLine())!=null ){
				data.append(line); 
			}
		}catch(Exception e){
		}

		return data.toString();
	}

	public String getLine(){
		String line = null;
		if( textData==null ) return null;

		try{
			line = textData.readLine();
		}catch(Exception e){
			return null;
		}

		return line;
	}

	
}
