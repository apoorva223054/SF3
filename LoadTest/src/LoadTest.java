import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import reservations.ContactPreference;
import reservations.Location;
import reservations.RequestType;
import reservations.Reservation;
import reservations.ReservationPacket;
import reservations.ReservationsStatus;
import reservations.ReservationsType;

import com.nirvanaxp.server.util.NirvanaLogger;

public class LoadTest {

	static List<String> cookies;
	private static final NirvanaLogger logger = new NirvanaLogger(LoadTest.class.getName());
	public static void main(String args[]) {

//		loadTestReservation();
		String parameters = "username=Maulik&password=Mintbistro1&rolesId=1";
		testLogin(parameters);
//		testGetWithCookie();

	}

	static void testGetWithCookie() {

		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			CookieStore cookieStore = httpclient.getCookieStore();
			BasicClientCookie cookie = new BasicClientCookie("SessionId", "123");
			cookie.setDomain("192.168.2.18");
			cookie.setPath("/");
			// Prepare a request object
			HttpGet httpget = new HttpGet(
					"http://192.168.2.18:8080/GlobalAccountService/getAddressById/1");

			cookieStore.addCookie(cookie);
			httpclient.setCookieStore(cookieStore);

			// Execute the request
			HttpResponse response = httpclient.execute(httpget);
			 BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			  String line = "";
			  while ((line = rd.readLine()) != null) {
			   System.out.println(line);
			  }
			  httpclient.close();
		} catch (ClientProtocolException e) {
			
			 logger.severe(e);
		} catch (IOException e) {
			
			 logger.severe(e);
		}
		// Examine the response status

	}
	
	static void testReservationWithCookie(String params){
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			CookieStore cookieStore = httpclient.getCookieStore();
			BasicClientCookie cookie = new BasicClientCookie("SessionId", "f6f23dee6158806da7bae782e34dcd23");
			cookie.setDomain("localhost");
			cookie.setPath("/");
			// Prepare a request object
			HttpPost httpPost = new HttpPost(
					"http://localhost:8443/ReservationService/add");
			StringEntity stringEntity =new StringEntity(params);
			httpPost.addHeader("content-type", "application/json");
			httpPost.setEntity(stringEntity);
			cookieStore.addCookie(cookie);
			httpclient.setCookieStore(cookieStore);

			// Execute the request
			HttpResponse response = httpclient.execute(httpPost);
			 BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			  String line = "";
			  while ((line = rd.readLine()) != null) {
			   System.out.println(line);
			  }
			  httpclient.close();
		} catch (ClientProtocolException e) {
			
			 logger.severe(e);
		} catch (IOException e) {
			
			 logger.severe(e);
		}
	}

	static void testLogin(String requestPara) {
		try {

			URL url = null;
			HttpURLConnection connection = null;
			// Create connection
			url = new URL("http://192.168.2.18:8080/GlobalLoginService/login");

			// connection.setd
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(requestPara.getBytes().length));
			connection.setRequestProperty("charset", "utf-8");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write(requestPara);
			writer.flush();
			writer.close();

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				System.out.println("OK");
				cookies = connection.getHeaderFields().get("Set-Cookie");
			} else {
				System.out.println(connection.getResponseCode());
			}

			InputStream errorstream = connection.getErrorStream();

			BufferedReader br = null;
			if (errorstream == null) {
				InputStream inputstream = connection.getInputStream();
				br = new BufferedReader(new InputStreamReader(inputstream));
			} else {
				br = new BufferedReader(new InputStreamReader(errorstream));
			}
			String response = "";
			String nachricht;
			while ((nachricht = br.readLine()) != null) {
				response += nachricht;
			}
			System.out.println(response);
			// Send request

		} catch (Exception e) {
			System.out.println("Exception in JsonTestBean.sendRequestToURL : "
					+ e);
		}
	}

	static void loadTestReservation() {
		Reservation reservation = new Reservation();
		reservation.setReservationSource("BusinessApp");
		reservation.setLocation(new Location(692));
		reservation.setCreatedBy(14);
		reservation.setUpdatedBy(14);
		reservation.setCreated(new Long("1378968391000"));
		reservation.setUpdated(new Long("1378968391000"));
		reservation.setFirstName("Ketan");
		reservation.setLastName("Saxena");
		reservation.setPhoneNumber("123456789065");
		reservation.setPartySize(2);
		reservation.setDate("2013-08-30");
		reservation.setTime("11:40:30");
		reservation.setUsersId(14);

		RequestType requestType = new RequestType();
		requestType.setId(2);
		reservation.setRequestType(requestType);

		reservation.setReservationsType(new ReservationsType(1));

		ReservationsStatus reservationsStatus = new ReservationsStatus();
		reservationsStatus.setId(4);
		reservation.setReservationsStatus(reservationsStatus);
		ContactPreference contactPreference = new ContactPreference();
		contactPreference.setId(3);
		reservation.setContactPreference1(contactPreference);
		reservation.setLocationName("Mint");
		reservation.setReservationPlatform("Java");

		ReservationPacket reservationPacket = new ReservationPacket();
		reservationPacket.setSchemaName("pncore");
		reservationPacket.setEchoString("Echo");
		reservationPacket.setLocationId("1");
		reservationPacket.setMerchantId("55");
		reservationPacket.setReservation(reservation);
		reservationPacket.setClientId("test java");

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper
				.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		objectMapper
				.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
		String jsonToSendToServer;
		try {
			jsonToSendToServer = objectMapper
					.writeValueAsString(reservationPacket);
			jsonToSendToServer = "{ \"ReservationPacket\":"
					+ jsonToSendToServer + "}";

			testReservationWithCookie(jsonToSendToServer);
			/*for (int i = 0; i <= 500; i++) {
				System.out.println("i " + i);
				sendRequestToURL(jsonToSendToServer);
			}*/

		} catch (JsonGenerationException e) {
			
			 logger.severe(e);
		} catch (JsonMappingException e) {
			
			 logger.severe(e);
		} catch (IOException e) {
			
			 logger.severe(e);
		}

	}

	static String sendRequestToURL(String requestPara) {
		String responseString = "SUCCESS";
		try {

			URL url = null;
			HttpURLConnection connection = null;
			// Create connection
			url = new URL("http://localhost:8080/ReservationService/add");

			// connection.setd
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(requestPara.getBytes().length));
			connection.setRequestProperty("charset", "utf-8");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write(requestPara);
			writer.flush();
			writer.close();

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				System.out.println("OK");
			} else {
				System.out.println(connection.getResponseCode());
			}

			InputStream errorstream = connection.getErrorStream();

			BufferedReader br = null;
			if (errorstream == null) {
				InputStream inputstream = connection.getInputStream();
				br = new BufferedReader(new InputStreamReader(inputstream));
			} else {
				br = new BufferedReader(new InputStreamReader(errorstream));
			}
			String response = "";
			String nachricht;
			while ((nachricht = br.readLine()) != null) {
				response += nachricht;
			}
			System.out.println(response);
			// Send request

		} catch (Exception e) {
			responseString = "";
			System.out.println("Exception in JsonTestBean.sendRequestToURL : "
					+ e);
		}
		return responseString;
	}
}
