package com.nirvanaxp.services.util.htmltopdf;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.allcolor.yahp.converter.CYaHPConverter;
import org.allcolor.yahp.converter.IHtmlToPdfTransformer;
import org.allcolor.yahp.converter.IHtmlToPdfTransformer.CHeaderFooter;
import org.allcolor.yahp.converter.IHtmlToPdfTransformer.PageSize;

import com.nirvanaxp.server.util.NirvanaLogger;


/**
 * A simple example to convert an URL pointing to an html file into a
 * PDF.
 *
 * @author Quentin Anciaux
 * @version 0.91
 */
public class CSimpleConversion {
	/** An handle to a yahp converter */
	private static CYaHPConverter converter = new CYaHPConverter();
	private static final NirvanaLogger logger = new NirvanaLogger(CSimpleConversion.class.getName());
	/**
	 * Start the Simple Conversion tool
	 *
	 * @param args startup arguments
	 *
	 * @throws Exception if an error occured while converting. exit
	 * 		   status = 0 if all is ok.
	 */
	public File convert(String  outfile, String htmlString, String htmlFooter)
		throws Exception {

		if (outfile == null) {
			showUsage("--out file must exists !");
		} // end if
		
		
		if (!outfile.endsWith(".pdf")) {
			outfile = outfile + ".pdf";
        }
		
		File fout = new File(outfile);
		FileOutputStream out = new FileOutputStream(fout);

	try {
			
		final PageSize A4P = new PageSize(21, 29.7, 1, 1, 0.5, 0.5); // 1184
		// x
		// 0832
		// pixels
		// (029.6
		// x
		// 020.8
		// cm)
		// (diagonal
		// 36.3
		// cm or
		// 15
		// inch)
		
			List<CHeaderFooter> headerFooterList = new ArrayList<CHeaderFooter>();
			System.out.println("before conversion");
			Map<String, Comparable> properties = new HashMap<String, Comparable>();
			headerFooterList.add(new IHtmlToPdfTransformer.CHeaderFooter(
					"<table width=\"100%\"><tbody><tr><td align=\"left\"></td><td align=\"right\">Page <pagenumber>/<pagecount></td></tr></tbody></table>",
					IHtmlToPdfTransformer.CHeaderFooter.HEADER));
			
			
			/*headerFooterList.add(new IHtmlToPdfTransformer.CHeaderFooter(
					htmlFooter,
					IHtmlToPdfTransformer.CHeaderFooter.FOOTER));
			*/
			
			
			properties.put(IHtmlToPdfTransformer.PDF_RENDERER_CLASS,
					IHtmlToPdfTransformer.FLYINGSAUCER_PDF_RENDERER);
			
			properties.put(IHtmlToPdfTransformer.PDF_ALLOW_PRINTING,
					true);
			
			properties.put(IHtmlToPdfTransformer.PDF_ALLOW_COPY,
					true);
			
		
			logger.severe("htmlString==========================================================================="+htmlString);
			logger.severe("headerFooterList==========================================================================="+headerFooterList);
			
			converter.convertToPdf(htmlString,
					A4P,
			          headerFooterList,
			          "file:\\http://localhost:8100/Font/pdf_styling.css", // root for relative external CSS and IMAGE
			          out,
			          properties);
			
			System.out.println("after conversion");
			
			out.flush();
			out.close();
		} // end try
		catch (final Throwable t) {
		logger.severe(t);
			System.err.println("An error occurs while converting '" + "' to '" + outfile + "'. Cause : " +
				t.getMessage());
		} // end catch

		return fout;
	} // end main()

	/**
	 * Return the value of the given parameter if set
	 *
	 * @param args startup arguments
	 * @param name parameter name
	 *
	 * @return the value of the given parameter if set or null
	 */
	private static String getParameter(
		final String args[],
		final String name) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(name)) {
				if ((i + 1) < args.length) {
					return args[i + 1];
				} // end if

				break;
			} // end if
		} // end for

		return null;
	} // end getParameter()

	/**
	 * return true if the given parameter is on the command line
	 *
	 * @param args startup arguments
	 * @param name parameter name
	 *
	 * @return true if the given parameter is on the command line
	 */
	private static boolean hasParameter(
		final String args[],
		final String name) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(name)) {
				return true;
			} // end if
		} // end for

		return false;
	} // end hasParameter()

	/**
	 * Show the usage of the tool
	 *
	 * @param message An error message
	 */
	private static void showUsage(final String message) {
		if (message != null) {
			System.out.println(message);
		} // end if

		System.out.println(
			"Usage :\n\tjava -cp yahp-sample.jar:yahp.jar org.allcolor.yahp.sample.CSimpleConversion" +
			" --url [http|file]://myuri --out /path/to.pdf [font options] [renderer options] [security options] [--help|-h]");
		System.out.println("\t[font options]:");
		System.out.println("\t\t[--fontpath directory where TTF font files are located]");
		System.out.println("\t[renderer options]:");
		System.out.println("\t(Default renderer use Flying Saucer XHTML renderer. no option.)");
		System.out.println("\t[security options]:");
		System.out.println("\t\t[--password password]");
		System.out.println("\t\t[--ks keystore file path]");
		System.out.println("\t\t[--kspassword keystore file password]");
		System.out.println("\t\t[--keypassword private key password]");
		System.out.println("\t\t[--cryptreason reason]");
		System.out.println("\t\t[--cryptlocation location]");
		if (message != null) {
			System.exit(-2);
		} else {
			System.exit(0);
		}
	} // end showUsage()
} // end CSimpleConversion