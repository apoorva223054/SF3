package reservations;



public class PostPacket {

	private String merchantId;

	private String clientId;

	private String locationId;

	private String echoString;
	
	private String schemaName;

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getEchoString() {
		return echoString;
	}

	public void setEchoString(String echoString) {
		this.echoString = echoString;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	@Override
	public String toString() {
		return "PostPacket [merchantId=" + merchantId + ", clientId="
				+ clientId + ", locationId=" + locationId + ", echoString="
				+ echoString + ", schemaName=" + schemaName + "]";
	}
	
	
	
}
