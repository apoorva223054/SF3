package com.nirvanaxp.common.methods;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.User;
import com.nirvanaxp.global.types.entities.partners.POSNPartners;
import com.nirvanaxp.global.types.entities.partners.POSNPartners_;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.locations.LocationSetting_;
import com.nirvanaxp.types.entities.locations.Location_;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.reservation.Reservation;

/**
 * @author XPERT
 *
 */
public class CommonMethods
{
	private final static NirvanaLogger logger = new NirvanaLogger(CommonMethods.class.getName());
	private static String salt = "6A98D3DC4E6FCA3B82A329A317F3A";
	private static int iterations = 65536;
	private static int keySize = 256;
	private static byte[] ivBytes;
	private static SecretKey secretKey;

	public Reservation getReservationById(HttpServletRequest httpRequest, EntityManager em, String id)
	{
		Reservation reservation = null;

		String selectClause = SQL_SELECT_CLAUSE;
		String queryString = selectClause + " from reservations r left join users u on u.id=r.users_id  LEFT JOIN reservations_status rs ON r.reservations_status_id=rs.id "
				+ "LEFT JOIN reservations_types rt ON r.reservation_types_id=rt.id LEFT JOIN request_type rqt ON r.request_type_id=rqt.id"
				+ " LEFT JOIN contact_preferences ctpref1 ON r.contact_preference_1=ctpref1.id LEFT JOIN contact_preferences ctpref2 ON r.contact_preference_2=ctpref2.id "
				+ "LEFT JOIN contact_preferences ctpref3 ON r.contact_preference_3=ctpref3.id LEFT JOIN locations loc ON r.locations_id=loc.id " + " WHERE r.id = ?   ";
		// + "and rs.name not in ('Void Walkin') ";

		Query query = em.createNativeQuery(queryString).setParameter(1, id);
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();

		if (resultList == null || resultList.size() < 0)
		{
			throw new NoResultException("No Reservation found by Id : " + id);
		}

		if (resultList.size() > 1)
		{
			throw new NonUniqueResultException("Found more than one reservation for id " + id);
		}

		for (Object[] objRow : resultList)
		{
			// if this has primary key not 0
			reservation = new Reservation(objRow);
		}
		return reservation;
	}

	// todo from apoorv :- how to manage this
	public PostPacket getPOSNPartner(HttpServletRequest httpRequest, PostPacket packet, String auth_token) throws FileNotFoundException, IOException, InvalidSessionException
	{
		EntityManager globalEm = null;
		try
		{
			globalEm = GlobalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = globalEm.getCriteriaBuilder();
			CriteriaQuery<POSNPartners> criteria = builder.createQuery(POSNPartners.class);
			Root<POSNPartners> root = criteria.from(POSNPartners.class);
			TypedQuery<POSNPartners> query = globalEm.createQuery(criteria.select(root).where(builder.equal(root.get(POSNPartners_.referenceNumber), auth_token)));

			POSNPartners posnPartner = query.getSingleResult();
			if (posnPartner != null)
				packet.setMerchantId(posnPartner.getAccountId() + "");
			return packet;
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEm);
		}

	}

	public void writeToFile(HttpServletRequest httpRequest, String content)
	{
		logger.severe(httpRequest, "Message in Payment Module: ", content);
		FileWriter writer = null;
		String filePath = "";
		try
		{
			filePath = ConfigFileReader.readPaymentLogsPathFromFile();
			writer = new FileWriter(filePath, true);
			writer.write(System.getProperty("line.separator"));
			Date date = new Date(new TimezoneTime().getGMTTimeInMilis());
			writer.write(date.toString() + " " + content);
		}
		catch (IOException e)
		{
			logger.severe(httpRequest, e, "Exception during writing to file: ", e.getMessage());
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (IOException e)
				{
					logger.severe(httpRequest, e, "Error closing the file : ", e.getMessage());
				}
			}
		}
	}

	public static String SQL_SELECT_CLAUSE = "select r.id as r_id,r.date as r_date ,r.time as r_time ,r.party_size as r_party_size,r.first_name as r_first_name,r.last_name as r_last_name,r.phone_number as r_phone_no,r.email as r_email,"
			+ "r.users_id as r_user_id,r.locations_id as r_location_id,r.reservation_types_id as r_reservation_status,r.contact_preference_1 as r_contact_prefrence_1,r.contact_preference_2 as r_contact_prefrence_2,r.contact_preference_3 as r_contact_prefrence_3,"
			+ "r.comment,r.request_type_id,r.reservations_status_id,r.reservation_source,r.reservation_platform,r.created,r.created_by,r.updated,"
			+ "r.updated_by as r_updated_by,u.visit_count as u_visitNumber,"
			+ " ( SELECT l.name  as l_name FROM order_header oh  left join locations l on l.id=oh.locations_id  where oh.reservations_id = r.id  order by oh.updated desc limit 0,1 ) as table_name, "
			+ "rs.id as rs_id,rs.name as rs_name,rs.display_name as rs_display_name,rs.display_sequence as rs_display_sequence,rs.description as rs_description,rs.show_to_customer  as rs_show_to_customer,rs.locations_id as rs_location_id,rs.hex_code_values as rs_hex_code_values,"
			+ "rs.status as rs_status,rs.created as rs_created,rs.created_by as rs_created_by,rs.updated as rs_updated ,rs.updated_by as rs_updated_by,rs.is_server_driven as rs_is_server_driven, "
			+ "rt.id as rt_id,rt.name as rt_name,rt.display_name as rt_display_name,rt.display_sequence as rt_display_sequence,rt.locations_id as rt_locations_id,rt.status as rt_status,rt.created as rt_created,rt.created_by as rt_created_by,rt.updated as rt_updated,rt.updated_by as rt_updated_by,"
			+ "rqt.id as rqt_id,rqt.request_name as rqt_request_name,rqt.display_name as rqt_display_name,rqt.display_sequence as rqt_display_sequence,rqt.description as rqt_description,rqt.locations_id as rqt_location_id,rqt.status as rqt_status,rqt.created as rqt_created,rqt.created_by as rqt_created_by,rqt.updated as rqt_updated,rqt.updated_by as rqt_updated_by,"
			+ "ctpref1.id as ctpref1_id,ctpref1.name as ctpref1_name,ctpref1.display_name as ctpref1_display_name,ctpref1.display_sequence as ctpref1_display_sequence,ctpref1.description as ctpref1_description,ctpref1.locations_id as ctpref1_location_id,ctpref1.status as ctpref1_status,ctpref1.created as ctpref1_created,ctpref1.created_by as ctpref1_created_by,ctpref1.updated as ctpref1_updated,ctpref1.updated_by as ctpref1_updated_by,"
			+ "ctpref2.id as ctpref2_id,ctpref2.name as ctpref2_name,ctpref2.display_name as ctpref2_display_name,ctpref2.display_sequence as ctpref2_display_sequence,ctpref2.description as ctpref2_description,ctpref2.locations_id as ctpref2_location_id,ctpref2.status as ctpref2_status,ctpref2.created as ctpref2_created,ctpref2.created_by as ctpref2_created_by,ctpref2.updated as ctpref2_updated,ctpref2.updated_by as ctpref2_updated_by,"
			+ "ctpref3.id as ctpref3_id,ctpref3.name as ctpref3_name,ctpref3.display_name as ctpref3_display_name,ctpref3.display_sequence as ctpref3_display_sequence,ctpref3.description as ctpref3_description,ctpref3.locations_id as ctpref3_location_id,ctpref3.status as ctpref3_status,ctpref3.created as ctpref3_created,ctpref3.created_by as ctpref3_created_by,ctpref3.updated as ctpref3_updated,ctpref3.updated_by as ctpref3_updated_by,"
			+ "loc.id as loc_id,r.pre_assigned_location_id as r_pre_assigned_location_id, rs.is_server_driven, r.contact_preference_1 as r_contact_preference_1,r.contact_preference_2 as r_contact_preference_2,r.contact_preference_3 as r_contact_preference_3,r.locations_id as r_locations_id, r.request_type_id as r_request_type_id, r.reservations_status_id as r_reservationsStatusId , r.reservation_types_id as r_reservationsTypeId, r.session_id as r_session_id , r.reservation_slot_id as r_reservation_slot_id ,(select count(*) from order_header where reservations_id=r.id) as r_is_order_present,r.business_comment as r_business_comment  ";

	public boolean generateQrcode(String myCodeText, String filePath, String folderPath) throws IOException, WriterException
	{

		boolean result = false;
		String sizeqr = null;
		// removed try catch by Apoorva 04-01-18
		sizeqr = ConfigFileReader.getQrcodeSize();

		int size = Integer.parseInt(sizeqr);
		String fileType = "png";
		File myFile = new File(filePath);
		File folder = new File(folderPath);

		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
		int CrunchifyWidth = byteMatrix.getWidth();
		BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth, BufferedImage.TYPE_INT_RGB);
		image.createGraphics();

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
		graphics.setColor(Color.BLACK);

		for (int i = 0; i < CrunchifyWidth; i++)
		{
			for (int j = 0; j < CrunchifyWidth; j++)
			{
				if (byteMatrix.get(i, j))
				{
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		myFile.setReadable(true);
		myFile.setWritable(true);
		myFile.setExecutable(true);

		// todo apoorv how to handle exception
		// removed try catch by Apoorva 04-01-18
		result = ImageIO.write(image, fileType, myFile);

		setPermission(myFile.getAbsolutePath());
		return result;
	}

	public boolean createQRCodeForUser(EntityManager em, com.nirvanaxp.global.types.entities.User user, HttpServletRequest httpRequest) throws FileNotFoundException, IOException, WriterException
	{

		String basePath = ConfigFileReader.getQRCodeUploadPathFromFile();
		String basePath2 = ConfigFileReader.getUserQRCodeUploadPath();

		String folderPath = basePath + basePath2;
		String fileName = "" + user.getFirstName() + "_" + user.getLastName() + user.getId();
		String path = folderPath + fileName;
		// creating data for qrcode
		com.nirvanaxp.global.types.entities.User user2 = new User();
		user2.setFirstName(user.getFirstName());
		user2.setLastName(user.getLastName());
		user2.setPhone(user.getPhone());
		user2.setEmail(user.getEmail());
		user2.setId(user.getId());

		String codeText = new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValuesAndNotDefault(user2);
		boolean result = new CommonMethods().generateQrcode(codeText, path + ".png", folderPath);
		if (result)
		{
			user.setQrCodePath(basePath2 + fileName);
			em.merge(user);
		}
		return result;

	}

	public String arrayToString(List<Object> list)
	{
		String value = "";
		for (int i = 0; i < list.size(); i++)
		{
			if (i == list.size() - 1)
			{
				value += value;
			}
			else
			{
				value += value + ",";
			}
		}
		return value;
	}

	public Location getBaseLocation(EntityManager em)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Location> criteria = builder.createQuery(Location.class);
		Root<Location> r = criteria.from(Location.class);
		TypedQuery<Location> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Location_.isGlobalLocation), 1)));
		return query.getSingleResult();

	}
	public String getLocationId(EntityManager em){
		return getBaseLocation(em).getId();
	}

	public OrderStatus getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(EntityManager em, String name, String locationId, String orderSourceGroupId)
	{

		String queryString = "select os from OrderStatus os  where os.name in (?) and  os.locationsId= ? and os.orderSourceGroupId=? ";
		TypedQuery<OrderStatus> query = em.createQuery(queryString, OrderStatus.class).setParameter(1, name).setParameter(2, locationId).setParameter(3, orderSourceGroupId);

		OrderStatus resultSet = query.getSingleResult();

		return resultSet;
	}

	public List<String> getAllActiveLocations(HttpServletRequest httpRequest, EntityManager em)
	{
		String queryString = "select l.id from locations l  join locations_type lt  on l.locations_type_id=lt.id "
				+ " where (l.locations_id=0 or l.locations_id is null) and  lt.name='grouping' and l.is_global_location=0 and l.status!='D' ";

		Query query = em.createNativeQuery(queryString);
		return ((List<String>) query.getResultList());
	}

	public static String encrypt(char[] plaintext) throws Exception
	{
		byte[] saltBytes = salt.getBytes();

		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(plaintext, saltBytes, iterations, keySize);
		secretKey = skf.generateSecret(spec);
		SecretKeySpec secretSpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretSpec);
		AlgorithmParameters params = cipher.getParameters();
		ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
		byte[] encryptedTextBytes = cipher.doFinal(String.valueOf(plaintext).getBytes("UTF-8"));

		return DatatypeConverter.printBase64Binary(encryptedTextBytes);
	}

	public static String decrypt(char[] encryptedText) throws Exception
	{

		System.out.println(encryptedText);

		byte[] encryptedTextBytes = DatatypeConverter.parseBase64Binary(new String(encryptedText));
		SecretKeySpec secretSpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretSpec, new IvParameterSpec(ivBytes));

		byte[] decryptedTextBytes = null;

		try
		{
			decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
		}
		catch (IllegalBlockSizeException e)
		{
			logger.severe(e);
		}
		catch (BadPaddingException e)
		{
			logger.severe(e);
		}

		return new String(decryptedTextBytes);

	}

	public static String getSalt() throws Exception
	{

		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[20];
		sr.nextBytes(salt);
		return new String(salt);
	}

	private void writeToFile(HttpServletRequest httpRequest, String content, String filePath)
	{
		logger.severe(httpRequest, "Message in Payment Module: ", content);
		FileWriter writer = null;
		try
		{
			if (filePath == null)
			{
				filePath = ConfigFileReader.readPaymentLogsPathFromFile();
			}
			writer = new FileWriter(filePath, true);
			writer.write(System.getProperty("line.separator"));
			Date date = new Date(new TimezoneTime().getGMTTimeInMilis());
			writer.write(date.toString() + " " + content);
		}
		catch (IOException e)
		{
			logger.severe(httpRequest, e, "Exception during writing to file: ", e.getMessage());
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (IOException e)
				{
					logger.severe(httpRequest, e, "Error closing the file : ", e.getMessage());
				}
			}
		}
	}

	public void setPermission(String filePath) throws IOException
	{
		Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
		// add owners permission
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_EXECUTE);
		// add group permissions
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_WRITE);
		perms.add(PosixFilePermission.GROUP_EXECUTE);
		// add others permissions
		perms.add(PosixFilePermission.OTHERS_READ);
		perms.add(PosixFilePermission.OTHERS_WRITE);
		perms.add(PosixFilePermission.OTHERS_EXECUTE);

		Files.setPosixFilePermissions(Paths.get(filePath), perms);
	}

	public File createFileWithAllPermission(String filePath)
	{
		File nFile = new File(filePath);
		// setting permission of folder changed by Ap 04-01-18
		try
		{
			if (!nFile.exists())
			{
				nFile.mkdir();
				setPermission(filePath);
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return nFile;
	}

	public String batchIdString(EntityManager em, String locationId, String sDate, String eDate)
	{
		String batchIds = "";
		TimezoneTime timezoneTime = new TimezoneTime();
		String startDate = timezoneTime.getDateAccordingToGMT(sDate + " 00:00:00", locationId, em);
		String endDate = timezoneTime.getDateAccordingToGMT(eDate + " 23:59:59", locationId, em);

		String queryString = " SELECT id FROM batch_detail " + "  where (startTime >= ? and startTime <= ?) " + " or (startTime <=? and closetime>=?)";
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, startDate).setParameter(2, endDate).setParameter(3, startDate).setParameter(4, endDate).getResultList();

		for (int i = 0; i < resultList.size(); i++)
		{
			if (i == (resultList.size() - 1))
			{
				batchIds += "'" + resultList.get(i) + "'";
			}
			else
			{
				batchIds += "'" + resultList.get(i) + "'" + ",";
			}
		}

		return batchIds;
	}

	public LocationSetting getAllLocationSettingByLocationId(EntityManager em, String locationId)
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<LocationSetting> criteria = builder.createQuery(LocationSetting.class);
			Root<LocationSetting> r = criteria.from(LocationSetting.class);
			TypedQuery<LocationSetting> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(LocationSetting_.locationId), locationId), builder.notEqual(r.get(LocationSetting_.status), "D")));
			return (LocationSetting) query.getSingleResult();
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return null;
	}

	public String shortURLWithTinyURL(String fullURL) throws IOException
	{
		String url = "http://tinyurl.com/api-create.php?url=" + fullURL;
		return sendGET(url);
	}

	private static String sendGET(String url) throws IOException
	{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK)
		{ // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null)
			{
				response.append(inputLine);
			}
			in.close();

			// print result
			return response.toString();
		}
		else
		{
			return responseCode + "";
		}

	}

 
	
	@SuppressWarnings("unchecked")
	public Object getObjectById(String objClass,EntityManager em,Class resultClass,String id){
		
		if(id!=null && id.length()>0){
			try {
				logger.severe(objClass+"=Id ==============================================================="+id);
				
				String queryString = "select l from "+objClass+" l where l.id =? ";
				return em.createQuery(queryString, resultClass).setParameter(1,id).getSingleResult();
			} catch (Exception e) {
				logger.severe(e);
			}
		}else{
			logger.severe(objClass+"=Id cannot be null or blank==============================================================="+id);
		}
		
		return null;
	}	
		
 
}