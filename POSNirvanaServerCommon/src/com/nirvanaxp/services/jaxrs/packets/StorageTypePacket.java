package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.items.StorageType;

@XmlRootElement(name = "StorageTypePacket")
public class StorageTypePacket extends PostPacket{
	
	private StorageType storageType;

	public StorageType getStorageType() {
		return storageType;
	}

	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
	}

	@Override
	public String toString() {
		return "StorageTypePacket [storageType=" + storageType
				+ ", getMerchantId()=" + getMerchantId() + ", getClientId()="
				+ getClientId() + ", getLocationId()=" + getLocationId()
				+ ", getEchoString()=" + getEchoString() + ", getSchemaName()="
				+ getSchemaName() + ", getSessionId()=" + getSessionId()
				+ ", getIdOfSessionUsedByPacket()="
				+ getIdOfSessionUsedByPacket() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + "]";
	}
	

}
