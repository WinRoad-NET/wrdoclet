package net.winroad.wrdoclet.builder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.winroad.wrdoclet.data.APIParameter;
import net.winroad.wrdoclet.data.ModificationHistory;
import net.winroad.wrdoclet.data.ModificationRecord;
import net.winroad.wrdoclet.data.OpenAPI;
import net.winroad.wrdoclet.data.ParameterOccurs;
import net.winroad.wrdoclet.data.ParameterType;
import net.winroad.wrdoclet.data.RequestMapping;
import net.winroad.wrdoclet.data.WRDoc;
import net.winroad.wrdoclet.taglets.WRBriefTaglet;
import net.winroad.wrdoclet.taglets.WRMemoTaglet;
import net.winroad.wrdoclet.taglets.WROccursTaglet;
import net.winroad.wrdoclet.taglets.WRParamTaglet;
import net.winroad.wrdoclet.taglets.WRReturnCodeTaglet;
import net.winroad.wrdoclet.taglets.WRReturnTaglet;
import net.winroad.wrdoclet.taglets.WRTagTaglet;
import net.winroad.wrdoclet.utils.ApplicationContextConfig;
import net.winroad.wrdoclet.utils.Logger;
import net.winroad.wrdoclet.utils.LoggerFactory;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.Util;

public abstract class AbstractDocBuilder {
	protected Logger logger;

	protected WRDoc wrDoc;

	protected Map<String, Set<MethodDoc>> taggedOpenAPIMethods = new HashMap<String, Set<MethodDoc>>();

	public AbstractDocBuilder(WRDoc wrDoc) {
		this.wrDoc = wrDoc;
		this.logger = LoggerFactory.getLogger(this.getClass());
	}

	public WRDoc getWrDoc() {
		return wrDoc;
	}

	public void setWrDoc(WRDoc wrDoc) {
		this.wrDoc = wrDoc;
	}

	public Map<String, Set<MethodDoc>> getTaggedOpenAPIMethods() {
		return taggedOpenAPIMethods;
	}

	public void setTaggedOpenAPIMethods(
			Map<String, Set<MethodDoc>> taggedOpenAPIMethods) {
		this.taggedOpenAPIMethods = taggedOpenAPIMethods;
	}

	public void buildWRDoc() {
		this.processOpenAPIClasses(
				this.wrDoc.getConfiguration().root.classes(),
				this.wrDoc.getConfiguration());
		this.buildOpenAPIs(this.wrDoc.getConfiguration());
	}

	protected abstract void processOpenAPIClasses(ClassDoc[] classDocs,
			Configuration configuration);

	protected Tag[] getTagTaglets(MethodDoc methodDoc) {
		return (Tag[]) ArrayUtils.addAll(methodDoc.tags(WRTagTaglet.NAME),
				methodDoc.containingClass().tags(WRTagTaglet.NAME));
	}

	protected void processOpenAPIMethod(MethodDoc methodDoc,
			Configuration configuration) {
		if ((configuration.nodeprecated && Util.isDeprecated(methodDoc))
				|| !isOpenAPIMethod(methodDoc)) {
			return;
		}

		Tag[] methodTagArray = getTagTaglets(methodDoc);
		if (methodTagArray.length == 0) {
			String tag = methodDoc.containingClass().simpleTypeName();
			this.wrDoc.getWRTags().add(tag);
			if (!this.taggedOpenAPIMethods.containsKey(tag)) {
				this.taggedOpenAPIMethods.put(tag, new HashSet<MethodDoc>());
			}
			this.taggedOpenAPIMethods.get(tag).add(methodDoc);
		} else {
			for (int i = 0; i < methodTagArray.length; i++) {
				Set<String> methodTags = WRTagTaglet
						.getTagSet(methodTagArray[i].text());
				this.wrDoc.getWRTags().addAll(methodTags);
				for (Iterator<String> iter = methodTags.iterator(); iter
						.hasNext();) {
					String tag = iter.next();
					if (!this.taggedOpenAPIMethods.containsKey(tag)) {
						this.taggedOpenAPIMethods.put(tag,
								new HashSet<MethodDoc>());
					}
					this.taggedOpenAPIMethods.get(tag).add(methodDoc);
				}
			}
		}
	}

	protected String getBriefFromCommentText(String commentText) {
		int index = StringUtils.indexOf(commentText, '\n');
		if (index != -1) {
			commentText = StringUtils.substring(commentText, 0, index);
		}
		index = StringUtils.indexOfAny(commentText, ".!?。！？…");
		if (index > 0) {
			commentText = StringUtils.substring(commentText, 0, index);
		}
		if (StringUtils.length(commentText) > 8) {
			commentText = StringUtils.substring(commentText, 0, 8) + "…";
		}
		return commentText;
	}

	protected void buildOpenAPIs(Configuration configuration) {
		Set<Entry<String, Set<MethodDoc>>> methods = this.taggedOpenAPIMethods
				.entrySet();
		for (Iterator<Entry<String, Set<MethodDoc>>> tagMthIter = methods
				.iterator(); tagMthIter.hasNext();) {
			Entry<String, Set<MethodDoc>> kv = tagMthIter.next();
			String tagName = kv.getKey();
			if (!this.wrDoc.getTaggedOpenAPIs().containsKey(tagName)) {
				this.wrDoc.getTaggedOpenAPIs().put(tagName,
						new LinkedList<OpenAPI>());
			}
			Set<MethodDoc> methodDocSet = kv.getValue();
			for (Iterator<MethodDoc> mthIter = methodDocSet.iterator(); mthIter
					.hasNext();) {
				MethodDoc methodDoc = mthIter.next();
				OpenAPI openAPI = new OpenAPI();
				Tag[] tags = this.getTagTaglets(methodDoc);
				if (tags.length == 0) {
					openAPI.addTag(methodDoc.containingClass().simpleTypeName());
				} else {
					for (Tag t : tags) {
						openAPI.addTags(WRTagTaglet.getTagSet(t.text()));
					}
				}
				openAPI.setQualifiedName(methodDoc.qualifiedName());
				if (StringUtils.isNotBlank(methodDoc.commentText())) {
					openAPI.setDescription(methodDoc.commentText());
				}
				if (methodDoc.tags(WRBriefTaglet.NAME).length == 0) {
					openAPI.setBrief(getBriefFromCommentText(methodDoc
							.commentText()));
				} else {
					openAPI.setBrief(methodDoc.tags(WRBriefTaglet.NAME)[0]
							.text());
				}
				openAPI.setModificationHistory(this
						.getModificationHistory(methodDoc));
				openAPI.setRequestMapping(this.parseRequestMapping(methodDoc));
				if (openAPI.getRequestMapping() != null) {
					openAPI.setAuthNeeded(this.isAPIAuthNeeded(openAPI
							.getRequestMapping().getUrl()));
				}
				openAPI.addInParameters(this.getInputParams(methodDoc));
				openAPI.setOutParameter(this.getOutputParam(methodDoc));
				openAPI.setReturnCode(this.getReturnCode(methodDoc));
				this.wrDoc.getTaggedOpenAPIs().get(tagName).add(openAPI);
			}
		}
	}

	protected abstract Boolean isAPIAuthNeeded(String url);

	protected abstract boolean isOpenAPIMethod(MethodDoc methodDoc);

	protected abstract RequestMapping parseRequestMapping(MethodDoc methodDoc);

	protected abstract APIParameter getOutputParam(MethodDoc methodDoc);

	protected abstract List<APIParameter> getInputParams(MethodDoc methodDoc);

	protected String getParamComment(MethodDoc method, String paramName) {
		ParamTag[] paramTags = method.paramTags();
		for (ParamTag paramTag : paramTags) {
			if (paramTag.parameterName().equals(paramName)) {
				return paramTag.parameterComment();
			}
		}
		return null;
	}

	protected boolean isClassDocAnnotatedWith(ClassDoc classDoc,
			String annotation) {
		AnnotationDesc[] annotations = classDoc.annotations();
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].annotationType().name().equals(annotation)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * get the modification history of the class.
	 */
	protected ModificationHistory getModificationHistory(Type type) {
		ModificationHistory history = new ModificationHistory();
		ClassDoc classDoc = this.wrDoc.getConfiguration().root.classNamed(type
				.qualifiedTypeName());
		if (classDoc != null) {
			LinkedList<ModificationRecord> list = this
					.getModificationRecords(classDoc);
			history.AddModificationRecords(list);
		}
		return history;
	}

	/*
	 * get the modification history of the method.
	 */
	protected ModificationHistory getModificationHistory(MethodDoc methodDoc) {
		ModificationHistory history = new ModificationHistory();
		history.AddModificationRecords(this.parseModificationRecords(methodDoc
				.tags()));
		return history;
	}

	/*
	 * get the modification records of the class.
	 */
	protected LinkedList<ModificationRecord> getModificationRecords(
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
	 * Parse tags to get customized parameters.
	 */
	protected LinkedList<APIParameter> parseCustomizedParameters(
			MethodDoc methodDoc) {
		Tag[] tags = methodDoc.tags(WRParamTaglet.NAME);
		LinkedList<APIParameter> result = new LinkedList<APIParameter>();
		for (int i = 0; i < tags.length; i++) {
			result.add(WRParamTaglet.parse(tags[i].text()));
		}
		return result;
	}

	/*
	 * Parse tags to get customized return.
	 */
	protected APIParameter parseCustomizedReturn(MethodDoc methodDoc) {
		Tag[] tags = methodDoc.tags(WRReturnTaglet.NAME);
		APIParameter result = null;
		if (tags.length > 0) {
			result = WRReturnTaglet.parse(tags[0].text());
		}
		return result;
	}

	/*
	 * Parse tags to get modification records.
	 */
	protected LinkedList<ModificationRecord> parseModificationRecords(Tag[] tags) {
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

	protected String getReturnCode(MethodDoc methodDoc) {
		Tag[] tags = methodDoc.tags(WRReturnCodeTaglet.NAME);
		return WRReturnCodeTaglet.concat(tags);
	}

	protected boolean isInStopClasses(ClassDoc classDoc) {
		String property = ApplicationContextConfig.getStopClasses();
		if (property != null) {
			String[] stopClasses = property.split(",");
			String[] cdParts = classDoc.qualifiedTypeName().split("\\.");
			for (String stopClass : stopClasses) {
				String[] scParts = stopClass.trim().split("\\.");
				if (scParts.length <= cdParts.length) {
					boolean hasDiffPart = false;
					for (int i = 0; i < scParts.length; i++) {
						if (scParts[i].equals("*")) {
							return true;
						} else if (!scParts[i].equalsIgnoreCase(cdParts[i])) {
							hasDiffPart = true;
							break;
						}
					}
					if (scParts.length == cdParts.length && !hasDiffPart) {
						return true;
					}
				}
			}
		}

		return false;
	}

	protected List<APIParameter> getFields(Type type, ParameterType paramType,
			HashSet<String> processingClasses) {
		processingClasses.add(type.toString());
		List<APIParameter> result = new LinkedList<APIParameter>();
		if (!type.isPrimitive()) {
			ParameterizedType pt = type.asParameterizedType();
			if (pt != null && pt.typeArguments().length > 0) {
				for (Type arg : pt.typeArguments()) {
					APIParameter tmp = new APIParameter();
					tmp.setName(arg.simpleTypeName());
					tmp.setType(this.getTypeName(arg));
					tmp.setDescription("");
					tmp.setParentTypeArgument(true);
					if (!processingClasses.contains(arg.qualifiedTypeName())) {
						tmp.setFields(this.getFields(arg, paramType,
								processingClasses));
					}
					result.add(tmp);
				}
			}

			ClassDoc classDoc = this.wrDoc.getConfiguration().root
					.classNamed(type.qualifiedTypeName());
			if (classDoc != null) {
				result.addAll(this.getFields(classDoc, paramType,
						processingClasses));
			}
		}
		return result;
	}

	protected List<APIParameter> getFields(ClassDoc classDoc,
			ParameterType paramType, HashSet<String> processingClasses) {
		processingClasses.add(classDoc.toString());
		List<APIParameter> result = new LinkedList<APIParameter>();

		ClassDoc superClassDoc = classDoc.superclass();
		if (superClassDoc != null
				&& !this.isInStopClasses(superClassDoc)
				&& !processingClasses.contains(superClassDoc
						.qualifiedTypeName())) {
			result.addAll(this.getFields(superClassDoc, paramType,
					processingClasses));
		}

		if (this.isInStopClasses(classDoc)) {
			return result;
		}

		FieldDoc[] fieldDocs = classDoc.fields(false);
		HashMap<String, String> privateFieldDesc = new HashMap<String, String>();
		for (FieldDoc fieldDoc : fieldDocs) {
			if (fieldDoc.isPublic() && !fieldDoc.isStatic()) {
				APIParameter param = new APIParameter();
				param.setName(fieldDoc.name());
				param.setType(this.getTypeName(fieldDoc.type()));
				if (!processingClasses.contains(fieldDoc.type()
						.qualifiedTypeName())) {
					param.setFields(this.getFields(fieldDoc.type(), paramType,
							processingClasses));
				}
				param.setDescription(fieldDoc.commentText());
				param.setHistory(new ModificationHistory(this
						.parseModificationRecords(fieldDoc.tags())));
				param.setParameterOccurs(this.parseParameterOccurs(fieldDoc
						.tags(WROccursTaglet.NAME)));
				result.add(param);
			} else {
				privateFieldDesc.put(fieldDoc.name(), fieldDoc.commentText());
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
				param.setType(this.getTypeName(typeToProcess));
				if (!processingClasses.contains(typeToProcess
						.qualifiedTypeName())) {
					param.setFields(this.getFields(typeToProcess, paramType,
							processingClasses));
				}
				param.setHistory(new ModificationHistory(this
						.parseModificationRecords(methodDoc.tags())));
				if(StringUtils.isEmpty(methodDoc.commentText())) {
					if(paramType == ParameterType.Request) {
						param.setDescription(this.getParamComment(methodDoc, methodDoc.parameters()[0].name()));
					} else {
						for (Tag tag : methodDoc.tags("return")) {
							param.setDescription(tag.text());
						}
					}
				} else {
					param.setDescription(methodDoc.commentText());
				}
				
				if(StringUtils.isEmpty(param.getDescription())) {
					param.setDescription(privateFieldDesc.get(param.getName()));
				}
				
				param.setParameterOccurs(this.parseParameterOccurs(methodDoc
						.tags(WROccursTaglet.NAME)));
				result.add(param);
			}
		}
		return result;
	}

	protected String getTypeName(Type typeToProcess) {
		// special type to process e.g. java.util.Map.Entry<Address,Person>
		ParameterizedType pt = typeToProcess.asParameterizedType();
		if (pt != null && pt.typeArguments().length > 0) {
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append(typeToProcess.qualifiedTypeName());
			strBuilder.append("<");
			for (Type arg : pt.typeArguments()) {
				strBuilder.append(this.getTypeName(arg));
				strBuilder.append(",");
			}
			int len = strBuilder.length();
			// trim the last ","
			strBuilder.deleteCharAt(len - 1);
			strBuilder.append(">");
			return strBuilder.toString();
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
				return strBuilder.toString();
			}
		}

		return typeToProcess.qualifiedTypeName();
	}

	/*
	 * Parse the ParameterOccurs from the tags.
	 */
	protected ParameterOccurs parseParameterOccurs(Tag[] tags) {
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
					this.logger.warn("Unexpected WROccursTaglet: "
							+ tags[i].text());
				}
			}
		}
		return null;
	}

	/*
	 * is the method a getter method of a field.
	 */
	protected boolean isGetterMethod(MethodDoc methodDoc) {
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
	protected boolean isSetterMethod(MethodDoc methodDoc) {
		if (methodDoc.parameters() != null
				&& methodDoc.parameters().length == 1
				&& methodDoc.name().matches("^set[A-Z].+")) {
			return true;
		}
		return false;
	}

	/*
	 * get the field name which the getter or setter method to access. NOTE: the
	 * getter or setter method name should follow the naming convention.
	 */
	protected String getFieldNameOfAccesser(String methodName) {
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

	public static Document readXMLConfig(String filePath)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		builderFactory.setNamespaceAware(true);
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		File dubboConfig = new File(filePath);
		return builder.parse(dubboConfig);
	}

	public static String getAttributeValue(Node node, String attributeName) {
		NamedNodeMap attributes = node.getAttributes();
		if (attributes != null) {
			Node attribute = attributes.getNamedItem(attributeName);
			if (attribute != null) {
				return attribute.getNodeValue();
			}
		}
		return null;
	}
}
