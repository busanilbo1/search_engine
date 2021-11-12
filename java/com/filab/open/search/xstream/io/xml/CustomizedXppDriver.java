
package com.filab.open.search.xstream.io.xml;

import java.io.IOException;
import java.io.Writer;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class CustomizedXppDriver extends XppDriver {

	private String version;

	private String encoding;

	public CustomizedXppDriver(XmlFriendlyReplacer replacer) {
		super(replacer);
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public HierarchicalStreamWriter createWriter(Writer out) {

		try {
			out.write("<?xml version=\"" + version + "\" encoding=\"" + encoding + "\"?>\n");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		// 특수문자 포함시 CDATA[]적용. 클래스 PrettyPrintWriter --> CustomizedPrettyPrintWriter 사용
		//return new PrettyPrintWriter(out, xmlFriendlyReplacer());
		// using CustomizedPrettyPrintWriter for wrap xxx with <![CDATA[ xxx ]]>.
		// ex) <![CDATA[ 문학의 이해<일본의 산문문학>]]>
		 return new CustomizedPrettyPrintWriter(out, xmlFriendlyReplacer());
	}

}
