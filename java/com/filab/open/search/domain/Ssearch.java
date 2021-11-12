
package com.filab.open.search.domain;

import java.io.PrintWriter;

import com.filab.open.search.util.ObjectUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
통합검색용 response 정보 처리
 */
@XStreamAlias("ssearch")
public class Ssearch {

	@XStreamAlias("domain")
	@XStreamAsAttribute
	private String domain;

	@XStreamAlias("section")
	@XStreamAsAttribute
	private String section;

	@XStreamAlias("ver")
	@XStreamAsAttribute
	private String ver;

	@XStreamAlias("output")
	private Result result;
	
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}
	


	@Override
	public String toString() {
		return ObjectUtils.toString(this);
	}

	
	
	public void log(PrintWriter writer) {
		writer.println("Response");
		

	}

}
