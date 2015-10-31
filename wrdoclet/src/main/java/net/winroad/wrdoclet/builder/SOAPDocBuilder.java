package net.winroad.wrdoclet.builder;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.winroad.wrdoclet.data.APIParameter;
import net.winroad.wrdoclet.data.ParameterOccurs;
import net.winroad.wrdoclet.data.ParameterType;
import net.winroad.wrdoclet.data.RequestMapping;
import net.winroad.wrdoclet.data.WRDoc;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Tag;

public class SOAPDocBuilder extends AbstractServiceDocBuilder {
	public SOAPDocBuilder(WRDoc wrDoc) {
		super(wrDoc);
	}

	@Override
	protected RequestMapping parseRequestMapping(MethodDoc method) {
		RequestMapping mapping = new RequestMapping();
		mapping.setContainerName(method.containingClass().simpleTypeName());
		AnnotationDesc[] annotations = method.annotations();
		String url = method.toString().replaceFirst(
				method.containingClass().qualifiedName() + ".", "");
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].annotationType().name().equals("WebMethod")) {
				for (ElementValuePair p : annotations[i].elementValues()) {
					if (p.element().name().equals("operationName")) {
						url = url.replace(method.name(), p.value().value()
								.toString().replace("\"", ""));
					}
				}
			}
		}
		mapping.setUrl(url);
		mapping.setTooltip(method.containingClass().simpleTypeName());
		mapping.setContainerName(method.containingClass().simpleTypeName());
		return mapping;
	}

	@Override
	protected APIParameter getOutputParam(MethodDoc method) {
		APIParameter apiParameter = null;
		if (method.returnType() != null) {
			apiParameter = new APIParameter();
			AnnotationDesc[] annotations = method.annotations();
			boolean isResNameCustomized = false;
			for (int i = 0; i < annotations.length; i++) {
				if (annotations[i].annotationType().name().equals("WebResult")) {
					for (ElementValuePair p : annotations[i].elementValues()) {
						if (p.element().name().equals("name")) {
							apiParameter.setName(p.value().value().toString());
							isResNameCustomized = true;
						}
					}
				}
			}
			if (!isResNameCustomized) {
				apiParameter.setName("return");
			}
			apiParameter.setParameterOccurs(ParameterOccurs.REQUIRED);
			apiParameter.setType(this.getTypeName(method.returnType()));
			for (Tag tag : method.tags("return")) {
				apiParameter.setDescription(tag.text());
			}
			HashSet<String> processingClasses = new HashSet<String>();
			apiParameter.setFields(this.getFields(method.returnType(),
					ParameterType.Response, processingClasses));
			apiParameter.setHistory(this.getModificationHistory(method
					.returnType()));
		}
		return apiParameter;
	}

	@Override
	protected List<APIParameter> getInputParams(MethodDoc method) {
		List<APIParameter> paramList = new LinkedList<APIParameter>();
		Parameter[] methodParameters = method.parameters();
		if (methodParameters.length != 0) {
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
					if (annotations[j].annotationType().name()
							.equals("XmlElement")) {
						for (int k = 0; k < annotations[j].elementValues().length; k++) {
							if ("required".equals(annotations[j]
									.elementValues()[k].element().name())) {
								if (annotations[j].elementValues()[k].value()
										.toString().equals("true")) {
									p.setParameterOccurs(ParameterOccurs.REQUIRED);
								} else if (annotations[j].elementValues()[k]
										.value().toString().equals("false")) {
									p.setParameterOccurs(ParameterOccurs.OPTIONAL);
								}
							}
						}
					}
				}
				if (p.getName() == null) {
					p.setName("arg" + i);
				}
				p.setType(this.getTypeName(methodParameters[i].type()));
				p.setDescription(this.getParamComment(method, methodParameters[i].name()));
				HashSet<String> processingClasses = new HashSet<String>();
				p.setFields(this.getFields(methodParameters[i].type(),
						ParameterType.Request, processingClasses));
				paramList.add(p);
			}
		}
		return paramList;
	}

	@Override
	protected boolean isServiceInterface(ClassDoc classDoc) {
		return classDoc.isInterface()
				&& this.isClassDocAnnotatedWith(classDoc, "WebService");
	}

	@Override
	protected Boolean isAPIAuthNeeded(String url) {
		return null;
	}
}
