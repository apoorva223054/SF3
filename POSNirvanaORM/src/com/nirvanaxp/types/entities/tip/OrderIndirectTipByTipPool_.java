package com.nirvanaxp.types.entities.tip;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-05T14:59:56.861+0530")
@StaticMetamodel(OrderIndirectTipByTipPool.class)
public class OrderIndirectTipByTipPool_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<OrderIndirectTipByTipPool, String> orderId;
	public static volatile SingularAttribute<OrderIndirectTipByTipPool, Integer> tipPoolId;
	public static volatile SingularAttribute<OrderIndirectTipByTipPool, BigDecimal> directCashTip;
	public static volatile SingularAttribute<OrderIndirectTipByTipPool, BigDecimal> directCardTip;
	public static volatile SingularAttribute<OrderIndirectTipByTipPool, BigDecimal> directCreditTermTip;
	public static volatile SingularAttribute<OrderIndirectTipByTipPool, BigDecimal> indirectCashTip;
	public static volatile SingularAttribute<OrderIndirectTipByTipPool, BigDecimal> indirectCardTip;
	public static volatile SingularAttribute<OrderIndirectTipByTipPool, BigDecimal> indirectCreditTermTip;
	public static volatile SingularAttribute<OrderIndirectTipByTipPool, BigDecimal> pendingCashTip;
	public static volatile SingularAttribute<OrderIndirectTipByTipPool, BigDecimal> pendingCardTip;
	public static volatile SingularAttribute<OrderIndirectTipByTipPool, BigDecimal> pendingCreditTip;
	public static volatile SingularAttribute<OrderIndirectTipByTipPool, String> localTime;
}
