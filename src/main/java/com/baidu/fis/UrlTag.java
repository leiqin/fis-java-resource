package com.baidu.fis;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlTag extends TagSupport {
	private static final Logger logger =
		LoggerFactory.getLogger(UrlTag.class);


	/**
	 * 
	 */
	private static final long serialVersionUID = 8694856279353734531L;
	
	private String id;
	
	public int doStartTag() {
		logger.debug("url tag : {}", id);
		JspWriter out = pageContext.getOut();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		Resource resource = (Resource) request.getAttribute(Resource.CONTEXT_ATTR_NAME);
		try {
			String url = resource.justGetUrl(this.id);
			out.append(url);
		} catch (Exception e) {
			logger.warn("require resouce error", e);
			try {
				out.append(this.id);
			} catch (IOException ioError) {
				logger.error("", ioError);
			}
		}
		return SKIP_BODY;
	}

	public void setId(String id) {
		this.id = id;
	}

}
