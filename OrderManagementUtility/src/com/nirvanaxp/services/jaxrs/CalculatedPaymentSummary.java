package com.nirvanaxp.services.jaxrs;

import java.math.BigDecimal;

public class CalculatedPaymentSummary
{
	BigDecimal totalCashAmmount;
	BigDecimal totalCardAmmount;
	BigDecimal totalCreditTermAmmount;
	BigDecimal cashTipAmount;
	BigDecimal cardTipAmount;
	BigDecimal creditTermTipAmount;
	public BigDecimal getTotalCashAmmount()
	{
		return totalCashAmmount;
	}
	public void setTotalCashAmmount(BigDecimal totalCashAmmount)
	{
		this.totalCashAmmount = totalCashAmmount;
	}
	public BigDecimal getTotalCardAmmount()
	{
		return totalCardAmmount;
	}
	public void setTotalCardAmmount(BigDecimal totalCardAmmount)
	{
		this.totalCardAmmount = totalCardAmmount;
	}
	public BigDecimal getTotalCreditTermAmmount()
	{
		return totalCreditTermAmmount;
	}
	public void setTotalCreditTermAmmount(BigDecimal totalCreditTermAmmount)
	{
		this.totalCreditTermAmmount = totalCreditTermAmmount;
	}
	public BigDecimal getCashTipAmount()
	{
		return cashTipAmount;
	}
	public void setCashTipAmount(BigDecimal cashTipAmount)
	{
		this.cashTipAmount = cashTipAmount;
	}
	public BigDecimal getCardTipAmount()
	{
		return cardTipAmount;
	}
	public void setCardTipAmount(BigDecimal cardTipAmount)
	{
		this.cardTipAmount = cardTipAmount;
	}
	public BigDecimal getCreditTermTipAmount()
	{
		return creditTermTipAmount;
	}
	public void setCreditTermTipAmount(BigDecimal creditTermTipAmount)
	{
		this.creditTermTipAmount = creditTermTipAmount;
	}
	
	
	
	
}
