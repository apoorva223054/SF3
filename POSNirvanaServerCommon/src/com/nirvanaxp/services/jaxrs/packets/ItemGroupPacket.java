package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.items.ItemGroup;


/**
 * @author 
 * 
 * {
    "CatalogPacket": {
        "printerList": [
            {
                "id": 1
            },
            {
                "id": 3
            }
        ],
        "category": {
            "id": 4,
            "updated": 1399454658000,
            "updatedBy": 1778,
            "created": 1292101920000,
            "createdBy": 1778,
            "status": "I",
            "description": "Lunch Buffet ordering",
            "iconColour": "#5c5147",
            "isActive": 0,
            "isDeleted": 0,
            "name": "Buffet",
            "displayName": "Buffet",
            "imageName": "",
            "sortSequence": 1,
            "locationsId": 1,
            "categoryId": 0,
            "categoryToPrinters": null,
            "categoryToDiscounts": null
        }
    }
}
 *
 */
@XmlRootElement(name = "ItemGroupPacket")
public class ItemGroupPacket extends PostPacket {
	ItemGroup itemGroup;
	
	private int isBaseLocationUpdate;
	private String locationsListId;

	public String getLocationsListId() {
		return locationsListId;
	}

	public void setLocationsListId(String locationsListId) {
		this.locationsListId = locationsListId;
	}

	public int getIsBaseLocationUpdate() {
		return isBaseLocationUpdate;
	}

	public void setIsBaseLocationUpdate(int isBaseLocationUpdate) {
		this.isBaseLocationUpdate = isBaseLocationUpdate;
	}

	public ItemGroup getItemGroup() {
		return itemGroup;
	}

	public void setItemGroup(ItemGroup itemGroup) {
		this.itemGroup = itemGroup;
	}

	@Override
	public String toString() {
		return "ItemGroupPacket [ItemGroup=" + itemGroup + "]";
	}
	
}
