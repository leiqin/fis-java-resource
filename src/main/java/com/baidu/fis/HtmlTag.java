package com.baidu.fis;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlTag extends BodyTagSupport implements DynamicAttributes {
	private static final Logger logger =
		LoggerFactory.getLogger(HtmlTag.class);

	
	private String mapDir = "/";
	private Resource resource;
	private Map<String, Object> dynamicAttributes = new LinkedHashMap<>();

	/**
	 * 
	 */
	private static final long serialVersionUID = -612383582047754887L;
	
	public int doStartTag() throws JspException{
		JspWriter out = pageContext.getOut();
		try {
			String tagStr = Utils.buildTagString("html", dynamicAttributes);
			out.append(tagStr);
		} catch (IOException e) {
			logger.error("", e);
		}
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String path = request.getSession().getServletContext().getRealPath(mapDir);
		logger.debug("realMapDir : {}", path);
		resource = new Resource(path);
		request.setAttribute(Resource.CONTEXT_ATTR_NAME, resource);
		return EVAL_BODY_BUFFERED;
	}
	
	public int doEndTag(){
		BodyContent body = this.getBodyContent();
		String html = body.getString() + "</html>";
		html = resource.replace(html);
		JspWriter out = pageContext.getOut();
		try {
			out.write(html);
		} catch (IOException e) {
			logger.error("", e);
		}
		return EVAL_PAGE;
	}

	public String getMapDir() {
		return mapDir;
	}

	public void setMapDir(String mapDir) {
		this.mapDir = mapDir;
	}

	@Override
	public void setDynamicAttribute(String uri, String name, Object value)
			throws JspException {
		logger.debug("DynamicAttributes : {} , {} , {}", uri, name, value);
		dynamicAttributes.put(name, value);
	}

}
