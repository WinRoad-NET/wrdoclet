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
import net.winroad.wrdoclet.taglets.WRTagTaglet;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.Util;

public class WRDoc {
	private Map<String, List<OpenAPI>> taggedOpenAPIs = new HashMap<String, List<OpenAPI>>();

	// The collection of tag name in this Doc.
	private Set<String> wrTags = new HashSet<String>();

	/*
	 * The collection of MethodDoc to build Doc. Map key: tag name. Map value:
	 * collection of MethodDoc
	 */
	private Map<String, Set<MethodDoc>> taggedMethods = new HashMap<String, Set<MethodDoc>>();

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

			if (!this.isController(classes[i])) {
				this.nonControllerclassDocs.put(classes[i].qualifiedTypeName(),
						classes[i]);
				continue;
			}

			this.processControllerClass(classes[i], configuration);
			MethodDoc[] methods = classes[i].methods();
			for (int l = 0; l < methods.length; l++) {
				this.processControllerMethod(methods[l], configuration);
			}
		}

		this.buildOpenAPIs(configuration);
	}

	private void buildOpenAPIs(Configuration configuration) {
		Set<Entry<String, Set<MethodDoc>>> methods = this.taggedMethods
				.entrySet();
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
				OpenAPI openAPI = this.buildOpenAPI(mthDoc, configuration);
				this.taggedOpenAPIs.get(tagName).add(openAPI);
			}
		}
	}

	private OpenAPI buildOpenAPI(MethodDoc methodDoc,
			Configuration configuration) {
		OpenAPI openAPI = new OpenAPI();
		openAPI.setDescription(methodDoc.commentText());
		openAPI.setModificationHistory(this.getModificationHistory(methodDoc));
		openAPI.setRequestMapping(this.getRequestMapping(methodDoc));
		openAPI.setInParameter(this.getRequestBody(methodDoc));
		openAPI.setOutParameter(this.getReponseBody(methodDoc));
		return openAPI;
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
				if (!this.taggedMethods.containsKey(tag)) {
					this.taggedMethods.put(tag, new HashSet<MethodDoc>());
				}
				// all action method of this controller should be processed
				// later.
				for (int j = 0; j < controller.methods().length; j++) {
					if (isActionMethod(controller.methods()[j])) {
						this.taggedMethods.get(tag)
								.add(controller.methods()[j]);
					}
				}
			}

			wrTags.addAll(controllerTags);
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
			if (!this.taggedMethods.containsKey(tag)) {
				this.taggedMethods.put(tag, new HashSet<MethodDoc>());
			}
			this.taggedMethods.get(tag).add(method);
		} else {
			for (int i = 0; i < methodTagArray.length; i++) {
				Set<String> methodTags = WRTagTaglet
						.getTagSet(methodTagArray[i].text());
				wrTags.addAll(methodTags);
				for (Iterator<String> iter = methodTags.iterator(); iter
						.hasNext();) {
					String tag = iter.next();
					if (!this.taggedMethods.containsKey(tag)) {
						this.taggedMethods.put(tag, new HashSet<MethodDoc>());
					}
					this.taggedMethods.get(tag).add(method);
				}
			}
		}
	}

	/*
	 * Here we only treat class which has spring "@Controller" annotation as a
	 * Controller, although it may not be enough.
	 */
	private boolean isController(ClassDoc classDoc) {
		AnnotationDesc[] annotations = classDoc.annotations();
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].annotationType().name().equals("Controller")) {
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

	private ModificationHistory getModificationHistory(Parameter param) {
		return this.getModificationHistory(param.type());
	}

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

	private ModificationHistory getModificationHistory(MethodDoc methodDoc) {
		ModificationHistory history = new ModificationHistory();
		history.AddModificationRecords(this.getModificationRecords(methodDoc
				.tags()));
		return history;
	}

	private LinkedList<ModificationRecord> getModificationRecords(
			ClassDoc classDoc) {
		ClassDoc superClass = classDoc.superclass();
		if (superClass == null) {
			return new LinkedList<ModificationRecord>();
		}
		LinkedList<ModificationRecord> result = this
				.getModificationRecords(superClass);
		result.addAll(this.getModificationRecords(classDoc.tags()));
		return result;
	}

	private LinkedList<ModificationRecord> getModificationRecords(Tag[] tags) {
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

	private APIParameter getRequestBody(MethodDoc method) {
		APIParameter apiParameter = null;
		Parameter reqBody = this.getRequestBody(method.parameters());
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
			apiParameter.setFields(this.getFields(reqBody.type()));
			apiParameter.setHistory(this.getModificationHistory(reqBody));
		}
		return apiParameter;
	}

	private List<APIParameter> getFields(ClassDoc classDoc) {
		List<APIParameter> result = new LinkedList<APIParameter>();
		if (classDoc != null) {
			ClassDoc superClassDoc = classDoc.superclass();
			result.addAll(this.getFields(superClassDoc));
			FieldDoc[] fieldDocs = classDoc.fields(false);
			for (FieldDoc fieldDoc : fieldDocs) {
				APIParameter field = new APIParameter();
				field.setName(fieldDoc.name());
				field.setType(fieldDoc.type().qualifiedTypeName());
				if (!fieldDoc.type().isPrimitive()
						&& !"java.lang.String".equalsIgnoreCase(fieldDoc.type()
								.qualifiedTypeName())) {
					field.setFields(this.getFields(fieldDoc.type()));
				}
				field.setHistory(this.getModificationHistory(fieldDoc.type()));
				field.setDescription(fieldDoc.commentText());
				field.setParameterOccurs(this.getParameterOccurs(fieldDoc
						.tags(WROccursTaglet.NAME)));
				result.add(field);
			}
		}
		return result;
	}

	private List<APIParameter> getFields(Type type) {
		if (this.nonControllerclassDocs.containsKey(type.qualifiedTypeName())) {
			ClassDoc classDoc = this.nonControllerclassDocs.get(type
					.qualifiedTypeName());
			return this.getFields(classDoc);
		}
		return null;
	}

	private ParameterOccurs getParameterOccurs(Tag[] tags) {
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
					// TODO: WARNING
				}
			}
		}
		return null;
	}

	private APIParameter getReponseBody(MethodDoc method) {
		APIParameter apiParameter = null;
		if (this.isAnnotatedResponseBody(method)) {
			apiParameter = new APIParameter();
			apiParameter.setParameterOccurs(ParameterOccurs.REQUIRED);
			apiParameter.setType(method.returnType().qualifiedTypeName());
			for (Tag tag : method.tags("return")) {
				apiParameter.setDescription(tag.text());
			}
			apiParameter.setHistory(this.getModificationHistory(method
					.returnType()));
		}
		return apiParameter;
	}

	private Parameter getRequestBody(Parameter[] parameters) {
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

	private boolean isAnnotatedResponseBody(MethodDoc method) {
		AnnotationDesc[] annotations = method.annotations();
		for (int i = 0; i < annotations.length; i++) {
			for (int j = 0; j < annotations.length; j++) {
				if (annotations[j].annotationType().name()
						.equals("ResponseBody")) {
					return true;
				}
			}
		}
		return false;
	}

	private RequestMapping getRequestMapping(AnnotationDesc[] annotations) {
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

	private RequestMapping getRequestMapping(MethodDoc method) {
		ClassDoc controllerClass = method.containingClass();
		AnnotationDesc[] baseAnnotations = controllerClass.annotations();
		RequestMapping baseMapping = this.getRequestMapping(baseAnnotations);
		AnnotationDesc[] annotations = method.annotations();
		RequestMapping mapping = this.getRequestMapping(annotations);
		if (mapping == null) {
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

	private String convertMethodType(String methodType) {
		return methodType
				.replace(
						"org.springframework.web.bind.annotation.RequestMethod.",
						"").replace("{", "").replace("}", "");
	}
}
