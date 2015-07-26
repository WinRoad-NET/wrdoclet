package net.winroad.wrdoclet.data;

import java.util.LinkedList;
import java.util.List;

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
		AnnotationDesc[] annotations = method.annotations();
		boolean isOprNameCustomized = false;
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].annotationType().name().equals("WebMethod")) {
				for(ElementValuePair p : annotations[i].elementValues()) {
					if(p.element().name().equals("operationName")) {
						isOprNameCustomized = true;
						mapping.setUrl(p.value().value().toString().replace("\"", ""));	
					}
				}
			}
		}
		if(!isOprNameCustomized) {
			mapping.setUrl(method.name());			
		}
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
					for(ElementValuePair p : annotations[i].elementValues()) {
						if(p.element().name().equals("name")) {
							apiParameter.setName(p.value().value().toString());
							isResNameCustomized = true;
						}
					}
				}
			}
			if(!isResNameCustomized) {
				apiParameter.setName("return");
			}
			apiParameter.setParameterOccurs(ParameterOccurs.REQUIRED);
			apiParameter.setType(this.getTypeName(method.returnType()));
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
							if ("required".equals(annotations[j].elementValues()[k]
									.element().name())) {
								if(annotations[j].elementValues()[k]
										.value().toString().equals("true")) {
									p.setParameterOccurs(ParameterOccurs.REQUIRED);
								} else if(annotations[j].elementValues()[k]
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
				p.setDescription("");
				//TODO:
				//p.setDescription(this.getParamComment(method, p.getName()));
				p.setFields(this.getFields(methodParameters[i].type(),
						ParameterType.Request));
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


}
