package net.winroad.wrdoclet.utils;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

public class JSCompressorErrorReporter implements ErrorReporter {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void warning(String message, String sourceName, int line,
			String lineSource, int lineOffset) {
		this.logger.warn("[" + sourceName + "@" + line + ", line offset:"
				+ lineOffset + "]" + message);
	}

	@Override
	public void error(String message, String sourceName, int line,
			String lineSource, int lineOffset) {
		this.logger.error("[" + sourceName + "@" + line + ", line offset:"
				+ lineOffset + "]" + message);
	}

	@Override
	public EvaluatorException runtimeError(String message, String sourceName,
			int line, String lineSource, int lineOffset) {
		return new EvaluatorException("[" + sourceName + "@" + line
				+ ", line offset:" + lineOffset + "]" + message);
	}
}
