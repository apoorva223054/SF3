/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.accounts.Account;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.PrinterOrderList;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.email.ReceiptPDFFormat;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.OrderPacket;
import com.nirvanaxp.services.util.email.EmailTemplateKeys;
import com.nirvanaxp.types.entities.catalog.category.Category;
import com.nirvanaxp.types.entities.catalog.category.CategoryToPrinter;
import com.nirvanaxp.types.entities.catalog.category.CategoryToPrinter_;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem_;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.ItemsToPrinter;
import com.nirvanaxp.types.entities.catalog.items.ItemsToPrinter_;
import com.nirvanaxp.types.entities.catalog.items.NotPrintedOrderDetailItemsToPrinter;
import com.nirvanaxp.types.entities.catalog.items.NotPrintedOrderDetailItemsToPrinter_;
import com.nirvanaxp.types.entities.email.PrintQueue;
import com.nirvanaxp.types.entities.email.PrintQueue_;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.orders.OrderDetailAttribute;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderDetailItem_;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.printers.Printer;
import com.nirvanaxp.types.entities.printers.Printer_;
import com.nirvanaxp.types.entities.printers.PrintersModel;
import com.nirvanaxp.types.entities.printers.PrintersType;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

// TODO: Auto-generated Javadoc
/**
 * The Class NirvanaEpsonPrintService.
 */
@WebListener
@Path("")
@LoggerInterceptor
@Singleton
@Lock(LockType.READ)
// TODO: remove this annotation when testing is complete and requests are
// handled by printer id
public class NirvanaEpsonPrintService extends AbstractNirvanaService {

	/** The http request. */
	@Context
	HttpServletRequest httpRequest;

	/** The logger. */
	private NirvanaLogger logger = new NirvanaLogger(NirvanaEpsonPrintService.class.getName());
	
	/** The response counter map. */
	// TODO: this needs to be be moved to database
	private static Map<String, Integer> RESPONSE_COUNTER_MAP = new HashMap<String, Integer>(); 
	
	/** The print queue id. */
	private static BigInteger printQueueId=BigInteger.ZERO;

	/**
	 * Do printer comm.
	 *
	 * @param connectionType the connection type
	 * @param printerId the printer id
	 * @param printResult the print result
	 * @param accountId the account id
	 * @param locationId the location id
	 * @return the string
	 * @throws NirvanaXPException the nirvana XP exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InvalidSessionException the invalid session exception
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	@Path("/PrinterComm/{accountId}/{locationId}")
	public String doPrinterComm(@FormParam("ConnectionType") String connectionType, @FormParam("ID") String printerId, @FormParam("ResponseFile") String printResult,
			@PathParam("accountId") int accountId, @PathParam("locationId") String locationId) throws NirvanaXPException, IOException, InvalidSessionException
	{
		
		EntityManager em = null;
		
		int responseCount = getResponseCountForAccountAndLocationAndPrinter(accountId, locationId, printerId);
		try
		{
			em = getEntityManager(accountId);
			if (em == null)
			{
				return null;
			}
			if ("GetRequest".equals(connectionType))
			{				
				return doGetRequest(em, responseCount, accountId, locationId, printerId);
			}
			else if ("SetResponse".equals(connectionType))
			{
				responseCount++;
				updateResponseCounterForAccountAndLocationAndPrinter(accountId, locationId, printerId, responseCount);
				if (responseCount < 3)
				{
					
					boolean status = extractStatusFromPrinterResponse(printResult);
					logger.severe("Got response from printerstatus:" + status);
					EntityTransaction tx = em.getTransaction();
					updateOrderStatus(status, em, locationId, accountId, tx);
					
				}
				else
				{
					logger.severe("Skipping setting response because we have already tried it 3 times for account:"+accountId+", location:"+locationId+" and printer:",printerId );
				}

			}
			
		}
		finally
		{
			EntityTransaction tx = em.getTransaction();
			if(tx!=null && tx.isActive())
			{
				logger.severe(new Exception(), "Transaction not completed within printer comm");
				tx.rollback();
			}
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;
	}

	/**
	 * Do get request.
	 *
	 * @param em the em
	 * @param responseCount the response count
	 * @param accountId the account id
	 * @param locationId the location id
	 * @param printerId the printer id
	 * @return the string
	 * @throws NirvanaXPException the nirvana XP exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String doGetRequest(EntityManager em, int responseCount, int accountId, String locationId, String printerId) throws NirvanaXPException, IOException
	{
		responseCount = 0;
		updateResponseCounterForAccountAndLocationAndPrinter(accountId, locationId, printerId, responseCount);
		// getting non printer order from database
		PrintQueue printQueue = getPrintFromDatabase(em, accountId, locationId, "N");
		// checking pending print which is not in queue
		if (printQueue != null)
		{
			// setting status of in-process
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			printQueue.setStatus("I");
			em.merge(printQueue);
			// TODO when does print queue clear?
			tx.commit();
			printQueueId = printQueue.getId();
			return printQueue.getPrintString();

		}
		else
		{
			preparePrintQueue(em, accountId, locationId, printerId);			
		}
		
		return null;
	}

	/**
	 * Prepare print queue.
	 *
	 * @param em the em
	 * @param accountId the account id
	 * @param locationId the location id
	 * @param printerId the printer id
	 * @throws NirvanaXPException the nirvana XP exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	// TODO - do this in separate thread with synchronized lock
	private void preparePrintQueue(EntityManager em, int accountId, String locationId, String printerId) throws NirvanaXPException, IOException
	{
		List<String> orderList = getUnprintedOnlineOrderForPrinterID(em, locationId);
		if (orderList == null || orderList.size() < 1)
		{
			return;
		}
		
		for (String orderId : orderList)
		{
			OrderHeader orderHeader = new OrderManagementServiceBean().getOrderById(em, orderId);
			List<OrderDetailItem> orderDetailItemsArray = orderHeader.getOrderDetailItems();
			
			List<PrinterOrderList> printerOrderList = null;
			printerOrderList =  filterOrderHeader(em, orderId, orderDetailItemsArray);
			List<OrderDetailItem> newOrderDetailItemList = null;
			for (int i = 0; i < printerOrderList.size(); i++)
			{
				newOrderDetailItemList = new ArrayList<OrderDetailItem>();
				OrderDetailItem detailItem = printerOrderList.get(i).getOrderDetailItem();
				detailItem.setPrinterId(printerOrderList.get(i).getPrinterId());
				newOrderDetailItemList.add(detailItem);
				for (int k = 0; k < printerOrderList.size(); k++)
				{
					if (i < printerOrderList.size() && k < printerOrderList.size() && printerOrderList.get(i).getPrinterId() == printerOrderList.get(k).getPrinterId() && (i != k))
					{
						detailItem = printerOrderList.get(k).getOrderDetailItem();
						detailItem.setPrinterId(printerOrderList.get(k).getPrinterId());
						newOrderDetailItemList.add(detailItem);
						printerOrderList.remove(k);
						k--;
					}
				}

				Printer printer = null;
				String info = null;
				ArrayList<OrderDetailItem> orderDetailItems = new ArrayList<OrderDetailItem>();
				for (OrderDetailItem orderDetailItem : newOrderDetailItemList)
				{
					OrderDetailStatus detailStatus = em.find(OrderDetailStatus.class, orderDetailItem.getOrderDetailStatusId());
					
					if (detailStatus != null && !detailStatus.getName().equals("Item saved") )
					{
						orderDetailItems.add(orderDetailItem);
					}
				}

				if (orderDetailItems != null && orderDetailItems.size() != 0)
				{
					printer = getPrinterByPrinterId(em, orderDetailItems.get(0).getPrinterId());
					if (printer != null && !printer.getPrintersName().equals("No Printer"))
					{

						orderHeader.setOrderDetailItems(orderDetailItems);
						PrintersModel printersModel = em.find(PrintersModel.class, printer.getPrintersModelId());
						PrintersType printersType = (PrintersType) new CommonMethods().getObjectById("PrintersType", em,PrintersType.class, printer.getPrintersTypeId());
						OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, orderHeader.getOrderStatusId());
						LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
						if (!printersModel.getModelNumber().equalsIgnoreCase("NXP-KDS")&&!printersModel.getModelNumber().equalsIgnoreCase("NXP-Prod-KDS") && !printersType.getName().equalsIgnoreCase("Label Printer"))
						{
						       info = convertOrderToPrinterResponseForItemizePrinting(orderHeader, printer.getPrintersName(), em, printersModel.getModelNumber());
							   
						       info = info.replace(" & ", " &amp; ");
						// adding print to queue
						addPrintToQueue(em, accountId, locationId, info, orderHeader,printer.getId());
						}else if ( printersType.getName().equalsIgnoreCase("Label Printer") && locationSetting.isNeedLabelPrinting() && (orderStatus!=null && !orderStatus.getName().replace(" ", "").contains("OrderAhead"))) {
							 info = convertOrderToPrinterResponseForLabelPrinting(orderHeader, printer.getPrintersName(), em, printersModel.getModelNumber());
						       info = info.replace(" & ", " &amp; ");
						    // adding print to queue
								addPrintToQueue(em, accountId, locationId, info, orderHeader,printer.getId());
							
						}
					}

					else
					{
						
						OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderHeader.getOrderSourceId());
						OrderStatus orderStatusPaidPrint = new CommonMethods().getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "PaidPrint", locationId,
								orderSource.getOrderSourceGroupId());
						OrderStatus orderStatusCODPrint = new CommonMethods().getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "CashOnDeliveryPrint", locationId,
								orderSource.getOrderSourceGroupId());
						
						OrderStatus orderAheadCashOnDeliveryPrint = new CommonMethods().getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "OrderAheadCashOnDeliveryPrint", locationId,
								orderSource.getOrderSourceGroupId());
						OrderStatus orderAheadPaidPrint = new CommonMethods().getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "OrderAheadPaidPrint", locationId,
								orderSource.getOrderSourceGroupId());
//						OrderHeader oldOrderHeader = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em,OrderHeader.class, orderHeader.getId());
						OrderStatus oldOrderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, orderHeader.getOrderStatusId());

						for (OrderDetailItem detailItems : orderDetailItems)
						{
							OrderDetailStatus detailStatusKOTPrinter = getOrderDetailStatus(em, locationId, "KOT Printed");
							OrderDetailStatus oldDetailStatus = em.find(OrderDetailStatus.class, detailItems.getOrderDetailStatusId());
							
							EntityTransaction tx = em.getTransaction();
							tx.begin();
							if (!oldDetailStatus.getName().equals("KOT Printed") && !oldDetailStatus.getName().equals("Ready to Order") && !oldDetailStatus.getName().equals("Reopen"))
							{
							detailItems.setOrderDetailStatusId(detailStatusKOTPrinter.getId());
							em.merge(detailItems);
							}
							if(orderStatusPaidPrint.getId()!=orderHeader.getOrderStatusId())
							{
								if(oldOrderStatus.getName().equals("Cash On Delivery")){
									orderHeader.setOrderStatusId(orderStatusCODPrint.getId());
								}else if (oldOrderStatus.getName().equals("Order Ahead Cash On Delivery")) {
									orderHeader.setOrderStatusId(orderAheadCashOnDeliveryPrint.getId());
								}else if (oldOrderStatus.getName().equals("Order Ahead Paid")) {
									orderHeader.setOrderStatusId(orderAheadPaidPrint.getId());
								}  else if(!oldOrderStatus.getName().equals("Order Paid") && orderHeader.getBalanceDue().doubleValue()>0){	
									orderHeader.setOrderStatusId(orderStatusCODPrint.getId());
								}else {
									orderHeader.setOrderStatusId(orderStatusPaidPrint.getId());
								}
								
								
							em.merge(orderHeader);
							}
							tx.commit();
							
						}
					}
			}
				}
			}
		}

	/**
	 * Update response counter for account and location and printer.
	 *
	 * @param accountId the account id
	 * @param locationId the location id
	 * @param printerId the printer id
	 * @param responseCount the response count
	 */
	private void updateResponseCounterForAccountAndLocationAndPrinter(int accountId, String locationId, String printerId,
			int responseCount) {
		String key = accountId+":"+locationId+":"+printerId;
		// overwrite whatever value exists right now
		RESPONSE_COUNTER_MAP.put(key, new Integer(responseCount));
		
	}

	/**
	 * Gets the response count for account and location and printer.
	 *
	 * @param accountId the account id
	 * @param locationId the location id
	 * @param printerId the printer id
	 * @return the response count for account and location and printer
	 */
	private int getResponseCountForAccountAndLocationAndPrinter(int accountId, String locationId, String printerId) {
		String key = accountId+":"+locationId+":"+printerId;
		Integer count = RESPONSE_COUNTER_MAP.get(key);
		if(count!=null)
		{
			return count.intValue();
		}
		return 0;
	}

	/**
	 * Gets the entity manager.
	 *
	 * @param accountId the account id
	 * @return the entity manager
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws NirvanaXPException the nirvana XP exception
	 */
	private EntityManager getEntityManager(int accountId) throws FileNotFoundException, IOException, NirvanaXPException
	{
		EntityManager em = null;
		EntityManager globalEm = GlobalSchemaEntityManager.getInstance().getEntityManager();
		try
		{
			Account account = globalEm.find(Account.class, accountId);

			if (account == null)
			{
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ACCOUNT_EXCEPTION_CODE, MessageConstants.ACCOUNT_EXCEPTION, MessageConstants.ACCOUNT_EXCEPTION));
			}

			String schemaName = account.getSchemaName();
			if (schemaName == null)
			{
				throw new NirvanaXPException(
						new NirvanaServiceErrorResponse(MessageConstants.SCHEMA_NAME_EXCEPTION_CODE, MessageConstants.SCHEMA_NAME_EXCEPTION, MessageConstants.SCHEMA_NAME_EXCEPTION));
			}

			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);

		}
		catch (NoResultException e)
		{
			logger.severe("Could not find account, during printer comm, for id: " + accountId);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEm);
		}
		return em;
	}

	/**
	 * Update order status.
	 *
	 * @param status the status
	 * @param em the em
	 * @param locationId the location id
	 * @param accountId the account id
	 * @param tx the tx
	 * @throws NirvanaXPException the nirvana XP exception
	 */
	private void updateOrderStatus(boolean status, EntityManager em, String locationId, int accountId,
			EntityTransaction tx) throws NirvanaXPException {
		// getting last printed order
		PrintQueue printQueueProcessing = getPrintQueueByOrderDetailIdAndPrinterId(em, printQueueId);
		if (printQueueProcessing != null) {
			// updating Print status
			if (status) {
				printQueueProcessing.setStatus("P");
			} else {
				// rejected
				printQueueProcessing.setStatus("R");
			}

			tx = em.getTransaction();
			tx.begin();
			em.merge(printQueueProcessing);
			tx.commit();
			String[] orderDetailsIds = null;
			if (printQueueProcessing.getOrderDetailItemId() != null
					&& printQueueProcessing.getOrderDetailItemId().length()>0) {
				orderDetailsIds = printQueueProcessing.getOrderDetailItemId().split(",");
			}
			
			// update detail item status
			OrderDetailStatus detailStatusKOTPrinter = getOrderDetailStatus(em, locationId, "KOT Printed");
			OrderDetailStatus detailStatusKOTNotPrinter = getOrderDetailStatus(em, locationId, "KOT Not Printed");

			OrderDetailItem detailItem = null;
			List<OrderDetailAttribute> orderDetailAttributes = null;
			if (orderDetailsIds != null) {
				for (String orderDetailItem : orderDetailsIds) {
					detailItem = (OrderDetailItem) new CommonMethods().getObjectById("OrderDetailItem", em,OrderDetailItem.class, orderDetailItem);
					orderDetailAttributes = new ArrayList<OrderDetailAttribute>();

					if (status ) {
						detailItem.setOrderDetailStatusId(detailStatusKOTPrinter.getId());
					} else {
						// todo shlok need
						// empty else
						detailItem.setOrderDetailStatusId(detailStatusKOTNotPrinter.getId());
					}
					String queryStringForAttribute = "select c from OrderDetailAttribute c where c.orderDetailItemId ="
							+ detailItem.getId();
					TypedQuery<OrderDetailAttribute> queryForAttribute = em.createQuery(queryStringForAttribute,
							OrderDetailAttribute.class);
					orderDetailAttributes = queryForAttribute.getResultList();
					tx = em.getTransaction();
					tx.begin();
					if (orderDetailAttributes != null && orderDetailAttributes.size() != 0) {
						for (OrderDetailAttribute detailAttribute : orderDetailAttributes) {

							if (status) {
								detailAttribute.setOrderDetailStatusId(detailStatusKOTPrinter.getId());
							} else {
								// todo shlok need
								// empty else
//								detailAttribute.setOrderDetailStatusId(detailStatusKOTNotPrinter.getId());
							}

							em.merge(detailAttribute);
						}
					}
					em.merge(detailItem);
					tx.commit();
				}
			}
			// find existing data in database to update the status of
			// transaction
			PrintQueue printQueue = getPrintFromDatabaseByOrderId(em, accountId, locationId, "'N','R'",
					printQueueProcessing.getOrderId());

			if (printQueue == null || status == false) {
				if (printQueue == null) {
					printQueue = printQueueProcessing;
				}
				OrderHeader header = new OrderManagementServiceBean().getOrderById(em, printQueue.getOrderId());

				OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, header.getOrderSourceId());
				OrderStatus oldOrderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, header.getOrderStatusId());

				if (orderSource != null) {
					OrderStatus orderStatusPaidNoPrint = new CommonMethods()
							.getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "PaidNoPrint", locationId,
									orderSource.getOrderSourceGroupId());					
					OrderStatus orderStatusPaidPrint = new CommonMethods()
							.getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "PaidPrint", locationId,
									orderSource.getOrderSourceGroupId());
					OrderStatus orderStatusCODNoPrint = new CommonMethods()
					.getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "CashOnDeliveryNoPrint", locationId,
							orderSource.getOrderSourceGroupId());
					OrderStatus orderStatusCODPrint = new CommonMethods()
					.getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "CashOnDeliveryPrint", locationId,
							orderSource.getOrderSourceGroupId());
			OrderStatus orderAheadCashOnDeliveryPrint = new CommonMethods()
					.getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "OrderAheadCashOnDeliveryPrint", locationId,
							orderSource.getOrderSourceGroupId());
			OrderStatus orderAheadCashOnDeliveryNoPrint = new CommonMethods()
			.getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "OrderAheadCashOnDeliveryNoPrint", locationId,
					orderSource.getOrderSourceGroupId());
			OrderStatus orderAheadPaidPrint = new CommonMethods()
			.getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "OrderAheadPaidPrint", locationId,
					orderSource.getOrderSourceGroupId());
			OrderStatus orderAheadPaidNoPrint = new CommonMethods()
			.getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "OrderAheadPaidNoPrint", locationId,
					orderSource.getOrderSourceGroupId());
					
					OrderStatus orderStatusReadyToOrder = new CommonMethods()
							.getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "Ready to Order", locationId,
									orderSource.getOrderSourceGroupId());
					if (header != null) {
						
						OrderStatus orderStatus = new OrderStatus();
						// be default paid no print
						if(oldOrderStatus.getName().equals("Cash On Delivery")|| oldOrderStatus.getName().equals("CashOnDeliveryPrint") || oldOrderStatus.getName().equals("CashOnDeliveryNoPrint")){
							orderStatus = orderStatusCODNoPrint;
						}else if (oldOrderStatus.getName().equals("Order Ahead Cash On Delivery")|| oldOrderStatus.getName().equals("OrderAheadCashOnDeliveryPrint") || oldOrderStatus.getName().equals("OrderAheadCashOnDeliveryNoPrint")) {
							orderStatus = orderAheadCashOnDeliveryNoPrint;
						}else if (oldOrderStatus.getName().equals("Order Ahead Paid")|| oldOrderStatus.getName().equals("OrderAheadPaidPrint") || oldOrderStatus.getName().equals("OrderAheadPaidNoPrint")) {
							orderStatus = orderAheadPaidNoPrint;
						}else if(!oldOrderStatus.getName().equals("Order Paid") && header.getBalanceDue().doubleValue()>0){	
							orderStatus=orderStatusCODNoPrint;
						} else{	
							orderStatus = orderStatusPaidNoPrint;
						}
						// if status is printed then set PaidPrint
						List<OrderDetailItem> detailItems = getOrderDetailItems(em, header.getId(),
								detailStatusKOTPrinter.getId());
						if (detailItems == null || detailItems.size() == 0) {
							if(oldOrderStatus.getName().equals("Cash On Delivery")|| oldOrderStatus.getName().equals("CashOnDeliveryPrint") || oldOrderStatus.getName().equals("CashOnDeliveryNoPrint")){
								orderStatus = orderStatusCODPrint;
							}else if (oldOrderStatus.getName().equals("Order Ahead Cash On Delivery")|| oldOrderStatus.getName().equals("OrderAheadCashOnDeliveryPrint") || oldOrderStatus.getName().equals("OrderAheadCashOnDeliveryNoPrint")) {
								orderStatus = orderAheadCashOnDeliveryPrint;
							}else if (oldOrderStatus.getName().equals("Order Ahead Paid")|| oldOrderStatus.getName().equals("OrderAheadPaidPrint") || oldOrderStatus.getName().equals("OrderAheadPaidNoPrint")) {
								orderStatus = orderAheadPaidPrint;
							}else if(!oldOrderStatus.getName().equals("Order Paid") && header.getBalanceDue().doubleValue()>0){	
								orderStatus=orderStatusCODPrint;
							} else{	
								orderStatus = orderStatusPaidPrint;
							}
						}

						// getting paid print status from database
						if (orderStatus != null) {
							// set status of order as "PaidPrint"
							if (orderStatus != orderStatusReadyToOrder && !oldOrderStatus.getName().equals("Ready to Order")) {
								header.setOrderStatusId(orderStatus.getId());
							}
							
							OrderHeader headerdb = new OrderManagementServiceBean().getOrderById(em, printQueue.getOrderId());

							OrderStatus oldOrderStatusdb = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, headerdb.getOrderStatusId());
							if((!oldOrderStatusdb.getName().equals("Ready to Order"))&& (orderStatus.getName().equals("PaidPrint") || orderStatus.getName().equals("CashOnDeliveryPrint")) ){
								try {
									// insert into order history
									ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
									// 0 because mail sending from dine in
									String data = receiptPDFFormat
											.createReceiptPDFString(em, httpRequest, header.getId(), 1, false,false)
											.toString();
									// Send email functionality :- printing
									// order
									// number instead of orderID :- By AP
									// 2015-12-29
									EmailTemplateKeys.sendOrderConfirmationEmailToCustomer(httpRequest, em, locationId,
											header.getUsersId(), header.getUpdatedBy(), data,
											EmailTemplateKeys.ORDER_CONFIRMATION, header.getOrderNumber(), null);

								} catch (Exception e) {
									logger.severe(httpRequest, e, "Could not send email due to configuration mismatch");
								}
							}
							
						} else {
							throw new NirvanaXPException(new NirvanaServiceErrorResponse(
									MessageConstants.ERROR_CODE_NO_RESULT_FOUND_FOR_ORDER_STATUS,
									MessageConstants.ERROR_MESSAGE_NO_RESULT_FOUND_FOR_ORDER_STATUS, null));
						}

						tx = em.getTransaction();
						try {
							// start transaction
							tx.begin();
							em.merge(header);
							tx.commit();
						} catch (RuntimeException e) {
							// on error, if transaction active,
							// rollback
							if (tx != null && tx.isActive()) {
								tx.rollback();
							}
							throw e;
						}
					}
					OrderPacket orderPacket = new OrderPacket();
					String clientId = "e83273kjkd67e10d66c";
					String echoString = locationId + "-" + clientId
							+ "-OrderManagementService-updateOrderStatus-" + locationId
							+ "-update order status";
					orderPacket.setClientId(clientId);
					orderPacket.setEchoString(echoString);
					OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(header);
					orderPacket.setOrderHeader(orderHeaderForPush);
					orderPacket.setLocationId(""+locationId);
					orderPacket.setMerchantId(""+accountId);
					sendPacketForBroadcast(httpRequest, orderPacket,
							POSNServiceOperations.OrderManagementService_updateOrderStatus.name(), false);
				} else {
					throw new NirvanaXPException(new NirvanaServiceErrorResponse(
							MessageConstants.ERROR_CODE_NO_RESULT_FOUND_FOR_ORDER_SOURCE,
							MessageConstants.ERROR_MESSAGE_NO_RESULT_FOUND_FOR_ORDER_SOURCE, null));
				}
			}
		}
	}

	/**
	 * Extract status from printer response.
	 *
	 * @param printResult the print result
	 * @return true, if successful
	 */
	private boolean extractStatusFromPrinterResponse(String printResult) {

		String lookingFor = "success=\"";
		String endWith = "\" code=\"";
		int start = printResult.indexOf(lookingFor);
		int end = printResult.indexOf(endWith);

		if (start > 0 && end > 0 && end>start) {
		    return Boolean.valueOf(printResult.substring(start + 9, end));
		   }else if(start > 0 && end > 0 && end<start){
		    lookingFor =  "success=\"";
		     endWith = "\" xmlns";
		     start=  printResult.indexOf(lookingFor);
		     end=   printResult.indexOf(endWith);

		       if (start > 0 && end > 0) {
		       return Boolean.valueOf(printResult.substring(start + 9, end));
		       }
		   }
		return false;
	}

	/**
	 * Convert order to printer response for itemize printing.
	 *
	 * @param orderHeader the order header
	 * @param printerName the printer name
	 * @param em the em
	 * @param printerModelNumber the printer model number
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String convertOrderToPrinterResponseForItemizePrinting(OrderHeader orderHeader, String printerName,
			EntityManager em, String printerModelNumber) throws IOException {

		if (printerModelNumber.equals("TM-U220")) {
			return new CloudReceiptFormat().createKitchenReceiptPDFStringForU220(em, httpRequest, orderHeader, 1,
					printerName);
		} else if (printerModelNumber.equals("TM-88VI")) {
			return new CloudReceiptFormat().createKitchenReceiptPDFStringForTMT88VI(em, httpRequest, orderHeader, 1,
					printerName);

		} else {
			return new CloudReceiptFormat().createKitchenReceiptPDFString(em, httpRequest, orderHeader, 1, printerName);
		}

	}

	/**
	 * Gets the unprinted online order for printer ID.
	 *
	 * @param em the em
	 * @param locationId the location id
	 * @return the unprinted online order for printer ID
	 * @throws NirvanaXPException the nirvana XP exception
	 */
	@SuppressWarnings("unchecked")
	private List<String> getUnprintedOnlineOrderForPrinterID(EntityManager em, String locationId)
			throws NirvanaXPException {

		// get order as "Order Paid"
		String queryString = "select oh.id from order_header oh left join order_status oss on oh.order_status_id =oss.id left join order_source os on  os.id=oh.order_source_id where "
				+ "  os.name in ('Web','Mobile App') and oss.name in ('Order Paid','Order Ahead Paid', 'Cash On Delivery','Order Ahead Cash On Delivery') and oh.locations_id=?";

		Query query = em.createNativeQuery(queryString).setParameter(1, locationId);
		return query.getResultList();

	}

	@Override
	@GET
	@Path("isAlive")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean isAlive() {
		return true;
	}

	@Override
	protected NirvanaLogger getNirvanaLogger() {
		return logger;
	}

	// ******************* TEST SECTION ********************//

	// All methods in this section are used to test printer communication

	// this is our flag to note that we do not have to respond to printer with a
	/** The allow print flag. */
	// print request
	private static boolean allowPrintFlag = false;

	/**
	 * Call this method from browser to set the flag to indicate that when
	 * printer pings next then send test data to print.
	 *
	 * @return true, if successful
	 */
	@GET
	@Path("/addPrint")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean addPrint() {
		logger.info("Setting Print Flag to true", "current instance of service is: " + this);
		allowPrintFlag = true;
		return allowPrintFlag;
	}

	/**
	 * When this method is configured on the printer configuration page as the
	 * direct server url then based on the flag above, it may return data to
	 * print.
	 *
	 * @param connectionType the connection type
	 * @param printerId the printer id
	 * @param printResult the print result
	 * @return the order to print
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_XML)
	@Path("/getOrderToPrint")
	public String getOrderToPrint(@FormParam("ConnectionType") String connectionType, @FormParam("ID") String printerId,
			@FormParam("ResponseFile") String printResult) throws FileNotFoundException, IOException {
		logger.info(httpRequest, "Incoming data: connection type=", connectionType, "; printer id=", printerId);

		if ("GetRequest".equals(connectionType)) {
			if (!allowPrintFlag) {
				return "";
			}

			allowPrintFlag = false;

			String testData = getTestData();

			logger.info("Sending back data to print: ", testData);

			return testData;
		}

		if ("SetResponse".equals(connectionType)) {
			logger.info("Got response from printer:", printResult);

		}

		return null;
	}

	/**
	 * Gets the test data.
	 *
	 * @return the test data
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String getTestData() throws FileNotFoundException, IOException {
		String xmlFilePath = ConfigFileReader.getEpsonTestXML();
		byte[] encoded = Files.readAllBytes(Paths.get(xmlFilePath));
		return new String(encoded, "utf-8");
	}

	/**
	 * Gets the order source by name and location id.
	 *
	 * @param em the em
	 * @param name the name
	 * @param locationId the location id
	 * @return the order source by name and location id
	 */
	public OrderSource getOrderSourceByNameAndLocationId(EntityManager em, String name, String locationId) {
		OrderSource resultSet = null;
		try {
			String queryString = "select os from OrderSource os  where os.name ='" + name + "' and  os.locationsId= "
					+ locationId;
			TypedQuery<OrderSource> query = em.createQuery(queryString, OrderSource.class);
			resultSet = query.getSingleResult();

		} catch (Exception e) {

			// todo shlok need
			// handle proper Exception
			logger.severe(httpRequest, e);
		}

		return resultSet;
	}

	/**
	 * Send packet for broadcast.
	 *
	 * @param httpRequest the http request
	 * @param orderPacket the order packet
	 * @param operationName the operation name
	 * @param shouldSendUSerInfoToo the should send U ser info too
	 * @throws NirvanaXPException the nirvana XP exception
	 */
	private void sendPacketForBroadcast(HttpServletRequest httpRequest, OrderPacket orderPacket, String operationName, boolean shouldSendUSerInfoToo) throws NirvanaXPException
	{
		try
		{
			// so that session id value does not get broadcasted
			orderPacket.setIdOfSessionUsedByPacket(0);
			orderPacket.setSessionId(null);

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

			String internalJson = null;
			if (!shouldSendUSerInfoToo)
			{
				internalJson = objectMapper.writeValueAsString(orderPacket.getOrderHeader());
			}
			else
			{
				internalJson = objectMapper.writeValueAsString(orderPacket);
			}

			operationName = ServiceOperationsUtility.getOperationName(operationName);
			MessageSender messageSender = new MessageSender();
			messageSender.sendMessage(httpRequest, orderPacket.getClientId(), POSNServices.OrderManagementService.name(), operationName, internalJson, orderPacket.getMerchantId(),
					orderPacket.getLocationId(), orderPacket.getEchoString(), orderPacket.getSchemaName());
		}
		catch (IOException e)
		{
			// could not send push
			// todo shlok need
			// handle proper Exception
			logger.severe(httpRequest, e, "Unable to broadcast order packet");
		}
	}
	
	/**
	 * Gets the order header with minimun required details.
	 *
	 * @param orderHeader the order header
	 * @return the order header with minimun required details
	 */
	private OrderHeader getOrderHeaderWithMinimunRequiredDetails(OrderHeader orderHeader) {
		OrderHeader orderHeaderForPush = new OrderHeader();
		orderHeaderForPush.setId(orderHeader.getId());
		orderHeaderForPush.setLocationsId(orderHeader.getLocationsId());
		orderHeaderForPush.setReservationsId(orderHeader.getReservationsId());
		orderHeaderForPush.setOrderStatusId(orderHeader.getOrderStatusId());
		orderHeaderForPush.setPointOfServiceCount(orderHeader.getPointOfServiceCount());
		orderHeaderForPush.setCreated(orderHeader.getCreated());
		orderHeaderForPush.setUpdated(orderHeader.getUpdated());
		orderHeaderForPush.setBalanceDue(orderHeader.getBalanceDue());
		orderHeaderForPush.setOrderSourceId(orderHeader.getOrderSourceId());
		orderHeaderForPush.setAmountPaid(orderHeader.getAmountPaid());
		orderHeaderForPush.setFirstName(orderHeader.getFirstName());
		orderHeaderForPush.setLastName(orderHeader.getLastName());
		orderHeaderForPush.setVoidReasonId(orderHeader.getVoidReasonId());
		orderHeaderForPush.setIsOrderReopened(orderHeader.getIsOrderReopened());
		orderHeaderForPush.setTaxExemptId(orderHeader.getTaxExemptId());
		orderHeaderForPush.setScheduleDateTime(orderHeader.getScheduleDateTime());
		orderHeaderForPush.setOrderNumber(orderHeader.getOrderNumber());

		String correctDateFormat = null;
		correctDateFormat = ConfigFileReader.correctDateFormat(orderHeader.getDate());

		if (correctDateFormat != null && correctDateFormat.trim().length() > 0) {
			orderHeaderForPush.setDate(correctDateFormat);
		} else {
			orderHeaderForPush.setDate(orderHeader.getDate());
		}
		orderHeaderForPush.setCreatedBy(orderHeader.getCreatedBy());
		orderHeaderForPush.setPartySizeUpdated(orderHeader.isPartySizeUpdated());
		if (orderHeader.getCloseTime() != 0) {
			orderHeaderForPush.setCloseTime(orderHeader.getCloseTime());
		}
		orderHeaderForPush.setIsTabOrder(orderHeader.getIsTabOrder());
		orderHeaderForPush.setServername(orderHeader.getServername());
		orderHeaderForPush.setAddressShipping(orderHeader.getAddressShipping());
		orderHeaderForPush.setNirvanaXpBatchNumber(orderHeader.getNirvanaXpBatchNumber());
		return orderHeaderForPush;
	}



	/**
	 * Gets the items to printer list.
	 *
	 * @param em the em
	 * @param itemId the item id
	 * @return the items to printer list
	 */
	private List<ItemsToPrinter> getItemsToPrinterList(EntityManager em, String itemId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ItemsToPrinter> criteria = builder.createQuery(ItemsToPrinter.class);
		Root<ItemsToPrinter> r = criteria.from(ItemsToPrinter.class);

		TypedQuery<ItemsToPrinter> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToPrinter_.itemsId), itemId),
						builder.notEqual(r.get(ItemsToPrinter_.status), "D")));

		return query.getResultList();

	}

	/**
	 * Gets the category to printer list.
	 *
	 * @param em the em
	 * @param categoryId the category id
	 * @return the category to printer list
	 */
	private List<CategoryToPrinter> getCategoryToPrinterList(EntityManager em, String categoryId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<CategoryToPrinter> criteria = builder.createQuery(CategoryToPrinter.class);
		Root<CategoryToPrinter> r = criteria.from(CategoryToPrinter.class);

		TypedQuery<CategoryToPrinter> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryToPrinter_.categoryId), categoryId),
						builder.notEqual(r.get(CategoryToPrinter_.status), "D")));

		return query.getResultList();

	}

	/**
	 * Gets the prints the from database.
	 *
	 * @param em the em
	 * @param accountId the account id
	 * @param locationId the location id
	 * @param status the status
	 * @return the prints the from database
	 */
	private PrintQueue getPrintFromDatabase(EntityManager em, int accountId, String locationId, String status) {

		try {
			String queryString = "select p from PrintQueue p where p.accountId =? and p.locationId= ? and status =? order by id asc ";
			TypedQuery<PrintQueue> query = em.createQuery(queryString, PrintQueue.class).setParameter(1, accountId)
					.setParameter(2, locationId).setParameter(3, status);
			List<PrintQueue> resultSet = query.getResultList();
			if (resultSet != null && resultSet.size() > 0) {
				return resultSet.get(0);
			}
			return null;
		} catch (NoResultException e) {
			// todo shlok need
			// handle proper Exception
			logger.severe(httpRequest, e);
			return null;
		}
	}

	/**
	 * Gets the prints the from database by order id.
	 *
	 * @param em the em
	 * @param accountId the account id
	 * @param locationId the location id
	 * @param status the status
	 * @param orderId the order id
	 * @return the prints the from database by order id
	 */
	private PrintQueue getPrintFromDatabaseByOrderId(EntityManager em, int accountId, String locationId, String status,
			String orderId) {
		try {
			String queryString = "select p from PrintQueue p where p.accountId =? and p.locationId= ? and status in (?) and orderId = ? order by id asc ";
			TypedQuery<PrintQueue> query = em.createQuery(queryString, PrintQueue.class).setParameter(1, accountId)
					.setParameter(2, locationId).setParameter(3, status).setParameter(4, orderId);
			List<PrintQueue> resultSet = query.getResultList();
			if (resultSet != null && resultSet.size() > 0) {
				return resultSet.get(0);
			}
			return null;
		} catch (NoResultException e) {
			// todo shlok need
			// handle proper Exception
			logger.severe(httpRequest, e);
			return null;
		}
	}

	/**
	 * Adds the print to queue.
	 *
	 * @param em the em
	 * @param accountId the account id
	 * @param locationId the location id
	 * @param printString the print string
	 * @param header the header
	 * @param printerId the printer id
	 * @return the prints the queue
	 */
	private PrintQueue addPrintToQueue(EntityManager em, int accountId, String locationId, String printString,
			OrderHeader header,String printerId) {
		PrintQueue printQueue = new PrintQueue();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		printQueue.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printQueue.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
		printQueue.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printQueue.setCreatedBy(header.getUsersId());
		printQueue.setUpdatedBy(header.getUsersId());
		printQueue.setStatus("N");
		printQueue.setAccountId(accountId);
		printQueue.setPrintString(printString);
		printQueue.setLocationId(locationId);
		printQueue.setOrderId(header.getId());
		printQueue.setPrinterId(printerId);
		String detailItemsId = "";
		for (int i = 0; i < header.getOrderDetailItems().size(); i++) {
			OrderDetailItem detailItem = header.getOrderDetailItems().get(i);
			if (i == (header.getOrderDetailItems().size() - 1)) {
				detailItemsId += detailItem.getId();
			} else {
				detailItemsId += detailItem.getId() + ",";
			}
		}
		printQueue.setOrderDetailItemId(detailItemsId);
		printQueue = em.merge(printQueue);
		tx.commit();
		return printQueue;

	}

	/**
	 * Gets the order detail status.
	 *
	 * @param em the em
	 * @param locationId the location id
	 * @param name the name
	 * @return the order detail status
	 */
	private OrderDetailStatus getOrderDetailStatus(EntityManager em, String locationId, String name) {
		String queryString = "select ods from OrderDetailStatus ods where ods.name = ? and ods.locationsId = ? ";
		TypedQuery<OrderDetailStatus> query = em.createQuery(queryString, OrderDetailStatus.class).setParameter(1, name)
				.setParameter(2, locationId);
		return query.getSingleResult();
	}

	/**
	 * Gets the order detail items.
	 *
	 * @param em the em
	 * @param orderId the order id
	 * @param kotPrintedStatusId the kot printed status id
	 * @return the order detail items
	 */
	private List<OrderDetailItem> getOrderDetailItems(EntityManager em, String orderId, int kotPrintedStatusId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderDetailItem> criteria = builder.createQuery(OrderDetailItem.class);
			Root<OrderDetailItem> r = criteria.from(OrderDetailItem.class);

			TypedQuery<OrderDetailItem> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(OrderDetailItem_.orderHeaderId), orderId),
							builder.notEqual(r.get(OrderDetailItem_.orderDetailStatusId), kotPrintedStatusId)));

			return query.getResultList();
		} catch (Exception e) {
			// todo shlok need
			// handle proper Exception
			logger.severe(e, "error in printer comm during getOrderDetailItems");
		}
		return null;

	}

	/**
	 * Filter order header.
	 *
	 * @param em the em
	 * @param orderId the order id
	 * @param orderDetailItemList the order detail item list
	 * @return the list
	 */
	private List<PrinterOrderList> filterOrderHeader(EntityManager em, String orderId, List<OrderDetailItem> orderDetailItemList)
	{
		PrinterOrderList printerOrderObj = null;
		List<PrinterOrderList> printerOrderList = new ArrayList<PrinterOrderList>();
		for (OrderDetailItem orderDetailItem : orderDetailItemList)
		{
			if (orderDetailItem.getId()!=null )
			{
				Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, orderDetailItem.getItemsId());
				List<ItemsToPrinter> itemsToPrinterList = getItemsToPrinterList(em, item.getId());
				if (itemsToPrinterList != null && itemsToPrinterList.size() != 0)
				{

					for (ItemsToPrinter itemsToPrinter : itemsToPrinterList)
					{

						printerOrderObj = new PrinterOrderList();
						printerOrderObj.setOrderDetailItem(orderDetailItem);
						printerOrderObj.setPrinterId(itemsToPrinter.getPrintersId());
						printerOrderList.add(printerOrderObj);
						// making null for GC
						printerOrderObj = null;

					}
				}
				else
				{
					Category category = null;
					if (orderDetailItem.getParentCategoryId() != null)
					{
						category = (Category) new CommonMethods().getObjectById("Category", em,Category.class, orderDetailItem.getParentCategoryId());
					}
					else
					{
						CriteriaBuilder builder = em.getCriteriaBuilder();
						CriteriaQuery<CategoryItem> criteria = builder.createQuery(CategoryItem.class);
						Root<CategoryItem> categoryItem = criteria.from(CategoryItem.class);
						TypedQuery<CategoryItem> query = em.createQuery(criteria.select(categoryItem).where(builder.equal(categoryItem.get(CategoryItem_.itemsId), orderDetailItem.getItemsId()),
								builder.notEqual(categoryItem.get(CategoryItem_.status), "D")));
						CategoryItem categoryItem2 = query.getSingleResult();
						category = (Category) new CommonMethods().getObjectById("Category", em,Category.class, categoryItem2.getCategoryId());
					}
					List<CategoryToPrinter> categoryToPrinters = getCategoryToPrinterList(em, category.getId());
					if (categoryToPrinters != null)
					{
						for (CategoryToPrinter categoryToPrinter : categoryToPrinters)
						{

							printerOrderObj = new PrinterOrderList();
							printerOrderObj.setOrderDetailItem(orderDetailItem);
							printerOrderObj.setPrinterId(categoryToPrinter.getPrintersId());

							printerOrderList.add(printerOrderObj);
							// making null for GC
							printerOrderObj = null;
						}
					}
				}

			}
		}

		return printerOrderList;

	}
	
	/**
	 * Gets the printer by printer id.
	 *
	 * @param em the em
	 * @param printerId the printer id
	 * @return the printer by printer id
	 */
	public Printer getPrinterByPrinterId(EntityManager em, String printerId)
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Printer> criteria = builder.createQuery(Printer.class);
			Root<Printer> ic = criteria.from(Printer.class);
			TypedQuery<Printer> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(Printer_.id), printerId), builder.notEqual(ic.get(Printer_.status), "D"),
					builder.notEqual(ic.get(Printer_.status), "I")));
			return query.getSingleResult();
		}
		catch (NoResultException e)
		{
			// todo shlok need
			// handle proper Exception
			return null;
		}

	}
	
	/**
	 * Gets the prints the queue by order detail id and printer id.
	 *
	 * @param em the em
	 * @param printQueueId the print queue id
	 * @return the prints the queue by order detail id and printer id
	 */
	public PrintQueue getPrintQueueByOrderDetailIdAndPrinterId(EntityManager em, BigInteger printQueueId)
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PrintQueue> criteria = builder.createQuery(PrintQueue.class);
			Root<PrintQueue> ic = criteria.from(PrintQueue.class);
			TypedQuery<PrintQueue> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(PrintQueue_.id), printQueueId),builder.notEqual(ic.get(PrintQueue_.status), "P")));
			return query.getSingleResult();
		}
		catch (NoResultException e)
		{
			// todo shlok need
			// handle proper Exception
			return null;
		}

	}
	
	private String convertOrderToPrinterResponseForLabelPrinting(OrderHeader orderHeader, String printerName,
			EntityManager em, String printerModelNumber) throws IOException {
		
		try {
			
			if (printerModelNumber.equals("TM-U220")) {
				return new CloudReceiptFormat().createLabelPDFStringForU220(em, httpRequest, orderHeader, 1,
						printerName);
			} else if (printerModelNumber.equals("TM-88VI")) {
				return new CloudReceiptFormat().createLabelPDFStringForTMT88VI(em, httpRequest, orderHeader, 1,
						printerName);

			} else {
				return new CloudReceiptFormat().createLabelPDFString(em, httpRequest, orderHeader, 1, printerName);
			}
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;

	}
 
}
