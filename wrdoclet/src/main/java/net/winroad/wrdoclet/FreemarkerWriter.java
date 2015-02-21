
package net.winroad.wrdoclet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import net.winroad.wrdoclet.utils.Logger;
import net.winroad.wrdoclet.utils.LoggerFactory;
import net.winroad.wrdoclet.utils.Util;

import org.apache.commons.io.IOUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreemarkerWriter {
	protected Logger logger;
	protected final static String DEFAULT_ENCODING = "UTF-8";
	protected final static Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;
	protected final static String freemarkerTemplateDefaultFilePath = "/ftl";

	public FreemarkerWriter() {
		this.logger = LoggerFactory.getLogger(this.getClass());
	}

	public boolean generateHtmlFile(String tmplFilePath, String tmplFileName,
			@SuppressWarnings("rawtypes") Map propMap, String outputPath,
			String outputFileName) {
		Writer outputWriter = null;
		try {
			Template tmpl = this.getTemplate(tmplFilePath, tmplFileName);
			Util.ensureDirectoryExist(outputPath);
			File outputDir = new File(outputPath);
			File htmlFile = new File(outputDir,
					outputFileName == null || outputFileName.isEmpty() ? this
							.mapTmplToHtmlFileName(tmplFileName)
							: outputFileName);
			outputWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(htmlFile), DEFAULT_ENCODING));
			tmpl.process(propMap, outputWriter);
		} catch (Exception e) {
			this.logger.error(e);
		} finally {
			IOUtils.closeQuietly(outputWriter);
		}

		return false;
	}

	protected String mapTmplToHtmlFileName(String tmplFileName) {
		return tmplFileName.replaceAll(".ftl$", ".html");
	}

	protected Template getTemplate(String tmplFilePath, String tmplFileName)
			throws IOException {
		Configuration cfg = new Configuration();
		cfg.setEncoding(DEFAULT_LOCALE, DEFAULT_ENCODING);
		if (tmplFilePath == null) {
			this.logger.debug("reading ftl " + tmplFileName
					+ " from class path " + freemarkerTemplateDefaultFilePath);
			cfg.setClassForTemplateLoading(this.getClass(),
					freemarkerTemplateDefaultFilePath);
		} else {
			this.logger.debug("reading ftl " + tmplFileName + " from path "
					+ tmplFilePath);
			cfg.setDirectoryForTemplateLoading(new File(tmplFilePath));
		}
		Template tmpl = cfg.getTemplate(tmplFileName);
		tmpl.setEncoding(DEFAULT_ENCODING);
		return tmpl;
	}
}
