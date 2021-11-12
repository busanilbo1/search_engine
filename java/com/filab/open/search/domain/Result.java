
package com.filab.open.search.domain;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.filab.open.search.util.ObjectUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;


@XStreamAlias("output")
public class Result {
	
	private int totalNum;

	
	
	// 통합 자동완성 추가  xml
	@XStreamImplicit(itemFieldName = "ACWORD_ITEM")
	private List<GetTotalAutoWords> ACWORD_ITEM = Collections.emptyList(); // add 2013.10.09
	


}
