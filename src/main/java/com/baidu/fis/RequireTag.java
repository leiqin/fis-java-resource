package com.baidu.fis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequireTag extends TagSupport {
	private static final Logger logger =
		LoggerFactory.getLogger(RequireTag.class);


	/**
	 * 
	 */
	private static final long serialVersionUID = 8694856279353734532L;
	
	private String id;
	
	public int doStartTag() {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		Resource resource = (Resource) request.getAttribute(Resource.CONTEXT_ATTR_NAME);
		try {
			resource.require(this.id);
		} catch (Exception e) {
			logger.debug("require resouce error", e);
		}
		return SKIP_BODY;
	}

	public void setId(String id) {
		this.id = id;
	}

}
