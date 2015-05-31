package net.winroad.wrdoclet.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.winroad.wrdoclet.taglets.WRMemoTaglet;
import net.winroad.wrdoclet.taglets.WROccursTaglet;
import net.winroad.wrdoclet.taglets.WRReturnCodeTaglet;
import net.winroad.wrdoclet.taglets.WRTagTaglet;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.Util;

/**
 * @author AdamsLee NOTE: WRDoc cannot cover API which returning objects whose
 *         type is unknown on API definition (known until runtime). e.g.
 * @RequestMapping(value = "/update", method = RequestMethod.POST) public @ResponseBody
 *                       Object updateStudent(Student student) { return student;
 *                       }
 */
public class WRDoc {
	private Map<String, List<OpenAPI>> taggedOpenAPIs = new HashMap<String, List<OpenAPI>>();

	// The collection of tag name in this Doc.
	private Set<String> wrTags = new HashSet<String>();

	/*
	 * The collection of MethodDoc to build Doc. Map key: tag name. Map value:
	 * collection of MethodDoc
	 */
	private Map<String, Set<MethodDoc>> taggedControllerMethods = new HashMap<String, Set<MethodDoc>>();

	private Map<String, Set<MethodDoc>> taggedServiceMethods = new HashMap<String, Set<MethodDoc>>();

	private Map<String, ClassDoc> nonControllerclassDocs = new HashMap<String, ClassDoc>();

	public Set<String> getWRTags() {
		return this.wrTags;
	}

	public Map<String, List<OpenAPI>> getTaggedOpenAPIs() {
		return taggedOpenAPIs;
	}

	public WRDoc(Configuration configuration) {
		this.buildWRDoc(configuration.root.classes(), configuration);
	}

	private void buildWRDoc(ClassDoc[] classes, Configuration configuration) {
		for (int i = 0; i < classes.length; i++) {
			if (configuration.nodeprecated
					&& (Util.isDeprecated(classes[i]) || Util
							.isDeprecated(classes[i].containingPackage()))) {
				continue;
			}

			if (this.isController(classes[i])) {
				this.processControllerClass(classes[i], configuration);
				MethodDoc[] methods = classes[i].methods();
				for (int l = 0; l < methods.length; l++) {
					this.processControllerMethod(methods[l], configuration);
				}
			} else if (this.isServiceInterface(classes[i])) {
				this.processServiceClass(classes[i], configuration);
				MethodDoc[] methods = classes[i].methods();
				for (int l = 0; l < methods.length; l++) {
					this.processServiceMethod(methods[l], configuration);
				}
			} else {
				this.nonControllerclassDocs.put(classes[i].qualifiedTypeName(),
						classes[i]);
				continue;
			}
		}

		this.buildOpenAPIs(configuration);
	}

	private void buildOpenAPIs(Configuration configuration) {
		Set<Entry<String, Set<MethodDoc>>> methods = this.taggedControllerMethods
				.entrySet();
		this.buildOpenAPIs(methods, configuration, APIType.HTTPRestAPI);
		methods = this.taggedServiceMethods.entrySet();
		this.buildOpenAPIs(methods, configuration, APIType.SOAPServiceAPI);
	}

	private void buildOpenAPIs(Set<Entry<String, Set<MethodDoc>>> methods,
			Configuration configuration, APIType apiMethodType) {
		for (Iterator<Entry<String, Set<MethodDoc>>> tagMthIter = methods
				.iterator(); tagMthIter.hasNext();) {
			Entry<String, Set<MethodDoc>> kv = tagMthIter.next();
			String tagName = kv.getKey();
			if (!this.taggedOpenAPIs.containsKey(tagName)) {
				this.taggedOpenAPIs.put(tagName, new LinkedList<OpenAPI>());
			}
			Set<MethodDoc> methodDocSet = kv.getValue();
			for (Iterator<MethodDoc> mthIter = methodDocSet.iterator(); mthIter
					.hasNext();) {
				MethodDoc mthDoc = mthIter.next();
				OpenAPI openAPI = this.buildOpenAPI(mthDoc, configuration,
						apiMethodType);
				this.taggedOpenAPIs.get(tagName).add(openAPI);
			}
		}
	}

	private OpenAPI buildOpenAPI(MethodDoc methodDoc,
			Configuration configuration, APIType apiMethodType) {
		OpenAPI openAPI = new OpenAPI();
		openAPI.setDescription(methodDoc.commentText());
		openAPI.setModificationHistory(this.getModificationHistory(methodDoc));
		switch (apiMethodType) {
		case HTTPRestAPI:
			openAPI.setRequestMapping(this
					.parseRESTAPIRequestMapping(methodDoc));
			openAPI.setInParameter(this.getRequestBody(methodDoc));
			openAPI.setOutParameter(this.getReponseBody(methodDoc));
			break;
		case SOAPServiceAPI:
			openAPI.setRequestMapping(this
					.parseSOAPAPIRequestMapping(methodDoc));
			openAPI.setInParameter(this.getSOAPAPIInputParams(methodDoc));
			openAPI.setOutParameter(this.getSOAPAPIOutputParams(methodDoc));
			break;
		}
		openAPI.setReturnCode(this.getReturnCode(methodDoc));
		return openAPI;
	}

	/*
	 * Process the tag on the Service.
	 */
	private void processServiceClass(ClassDoc service,
			Configuration configuration) {
		Tag[] serviceTagArray = service.tags(WRTagTaglet.NAME);
		for (int i = 0; i < serviceTagArray.length; i++) {
			Set<String> serviceTags = WRTagTaglet.getTagSet(serviceTagArray[i]
					.text());
			for (Iterator<String> iter = serviceTags.iterator(); iter.hasNext();) {
				String tag = iter.next();
				if (!this.taggedServiceMethods.containsKey(tag)) {
					this.taggedServiceMethods
							.put(tag, new HashSet<MethodDoc>());
				}
				// all method of this service should be processed later.
				for (int j = 0; j < service.methods().length; j++) {
					this.taggedServiceMethods.get(tag)
							.add(service.methods()[j]);
				}
			}

			wrTags.addAll(serviceTags);
		}
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
				if (!this.taggedControllerMethods.containsKey(tag)) {
					this.taggedControllerMethods.put(tag,
							new HashSet<MethodDoc>());
				}
				// all action method of this controller should be processed
				// later.
				for (int j = 0; j < controller.methods().length; j++) {
					if (isActionMethod(controller.methods()[j])) {
						this.taggedControllerMethods.get(tag).add(
								controller.methods()[j]);
					}
				}
			}

			wrTags.addAll(controllerTags);
		}
	}

	private void processServiceMethod(MethodDoc method,
			Configuration configuration) {
		if (configuration.nodeprecated && Util.isDeprecated(method)) {
			return;
		}

		Tag[] methodTagArray = method.tags(WRTagTaglet.NAME);
		if (methodTagArray.length == 0) {
			String tag = WRTagTaglet.DEFAULT_TAG_NAME;
			wrTags.add(tag);
			if (!this.taggedServiceMethods.containsKey(tag)) {
				this.taggedServiceMethods.put(tag, new HashSet<MethodDoc>());
			}
			this.taggedServiceMethods.get(tag).add(method);
		} else {
			for (int i = 0; i < methodTagArray.length; i++) {
				Set<String> methodTags = WRTagTaglet
						.getTagSet(methodTagArray[i].text());
				wrTags.addAll(methodTags);
				for (Iterator<String> iter = methodTags.iterator(); iter
						.hasNext();) {
					String tag = iter.next();
					if (!this.taggedServiceMethods.containsKey(tag)) {
						this.taggedServiceMethods.put(tag,
								new HashSet<MethodDoc>());
					}
					this.taggedServiceMethods.get(tag).add(method);
				}
			}
		}
	}

	/*
	 * Process the tag on the action method.
	 */
	private void processControllerMethod(MethodDoc method,
			Configuration configuration) {
		if (configuration.nodeprecated && Util.isDeprecated(method)) {
			return;
		}

		if (!isActionMethod(method)) {
			return;
		}

		Tag[] methodTagArray = method.tags(WRTagTaglet.NAME);
		if (methodTagArray.length == 0) {
			String tag = WRTagTaglet.DEFAULT_TAG_NAME;
			wrTags.add(tag);
			if (!this.taggedControllerMethods.containsKey(tag)) {
				this.taggedControllerMethods.put(tag, new HashSet<MethodDoc>());
			}
			this.taggedControllerMethods.get(tag).add(method);
		} else {
			for (int i = 0; i < methodTagArray.length; i++) {
				Set<String> methodTags = WRTagTaglet
						.getTagSet(methodTagArray[i].text());
				wrTags.addAll(methodTags);
				for (Iterator<String> iter = methodTags.iterator(); iter
						.hasNext();) {
					String tag = iter.next();
					if (!this.taggedControllerMethods.containsKey(tag)) {
						this.taggedControllerMethods.put(tag,
								new HashSet<MethodDoc>());
					}
					this.taggedControllerMethods.get(tag).add(method);
				}
			}
		}
	}

	private boolean isServiceInterface(ClassDoc classDoc) {
		return classDoc.isInterface()
				&& this.isClassDocAnnotatedWith(classDoc, "WebService");
	}

	/*
	 * Here we only treat class which has spring "@Controller" annotation as a
	 * Controller, although it may not be enough.
	 */
	private boolean isController(ClassDoc classDoc) {
		return this.isClassDocAnnotatedWith(classDoc, "Controller");
	}

	private boolean isClassDocAnnotatedWith(ClassDoc classDoc, String annotation) {
		AnnotationDesc[] annotations = classDoc.annotations();
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].annotationType().name().equals(annotation)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * The method has spring "@RequestMapping".
	 */
	private boolean isActionMethod(MethodDoc method) {
		AnnotationDesc[] annotations = method.annotations();
		boolean isActionMethod = false;
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].annotationType().name().equals("RequestMapping")) {
				isActionMethod = true;
				break;
			}
		}
		return isActionMethod;
	}

	private String getReturnCode(MethodDoc methodDoc) {
		Tag[] tags = methodDoc.tags(WRReturnCodeTaglet.NAME);
		return WRReturnCodeTaglet.concat(tags);
	}

	/*
	 * get the modification history of the class.
	 */
	private ModificationHistory getModificationHistory(Type type) {
		ModificationHistory history = new ModificationHistory();
		if (this.nonControllerclassDocs.containsKey(type.qualifiedTypeName())) {
			ClassDoc classDoc = this.nonControllerclassDocs.get(type
					.qualifiedTypeName());
			LinkedList<ModificationRecord> list = this
					.getModificationRecords(classDoc);
			history.AddModificationRecords(list);
		}
		return history;
	}

	/*
	 * get the modification history of the method.
	 */
	private ModificationHistory getModificationHistory(MethodDoc methodDoc) {
		ModificationHistory history = new ModificationHistory();
		history.AddModificationRecords(this.parseModificationRecords(methodDoc
				.tags()));
		return history;
	}

	/*
	 * get the modification records of the class.
	 */
	private LinkedList<ModificationRecord> getModificationRecords(
			ClassDoc classDoc) {
		ClassDoc superClass = classDoc.superclass();
		if (superClass == null) {
			return new LinkedList<ModificationRecord>();
		}
		LinkedList<ModificationRecord> result = this
				.getModificationRecords(superClass);
		result.addAll(this.parseModificationRecords(classDoc.tags()));
		return result;
	}

	/*
	 * Parse tags to get modification records.
	 */
	private LinkedList<ModificationRecord> parseModificationRecords(Tag[] tags) {
		LinkedList<ModificationRecord> result = new LinkedList<ModificationRecord>();
		for (int i = 0; i < tags.length; i++) {
			if ("@author".equalsIgnoreCase(tags[i].name())) {
				ModificationRecord record = new ModificationRecord();
				record.setModifier(tags[i].text());

				if (i + 1 < tags.length) {
					if ("@version".equalsIgnoreCase(tags[i + 1].name())) {
						record.setVersion(tags[i + 1].text());
						if (i + 2 < tags.length
								&& ("@" + WRMemoTaglet.NAME)
										.equalsIgnoreCase(tags[i + 2].name())) {
							record.setMemo(tags[i + 2].text());
						}
					} else if (("@" + WRMemoTaglet.NAME)
							.equalsIgnoreCase(tags[i + 1].name())) {
						record.setMemo(tags[i + 1].text());
					}
				}
				result.add(record);
			}
		}

		return result;
	}

	/*
	 * Get response parameter of the MVC action method.
	 */
	private APIParameter getReponseBody(MethodDoc method) {
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

	private APIParameter getSOAPAPIOutputParams(MethodDoc method) {
		APIParameter apiParameter = null;
		if (method.returnType() != null) {
			apiParameter = new APIParameter();
			// TODO: how to represent @WebResult?
			/*
			 * AnnotationDesc[] annotations = method.annotations(); for (int i =
			 * 0; i < annotations.length; i++) { if
			 * (annotations[i].annotationType().name().equals("WebResult")) { }
			 * }
			 */
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

	private APIParameter getSOAPAPIInputParams(MethodDoc method) {
		APIParameter apiParameter = null;
		Parameter[] methodParameters = method.parameters();
		if (methodParameters.length != 0) {
			apiParameter = new APIParameter();
			List<APIParameter> paramList = new LinkedList<APIParameter>();
			for (int i = 0; i < methodParameters.length; i++) {
				APIParameter p = new APIParameter();
				AnnotationDesc[] annotations = methodParameters[i]
						.annotations();
				for (int j = 0; j < annotations.length; j++) {
					if (annotations[j].annotationType().name()
							.equals("WebParam")) {
						for (int k = 0; k < annotations[j].elementValues().length; k++) {
							if ("name".equals(annotations[j].elementValues()[k]
									.element().name())) {
								p.setName(annotations[j].elementValues()[k]
										.value().toString().replace("\"", ""));
							}
						}
					}
				}
				if (p.getName() == null) {
					p.setName("arg" + i);
				}
				p.setType(methodParameters[i].type().qualifiedTypeName());
				// TODO:
				p.setDescription("");
				p.setFields(this.getFields(methodParameters[i].type(),
						ParameterType.Request));
				paramList.add(p);
			}
			apiParameter.setFields(paramList);
		}
		return apiParameter;
	}

	/*
	 * Get request parameter of the MVC action method.
	 */
	private APIParameter getRequestBody(MethodDoc method) {
		APIParameter apiParameter = null;
		Parameter reqBody = this.findRequestBody(method.parameters());
		if (reqBody != null) {
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
		}
		return apiParameter;
	}

	/*
	 * get the field name which the getter or setter method to access. NOTE: the
	 * getter or setter method name should follow the naming convention.
	 */
	private String getFieldNameOfAccesser(String methodName) {
		if (methodName.startsWith("get")) {
			return net.winroad.wrdoclet.utils.Util.uncapitalize(methodName
					.replaceFirst("get", ""));
		} else if (methodName.startsWith("set")) {
			return net.winroad.wrdoclet.utils.Util.uncapitalize(methodName
					.replaceFirst("set", ""));
		} else {
			return net.winroad.wrdoclet.utils.Util.uncapitalize(methodName
					.replaceFirst("is", ""));
		}
	}

	/*
	 * is the method a getter method of a field.
	 */
	private boolean isGetterMethod(MethodDoc methodDoc) {
		if (methodDoc.parameters() != null
				&& methodDoc.parameters().length == 0
				&& (!"boolean".equalsIgnoreCase(methodDoc.returnType()
						.qualifiedTypeName()) && methodDoc.name().matches(
						"^get[A-Z].+"))
				|| (("boolean".equalsIgnoreCase(methodDoc.returnType()
						.qualifiedTypeName()) && methodDoc.name().matches(
						"^is[A-Z].+")))) {
			return true;
		}
		return false;
	}

	/*
	 * is the method a setter method of a field.
	 */
	private boolean isSetterMethod(MethodDoc methodDoc) {
		if (methodDoc.parameters() != null
				&& methodDoc.parameters().length == 1
				&& methodDoc.name().matches("^set[A-Z].+")) {
			return true;
		}
		return false;
	}

	private List<APIParameter> getFields(Type type, ParameterType paramType) {
		if (this.nonControllerclassDocs.containsKey(type.qualifiedTypeName())) {
			ClassDoc classDoc = this.nonControllerclassDocs.get(type
					.qualifiedTypeName());
			return this.getFields(classDoc, paramType);
		}
		return null;
	}

	private List<APIParameter> getFields(ClassDoc classDoc,
			ParameterType paramType) {
		List<APIParameter> result = null;
		ClassDoc superClassDoc = classDoc.superclass();
		if (superClassDoc != null
				&& !"java.lang.Object"
						.equals(superClassDoc.qualifiedTypeName())
				&& !"java.lang.Enum".equals(superClassDoc.qualifiedTypeName())) {
			result = this.getFields(superClassDoc, paramType);
		} else {
			result = new LinkedList<APIParameter>();
		}

		FieldDoc[] fieldDocs = classDoc.fields();
		for (FieldDoc fieldDoc : fieldDocs) {
			if (fieldDoc.isPublic() && !fieldDoc.isStatic()) {
				APIParameter param = new APIParameter();
				param.setName(fieldDoc.name());
				processType(paramType, param, fieldDoc.type());
				param.setDescription(fieldDoc.commentText());
				param.setHistory(new ModificationHistory(this
						.parseModificationRecords(fieldDoc.tags())));
				param.setParameterOccurs(this.parseParameterOccurs(fieldDoc
						.tags(WROccursTaglet.NAME)));
				result.add(param);
			}
		}

		MethodDoc[] methodDocs = classDoc.methods(false);
		for (MethodDoc methodDoc : methodDocs) {
			if ((paramType == ParameterType.Response && this
					.isGetterMethod(methodDoc))
					|| (paramType == ParameterType.Request && this
							.isSetterMethod(methodDoc))) {
				APIParameter param = new APIParameter();
				param.setName(this.getFieldNameOfAccesser(methodDoc.name()));
				Type typeToProcess = null;
				if (paramType == ParameterType.Request) {
					// set method only has one parameter.
					typeToProcess = methodDoc.parameters()[0].type();
				} else {
					typeToProcess = methodDoc.returnType();
				}
				processType(paramType, param, typeToProcess);
				param.setHistory(new ModificationHistory(this
						.parseModificationRecords(methodDoc.tags())));
				param.setDescription(methodDoc.commentText());
				param.setParameterOccurs(this.parseParameterOccurs(methodDoc
						.tags(WROccursTaglet.NAME)));
				result.add(param);
			}
		}
		return result;
	}

	private void processType(ParameterType paramType, APIParameter param,
			Type typeToProcess) {
		if (!typeToProcess.isPrimitive()
				&& !"java.lang.String".equalsIgnoreCase(typeToProcess
						.qualifiedTypeName())) {
			ParameterizedType pt = typeToProcess.asParameterizedType();
			if (pt != null && pt.typeArguments().length > 0) {
				StringBuilder strBuilder = new StringBuilder();
				strBuilder.append(typeToProcess.qualifiedTypeName());
				strBuilder.append("<");
				for (Type arg : pt.typeArguments()) {
					strBuilder.append(arg.simpleTypeName());
					strBuilder.append(",");
					APIParameter tmp = new APIParameter();
					tmp.setName(arg.simpleTypeName());
					tmp.setType(arg.qualifiedTypeName());
					tmp.setDescription("");
					tmp.setParentTypeArgument(true);
					tmp.setFields(this.getFields(arg, paramType));
					param.appendField(tmp);
				}
				int len = strBuilder.length();
				// trim the last ","
				strBuilder.deleteCharAt(len - 1);
				strBuilder.append(">");
				param.setType(strBuilder.toString());
			} else {
				param.setType(typeToProcess.qualifiedTypeName());
				param.setFields(this.getFields(typeToProcess, paramType));
			}
		} else {
			param.setType(typeToProcess.qualifiedTypeName());
		}

		// handle enum to output enum values into doc
		if (typeToProcess.asClassDoc() != null) {
			ClassDoc superClass = typeToProcess.asClassDoc().superclass();
			if (superClass != null
					&& "java.lang.Enum".equals(superClass.qualifiedTypeName())) {
				FieldDoc[] enumConstants = typeToProcess.asClassDoc()
						.enumConstants();
				StringBuilder strBuilder = new StringBuilder();
				strBuilder.append("Enum[");
				for (FieldDoc enumConstant : enumConstants) {
					strBuilder.append(enumConstant.name());
					strBuilder.append(",");
				}
				int len = strBuilder.length();
				// trim the last ","
				strBuilder.deleteCharAt(len - 1);
				strBuilder.append("]");
				param.setType(strBuilder.toString());
			}
		}
	}

	/*
	 * Parse the ParameterOccurs from the tags.
	 */
	private ParameterOccurs parseParameterOccurs(Tag[] tags) {
		for (int i = 0; i < tags.length; i++) {
			if (("@" + WROccursTaglet.NAME).equalsIgnoreCase(tags[i].name())) {
				if (WROccursTaglet.REQUIRED.equalsIgnoreCase(tags[i].text())) {
					return ParameterOccurs.REQUIRED;
				} else if (WROccursTaglet.OPTIONAL.equalsIgnoreCase(tags[i]
						.text())) {
					return ParameterOccurs.OPTIONAL;
				} else if (WROccursTaglet.DEPENDS.equalsIgnoreCase(tags[i]
						.text())) {
					return ParameterOccurs.DEPENDS;
				} else {
					// TODO: WARNING in this case
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

	private RequestMapping parseSOAPAPIRequestMapping(MethodDoc method) {
		RequestMapping mapping = new RequestMapping();
		mapping.setUrl(method.name());
		return mapping;
	}

	/*
	 * Parse the @RequestMapping from the MVC action method.
	 */
	private RequestMapping parseRESTAPIRequestMapping(MethodDoc method) {
		ClassDoc controllerClass = method.containingClass();
		AnnotationDesc[] baseAnnotations = controllerClass.annotations();
		RequestMapping baseMapping = this.parseRequestMapping(baseAnnotations);
		AnnotationDesc[] annotations = method.annotations();
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
	 * Simplify the methodType of @RequestMapping to display.
	 */
	private String convertMethodType(String methodType) {
		return methodType
				.replace(
						"org.springframework.web.bind.annotation.RequestMethod.",
						"").replace("{", "").replace("}", "");
	}
}
