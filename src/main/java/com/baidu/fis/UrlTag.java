package com.baidu.fis;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class UrlTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8694856279353734532L;
	
	private String id;
	
	public int doStartTag() {
		JspWriter out = pageContext.getOut();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		Resource resource = (Resource) request.getAttribute(Resource.CONTEXT_ATTR_NAME);
		try {
			String url = resource.require(this.id);
			out.append(url);
		} catch (Exception e) {
			try {
				out.append(this.id);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return SKIP_BODY;
	}

	public void setId(String id) {
		this.id = id;
	}

}
