package com.baidu.fis;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StylesTag extends TagSupport {
	private static final Logger logger =
		LoggerFactory.getLogger(StylesTag.class);


	/**
	 * 
	 */
	private static final long serialVersionUID = 6012385708226095164L;
	
	public int doStartTag(){
		JspWriter out = pageContext.getOut();
		try {
			out.append(Resource.STYLE_PLACEHOLDER);
		} catch (IOException e) {
			logger.error("", e);
		}
		return SKIP_BODY;
	}
}
