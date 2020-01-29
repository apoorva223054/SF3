package com.nirvanaxp.payment.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.NameConstant;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.payment.gateway.data.MerchentAccount;
import com.nirvanaxp.payment.gateway.data.SupportedPaymentGateway;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.jaxrs.OrderManagementServiceBean;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail_;
import com.nirvanaxp.types.entities.orders.OrderSourceGroupToPaymentgatewayType;
import com.nirvanaxp.types.entities.orders.OrderSourceToPaymentgatewayType;
import com.nirvanaxp.types.entities.payment.PaymentMethod;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType;
import com.nirvanaxp.types.entities.payment.TransactionStatus;

public class SettlementUpdateQueue implements Runnable{

	private SupportedPaymentGateway supportedPaymentGateway;
	private MerchentAccount merchentAccount;
	private OrderHeader orderHeader;
	private HttpServletRequest httpRequest;
	private String schemaName;
	private String userId;
	private String currentDate;
	private String currentTime;
	private final CountDownLatch doneSignal;
	private NirvanaLogger logger = new NirvanaLogger(SettlementUpdateQueue.class.getName());
	
	@Override
	public void run() {
		 
		try {
			saveTransactionForCaptureAll(httpRequest, schemaName, supportedPaymentGateway, merchentAccount,orderHeader);
			doneSignal.countDown();
		} catch (IOException e) {
			logger.severe(e,"Error while saving transaction for capture all: ", e.getMessage());
		}
	}

	public SettlementUpdateQueue(CountDownLatch doneSignal, String schemaName, HttpServletRequest httpRequest, SupportedPaymentGateway supportedPaymentGateway, MerchentAccount merchentAccount,
			OrderHeader orderHeader, String userId, String currentDate, String currentTime) {
		this.supportedPaymentGateway=supportedPaymentGateway;
		this.merchentAccount=merchentAccount;
		this.orderHeader=orderHeader;
		this.httpRequest=httpRequest;
		this.schemaName=schemaName;
		this.currentDate = currentDate;
		this.currentTime = currentTime;
		this.userId = userId;
		this.doneSignal = doneSignal;
	}

	private void saveTransactionForCaptureAll(HttpServletRequest httpRequest, String schemaName,
			SupportedPaymentGateway supportedPaymentGateway, MerchentAccount merchentAccount, OrderHeader orderHeader)
			throws IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);
			em.getTransaction().begin();
			TransactionStatus transactionStatusCCAuth = new PaymentCommonUtil().getTransactionStatusByName(httpRequest, em, "CC Auth");
			TransactionStatus transactionStatusTipSaved = new PaymentCommonUtil().getTransactionStatusByName(httpRequest, em, "Tip Saved");
			TransactionStatus transactionStatusCCPreCapture = new PaymentCommonUtil().getTransactionStatusByName(httpRequest, em,
					"CC Pre Capture");
			PaymentTransactionType paymentTransactionTypeForce = new PaymentCommonUtil().getPaymentTransactionType(httpRequest, em, "Force");
			PaymentTransactionType paymentTransactionTypeCaptureAll = new PaymentCommonUtil().getPaymentTransactionType(httpRequest, em,
					"CaptureAll");
			TransactionStatus transactionStatusCCSettled = new PaymentCommonUtil().getTransactionStatusByName(httpRequest, em, "CC Settled");

			List<OrderPaymentDetail> orderDetailPaymentList = orderHeader.getOrderPaymentDetails();

			if (orderDetailPaymentList != null && orderDetailPaymentList.size() > 0) {
				OrderHeader orderTosend = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em,OrderHeader.class, orderHeader.getId());

				if (orderTosend != null) {
					if (orderTosend.getOrderPaymentDetails() != null) {
						orderTosend.getOrderPaymentDetails().clear();
					} else {
						// add the list
						List<OrderPaymentDetail> orderPaymentDetailsList = new ArrayList<OrderPaymentDetail>();
						orderTosend.setOrderPaymentDetails(orderPaymentDetailsList);
					}

					for (OrderPaymentDetail orderPaymentDetail : orderHeader.getOrderPaymentDetails()) {

						if (orderPaymentDetail != null && isAllowedToCapture(httpRequest, em, supportedPaymentGateway,
								merchentAccount, orderPaymentDetail)) {
							if (orderPaymentDetail.getTransactionStatus().getId() == transactionStatusCCPreCapture
									.getId()) {
								if (orderPaymentDetail.getPaymentTransactionType().getName().equals("Auth")) {
									if (orderPaymentDetail.getIsRefunded() != 1) {
										OrderPaymentDetail transformedOrderPaymentDetail = updateObjectForCaptureAll(
												httpRequest, orderPaymentDetail, transactionStatusCCSettled);
										orderTosend.getOrderPaymentDetails().add(transformedOrderPaymentDetail);

									}
								} else if (orderPaymentDetail.getPaymentTransactionType().getName().equals("Force")) {
									if (orderPaymentDetail.getIsRefunded() != 1) {
										OrderPaymentDetail transformedOrderPaymentDetail = updateObjectForCaptureAll(
												httpRequest, orderPaymentDetail, transactionStatusCCSettled);
										orderTosend.getOrderPaymentDetails().add(transformedOrderPaymentDetail);

										OrderPaymentDetail forceSingleToCapture = getLastForceTransactionWithLastUpdatedTime(
												httpRequest, em, orderPaymentDetail, paymentTransactionTypeForce);

										OrderPaymentDetail orderPaymentTosend = createObjectForPaymentDetailsForCapture(
												httpRequest, orderHeader, forceSingleToCapture,
												paymentTransactionTypeCaptureAll, transactionStatusCCSettled,
												supportedPaymentGateway, merchentAccount);

										orderTosend.setCashierId(userId);

										orderTosend.getOrderPaymentDetails().add(orderPaymentTosend);

									}
								}
							} else if ((supportedPaymentGateway.getSupportedPaymentGateway().equals(NameConstant.DATACAP_GATEWAY) || supportedPaymentGateway.getSupportedPaymentGateway().equals(NameConstant.DATACAP_GATEWAY_WITH_FIRSTDATA) )
									&& (orderPaymentDetail.getTransactionStatus().getId() == transactionStatusCCAuth
											.getId()
											|| orderPaymentDetail.getTransactionStatus()
													.getId() == transactionStatusTipSaved.getId())) {

								if (orderPaymentDetail.getPaymentTransactionType().getName().equals("Auth")) {

									if (orderPaymentDetail.getIsRefunded() != 1) {
										OrderPaymentDetail transformedOrderPaymentDetail = updateObjectForCaptureAll(
												httpRequest, orderPaymentDetail, transactionStatusCCSettled);
										orderTosend.getOrderPaymentDetails().add(transformedOrderPaymentDetail);

										OrderPaymentDetail orderPaymentTosend = createObjectForPaymentDetailsForCapture(
												httpRequest, orderHeader, transformedOrderPaymentDetail,
												paymentTransactionTypeCaptureAll, transactionStatusCCSettled,
												supportedPaymentGateway, merchentAccount);

										orderTosend.setCashierId(userId);

										orderTosend.getOrderPaymentDetails().add(orderPaymentTosend);

									}
								}

							}
						}
					}

				}

				// save into our own database
				OrderManagementServiceBean bean = new OrderManagementServiceBean();

				try {
					bean.updateOrderPaymentForBatchSettle(httpRequest, em, orderTosend);
				} catch (Exception e) {
					logger.severe(e);
				}

			}
			em.getTransaction().commit();
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (em.getTransaction() != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private boolean isAllowedToCapture(HttpServletRequest httpRequest, EntityManager em,
			SupportedPaymentGateway supportedPaymentGateway, MerchentAccount merchentAccount,
			OrderPaymentDetail orderPaymentDetail) {

		if (merchentAccount.getOrderSourceGroupToPaymentGatewayTypeId() == 0
				&& merchentAccount.getOrderSourceToPaymentGatewayTypeId() == 0) {
			if (orderPaymentDetail.getPayementGatewayId() == supportedPaymentGateway.getId()) {
				return true;
			}
		} else if (orderPaymentDetail.getOrderSourceGroupToPaymentGatewayTypeId() == merchentAccount
				.getOrderSourceGroupToPaymentGatewayTypeId()
				&& orderPaymentDetail.getOrderSourceToPaymentGatewayTypeId() == merchentAccount
						.getOrderSourceToPaymentGatewayTypeId()) {
			return true;
		} else if (orderPaymentDetail.getOrderSourceToPaymentGatewayTypeId() != 0) {
			OrderSourceToPaymentgatewayType orderSourceToPaymentGatewayType = em.find(
					OrderSourceToPaymentgatewayType.class, orderPaymentDetail.getOrderSourceToPaymentGatewayTypeId());
			if (orderSourceToPaymentGatewayType.getMerchantId().equals(merchentAccount.getUserName())) {
				return true;
			}

		} else if (orderPaymentDetail.getOrderSourceGroupToPaymentGatewayTypeId() != 0) {

			OrderSourceGroupToPaymentgatewayType orderSourceGroupToPaymentGatewayType = em.find(
					OrderSourceGroupToPaymentgatewayType.class,
					orderPaymentDetail.getOrderSourceGroupToPaymentGatewayTypeId());
			if (orderSourceGroupToPaymentGatewayType.getMerchantId().equals(merchentAccount.getUserName())) {
				return true;
			}

		}

		return false;

	}
	

	private OrderPaymentDetail updateObjectForCaptureAll(HttpServletRequest httpRequest,
			OrderPaymentDetail orderPaymentDetail, TransactionStatus transactionStatusCCSEttled) {
		try {
			orderPaymentDetail.setUpdatedBy(userId);

			orderPaymentDetail.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			orderPaymentDetail.setTransactionStatus(transactionStatusCCSEttled);
			return orderPaymentDetail;
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;

	}
	
	private OrderPaymentDetail getLastForceTransactionWithLastUpdatedTime(HttpServletRequest httpRequest,
			EntityManager em, OrderPaymentDetail orderPaymentDetail,
			PaymentTransactionType paymentTransactionTypeForce) {
		try {

			List<OrderPaymentDetail> forceList = getOrderPaymentObjectByTransactionTypeAndAuthCode(httpRequest, em,
					paymentTransactionTypeForce.getId(), orderPaymentDetail.getAuthCode(), orderPaymentDetail.getId());
			Comparator<OrderPaymentDetail> orderpaymentdatilComparator = new SortOrderpaymentDetailByID();
			Collections.sort(forceList, orderpaymentdatilComparator);
			Collections.reverse(forceList);
			OrderPaymentDetail lastUpdatedObject = forceList.get(0);
			return lastUpdatedObject;
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;
	}

	private List<OrderPaymentDetail> getOrderPaymentObjectByTransactionTypeAndAuthCode(HttpServletRequest httpRequest,
			EntityManager em, int id, String authCode, String orderPaymentDetailId) {

		PaymentTransactionType paymentTransactionType = new PaymentTransactionType();
		paymentTransactionType.setId(id);

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderPaymentDetail> criteria = builder.createQuery(OrderPaymentDetail.class);
		Root<OrderPaymentDetail> r = criteria.from(OrderPaymentDetail.class);
		TypedQuery<OrderPaymentDetail> query = em.createQuery(
				criteria.select(r).where(builder.equal(r.get(OrderPaymentDetail_.authCode), authCode),
						builder.equal(r.get(OrderPaymentDetail_.id), orderPaymentDetailId), builder
								.equal(r.get(OrderPaymentDetail_.paymentTransactionType), paymentTransactionType)));

		return query.getResultList();

	}
	
	private OrderPaymentDetail createObjectForPaymentDetailsForCapture(HttpServletRequest httpRequest,
			OrderHeader orderheader, OrderPaymentDetail orderPaymentDetail,
			PaymentTransactionType captureAllPaymentTransactionType, TransactionStatus transactionStatusCCSettled,
			SupportedPaymentGateway supportedPaymentGateway, MerchentAccount merchentAccount) {
		// create total object which pass to server on click of credit card or
		// cash button

		try {

			if (orderheader != null) {

				OrderPaymentDetail orderPaymentDetailToSend = new OrderPaymentDetail();
				orderPaymentDetailToSend.setSettledAmount(orderPaymentDetail.getTotalAmount());
				orderPaymentDetailToSend.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				orderPaymentDetailToSend.setUpdatedBy(userId);

				orderPaymentDetailToSend.setDate(currentDate);
				orderPaymentDetailToSend.setTime(currentTime);
				orderPaymentDetailToSend.setCreatedBy(userId);
				orderPaymentDetailToSend.setPaymentTransactionType(captureAllPaymentTransactionType);
				PaymentMethod paymentmethod = new PaymentMethod();
				paymentmethod = orderPaymentDetail.getPaymentMethod();
				orderPaymentDetailToSend.setPaymentMethod(paymentmethod);
				orderPaymentDetailToSend.setPnRef(orderPaymentDetail.getPnRef());

				orderPaymentDetailToSend.setAuthCode(orderPaymentDetail.getAuthCode());
				orderPaymentDetailToSend.setNirvanaXpBatchNumber(orderPaymentDetail.getNirvanaXpBatchNumber());

				orderPaymentDetailToSend.setHostRefStr(orderPaymentDetail.getHostRefStr());
				orderPaymentDetailToSend.setTotalAmount(orderheader.getTotal());
				orderPaymentDetailToSend.setAmountPaid(orderPaymentDetail.getAmountPaid());
				orderPaymentDetailToSend.setBalanceDue((orderPaymentDetail.getBalanceDue()));
				orderPaymentDetailToSend.setSettledAmount(orderPaymentDetail.getAmountPaid());
				orderPaymentDetailToSend.setCashTipAmt(orderPaymentDetail.getCashTipAmt());
				orderPaymentDetailToSend.setCreditcardTipAmt(orderPaymentDetail.getCreditcardTipAmt());
				orderPaymentDetailToSend.setSeatId(orderPaymentDetail.getSeatId());
				orderPaymentDetailToSend.setExpiryMonth(orderPaymentDetail.getExpiryMonth());
				orderPaymentDetailToSend.setExpiryYear(orderPaymentDetail.getExpiryYear());
				orderPaymentDetailToSend.setCardNumber(orderPaymentDetail.getCardNumber());
				orderPaymentDetailToSend.setPriceGratuity(orderPaymentDetail.getPriceGratuity());
				orderPaymentDetailToSend.setGratuity(orderPaymentDetail.getGratuity());
				orderPaymentDetailToSend.setTaxDisplayName1(orderPaymentDetail.getTaxDisplayName1());
				orderPaymentDetailToSend.setTaxDisplayName2(orderPaymentDetail.getTaxDisplayName2());
				orderPaymentDetailToSend.setTaxDisplayName3(orderPaymentDetail.getTaxDisplayName3());
				orderPaymentDetailToSend.setTaxDisplayName4(orderPaymentDetail.getTaxDisplayName4());
				orderPaymentDetailToSend.setTaxName1(orderPaymentDetail.getTaxName1());
				orderPaymentDetailToSend.setTaxName2(orderPaymentDetail.getTaxName2());
				orderPaymentDetailToSend.setTaxName3(orderPaymentDetail.getTaxName3());
				orderPaymentDetailToSend.setTaxName4(orderPaymentDetail.getTaxName4());
				orderPaymentDetailToSend.setTaxRate1(orderPaymentDetail.getTaxRate1());
				orderPaymentDetailToSend.setTaxRate2(orderPaymentDetail.getTaxRate2());
				orderPaymentDetailToSend.setTaxRate3(orderPaymentDetail.getTaxRate3());
				orderPaymentDetailToSend.setTaxRate4(orderPaymentDetail.getTaxRate4());
				orderPaymentDetailToSend.setPriceTax1(orderPaymentDetail.getPriceTax1());
				orderPaymentDetailToSend.setPriceTax2(orderPaymentDetail.getPriceTax2());
				orderPaymentDetailToSend.setPriceTax3(orderPaymentDetail.getPriceTax3());
				orderPaymentDetailToSend.setPriceTax4(orderPaymentDetail.getPriceTax4());
				orderPaymentDetailToSend.setRegister(orderPaymentDetail.getRegister());
				orderPaymentDetailToSend.setSecurityCode(orderPaymentDetail.getSecurityCode());
				orderPaymentDetailToSend.setAuthAmount(orderPaymentDetail.getAuthAmount());
				orderPaymentDetailToSend.setTipAmount(orderPaymentDetail.getTipAmount());
				orderPaymentDetailToSend.setDiscountsName(orderPaymentDetail.getDiscountsName());
				orderPaymentDetailToSend.setDiscountsValue(orderPaymentDetail.getDiscountsValue());
				orderPaymentDetailToSend.setCalculatedDiscountValue(orderPaymentDetail.getCalculatedDiscountValue());
				orderPaymentDetailToSend.setPriceDiscount(orderPaymentDetail.getPriceDiscount());
				if (orderPaymentDetail.getCardType() != null) {
					orderPaymentDetailToSend.setCardType(orderPaymentDetail.getCardType());
				} else {
					orderPaymentDetailToSend.setCardType("");
				}
				orderPaymentDetailToSend.setTransactionStatus(transactionStatusCCSettled);
				orderPaymentDetailToSend.setChangeDue(new BigDecimal("0.00"));
				orderPaymentDetailToSend.setPayementGatewayId(supportedPaymentGateway.getId());
				orderPaymentDetailToSend.setOrderSourceGroupToPaymentGatewayTypeId(
						merchentAccount.getOrderSourceGroupToPaymentGatewayTypeId());
				orderPaymentDetailToSend
						.setOrderSourceToPaymentGatewayTypeId(merchentAccount.getOrderSourceToPaymentGatewayTypeId());
				if (orderPaymentDetail.getInvoiceNumber() != null) {
					orderPaymentDetailToSend.setInvoiceNumber(orderPaymentDetail.getInvoiceNumber());
				}
				if (orderPaymentDetail.getAcqRefData() != null) {
					orderPaymentDetailToSend.setAcqRefData(orderPaymentDetail.getAcqRefData());
				}
				if(orderPaymentDetail.getPosEntry()!=null){
					orderPaymentDetailToSend.setPosEntry(orderPaymentDetail.getPosEntry());
				}

				return orderPaymentDetailToSend;
			}

		} catch (Exception e) {
			logger.severe(e);
		}
		return null;

	}
}
