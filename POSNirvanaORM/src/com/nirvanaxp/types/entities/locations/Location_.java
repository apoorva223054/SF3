package com.nirvanaxp.types.entities.locations;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds_;
import com.nirvanaxp.types.entities.address.Address;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-06-24T12:56:23.440+0530")
@StaticMetamodel(Location.class)
public class Location_ extends POSNirvanaBaseClassWithoutGeneratedIds_ {
	public static volatile SingularAttribute<Location, Address> address;
	public static volatile SingularAttribute<Location, Integer> businessId;
	public static volatile SingularAttribute<Location, String> email;
	public static volatile SingularAttribute<Location, String> functionalCurrency;
	public static volatile SingularAttribute<Location, String> imageUrl;
	public static volatile SingularAttribute<Location, Integer> maxPointOfServiceNum;
	public static volatile SingularAttribute<Location, String> name;
	public static volatile SingularAttribute<Location, Float> posX;
	public static volatile SingularAttribute<Location, Float> posY;
	public static volatile SingularAttribute<Location, String> reportingCurrency;
	public static volatile SingularAttribute<Location, String> salesTax1;
	public static volatile SingularAttribute<Location, String> salesTax2;
	public static volatile SingularAttribute<Location, String> salesTax3;
	public static volatile SingularAttribute<Location, String> salesTax4;
	public static volatile SingularAttribute<Location, Integer> timezoneId;
	public static volatile SingularAttribute<Location, Integer> transactionalCurrencyId;
	public static volatile SingularAttribute<Location, String> locationsId;
	public static volatile SingularAttribute<Location, Integer> locationsTypeId;
	public static volatile SingularAttribute<Location, String> website;
	public static volatile SingularAttribute<Location, Integer> numberofpeople;
	public static volatile SingularAttribute<Location, Float> gratuity;
	public static volatile SingularAttribute<Location, Integer> floorTimer;
	public static volatile SingularAttribute<Location, String> preassignedServerId;
	public static volatile SingularAttribute<Location, Integer> businessTypeId;
	public static volatile SingularAttribute<Location, String> preassignedCashierId;
	public static volatile SingularAttribute<Location, String> managerUserId;
	public static volatile SingularAttribute<Location, Integer> seatWiseOrderMode;
	public static volatile SingularAttribute<Location, Integer> isCurrentlyMerged;
	public static volatile SingularAttribute<Location, Integer> isRoundOffRequired;
	public static volatile SingularAttribute<Location, Integer> isRealTimeInventoryRequired;
	public static volatile SingularAttribute<Location, Integer> isRealTimeProductUpdateRequired;
	public static volatile SingularAttribute<Location, Integer> isThirdPartyLocation;
	public static volatile SingularAttribute<Location, Integer> isLoggingRequired;
	public static volatile SingularAttribute<Location, Integer> isPaymentReceiptRequired;
	public static volatile SingularAttribute<Location, Integer> displayQrcode;
	public static volatile SingularAttribute<Location, Integer> isAdvanceReceipt;
	public static volatile SingularAttribute<Location, Integer> printBatchSettlement;
	public static volatile SingularAttribute<Location, Integer> printFeedback;
	public static volatile SingularAttribute<Location, Integer> isGlobalLocation;
	public static volatile SingularAttribute<Location, Integer> isPluScan;
	public static volatile SingularAttribute<Location, Integer> isOrderNumberSequencing;
	public static volatile SingularAttribute<Location, String> inventoryDeductionBusinessId;
	public static volatile SingularAttribute<Location, Integer> isTaxIncluding;
	public static volatile SingularAttribute<Location, String> taxRegNumber;
	public static volatile SingularAttribute<Location, Integer> isLogoPrintOnReceipt;
	public static volatile SingularAttribute<Location, Integer> isRecipeShowOnKds;
	public static volatile SingularAttribute<Location, Integer> isAttributePrinting;
	public static volatile SingularAttribute<Location, Integer> isCreditTermAllowed;
	public static volatile SingularAttribute<Location, Integer> isNegativeBalanceAllowed;
	public static volatile SingularAttribute<Location, String> preassignedServerName;
}
