package net.winroad.wrdoclet.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface JSCompressor {
	String compress(File inFile, String destDir) throws IOException;

	String compress(InputStream inputStream, String inFileName,
			String resDestDir) throws IOException;
}
