package com.nirvanaxp.types.entities.tip;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-05-22T13:18:02.852+0530")
@StaticMetamodel(TipPoolRules.class)
public class TipPoolRules_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<TipPoolRules, Integer> tipClassId;
	public static volatile SingularAttribute<TipPoolRules, Integer> tipPoolId;
	public static volatile SingularAttribute<TipPoolRules, String> name;
	public static volatile SingularAttribute<TipPoolRules, String> displayName;
	public static volatile SingularAttribute<TipPoolRules, String> orderSourceGroupId;
	public static volatile SingularAttribute<TipPoolRules, String> itemGroupId;
	public static volatile SingularAttribute<TipPoolRules, String> sectionId;
	public static volatile SingularAttribute<TipPoolRules, String> jobRoleId;
	public static volatile SingularAttribute<TipPoolRules, Integer> tipPoolBasisId;
	public static volatile SingularAttribute<TipPoolRules, BigDecimal> tipRate;
	public static volatile SingularAttribute<TipPoolRules, String> fromJobRoleId;
	public static volatile SingularAttribute<TipPoolRules, String> locationId;
	public static volatile SingularAttribute<TipPoolRules, Date> effectiveStartDate;
	public static volatile SingularAttribute<TipPoolRules, Date> effectiveEndDate;
}
