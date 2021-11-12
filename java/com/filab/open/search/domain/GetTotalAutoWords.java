
package com.filab.open.search.domain;

import java.io.PrintWriter;

import org.apache.commons.lang.StringUtils;

import com.filab.open.search.util.ObjectUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ACWORD_ITEM")
public class GetTotalAutoWords {
	@XStreamAlias("CONST_ID") 
	private String CONST_ID;
	
	public String getCONST_ID() {
		try{
			
		}catch(Exception e){
			CONST_ID = "";
		}
		return CONST_ID;
	}

	public void setCONST_ID(String cCONST_ID) {
		try{
			this.CONST_ID = cCONST_ID;
		}catch(Exception e){
			this.CONST_ID = "";
		}
		
	}
	
	
	@XStreamAlias("TITLE") 
	private String TITLE;
	
	public String getTITLE() {
		try{
			
		}catch(Exception e){
			TITLE = "";
		}
		return TITLE;
	}

	public void setTITLE(String cTITLE) {
		try{
			this.TITLE = cTITLE;
		}catch(Exception e){
			this.TITLE = "";
		}
		
	}
	
	
	@XStreamAlias("CATEGORY_NAME") 
	private String CATEGORY_NAME;
	
	public String getCATEGORY_NAME() {
		try{
			
		}catch(Exception e){
			CATEGORY_NAME = "";
		}
		return CATEGORY_NAME;
	}

	public void setCATEGORY_NAME(String cCATEGORY_NAME) {
		try{
			this.CATEGORY_NAME = cCATEGORY_NAME;
		}catch(Exception e){
			this.CATEGORY_NAME = "";
		}
		
	}
	
	@XStreamAlias("SERIES_YN") 
	private String SERIES_YN;
	
	public String getSERIES_YN() {
		try{
			
		}catch(Exception e){
			SERIES_YN = "";
		}
		return SERIES_YN;
	}

	public void setSERIES_YN(String cSERIES_YN) {
		try{
			this.SERIES_YN = cSERIES_YN;
		}catch(Exception e){
			this.SERIES_YN = "";
		}
		
	}

	
	@XStreamAlias("CHARGE_YN") 
	private String CHARGE_YN;
	
	public String getCHARGE_YN() {
		try{
			
		}catch(Exception e){
			CHARGE_YN = "";
		}
		return CHARGE_YN;
	}

	public void setCHARGE_YN(String cCHARGE_YN) {
		try{
			this.CHARGE_YN = cCHARGE_YN;
		}catch(Exception e){
			this.CHARGE_YN = "";
		}
		
	}
	
	@XStreamAlias("IMG_URL") 
	private String IMG_URL;
	
	public String getIMG_URL() {
		try{
			
		}catch(Exception e){
			IMG_URL = "";
		}
		return IMG_URL;
	}

	public void setIMG_URL(String cIMG_URL) {
		try{
			this.IMG_URL = cIMG_URL;
		}catch(Exception e){
			this.IMG_URL = "";
		}
		
	}
	
	@XStreamAlias("ACTOR") 
	private String ACTOR;
	
	public String getACTOR() {
		try{
			
		}catch(Exception e){
			ACTOR = "";
		}
		return ACTOR;
	}

	public void setACTOR(String cACTOR) {
		try{
			this.ACTOR = cACTOR;
		}catch(Exception e){
			this.ACTOR = "";
		}
		
	}
	
	@XStreamAlias("DIRECTOR") 
	private String DIRECTOR;
	
	public String getDIRECTOR() {
		try{
			
		}catch(Exception e){
			DIRECTOR = "";
		}
		return DIRECTOR;
	}

	public void setDIRECTOR(String cDIRECTOR) {
		try{
			this.DIRECTOR = cDIRECTOR;
		}catch(Exception e){
			this.DIRECTOR = "";
		}
		
	}
	
	
	@XStreamAlias("RATING") 
	private String RATING;
	
	public String getRATING() {
		try{
			
		}catch(Exception e){
			RATING = "";
		}
		return RATING;
	}

	public void setRATING(String cRATING) {
		try{
			this.RATING = cRATING;
		}catch(Exception e){
			this.RATING = "";
		}
		
	}
	
	@XStreamAlias("RUNTIME") 
	private String RUNTIME;
	
	public String getRUNTIME() {
		try{
			
		}catch(Exception e){
			RUNTIME = "";
		}
		return RUNTIME;
	}

	public void setRUNTIME(String cRUNTIME) {
		try{
			this.RUNTIME = cRUNTIME;
		}catch(Exception e){
			this.RUNTIME = "";
		}
		
	}
	
	@XStreamAlias("IMG_EXT") 
	private String IMG_EXT;
	
	public String getIMG_EXT() {
		try{
			
		}catch(Exception e){
			IMG_EXT = "";
		}
		return IMG_EXT;
	}

	public void setIMG_EXT(String cIMG_EXT) {
		try{
			this.IMG_EXT = cIMG_EXT;
		}catch(Exception e){
			this.IMG_EXT = "";
		}
		
	}
	
	
	@XStreamAlias("CONTENTS_TYPE") 
	private String CONTENTS_TYPE;
	
	public String getCONTENTS_TYPE() {
		try{
			
		}catch(Exception e){
			CONTENTS_TYPE = "";
		}
		return CONTENTS_TYPE;
	}

	public void setCONTENTS_TYPE(String sCONTENTS_TYPE) {
		try{
			this.CONTENTS_TYPE = sCONTENTS_TYPE;
		}catch(Exception e){
			this.CONTENTS_TYPE = "";
		}
		
	}
	
	@XStreamAlias("SEARCH_WORD") 
	private String SEARCH_WORD;
	
	public String getSEARCH_WORD() {
		try{
			
		}catch(Exception e){
			SEARCH_WORD = "";
		}
		return SEARCH_WORD;
	}

	public void setSEARCH_WORD(String sSEARCH_WORD) {
		try{
			this.SEARCH_WORD = sSEARCH_WORD;
		}catch(Exception e){
			this.SEARCH_WORD = "";
		}
		
	}
	
	
	@XStreamAlias("WORD_TYPE") 
	private String WORD_TYPE;
	
	public String getWORD_TYPE() {
		try{
			
		}catch(Exception e){
			WORD_TYPE = "";
		}
		return WORD_TYPE;
	}

	public void setWORD_TYPE(String sWORD_TYPE) {
		try{
			this.WORD_TYPE = sWORD_TYPE;
		}catch(Exception e){
			this.WORD_TYPE = "";
		}
		
	}
	
	@XStreamAlias("URL") 
	private String URL;
	
	public String getURL() {
		try{
			
		}catch(Exception e){
			URL = "";
		}
		return URL;
	}

	public void setURL(String sURL) {
		try{
			this.URL = sURL;
		}catch(Exception e){
			this.URL = "";
		}
		
	}
	
	
	@XStreamAlias("HDR_YN") 
	private String HDR_YN;
	
	public String getHDR_YN() {
		try{
			
		}catch(Exception e){
			HDR_YN = "";
		}
		return HDR_YN;
	}

	public void setHDR_YN(String sHDR_YN) {
		try{
			this.HDR_YN = sHDR_YN;
		}catch(Exception e){
			this.HDR_YN = "";
		}
		
	}
	
	@XStreamAlias("SMART_DVD_YN") 
	private String SMART_DVD_YN;
	
	public String getSMART_DVD_YN() {
		try{
			
		}catch(Exception e){
			SMART_DVD_YN = "";
		}
		return SMART_DVD_YN;
	}

	public void setSMART_DVD_YN(String sSMART_DVD_YN) {
		try{
			this.SMART_DVD_YN = sSMART_DVD_YN;
		}catch(Exception e){
			this.SMART_DVD_YN = "";
		}
		
	}
	
	
	
	@Override
	public String toString() {
		return ObjectUtils.toString(this);
	}

	public void log(PrintWriter writer) {
		
		//writer.println("\t--------------------");
		//writer.println("\tCONTENTS_TYPE=" + StringUtils.defaultString(CONTENTS_TYPE, ""));
		//writer.println("\tTITLE=" + StringUtils.defaultString(TITLE, ""));
	//	writer.println("\tWORD_TYPE=" + StringUtils.defaultString(WORD_TYPE, ""));
		//writer.println("\tSPELL_EXPAND=" + StringUtils.defaultString(SPELL_EXPAND, ""));
		//writer.println("\tURL=" + StringUtils.defaultString(URL, ""));
		//writer.println("\tACTOR=" + StringUtils.defaultString(ACTOR, ""));
		//writer.println("\tDIRECTOR=" + StringUtils.defaultString(DIRECTOR, ""));
		//writer.println("\tAUTO_ID=" + StringUtils.defaultString(AUTO_ID, ""));
		//writer.println("\tPARENT_ID=" + StringUtils.defaultString(PARENT_ID, ""));
		//writer.println("\tSEARCH_QUERY=" + StringUtils.defaultString(SEARCH_QUERY, ""));
		//writer.println("\tDATA_PATTERN=" + StringUtils.defaultString(DATA_PATTERN, ""));
		//writer.println("\tHDR_YN=" + StringUtils.defaultString(HDR_YN, ""));
		//writer.println("\tSMART_DVD_YN=" + StringUtils.defaultString(SMART_DVD_YN, ""));
		//writer.println("\tRATING=" + StringUtils.defaultString(RATING, ""));
		
	}

}
