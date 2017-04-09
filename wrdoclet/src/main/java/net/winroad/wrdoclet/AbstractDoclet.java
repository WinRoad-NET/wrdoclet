package net.winroad.wrdoclet;

import net.winroad.wrdoclet.data.WRDoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.builders.AbstractBuilder;
import com.sun.tools.doclets.internal.toolkit.builders.BuilderFactory;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;

public abstract class AbstractDoclet {

	public AbstractDoclet() {
	}

	/**
	 * The global configuration information for this run.
	 */
	public Configuration configuration;

	/**
	 * The method that starts the execution of the doclet.
	 * 
	 * @param doclet
	 *            the doclet to start the execution for.
	 * @param root
	 *            the {@link com.sun.javadoc.RootDoc} that points to the source
	 *            to document.
	 * @return true if the doclet executed without error. False otherwise.
	 */
	public boolean start(AbstractDoclet doclet, RootDoc root) {
		configuration = configuration();
		configuration.root = root;
		try {
			doclet.startGeneration(root);
		} catch (Exception exc) {
			exc.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Indicate that this doclet supports the 1.5 language features.
	 * 
	 * @return JAVA_1_5, indicating that the new features are supported.
	 */
	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}

	/**
	 * Create the configuration instance and returns it.
	 * 
	 * @return the configuration of the doclet.
	 */
	public abstract Configuration configuration();

	/**
	 * Start the generation of files. Call generate methods in the individual
	 * writers, which will in turn genrate the documentation files. Call the
	 * TreeWriter generation first to ensure the Class Hierarchy is built first
	 * and then can be used in the later generation.
	 * 
	 * @see com.sun.javadoc.RootDoc
	 */
	private void startGeneration(RootDoc root) throws Exception {
		if (root.classes().length == 0) {
			configuration.message.error("doclet.No_Public_Classes_To_Document");
			return;
		}
		configuration.setOptions();
		configuration.getDocletSpecificMsg().notice("doclet.build_version",
				configuration.getDocletSpecificBuildDate());

		WRDoc wrDoc = new WRDoc(configuration);

		generateWRDocFiles(root, wrDoc);

		configuration.tagletManager.printReport();
	}

	protected abstract void generateWRDocFiles(RootDoc root, WRDoc wrDoc)
			throws Exception;

	/**
	 * Generate additional documentation that is added to the API documentation.
	 * 
	 * @param root
	 *            the RootDoc of source to document.
	 * @param classtree
	 *            the data structure representing the class tree.
	 * @throws Exception builderFactory may throw exception
	 */
	protected void generateOtherFiles(RootDoc root, ClassTree classtree)
			throws Exception {
		BuilderFactory builderFactory = configuration.getBuilderFactory();
		AbstractBuilder constantsSummaryBuilder = builderFactory
				.getConstantsSummaryBuider();
		constantsSummaryBuilder.build();
		AbstractBuilder serializedFormBuilder = builderFactory
				.getSerializedFormBuilder();
		serializedFormBuilder.build();
	}

	/**
	 * Generate the package documentation.
	 * 
	 * @param classtree
	 *            the data structure representing the class tree.
	 * @throws Exception blablabla
	 */
	protected abstract void generatePackageFiles(ClassTree classtree)
			throws Exception;

	/**
	 * Generate the class documentation.
	 * 
	 * @param classtree
	 *            the data structure representing the class tree.
	 * @param arr 
	 * 				classdocs.
	 */
	protected abstract void generateClassFiles(ClassDoc[] arr,
			ClassTree classtree);

	/**
	 * Iterate through all classes and construct documentation for them.
	 * 
	 * @param root
	 *            the RootDoc of source to document.
	 * @param classtree
	 *            the data structure representing the class tree.
	 */
	protected void generateClassFiles(RootDoc root, ClassTree classtree) {
		generateClassFiles(classtree);
		PackageDoc[] packages = root.specifiedPackages();
		for (int i = 0; i < packages.length; i++) {
			generateClassFiles(packages[i].allClasses(), classtree);
		}
	}

	/**
	 * Generate the class files for single classes specified on the command
	 * line.
	 * 
	 * @param classtree
	 *            the data structure representing the class tree.
	 */
	private void generateClassFiles(ClassTree classtree) {
		String[] packageNames = configuration.classDocCatalog.packageNames();
		for (int packageNameIndex = 0; packageNameIndex < packageNames.length; packageNameIndex++) {
			generateClassFiles(
					configuration.classDocCatalog
							.allClasses(packageNames[packageNameIndex]),
					classtree);
		}
	}
}
