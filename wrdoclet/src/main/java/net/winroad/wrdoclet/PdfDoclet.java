package net.winroad.wrdoclet;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.winroad.wrdoclet.data.WRDoc;
import net.winroad.wrdoclet.utils.Util;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.sun.javadoc.RootDoc;

public class PdfDoclet extends HtmlDoclet{
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
			PdfDoclet doclet = new PdfDoclet();
			return doclet.start(doclet, root);
		} finally {
			ConfigurationImpl.reset();
		}
	}
	
	@Override
	protected void generateWRDocFiles(RootDoc root, WRDoc wrDoc) throws Exception {
		this.generateWRNoFrameFile(root, wrDoc);
		this.generateWRPdfFile(root, wrDoc);
	}
	
	protected void generateWRPdfFile(RootDoc root, WRDoc wrDoc) throws Exception {
		// step 1
		Document document = new Document();
		// step 2
		PdfWriter writer = PdfWriter.getInstance(
				document,
				new FileOutputStream(Util.combineFilePath(
						this.configuration.destDirName, "doc.pdf")));
		// step 3
		document.open();
		document.addCreationDate();
		document.addTitle(this.configuration.doctitle);

		// step 4
		XMLWorkerHelper.getInstance().parseXHtml(
				writer,
				document,
				new FileInputStream(Util.combineFilePath(
						this.configuration.destDirName,
						"index.html")),
				HtmlDoclet.class.getResourceAsStream("/css/stylesheet.css"),
				null, new AsianFontProvider());
		// step 5
		document.close();	
	}
	
}
