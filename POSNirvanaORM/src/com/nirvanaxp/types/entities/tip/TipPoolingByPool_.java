package com.nirvanaxp.types.entities.tip;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-25T11:19:12.499+0530")
@StaticMetamodel(TipPoolingByPool.class)
public class TipPoolingByPool_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<TipPoolingByPool, Integer> tipPoolId;
	public static volatile SingularAttribute<TipPoolingByPool, String> shiftId;
	public static volatile SingularAttribute<TipPoolingByPool, BigDecimal> cashTotal;
	public static volatile SingularAttribute<TipPoolingByPool, BigDecimal> cardTotal;
	public static volatile SingularAttribute<TipPoolingByPool, BigDecimal> creditTotal;
	public static volatile SingularAttribute<TipPoolingByPool, BigDecimal> directCashTip;
	public static volatile SingularAttribute<TipPoolingByPool, BigDecimal> directCardTip;
	public static volatile SingularAttribute<TipPoolingByPool, BigDecimal> directCreditTermTip;
	public static volatile SingularAttribute<TipPoolingByPool, BigDecimal> pendingCashTip;
	public static volatile SingularAttribute<TipPoolingByPool, BigDecimal> pendingCardTip;
	public static volatile SingularAttribute<TipPoolingByPool, BigDecimal> pendingCreditTip;
	public static volatile SingularAttribute<TipPoolingByPool, BigDecimal> indirectCashTip;
	public static volatile SingularAttribute<TipPoolingByPool, BigDecimal> indirectCardTip;
	public static volatile SingularAttribute<TipPoolingByPool, BigDecimal> indirectCreditTermTip;
	public static volatile SingularAttribute<TipPoolingByPool, String> nirvanaxpBatchId;
	public static volatile SingularAttribute<TipPoolingByPool, String> localTime;
	public static volatile SingularAttribute<TipPoolingByPool, String> sectionId;
	public static volatile SingularAttribute<TipPoolingByPool, String> orderSourceGroupId;
}
