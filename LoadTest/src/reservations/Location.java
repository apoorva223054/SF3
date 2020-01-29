package reservations;

import java.io.Serializable;
import java.util.Date;

import com.nirvanaxp.server.util.NirvanaLogger;

/**
 * The persistent class for the locations database table.
 * 
 */
public class Location implements Serializable {
	private static final NirvanaLogger logger = new NirvanaLogger(Location.class.getName());
	private static final long serialVersionUID = 1L;

	private int id;

	private Address address;

	private int businessId;

	private Date created;

	private int createdBy;


	private String email;

	private String functionalCurrency;

	private String imageUrl;

	private int maxPointOfServiceNum;

	private String name;

	private float posX;

	private float posY;

	private String reportingCurrency;

	private float salesTaxRate1;

	private float salesTaxRate2;

	private float salesTaxRate3;

	private float salesTaxRate4;

	private Integer timezoneId;

	private Integer transactionalCurrencyId;

	private Date updated;

	private int updatedBy;

	private String locationsId;

	private int locationsTypeId;

	private String website;

	private String status;

	private int floorTimer;

	public Location(Object object[], int index) {
		try {
			// 24
			if (object[index] != null) {
				id = (Integer) object[index];
			}
			index++;
			if (object[index] != null) {
				name = (String) object[index];
			}
			index++;
			if (object[index] != null) {
				businessId = (Integer) object[index];
			}
			index++;
			if (object[index] != null) {
				locationsId = (String) object[index];
			}
			index++;
			if (object[index] != null) {
				locationsTypeId = (Integer) object[index];
			}
			index++;
			if (object[index] != null) {
				timezoneId = (Integer) object[index];
			}
			index++;

			if (object[index] != null) {
				address = new Address();
				address.setId((Integer) object[index]);
			}
			index++;
			try{
				if (object[index] != null) {
					boolean isFloorTimerOn = (Boolean) object[index];
					if(isFloorTimerOn ){
						floorTimer = 1;
					}else{
						floorTimer = 0;
					}
					
				}
			}catch(Exception e){
				
			}
			
			index++;
			if (object[index] != null) {
				status = (String) object[index];
			}
			index++;

			if (object[index] != null) {
				salesTaxRate1 = (Float) object[index];
			}
			index++;

			if (object[index] != null) {
				salesTaxRate2 = (Float) object[index];
			}
			index++;
			if (object[index] != null) {
				salesTaxRate3 = (Float) object[index];
			}
			index++;
			if (object[index] != null) {
				salesTaxRate4 = (Float) object[index];
			}
			index++;
			
			if (object[index] != null) {
				transactionalCurrencyId = (Integer) object[index];
			}
			index++;

			if (object[index] != null) {
				functionalCurrency = (String) object[index];
			}
			index++;

			if (object[index] != null) {
				reportingCurrency = (String) object[index];
			}
			index++;
			if (object[index] != null) {
				maxPointOfServiceNum = (Integer) object[index];
			}
			index++;
			if (object[index] != null) {
				posX = (Float) object[index];
			}
			index++;
			if (object[index] != null) {
				posY = (Float) object[index];
			}
			index++;
			if (object[index] != null) {
				imageUrl = (String) object[index];
			}
			index++;
			if (object[index] != null) {
				email = (String) object[index];
			}
			index++;
			if (object[index] != null) {
				website = (String) object[index];
			}
			index++;
			

			if (object[index] != null) {
				created =((Date) object[index]);
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

	public Location() {
	}

	public Location(int id) {
		this.id = id;
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

	public String getLocationsId() {
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId) {
		this.locationsId = locationsId;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public int getBusinessId() {
		return this.businessId;
	}

	public void setBusinessId(int businessId) {
		this.businessId = businessId;
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

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFunctionalCurrency() {
		return this.functionalCurrency;
	}

	public void setFunctionalCurrency(String functionalCurrency) {
		this.functionalCurrency = functionalCurrency;
	}

	public String getImageUrl() {
		return this.imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getMaxPointOfServiceNum() {
		return this.maxPointOfServiceNum;
	}

	public void setMaxPointOfServiceNum(int maxPointOfServiceNum) {
		this.maxPointOfServiceNum = maxPointOfServiceNum;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPosX() {
		return this.posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return this.posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public String getReportingCurrency() {
		return this.reportingCurrency;
	}

	public void setReportingCurrency(String reportingCurrency) {
		this.reportingCurrency = reportingCurrency;
	}

	public float getSalesTaxRate1() {
		return this.salesTaxRate1;
	}

	public void setSalesTaxRate1(float salesTaxRate1) {
		this.salesTaxRate1 = salesTaxRate1;
	}

	public float getSalesTaxRate2() {
		return this.salesTaxRate2;
	}

	public void setSalesTaxRate2(float salesTaxRate2) {
		this.salesTaxRate2 = salesTaxRate2;
	}

	public float getSalesTaxRate3() {
		return this.salesTaxRate3;
	}

	public void setSalesTaxRate3(float salesTaxRate3) {
		this.salesTaxRate3 = salesTaxRate3;
	}

	public float getSalesTaxRate4() {
		return this.salesTaxRate4;
	}

	public void setSalesTaxRate4(float salesTaxRate4) {
		this.salesTaxRate4 = salesTaxRate4;
	}

	public Integer getTimezoneId() {
		return timezoneId;
	}

	public void setTimezoneId(Integer timezoneId) {
		this.timezoneId = timezoneId;
	}

	public Integer getTransactionalCurrencyId() {
		return transactionalCurrencyId;
	}

	public void setTransactionalCurrencyId(Integer transactionalCurrencyId) {
		this.transactionalCurrencyId = transactionalCurrencyId;
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

	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public int getLocationsTypeId() {
		return locationsTypeId;
	}

	public void setLocationsTypeId(int locationsTypeId) {
		this.locationsTypeId = locationsTypeId;
	}

	public int getFloorTimer() {
		return floorTimer;
	}

	public void setFloorTimer(int floorTimer) {
		this.floorTimer = floorTimer;
	}

	public boolean equals(Location location) {
		if (location instanceof Location
				&& ((Location) location).getId() == this.id) {
			return true;
		} else {
			return false;
		}
	}

}