//package net.winroad.wrdoclet;
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//
//import net.winroad.wrdoclet.data.WRDoc;
//import net.winroad.wrdoclet.utils.Util;
//
//import com.itextpdf.text.Document;
//import com.itextpdf.text.pdf.PdfWriter;
//import com.itextpdf.tool.xml.XMLWorkerHelper;
//import com.sun.javadoc.RootDoc;
//
//public class PdfDoclet extends HtmlDoclet{
//	/**
//	 * The "start" method as required by Javadoc.
//	 * 
//	 * @param root
//	 *            the root of the documentation tree.
//	 * @see com.sun.javadoc.RootDoc
//	 * @return true if the doclet ran without encountering any errors.
//	 */
//	public static boolean start(RootDoc root) {
//		try {
//			PdfDoclet doclet = new PdfDoclet();
//			return doclet.start(doclet, root);
//		} finally {
//			ConfigurationImpl.reset();
//		}
//	}
//	
//	@Override
//	protected void generateWRDocFiles(RootDoc root, WRDoc wrDoc) throws Exception {
//		this.generateWRNoFrameFile(root, wrDoc);
//		this.generateWRPdfFile(root, wrDoc);
//	}
//	
//	protected void generateWRPdfFile(RootDoc root, WRDoc wrDoc) throws Exception {
//		Document document = null;
//		PdfWriter writer = null;
//		FileOutputStream fos = null;
//		FileInputStream fis = null;
//		InputStream rs = null;
//
//		try {
//			// step 1
//			document = new Document();
//			
//			// step 2
//			fos = new FileOutputStream(Util.combineFilePath(
//					this.configurationEx.destDirName, "doc.pdf"));
//			writer = PdfWriter.getInstance(document, fos);
//			
//			// step 3
//			document.open();
//			document.addCreationDate();
//			document.addTitle(this.configurationEx.doctitle);
//	
//			// step 4
//			fis = new FileInputStream(Util.combineFilePath(
//					this.configurationEx.destDirName,
//					"index.html"));
//			rs = HtmlDoclet.class.getResourceAsStream("/css/stylesheet.css");
//			XMLWorkerHelper.getInstance().parseXHtml(
//					writer,
//					document,
//					fis,
//					rs,
//					null, new AsianFontProvider());
//		} finally {
//			// step 5
//			if(document != null) {
//				document.close();
//			}
//			if(writer != null) {
//				writer.close();
//			}
//			if(fos != null) {
//				fos.close();
//			}
//			if(fis != null) {
//				fis.close();
//			}
//			if(rs != null) {
//				rs.close();
//			}
//		}
//	}
//	
//}
