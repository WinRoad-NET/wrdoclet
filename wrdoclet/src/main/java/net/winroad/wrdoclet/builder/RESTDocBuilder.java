package net.winroad.wrdoclet.builder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.winroad.wrdoclet.ConfigurationImpl;
import net.winroad.wrdoclet.data.APIParameter;
import net.winroad.wrdoclet.data.ParameterOccurs;
import net.winroad.wrdoclet.data.ParameterType;
import net.winroad.wrdoclet.data.RequestMapping;
import net.winroad.wrdoclet.data.WRDoc;
import net.winroad.wrdoclet.taglets.WRAPITaglet;
import net.winroad.wrdoclet.taglets.WROccursTaglet;
import net.winroad.wrdoclet.taglets.WRRefReqTaglet;
import net.winroad.wrdoclet.taglets.WRRefRespTaglet;
import net.winroad.wrdoclet.taglets.WRTagTaglet;
import net.winroad.wrdoclet.utils.UniversalNamespaceCache;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.Util;

/**
 * @author AdamsLee NOTE: WRDoc cannot cover API which returning objects whose
 *         type is unknown on API definition (known until runtime). e.g.
 * @RequestMapping(value = "/update", method = RequestMethod.POST) public @ResponseBody
 *                       Object updateStudent(Student student) { return student;
 *                       }
 */
public class RESTDocBuilder extends AbstractDocBuilder {

	public RESTDocBuilder(WRDoc wrDoc) {
		super(wrDoc);
	}

	private PathMatcher matcher = new AntPathMatcher();
	private List<String> excludedUrls;

	protected List<String> getExcludedUrls(Configuration configuration) {
		List<String> excludedUrls = new LinkedList<String>();
		String contextConfigPath = ((ConfigurationImpl) configuration).springcontextconfigpath;
		if (!StringUtils.isBlank(contextConfigPath)) {
			String excludedUrlsXpath = ((ConfigurationImpl) configuration).excludedurlsxpath;
			try {
				Document contextConfig = readXMLConfig(contextConfigPath);
				XPath xPath = XPathFactory.newInstance().newXPath();
				xPath.setNamespaceContext(new UniversalNamespaceCache(
						contextConfig, false));
				if (StringUtils.isBlank(excludedUrlsXpath)) {
					NodeList serviceNodes = (NodeList) xPath
							.evaluate(
									"//:beans/mvc:interceptors/mvc:interceptor/mvc:exclude-mapping",
									contextConfig, XPathConstants.NODESET);
					for (int i = 0; i < serviceNodes.getLength(); i++) {
						Node node = serviceNodes.item(i);
						String path = getAttributeValue(node, "path");
						if (path != null) {
							excludedUrls.add(path);
						}
					}
				} else {
					NodeList serviceNodes = (NodeList) xPath.evaluate(
							excludedUrlsXpath, contextConfig,
							XPathConstants.NODESET);
					for (int i = 0; i < serviceNodes.getLength(); i++) {
						Node node = serviceNodes.item(i);
						excludedUrls.add(node.getTextContent());
					}
				}
			} catch (Exception e) {
				this.logger.error(e);
			}
		}
		this.logger.debug("excludedUrls: ");
		for (String s : excludedUrls) {
			this.logger.debug(s);
		}
		return excludedUrls;
	}

	@Override
	public void processOpenAPIClasses(ClassDoc[] classDocs,
			Configuration configuration) {
		this.excludedUrls = this.getExcludedUrls(configuration);
		for (int i = 0; i < classDocs.length; i++) {
			if (configuration.nodeprecated
					&& (Util.isDeprecated(classDocs[i]) || Util
							.isDeprecated(classDocs[i].containingPackage()))) {
				continue;
			}

			if (this.isController(classDocs[i])
					|| this.isAPIClass(classDocs[i])) {
				this.processControllerClass(classDocs[i], configuration);
				MethodDoc[] methods = classDocs[i].methods();
				for (int l = 0; l < methods.length; l++) {
					this.processOpenAPIMethod(methods[l], configuration);
				}
			}
		}
	}

	/*
	 * Parse the @RequestMapping from the MVC action method.
	 */
	@Override
	protected RequestMapping parseRequestMapping(MethodDoc methodDoc) {
		ClassDoc controllerClass = methodDoc.containingClass();
		AnnotationDesc[] baseAnnotations = controllerClass.annotations();
		RequestMapping baseMapping = this.parseRequestMapping(baseAnnotations);
		if (baseMapping == null) {
			Tag[] tags = controllerClass.tags(WRAPITaglet.NAME);
			if (tags.length > 0) {
				baseMapping = this.parseRequestMapping(tags[0]);
			}
		}
		AnnotationDesc[] annotations = methodDoc.annotations();
		RequestMapping mapping = this.parseRequestMapping(annotations);
		if (mapping == null) {
			Tag[] tags = methodDoc.tags(WRAPITaglet.NAME);
			if (tags.length > 0) {
				mapping = this.parseRequestMapping(tags[0]);
			}
		}
		RequestMapping result;
		if (baseMapping == null) {
			result = mapping;
		} else if (mapping == null) {
			result = baseMapping;
		} else {
			mapping.setUrl(net.winroad.wrdoclet.utils.Util.urlConcat(
					baseMapping.getUrl(), mapping.getUrl()));
			if (baseMapping.getMethodType() != null) {
				if (mapping.getMethodType() != null) {
					mapping.setMethodType(baseMapping.getMethodType() + ","
							+ mapping.getMethodType());
				} else {
					mapping.setMethodType(baseMapping.getMethodType());
				}
			}
			result = mapping;
		}
		if (result != null) {
			result.setTooltip(methodDoc.containingClass().simpleTypeName());
			result.setContainerName(methodDoc.containingClass()
					.simpleTypeName());
		}
		return result;
	}

	/*
	 * Parse the RequestMapping from the annotations.
	 */
	private RequestMapping parseRequestMapping(AnnotationDesc[] annotations) {
		RequestMapping requestMapping = null;
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].annotationType().name().equals("RequestMapping")) {
				requestMapping = new RequestMapping();
				for (int j = 0; j < annotations[i].elementValues().length; j++) {
					if ("value".equals(annotations[i].elementValues()[j]
							.element().name())) {
						String url = annotations[i].elementValues()[j].value()
								.toString().replace("\"", "");
						requestMapping.setUrl(url);
					} else if ("method"
							.equals(annotations[i].elementValues()[j].element()
									.name())) {
						requestMapping
								.setMethodType(this
										.convertMethodType(annotations[i]
												.elementValues()[j].value()
												.toString()));
					}
				}
				break;
			}
		}
		return requestMapping;
	}

	private RequestMapping parseRequestMapping(Tag tag) {
		RequestMapping requestMapping = null;
		if (!StringUtils.isEmpty(tag.text())) {
			requestMapping = new RequestMapping();
			int index = tag.text().indexOf(" ");
			if (index > 0) {
				String methodType = tag.text().substring(0, index);
				String url = tag.text().substring(index + 1);
				requestMapping.setMethodType(methodType);
				requestMapping.setUrl(url);
			} else {
				requestMapping.setUrl(tag.text());
			}
		}
		return requestMapping;
	}

	/*
	 * Simplify the methodType of @RequestMapping to display.
	 */
	private String convertMethodType(String methodType) {
		return methodType
				.replace(
						"org.springframework.web.bind.annotation.RequestMethod.",
						"").replace("{", "").replace("}", "");
	}

	@Override
	protected APIParameter getOutputParam(MethodDoc method) {
		APIParameter apiParameter = null;
		if (this.isAnnotatedResponseBody(method)) {
			apiParameter = new APIParameter();
			apiParameter.setParameterOccurs(ParameterOccurs.REQUIRED);
			apiParameter.setType(this.getTypeName(method.returnType(), false));
			for (Tag tag : method.tags("return")) {
				apiParameter.setDescription(tag.text());
			}
			HashSet<String> processingClasses = new HashSet<String>();
			apiParameter.setFields(this.getFields(method.returnType(),
					ParameterType.Response, processingClasses));
			apiParameter.setHistory(this.getModificationHistory(method
					.returnType()));
		} else {
			apiParameter = this.parseCustomizedReturn(method);
		}

		apiParameter = handleRefResp(method, apiParameter);
		return apiParameter;
	}

	private APIParameter handleRefResp(MethodDoc method,
			APIParameter apiParameter) {
		if (apiParameter == null) {
			Tag[] tags = method.tags(WRRefRespTaglet.NAME);
			if (tags.length > 0) {
				apiParameter = new APIParameter();
				apiParameter.setType(tags[0].text());
				apiParameter.setParameterOccurs(ParameterOccurs.REQUIRED);
				HashSet<String> processingClasses = new HashSet<String>();
				ClassDoc c = this.wrDoc.getConfiguration().root
						.classNamed(apiParameter.getType());
				if (c != null) {
					apiParameter.setFields(this.getFields(c,
							ParameterType.Response, processingClasses));
				}
			}
		}
		return apiParameter;
	}

	// TODO: handle the @RequestHeader、@CookieValue, @RequestParam,
	// @SessionAttributes, @ModelAttribute
	@Override
	protected List<APIParameter> getInputParams(MethodDoc method) {
		List<APIParameter> paramList = new LinkedList<APIParameter>();
		paramList.addAll(parseCustomizedParameters(method));
		Parameter[] parameters = method.parameters();
		for (int i = 0; i < parameters.length; i++) {
			AnnotationDesc[] annotations = parameters[i].annotations();
			APIParameter apiParameter = new APIParameter();
			if (annotations.length > 0) {
				apiParameter.setParameterOccurs(ParameterOccurs.REQUIRED);
				apiParameter.setType(this.getTypeName(parameters[i].type(), false));
				apiParameter.setName(parameters[i].name());
				HashSet<String> processingClasses = new HashSet<String>();
				apiParameter.setFields(this.getFields(parameters[i].type(),
						ParameterType.Request, processingClasses));
				apiParameter.setHistory(this
						.getModificationHistory(parameters[i].type()));
				StringBuffer buf = new StringBuffer();
				for (int j = 0; j < annotations.length; j++) {
					processAnnotations(annotations[j], apiParameter);
					buf.append("@");
					buf.append(annotations[j].annotationType().name());
					buf.append(" ");
				}
				for (Tag tag : method.tags("param")) {
					if (parameters[i].name().equals(
							((ParamTag) tag).parameterName())) {
						buf.append("\n");
						buf.append(((ParamTag) tag).parameterComment());
					}
				}
				apiParameter.setDescription(buf.toString());
				paramList.add(apiParameter);
			}
		}

		handleRefReq(method, paramList);
		return paramList;
	}

	private void processAnnotations(AnnotationDesc annotation,
			APIParameter apiParameter) {
		if ("org.springframework.web.bind.annotation.RequestBody"
				.equals(annotation.annotationType().qualifiedName())
				&& annotation.elementValues() != null
				&& annotation.elementValues().length != 0) {
			for (ElementValuePair pair : annotation.elementValues()) {
				if (pair.element().name().equals("required")) {
					if (annotation.elementValues()[0].value().value()
							.equals(true)) {
						apiParameter
								.setParameterOccurs(ParameterOccurs.REQUIRED);
					} else {
						apiParameter
								.setParameterOccurs(ParameterOccurs.OPTIONAL);
					}
				}
			}
		}
		if ("org.springframework.web.bind.annotation.PathVariable"
				.equals(annotation.annotationType().qualifiedName())) {
			for (ElementValuePair pair : annotation.elementValues()) {
				if (pair.element().name().equals("value")) {
					if (annotation.elementValues()[0].value() != null) {
						apiParameter.setName(annotation.elementValues()[0]
								.value().toString().replace("\"", ""));
					}
				}
			}
		}
		if ("org.springframework.web.bind.annotation.RequestParam"
				.equals(annotation.annotationType().qualifiedName())) {
			for (ElementValuePair pair : annotation.elementValues()) {
				if (pair.element().name().equals("value")) {
					if (annotation.elementValues()[0].value() != null) {
						apiParameter.setName(annotation.elementValues()[0]
								.value().toString().replace("\"", ""));
					}
				}
				if (pair.element().name().equals("required")) {
					if (annotation.elementValues()[0].value().value()
							.equals(true)) {
						apiParameter
								.setParameterOccurs(ParameterOccurs.REQUIRED);
					} else {
						apiParameter
								.setParameterOccurs(ParameterOccurs.OPTIONAL);
					}
				}
			}
		}
	}

	private void handleRefReq(MethodDoc method, List<APIParameter> paramList) {
		Tag[] tags = method.tags(WRRefReqTaglet.NAME);
		for (int i = 0; i < tags.length; i++) {
			APIParameter apiParameter = new APIParameter();
			String[] strArr = tags[i].text().split(" ");
			for (int j = 0; j < strArr.length; j++) {
				switch (j) {
				case 0:
					apiParameter.setName(strArr[j]);
					break;
				case 1:
					apiParameter.setType(strArr[j]);
					break;
				case 2:
					apiParameter.setDescription(strArr[j]);
					break;
				case 3:
					if (StringUtils.equalsIgnoreCase(strArr[j],
							WROccursTaglet.REQUIRED)) {
						apiParameter
								.setParameterOccurs(ParameterOccurs.REQUIRED);
					} else if (StringUtils.equalsIgnoreCase(strArr[j],
							WROccursTaglet.OPTIONAL)) {
						apiParameter
								.setParameterOccurs(ParameterOccurs.OPTIONAL);
					}
					break;
				default:
					logger.warn("Unexpected tag:" + tags[i].text());
				}
			}
			HashSet<String> processingClasses = new HashSet<String>();
			ClassDoc c = this.wrDoc.getConfiguration().root
					.classNamed(apiParameter.getType());
			if (c != null) {
				apiParameter.setFields(this.getFields(c, ParameterType.Request,
						processingClasses));
			}
			paramList.add(apiParameter);
		}
	}

	/*
	 * Here we only treat class which has spring "@Controller" annotation as a
	 * Controller, although it may not be enough.
	 */
	private boolean isController(ClassDoc classDoc) {
		return this.isClassDocAnnotatedWith(classDoc, "Controller");
	}

	private boolean isAPIClass(ClassDoc classDoc) {
		Tag[] t = classDoc.tags(WRAPITaglet.NAME);
		return t.length > 0;
	}

	/*
	 * Process the tag on the Controller.
	 */
	private void processControllerClass(ClassDoc controller,
			Configuration configuration) {
		Tag[] controllerTagArray = controller.tags(WRTagTaglet.NAME);
		for (int i = 0; i < controllerTagArray.length; i++) {
			Set<String> controllerTags = WRTagTaglet
					.getTagSet(controllerTagArray[i].text());
			for (Iterator<String> iter = controllerTags.iterator(); iter
					.hasNext();) {
				String tag = iter.next();
				if (!this.taggedOpenAPIMethods.containsKey(tag)) {
					this.taggedOpenAPIMethods
							.put(tag, new HashSet<MethodDoc>());
				}
				// all action method of this controller should be processed
				// later.
				for (int j = 0; j < controller.methods().length; j++) {
					if (configuration.nodeprecated
							&& Util.isDeprecated(controller.methods()[j])) {
						continue;
					}
					if (isOpenAPIMethod(controller.methods()[j])) {
						this.taggedOpenAPIMethods.get(tag).add(
								controller.methods()[j]);
					}
				}
			}

			this.wrDoc.getWRTags().addAll(controllerTags);
		}
	}

	/*
	 * The method has spring "@RequestMapping" or tagged as "api".
	 */
	protected boolean isOpenAPIMethod(MethodDoc methodDoc) {
		AnnotationDesc[] annotations = methodDoc.annotations();
		boolean isActionMethod = false;
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].annotationType().name().equals("RequestMapping")) {
				isActionMethod = true;
				break;
			}
		}
		Tag[] t = methodDoc.tags(WRAPITaglet.NAME);
		return isActionMethod || t.length > 0;
	}

	/*
	 * Whether the method is annotated with @ResponseBody.
	 */
	private boolean isAnnotatedResponseBody(MethodDoc method) {
		AnnotationDesc[] annotations = method.annotations();
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].annotationType().name().equals("ResponseBody")) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected int isAPIAuthNeeded(String url) {
		if (url != null && this.excludedUrls != null
				&& this.excludedUrls.size() != 0) {
			if (url.startsWith("{") && url.endsWith("}")) {
				url = StringUtils.substring(url, 1, url.length() - 1);
			}
			String[] urls = url.split(",");
			for (String u : urls) {
				for (String excludedUrl : this.excludedUrls) {
					if (matcher.match(excludedUrl, u)) {
						return 0;
					}
				}
				return 1;
			}
		}
		return -1;
	}

}
