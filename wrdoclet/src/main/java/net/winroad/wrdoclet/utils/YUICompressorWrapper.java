package net.winroad.wrdoclet.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class YUICompressorWrapper implements JSCompressor {
	private Logger logger;
	private int lineBreakPosition = 2048;;
	private boolean munge = false;
	private boolean warn = false;
	private boolean preserveAllSemiColons = true;
	private boolean disableOptimizations = false;

	public YUICompressorWrapper() {
		this.logger = LoggerFactory.getLogger(this.getClass());
	}

	public YUICompressorWrapper(int lineBreakPosition, boolean munge,
			boolean warn, boolean preserveAllSemiColons,
			boolean disableOptimizations) {
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.lineBreakPosition = lineBreakPosition;
		this.munge = munge;
		this.warn = warn;
		this.preserveAllSemiColons = preserveAllSemiColons;
		this.disableOptimizations = disableOptimizations;
	}

	@Override
	public String compress(File inFile, String destDir) throws IOException {
		if (StringUtils.endsWith(inFile.getName(), ".js")
				&& !StringUtils.endsWith(inFile.getName(), ".min.js")) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(inFile);
				return this.compress(fis, inFile.getName(), destDir);
			} finally {
				if (fis != null) {
					fis.close();
				}
			}
		}
		return null;
	}

	@Override
	public String compress(InputStream inputStream, String inFileName,
			String resDestDir) throws IOException {
		if (StringUtils.endsWith(inFileName, ".js")
				&& !StringUtils.endsWith(inFileName, ".min.js")) {
			this.logger.debug("compressing " + inFileName);
			String outFileName = Util.combineFilePath(resDestDir,
					FilenameUtils.getBaseName(inFileName) + ".min.js");
			if (new File(outFileName).exists()) {
				this.logger
						.warn("File was already compressed and will be overwritten:"
								+ outFileName);
			}
			Reader inFile = null;
			OutputStreamWriter outFile = null;
			try {
				inFile = new InputStreamReader(inputStream, "UTF-8");
				outFile = new OutputStreamWriter(new FileOutputStream(
						outFileName), "UTF-8");
				JavaScriptCompressor compressor = new JavaScriptCompressor(
						inFile, new JSCompressorErrorReporter());
				compressor.compress(outFile, lineBreakPosition, munge, warn,
						preserveAllSemiColons, disableOptimizations);
			} finally {
				if (inFile != null) {
					inFile.close();
				}
				if (outFile != null) {
					outFile.close();
				}
			}
			return outFileName;
		}
		return null;
	}
}
