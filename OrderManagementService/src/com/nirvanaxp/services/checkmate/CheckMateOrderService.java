package com.nirvanaxp.services.checkmate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.google.zxing.WriterException;
import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.countries.City;
import com.nirvanaxp.global.types.entities.countries.Countries;
import com.nirvanaxp.global.types.entities.countries.State;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.MenuPacket;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.AbstractNirvanaService;
import com.nirvanaxp.services.jaxrs.INirvanaService;
import com.nirvanaxp.services.jaxrs.OrderHeaderCalculation;
import com.nirvanaxp.services.jaxrs.OrderManagementServiceBean;
import com.nirvanaxp.services.jaxrs.OrderServiceForPost;
import com.nirvanaxp.services.jaxrs.packets.SubmitPacket;
import com.nirvanaxp.storeForward.PaymentBatchManager;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.catalog.category.Category;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeType;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttributeType;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttributeType_;
import com.nirvanaxp.types.entities.checkmate.AllSections;
import com.nirvanaxp.types.entities.checkmate.Calculate;
import com.nirvanaxp.types.entities.checkmate.CalculateResponse;
import com.nirvanaxp.types.entities.checkmate.CheckMateDiscounts;
import com.nirvanaxp.types.entities.checkmate.CheckMateItems;
import com.nirvanaxp.types.entities.checkmate.CheckMateModifiers;
import com.nirvanaxp.types.entities.checkmate.CheckMatePaymentOptions;
import com.nirvanaxp.types.entities.checkmate.CustomerAddress;
import com.nirvanaxp.types.entities.checkmate.CustomerInfo;
import com.nirvanaxp.types.entities.checkmate.DiningOptions;
import com.nirvanaxp.types.entities.checkmate.DiningOptionsGroup;
import com.nirvanaxp.types.entities.checkmate.MenuSections;
import com.nirvanaxp.types.entities.checkmate.ModifierGroups;
import com.nirvanaxp.types.entities.checkmate.Payment;
import com.nirvanaxp.types.entities.checkmate.RootMenuSection;
import com.nirvanaxp.types.entities.checkmate.ServiceCharges;
import com.nirvanaxp.types.entities.checkmate.Submit;
import com.nirvanaxp.types.entities.checkmate.SubmitResponse;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.discounts.Discount_;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.orders.NirvanaIndex;
import com.nirvanaxp.types.entities.orders.OrderDetailAttribute;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus_;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup_;
import com.nirvanaxp.types.entities.orders.OrderSource_;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.orders.OrderStatus_;
import com.nirvanaxp.types.entities.payment.PaymentMethod;
import com.nirvanaxp.types.entities.payment.PaymentMethodType;
import com.nirvanaxp.types.entities.payment.PaymentMethodType_;
import com.nirvanaxp.types.entities.payment.PaymentMethod_;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType_;
import com.nirvanaxp.types.entities.payment.TransactionStatus;
import com.nirvanaxp.types.entities.payment.TransactionStatus_;
import com.nirvanaxp.types.entities.salestax.SalesTax;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.user.utility.GlobalUsermanagement;
import com.nirvanaxp.user.utility.UserManagementObj;

@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class CheckMateOrderService extends AbstractNirvanaService
{
	private final static NirvanaLogger logger = new NirvanaLogger(CheckMateOrderService.class.getName());
	private static final Address category = null;
	@Context
	HttpServletRequest httpRequest;

	@GET
	@Path("/getOrderSourceGroupByLocationIdForCM/{locationId}")
	public String getOrderSourceGroupByLocationIdForCM(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
			Root<OrderSourceGroup> orderSourceGroup = criteria.from(OrderSourceGroup.class);
			TypedQuery<OrderSourceGroup> query = em.createQuery(criteria.select(orderSourceGroup).where(builder.equal(orderSourceGroup.get(OrderSourceGroup_.locationsId), locationId),
					builder.notEqual(orderSourceGroup.get(OrderSourceGroup_.status), "D")));
			List<OrderSourceGroup> orderSourceGroups = query.getResultList();
			List<DiningOptionsGroup> diningOptionsGroupList = new ArrayList<DiningOptionsGroup>();
			DiningOptionsGroup diningOptionsGroup;
			DiningOptions diningOptions;
			for (OrderSourceGroup sourceGroup : orderSourceGroups)
			{
				List<DiningOptions> diningOptionsList = new ArrayList<DiningOptions>();
				
				diningOptionsGroup = new DiningOptionsGroup();
				diningOptionsGroup.setName(sourceGroup.getDisplayName());
				List<OrderSource>orderSourceList= getOrderSourceBySourceGroupIdAndLocationId(em, sourceGroup.getId(), locationId);
				for(OrderSource source : orderSourceList){
					diningOptions = new DiningOptions();
					diningOptions.setName(source.getDisplayName());
					diningOptions.setId(""+source.getId());
					diningOptionsList.add(diningOptions);
					diningOptionsGroup.setDiningOptionList(diningOptionsList);
				}
				diningOptionsGroupList.add(diningOptionsGroup);
			}
			return new JSONUtility(httpRequest).convertToJsonString(diningOptionsGroupList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getDiscountsByLocationIdForCM/{locationId}")
	public String getDiscountsByLocationIdForCM(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Discount> criteria = builder.createQuery(Discount.class);
			Root<Discount> discount = criteria.from(Discount.class);
			TypedQuery<Discount> query = em
					.createQuery(criteria.select(discount).where(builder.equal(discount.get(Discount_.locationsId), locationId), builder.notEqual(discount.get(Discount_.status), "D")));
			List<Discount> discountsList = query.getResultList();
			List<CheckMateDiscounts> checkMateDiscountsList = new ArrayList<CheckMateDiscounts>();
			CheckMateDiscounts checkMateDiscount;
			for (Discount discounts : discountsList)
			{
				checkMateDiscount = new CheckMateDiscounts();
				checkMateDiscount.setId("" + discounts.getId());
				checkMateDiscount.setName(discounts.getDisplayName());
				checkMateDiscountsList.add(checkMateDiscount);
			}
			return new JSONUtility(httpRequest).convertToJsonString(checkMateDiscountsList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getPaymentMethodByLocationIdAndPaymentTypeIdForCM/{locationId}")
	public String getPaymentMethodByLocationIdAndPaymentTypeId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			CriteriaBuilder builder = em.getCriteriaBuilder();

			PaymentMethodType paymentMethodType = getPaymentMethodTypeByLocationId(locationId, em);

			CriteriaQuery<PaymentMethod> criteria = builder.createQuery(PaymentMethod.class);
			Root<PaymentMethod> paymentMethod = criteria.from(PaymentMethod.class);
			TypedQuery<PaymentMethod> query = em.createQuery(criteria.select(paymentMethod).where(builder.equal(paymentMethod.get(PaymentMethod_.locationsId), locationId),
					builder.equal(paymentMethod.get(PaymentMethod_.paymentMethodTypeId), paymentMethodType.getId()), builder.notEqual(paymentMethod.get(PaymentMethod_.status), "D")));
			List<PaymentMethod> paymentMethodList = query.getResultList();
			List<CheckMatePaymentOptions> checkMatePaymentOptionsList = new ArrayList<CheckMatePaymentOptions>();
			CheckMatePaymentOptions paymentOptions;
			for (PaymentMethod method : paymentMethodList)
			{
				paymentOptions = new CheckMatePaymentOptions();
				paymentOptions.setId("" + method.getId());
				paymentOptions.setName(method.getDisplayName());
				checkMatePaymentOptionsList.add(paymentOptions);
			}
			return new JSONUtility(httpRequest).convertToJsonString(checkMatePaymentOptionsList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getServiceChargesforCM/{locationId}")
	public String getServiceChargesforCM(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// String string = getSalesTaxByNameAndLocationId(em, "Service
			// Charge", locationId).getDisplayName();
			List<SalesTax> salesTaxsList = getSalesTaxsByLocationId(em, locationId);
			List<ServiceCharges> serviceChargesList = new ArrayList<ServiceCharges>();
			ServiceCharges serviceCharges = null;
			for (SalesTax salesTax : salesTaxsList)
			{
				serviceCharges = new ServiceCharges();
				serviceCharges.setId("" + salesTax.getId());
				serviceCharges.setName(salesTax.getDisplayName());
				serviceCharges.setAmount(salesTax.getRate().doubleValue());
				serviceChargesList.add(serviceCharges);
			}

			return new JSONUtility(httpRequest).convertToJsonString(serviceChargesList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getMenuByLocationId/{locationId}")
	public String getMenuByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<Category> categoriesList = getRootCategoriesByLocationIdForCM(em, locationId);
			List<MenuSections> finalMenuSectionsList = new ArrayList<MenuSections>();
			if (categoriesList != null && categoriesList.size() > 0)
			{
				List<AllSections> allSectionsList = new ArrayList<AllSections>();
				AllSections allSections = null;
				for (Category category : categoriesList)
				{
					allSections = new AllSections();
					List<MenuSections> menuSectionsList = getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(em, category.getId(), locationId);
					finalMenuSectionsList.addAll(menuSectionsList);
					allSections.setId("" + category.getId());
					allSections.setName(category.getDisplayName());
					allSections.setMenu_sections(menuSectionsList);
					allSectionsList.add(allSections);
				}
				return new JSONUtility(httpRequest).convertToJsonString(allSectionsList);
			}
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;

	}

	@POST
	@Path("/submit")
	public String submit(Submit submit, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			;
			tx = em.getTransaction();
			tx.begin();
		
			SubmitResponse response = submitResponse(httpRequest, auth_token, em, submit);

			tx.commit();

			return new JSONUtility(httpRequest).convertToJsonString(response);
		}
		catch (RuntimeException e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/calculate")
	public String calculate(Calculate calculate, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			;
			tx = em.getTransaction();
			tx.begin();
			CalculateResponse response = calculateOrderHeader(httpRequest, em, calculate);

			tx.commit();

			return new JSONUtility(httpRequest).convertToJsonString(response);
		}
		catch (RuntimeException e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getCalculateResponse")
	public String getCalculateResponse(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			Calculate calculate = new Calculate();
			CheckMateItems checkMateItems = new CheckMateItems();
			CheckMateModifiers checkMateModifiers = new CheckMateModifiers();
			List<CheckMateItems> items = new ArrayList<CheckMateItems>();
			List<CheckMateModifiers> modifiers = new ArrayList<CheckMateModifiers>();
			modifiers.add(checkMateModifiers);
			modifiers.add(checkMateModifiers);
			checkMateItems.setModifiers(modifiers);
			items.add(checkMateItems);
			calculate.setItems(items);
			return new JSONUtility(httpRequest).convertToJsonString(calculate);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getresponse")
	public String getresponse(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			Submit submit = new Submit();
			DiningOptions dining_option = new DiningOptions();
			CustomerInfo customer_info = new CustomerInfo();
			CustomerAddress address = new CustomerAddress();
			customer_info.setCustomerAddress(address);
			CheckMateItems checkMateItems = new CheckMateItems();
			CheckMateModifiers checkMateModifiers = new CheckMateModifiers();
			List<CheckMateItems> items = new ArrayList<CheckMateItems>();
			List<CheckMateModifiers> modifiers = new ArrayList<CheckMateModifiers>();
			modifiers.add(checkMateModifiers);
			modifiers.add(checkMateModifiers);
			checkMateItems.setModifiers(modifiers);
			items.add(checkMateItems);
			Payment payment = new Payment();
			CheckMateDiscounts checkMateDiscounts = new CheckMateDiscounts();
			List<CheckMateDiscounts> discounts = new ArrayList<CheckMateDiscounts>();
			discounts.add(checkMateDiscounts);
			ServiceCharges charges = new ServiceCharges();
			List<ServiceCharges> service_charges = new ArrayList<ServiceCharges>();
			service_charges.add(charges);
			submit.setCustomer_info(customer_info);
			submit.setDining_option(dining_option);
			submit.setDiscounts(discounts);
			submit.setItems(items);
			submit.setService_charges(service_charges);
			submit.setPayment(payment);
			SubmitPacket packet = new SubmitPacket();
			packet.setSubmit(submit);
			return new JSONUtility(httpRequest).convertToJsonString(packet);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	public OrderDetailStatus getOrderDetailStatusByNameAndLocationId(EntityManager em, String statusName, String locationsId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderDetailStatus> criteria = builder.createQuery(OrderDetailStatus.class);
		Root<OrderDetailStatus> r = criteria.from(OrderDetailStatus.class);
		TypedQuery<OrderDetailStatus> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(OrderDetailStatus_.name), statusName), builder.equal(r.get(OrderDetailStatus_.locationsId), locationsId)));
		return query.getSingleResult();
	}

	public PaymentMethodType getPaymentMethodTypeByLocationId(String locationId, EntityManager em) throws Exception
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentMethodType> criteria = builder.createQuery(PaymentMethodType.class);
			Root<PaymentMethodType> paymentMethodType = criteria.from(PaymentMethodType.class);
			TypedQuery<PaymentMethodType> query = em.createQuery(criteria.select(paymentMethodType).where(builder.equal(paymentMethodType.get(PaymentMethodType_.locationsId), locationId),
					builder.equal(paymentMethodType.get(PaymentMethodType_.name), "Credit Term"), builder.notEqual(paymentMethodType.get(PaymentMethodType_.status), "D")));
			PaymentMethodType paymentMethodType2 = query.getSingleResult();

			return paymentMethodType2;
		}
		catch (Exception e)
		{
			logger.equals(e);

		}
		return null;

	}

	public PaymentTransactionType getPaymentTransactionTypeByLocationId(String locationId, EntityManager em) throws Exception
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentTransactionType> criteria = builder.createQuery(PaymentTransactionType.class);
			Root<PaymentTransactionType> paymentTransactionType = criteria.from(PaymentTransactionType.class);
			TypedQuery<PaymentTransactionType> query = em
					.createQuery(criteria.select(paymentTransactionType).where(builder.equal(paymentTransactionType.get(PaymentTransactionType_.locationsId), locationId),
							builder.equal(paymentTransactionType.get(PaymentTransactionType_.name), "Capture"), builder.notEqual(paymentTransactionType.get(PaymentTransactionType_.status), "D")));
			PaymentTransactionType paymentTransactionType2 = query.getSingleResult();

			return paymentTransactionType2;
		}
		catch (Exception e)
		{
			logger.equals(e);

		}
		return null;

	}

	public OrderStatus getOrderStatusByNameAndLocationIdAndSourceId(EntityManager em, String name, String locationId, String orderSourceGroupId) throws Exception
	{

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> r = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.name), name), builder.notEqual(r.get(OrderStatus_.status), "D"),
					builder.equal(r.get(OrderStatus_.locationsId), locationId), builder.equal(r.get(OrderStatus_.orderSourceGroupId), orderSourceGroupId)));
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
	}

	public SubmitResponse submitResponse(HttpServletRequest httpRequest, String referenceNumber, EntityManager em, Submit submit)
			throws NirvanaXPException, JsonGenerationException, JsonMappingException, IOException, ParseException, WriterException, InvalidSessionException
	{
		OrderHeader orderHeader = checkmateToOrderHeader(httpRequest, em, submit, referenceNumber);
		OrderHeader orderHeaderForPush = new OrderServiceForPost().getOrderHeaderWithMinimunRequiredDetails(orderHeader);

		SubmitResponse response = new SubmitResponse();
		response.setOrder_id("" + orderHeader.getId());
		return response;
	}

	OrderHeader checkmateToOrderHeader(HttpServletRequest httpRequest, EntityManager em, Submit submit, String referenceNumber)
			throws NirvanaXPException, IOException, ParseException, WriterException, InvalidSessionException
	{

		try {
			PaymentBatchManager batchManager = PaymentBatchManager.getInstance();
			OrderHeader order_his = new OrderHeader();
			String batchId = batchManager.getCurrentBatchIdBySession(httpRequest, em, submit.getLocation_id(), true, null,"21");
			List<OrderDetailItem> orderDetailItems = new ArrayList<OrderDetailItem>();
			 
			OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, submit.getDining_option().getId());
			OrderSourceGroup sourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
			
			OrderDetailStatus detailStatus = getOrderDetailStatusByNameAndLocationId(em, "KOT Not Printed", submit.getLocation_id());
			BigDecimal subTotal = new BigDecimal(0);
			User user=null;
			try {
			 user=	addUpdateCustomer(em, submit.getCustomer_info(), ""+submit.getLocation_id());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (submit.getItems() != null)
			{
				for (CheckMateItems checkMateItems : submit.getItems())
				{
					subTotal = new BigDecimal(0);
					OrderDetailItem orderItem = new OrderDetailItem();
					 
					Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, checkMateItems.getId());
					List<OrderDetailAttribute> attributesList = new ArrayList<OrderDetailAttribute>();
					orderItem.setCreatedBy("1");
					orderItem.setUpdatedBy("1");
					orderItem.setItemsId(item.getId());
					orderItem.setItemsQty(new BigDecimal(checkMateItems.getQuantity()));
					orderItem.setSentCourseId(item.getCourseId());
					orderItem.setSeatId("s1");
					orderItem.setPointOfServiceNum(1);
					orderItem.setDiscountReason(null);
					orderItem.setDiscountValue(0);
					orderItem.setInventoryAccrual(0);
					orderItem.setParentCategoryId(null);
					orderItem.setRootCategoryId(null);
					orderItem.setOrderHeaderToSeatDetailId(BigInteger.ZERO);
					orderItem.setIsTabOrderItem(0);
					orderItem.setIsInventoryHandled(0);

					orderItem.setItemsShortName(item.getShortName());
					orderItem.setPriceMsrp(item.getPriceMsrp());
					orderItem.setPriceSelling(item.getPriceSelling());
					BigDecimal priceExtended = orderItem.getItemsQty().multiply(orderItem.getPriceSelling());
					orderItem.setPriceExtended(priceExtended);
					orderItem.setPriceMsrp(item.getPriceMsrp());
					orderItem.setPointOfServiceNum(1);
					orderItem.setOrderDetailStatusId(detailStatus.getId());
					orderItem.setOrderDetailStatusName(detailStatus.getDisplayName());
					orderItem.setPlu(item.getPlu());
					subTotal = subTotal.add(priceExtended);
			
					if (checkMateItems.getModifiers() != null)
					{
						for (CheckMateModifiers modifiers : checkMateItems.getModifiers())
						{
					
							OrderDetailAttribute detailAttribute = new OrderDetailAttribute();
							
							ItemsAttribute attribute = (ItemsAttribute) new CommonMethods().getObjectById("ItemsAttribute", em,ItemsAttribute.class, modifiers.getId());
							detailAttribute.setAttributeQty(new BigDecimal(checkMateItems.getQuantity()));
							detailAttribute.setItemQty(checkMateItems.getQuantity());
							detailAttribute.setItemsAttributeId(attribute.getId());
							detailAttribute.setItemsAttributeName(attribute.getName());
							detailAttribute.setItemsId(item.getId());
							detailAttribute.setOrderDetailStatusId(detailStatus.getId());
							detailAttribute.setOrderDetailStatusName(detailStatus.getDisplayName());
							detailAttribute.setPlu(attribute.getPlu());
							detailAttribute.setPriceSelling(attribute.getSellingPrice());
							BigDecimal priceExtendedAttribute = detailAttribute.getAttributeQty().multiply(detailAttribute.getPriceSelling());
							detailAttribute.setPriceExtended(priceExtendedAttribute);
							detailAttribute.setPriceMsrp(attribute.getSellingPrice());
							detailAttribute.setCreatedBy(orderItem.getCreatedBy());
							detailAttribute.setUpdatedBy(orderItem.getUpdatedBy());
							detailAttribute.setSubTotal(priceExtendedAttribute);
				
							attributesList.add(detailAttribute);
							subTotal = subTotal.add(priceExtendedAttribute);
						}
					}
					orderItem.setSubTotal(subTotal);

					if (attributesList.size() > 0)
					{
						orderItem.setOrderDetailAttributes(attributesList);
					}

					orderDetailItems.add(orderItem);
				}
			}

			OrderHeaderCalculation calculation = new OrderHeaderCalculation();
			OrderStatus orderStatus = new OrderStatus();
			try
			{
				orderStatus = getOrderStatusByNameAndLocationIdAndSourceId(em, "Order Paid", submit.getLocation_id(), sourceGroup.getId());
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				logger.severe(e);
			}
			OrderHeader o = new OrderHeader();
			o.setOrderDetailItems(orderDetailItems);
			o.setLocationsId(submit.getLocation_id());
			o.setOrderStatusId(orderStatus.getId());
			o.setOrderSourceId(orderSource.getId());
			o.setOpenTime(new TimezoneTime().getGMTTimeInMilis());
			o.setIpAddress("1.1.1.1");
			if(user!=null){
			o.setUsersId(user.getId());
			o.setUpdatedBy(user.getId());
			o.setCreatedBy(user.getId());
			o.setPreassignedServerId(user.getId());
			o.setServerId(user.getId());
			o.setCashierId(user.getId());
			}
			o.setPointOfServiceCount(1);
			o.setVoidReasonId(null);
			o.setIsOrderReopened(0);
			o.setIsTabOrder(0);
			o.setIsSeatWiseOrder(0);
			
			o.setDeliveryOptionId(null);
			o.setOrderTypeId(1);
			o.setSubTotal(subTotal);
			o.setReservationsId(null);
			o.setSplitCount(0);
			o.setComment(submit.getOrder_notes());
			OrderHeader orderHeader = calculation.getOrderHeaderCalculationForCM(em, submit, o);

			o.setTotal(orderHeader.getTotal());
			o.setBalanceDue(orderHeader.getBalanceDue());
			o.setServiceTax(orderHeader.getServiceTax());
			o.setPriceTax4(orderHeader.getPriceTax4());
			o.setPriceTax1(orderHeader.getPriceTax1());
			o.setPriceTax2(orderHeader.getPriceTax2());
			o.setPriceTax3(orderHeader.getPriceTax3());
			o.setTaxDisplayName1(orderHeader.getTaxDisplayName1());
			o.setTaxDisplayName2(orderHeader.getTaxDisplayName2());
			o.setTaxDisplayName3(orderHeader.getTaxDisplayName3());
			o.setTaxDisplayName4(orderHeader.getTaxDisplayName4());
			o.setTaxName1(orderHeader.getTaxName1());
			o.setTaxName2(orderHeader.getTaxName2());
			o.setTaxName3(orderHeader.getTaxName3());
			o.setTaxName4(orderHeader.getTaxName4());
			o.setTaxRate1(orderHeader.getTaxRate1());
			o.setTaxRate2(orderHeader.getTaxRate2());
			o.setTaxRate3(orderHeader.getTaxRate3());
			o.setTaxRate4(orderHeader.getTaxRate4());
			o.setTotalTax(orderHeader.getTotalTax());
			o.setPriceExtended(orderHeader.getPriceExtended());
			o.setPriceGratuity(orderHeader.getPriceGratuity());
			o.setPriceDiscount(orderHeader.getPriceDiscount());
			o.setGratuity(orderHeader.getGratuity());
			o.setAmountPaid(orderHeader.getAmountPaid());
			o.setSubTotal(orderHeader.getSubTotal());
			o.setLocationsId(orderHeader.getLocationsId());
			o.setAddressShipping(orderHeader.getAddressShipping());
			o.setAddressBilling(orderHeader.getAddressBilling());
			o.setDiscountsId(orderHeader.getDiscountsId());
			o.setDiscountsName(orderHeader.getDiscountsName());
			o.setDiscountsTypeId(orderHeader.getDiscountsTypeId());
			o.setDiscountsTypeName(orderHeader.getDiscountsTypeName());
			o.setDiscountsValue(orderHeader.getDiscountsValue());
			o.setSessionKey(orderHeader.getSessionKey());
			o.setFirstName(orderHeader.getFirstName());
			o.setLastName(orderHeader.getLastName());
			o.setUpdatedBy(orderHeader.getUpdatedBy());
			o.setRoundOffTotal(orderHeader.getRoundOffTotal());
			o.setIsGratuityApplied(orderHeader.getIsGratuityApplied());
			o.setDiscountDisplayName(orderHeader.getDiscountDisplayName());
			o.setTaxExemptId(orderHeader.getTaxExemptId());
			o.setReferenceNumber(referenceNumber);
			o.setNirvanaXpBatchNumber(batchId);
			o.setCalculatedDiscountValue(orderHeader.getCalculatedDiscountValue());
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			o.setOrderHeaderToSalesTax(orderHeader.getOrderHeaderToSalesTax());
			o.setShiftSlotId(orderHeader.getShiftSlotId());
			o.setPriceDiscountItemLevel(orderHeader.getPriceDiscountItemLevel());
			o.setDeliveryCharges(orderHeader.getDeliveryCharges());

			Location l = (Location) new CommonMethods().getObjectById("Location", em,Location.class, o.getLocationsId());
		
			// adding order dequence
			o.setOrderNumber(new StoreForwardUtility().generateUUID());
			o.setCreated(orderHeader.getCreated());
			o.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(orderHeader.getLocationsId(), em));
			TimezoneTime timezoneTime = new TimezoneTime();
			String currentDateTime[] = timezoneTime.getCurrentTimeofLocation(orderHeader.getLocationsId(), em);
			if (currentDateTime != null && currentDateTime.length > 0) {
				o.setDate(currentDateTime[0]);
				if (orderHeader.getScheduleDateTime() == null) {
					o.setScheduleDateTime(currentDateTime[2]);
					o.setScheduleDateTime(timezoneTime.getDateAccordingToGMT(o.getScheduleDateTime(), orderHeader.getLocationsId(), em));
				} else {
					o.setScheduleDateTime(orderHeader.getScheduleDateTime());
					o.setScheduleDateTime(timezoneTime.getDateAccordingToGMT(o.getScheduleDateTime(), orderHeader.getLocationsId(), em));
				}

			}
			
			o = em.merge(o);

			if (submit.getPayment() != null)
			{
				OrderPaymentDetail orderPaymentDetail = new OrderPaymentDetail();
				Payment payment = submit.getPayment();
				
				PaymentMethod paymentMethod = (PaymentMethod) new CommonMethods().getObjectById("PaymentMethod", em,PaymentMethod.class, payment.getPayment_option());
				Discount selectedDiscount = (Discount) new CommonMethods().getObjectById("Discount", em,Discount.class, o.getId());
				PaymentTransactionType paymentTransactionType = null;
				try
				{
					paymentTransactionType = getPaymentTransactionTypeByLocationId(o.getLocationsId(), em);
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				orderPaymentDetail.setAmountPaid(new BigDecimal(payment.getAmount()));
				orderPaymentDetail.setCreditTermTip(new BigDecimal(payment.getTip()));
				orderPaymentDetail.setOrderHeaderId(o.getId());
				if (paymentMethod != null)
				{
					orderPaymentDetail.setPaymentMethod(paymentMethod);
				}
				if (paymentTransactionType != null)
				{
					orderPaymentDetail.setPaymentTransactionType(paymentTransactionType);
				}
				if(orderPaymentDetail.getCreated()==null){
					orderPaymentDetail.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}
				if(orderPaymentDetail.getUpdated()==null){
					orderPaymentDetail.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}
		 
				orderPaymentDetail.setUpdatedBy(o.getUpdatedBy());
				 timezoneTime = new TimezoneTime();
				String currentDate = timezoneTime.getCurrentDate(em, o.getLocationsId());
				String currentTime = timezoneTime.getCurrentTime(em, o.getLocationsId());
				orderPaymentDetail.setDate(currentDate);
				orderPaymentDetail.setTime(currentTime);
				orderPaymentDetail.setCreatedBy(o.getUpdatedBy());
				orderPaymentDetail.setOrderSourceGroupToPaymentGatewayTypeId(0);
				orderPaymentDetail.setOrderSourceToPaymentGatewayTypeId(0);
				orderPaymentDetail.setCashTipAmt(new BigDecimal(0));
				orderPaymentDetail.setCreditcardTipAmt(new BigDecimal(0));
				orderPaymentDetail.setPayementGatewayId(0);
				orderPaymentDetail.setTotalAmount(o.getTotal());
				orderPaymentDetail.setChangeDue(new BigDecimal(0));
				orderPaymentDetail.setBalanceDue(new BigDecimal(0));
				orderPaymentDetail.setNirvanaXpBatchNumber(o.getNirvanaXpBatchNumber());
				orderPaymentDetail.setSettledAmount(new BigDecimal(payment.getAmount()));
				manageOrderPaymentDetailSaleTax(em, o, orderPaymentDetail);
				orderPaymentDetail.setDiscountId(o.getDiscountsId());
				orderPaymentDetail.setDiscountsName(o.getDiscountsName());
				orderPaymentDetail.setDiscountsValue(o.getDiscountsValue());
				orderPaymentDetail.setPriceDiscount(o.getPriceDiscount());
				orderPaymentDetail.setDiscountCode("0000");
				orderPaymentDetail.setLocalTime(o.getLocalTime());
				TransactionStatus transactionStatus = getTransactionStatusByName(em, "Credit");
				orderPaymentDetail.setTransactionStatus(transactionStatus);
				orderPaymentDetail = em.merge(orderPaymentDetail);
				new OrderManagementServiceBean().insertIntoOrderPaymentDetailHistory(em, orderPaymentDetail);

			}

			o = new OrderManagementServiceBean().manageOrderRelationship(em, o);
			// find order
			order_his = new OrderManagementServiceBean().getOrderById(em, o.getId());
			// insert into order status history
			// insertIntoOrderStatusHistory(em, order_his);
			if (orderHeader.getOrderDetailItems() != null)
			{
				for (OrderDetailItem orderDetailItem : orderHeader.getOrderDetailItems())
				{

					orderDetailItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					orderDetailItem.setOrderHeaderId(o.getId());
					// add or update the order detail item
					if (orderDetailItem.getId()!=null )
					{
						orderDetailItem.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(orderHeader.getLocationsId(), em));
						orderDetailItem.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						orderDetailItem.setId(new StoreForwardUtility().generateDynamicBigIntId(em, submit.getLocation_id(), httpRequest,  "order_detail_items"));
						orderDetailItem = em.merge(orderDetailItem);
					}
					else
					{
						em.merge(orderDetailItem);
					}
					// add orderdetails item attribute also if sent by
					// client
					if (orderDetailItem.getOrderDetailAttributes() != null && orderDetailItem.getOrderDetailAttributes().size() > 0)
					{

						for (OrderDetailAttribute orderDetailAttribute : orderDetailItem.getOrderDetailAttributes())
						{
							orderDetailAttribute.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							orderDetailAttribute.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							orderDetailAttribute.setOrderDetailItemId(orderDetailItem.getId());
							if (orderDetailAttribute.getId()!=null )
							{
								// its trying to insert first time
								orderDetailAttribute.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(o.getLocationsId(), em));
								orderDetailAttribute.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
								orderDetailAttribute.setId(new StoreForwardUtility().generateDynamicBigIntId(em, submit.getLocation_id(), httpRequest,  "order_detail_attribute"));
								orderDetailAttribute = em.merge(orderDetailAttribute);
								 
							}
							else
							{
								// its trying to update the already
								// existing
								orderDetailAttribute.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(orderHeader.getLocationsId(), em));
								em.merge(orderDetailAttribute);
							}
						}
					}

				}

				o.setOrderDetailItems(orderHeader.getOrderDetailItems());

			}

			// we need to save the order detail item and order detail item
			// attribute that is sent by the client always
			order_his.setOrderDetailItems(orderHeader.getOrderDetailItems());
			order_his.setOrderPaymentDetails(o.getOrderPaymentDetails());
			order_his = new OrderManagementServiceBean().updateQRCodeAndHistory(httpRequest, em, o, 0, o.getLocationsId(), "")
					.getOrderHeader();
		
			return o;
		}  catch (Exception e) {
		logger.severe(e);
		}
		return null;

	}

	List<Category> getRootCategoriesByLocationIdForCM(EntityManager em, String locationId) throws Exception
	{
		try
		{
			//
			String queryString = "select c from Category c where c.locationsId=? and c.isOnlineCategory=1 and c.status not in('I','D') and (c.categoryId='0' or c.categoryId is null) order by c.sortSequence  asc";
			TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, locationId);
			return query.getResultList();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	List<Category> getsubCategoriesByLocationIdForCM(EntityManager em, String locationId, String categoryId) throws Exception
	{
		try
		{
			//
			String queryString = "select c from Category c where c.locationsId=? and c.isOnlineCategory=1 and c.status not in('I','D') and c.categoryId=?  order by c.sortSequence  asc";
			TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, locationId).setParameter(2, categoryId);
			return query.getResultList();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	private SalesTax getSalesTaxByNameAndLocationId(EntityManager em, String name, String locationId)
	{
		try
		{
			String queryString = "select s from SalesTax s where s.taxName =? and s.locationsId=? and s.status !='D' ";
			TypedQuery<SalesTax> query = em.createQuery(queryString, SalesTax.class).setParameter(1, name).setParameter(2, locationId);
			return query.getSingleResult();
		}
		catch (Exception e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);

		}
		return null;
	}

	private List<SalesTax> getSalesTaxsByLocationId(EntityManager em, String locationId)
	{
		try
		{
			String queryString = "select s from SalesTax s where  s.locationsId=? and s.status !='D' ";
			TypedQuery<SalesTax> query = em.createQuery(queryString, SalesTax.class).setParameter(1, locationId);
			return query.getResultList();
		}
		catch (Exception e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);

		}
		return null;
	}

	List<MenuSections> getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(EntityManager em, String categoryId, String locationId) throws Exception
	{

		try
		{
			@SuppressWarnings("unchecked")

			List<Object[]> resultList = null;
			String queryString = "";
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
			if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Ascending"))
			{
				queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'Ascending')";
			}
			else if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Descending"))
			{
				queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'Descending')";
			}
			else
			{
				queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'DisplaySequence')";
			}

			resultList = em.createNativeQuery(queryString).setParameter(1, categoryId).setParameter(2, locationId).getResultList();
			List<MenuSections> menuSectionsList = new ArrayList<MenuSections>();
			List<CheckMateItems> checkMateItemsList = new ArrayList<CheckMateItems>();
			List<Integer> categoryIdList = new ArrayList<Integer>();
			MenuSections menuSections = null;
			CheckMateItems checkMateItems = null;
			String itemId = null;
			String menuSectionId = null;
			for (Object[] objRow : resultList)
			{
				menuSections = new MenuSections();
				checkMateItems = new CheckMateItems();

				// if this has primary key not 0
				if ((String) objRow[0] != null)
				{
					int i = 0;
					itemId = null;
					menuSectionId = null;
					if (objRow[i] != null)
					{
						menuSectionId = (String) objRow[i];
						menuSections.setId("" + (String) objRow[i]);
					}
					i++;
			
					if (objRow[i] != null)
					{
						menuSections.setName((String) objRow[i]);
					}
					i++;
					if (objRow[i] != null)
					{
						itemId = (String) objRow[i];
						checkMateItems.setId("" + (int) objRow[i]);
					}
					i++;
					i++;
					i++;
					if (objRow[i] != null)
					{
						checkMateItems.setName((String) objRow[i]);
					}
					i++;
					i++;

					if (objRow[i] != null)
					{
						checkMateItems.setPrice(((BigDecimal) objRow[i]).doubleValue());
					}

					if (itemId != null)
					{
						Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemId);
						List<ModifierGroups> modifierGroups = getModifierGroupsByItemId(item.getId(), em);
						checkMateItems.setId("" + item.getId());
						checkMateItems.setName(item.getShortName());
						checkMateItems.setModifier_groups(modifierGroups);
						checkMateItemsList.add(checkMateItems);
						menuSections.setItems(checkMateItemsList);

					}
					else
					{
						menuSections = null;
						if (categoryId.equals(menuSectionId))
						{
							menuSectionsList = getSubCategoryAndItemByCategoryIdAndLocationId(em, menuSectionId, locationId, menuSectionsList);
							menuSectionsList.add(menuSections);
						}
					}

				}
			}

			if (menuSections != null)
			{
				menuSectionsList.add(menuSections);
			}
			
			return menuSectionsList;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	List<MenuSections> getSubCategoryAndItemByCategoryIdAndLocationId(EntityManager em, String categoryId, String locationId, List<MenuSections> menuSectionsList) throws Exception
	{

		try
		{
			List<Object[]> resultList = null;
			String queryString = "";
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
			if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Ascending"))
			{
				queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'Ascending')";
			}
			else if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Descending"))
			{
				queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'Descending')";
			}
			else
			{
				queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'DisplaySequence')";
			}

			resultList = em.createNativeQuery(queryString).setParameter(1, categoryId).setParameter(2, locationId).getResultList();
			List<CheckMateItems> checkMateItemsList = new ArrayList<CheckMateItems>();
			MenuSections menuSections = null;
			CheckMateItems checkMateItems = null;
			String itemId = null;
			for (Object[] objRow : resultList)
			{
				menuSections = new MenuSections();
				checkMateItems = new CheckMateItems();

				// if this has primary key not 0
				if ((String) objRow[0] != null)
				{
					int i = 0;
					itemId = null;
					if (objRow[i] != null)
					{
						menuSections.setId("" + (String) objRow[i]);
					}
					i++;
		
					if (objRow[i] != null)
					{
						menuSections.setName((String) objRow[i]);
					}
					i++;
					if (objRow[i] != null)
					{
						itemId = (String) objRow[i];
						checkMateItems.setId("" + (int) objRow[i]);
					}
					i++;
					i++;
					i++;
					if (objRow[i] != null)
					{
						checkMateItems.setName((String) objRow[i]);
					}
					i++;
					i++;
					if (objRow[i] != null)
					{
						checkMateItems.setPrice(((BigDecimal) objRow[i]).doubleValue());
					}

					if (itemId != null)
					{
						Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemId);
						List<ModifierGroups> modifierGroups = getModifierGroupsByItemId(item.getId(), em);
						checkMateItems.setId("" + item.getId());
						checkMateItems.setName(item.getShortName());
						checkMateItems.setModifier_groups(modifierGroups);
						checkMateItemsList.add(checkMateItems);
						menuSections.setItems(checkMateItemsList);
	
					}
					else
					{
						// menuSections=null;
					}
				}

			}
			if (menuSections != null)
			{
				menuSectionsList.add(menuSections);
			}
			return menuSectionsList;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	public List<ModifierGroups> getModifierGroupsByItemId(String itemId, EntityManager em)
	{
		if (itemId != null && em != null)
		{

			try
			{
				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<ItemsToItemsAttributeType> criteria = builder.createQuery(ItemsToItemsAttributeType.class);
				Root<ItemsToItemsAttributeType> r = criteria.from(ItemsToItemsAttributeType.class);

				TypedQuery<ItemsToItemsAttributeType> query = em
						.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToItemsAttributeType_.itemsId), itemId), builder.notEqual(r.get(ItemsToItemsAttributeType_.status), "D")));
				List<ItemsToItemsAttributeType> itemsToItemsAttributeTypes = query.getResultList();
				List<ModifierGroups> groups = new ArrayList<ModifierGroups>();
				ModifierGroups modifierGroups = null;
				for (ItemsToItemsAttributeType itemsToItemsAttributeType : itemsToItemsAttributeTypes)
				{
					modifierGroups = new ModifierGroups();
					ItemsAttributeType attributeType = (ItemsAttributeType) new CommonMethods().getObjectById("ItemsAttributeType", em,ItemsAttributeType.class,  itemsToItemsAttributeType.getItemsAttributeTypeId());
					String queryString = "select ia.id,ia.short_name,ia.selling_price,ia.multi_select from items_attribute_type_to_items_attribute iattia "
							+ "left join items_attribute_type iat on iat.id = iattia.items_attribute_type_id "
							+ "left join items_to_items_attribute itia on itia.items_attribute_id = iattia.items_attribute_id " + "left join items i on i.id = itia.items_id "
							+ "left join items_attribute ia on ia.id = iattia.items_attribute_id " + "where  i.id =? And iat.id=? ";
					List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, itemId).setParameter(2, attributeType.getId()).getResultList();
					CheckMateModifiers checkMateModifiers = null;

					List<CheckMateModifiers> checkMateModifiersList = new ArrayList<CheckMateModifiers>();
					for (Object[] objRow : resultList)
					{
						// if this has primary key not 0
						if ((String) objRow[0] != null)
						{
							checkMateModifiers = new CheckMateModifiers();
							int i = 0;

							if (objRow[i] != null)
							{
								checkMateModifiers.setId("" + (String) objRow[i]);
							}
							i++;
							if (objRow[i] != null)
							{
								checkMateModifiers.setName((String) objRow[i]);
							}
							i++;
							if (objRow[i] != null)
							{
								checkMateModifiers.setPrice(((BigDecimal) objRow[i]).doubleValue());
							}
							i++;
							if (objRow[i] != null)
							{
							boolean multiSelect=false;
							
								if(((byte) objRow[i])==1){
									multiSelect=true;
								}
								checkMateModifiers.setMultiSelect(multiSelect);;
							}
							checkMateModifiersList.add(checkMateModifiers);
						}

					}
					modifierGroups.setId("" + attributeType.getId());
					modifierGroups.setName(attributeType.getDisplayName());
					modifierGroups.setIs_required(attributeType.getIsRequired());
					if(attributeType.getIsRequired()>0){
					modifierGroups.setMinimum_amount(attributeType.getMaxAttributeAllowed());
					}
					modifierGroups.setModifiers(checkMateModifiersList);
					groups.add(modifierGroups);

				}
				return groups;
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				logger.severe(e);
			}

		}
		return null;

	}

	/*
	 * public String addCustomer(CustomerInfo customerInfo,String locationId)
	 * throws Exception {
	 * 
	 * 
	 * if (customerInfo == null) { return new
	 * NirvanaServiceErrorResponse(MessageConstants.
	 * ERROR_CODE_USER_CANNOT_BE_NULL_EXCEPTION,
	 * MessageConstants.ERROR_MESSAGE_USER_CANNOT_BE_NULL_DISPLAY_MESSAGE,
	 * null).toString(); }
	 * 
	 * EntityManager localEM = null; EntityManager globalEM = null;
	 * 
	 * try {
	 * 
	 * GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
	 * 
	 * User localuser = new User(); String name=customerInfo.getEmail();
	 * String[] nameStrings = name.split(" ");
	 * localuser.setFirstName(nameStrings[0]);
	 * localuser.setLastName(nameStrings[1]);
	 * localuser.setPhone(customerInfo.getPhone());
	 * localuser.setEmail(customerInfo.getEmail());
	 * 
	 * Address address=new Address(); String
	 * addressString=customerInfo.getAddress(); String[] addressStrings =
	 * addressString.split(" "); address.setAddress1(addressStrings[0]);;
	 * address.setCity(addressStrings[1]); address.setState(addressStrings[2]);
	 * 
	 * globalEM =
	 * GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest,
	 * sessionId); localEM =
	 * LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest,
	 * sessionId);
	 * 
	 * UserManagementObj userManagementObj =
	 * globalUsermanagement.addCustomer(httpRequest, globalEM, localEM,
	 * localuser, locationId, localAddressSet);
	 * 
	 * // check if user already exists in our database, then return // client
	 * the reason for not adding the user if (userManagementObj != null &&
	 * userManagementObj.getResponse() != null &&
	 * userManagementObj.getResponse().equals("Already added user")) {
	 * 
	 * String errorMessage = ""; String phoneOfUserSentByClient =
	 * customerInfo.getPhone(); String emailOfUserSentByClient =
	 * customerInfo.getEmail(); String usernameOfUserSentByClient =
	 * customerInfo.getUsername();
	 * 
	 * if (phoneOfUserSentByClient != null &&
	 * phoneOfUserSentByClient.trim().length() == 0) { phoneOfUserSentByClient =
	 * null; userPostPacket.getUser().setPhone(null); }
	 * 
	 * if (emailOfUserSentByClient != null &&
	 * emailOfUserSentByClient.trim().length() == 0) { emailOfUserSentByClient =
	 * null; userPostPacket.getUser().setEmail(null); }
	 * 
	 * String phoneOfAlreadyExistingUser =
	 * userManagementObj.getUser().getPhone(); String emailOfAlreadyExistingUser
	 * = userManagementObj.getUser().getEmail();
	 * 
	 * String usernameOfAlreadyExistingUser =
	 * userManagementObj.getUser().getUsername();
	 * 
	 * if (phoneOfUserSentByClient != null &&
	 * phoneOfUserSentByClient.trim().length() > 0 && phoneOfAlreadyExistingUser
	 * != null && phoneOfAlreadyExistingUser.trim().length() > 0) {
	 * 
	 * if
	 * (phoneOfUserSentByClient.trim().equals(phoneOfAlreadyExistingUser.trim())
	 * ) { errorMessage = "Phone number already Exists in our database."; } }
	 * 
	 * if (emailOfUserSentByClient != null &&
	 * emailOfUserSentByClient.trim().length() > 0 && emailOfAlreadyExistingUser
	 * != null && emailOfAlreadyExistingUser.trim().length() > 0) {
	 * 
	 * if
	 * (emailOfUserSentByClient.trim().equals(emailOfAlreadyExistingUser.trim())
	 * ) { errorMessage += "Email already Exists in our database."; }
	 * 
	 * }
	 * 
	 * if (usernameOfUserSentByClient != null &&
	 * usernameOfUserSentByClient.trim().length() > 0 &&
	 * usernameOfAlreadyExistingUser != null &&
	 * usernameOfAlreadyExistingUser.trim().length() > 0) {
	 * 
	 * if
	 * (usernameOfUserSentByClient.trim().equals(usernameOfAlreadyExistingUser.
	 * trim())) { errorMessage += "Username already Exists in our database."; }
	 * 
	 * }
	 * 
	 * return errorMessage; }
	 * 
	 * // user was added and appropriate details were returned localuser =
	 * userManagementObj.getUser();
	 * 
	 * userPostPacket.setUser(localuser);
	 * userPostPacket.setAddressList(localAddressSet);
	 * 
	 * sendPacketForBroadcast(createPacketForPush(userPostPacket),
	 * POSNServiceOperations.UserManagementService_addCustomer.name(), false);
	 * 
	 * return new JSONUtility(httpRequest).convertToJsonString(userPostPacket);
	 * 
	 * } finally {
	 * LocalSchemaEntityManager.getInstance().closeEntityManager(localEM);
	 * GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM); } }
	 */

	CalculateResponse calculateOrderHeader(HttpServletRequest httpRequest, EntityManager em, Calculate calculate)
			throws NirvanaXPException, IOException, ParseException, WriterException, InvalidSessionException
	{

		List<OrderDetailItem> orderDetailItems = new ArrayList<OrderDetailItem>();
		 
		String locationId = calculate.getLocation_id();
		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, calculate.getDining_option());
		OrderSourceGroup sourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
		OrderDetailStatus detailStatus = getOrderDetailStatusByNameAndLocationId(em, "KOT Not Printed", locationId);
		
		BigDecimal subTotal = new BigDecimal(0);
		if (calculate.getItems() != null)
		{
			for (CheckMateItems checkMateItems : calculate.getItems())
			{
				subTotal = new BigDecimal(0);
				OrderDetailItem orderItem = new OrderDetailItem();
				String itemId = checkMateItems.getId();
				Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemId);
				List<OrderDetailAttribute> attributesList = new ArrayList<OrderDetailAttribute>();
				orderItem.setItemsQty(new BigDecimal(checkMateItems.getQuantity()));
				orderItem.setPriceMsrp(item.getPriceMsrp());
				orderItem.setPriceSelling(item.getPriceSelling());
				BigDecimal priceExtended = orderItem.getItemsQty().multiply(orderItem.getPriceSelling());
				orderItem.setPriceExtended(priceExtended);
				orderItem.setPriceMsrp(item.getPriceMsrp());
				orderItem.setPointOfServiceNum(1);
				orderItem.setOrderDetailStatusId(detailStatus.getId());
				orderItem.setDiscountCode("0000");
				orderItem.setItemsId(itemId);
				
				subTotal = subTotal.add(priceExtended);
		
				if (checkMateItems.getModifiers() != null)
				{
					for (CheckMateModifiers modifiers : checkMateItems.getModifiers())
					{
					
						OrderDetailAttribute detailAttribute = new OrderDetailAttribute();
						
						ItemsAttribute attribute = (ItemsAttribute) new CommonMethods().getObjectById("ItemsAttribute", em,ItemsAttribute.class, modifiers.getId());
						detailAttribute.setAttributeQty(new BigDecimal(checkMateItems.getQuantity()));
						detailAttribute.setItemQty(checkMateItems.getQuantity());
						detailAttribute.setPriceSelling(attribute.getSellingPrice());
						BigDecimal priceExtendedAttribute = detailAttribute.getAttributeQty().multiply(detailAttribute.getPriceSelling());
						detailAttribute.setPriceExtended(priceExtendedAttribute);
						detailAttribute.setPriceMsrp(attribute.getSellingPrice());
						detailAttribute.setSubTotal(priceExtendedAttribute);
						attributesList.add(detailAttribute);
						subTotal = subTotal.add(priceExtendedAttribute);
					}
				}
				orderItem.setSubTotal(subTotal);

				if (attributesList.size() > 0)
				{
					orderItem.setOrderDetailAttributes(attributesList);
				}

				orderDetailItems.add(orderItem);
			}
		}

		OrderHeaderCalculation calculation = new OrderHeaderCalculation();
		OrderHeader o = new OrderHeader();
		o.setOrderDetailItems(orderDetailItems);
		o.setLocationsId(locationId);
		o.setOrderSourceId(orderSource.getId());
		o.setSubTotal(subTotal);

		OrderHeader orderHeader = calculation.getCalculationForCM(em, calculate, o);
		CalculateResponse calculateResponse = new CalculateResponse();
		calculateResponse.setSubtotal(orderHeader.getSubTotal().doubleValue());
		calculateResponse.setTaxes(orderHeader.getTotalTax().doubleValue());
		calculateResponse.setTotal(orderHeader.getTotal().doubleValue());
		return calculateResponse;
	}

	List<RootMenuSection> getRootCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(EntityManager em, int categoryId, String locationId) throws Exception
	{

		try
		{
			@SuppressWarnings("unchecked")

			List<Object[]> resultList = null;
			String queryString = "";
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
			if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Ascending"))
			{
				queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'Ascending')";
			}
			else if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Descending"))
			{
				queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'Descending')";
			}
			else
			{
				queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'DisplaySequence')";
			}

			resultList = em.createNativeQuery(queryString).setParameter(1, categoryId).setParameter(2, locationId).getResultList();
			List<RootMenuSection> rootMenuSectionsList = new ArrayList<RootMenuSection>();
			List<Integer> categoryIdList = new ArrayList<Integer>();
			RootMenuSection rootMenuSections = null;
			List<MenuSections> subMenuSectionsList = null;
			String itemId = null;
			String menuSectionId = null;
			for (Object[] objRow : resultList)
			{
				rootMenuSections = new RootMenuSection();

				// if this has primary key not 0
				if ((String) objRow[0] != null)
				{
					int i = 0;
					itemId = null;
					menuSectionId = null;
					if (objRow[i] != null)
					{
						menuSectionId = (String) objRow[i];
						rootMenuSections.setId("" + (String) objRow[i]);
					}
					i++;
				
					if (objRow[i] != null)
					{
						rootMenuSections.setName((String) objRow[i]);
					}

					rootMenuSectionsList.add(rootMenuSections);

				}
			}

			return rootMenuSectionsList;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	@Override
	public boolean isAlive()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		// TODO Auto-generated method stub
		return logger;
	}

	List<MenuSections> getMenu(EntityManager em, int categoryId, String locationId) throws Exception
	{

		try
		{
			List<Object[]> resultList = null;
			String queryString = "";
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
			queryString = "call p_categoryList(?,?, 'Ascending')";

			resultList = em.createNativeQuery(queryString).setParameter(1, categoryId).setParameter(2, locationId).getResultList();

			List<MenuSections> menuSectionsList = new ArrayList<MenuSections>();
			List<MenuSections> submenuSectionsList = new ArrayList<MenuSections>();
			List<CheckMateItems> checkMateItemsList = new ArrayList<CheckMateItems>();
			List<Integer> categoryIdList = new ArrayList<Integer>();
			MenuSections menuSections = null;
			CheckMateItems checkMateItems = null;
			String itemId = null;
			String menuSectionId = null;
			for (Object[] objRow : resultList)
			{
				menuSections = new MenuSections();
				checkMateItems = new CheckMateItems();

				// if this has primary key not 0
				if ((String) objRow[0] != null)
				{
					int i = 0;
					itemId = null;
					menuSectionId = null;
					if (objRow[i] != null)
					{
						menuSectionId = (String) objRow[i];
						menuSections.setId("" + (String) objRow[i]);
					}
					i++;
					if (objRow[i] != null)
					{
						menuSections.setName((String) objRow[i]);
					}
					i++;
					if (objRow[i] != null)
					{
						itemId = (String) objRow[i];
						checkMateItems.setId("" + (int) objRow[i]);
					}
					i++;
					i++;
					i++;
					if (objRow[i] != null)
					{
						checkMateItems.setName((String) objRow[i]);
					}
					i++;
					i++;

					if (objRow[i] != null)
					{
						checkMateItems.setPrice(((BigDecimal) objRow[i]).doubleValue());
					}

					if (itemId != null)
					{
						Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemId);
						List<ModifierGroups> modifierGroups = getModifierGroupsByItemId(item.getId(), em);
						checkMateItems.setId("" + item.getId());
						checkMateItems.setName(item.getShortName());
						checkMateItems.setModifier_groups(modifierGroups);
						checkMateItemsList.add(checkMateItems);
						menuSections.setItems(checkMateItemsList);

					}
					else
					{
						checkMateItems.setId("");
						checkMateItems.setName("");
						checkMateItems.setModifier_groups(null);
						checkMateItemsList.add(checkMateItems);
						menuSections.setItems(checkMateItemsList);
					}

				}
				if (menuSections != null)
				{
					menuSectionsList.add(menuSections);
					;
				}

			}

			return menuSectionsList;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
	}

	@GET
	@Path("/getMenuByLocationIdForCM/{locationId}")
	public String getMenuByLocationIdForCM(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<Category> categoriesList = getRootCategoriesByLocationIdForCM(em, locationId);
			List<AllSections> allSectionsList = new ArrayList<AllSections>();
			if (categoriesList != null && categoriesList.size() > 0)
			{
				List<MenuSections> subMenuSectionsList = null;
				List<MenuSections> subsubMenuSectionsList = null;
				MenuSections menuSections = null;
				MenuSections submenuSections = null;
				AllSections allSections = null;
				for (Category category : categoriesList)
				{
					List<Category> subCategoriesList = new ArrayList<Category>();
					subMenuSectionsList = new ArrayList<MenuSections>();
					allSections = new AllSections();
					allSections.setId("" + category.getId());
					allSections.setName(category.getDisplayName());
					subCategoriesList = getsubCategoriesByLocationIdForCM(em, locationId, category.getId());
					if (subCategoriesList.size() == 0)
					{
						subCategoriesList.add(category);
					}
					for (Category sub : subCategoriesList)
					{
						subsubMenuSectionsList = new ArrayList<MenuSections>();
						List<Category> subSubCategoriesList = new ArrayList<Category>();
						menuSections = new MenuSections();
						menuSections.setName(sub.getDisplayName());
						menuSections.setId("" + sub.getId());
						subSubCategoriesList = getsubCategoriesByLocationIdForCM(em, locationId, sub.getId());
						if (subSubCategoriesList.size() == 0)
						{
							subSubCategoriesList.add(sub);
						}
						for (Category subsub : subSubCategoriesList)
						{
							submenuSections = new MenuSections();
							submenuSections.setName(sub.getDisplayName());
							submenuSections.setId("" + sub.getId());
							List<MenuSections> finalmenuSectionsList = getfinal(em, subsub.getId(), locationId);
							submenuSections.setMenu_sections(finalmenuSectionsList);
							subsubMenuSectionsList.add(submenuSections);
						}
						menuSections.setMenu_sections(subsubMenuSectionsList);
						subMenuSectionsList.add(menuSections);
					}
					allSections.setMenu_sections(subMenuSectionsList);
					allSectionsList.add(allSections);
				}

			}
			return new JSONUtility(httpRequest).convertToJsonString(allSectionsList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	List<MenuSections> getsub(EntityManager em, String categoryId, String locationId) throws Exception
	{

		try
		{
			@SuppressWarnings("unchecked")

			// int categoryId=Integer.parseInt(categoryIds);
			List<Object[]> resultList = null;
			String queryString = "";
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
			if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Ascending"))
			{
				queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'Ascending')";
			}
			else if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Descending"))
			{
				queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'Descending')";
			}
			else
			{
				queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'DisplaySequence')";
			}
			// queryString = "call p_categoryList(?,?)";

			resultList = em.createNativeQuery(queryString).setParameter(1, categoryId).setParameter(2, locationId).getResultList();
			List<MenuSections> menuSectionsList = new ArrayList<MenuSections>();
			MenuSections menuSections = null;
			// int itemId = 0;
			String menuSectionId = null;
			for (Object[] objRow : resultList)
			{
				menuSections = new MenuSections();

				// if this has primary key not 0
				if ((String) objRow[0] != null)
				{
					int i = 0;
					// itemId = 0;
					menuSectionId = null;
					if (objRow[i] != null)
					{
						menuSectionId = (String) objRow[i];
						menuSections.setId("" + (String) objRow[i]);
					}
					i++;

					if (objRow[i] != null)
					{
						menuSections.setName((String) objRow[i]);
					}
					i++;
					if (objRow[i] != null)
					{
						/*
						 * itemId=(int) objRow[i]; checkMateItems.setId(""+(int)
						 * objRow[i]);
						 */}
					i++;
					i++;
					i++;
					if (objRow[i] != null)
					{
						/*
						 * checkMateItems.setName((String) objRow[i]);
						 */}
					i++;
					i++;

					if (objRow[i] != null)
					{
						/*
						 * checkMateItems.setPrice(((BigDecimal)
						 * objRow[i]).doubleValue());
						 */}

					if (!categoryId.equals(menuSectionId))
					{
						//
						menuSectionsList.add(menuSections);
					}
				}

			}

			return menuSectionsList;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	List<MenuSections> getfinal(EntityManager em, String categoryId, String locationId) throws Exception
	{

		try
		{
			@SuppressWarnings("unchecked")

			// int categoryId=Integer.parseInt(categoryIds);
			List<Object[]> resultList = null;
			String queryString = "";
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
			if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Ascending"))
			{
				queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'Ascending')";
			}
			else if (locationSetting != null && locationSetting.getItemSortingFormat() != null && locationSetting.getItemSortingFormat().equalsIgnoreCase("Descending"))
			{
				queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'Descending')";
			}
			else
			{
				queryString = "call p_getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(?,?, 'DisplaySequence')";
			}
			// queryString = "call p_categoryList(?,?)";

			resultList = em.createNativeQuery(queryString).setParameter(1, categoryId).setParameter(2, locationId).getResultList();
			List<MenuSections> menuSectionsList = new ArrayList<MenuSections>();
			List<MenuSections> subMenuSectionsList = new ArrayList<MenuSections>();
			List<CheckMateItems> checkMateItemsList = new ArrayList<CheckMateItems>();
			MenuSections menuSections = null;
			CheckMateItems checkMateItems = null;
			String itemId = null;
			String menuSectionId = null;
			for (Object[] objRow : resultList)
			{
				menuSections = new MenuSections();
				checkMateItems = new CheckMateItems();

				// if this has primary key not 0
				if ((String) objRow[0] != null)
				{
					int i = 0;
					itemId = null;
					menuSectionId = null;
					if (objRow[i] != null)
					{
						menuSectionId = (String) objRow[i];
						menuSections.setId("" + (String) objRow[i]);
					}
					i++;

					if (objRow[i] != null)
					{
						menuSections.setName((String) objRow[i]);
					}
					i++;
					if (objRow[i] != null)
					{
						itemId = (String) objRow[i];
						checkMateItems.setId("" + (int) objRow[i]);
					}
					i++;
					i++;
					i++;
					if (objRow[i] != null)
					{
						checkMateItems.setName((String) objRow[i]);
					}
					i++;
					i++;

					if (objRow[i] != null)
					{
						checkMateItems.setPrice(((BigDecimal) objRow[i]).doubleValue());
					}
					if (itemId != null)
					{
					
						Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemId);
						List<ModifierGroups> modifierGroups = getModifierGroupsByItemId(item.getId(), em);
						checkMateItems.setId("" + item.getId());
						checkMateItems.setName(item.getShortName());
						checkMateItems.setModifier_groups(modifierGroups);
						checkMateItemsList.add(checkMateItems);
						menuSections.setItems(checkMateItemsList);

					}
					menuSectionsList.add(menuSections);
					;
				}

			}

			//

			return menuSectionsList;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	List<Object> getItemByCategoryId(EntityManager em, String categoryId) throws Exception
	{
		try
		{
			//
			String queryString = "SELECT i.id FROM items i join category_items ci on ci.items_id=i.id  join items_type it on i.item_type=it.id where it.name='Sale Only' and ci.category_id=? and i.status !='D'  ";
			List<Object> resultList = em.createNativeQuery(queryString).setParameter(1, categoryId).getResultList();
			return resultList;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	List<Category> getSubCategoriesByCategoryId(EntityManager em, String locationId, String categoryId) throws Exception
	{
		try
		{
			String queryString = "select c from Category c where c.locationsId=? and c.isOnlineCategory=1 and c.status not in('I','D') and c.categoryId=?  order by c.sortSequence  asc";
			TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, locationId).setParameter(2, categoryId);
			return query.getResultList();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	@GET
	@Path("/getMenusByLocationIdForCM/{locationId}")
	public String getMenusByLocationIdForCM(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// get root category
			List<Category> categoriesList = getRootCategoriesByLocationIdForCM(em, locationId);
			List<MenuSections> rootSection = new ArrayList<MenuSections>();
			if (categoriesList != null && categoriesList.size() > 0)
			{ 
				
				for(Category rootCategory:categoriesList){
					MenuSections rootMenuSection  = new MenuSections();
					//get all items of root categies
					List<Object> rootCategoryItem = getItemByCategoryId(em, rootCategory.getId());
					List<CheckMateItems> rootCheckMateItems = createCheckMateItems(em, rootCategoryItem);
					rootMenuSection.setId(rootCategory.getId()+"");
					rootMenuSection.setName(rootCategory.getName());
					rootMenuSection.setItems(rootCheckMateItems);
				
					// get subcategories
					List<Category> categories = getSubCategoriesByCategoryId(em, locationId, rootCategory.getId());
					List<MenuSections> section = new ArrayList<MenuSections>();
					for(Category category:categories){
						MenuSections menuSection  = new MenuSections();
						//get all items of root categies
						List<Object> categoryItem = getItemByCategoryId(em, category.getId());
						List<CheckMateItems> checkMateItems = createCheckMateItems(em, categoryItem);
						menuSection.setId(category.getId()+"");
						menuSection.setName(category.getDisplayName());
						menuSection.setItems(checkMateItems);
						
						// get subsubcategories
						List<Category> subCategories = getSubCategoriesByCategoryId(em, locationId, category.getId());
						List<MenuSections> subSection = new ArrayList<MenuSections>();
						for(Category subCategory:subCategories){
							MenuSections subMenuSection  = new MenuSections();
							//get all items of root categies
							List<Object> subCategoryItem = getItemByCategoryId(em, subCategory.getId());
							List<CheckMateItems> subCheckMateItems = createCheckMateItems(em, subCategoryItem);
							subMenuSection.setId(subCategory.getId()+"");
							subMenuSection.setName(subCategory.getDisplayName());
							subMenuSection.setItems(subCheckMateItems);
							subSection.add(subMenuSection);
							
						}
						menuSection.setMenu_sections(subSection);
						section.add(menuSection);
					}
					rootMenuSection.setMenu_sections(section);
					rootSection.add(rootMenuSection);
					
				}
				
				
			}
			MenuPacket menuPacket=new  MenuPacket();
			menuPacket.setAllSections(rootSection);
			return new JSONUtility(httpRequest).convertToJsonString(menuPacket);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	private List<CheckMateItems> createCheckMateItems(EntityManager em, List<Object> itemIds){
		List<CheckMateItems> checkMateItems = new ArrayList<>();
		for(Object obj:itemIds){
			CheckMateItems checkMateItem = new CheckMateItems();
			Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class,(String) obj);
			List<ModifierGroups> modifierGroups = getModifierGroupsByItemId(item.getId(), em);
			checkMateItem.setId("" + item.getId());
			checkMateItem.setName(item.getShortName());
			checkMateItem.setModifier_groups(modifierGroups);
			checkMateItem.setPrice(item.getPriceSelling().doubleValue());
			checkMateItems.add(checkMateItem);
		}
		return checkMateItems;
	}
	public User addUpdateCustomer(EntityManager localEM,CustomerInfo customerInfo,String locationIdString) throws Exception
	{
		EntityManager globalEM = null;

		try
		{

			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();

			String locationId = locationIdString;
			User localUser = new  User();
			localUser.setFirstName(customerInfo.getFirstName());
			localUser.setLastName(customerInfo.getLastName());
			localUser.setPhone(customerInfo.getPhone());
			localUser.setEmail(customerInfo.getEmail());
			localUser.setStatus("A");
			localUser.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			localUser.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			localUser.setCreatedBy("1");
			localUser.setUpdatedBy("1");
			CustomerAddress customerAddress = customerInfo.getCustomerAddress();
			Address localAddress=new Address();
			localAddress.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			localAddress.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			localAddress.setAddress1(customerAddress.getAddressLine1());
			localAddress.setAddress2(customerAddress.getAddressLine2());
			localAddress.setPhone(customerInfo.getPhone());
			localAddress.setCity(customerAddress.getCity());
			localAddress.setState(customerAddress.getState());
			localAddress.setZip(customerAddress.getZip());
			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
			Countries countries=getCountriesByName(globalEM, customerAddress.getCountry());
			State state=getStateByName(globalEM, customerAddress.getState());
			City city=getCityByName(globalEM, customerAddress.getCity());
			if(countries!=null){
				localAddress.setCountryId(countries.getId());	
			}if(state!=null){
				localAddress.setStateId(state.getId());
			}if(city!=null){
				localAddress.setCityId(city.getId());
			}
			
			// add update of customer in global and local user
			Set<Address> globalAddressList = new HashSet<Address>();
			globalAddressList.add(localAddress);
			UserManagementObj userManagementObj = globalUsermanagement.addUpdateCustomer(httpRequest, globalEM, localEM, localUser, locationId, globalAddressList,null);

	 		return localUser=userManagementObj.getUser();
		}catch(Exception e){
			logger.severe(e);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}
		return null;
	}
	private City getCityByName(EntityManager em, String name)
	{
		try
		{
			String queryString = "select s from City s where  s.cityName=? and s.status !='D' ";
			TypedQuery<City> query = em.createQuery(queryString, City.class).setParameter(1, name);
			return query.getSingleResult();
		}
		catch (Exception e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);

		}
		return null;
	}
	private State getStateByName(EntityManager em, String name)
	{
		try
		{
			String queryString = "select s from State s where  s.stateName=? and s.status !='D' ";
			TypedQuery<State> query = em.createQuery(queryString, State.class).setParameter(1, name);
			return query.getSingleResult();
		}
		catch (Exception e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);

		}
		return null;
	}
	private Countries getCountriesByName(EntityManager em, String name)
	{
		try
		{
			String queryString = "select s from Countries s where  s.name=? ";
			TypedQuery<Countries> query = em.createQuery(queryString, Countries.class).setParameter(1, name);
			return query.getSingleResult();
		}
		catch (Exception e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);

		}
		return null;
	}
	public List<OrderSource> getOrderSourceBySourceGroupIdAndLocationId(EntityManager em, String sourceGroupId, String locationId) throws Exception {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
			Root<OrderSource> r = criteria.from(OrderSource.class);
			TypedQuery<OrderSource> query = em.createQuery(criteria.select(r).where(
					builder.equal(r.get(OrderSource_.orderSourceGroupId), sourceGroupId), builder.notEqual(r.get(OrderSource_.status), "D"),
					builder.equal(r.get(OrderSource_.locationsId), locationId),
					builder.equal(r.get(OrderSource_.orderSourceGroupId), sourceGroupId)));
			return query.getResultList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
	}
	private OrderPaymentDetail manageOrderPaymentDetailSaleTax(EntityManager em, OrderHeader o,
			OrderPaymentDetail orderPaymentDetail) {

			try {
				// compare bg1 with bg2

						if (o.getPriceTax1() != null ) {

							orderPaymentDetail.setTaxName1(o.getTaxName1());
							orderPaymentDetail.setTaxDisplayName1(o.getTaxDisplayName1());
							orderPaymentDetail.setTaxRate1(o.getTaxRate1());
							orderPaymentDetail.setPriceTax1(o.getPriceTax1());
						}
						if (o.getPriceTax2() != null ) {

							orderPaymentDetail.setTaxName2(o.getTaxName2());
							orderPaymentDetail.setTaxDisplayName2(o.getTaxDisplayName2());
							orderPaymentDetail.setTaxRate2(o.getTaxRate2());
							orderPaymentDetail.setPriceTax2(o.getPriceTax2());
						}
						if (o.getPriceTax3() != null ) {

							orderPaymentDetail.setTaxName3(o.getTaxName3());
							orderPaymentDetail.setTaxDisplayName3(o.getTaxDisplayName3());
							orderPaymentDetail.setTaxRate3(o.getTaxRate3());
							orderPaymentDetail.setPriceTax3(o.getPriceTax3());
						}
						if (o.getPriceTax4() != null ) {

							orderPaymentDetail.setTaxName4(o.getTaxName4());
							orderPaymentDetail.setTaxDisplayName4(o.getTaxDisplayName4());
							orderPaymentDetail.setTaxRate4(o.getTaxRate4());
							orderPaymentDetail.setPriceTax4(o.getPriceTax4());
						}

					return orderPaymentDetail;
			} catch (Exception e) {
			logger.severe(e);
			}
			return orderPaymentDetail;
	}
	private TransactionStatus getTransactionStatusByName(
			EntityManager em, String name) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TransactionStatus> criteria = builder
				.createQuery(TransactionStatus.class);
		Root<TransactionStatus> r = criteria.from(TransactionStatus.class);
		TypedQuery<TransactionStatus> query = em.createQuery(criteria
				.select(r).where(
						builder.equal(r.get(TransactionStatus_.name),
								name)));
		TransactionStatus result = (TransactionStatus) query
				.getSingleResult();
		return result;
	}
	
}
