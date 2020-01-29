package com.nirvanaxp.services.packet;

/**
 * @author kris
 *
 */
public class PDFInHTMLFormatPacket {

	String fileName;

	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPdfData() {
		return pdfData;
	}

	public void setPdfData(String pdfData) {
		this.pdfData = pdfData;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public String getEmailFooter() {
		return emailFooter;
	}

	public void setEmailFooter(String emailFooter) {
		this.emailFooter = emailFooter;
	}

	
	String pdfData;

	String emailBody;

	String emailFooter;
	
	
}
