package reservations;

import java.io.Serializable;
import java.util.Date;

import com.nirvanaxp.server.util.NirvanaLogger;



/**
 * The persistent class for the reservations database table.
 * 
 */
public class Reservation implements Serializable {
	private static final NirvanaLogger logger = new NirvanaLogger(Reservation.class.getName());
	private static final long serialVersionUID = 1L;

	private int id;

	private String comment;

	private Date created;

	private int createdBy;

	private String date;

	private String email;

	private String firstName;

	private String lastName;

	private int partySize;

	private String phoneNumber;

	private String reservationPlatform;

	private String reservationSource;

	private String time;

	private Date updated;

	private int updatedBy;

	private int usersId;

	// uni-directional many-to-one association to ContactPreference
	private ContactPreference contactPreference2;

	private ContactPreference contactPreference1;

	// uni-directional many-to-one association to ContactPreference
	private ContactPreference contactPreference3;

	// uni-directional many-to-one association to Location
	private Location location;

	// uni-directional many-to-one association to RequestType
	private RequestType requestType;

	// uni-directional many-to-one association to ReservationsStatus
	private ReservationsStatus reservationsStatus;

	// uni-directional many-to-one association to ReservationsType
	private ReservationsType reservationsType;

	private transient String locationName;

	public Reservation(Object[] obj) {
		// the passed object[] must have data in this Item
		try {
			if (obj[0] != null) {
				setId((Integer) obj[0]);

			}
			if (obj[1] != null) {
				setDate((String) obj[1]);
			}
			if (obj[2] != null) {
				setTime((String) obj[2]);
			}
			if (obj[3] != null) {
				setPartySize((Integer) obj[3]);
			}
			if (obj[4] != null) {
				setFirstName((String) obj[4]);
			}
			if (obj[5] != null) {
				setLastName((String) obj[5]);
			}
			if (obj[6] != null) {
				setPhoneNumber((String) obj[6]);
			}
			if (obj[7] != null) {
				setEmail((String) obj[7]);
			}
			if (obj[8] != null) {
				setUsersId((Integer) obj[8]);
			}
			if (obj[9] != null) {
				//Location location = new Location(obj,91);
				setLocation(location);
			}

			if (obj[10] != null) {
				ReservationsType reservationsType = new ReservationsType(
						obj,37);
				setReservationsType(reservationsType);
			}
			if (obj[11] != null) {
				ContactPreference contactPreference1 = new ContactPreference(obj,58);//69
				setContactPreference1(contactPreference1);
			}
			if (obj[12] != null) {
				ContactPreference contactPreference2 = new ContactPreference(obj,69);
				contactPreference2.setId((Integer) obj[12]);
				setContactPreference2(contactPreference2);
			}
			if (obj[13] != null) {

				ContactPreference contactPreference3 = new ContactPreference(obj,80);
				contactPreference3.setId((Integer) obj[13]);
				setContactPreference3(contactPreference3);
			}
			if (obj[14] != null) {
				setComment((String) obj[14]);
			}
			if (obj[15] != null) {
				RequestType requestType = new RequestType(obj,47);
				setRequestType(requestType);
			}
			if (obj[16] != null) {
				ReservationsStatus reservationsStatus = new ReservationsStatus(obj,24);
				setReservationsStatus(reservationsStatus);
			}
			if (obj[17] != null) {
				setReservationSource((String) obj[17]);
			}
			if (obj[18] != null) {
				setReservationPlatform((String) obj[18]);
			}
			if (obj[19] != null) {
				created = (Date) obj[19];
			}
			if (obj[20] != null) {
				setCreatedBy((Integer) obj[20]);
			}
			if (obj[21] != null) {
				updated = ((Date) obj[21]);
			}
			if (obj[22] != null) {
				setUpdatedBy((Integer) obj[22]);
			}
			if (obj[23] != null) {
				setLocationName((String) obj[23]);
			}

		} catch (Exception e) {
			
			 logger.severe(e);
		}
	}

	public Reservation() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public long getCreated() {
		if(this.created !=null){
			return this.created.getTime();
		}
		return 0;
	}

	public void setCreated(long created) {
		if(created !=0){
			this.created =  new Date(created);
		}
		
	}

	public int getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getPartySize() {
		return this.partySize;
	}

	public void setPartySize(int partySize) {
		this.partySize = partySize;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getReservationPlatform() {
		return this.reservationPlatform;
	}

	public void setReservationPlatform(String reservationPlatform) {
		this.reservationPlatform = reservationPlatform;
	}

	public String getReservationSource() {
		return this.reservationSource;
	}

	public void setReservationSource(String reservationSource) {
		this.reservationSource = reservationSource;
	}

	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

public long getUpdated() {
		if(this.updated !=null){
			return this.updated.getTime();
		}
		return 0;
	}

	public void setUpdated(long updated) {
		if(updated !=0){
			this.updated = new Date(updated);
		}
	}

	public int getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public int getUsersId() {
		return this.usersId;
	}

	public void setUsersId(int usersId) {
		this.usersId = usersId;
	}

	public ContactPreference getContactPreference2() {
		 if(contactPreference2 != null && ( contactPreference2.equals("0"))){return null;}else{	return contactPreference2;}
	}

	public void setContactPreference2(ContactPreference contactPreference2) {
		this.contactPreference2 = contactPreference2;
	}

	public ContactPreference getContactPreference1() {
		 if(contactPreference1 != null && ( contactPreference1.equals("0"))){return null;}else{	return contactPreference1;}
	}

	public void setContactPreference1(ContactPreference contactPreference1) {
		this.contactPreference1 = contactPreference1;
	}

	public ContactPreference getContactPreference3() {
		 if(contactPreference3 != null && ( contactPreference3.equals("0"))){return null;}else{	return contactPreference3;}
	}

	public void setContactPreference3(ContactPreference contactPreference3) {
		this.contactPreference3 = contactPreference3;
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public RequestType getRequestType() {
		return this.requestType;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	public ReservationsStatus getReservationsStatus() {
		return this.reservationsStatus;
	}

	public void setReservationsStatus(ReservationsStatus reservationsStatus) {
		this.reservationsStatus = reservationsStatus;
	}

	public ReservationsType getReservationsType() {
		return this.reservationsType;
	}

	public void setReservationsType(ReservationsType reservationsType) {
		this.reservationsType = reservationsType;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

}