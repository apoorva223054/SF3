package com.nirvanaxp.types.entities;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:23.565+0530")
@StaticMetamodel(CdoMgmt.class)
public class CdoMgmt_ {
	public static volatile SingularAttribute<CdoMgmt, Integer> id;
	public static volatile SingularAttribute<CdoMgmt, String> cdoDescription;
	public static volatile SingularAttribute<CdoMgmt, String> cdoName;
	public static volatile SingularAttribute<CdoMgmt, Date> created;
	public static volatile SingularAttribute<CdoMgmt, String> createdBy;
	public static volatile SingularAttribute<CdoMgmt, Date> updated;
	public static volatile SingularAttribute<CdoMgmt, String> updatedBy;
	public static volatile SingularAttribute<CdoMgmt, BigInteger> versionNumber;
	public static volatile SingularAttribute<CdoMgmt, Integer> isLocationSpecific;
}
