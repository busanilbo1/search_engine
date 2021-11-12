
package com.filab.open.search.command;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public interface SearchRequest {

	public String toQueryString(String uriEncoding) throws UnsupportedEncodingException;

	public String getParameterString();
	
	public void log(PrintWriter writer);
}
