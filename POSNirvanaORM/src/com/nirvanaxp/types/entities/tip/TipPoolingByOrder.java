/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.tip;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

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
@Table(name = "tip_pooling_by_order")
@XmlRootElement(name = "tip_pooling_by_order")
public class TipPoolingByOrder extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Column(name = "order_id")
	String orderId;

	@Column(name = "user_id")
	String userId;
	
	@Column(name = "cash_total")
	BigDecimal cashTotal;

	@Column(name = "card_total")
	BigDecimal cardTotal;

	@Column(name = "credit_total")
	BigDecimal creditTotal;

	@Column(name = "direct_cash_tip")
	BigDecimal directCashTip;
	
	@Column(name = "direct_card_tip")
	BigDecimal directCardTip;
	
	@Column(name = "direct_credit_term_tip")
	BigDecimal directCreditTermTip;


	@Column(name = "indirect_cash_tip_submited")
	BigDecimal indirectCashTipSubmited;
	
	@Column(name = "indirect_card_tip_submited")
	BigDecimal indirectCardTipSubmited;
	
	@Column(name = "indirect_credit_term_tip_submited")
	BigDecimal indirectCreditTermTipSubmited;

	@Column(name = "nirvanaxp_batch_id")
	String nirvanaxpBatchId;
	
	@Column(name = "pending_cash_tip")
	BigDecimal pendingCashTip;
	
	@Column(name = "pending_card_tip")
	BigDecimal pendingCardTip;
	
	@Column(name = "pending_credit_tip")
	BigDecimal pendingCreditTip;
	
	@Column(name = "local_time")
	private String localTime;

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	
	public TipPoolingByOrder()
	{

	}

	public String getOrderId() {
		 if(orderId != null && (orderId.length()==0 || orderId.equals("0"))){return null;}else{	return orderId;}
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public BigDecimal getCashTotal() {
		return cashTotal;
	}



	public void setCashTotal(BigDecimal cashTotal) {
		this.cashTotal = cashTotal;
	}



	public BigDecimal getCardTotal() {
		return cardTotal;
	}



	public void setCardTotal(BigDecimal cardTotal) {
		this.cardTotal = cardTotal;
	}



	public BigDecimal getCreditTotal() {
		return creditTotal;
	}



	public void setCreditTotal(BigDecimal creditTotal) {
		this.creditTotal = creditTotal;
	}

	

	public BigDecimal getDirectCashTip() {
		return directCashTip;
	}


	public void setDirectCashTip(BigDecimal directCashTip) {
		this.directCashTip = directCashTip;
	}


	public BigDecimal getDirectCardTip() {
		return directCardTip;
	}


	public void setDirectCardTip(BigDecimal directCardTip) {
		this.directCardTip = directCardTip;
	}


	public BigDecimal getDirectCreditTermTip() {
		return directCreditTermTip;
	}


	public void setDirectCreditTermTip(BigDecimal directCreditTermTip) {
		this.directCreditTermTip = directCreditTermTip;
	}
	


	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public BigDecimal getIndirectCashTipSubmited() {
		return indirectCashTipSubmited;
	}


	public void setIndirectCashTipSubmited(BigDecimal indirectCashTipSubmited) {
		this.indirectCashTipSubmited = indirectCashTipSubmited;
	}


	public BigDecimal getIndirectCardTipSubmited() {
		return indirectCardTipSubmited;
	}


	public void setIndirectCardTipSubmited(BigDecimal indirectCardTipSubmited) {
		this.indirectCardTipSubmited = indirectCardTipSubmited;
	}


	public BigDecimal getIndirectCreditTermTipSubmited() {
		return indirectCreditTermTipSubmited;
	}


	public void setIndirectCreditTermTipSubmited(
			BigDecimal indirectCreditTermTipSubmited) {
		this.indirectCreditTermTipSubmited = indirectCreditTermTipSubmited;
	}


	public String getNirvanaxpBatchId()
	{
		return nirvanaxpBatchId;
	}
	public void setNirvanaxpBatchId(String nirvanaxpBatchId)
	{
		this.nirvanaxpBatchId = nirvanaxpBatchId;
	}

	public BigDecimal getPendingCashTip() {
		return pendingCashTip;
	}

	public void setPendingCashTip(BigDecimal pendingCashTip) {
		this.pendingCashTip = pendingCashTip;
	}

	public BigDecimal getPendingCardTip() {
		return pendingCardTip;
	}

	public void setPendingCardTip(BigDecimal pendingCardTip) {
		this.pendingCardTip = pendingCardTip;
	}

	public BigDecimal getPendingCreditTip() {
		return pendingCreditTip;
	}

	public void setPendingCreditTip(BigDecimal pendingCreditTip) {
		this.pendingCreditTip = pendingCreditTip;
	}

	@Override
	public String toString() {
		return "TipPoolingByOrder [orderId=" + orderId + ", userId=" + userId
				+ ", cashTotal=" + cashTotal + ", cardTotal=" + cardTotal
				+ ", creditTotal=" + creditTotal + ", directCashTip="
				+ directCashTip + ", directCardTip=" + directCardTip
				+ ", directCreditTermTip=" + directCreditTermTip
				+ ", indirectCashTipSubmited=" + indirectCashTipSubmited
				+ ", indirectCardTipSubmited=" + indirectCardTipSubmited
				+ ", indirectCreditTermTipSubmited="
				+ indirectCreditTermTipSubmited + ", nirvanaxpBatchId="
				+ nirvanaxpBatchId + ", pendingCashTip=" + pendingCashTip
				+ ", pendingCardTip=" + pendingCardTip + ", pendingCreditTip="
				+ pendingCreditTip + ", localTime=" + localTime + "]";
	}

	
	



}