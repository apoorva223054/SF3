package com.nirvanaxp.payment.util;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.types.entities.payment.PaymentTransactionType;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType_;
import com.nirvanaxp.types.entities.payment.TransactionStatus;
import com.nirvanaxp.types.entities.payment.TransactionStatus_;

final class PaymentCommonUtil
{
	
	PaymentTransactionType getPaymentTransactionType(HttpServletRequest httpRequest, EntityManager em,
			String name) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<PaymentTransactionType> criteria = builder.createQuery(PaymentTransactionType.class);
		Root<PaymentTransactionType> r = criteria.from(PaymentTransactionType.class);
		TypedQuery<PaymentTransactionType> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(PaymentTransactionType_.name), name)));
		return query.getSingleResult();

	}

	TransactionStatus getTransactionStatusByName(HttpServletRequest httpRequest, EntityManager em,
			String name) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TransactionStatus> criteria = builder.createQuery(TransactionStatus.class);
		Root<TransactionStatus> r = criteria.from(TransactionStatus.class);
		TypedQuery<TransactionStatus> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(TransactionStatus_.name), name)));
		return query.getSingleResult();

	}
	
	
}
