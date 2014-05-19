package com.baidu.fis;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImgTag extends TagSupport implements DynamicAttributes {
	private static final Logger logger = LoggerFactory.getLogger(ImgTag.class);

	private String src;

	private Map<String, Object> dynamicAttributes = new LinkedHashMap<>();

	/**
	 *
	 */
	private static final long serialVersionUID = -8981028602817984681L;

	public int doStartTag() {
		JspWriter out = pageContext.getOut();
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		Resource resource = (Resource) request
				.getAttribute(Resource.CONTEXT_ATTR_NAME);
		String src = this.src;
		try {
			src = resource.require(this.src);
		} catch (Exception e) {
			logger.debug("require resouce error", e);
		}
		dynamicAttributes.put("src", src);
		String tagStr = Utils.buildTagString("img", dynamicAttributes);
		try {
			out.append(tagStr);
		} catch (IOException e) {
			logger.error("", e);
		}
		return SKIP_BODY;
	}

	@Override
	public void setDynamicAttribute(String uri, String name, Object value)
			throws JspException {
		logger.debug("DynamicAttributes : {} , {} , {}", uri, name, value);
		dynamicAttributes.put(name, value);
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}
}
