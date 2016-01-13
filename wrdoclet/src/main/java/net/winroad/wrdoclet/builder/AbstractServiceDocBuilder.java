package net.winroad.wrdoclet.builder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.winroad.wrdoclet.data.WRDoc;
import net.winroad.wrdoclet.taglets.WRTagTaglet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.Util;

public abstract class AbstractServiceDocBuilder extends AbstractDocBuilder {
	public AbstractServiceDocBuilder(WRDoc wrDoc) {
		super(wrDoc);
	}

	@Override
	protected void processOpenAPIClasses(ClassDoc[] classes,
			Configuration configuration) {
		for (int i = 0; i < classes.length; i++) {
			if (configuration.nodeprecated
					&& (Util.isDeprecated(classes[i]) || Util
							.isDeprecated(classes[i].containingPackage()))) {
				continue;
			}

			if (this.isServiceInterface(classes[i])) {
				this.processServiceClass(classes[i], configuration);
				MethodDoc[] methods = classes[i].methods();
				for (int l = 0; l < methods.length; l++) {
					if (configuration.nodeprecated
							&& Util.isDeprecated(methods[l])) {
						continue;
					}
					this.processOpenAPIMethod(methods[l], configuration);
				}
			}
		}
	}

	abstract boolean isServiceInterface(ClassDoc classDoc);

	@Override
	protected boolean isOpenAPIMethod(MethodDoc methodDoc) {
		return methodDoc.isPublic();
	}

	/*
	 * Process the tag on the Service.
	 */
	protected void processServiceClass(ClassDoc service,
			Configuration configuration) {
		Tag[] serviceTagArray = service.tags(WRTagTaglet.NAME);
		for (int i = 0; i < serviceTagArray.length; i++) {
			Set<String> serviceTags = WRTagTaglet.getTagSet(serviceTagArray[i]
					.text());
			for (Iterator<String> iter = serviceTags.iterator(); iter.hasNext();) {
				String tag = iter.next();
				if (!this.taggedOpenAPIMethods.containsKey(tag)) {
					this.taggedOpenAPIMethods
							.put(tag, new HashSet<MethodDoc>());
				}
				// all method of this service should be processed later.
				for (int j = 0; j < service.methods().length; j++) {
					if (configuration.nodeprecated
							&& Util.isDeprecated(service.methods()[j])) {
						continue;
					}
					this.taggedOpenAPIMethods.get(tag)
							.add(service.methods()[j]);
				}
			}

			this.wrDoc.getWRTags().addAll(serviceTags);
		}
	}
}
