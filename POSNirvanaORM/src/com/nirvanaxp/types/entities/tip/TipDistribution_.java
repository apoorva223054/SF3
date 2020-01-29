package com.nirvanaxp.types.entities.tip;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-25T11:14:08.625+0530")
@StaticMetamodel(TipDistribution.class)
public class TipDistribution_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<TipDistribution, String> userId;
	public static volatile SingularAttribute<TipDistribution, String> shiftId;
	public static volatile SingularAttribute<TipDistribution, BigDecimal> cashTotal;
	public static volatile SingularAttribute<TipDistribution, BigDecimal> cardTotal;
	public static volatile SingularAttribute<TipDistribution, BigDecimal> creditTotal;
	public static volatile SingularAttribute<TipDistribution, BigDecimal> directCashTip;
	public static volatile SingularAttribute<TipDistribution, BigDecimal> directCardTip;
	public static volatile SingularAttribute<TipDistribution, BigDecimal> directCreditTermTip;
	public static volatile SingularAttribute<TipDistribution, BigDecimal> indirectCashTip;
	public static volatile SingularAttribute<TipDistribution, BigDecimal> indirectCardTip;
	public static volatile SingularAttribute<TipDistribution, BigDecimal> indirectCreditTermTip;
	public static volatile SingularAttribute<TipDistribution, BigDecimal> batchSalary;
	public static volatile SingularAttribute<TipDistribution, String> nirvanaxpBatchId;
	public static volatile SingularAttribute<TipDistribution, String> localTime;
	public static volatile SingularAttribute<TipDistribution, BigDecimal> hourlyRate;
	public static volatile SingularAttribute<TipDistribution, BigDecimal> noOfHours;
	public static volatile SingularAttribute<TipDistribution, String> jobRoleId;
	public static volatile SingularAttribute<TipDistribution, String> sectionId;
	public static volatile SingularAttribute<TipDistribution, String> orderSourceGroupId;
}
