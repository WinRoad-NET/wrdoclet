package net.winroad.wrdoclet.utils;

import java.io.IOException;
import java.io.InputStream;

public interface JSCompressor {
	String compress(InputStream inputStream, String inFileName, String resDestDir) throws IOException;
}
