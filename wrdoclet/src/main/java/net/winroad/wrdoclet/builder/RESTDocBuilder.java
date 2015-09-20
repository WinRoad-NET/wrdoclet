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

			if (this.isController(classDocs[i])) {
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
		AnnotationDesc[] annotations = methodDoc.annotations();
		RequestMapping mapping = this.parseRequestMapping(annotations);
		if (baseMapping == null) {
			return mapping;
		} else if (mapping == null) {
			return baseMapping;
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
			return mapping;
		}
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
			apiParameter.setType(this.getTypeName(method.returnType()));
			for (Tag tag : method.tags("return")) {
				apiParameter.setDescription(tag.text());
			}
			apiParameter.setFields(this.getFields(method.returnType(),
					ParameterType.Response));
			apiParameter.setHistory(this.getModificationHistory(method
					.returnType()));
		} else {
			apiParameter = this.parseCustomizedReturn(method);
		}
		return apiParameter;
	}

	//TODO: handle the @RequestHeader、@CookieValue, @RequestParam, @SessionAttributes, @ModelAttribute
	@Override
	protected List<APIParameter> getInputParams(MethodDoc method) {
		List<APIParameter> paramList = new LinkedList<APIParameter>();
		paramList.addAll(parseCustomizedParameters(method));
		Parameter[] parameters = method.parameters();
		for (int i = 0; i < parameters.length; i++) {
			AnnotationDesc[] annotations = parameters[i].annotations();
			for (int j = 0; j < annotations.length; j++) {
				APIParameter apiParameter = new APIParameter();
				apiParameter.setParameterOccurs(ParameterOccurs.REQUIRED);
				if ("org.springframework.web.bind.annotation.RequestBody"
						.equals(annotations[j].annotationType().qualifiedName())
						&& annotations[j].elementValues() != null
						&& annotations[j].elementValues().length != 0) {
					for (ElementValuePair pair : annotations[j].elementValues()) {
						if (pair.element().name().equals("required")) {
							if (annotations[j].elementValues()[0].value()
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
				apiParameter.setType(this.getTypeName(parameters[i].type()));
				apiParameter.setName(parameters[i].name());
				if ("org.springframework.web.bind.annotation.PathVariable"
						.equals(annotations[j].annotationType().qualifiedName())) {
					for (ElementValuePair pair : annotations[j].elementValues()) {
						if (pair.element().name().equals("value")) {
							if (annotations[j].elementValues()[0].value() != null) {
								apiParameter.setName(annotations[j]
										.elementValues()[0].value().toString()
										.replace("\"", ""));
							}
						}
					}
				}
				String desc = "@" + annotations[j].annotationType().name();
				for (Tag tag : method.tags("param")) {
					if (parameters[i].name().equals(
							((ParamTag) tag).parameterName())) {
						desc += "\n" + ((ParamTag) tag).parameterComment();
					}
				}
				apiParameter.setDescription(desc);
				apiParameter.setFields(this.getFields(parameters[i].type(),
						ParameterType.Request));
				apiParameter.setHistory(this
						.getModificationHistory(parameters[i].type()));
				paramList.add(apiParameter);
			}
		}

		return paramList;
	}

	/*
	 * Here we only treat class which has spring "@Controller" annotation as a
	 * Controller, although it may not be enough.
	 */
	private boolean isController(ClassDoc classDoc) {
		return this.isClassDocAnnotatedWith(classDoc, "Controller");
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
	 * The method has spring "@RequestMapping".
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
		return isActionMethod;
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
	protected Boolean isAPIAuthNeeded(String url) {
		if (url != null && this.excludedUrls != null
				&& this.excludedUrls.size() != 0) {
			for (String excludedUrl : this.excludedUrls) {
				if (matcher.match(excludedUrl, url)) {
					return false;
				}
			}
			return true;
		}
		return null;
	}

}
