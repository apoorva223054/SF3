package com.nirvanaxp.types.entities.catalog.items;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds_;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-11-15T16:17:17.176+0530")
@StaticMetamodel(Item.class)
public class Item_ extends POSNirvanaBaseClassWithoutGeneratedIds_ {
	public static volatile SingularAttribute<Item, String> courseId;
	public static volatile SingularAttribute<Item, String> description;
	public static volatile SingularAttribute<Item, String> hexCodeValues;
	public static volatile SingularAttribute<Item, Date> effectiveEndDate;
	public static volatile SingularAttribute<Item, String> startTime;
	public static volatile SingularAttribute<Item, String> endTime;
	public static volatile SingularAttribute<Item, Date> effectiveStartDate;
	public static volatile SingularAttribute<Item, String> imageName;
	public static volatile SingularAttribute<Item, Integer> isActive;
	public static volatile SingularAttribute<Item, Integer> isDeleted;
	public static volatile SingularAttribute<Item, Integer> isFeatured;
	public static volatile SingularAttribute<Item, Integer> isInStock;
	public static volatile SingularAttribute<Item, Integer> isRatingAllowed;
	public static volatile SingularAttribute<Item, Integer> isReviewAllowed;
	public static volatile SingularAttribute<Item, String> itemNumber;
	public static volatile SingularAttribute<Item, String> name;
	public static volatile SingularAttribute<Item, BigDecimal> priceMsrp;
	public static volatile SingularAttribute<Item, BigDecimal> priceSelling;
	public static volatile SingularAttribute<Item, Integer> ratingTotal;
	public static volatile SingularAttribute<Item, Integer> ratingVotesTotal;
	public static volatile SingularAttribute<Item, String> shortName;
	public static volatile SingularAttribute<Item, String> displayName;
	public static volatile SingularAttribute<Item, Integer> displaySequence;
	public static volatile SingularAttribute<Item, String> locationsId;
	public static volatile SingularAttribute<Item, Integer> itemType;
	public static volatile SingularAttribute<Item, Integer> isBelowThreashold;
	public static volatile SingularAttribute<Item, Integer> isRealTimeUpdateNeeded;
	public static volatile SingularAttribute<Item, String> itemGroupId;
	public static volatile SingularAttribute<Item, Integer> isOnlineItem;
	public static volatile SingularAttribute<Item, String> sellableUom;
	public static volatile SingularAttribute<Item, BigDecimal> purchasingRate;
	public static volatile SingularAttribute<Item, String> plu;
	public static volatile SingularAttribute<Item, Integer> isScanRequired;
	public static volatile SingularAttribute<Item, Integer> isManualQuantity;
	public static volatile SingularAttribute<Item, Integer> isManualPrice;
	public static volatile SingularAttribute<Item, BigDecimal> priceInclusiveTax;
	public static volatile SingularAttribute<Item, BigDecimal> yieldPercent;
	public static volatile SingularAttribute<Item, Integer> isOnlineDisplay;
	public static volatile SingularAttribute<Item, String> labelIngredients;
	public static volatile SingularAttribute<Item, String> storageTypeId;
	public static volatile SingularAttribute<Item, String> contains;
	public static volatile SingularAttribute<Item, String> leadTime;
	public static volatile SingularAttribute<Item, String> cutOffTime;
	public static volatile SingularAttribute<Item, BigDecimal> incentive;
	public static volatile SingularAttribute<Item, String> incentiveId;
	public static volatile SingularAttribute<Item, Boolean> availability;
	public static volatile SingularAttribute<Item, String> salesTax1;
	public static volatile SingularAttribute<Item, String> salesTax2;
	public static volatile SingularAttribute<Item, String> salesTax3;
	public static volatile SingularAttribute<Item, String> salesTax4;
	public static volatile SingularAttribute<Item, String> stockUom;
	public static volatile SingularAttribute<Item, Integer> inventoryAccrual;
	public static volatile SingularAttribute<Item, Integer> isinventoryAccrualOverriden;
	public static volatile SingularAttribute<Item, String> globalItemId;
	public static volatile SingularAttribute<Item, BigDecimal> distributionPrice;
	public static volatile SingularAttribute<Item, Integer> isWeighingScale;
}
