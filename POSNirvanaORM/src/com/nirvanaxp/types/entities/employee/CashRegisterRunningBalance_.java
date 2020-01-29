package com.nirvanaxp.types.entities.employee;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-10-11T11:59:28.004+0530")
@StaticMetamodel(CashRegisterRunningBalance.class)
public class CashRegisterRunningBalance_ extends POSNirvanaBaseClass_ {
	public static volatile SingularAttribute<CashRegisterRunningBalance, String> opdId;
	public static volatile SingularAttribute<CashRegisterRunningBalance, String> employeeOperationToCashRegisterId;
	public static volatile SingularAttribute<CashRegisterRunningBalance, BigDecimal> transactionAmount;
	public static volatile SingularAttribute<CashRegisterRunningBalance, BigDecimal> runningBalance;
	public static volatile SingularAttribute<CashRegisterRunningBalance, String> registerId;
	public static volatile SingularAttribute<CashRegisterRunningBalance, String> nirvanaXpBatchNumber;
	public static volatile SingularAttribute<CashRegisterRunningBalance, Integer> isAmountCarryForwarded;
	public static volatile SingularAttribute<CashRegisterRunningBalance, BigDecimal> carryForwardedBalance;
	public static volatile SingularAttribute<CashRegisterRunningBalance, String> transactionStatus;
	public static volatile SingularAttribute<CashRegisterRunningBalance, String> localTime;
}
