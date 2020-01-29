package com.nirvanaxp.types.entities.orders;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-10-04T12:34:24.287+0530")
@StaticMetamodel(BatchDetail.class)
public class BatchDetail_ {
	public static volatile SingularAttribute<BatchDetail, String> id;
	public static volatile SingularAttribute<BatchDetail, Date> startTime;
	public static volatile SingularAttribute<BatchDetail, Date> closeTime;
	public static volatile SingularAttribute<BatchDetail, String> updatedBy;
	public static volatile SingularAttribute<BatchDetail, String> locationId;
	public static volatile SingularAttribute<BatchDetail, Integer> isPrecapturedError;
	public static volatile SingularAttribute<BatchDetail, Integer> isBatchSettledError;
	public static volatile SingularAttribute<BatchDetail, String> status;
	public static volatile SingularAttribute<BatchDetail, String> localTime;
	public static volatile SingularAttribute<BatchDetail, String> isTipCalculated;
	public static volatile SingularAttribute<BatchDetail, String> dayOfYear;
}
