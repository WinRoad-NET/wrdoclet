package net.winroad.wrdoclet;

import com.sun.tools.doclets.internal.toolkit.WriterFactory;

public interface WRWriterFactory extends WriterFactory {
	public FreemarkerWriter getFreemarkerWriter();
}
