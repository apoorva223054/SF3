package com.nirvanaxp.types.entities.tip;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-25T11:17:05.074+0530")
@StaticMetamodel(TipPoolingByOrder.class)
public class TipPoolingByOrder_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<TipPoolingByOrder, String> orderId;
	public static volatile SingularAttribute<TipPoolingByOrder, String> userId;
	public static volatile SingularAttribute<TipPoolingByOrder, BigDecimal> cashTotal;
	public static volatile SingularAttribute<TipPoolingByOrder, BigDecimal> cardTotal;
	public static volatile SingularAttribute<TipPoolingByOrder, BigDecimal> creditTotal;
	public static volatile SingularAttribute<TipPoolingByOrder, BigDecimal> directCashTip;
	public static volatile SingularAttribute<TipPoolingByOrder, BigDecimal> directCardTip;
	public static volatile SingularAttribute<TipPoolingByOrder, BigDecimal> directCreditTermTip;
	public static volatile SingularAttribute<TipPoolingByOrder, BigDecimal> indirectCashTipSubmited;
	public static volatile SingularAttribute<TipPoolingByOrder, BigDecimal> indirectCardTipSubmited;
	public static volatile SingularAttribute<TipPoolingByOrder, BigDecimal> indirectCreditTermTipSubmited;
	public static volatile SingularAttribute<TipPoolingByOrder, String> nirvanaxpBatchId;
	public static volatile SingularAttribute<TipPoolingByOrder, BigDecimal> pendingCashTip;
	public static volatile SingularAttribute<TipPoolingByOrder, BigDecimal> pendingCardTip;
	public static volatile SingularAttribute<TipPoolingByOrder, BigDecimal> pendingCreditTip;
	public static volatile SingularAttribute<TipPoolingByOrder, String> localTime;
}
