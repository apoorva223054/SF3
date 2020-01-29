package com.nirvanaxp.server.util;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.services.jaxrs.packets.KDSToOrderDetailItemStatusPacket;
import com.nirvanaxp.types.entities.catalog.category.Category;
import com.nirvanaxp.types.entities.catalog.category.CategoryToPrinter;
import com.nirvanaxp.types.entities.catalog.category.CategoryToPrinter_;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem_;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.ItemsToPrinter;
import com.nirvanaxp.types.entities.catalog.items.ItemsToPrinter_;
import com.nirvanaxp.types.entities.email.PrintQueue;
import com.nirvanaxp.types.entities.email.PrintQueue_;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.orders.KDSToOrderDetailItemStatus;
import com.nirvanaxp.types.entities.orders.KDSToOrderDetailItemStatus_;
import com.nirvanaxp.types.entities.orders.OrderDetailAttribute;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderHeaderForKDS;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.orders.OrderStatus_;
import com.nirvanaxp.types.entities.printers.Printer;
import com.nirvanaxp.types.entities.printers.Printer_;
import com.nirvanaxp.types.entities.printers.PrintersModel;
import com.nirvanaxp.types.entities.printers.PrintersType;

public class PrinterUtility
{

	private final static NirvanaLogger logger = new NirvanaLogger(PrinterUtility.class.getName());
	public static int isOrderBumped;

	public List<PrintQueue> insertIntoPrintQueueForCustomer(HttpServletRequest httpRequest, EntityManager em, OrderHeader header, String locationId)
	{
		OrderHeaderForKDS orderHeader = new OrderHeaderForKDS().getOrderHeaderWithLimitedFields(em, header, locationId,
				new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em),null);
		List<OrderDetailItem> orderDetailItemsArray = orderHeader.getOrderDetailItems();
		String orderId = orderHeader.getId();
		List<PrinterOrderList> printerOrderList = null;
		printerOrderList = filterOrderHeaderForCustomer(em, orderHeader.getId(), orderDetailItemsArray);
		List<OrderDetailItem> newOrderDetailItemList = null;
		List<PrintQueue> printQueueList = new ArrayList<PrintQueue>();
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
			PrintQueue printQueue = null;
			ArrayList<OrderDetailItem> orderDetailItems = new ArrayList<OrderDetailItem>();
			
			for (OrderDetailItem orderDetailItem : newOrderDetailItemList)
			{
				OrderDetailStatus detailStatus = em.find(OrderDetailStatus.class, orderDetailItem.getOrderDetailStatusId());
				PrintQueue row = getPrintQueueByOrderDetailIdAndPrinterId(em, orderDetailItem.getId(), orderDetailItem.getPrinterId());
				
				if (row != null && detailStatus != null && !detailStatus.getName().equals("Item saved") && !detailStatus.getName().equals("Item Removed"))
				{

					row = updatePrintQueue(httpRequest, em, orderDetailItem, detailStatus.getId(), row, null, orderHeader, orderHeader.getIsSeatWiseOrder());
				}
				if (detailStatus != null && !detailStatus.getName().equals("Item saved") && !detailStatus.getName().equals("Item Removed") && row == null)
				{
					orderDetailItems.add(orderDetailItem);
				}
			}
			
			if (orderDetailItems != null && orderDetailItems.size() != 0)
			{
				printer = getPrinterByPrinterId(em, orderDetailItems.get(0).getPrinterId());
				
				if (printer != null && !printer.getPrintersName().equals("No Printer"))
				{
					List<PrintQueue> rowList = getPrintQueueByOrderIdAndLocationIdAndPinterId(em, orderId, orderHeader.getLocationsId(), printer.getId());
					OrderHeaderForKDS headerForKDS = null;
					BigInteger printQueueId = BigInteger.ZERO;

					if (rowList != null && rowList.size() > 0)
					{
						PrintQueue row = rowList.get(rowList.size() - 1);
						if (row != null)
						{
							try
							{
								printQueueId = row.getId();
								headerForKDS = new ObjectMapper().readValue(row.getPrintString(), OrderHeaderForKDS.class);
							}
							catch (JsonParseException e)
							{
								// TODO Auto-generated catch block
								logger.severe(e);
							}
							catch (JsonMappingException e)
							{
								// TODO Auto-generated catch block
								logger.severe(e);
							}
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								logger.severe(e);
							}
						}
					}
					List<OrderDetailItem> detailItems = new ArrayList<OrderDetailItem>();
					detailItems.addAll(orderDetailItems);
					if (headerForKDS != null)
					{
						detailItems.addAll(headerForKDS.getOrderDetailItems());
					}
					orderHeader.setOrderDetailItems(detailItems);
					PrintersType printersType=(PrintersType) new CommonMethods().getObjectById("PrintersType", em,PrintersType.class, printer.getPrintersTypeId());
					if(printersType.getName().equals("Label Printer")){
						orderHeader.setLablePrinter(true);
						orderHeader.setIpAddress(printer.getIpAddress());
					}
					PrintersModel printersModel = em.find(PrintersModel.class, printer.getPrintersModelId());
					info = new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(orderHeader);
					// adding print to queue
					printQueue = addPrintToQueue(em, printer.getId(), orderHeader.getLocationsId(), info, orderHeader, printersModel.getModelNumber(), printQueueId);
					printQueueList.add(printQueue);
				}
			}
		}
		return printQueueList;

	}

	private List<PrinterOrderList> filterOrderHeaderForCustomer(EntityManager em, String orderId, List<OrderDetailItem> orderDetailItemList)
	{
		PrinterOrderList printerOrderObj = null;
		List<PrinterOrderList> printerOrderList = new ArrayList<PrinterOrderList>();
		for (OrderDetailItem orderDetailItem : orderDetailItemList)
		{
			if (orderDetailItem.getId()!= null && orderDetailItem.getId()!=null)
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

	private List<ItemsToPrinter> getItemsToPrinterList(EntityManager em, String itemId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ItemsToPrinter> criteria = builder.createQuery(ItemsToPrinter.class);
		Root<ItemsToPrinter> r = criteria.from(ItemsToPrinter.class);

		TypedQuery<ItemsToPrinter> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToPrinter_.itemsId), itemId), builder.notEqual(r.get(ItemsToPrinter_.status), "D")));

		return query.getResultList();

	}

	private List<CategoryToPrinter> getCategoryToPrinterList(EntityManager em, String categoryId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<CategoryToPrinter> criteria = builder.createQuery(CategoryToPrinter.class);
		Root<CategoryToPrinter> r = criteria.from(CategoryToPrinter.class);

		TypedQuery<CategoryToPrinter> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryToPrinter_.categoryId), categoryId), builder.notEqual(r.get(CategoryToPrinter_.status), "D")));

		return query.getResultList();

	}

	private PrintQueue addPrintToQueue(EntityManager em, String printerId, String locationId, String printString, OrderHeader header, String printerModel, BigInteger printQueueId)
	{
		PrintQueue printQueue = new PrintQueue();

		if (printerModel.equalsIgnoreCase("NXP-KDS")||printerModel.equalsIgnoreCase("NXP-Prod-KDS"))
		{
			if (printQueueId!=null && printQueueId.compareTo(BigInteger.ZERO) == 0)
			{
				printQueue.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
				printQueue.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				printQueue.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			}
			else
			{
				printQueue.setId(printQueueId);
				try {
					PrintQueue printQueueDB=em.find(PrintQueue.class, printQueueId);
					if(printQueueDB!=null){
						printQueue.setUpdated(printQueueDB.getUpdated());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			printQueue.setCreatedBy(header.getUsersId());
			printQueue.setUpdatedBy(header.getUsersId());
			printQueue.setStatus("U");
			printQueue.setPrinterId(printerId);
			printQueue.setPrintString(printString);
			printQueue.setLocationId(locationId);
			printQueue.setOrderId(header.getId());
			printQueue.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
			String detailItemsId = "";
			for (int i = 0; i < header.getOrderDetailItems().size(); i++)
			{
				OrderDetailItem detailItem = header.getOrderDetailItems().get(i);
				if (i == (header.getOrderDetailItems().size() - 1))
				{
					detailItemsId += detailItem.getId();
				}
				else
				{
					detailItemsId += detailItem.getId() + ",";
				}
			}
			printQueue.setOrderDetailItemId(detailItemsId);
			String orderStatusName=getOrderStatusName(em, header.getOrderStatusId());
			if (orderStatusName.replace(" ", "").contains("OrderAhead")) {
				printQueue.setIsOrderAhead(1);
			} else {
				printQueue.setIsOrderAhead(0);
			}
			  printQueue.setScheduleDateTime(header.getScheduleDateTime());
			  
			  try {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date scheduleDateTime=formatter.parse(printQueue.getScheduleDateTime());
					if(scheduleDateTime.after(printQueue.getUpdated())){
						printQueue.setUpdated(formatter.parse(printQueue.getScheduleDateTime()));
						}
					} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  
			printQueue = em.merge(printQueue);
		}
		return printQueue;

	}

	public PrintQueue getPrintQueueByOrderDetailIdAndPrinterId(EntityManager em, String orderDetailItemId, String printerId)
	{
		try
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PrintQueue> criteria = builder.createQuery(PrintQueue.class);
			Root<PrintQueue> ic = criteria.from(PrintQueue.class);
			TypedQuery<PrintQueue> query = em.createQuery(
					criteria.select(ic).where(builder.equal(ic.get(PrintQueue_.printerId), printerId), builder.like(ic.get(PrintQueue_.orderDetailItemId), "%" + orderDetailItemId + "%")));

			
			return query.getSingleResult();
		}
		catch (NoResultException e)
		{
			return null;
		}

	}

	public KDSToOrderDetailItemStatus getkdsToODIStatusByOrderDetailIdAndPrinterId(EntityManager em, String orderDetailItemId, String printerId)
	{
		try
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<KDSToOrderDetailItemStatus> criteria = builder.createQuery(KDSToOrderDetailItemStatus.class);
			Root<KDSToOrderDetailItemStatus> ic = criteria.from(KDSToOrderDetailItemStatus.class);
			TypedQuery<KDSToOrderDetailItemStatus> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(KDSToOrderDetailItemStatus_.orderDetailItemId), orderDetailItemId),
					builder.equal(ic.get(KDSToOrderDetailItemStatus_.printerId), printerId)));
			return query.getSingleResult();
		}
		catch (NoResultException e)
		{
			return null;
		}

	}

	public PrintQueue updatePrintQueue(HttpServletRequest httpRequest, EntityManager em, OrderDetailItem updatedOrderDetailItem, int statusId, PrintQueue printQueue,
			KDSToOrderDetailItemStatus kdsToOrderDetailItemStatus, OrderHeaderForKDS updatedHeaderForKDS, int isSeatWiseOrder)
	{

		try
		{
			
			boolean needToUpdate = false;
			boolean needToUpdateKOTStatus = true;
			boolean needToUpdateStatus= false;
			OrderHeaderForKDS headerForKDS = new ObjectMapper().readValue(printQueue.getPrintString(), OrderHeaderForKDS.class);
			OrderDetailStatus detailStatus = em.find(OrderDetailStatus.class, statusId);
			Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, printQueue.getPrinterId());
			for (OrderDetailItem orderDetailItem : headerForKDS.getOrderDetailItems())
			{
				if (updatedOrderDetailItem.getId().compareTo(orderDetailItem.getId()) ==0 )
				{

					if (detailStatus != null && (detailStatus.getName().equals("Recall") || detailStatus.getName().equals("Item Ready") || detailStatus.getName().equals("Item Displayed")))
					{
						orderDetailItem.setOrderDetailStatusId(detailStatus.getId());
						orderDetailItem.setOrderDetailStatusName(detailStatus.getName());
						
						if (kdsToOrderDetailItemStatus != null)
						{
//							String statusName = getOrderDetailStatusName(em, kdsToOrderDetailItemStatus.getStatusId());
							String statusName=detailStatus.getName();
							if (statusName.equals("Item Displayed") && printQueue.getStatus().equals("B"))
							{
								
								printQueue.setStatus("U");
							}else if (statusName.equals("Item Ready")) {
								needToUpdateStatus=true;
							}
							
							
							kdsToOrderDetailItemStatus.setOrderDetailStatusName(statusName);
							List<KDSToOrderDetailItemStatus> kdsToOrderDetailItemStatusList = new ArrayList<KDSToOrderDetailItemStatus>();
							if (orderDetailItem.getKdsToOrderDetailItemStatusList() != null)
							{
								kdsToOrderDetailItemStatusList.addAll(orderDetailItem.getKdsToOrderDetailItemStatusList());
							}
							kdsToOrderDetailItemStatusList.add(kdsToOrderDetailItemStatus);
							orderDetailItem.setKdsToOrderDetailItemStatusList(kdsToOrderDetailItemStatusList);
						}
						needToUpdate = true;
					}
					else if (isSeatWiseOrder != -1 && updatedOrderDetailItem.getSeatId() != orderDetailItem.getSeatId())
					{
						orderDetailItem.setSeatId(updatedOrderDetailItem.getSeatId());
						needToUpdate = true;
					}
				}
				if (printer.getIsAutoBumpOn() == 0 || orderDetailItem.getOrderDetailStatusName().equals("Item Displayed"))
				{
					needToUpdateKOTStatus = false;
				}
			}
			if (needToUpdateKOTStatus)
			{
				isOrderBumped = 1;
				needToUpdate = true;
				printQueue.setStatus("B");
				kdsToOrderDetailItemStatus.setIsOrderBumped(1);
				OrderHeader header = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em,OrderHeader.class, printQueue.getOrderId());
				OrderStatus currentOrderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, header.getOrderStatusId());
				if ( !currentOrderStatus.getName().equals("Bus Ready") && !currentOrderStatus.getName().equals("Ready to Order")
						&& !currentOrderStatus.getName().equals("Ready To Serve") && !currentOrderStatus.getName().equals("Reopen"))
				{
					OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, header.getOrderSourceId());
					OrderStatus orderStatus = null;
					OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
					if(header.getOrderTypeId() == 2)
					{
						orderStatus = getOrderStatusByNameAndLocation(em, "Close Production", printQueue.getLocationId(), orderSource.getOrderSourceGroupId());
					}else
					{
						if (orderSourceGroup.getName().equals("Pick Up")){
						orderStatus = getOrderStatusByNameAndLocation(em, "Ready for pick up", printQueue.getLocationId(), orderSource.getOrderSourceGroupId());
						}else if (orderSourceGroup.getName().equals("Delivery")) {
							orderStatus = getOrderStatusByNameAndLocation(em, "Ready for delivery", printQueue.getLocationId(), orderSource.getOrderSourceGroupId());
								
						}else {
							if(!currentOrderStatus.getName().equals("Order Paid") && !currentOrderStatus.getName().equals("Cash On Delivery"))
							orderStatus = getOrderStatusByNameAndLocation(em, "Ready To Serve", printQueue.getLocationId(), orderSource.getOrderSourceGroupId());
							
						}
						
						}
					
					if(orderStatus!=null){
					header.setOrderStatusId(orderStatus.getId());
					header = em.merge(header);
					if(header!=null){
						printQueue.setOrderStatusId(header.getOrderStatusId());
						}
					}
				}
			}else if (needToUpdateStatus) {

				OrderHeader	 header = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em,OrderHeader.class, printQueue.getOrderId());
				OrderStatus currentOrderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, header.getOrderStatusId());
				if ( !currentOrderStatus.getName().equals("Bus Ready") && !currentOrderStatus.getName().equals("Ready to Order")
						 && !currentOrderStatus.getName().equals("Reopen")&& !currentOrderStatus.getName().equals("Quality Check") && !currentOrderStatus.getName().equals("Ready for pick up")
						 && !currentOrderStatus.getName().equals("Ready for delivery")&& !currentOrderStatus.getName().equals("Ready for Serve"))
				{
					OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, header.getOrderSourceId());
					OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
					
						if (orderSourceGroup.getName().equals("Delivery") || orderSourceGroup.getName().equals("Pick Up")){
							OrderStatus orderStatus = getOrderStatusByNameAndLocation(em, "Quality Check", printQueue.getLocationId(), orderSource.getOrderSourceGroupId());
							header.setOrderStatusId(orderStatus.getId());
							header = em.merge(header);
							if(header!=null){
								printQueue.setOrderStatusId(header.getOrderStatusId());
								}
						}else {
							needToUpdateStatus=false;
						}
					
				}else {
					needToUpdateStatus=false;
				}
			
				
			} 
			if (updatedHeaderForKDS != null && !needToUpdate && updatedHeaderForKDS.getOrderSourceId() != headerForKDS.getOrderSourceId())
			{
				needToUpdate = true;

				headerForKDS.setOrderSourceId(updatedHeaderForKDS.getOrderSourceId());
				headerForKDS.setOrderSourceName(updatedHeaderForKDS.getOrderSourceName());
				headerForKDS.setOrderSourceGroupName(updatedHeaderForKDS.getOrderSourceGroupName());
				headerForKDS.setLocationsId(updatedHeaderForKDS.getLocationsId());
				headerForKDS.setLocationName(updatedHeaderForKDS.getLocationName());
				headerForKDS.setOrderDetailItems(updatedHeaderForKDS.getOrderDetailItems());
			}
			
			if (needToUpdate)
			{
				if (isSeatWiseOrder != -1)
				{
					headerForKDS.setIsSeatWiseOrder(isSeatWiseOrder);
				}
				printQueue.setPrintString(new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValuesAndNotDefault(headerForKDS));
			
				printQueue = em.merge(printQueue);
				return printQueue;
			}
			else
			{
				return printQueue;
			}

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	public void insertIntoPrintQueueForCancelOrderAndQuickPay(HttpServletRequest httpRequest, EntityManager em, OrderHeader orderHeader, String parentLocationsId)
	{

		try
		{
			OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, orderHeader.getOrderStatusId());
			OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderHeader.getOrderSourceId());
			OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());

			 if (orderStatus != null && orderStatus.getName().equals("Cancel Order"))
			{
				String queryString = "UPDATE print_queue SET status='C' WHERE order_id=?";
				em.createNativeQuery(queryString, PrintQueue.class).setParameter(1, orderHeader.getId()).executeUpdate();
				
			}else if (orderSourceGroup.getName().equals("In Store") && orderHeader.getReservationsId()!= null && orderHeader.getReservationsId() == null && orderHeader.getIsTabOrder() == 0)

			{
				insertIntoPrintQueue(httpRequest, em, orderHeader, parentLocationsId);
			}
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
	}

	public void insertIntoPrintQueueForOrderTransfer(HttpServletRequest httpRequest, EntityManager em, OrderHeader toOrderHeader, String parentLocationsId, String fromOrderHeaderId)
	{

		try
		{
			/*
			 * String locationsId=0; if(parentLocationsId!=null &&
			 * parentLocationsId.length()>0){
			 * locationsId=Integer.parseInt(parentLocationsId); }
			 * 
			 * List<PrintQueue>printQueues=getPrintQueueByOrderIdAndLocationId(
			 * em , fromOrderHeaderId, locationsId);
			 * List<OrderDetailItem>orderDetailItemList=new
			 * ArrayList<OrderDetailItem>(); for(PrintQueue
			 * printQueue:printQueues){ String orderDetailItemIds[] =
			 * printQueue.getOrderDetailItemId().split(","); if
			 * (orderDetailItemIds != null && orderDetailItemIds.length > 0) {
			 * for (String idString : orderDetailItemIds) { int
			 * orderDetailItemId=Integer.parseInt(idString); OrderDetailItem
			 * detailItem=(OrderDetailItem) new CommonMethods().getObjectById("OrderDetailItem", em,OrderDetailItem.class,orderDetailItemId);
			 * orderDetailItemList.e detailItem.setDeviceToKDSIds("detailItem");
			 * } } }
			 */
			String queryString = "DELETE from print_queue where order_id=?";
			em.createNativeQuery(queryString, PrintQueue.class).setParameter(1, fromOrderHeaderId).executeUpdate();
			insertIntoPrintQueue(httpRequest, em, toOrderHeader, parentLocationsId);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
	}

	public void insertIntoPrintQueueForItemTransfer(HttpServletRequest httpRequest, EntityManager em, OrderHeader toOrderHeader, String parentLocationsId, OrderHeader fromOrderHeader)
	{

		try
		{

			String queryString = "DELETE from print_queue where order_id in (?,?)";
			em.createNativeQuery(queryString, PrintQueue.class).setParameter(1, fromOrderHeader.getId()).setParameter(2, toOrderHeader.getId()).executeUpdate();
			insertIntoPrintQueue(httpRequest, em, fromOrderHeader, parentLocationsId);
			insertIntoPrintQueue(httpRequest, em, toOrderHeader, parentLocationsId);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
	}

	/*
	 * public void insertIntoPrintQueueForOrderItemTransfer(HttpServletRequest
	 * httpRequest,EntityManager em,OrderHeader orderHeader, String
	 * parentLocationsId,int fromOrderHeaderId ){
	 * 
	 * try { getPrintQueueByOrderIdAndLocationId(em, orderHeader.getId(),
	 * parentLocationsId); insertIntoPrintQueue(httpRequest,
	 * em,orderHeader,parentLocationsId); String queryString =
	 * "DELETE from print_queue where order_id=?";
	 * em.createNativeQuery(queryString, PrintQueue.class).setParameter(1,
	 * fromOrderHeaderId).executeUpdate();
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block
	 * logger.severe(e); } }
	 */
	public List<PrintQueue> getPrintQueueByOrderIdAndLocationIdAndPinterId(EntityManager em, String orderId, String locationId, String printerId)
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PrintQueue> criteria = builder.createQuery(PrintQueue.class);
			Root<PrintQueue> ic = criteria.from(PrintQueue.class);
			TypedQuery<PrintQueue> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(PrintQueue_.orderId), orderId), builder.equal(ic.get(PrintQueue_.locationId), locationId),
					builder.equal(ic.get(PrintQueue_.printerId), printerId), builder.equal(ic.get(PrintQueue_.status), "U")));
			return query.getResultList();
		}
		catch (NoResultException e)
		{
			return null;
		}

	}

	public OrderStatus getOrderStatusByNameAndLocation(EntityManager em, String statusName, String locationId)
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> r = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.name), statusName), builder.equal(r.get(OrderStatus_.locationsId), locationId)));
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
	}

	private String getOrderDetailStatusName(EntityManager em, int statusId)
	{
		OrderDetailStatus detailStatus = em.find(OrderDetailStatus.class, statusId);
		return detailStatus.getName();
	}
	private String getOrderStatusName(EntityManager em, String statusId)
	{
		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, statusId);
		return orderStatus.getName();
	}
	public void insertIntoPrintQueueForPartySizeUpdate(HttpServletRequest httpRequest, EntityManager em, OrderHeader header, String parentLocationsId, boolean isPartySizeUpdated)
	{

		try
		{

			OrderHeaderForKDS kds = new OrderHeaderForKDS().getOrderHeaderWithLimitedFields(em, header, parentLocationsId,
					new TimezoneTime().getLocationSpecificTimeToAdd(parentLocationsId, em),null);
			List<PrintQueue> printQueuesList = getPrintQueueByOrderIdAndLocationId(em, header.getId(), kds.getLocationsId());
			OrderHeaderForKDS headerForKDS = null;
			if (printQueuesList != null)
			{
				for (PrintQueue printQueue : printQueuesList)
				{
					try
					{
						headerForKDS = new ObjectMapper().readValue(printQueue.getPrintString(), OrderHeaderForKDS.class);

					}
					catch (JsonParseException e)
					{
						// TODO Auto-generated catch block
						logger.severe(e);
					}
					catch (JsonMappingException e)
					{
						// TODO Auto-generated catch block
						logger.severe(e);
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						logger.severe(e);
					}
					if (isPartySizeUpdated)
					{
						headerForKDS.setPointOfServiceCount(header.getPointOfServiceCount());
					}
					else
					{
						Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, header.getLocationsId());
						headerForKDS.setLocationsId(header.getLocationsId());
						headerForKDS.setLocationName(location.getName());
						headerForKDS.setIsTabOrder(header.getIsTabOrder());
					}
					printQueue.setPrintString(new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValuesAndNotDefault(headerForKDS));
					printQueue = em.merge(printQueue);
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
	}

	public List<PrintQueue> getPrintQueueByOrderIdAndLocationId(EntityManager em, String orderId, String locationId)
	{
		try
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PrintQueue> criteria = builder.createQuery(PrintQueue.class);
			Root<PrintQueue> ic = criteria.from(PrintQueue.class);
			TypedQuery<PrintQueue> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(PrintQueue_.orderId), orderId), builder.equal(ic.get(PrintQueue_.locationId), locationId),
					builder.equal(ic.get(PrintQueue_.status), "U")));
			return query.getResultList();
		}
		catch (NoResultException e)
		{
			return null;
		}

	}

	public Printer getPrinterByPrinterId(EntityManager em, String printerId)
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Printer> criteria = builder.createQuery(Printer.class);
			Root<Printer> ic = criteria.from(Printer.class);
			TypedQuery<Printer> query = em.createQuery(
					criteria.select(ic).where(builder.equal(ic.get(Printer_.id), printerId), builder.notEqual(ic.get(Printer_.status), "D"), builder.notEqual(ic.get(Printer_.status), "I")));
			return query.getSingleResult();
		}
		catch (NoResultException e)
		{
			return null;
		}

	}

	public List<KDSToOrderDetailItemStatus> insertIntoKDSToOrderDetailItemStatus(HttpServletRequest httpRequest, EntityManager em, List<KDSToOrderDetailItemStatus> kdsToOrderDetailItemStatusList)
	{
		List<KDSToOrderDetailItemStatus> newKdsToOrderDetailItemStatusList = new ArrayList<KDSToOrderDetailItemStatus>();
		for (KDSToOrderDetailItemStatus kdsToOrderDetailItemStatus : kdsToOrderDetailItemStatusList)
		{
			KDSToOrderDetailItemStatus kdsToOrderDetailItemStatus2 = getkdsToODIStatusByOrderDetailIdAndPrinterId(em, kdsToOrderDetailItemStatus.getOrderDetailItemId(),
					kdsToOrderDetailItemStatus.getPrinterId());
			if (kdsToOrderDetailItemStatus2 == null)
			{

				OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class, kdsToOrderDetailItemStatus.getStatusId());

				if (orderDetailStatus != null)
				{
					kdsToOrderDetailItemStatus.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(orderDetailStatus.getLocationsId(), em));
				}

				kdsToOrderDetailItemStatus.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				kdsToOrderDetailItemStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				kdsToOrderDetailItemStatus = em.merge(kdsToOrderDetailItemStatus);
				newKdsToOrderDetailItemStatusList.add(kdsToOrderDetailItemStatus);
				PrintQueue printQueue = getPrintQueueByOrderDetailIdAndPrinterId(em, kdsToOrderDetailItemStatus.getOrderDetailItemId(), kdsToOrderDetailItemStatus.getPrinterId());
				if (printQueue != null)
				{
					OrderDetailItem detailItem = (OrderDetailItem) new CommonMethods().getObjectById("OrderDetailItem", em,OrderDetailItem.class, kdsToOrderDetailItemStatus.getOrderDetailItemId());

					printQueue = updatePrintQueue(httpRequest, em, detailItem, kdsToOrderDetailItemStatus.getStatusId(), printQueue, kdsToOrderDetailItemStatus, null, -1);
				}

			}
			else
			{
				if (!getOrderDetailStatusName(em, kdsToOrderDetailItemStatus2.getStatusId()).equals("Item Ready"))
				{
					kdsToOrderDetailItemStatus.setId(kdsToOrderDetailItemStatus2.getId());
					kdsToOrderDetailItemStatus.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					kdsToOrderDetailItemStatus = em.merge(kdsToOrderDetailItemStatus);
					newKdsToOrderDetailItemStatusList.add(kdsToOrderDetailItemStatus);
					PrintQueue printQueue = getPrintQueueByOrderDetailIdAndPrinterId(em, kdsToOrderDetailItemStatus.getOrderDetailItemId(), kdsToOrderDetailItemStatus.getPrinterId());
					if (printQueue != null)
					{
						OrderDetailItem detailItem = (OrderDetailItem) new CommonMethods().getObjectById("OrderDetailItem", em,OrderDetailItem.class, kdsToOrderDetailItemStatus.getOrderDetailItemId());

						printQueue = updatePrintQueue(httpRequest, em, detailItem, kdsToOrderDetailItemStatus.getStatusId(), printQueue, kdsToOrderDetailItemStatus, null, -1);
					}
				}
			}
		}

		return newKdsToOrderDetailItemStatusList;

	}
	
	

	public List<KDSToOrderDetailItemStatus> insertIntoKDSToOrderDetailItemStatusForUpdate(HttpServletRequest httpRequest, EntityManager em,
			KDSToOrderDetailItemStatusPacket	 packet)
	{

		List<KDSToOrderDetailItemStatus> newKdsToOrderDetailItemStatusList = new ArrayList<KDSToOrderDetailItemStatus>();
		for (KDSToOrderDetailItemStatus kdsToOrderDetailItemStatus : packet.getKdsToOrderDetailItemStatusList())
		{
			OrderDetailItem detailItem = (OrderDetailItem) new CommonMethods().getObjectById("OrderDetailItem", em,OrderDetailItem.class, kdsToOrderDetailItemStatus.getOrderDetailItemId());

//			OrderHeader header = new OrderManagementServiceBean().getOrderById(em, orderHeader.getId());
			List<Printer> printerArray = getKDSPrinterListByItemId(em, detailItem.getItemsId());
			if (printerArray != null)
			{
				for (Printer printer : printerArray)
				{

					KDSToOrderDetailItemStatus kdsToOrderDetailItemStatus2 = getkdsToODIStatusByOrderDetailIdAndPrinterId(em, kdsToOrderDetailItemStatus.getOrderDetailItemId(), printer.getId());
					if (kdsToOrderDetailItemStatus2 != null)
					{
						kdsToOrderDetailItemStatus2.setStatusId(kdsToOrderDetailItemStatus.getStatusId());
						kdsToOrderDetailItemStatus2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						kdsToOrderDetailItemStatus2 = em.merge(kdsToOrderDetailItemStatus2);
						newKdsToOrderDetailItemStatusList.add(kdsToOrderDetailItemStatus2);

						PrintQueue printQueue = null;
						try
						{
							printQueue = getPrintQueueByOrderDetailIdAndPrinterId(em, kdsToOrderDetailItemStatus2.getOrderDetailItemId(), kdsToOrderDetailItemStatus2.getPrinterId());
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							logger.severe(e);
						}
						if (printQueue != null)
						{
							printQueue = updatePrintQueue(httpRequest, em, detailItem, kdsToOrderDetailItemStatus2.getStatusId(), printQueue, kdsToOrderDetailItemStatus2, null, -1);
							if(printQueue.getOrderStatusId()!=null){
//							kdsToOrderDetailItemStatus2.setOrderHeaderId(printQueue.getOrderId());
//							kdsToOrderDetailItemStatus2.setOrderStatusId(printQueue.getOrderStatusId());
								packet.setOrderHeaderId(""+printQueue.getOrderId());
								packet.setOrderStatusId(printQueue.getOrderStatusId());
							}
							
						}
						newKdsToOrderDetailItemStatusList.add(kdsToOrderDetailItemStatus2);
					}
					

				}
			}
		}

		return newKdsToOrderDetailItemStatusList;		

	}
	

	public List<PrintQueue> insertIntoPrintQueue(HttpServletRequest httpRequest, EntityManager em, OrderHeader header, String locationId)
	{
		OrderHeaderForKDS orderHeader = new OrderHeaderForKDS().getOrderHeaderWithLimitedFields(em, header, locationId,
				new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em),null);
		List<OrderDetailItem> orderDetailItemsArray = orderHeader.getOrderDetailItems();
		String orderId = orderHeader.getId();
		List<OrderDetailItem> newOrderDetailItemList = null;
		List<PrintQueue> printQueueList = new ArrayList<PrintQueue>();
		List<PrinterOrderList> printerOrderList = null;
		printerOrderList = filterOrderHeader(em, orderHeader.getId(), orderDetailItemsArray);

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
			PrintQueue printQueue = null;
			ArrayList<OrderDetailItem> orderDetailItems = new ArrayList<OrderDetailItem>();
			for (OrderDetailItem orderDetailItem : newOrderDetailItemList)
			{

				OrderDetailStatus detailStatus = em.find(OrderDetailStatus.class, orderDetailItem.getOrderDetailStatusId());
				PrintQueue row = getPrintQueueByOrderDetailIdAndPrinterId(em, orderDetailItem.getId(), orderDetailItem.getPrinterId());
				
				if (row != null && detailStatus != null && !detailStatus.getName().equals("Item saved") && !detailStatus.getName().equals("Item Removed"))
				{

					if (orderDetailItem.getOrderDetailAttributes() != null && orderDetailItem.getOrderDetailAttributes().size() > 0)
					{
						List<OrderDetailAttribute> attributesList = new ArrayList<OrderDetailAttribute>();
						for (OrderDetailAttribute detailAttribute : orderDetailItem.getOrderDetailAttributes())
						{

							OrderDetailStatus detailStatusAttr = em.find(OrderDetailStatus.class, detailAttribute.getOrderDetailStatusId());
							if (detailStatusAttr != null && !detailStatusAttr.getName().equals("Attribute Removed"))
							{
								attributesList.add(detailAttribute);
							}
						}

						orderDetailItem.setOrderDetailAttributes(attributesList);
					}

					row = updatePrintQueue(httpRequest, em, orderDetailItem, detailStatus.getId(), row, null, orderHeader, orderHeader.getIsSeatWiseOrder());
				}

				if (detailStatus != null && !detailStatus.getName().equals("Item saved") && !detailStatus.getName().equals("Item Removed") && row == null)
				{
					if (orderDetailItem.getOrderDetailAttributes() != null && orderDetailItem.getOrderDetailAttributes().size() > 0)
					{
						List<OrderDetailAttribute> attributesList = new ArrayList<OrderDetailAttribute>();
						for (OrderDetailAttribute detailAttribute : orderDetailItem.getOrderDetailAttributes())
						{

							OrderDetailStatus detailStatusAttr = em.find(OrderDetailStatus.class, detailAttribute.getOrderDetailStatusId());
							if (detailStatusAttr != null && !detailStatusAttr.getName().equals("Attribute Removed"))
							{
								attributesList.add(detailAttribute);
							}
						}

						orderDetailItem.setOrderDetailAttributes(attributesList);
					}

					orderDetailItems.add(orderDetailItem);
				}
			}

			if (orderDetailItems != null && orderDetailItems.size() != 0)
			{
				printer = getPrinterByPrinterId(em, orderDetailItems.get(0).getPrinterId());

				if (printer != null && !printer.getPrintersName().equals("No Printer"))
				{
					List<PrintQueue> rowList = getPrintQueueByOrderIdAndLocationIdAndPinterId(em, orderId, orderHeader.getLocationsId(), printer.getId());
					OrderHeaderForKDS headerForKDS = null;
					BigInteger printQueueId = BigInteger.ZERO;
					if (rowList != null && rowList.size() > 0)
					{
						PrintQueue row = rowList.get(rowList.size() - 1);
						if (row != null)
						{
							try
							{
								printQueueId = row.getId();
								headerForKDS = new ObjectMapper().readValue(row.getPrintString(), OrderHeaderForKDS.class);
							}
							catch (JsonParseException e)
							{
								// TODO Auto-generated catch block
								logger.severe(e);
							}
							catch (JsonMappingException e)
							{
								// TODO Auto-generated catch block
								logger.severe(e);
							}
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								logger.severe(e);
							}
						}
					}
					List<OrderDetailItem> detailItems = new ArrayList<OrderDetailItem>();
					detailItems.addAll(orderDetailItems);
					if (headerForKDS != null)
					{
						detailItems.addAll(headerForKDS.getOrderDetailItems());
					}
					
					// TODO Ankur: if it is null then what?
					orderHeader.setOrderDetailItems(detailItems);
					PrintersType printersType=(PrintersType) new CommonMethods().getObjectById("PrintersType", em,PrintersType.class, printer.getPrintersTypeId());
					if(printersType.getName().equals("Label Printer")){
						orderHeader.setLablePrinter(true);
						orderHeader.setIpAddress(printer.getIpAddress());
					}
					PrintersModel printersModel = em.find(PrintersModel.class, printer.getPrintersModelId());
					info = new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(orderHeader);
					// adding print to queue
					printQueue = addPrintToQueue(em, printer.getId(), orderHeader.getLocationsId(), info, orderHeader, printersModel.getModelNumber(), printQueueId);
					printQueueList.add(printQueue);
				}
			}
		}
		return printQueueList;

	}

	private List<PrinterOrderList> filterOrderHeader(EntityManager em, String orderId, List<OrderDetailItem> orderDetailItemList)
	{
		PrinterOrderList printerOrderObj = null;
		List<PrinterOrderList> printerOrderList = new ArrayList<PrinterOrderList>();
		for (OrderDetailItem orderDetailItem : orderDetailItemList)
		{
			List<KDSToOrderDetailItemStatus> kdsToOrderDetailItemStatus = getkdsToODIStatusByOrderDetailId(em, orderDetailItem.getId());

			if (kdsToOrderDetailItemStatus != null && kdsToOrderDetailItemStatus.size() > 0)
			{
				for (KDSToOrderDetailItemStatus detailItemStatus : kdsToOrderDetailItemStatus)
				{

					if (detailItemStatus != null)
					{
						printerOrderObj = new PrinterOrderList();
						printerOrderObj.setOrderDetailItem(orderDetailItem);
						printerOrderObj.setPrinterId(detailItemStatus.getPrinterId());
						printerOrderList.add(printerOrderObj);
						// making null for GC
						printerOrderObj = null;
					}

				}
			}
		}

		return printerOrderList;

	}

	public List<KDSToOrderDetailItemStatus> getkdsToODIStatusByOrderDetailId(EntityManager em, String orderDetailItemId)
	{
		try
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<KDSToOrderDetailItemStatus> criteria = builder.createQuery(KDSToOrderDetailItemStatus.class);
			Root<KDSToOrderDetailItemStatus> ic = criteria.from(KDSToOrderDetailItemStatus.class);
			TypedQuery<KDSToOrderDetailItemStatus> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(KDSToOrderDetailItemStatus_.orderDetailItemId), orderDetailItemId)));
			return query.getResultList();
		}
		catch (NoResultException e)
		{
			return null;
		}

	}

	public List<Printer> getKDSPrinterListByItemId(EntityManager em, String itemId)
	{

		String sql = "select p.* from printers p join items_to_printers ip on ip.printers_id= p.id and ip.status='A' join printers_model pm on pm.id =p.printers_model_id where ip.items_id=? and pm.model_number in ('NXP-KDS','NXP-Prod-KDS')";

		Query query = em.createNativeQuery(sql, Printer.class).setParameter(1, itemId);
		@SuppressWarnings("unchecked")
		List<Printer> resultSet = query.getResultList();

		return resultSet;

	}
	public List<Printer> getKDSListForProductionByItemId(EntityManager em, int itemId)
	{

		String sql = "select p.* from printers p join items_to_printers ip on ip.printers_id= p.id and ip.status='A' join printers_model pm on pm.id =p.printers_model_id where ip.items_id=? and pm.model_number in ('NXP-Prod-KDS')";

		Query query = em.createNativeQuery(sql, Printer.class).setParameter(1, itemId);
		@SuppressWarnings("unchecked")
		List<Printer> resultSet = query.getResultList();

		return resultSet;

	}
	public List<Printer> getKDSListForIntraTransferByItemId(EntityManager em, String itemId)
	{

		String sql = "select p.* from printers p join items_to_printers ip on ip.printers_id= p.id and ip.status='A' join printers_model pm on pm.id =p.printers_model_id where ip.items_id=? and pm.model_number ='NXP-KDS'";

		Query query = em.createNativeQuery(sql, Printer.class).setParameter(1, itemId);
		@SuppressWarnings("unchecked")
		List<Printer> resultSet = query.getResultList();

		return resultSet;

	}
	public OrderStatus getOrderStatusByNameAndLocation(EntityManager em, String statusName, String locationId, String orderSourceGroupId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
		Root<OrderStatus> r = criteria.from(OrderStatus.class);
		TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.name), statusName), builder.equal(r.get(OrderStatus_.locationsId), locationId),
				builder.equal(r.get(OrderStatus_.orderSourceGroupId), orderSourceGroupId)));
		return query.getSingleResult();
		
	}

public List<PrintQueue> insertIntoPrintQueueForInventory(HttpServletRequest httpRequest, EntityManager em, OrderHeader header, String locationId,
		String poRequestName)
{
	OrderHeaderForKDS orderHeader = new OrderHeaderForKDS().getOrderHeaderWithLimitedFields(em, header, locationId,
			new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em),poRequestName);
	List<OrderDetailItem> orderDetailItemsArray = orderHeader.getOrderDetailItems();
	String orderId = orderHeader.getId();
	List<PrinterOrderList> printerOrderList = null;
	printerOrderList = filterOrderHeader(em, orderHeader.getId(), orderDetailItemsArray);
	List<OrderDetailItem> newOrderDetailItemList = null;
	List<PrintQueue> printQueueList = new ArrayList<PrintQueue>();
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
		PrintQueue printQueue = null;
		ArrayList<OrderDetailItem> orderDetailItems = new ArrayList<OrderDetailItem>();
		
		for (OrderDetailItem orderDetailItem : newOrderDetailItemList)
		{
			OrderDetailStatus detailStatus = em.find(OrderDetailStatus.class, orderDetailItem.getOrderDetailStatusId());
			PrintQueue row = getPrintQueueByOrderDetailIdAndPrinterId(em, orderDetailItem.getId(), orderDetailItem.getPrinterId());
			
			if (row != null && detailStatus != null  && !detailStatus.getName().equals("Item Removed"))
			{

				row = updatePrintQueue(httpRequest, em, orderDetailItem, detailStatus.getId(), row, null, orderHeader, orderHeader.getIsSeatWiseOrder());
			}
			if (detailStatus != null && !detailStatus.getName().equals("Item Removed") && row == null)
			{
				orderDetailItems.add(orderDetailItem);
			}
		}
		
		if (orderDetailItems != null && orderDetailItems.size() != 0)
		{
			printer = getPrinterByPrinterId(em, orderDetailItems.get(0).getPrinterId());
			
			if (printer != null && !printer.getPrintersName().equals("No Printer"))
			{
				List<PrintQueue> rowList = getPrintQueueByOrderIdAndLocationIdAndPinterId(em, orderId, orderHeader.getLocationsId(), printer.getId());
				OrderHeaderForKDS headerForKDS = null;
				BigInteger printQueueId = BigInteger.ZERO;

				if (rowList != null && rowList.size() > 0)
				{
					PrintQueue row = rowList.get(rowList.size() - 1);
					if (row != null)
					{
						try
						{
							printQueueId = row.getId();
							headerForKDS = new ObjectMapper().readValue(row.getPrintString(), OrderHeaderForKDS.class);
						}
						catch (JsonParseException e)
						{
							// TODO Auto-generated catch block
							logger.severe(e);
						}
						catch (JsonMappingException e)
						{
							// TODO Auto-generated catch block
							logger.severe(e);
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							logger.severe(e);
						}
					}
				}
				
				List<OrderDetailItem> detailItems = new ArrayList<OrderDetailItem>();
				detailItems.addAll(orderDetailItems);
				if (headerForKDS != null)
				{
					detailItems.addAll(headerForKDS.getOrderDetailItems());
				}
				orderHeader.setOrderDetailItems(detailItems);
				PrintersType printersType=(PrintersType) new CommonMethods().getObjectById("PrintersType", em,PrintersType.class, printer.getPrintersTypeId());
				if(printersType.getName().equals("Label Printer")){
					orderHeader.setLablePrinter(true);
					orderHeader.setIpAddress(printer.getIpAddress());
				}
				PrintersModel printersModel = em.find(PrintersModel.class, printer.getPrintersModelId());
				info = new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(orderHeader);
				// adding print to queue
				printQueue = addPrintToQueue(em, printer.getId(), orderHeader.getLocationsId(), info, orderHeader, printersModel.getModelNumber(), printQueueId);
				printQueueList.add(printQueue);
			}
		}
	}
	return printQueueList;
}

public void insertIntoPrintQueueForStatusAndPartySize(HttpServletRequest httpRequest, EntityManager em, OrderHeader header, String locationId)
{
		List<PrintQueue> rowList = getPrintQueueByOrderIdAndLocationId(em, header.getId(), locationId);
			OrderHeaderForKDS headerForKDS;
			if (rowList != null && rowList.size() > 0)
			{
				PrintQueue row = rowList.get(rowList.size() - 1);
				if (row != null)
				{
					try
					{
						headerForKDS = new ObjectMapper().readValue(row.getPrintString(), OrderHeaderForKDS.class);
						headerForKDS.setPointOfServiceCount(header.getPointOfServiceCount());
						headerForKDS.setOrderStatusId(header.getOrderStatusId());
						headerForKDS.setScheduleDateTime(header.getScheduleDateTime());
						headerForKDS.setFirstName(header.getFirstName());
						headerForKDS.setLastName(header.getLastName());
						row.setScheduleDateTime(header.getScheduleDateTime());
						row.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						row.setUpdatedBy(header.getUsersId());
						row.setUpdated(header.getUpdated());
						row.setPrintString(new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValuesAndNotDefault(headerForKDS));
						em.merge(row);
					}
					catch (JsonParseException e)
					{
						// TODO Auto-generated catch block
						logger.severe(e);
					}
					catch (JsonMappingException e)
					{
						// TODO Auto-generated catch block
						logger.severe(e);
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						logger.severe(e);
					}
					
				}
				
			}
				
	}
	
public void insertIntoPrintQueueForOrderUpdateFromOutside(HttpServletRequest httpRequest, EntityManager em, OrderHeader header, String parentLocationsId)
{
	
	    String locationId=parentLocationsId;
		List<PrintQueue> rowList = getPrintQueueByOrderIdAndLocationId(em, header.getId(), locationId);
			if (rowList != null && rowList.size() > 0)
			{
				PrintQueue row = rowList.get(rowList.size() - 1);
				if (row != null)
				{
					try
					{
						OrderHeaderForKDS headerForKDS = new OrderHeaderForKDS().getOrderHeaderWithLimitedFieldsForUserAndSchedule(em, header,row.getPrintString());
						row.setScheduleDateTime(header.getScheduleDateTime());
						row.setPrintString(new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValuesAndNotDefault(headerForKDS));
						String orderStatusName=getOrderStatusName(em, header.getOrderStatusId());
					if (orderStatusName.replace(" ", "").contains("OrderAhead")) {
						row.setIsOrderAhead(1);
					} else {
						row.setIsOrderAhead(0);
					}
							   
						  try {
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Date scheduleDateTime=formatter.parse(row.getScheduleDateTime());
								if(scheduleDateTime.after(row.getUpdated())){
									row.setUpdated(formatter.parse(row.getScheduleDateTime()));
									}
								} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
								}
						
						em.merge(row);
					}
					catch (JsonParseException e)
					{
						// TODO Auto-generated catch block
						logger.severe(e);
					}
					catch (JsonMappingException e)
					{
						// TODO Auto-generated catch block
						logger.severe(e);
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						logger.severe(e);
					}
					
				}
				
			}
				
	}
	


}


