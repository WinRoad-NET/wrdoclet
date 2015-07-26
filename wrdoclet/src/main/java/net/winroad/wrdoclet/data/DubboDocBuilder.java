package net.winroad.wrdoclet.data;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.winroad.wrdoclet.ConfigurationImpl;
import net.winroad.wrdoclet.utils.Logger;
import net.winroad.wrdoclet.utils.LoggerFactory;
import net.winroad.wrdoclet.utils.UniversalNamespaceCache;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Tag;

public class DubboDocBuilder extends AbstractServiceDocBuilder {
	protected List<String> dubboInterfaces = null;
	protected Logger logger;

	public DubboDocBuilder(WRDoc wrDoc) {
		super(wrDoc);
		this.logger = LoggerFactory.getLogger(this.getClass());
		dubboInterfaces = this.getDubboInterfaces();
	}

	public static Document readDubboConfig(String filePath)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		builderFactory.setNamespaceAware(true);
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		File dubboConfig = new File(filePath);
		return builder.parse(dubboConfig);
	}

	protected static String getAttributeValue(Node node, String attributeName) {
		NamedNodeMap attributes = node.getAttributes();
		if (attributes != null) {
			Node attribute = attributes.getNamedItem(attributeName);
			if (attribute != null) {
				return attribute.getNodeValue();
			}
		}
		return null;
	}

	protected List<String> getDubboInterfaces() {
		List<String> result = new LinkedList<String>();
		try {
			Document dubboConfig = readDubboConfig(((ConfigurationImpl) this.wrDoc
					.getConfiguration()).dubboconfigpath);
			XPath xPath = XPathFactory.newInstance().newXPath();
			xPath.setNamespaceContext(new UniversalNamespaceCache(dubboConfig,
					false));
			NodeList serviceNodes = (NodeList) xPath.evaluate(
					"//:beans/dubbo:service", dubboConfig,
					XPathConstants.NODESET);
			for (int i = 0; i < serviceNodes.getLength(); i++) {
				Node node = serviceNodes.item(i);
				String ifc = getAttributeValue(node, "interface");
				if (ifc != null)
					result.add(ifc);
			}
		} catch (Exception e) {
			this.logger.error(e);
		}
		this.logger.debug("dubbo interface list:");
		for (String s : result) {
			this.logger.debug("interface: " + s);
		}
		return result;
	}

	@Override
	protected RequestMapping parseRequestMapping(MethodDoc methodDoc) {
		RequestMapping mapping = new RequestMapping();
		mapping.setUrl(methodDoc.name());
		return mapping;
	}

	@Override
	protected APIParameter getOutputParam(MethodDoc methodDoc) {
		APIParameter apiParameter = null;
		if (methodDoc.returnType() != null) {
			apiParameter = new APIParameter();
			apiParameter.setParameterOccurs(ParameterOccurs.REQUIRED);
			apiParameter.setType(this.getTypeName(methodDoc.returnType()));
			for (Tag tag : methodDoc.tags("return")) {
				apiParameter.setDescription(tag.text());
			}
			apiParameter.setFields(this.getFields(methodDoc.returnType(),
					ParameterType.Response));
			apiParameter.setHistory(this.getModificationHistory(methodDoc
					.returnType()));
		}
		return apiParameter;
	}

	@Override
	protected List<APIParameter> getInputParams(MethodDoc methodDoc) {
		List<APIParameter> paramList = new LinkedList<APIParameter>();
		Parameter[] methodParameters = methodDoc.parameters();
		if (methodParameters.length != 0) {
			for (int i = 0; i < methodParameters.length; i++) {
				APIParameter p = new APIParameter();
				p.setName(methodParameters[i].name());
				p.setType(this.getTypeName(methodParameters[i].type()));
				p.setDescription("");
				// p.setDescription(this.getParamComment(method, p.getName()));
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
				&& dubboInterfaces.contains(classDoc.qualifiedName());
	}

}
