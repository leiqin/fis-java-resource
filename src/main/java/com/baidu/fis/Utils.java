package com.baidu.fis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
	private static final Logger logger =
		LoggerFactory.getLogger(Utils.class);


	public static String buildTagString(String tagName, Map<String, Object> attributes) {
		if (attributes.isEmpty()) {
			return "<" + tagName + ">";
		} 

		StringBuffer buffer = new StringBuffer();
		buffer.append("<");
		buffer.append(tagName);
		buffer.append(" ");
		for (String name : attributes.keySet()) {
			Object value = attributes.get(name);
			String strValue = value.toString().replaceAll("\"", "\\\"");
			buffer.append(name);
			buffer.append("=\"");
			buffer.append(strValue);
			buffer.append("\" ");
		}
		buffer.append(">");
		logger.debug("tag string : {}", buffer);
		return buffer.toString();
	}

}
