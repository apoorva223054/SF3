package com.nirvanaxp.common.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.activemq.util.ByteArrayOutputStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;

public class Utilities {

	public static Date convertStringToDate(String testDate) throws ParseException{
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = formatter.parse(testDate);
		return date;
	}
	
	public static String convertDateToString(Date testDate) throws ParseException{
		SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sm.format(testDate);
		return date;
	}
	public static String getCurrentDateString() throws ParseException{
		DateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd");
		  //get current date time with Date()
		   Date date = new Date();
		   return dateFormat.format(date);
	}
	
	public static String getCurrentDateTimeString() throws ParseException{
		DateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  //get current date time with Date()
		   Date date = new Date();
		   return dateFormat.format(date);
	}
	public static String getCurrentTimeString() throws ParseException{
		DateFormat dateFormat  = new SimpleDateFormat("HH:mm:ss");
		  //get current date time with Date()
		   Calendar cal = Calendar.getInstance();
		   return dateFormat.format(cal.getTime());
	}

	public static String convertAllSpecialCharForSearch(String searchString ){
		if(searchString!=null && searchString.length()>0){
		String temp = StringEscapeUtils.escapeSql(searchString);
		return temp;
		}else {
		return searchString;	
		}
	}
	
	/*public String saveImage(String imageUrl, String destinationFile) throws Exception {
		URL url = new URL(imageUrl);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(destinationFile);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
//		String encodedString = Base64.getEncoder().encodeToString(b);
//		BufferedImage bufferedImage=ByteArrayToImage(b);
		return encodedString;
		
	}*/
	
/*	public String saveImage(String imageUrl, String destinationFile) throws Exception {
		URL url = new URL(imageUrl);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(destinationFile);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
//		String encodedString = Base64.getEncoder().encodeToString(b);
//		BufferedImage bufferedImage=ByteArrayToImage(b);
		return convertImageToBase64(image);
		
	}
	public BufferedImage ByteArrayToImage (byte args[]) throws Exception {
		      BufferedImage bImage = ImageIO.read(new File("sample.jpg"));
		      ByteArrayOutputStream bos = new ByteArrayOutputStream();
		      ImageIO.write(bImage, "jpg", bos );
		      byte [] data = bos.toByteArray();
		      ByteArrayInputStream bis = new ByteArrayInputStream(data);
		      BufferedImage bImage2 = ImageIO.read(bis);
		      ImageIO.write(bImage2, "jpg", new File("output.jpg") );
		      System.out.println("image created");
			return bImage2;
		}
	
	   public String convertImageToBase64(BufferedImage image) throws Exception {
	        // load file from /src/test/resources
	       // ClassLoader classLoader = getClass().getClassLoader();
		   
		   ByteArrayOutputStream bos = new ByteArrayOutputStream();
           BufferedImage bImage = new BufferedImage(
                   image.getWidth(),
                   image.getHeight(),
                   BufferedImage.TYPE_BYTE_BINARY);
           ImageIO.write(bImage, "jpg", bos );
           Graphics2D graphic = bImage.createGraphics();
           graphic.drawImage(image, 0, 0, Color.WHITE, null);
           graphic.dispose();
           
		   byte[] fileContent = bos.toByteArray();
	 
	        String encodedString = org.apache.commons.codec.binary.Base64.encodeBase64String(fileContent);
	       
	 
	         return encodedString;
	    }*/
	
}
