package net.winroad.wrdoclet.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.winroad.wrdoclet.taglets.WRTagTaglet;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.Util;

public class TagTree {

	private Set<String> wrTags = new HashSet<String>();

	private Map<String, Set<MethodDoc>> taggedMethods = new HashMap<String, Set<MethodDoc>>();

	public TagTree(Configuration configuration) {
		buildTree(configuration.root.classes(), configuration);
	}

	public TagTree(RootDoc root, Configuration configuration) {
		buildTree(root.classes(), configuration);
	}

	public TagTree(ClassDoc[] classes, Configuration configuration) {
		buildTree(classes, configuration);
	}

	private void buildTree(ClassDoc[] classes, Configuration configuration) {
		for (int i = 0; i < classes.length; i++) {
			if (configuration.nodeprecated
					&& (Util.isDeprecated(classes[i]) || Util
							.isDeprecated(classes[i].containingPackage()))) {
				continue;
			}

			if (!this.isController(classes[i])) {
				continue;
			}

			this.processControllerClass(classes[i], configuration);
			MethodDoc[] methods = classes[i].methods();
			for (int l = 0; l < methods.length; l++) {
				this.processControllerMethod(methods[l], configuration);
			}
		}
	}

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

	private void processControllerMethod(MethodDoc method,
			Configuration configuration) {
		if (configuration.nodeprecated && Util.isDeprecated(method)) {
			return;
		}

		if (!isActionMethod(method)) {
			return;
		}

		Tag[] methodTagArray = method.tags(WRTagTaglet.NAME);
		for (int m = 0; m < methodTagArray.length; m++) {
			Set<String> methodTags = WRTagTaglet.getTagSet(methodTagArray[m]
					.text());
			wrTags.addAll(methodTags);
			for (Iterator<String> iter = methodTags.iterator(); iter.hasNext();) {
				String tag = iter.next();
				if (!this.taggedMethods.containsKey(tag)) {
					this.taggedMethods.put(tag, new HashSet<MethodDoc>());
				}
				this.taggedMethods.get(tag).add(method);
			}
		}

	}

	private boolean isController(ClassDoc classDoc) {
		AnnotationDesc[] annotations = classDoc.annotations();
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].annotationType().name().equals("Controller")) {
				return true;
			}
		}
		return false;
	}

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

	public Set<String> getWRTags() {
		return this.wrTags;
	}

	public Map<String, Set<MethodDoc>> getTaggedMethods() {
		return this.taggedMethods;
	}
}
