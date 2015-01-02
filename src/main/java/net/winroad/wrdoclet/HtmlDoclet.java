package net.winroad.wrdoclet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.winroad.wrdoclet.taglets.WRTagTaglet;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.builders.AbstractBuilder;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;

/**
 * @author AdamsLee
 *
 */
public class HtmlDoclet extends AbstractDoclet {

	public HtmlDoclet() {
		configuration = (ConfigurationImpl) configuration();
	}

	/**
	 * The global configuration information for this run.
	 */
	public ConfigurationImpl configuration;

	/**
	 * The "start" method as required by Javadoc.
	 * 
	 * @param root
	 *            the root of the documentation tree.
	 * @see com.sun.javadoc.RootDoc
	 * @return true if the doclet ran without encountering any errors.
	 */
	public static boolean start(RootDoc root) {
		try {
			HtmlDoclet doclet = new HtmlDoclet();
			return doclet.start(doclet, root);
		} finally {
			ConfigurationImpl.reset();
		}
	}

	@Override
	public Configuration configuration() {
		return ConfigurationImpl.getInstance();
	}

	@Override
	protected void generateWRTagFiles(RootDoc root) {
		Set<String> wrTags = new HashSet<String>();		
		ClassDoc[] classes = root.classes();
		for(int i = 0; i < classes.length; i++) {
			AnnotationDesc[] annotations =  classes[i].annotations();
			for(int j = 0; j < annotations.length; j++) {
				if(annotations[j].toString().compareTo("@Controller") == 0) {
					Tag[] tags = classes[i].tags(WRTagTaglet.NAME);
					for(int k = 0; k < tags.length; k++) {
						wrTags.addAll(WRTagTaglet.splitTags(tags[k].text()));
					}
					MethodDoc[] methods = classes[i].methods();
					for(int l = 0; l < methods.length; l++) {
						Tag[] methodTags = methods[l].tags(WRTagTaglet.NAME);
						for(int m = 0; m < methodTags.length; m++) {
							wrTags.addAll(WRTagTaglet.splitTags(methodTags[m].text()));
						}						
					}
				}
			}
		}
		wrTags.size();
        Map<String, Set<String>> tagMap = new HashMap<String, Set<String>>();
        tagMap.put("wrTags", wrTags);
        
		this.configuration.getWriterFactory().getFreemarkerWriter().generateHtmlFile(
				this.configuration.getFreemarkerTemplateFilePath(), "wrTagIndex.ftl", 
				tagMap, this.configuration.destDirName);
	}
	
	@Override
	protected void generateClassFiles(ClassDoc[] arr, ClassTree classtree) {

		Arrays.sort(arr);
		for (int i = 0; i < arr.length; i++) {
			if (!(configuration.isGeneratedDoc(arr[i]) && arr[i].isIncluded())) {
				continue;
			}
			ClassDoc prev = (i == 0) ? null : arr[i - 1];
			ClassDoc curr = arr[i];
			ClassDoc next = (i + 1 == arr.length) ? null : arr[i + 1];
			try {
				if (curr.isAnnotationType()) {
					AbstractBuilder annotationTypeBuilder = configuration
							.getBuilderFactory().getAnnotationTypeBuilder(
									(AnnotationTypeDoc) curr, prev, next);
					annotationTypeBuilder.build();
				} else {
					AbstractBuilder classBuilder = configuration
							.getBuilderFactory().getClassBuilder(curr, prev,
									next, classtree);
					classBuilder.build();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new DocletAbortException();
			}
		}

	}

	@Override
	protected void generatePackageFiles(ClassTree arg0) throws Exception {
/*
		PackageDoc[] packages = configuration.packages;
		if (packages.length > 1) {
			PackageIndexFrameWriter.generate(configuration);
		}
		PackageDoc prev = null, next;
		for (int i = 0; i < packages.length; i++) {
			// if -nodeprecated option is set and the package is marked as
			// deprecated, do not generate the package-summary.html,
			// package-frame.html
			// and package-tree.html pages for that package.
			if (!(configuration.nodeprecated && Util.isDeprecated(packages[i]))) {
				PackageFrameWriter.generate(configuration, packages[i]);
				next = (i + 1 < packages.length && packages[i + 1].name()
						.length() > 0) ? packages[i + 1] : null;
				// If the next package is unnamed package, skip 2 ahead if
				// possible
				next = (i + 2 < packages.length && next == null) ? packages[i + 2]
						: next;
				AbstractBuilder packageSummaryBuilder = configuration
						.getBuilderFactory().getPackageSummaryBuilder(
								packages[i], prev, next);
				packageSummaryBuilder.build();
				if (configuration.createtree) {
					PackageTreeWriter.generate(configuration, packages[i],
							prev, next, configuration.nodeprecated);
				}
				prev = packages[i];
			}
		}
*/
	}

	public static boolean validOptions(String options[][],
			DocErrorReporter reporter) {
		return (ConfigurationImpl.getInstance())
				.validOptions(options, reporter);
	}

	public static int optionLength(String option) {
		return (ConfigurationImpl.getInstance()).optionLength(option);
	}

	public static void main(String[] args) {
		String[] docArgs = new String[] {
				"-doclet",
				HtmlDoclet.class.getName(),
				"-docletpath",
				"D:/winroad/workspace/wrdoclet/target/classes",
				"-taglet",
				WRTagTaglet.class.getName(),
				"-tagletpath",
				"D:/winroad/workspace/wrdoclet/target/classes",
				"-encoding",
				"utf-8",
				"-charset",
				"utf-8",
				"-sourcepath",
				"D:/workspaces/mtp_01.23release/mtp-web/src/main/java",
				"com.pinganfu.mtp.web.controller.p1",
				"com.pinganfu.mtp.web.controller.p1.packet",
				"org.springframework.web.bind.annotation",
				"-classpath",
				"C:/Users/AdamsLi/.m2/repository/org/springframework/spring-web/3.2.3.RELEASE/spring-web-3.2.3.RELEASE.jar",
				"-d", 
				"D:/winroad/mtpdoc" };
		com.sun.tools.javadoc.Main.execute(docArgs);
	}

}
