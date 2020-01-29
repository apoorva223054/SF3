/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.sms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail_;

// TODO: Auto-generated Javadoc
/**
 * @author nirvanaxp
 *
 */
public class UploadFile
{

	/**  */
	private final static NirvanaLogger logger = new NirvanaLogger(UploadFile.class.getName());

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param locationId
	 * @param gatewayName
	 * @param date
	 * @param filename
	 * @param orderPaymentDetailId
	 * @param sessionId
	 * @param attachments
	 * @return
	 * @throws Exception
	 */
	public String fileUpload(HttpServletRequest httpRequest, String locationId, String gatewayName, String date, String filename, int orderPaymentDetailId, String sessionId, List<Attachment> attachments)
			throws Exception
	{

		EntityManager em = null;
		// todo -ap- need to handle specific exception
		try
		{

			PostPacket postPacket = new PostPacket();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, postPacket);

			// String datePattern = "????-??-??";
			// matching date pattern
			Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
			// todo -ap- need to handle exception in match date
			Matcher matcher = pattern.matcher(date);
			if (matcher.matches())
			{
				// save it
				String isFileUploadedSuccessfully = uploadFile(filename, postPacket.getMerchantId(), locationId, gatewayName, date, attachments);

				if (isFileUploadedSuccessfully != null)
				{
					OrderPaymentDetail orderPaymentDetail = getOrderPaymentDetailObjById(orderPaymentDetailId, em);
					orderPaymentDetail.setSignatureUrl(isFileUploadedSuccessfully);

					EntityTransaction tx = em.getTransaction();
					try
					{
						// start transaction
						tx.begin();
						em.merge(orderPaymentDetail);
						tx.commit();
					}
					catch (RuntimeException e)
					{
						// on error, if transaction active,
						// rollback
						if (tx != null && tx.isActive())
						{
							tx.rollback();
						}
						throw e;
					}

					return isFileUploadedSuccessfully;
				}
				else
				{
					return "Unable to upload file. Please try again later";
				}
			}
			else
			{
				throw new Exception("Invalid date format.Date Format must be yyyy-mm-dd");
			}

		}
		catch (Exception e)
		{

			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	// search payment object for which this file has been uploaded and enter the
	/**
	 * 
	 *
	 * @param orderPaymentDetailId
	 * @param em
	 * @return
	 * @throws Exception
	 */
	// image url path there
	private OrderPaymentDetail getOrderPaymentDetailObjById(int orderPaymentDetailId, EntityManager em) throws Exception
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderPaymentDetail> criteria = builder.createQuery(OrderPaymentDetail.class);
			Root<OrderPaymentDetail> root = criteria.from(OrderPaymentDetail.class);
			TypedQuery<OrderPaymentDetail> query = em.createQuery(criteria.select(root).where(builder.equal(root.get(OrderPaymentDetail_.id), orderPaymentDetailId)));
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			logger.severe(e, "Error during getItemsByItemNumber: ", e.getMessage());
			throw e;
		}
	}

	/**
	 * This is used to store uploaded image files. It will read upto 4k data at
	 * a time. The max allowed file size is controlled through the property
	 * MaxImageUploadSize in config file.
	 * 
	 * @param fileName
	 * @param merchantId
	 * @param locationId
	 * @param gatewayName
	 * @param date
	 * @param attachments
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String uploadFile(String fileName, String merchantId, String locationId, String gatewayName, String date, List<Attachment> attachments) throws FileNotFoundException, IOException
	{

		// change according to server when creating build
		// String serverPath = ConfigFileReader.getImageUploadPathFromFile();
		// todo -ap- need to handle exception, its not getting logged
		String serverPath = ConfigFileReader.getQRCodeUploadPathFromFile();
		if (serverPath != null)
		{
			String serverPhysicalPath = serverPath;
			String imagePath = "/images/SignatureFiles/" + merchantId + "/" + locationId + "/" + gatewayName + "/" + date + "/";
			new CommonMethods().createFileWithAllPermission(serverPhysicalPath + "/images/SignatureFiles/" + merchantId);
			new CommonMethods().createFileWithAllPermission(serverPhysicalPath + "/images/SignatureFiles/" + merchantId + "/" + locationId);
			new CommonMethods().createFileWithAllPermission(serverPhysicalPath + "/images/SignatureFiles/" + merchantId + "/" + locationId + "/" + gatewayName);
			new CommonMethods().createFileWithAllPermission(serverPhysicalPath + imagePath);
			String fileUploadPath = serverPhysicalPath + imagePath;
			for (Attachment attachment : attachments)
			{
				DataHandler handler = attachment.getDataHandler();
				InputStream stream = null;
				OutputStream out = null;
				try
				{
					stream = handler.getInputStream();
					MultivaluedMap<String, String> map = attachment.getHeaders();
					logger.info("Upload File's name from request:", getFileName(map));

					File file = new File(fileUploadPath + fileName + ".png");
					file.setExecutable(true);
					out = new FileOutputStream(file);
					logger.info("writing to :", file.getAbsolutePath());

					// read all into a buffer
					byte[] resultBuff = new byte[0];

					// read upto 4k at a time
					byte[] buff = new byte[4096];

					// variable to check for how much is read
					int read = -1;

					int maxImageUploadSize = ConfigFileReader.getMaxImageUploadSize();

					while ((read = stream.read(buff, 0, buff.length)) > -1)
					{
						// are we beyond max file size
						if (resultBuff.length > (maxImageUploadSize * 1024))
						{
							throw new IOException(MessageConstants.IMAGE_UPLOAD_MAX_SIZE_EXCEPTION);
						}

						// temp buffer = bytes already read + bytes last read
						byte[] tbuff = new byte[resultBuff.length + read];

						// copy previous bytes
						System.arraycopy(resultBuff, 0, tbuff, 0, resultBuff.length);

						// copy current lot
						System.arraycopy(buff, 0, tbuff, resultBuff.length, read);

						// call the temp buffer as your result buff
						resultBuff = tbuff;
					}

					out.write(resultBuff);
					// sett permission to file for read write and execute // by
					// Apoorva C
					new CommonMethods().setPermission(file.getAbsolutePath());
				}
				catch (Exception e)
				{
					logger.severe(e);

				}
				finally
				{
					if (stream != null)
					{
						stream.close();
					}

					if (out != null)
					{
						out.flush();
						out.close();
					}
				}
				return imagePath + fileName + ".png";
			}

			return null;

		}
		else
		{
			throw new IOException(MessageConstants.IMAGE_UPLOAD_PATH_EXCEPTION);
		}

	}

	/**
	 * 
	 *
	 * @param header
	 * @return
	 */
	private String getFileName(MultivaluedMap<String, String> header)
	{
		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
		for (String filename : contentDisposition)
		{
			if ((filename.trim().startsWith("filename")))
			{
				String[] name = filename.split("=");
				String exactFileName = name[1].trim().replaceAll("\"", "");
				return exactFileName;
			}
		}
		return "unknown";
	}

}
