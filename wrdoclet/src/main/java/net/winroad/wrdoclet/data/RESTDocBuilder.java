package net.winroad.wrdoclet.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.winroad.wrdoclet.taglets.WRTagTaglet;

import com.sun.javadoc.AnnotationDesc;
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

	@Override
	public void processOpenAPIClasses(ClassDoc[] classDocs,
			Configuration configuration) {
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
						requestMapping.setUrl(annotations[i].elementValues()[j]
								.value().toString().replace("\"", ""));
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
			apiParameter.setType(method.returnType().qualifiedTypeName());
			for (Tag tag : method.tags("return")) {
				apiParameter.setDescription(tag.text());
			}
			apiParameter.setFields(this.getFields(method.returnType(),
					ParameterType.Response));
			apiParameter.setHistory(this.getModificationHistory(method
					.returnType()));
		}
		return apiParameter;
	}

	@Override
	protected List<APIParameter> getInputParams(MethodDoc method) {
		List<APIParameter> paramList = new LinkedList<APIParameter>();
		Parameter reqBody = this.findRequestBody(method.parameters());
		if (reqBody != null) {
			APIParameter apiParameter = null;
			apiParameter = new APIParameter();
			apiParameter.setParameterOccurs(ParameterOccurs.REQUIRED);
			apiParameter.setType(reqBody.type().qualifiedTypeName());
			apiParameter.setName(reqBody.name());
			for (Tag tag : method.tags("param")) {
				if (reqBody.name().equals(((ParamTag) tag).parameterName())) {
					apiParameter.setDescription(((ParamTag) tag)
							.parameterComment());
				}
			}
			apiParameter.setFields(this.getFields(reqBody.type(),
					ParameterType.Request));
			apiParameter
					.setHistory(this.getModificationHistory(reqBody.type()));
			paramList.add(apiParameter);
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
	 * Find the Parameter which is annotated with @RequestBody.
	 */
	private Parameter findRequestBody(Parameter[] parameters) {
		// TODO: ONLY ONE PARAM WITHOUT @RequestBody
		for (int i = 0; i < parameters.length; i++) {
			AnnotationDesc[] annotations = parameters[i].annotations();
			for (int j = 0; j < annotations.length; j++) {
				if (annotations[j].annotationType().name()
						.equals("RequestBody")) {
					return parameters[i];
				}
			}
		}
		return null;
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

}
