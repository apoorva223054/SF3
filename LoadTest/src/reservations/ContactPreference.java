package reservations;

import java.io.Serializable;
import java.util.Date;

import com.nirvanaxp.server.util.NirvanaLogger;



/**
 * The persistent class for the contact_preferences database table.
 * 
 */
public class ContactPreference implements Serializable {
	private static final NirvanaLogger logger = new NirvanaLogger(ContactPreference.class.getName());
	private static final long serialVersionUID = 1L;

	private int id;

	private Date created;

	private int createdBy;

	private String displayName;

	private Integer displaySequence;

	private String locationsId;

	private String name;

	private Date updated;

	private int updatedBy;

	private String status;
	
	private String description;
	
	public ContactPreference(Object[] object, int startIndex) {
		try {
			if (object[startIndex] != null) {
				id = (Integer) object[startIndex];
			}
			startIndex++;
			if (object[startIndex] != null) {
				name = (String) object[startIndex];
			}
			startIndex++;
			if (object[startIndex] != null) {
				displayName = (String) object[startIndex];
			}
			startIndex++;
			if (object[startIndex] != null) {
				displaySequence = (Integer) object[startIndex];
			}
			startIndex++;
			if (object[startIndex] != null) {
				description = (String) object[startIndex];
			}
			startIndex++;
			if (object[startIndex] != null) {
				locationsId = (String) object[startIndex];
			}
			startIndex++;
			if (object[startIndex] != null) {
				status = (String) object[startIndex];
			}
			startIndex++;

			if (object[startIndex] != null) {
				created = ((Date) object[startIndex]);
			}
			startIndex++;
			if (object[startIndex] != null) {
				createdBy = (Integer) object[startIndex];
			}
			startIndex++;
			if (object[startIndex] != null) {
				updated = ((Date) object[startIndex]);
			}
			startIndex++;
			if (object[startIndex] != null) {
				updatedBy = (Integer) object[startIndex];
			}
		}catch(Exception e){
			 logger.severe(e);
		}

	}

	public ContactPreference() {
	}


	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Integer getDisplaySequence() {
		return this.displaySequence;
	}

	public void setDisplaySequence(Integer displaySequence) {
		this.displaySequence = displaySequence;
	}

	public String getLocationsId() {
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId) {
		this.locationsId = locationsId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}

}