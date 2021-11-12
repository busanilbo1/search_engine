
package com.filab.open.search.xstream.io.xml;

import java.io.Writer;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;


public class CustomizedPrettyPrintWriter extends PrettyPrintWriter {

	private static final char[] AMP = "&amp;".toCharArray();

	private static final char[] LT = "<".toCharArray();

	private static final char[] GT = ">".toCharArray();

	private static final char[] SLASH_R = " ".toCharArray();

	private static final char[] QUOT = "&quot;".toCharArray();

	private static final char[] APOS = "&apos;".toCharArray();

	public CustomizedPrettyPrintWriter(Writer writer) {
		super(writer);
	}

	public CustomizedPrettyPrintWriter(Writer writer, XmlFriendlyReplacer replacer) {
		super(writer, replacer);
	}

	protected void writeText(QuickWriter writer, String text) {

		if (text.trim().length() < 1) {
			super.writeText(writer, text);
			return;
		}

		String CDATAPrefix = "<![CDATA[";
		String CDATASuffix = "]]>";

		// using CDATA and numerical text
		// if (!text.startsWith(CDATAPrefix) && !Pattern.matches("[^[0-9]]+", text)) {
		// using CDATA and text including escape letter
		//if (!text.startsWith(CDATAPrefix) && Pattern.compile("[\\&\\<\\>\"\'\r]").matcher(text).find()) {
		if (!text.startsWith(CDATAPrefix) && Pattern.compile("[\\,\\?\\!\\@\\(\\)\\#\\:\\;\\/\\&\\<\\>\"\'\r]").matcher(text).find()) {
			text = CDATAPrefix + text + CDATASuffix;
		}

		int length = text.length();
		if (!text.startsWith(CDATAPrefix)) {
			for (int i = 0; i < length; i++) {
				char c = text.charAt(i);
				switch (c) {
					case '&':
						writer.write(AMP);
						break;
					case '<':
						writer.write(LT);
						break;
					case '>':
						writer.write(GT);
						break;
					case '"':
						writer.write(QUOT);
						break;
					case '\'':
						writer.write(APOS);
						break;
					case '\r':
						writer.write(SLASH_R);
						break;
					default:
						writer.write(c);
				}
			}
		}
		else {
			for (int i = 0; i < length; i++) {
				char c = text.charAt(i);
				writer.write(c);
			}
		}
	}
}