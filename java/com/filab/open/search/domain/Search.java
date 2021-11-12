
package com.filab.open.search.domain;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import com.filab.open.search.util.ObjectUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;


@XStreamAlias("search")
public class Search {
	
	//통합검색 영역 
	@XStreamAlias("code")
	@XStreamAsAttribute
	private String code;
	
	private int totalNum;

	private int itemNum;
	
	private int keywordNum;
	
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public int getItemNum() {
		return itemNum;
	}

	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}
	
	public int getKeywordNum() {
		return keywordNum;
	}

	public void setKeywordNum(int keywordNum) {
		this.keywordNum = keywordNum;
	}
	


	
	
	@Override
	public String toString() {
		return ObjectUtils.toString(this);
	}

	public void log(PrintWriter writer) {

	
		
	}
	

}
