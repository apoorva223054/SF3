package reservations;

import java.io.Serializable;
import java.util.Date;

import com.nirvanaxp.server.util.NirvanaLogger;

/**
 * The persistent class for the reservations_status database table.
 * 
 */
public class ReservationsStatus implements Serializable {
	private static final NirvanaLogger logger = new NirvanaLogger(ReservationsStatus.class.getName());
	private static final long serialVersionUID = 1L;

	private int id;
	
	private Date created;

	private int createdBy;

	private String displayName;

	private Integer displaySequence;

	private String hexCodeValues;

	private String locationsId;

	private String name;

	private int showToCustomer;

	private Date updated;


	private int updatedBy;

	private String status;

	private String description;

	public ReservationsStatus(Object object[],int index) {
		try {
			//24
			if (object[index] != null) {
				id = (Integer) object[index];
			}
			index++;
			if (object[index] != null) {
				name = (String) object[index];
			}
			index++;
			if (object[index] != null) {
				displayName = (String) object[index];
			}
			index++;
			if (object[index] != null) {
				displaySequence = (Integer) object[index];
			}
			index++;
			if (object[index] != null) {
				description = (String) object[index];
			}
			index++;
			if (object[index] != null) {
				showToCustomer = (Integer) object[index];
			}
			index++;

			if (object[index] != null) {
				locationsId = (String) object[index];
			}
			index++;
			if (object[index] != null) {
				hexCodeValues = (String) object[index];
			}
			index++;
			if (object[index] != null) {
				status = (String) object[index];
			}
			index++;

			if (object[index] != null) {
				created = (Date) object[index];
			}
			index++;

			if (object[index] != null) {
				createdBy = (Integer) object[index];
			}
			index++;
			if (object[index] != null) {
				updated = (Date) object[index];
			}
			index++;
			if (object[index] != null) {
				updatedBy = (Integer) object[index];
			}

		} catch (Exception e) {
			 logger.severe(e);
		}
	}

	public ReservationsStatus() {
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

	public String getHexCodeValues() {
		return this.hexCodeValues;
	}

	public void setHexCodeValues(String hexCodeValues) {
		this.hexCodeValues = hexCodeValues;
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

	public int getShowToCustomer() {
		return this.showToCustomer;
	}

	public void setShowToCustomer(int showToCustomer) {
		this.showToCustomer = showToCustomer;
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