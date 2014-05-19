package com.baidu.fis;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptsTag extends TagSupport {
	private static final Logger logger =
		LoggerFactory.getLogger(ScriptsTag.class);


	/**
	 * 
	 */
	private static final long serialVersionUID = -8981028602817984686L;
	
	public int doStartTag(){
		JspWriter out = pageContext.getOut();
		try {
			out.append(Resource.SCRIPT_PLACEHOLDER);
		} catch (IOException e) {
			logger.error("", e);
		}
		return SKIP_BODY;
	}
}
