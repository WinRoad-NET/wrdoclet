package net.winroad.wrdoclet.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class Util {
	private static Logger logger = LoggerFactory.getLogger(Util.class);
	private static YUICompressorWrapper compressor = new YUICompressorWrapper();
	
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
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(filename);
			int byteCount = 0;
			byte[] bytes = new byte[1024];
			while ((byteCount = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, byteCount);
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	public static void copyResourceFolder(String resourceFolder, String destDir)
			throws IOException {
		final File jarFile = new File(Util.class.getProtectionDomain()
				.getCodeSource().getLocation().getPath());
		if (jarFile.isFile()) { // Run with JAR file
			resourceFolder = StringUtils.strip(resourceFolder, "/");
			final JarFile jar = new JarFile(jarFile);
			// gives ALL entries in jar
			final Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry element = entries.nextElement();
				final String name = element.getName();
				// filter according to the path
				if (name.startsWith(resourceFolder + "/")) {
					String resDestDir = Util.combineFilePath(destDir,
							name.replaceFirst(resourceFolder + "/", ""));
					if (element.isDirectory()) {
						File newDir = new File(resDestDir);
						if (!newDir.exists()) {
							boolean mkdirRes = newDir.mkdirs();
							if (!mkdirRes) {
								logger.equals("Failed to create directory "
										+ resDestDir);
							}
						}
					} else {
						InputStream inputStream = null;
						try {
							inputStream = Util.class
									.getResourceAsStream("/" + name);
							if (inputStream == null) {
								logger.error("No resource is found:" + name);
							} else {
								Util.outputFile(inputStream, resDestDir);
							}
							/* TODO: compress js files
							inputStream = Util.class
									.getResourceAsStream("/" + name);
							compressor.compress(inputStream, name, destDir);
							*/
						} finally {
							if( inputStream != null) {
								inputStream.close();
							}
						}
					}
				}
			}
			jar.close();
		} else { // Run with IDE
			final URL url = Util.class.getResource(resourceFolder);
			if (url != null) {
				try {
					final File src = new File(url.toURI());
					File dest = new File(destDir);
					FileUtils.copyDirectory(src, dest);
				} catch (URISyntaxException ex) {
					// never happens
				}
			}
		}
	}

	public static String urlConcat(String url1, String url2) {
		if (url1 == null) {
			return url2;
		}
		if (url2 == null) {
			return url1;
		}
		if (!url1.endsWith("/") && !url2.startsWith("/")) {
			url1 = url1 + "/";
		}
		return url1 + url2;
	}

	public static String uncapitalize(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		int capitalIndex = -1;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) >= 'A' && str.charAt(i) <= 'Z') {
				capitalIndex = i;
			} else {
				break;
			}
		}
		if (capitalIndex < 0) {
			return str;
		} else if (capitalIndex == str.length() - 1) {
			return str.toLowerCase();
		} else {
			return str.substring(0, capitalIndex + 1).toLowerCase()
					+ str.substring(capitalIndex + 1);
		}
	}
}
