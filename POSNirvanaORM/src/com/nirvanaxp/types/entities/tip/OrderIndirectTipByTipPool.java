/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.tip;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the order_source database table.
 * 
 */
@Entity
@Table(name = "order_indirect_tip_by_tip_pool")
@XmlRootElement(name = "order_indirect_tip_by_tip_pool")
public class OrderIndirectTipByTipPool extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "order_id")
	String orderId;

	@Column(name = "tip_pool_id")
	int tipPoolId;

	@Column(name = "direct_cash_tip")
	BigDecimal directCashTip;

	@Column(name = "direct_card_tip")
	BigDecimal directCardTip;

	@Column(name = "direct_credit_term_tip")
	BigDecimal directCreditTermTip;

	@Column(name = "indirect_cash_tip")
	BigDecimal indirectCashTip;

	@Column(name = "indirect_card_tip")
	BigDecimal indirectCardTip;

	@Column(name = "indirect_credit_term_tip")
	BigDecimal indirectCreditTermTip;

	@Column(name = "pending_cash_tip")
	BigDecimal pendingCashTip;

	@Column(name = "pending_card_tip")
	BigDecimal pendingCardTip;

	@Column(name = "pending_credit_tip")
	BigDecimal pendingCreditTip;

	@Column(name = "local_time")
	private String localTime;

	public OrderIndirectTipByTipPool()
	{

	}

	public String getOrderId()
	{
		 if(orderId != null && (orderId.length()==0 || orderId.equals("0"))){return null;}else{	return orderId;}
	}

	public void setOrderId(String orderId)
	{
		this.orderId = orderId;
	}

	public int getTipPoolId()
	{
		return tipPoolId;
	}

	public void setTipPoolId(int tipPoolId)
	{
		this.tipPoolId = tipPoolId;
	}

	public BigDecimal getIndirectCashTip()
	{
		return indirectCashTip;
	}

	public void setIndirectCashTip(BigDecimal indirectCashTip)
	{
		this.indirectCashTip = indirectCashTip;
	}

	public BigDecimal getIndirectCardTip()
	{
		return indirectCardTip;
	}

	public void setIndirectCardTip(BigDecimal indirectCardTip)
	{
		this.indirectCardTip = indirectCardTip;
	}

	public BigDecimal getIndirectCreditTermTip()
	{
		return indirectCreditTermTip;
	}

	public void setIndirectCreditTermTip(BigDecimal indirectCreditTermTip)
	{
		this.indirectCreditTermTip = indirectCreditTermTip;
	}

	public BigDecimal getPendingCashTip()
	{
		return pendingCashTip;
	}

	public void setPendingCashTip(BigDecimal pendingCashTip)
	{
		this.pendingCashTip = pendingCashTip;
	}

	public BigDecimal getPendingCardTip()
	{
		return pendingCardTip;
	}

	public void setPendingCardTip(BigDecimal pendingCardTip)
	{
		this.pendingCardTip = pendingCardTip;
	}

	public BigDecimal getPendingCreditTip()
	{
		return pendingCreditTip;
	}

	public void setPendingCreditTip(BigDecimal pendingCreditTip)
	{
		this.pendingCreditTip = pendingCreditTip;
	}

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}

	public BigDecimal getDirectCashTip()
	{
		return directCashTip;
	}

	public void setDirectCashTip(BigDecimal directCashTip)
	{
		this.directCashTip = directCashTip;
	}

	public BigDecimal getDirectCardTip()
	{
		return directCardTip;
	}

	public void setDirectCardTip(BigDecimal directCardTip)
	{
		this.directCardTip = directCardTip;
	}

	public BigDecimal getDirectCreditTermTip()
	{
		return directCreditTermTip;
	}

	public void setDirectCreditTermTip(BigDecimal directCreditTermTip)
	{
		this.directCreditTermTip = directCreditTermTip;
	}

	@Override
	public String toString()
	{
		return "OrderIndirectTipByTipPool [orderId=" + orderId + ", tipPoolId=" + tipPoolId + ", "
				+ "directCashTip=" + directCashTip + ", directCardTip=" + directCardTip + ", directCreditTermTip="
				+ directCreditTermTip + ", indirectCashTip=" + indirectCashTip + ", indirectCardTip=" + indirectCardTip + ", "
				+ "indirectCreditTermTip=" + indirectCreditTermTip + ", pendingCashTip="
				+ pendingCashTip + ", pendingCardTip=" + pendingCardTip + ", pendingCreditTip=" 
				+ pendingCreditTip + ", localTime=" + localTime + "]";
	}

}