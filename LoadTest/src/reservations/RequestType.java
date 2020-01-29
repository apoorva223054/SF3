package reservations;

import java.io.Serializable;
import java.util.Date;

import com.nirvanaxp.server.util.NirvanaLogger;


/**
 * The persistent class for the request_type database table.
 * 
 */

public class RequestType implements Serializable {
	private static final NirvanaLogger logger = new NirvanaLogger(RequestType.class.getName());
	private static final long serialVersionUID = 1L;

	private int id;

	private Date created;

	private int createdBy;

	private String displayName;

	private Integer displaySequence;

	private String locationsId;

	private String requestName;

	private Date updated;

	private int updatedBy;
	
	private String status;
	
	private String description;
	
	
	public RequestType(Object object[],int index) {
		try {
			//47
			if (object[index] != null) {
				id = (Integer) object[index];
			}
			index++;
			if (object[index] != null) {
				requestName = (String) object[index];
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
				locationsId = (String) object[index];
			}
			index++;
			if (object[index] != null) {
				status = (String) object[index];
			}
			index++;
			if (object[index] != null) {
				created = ((Date) object[index]);
			}
			index++;
			if (object[index] != null) {
				createdBy = (Integer) object[index];
			}
			index++;
			if (object[index] != null) {
				updated = ((Date) object[index]);
			}
			index++;
			if (object[index] != null) {
				updatedBy = (Integer) object[index];
			}

		} catch (Exception e) {
			 logger.severe(e);
		}
	}
	
	public RequestType() {
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

	public String getRequestName() {
		return this.requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
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