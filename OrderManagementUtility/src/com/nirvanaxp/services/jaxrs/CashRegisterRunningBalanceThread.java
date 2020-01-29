package com.nirvanaxp.services.jaxrs;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;

public class CashRegisterRunningBalanceThread extends Thread {

	OrderPaymentDetail orderPaymentDetail;

	HttpServletRequest httpRequest;
	NirvanaLogger logger;
	String sessionId;
	String locationId;
	 

	public CashRegisterRunningBalanceThread(OrderPaymentDetail orderPaymentDetail, HttpServletRequest httpRequest,
			NirvanaLogger logger, String locationId,  String sessionId) {
		super();
		this.orderPaymentDetail = orderPaymentDetail;
		this.httpRequest = httpRequest;
		this.logger = logger;
		this.locationId = locationId;
		this.sessionId=sessionId;
	}

	@Override
	public void run() {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForThread(httpRequest, sessionId);
			tx = em.getTransaction();
			tx.begin();
			new OrderManagementServiceBean().setCashRegisterRunningBalance(em, orderPaymentDetail, locationId);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && em.getTransaction().isActive()) {
				tx.rollback();
			}
			throw e;
		} catch (Exception e) {
			logger.severe(e);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
}
