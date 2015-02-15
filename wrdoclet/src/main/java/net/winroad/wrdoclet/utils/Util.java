package net.winroad.wrdoclet.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Util {
	public static String combineFilePath(String path1, String path2) {
		return new File(path1, path2).toString();
	}

	public static boolean ensureDirectoryExist(String directoryName) {
		File theDir = new File(directoryName);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			return theDir.mkdir();
		}
		return false;
	}

	public static void outputFile(InputStream inputStream, String filename)
			throws IOException {
		File file = new File(filename);
		if (!file.exists()) {
			file.createNewFile();
		}
		OutputStream outputStream = new FileOutputStream(filename);
		int bytesWritten = 0;
		int byteCount = 0;
		byte[] bytes = new byte[1024];
		while ((byteCount = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, bytesWritten, byteCount);
			bytesWritten += byteCount;
		}
		inputStream.close();
		outputStream.close();
	}
}
