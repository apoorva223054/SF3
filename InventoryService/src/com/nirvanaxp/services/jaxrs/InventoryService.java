/**

 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
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

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.Utilities;
import com.nirvanaxp.common.utils.synchistory.InsertIntoHistory;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.email.ReceiptPDFFormat;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.POSExceptionMessage;
import com.nirvanaxp.services.helper.InventoryItem;
import com.nirvanaxp.services.helper.InventoryManagementHelper;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.AttributeItemBomDisplayPacket;
import com.nirvanaxp.services.jaxrs.packets.InventoryAttributeToBomPacket;
import com.nirvanaxp.services.jaxrs.packets.InventoryFetchPacket;
import com.nirvanaxp.services.jaxrs.packets.InventoryItemToBOM;
import com.nirvanaxp.services.jaxrs.packets.InventoryOrderReceiptPacket;
import com.nirvanaxp.services.jaxrs.packets.InventoryPacket;
import com.nirvanaxp.services.jaxrs.packets.InventoryPostPacket;
import com.nirvanaxp.services.jaxrs.packets.InventoryPushPacket;
import com.nirvanaxp.services.jaxrs.packets.InventoryToBomPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemByLocationIdPacket;
import com.nirvanaxp.services.jaxrs.packets.OptionalAttribtes;
import com.nirvanaxp.services.jaxrs.packets.PhysicalInventoryPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.jaxrs.packets.RequestOrderDetailItemsPacket;
import com.nirvanaxp.services.jaxrs.packets.RequestOrderPacket;
import com.nirvanaxp.services.jaxrs.packets.UOMPostPacket;
import com.nirvanaxp.services.jaxrs.packets.UnitConversionPostPacket;
import com.nirvanaxp.services.packet.GoodsReceiveNotesPacket;
import com.nirvanaxp.services.packet.ItemToSupplierPacket;
import com.nirvanaxp.services.packet.PDFInHTMLFormatPacket;
import com.nirvanaxp.services.packet.RequestOrderDetailItemsDisplayPacket;
import com.nirvanaxp.services.packet.SupplierPacket;
import com.nirvanaxp.services.util.email.EmailTemplateKeys;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem_;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
import com.nirvanaxp.types.entities.inventory.GoodsReceiveNotes;
import com.nirvanaxp.types.entities.inventory.Inventory;
import com.nirvanaxp.types.entities.inventory.InventoryAttributeBOM;
import com.nirvanaxp.types.entities.inventory.InventoryItemBom;
import com.nirvanaxp.types.entities.inventory.InventoryItemDefault;
import com.nirvanaxp.types.entities.inventory.InventoryOrderReceipt;
import com.nirvanaxp.types.entities.inventory.InventoryOrderReceiptForItem;
import com.nirvanaxp.types.entities.inventory.InventoryOrderReceiptForItem_;
import com.nirvanaxp.types.entities.inventory.Inventory_;
import com.nirvanaxp.types.entities.inventory.PhysicalInventory;
import com.nirvanaxp.types.entities.inventory.RequestOrder;
import com.nirvanaxp.types.entities.inventory.RequestOrderDetailItems;
import com.nirvanaxp.types.entities.inventory.UnitConversion;
import com.nirvanaxp.types.entities.inventory.UnitOfMeasurement;
import com.nirvanaxp.types.entities.inventory.UnitOfMeasurement_;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.Location_;
import com.nirvanaxp.types.entities.orders.ItemToSupplier;
import com.nirvanaxp.types.entities.orders.ItemToSupplier_;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.user.utility.GlobalUserUtil;
import com.nirvanaxp.user.utility.GlobalUsermanagement;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class InventoryService extends AbstractNirvanaService {

	@Context
	HttpServletRequest httpRequest;

	private final static NirvanaLogger logger = new NirvanaLogger(InventoryService.class.getName());

	@Override
	protected NirvanaLogger getNirvanaLogger() {
		return logger;
	}

	/**
	 * 
	 * @param sessionId
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getStockUnitOfMeasureByLocationId/{locationId}")
	public String getStockUnitOfMeasureByLocationId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("locationId") String locationId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select uom from UnitOfMeasurement uom where uom.status !='D' and uom.locationId =?";
			TypedQuery<UnitOfMeasurement> query = em.createQuery(queryString, UnitOfMeasurement.class).setParameter(1,
					locationId);
			List<UnitOfMeasurement> resultSet = query.getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(resultSet);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 * @param sessionId
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUnitConversionByFromUOMId/{fromUOMId}")
	public String getUnitConversionByFromUOMId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("fromUOMId") String fromUOMId) throws Exception {
		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			String queryString = "select uom from UnitConversion uom where uom.status !='D' and uom.fromUOMId =?";
			TypedQuery<UnitConversion> query = em.createQuery(queryString, UnitConversion.class).setParameter(1,
					fromUOMId);
			List<UnitConversion> resultSet = query.getResultList();

			if (resultSet != null && resultSet.size() > 0) {
				for (UnitConversion conversion : resultSet) {
					if (conversion.getFromUOMId() != null) {
						CriteriaQuery<UnitOfMeasurement> criteriaUom = builder.createQuery(UnitOfMeasurement.class);
						Root<UnitOfMeasurement> uom1 = criteriaUom.from(UnitOfMeasurement.class);
						TypedQuery<UnitOfMeasurement> query1 = em.createQuery(criteriaUom.select(uom1)
								.where(builder.equal(uom1.get(UnitOfMeasurement_.id), conversion.getFromUOMId())));
						conversion.setFromUOMIdName(query1.getSingleResult().getDisplayName());

					}

					if (conversion.getToUOMId() != null) {
						CriteriaQuery<UnitOfMeasurement> criteriaUom = builder.createQuery(UnitOfMeasurement.class);
						Root<UnitOfMeasurement> uom1 = criteriaUom.from(UnitOfMeasurement.class);
						TypedQuery<UnitOfMeasurement> query1 = em.createQuery(criteriaUom.select(uom1)
								.where(builder.equal(uom1.get(UnitOfMeasurement_.id), conversion.getToUOMId())));
						conversion.setToUOMIdName(query1.getSingleResult().getDisplayName());

					}

				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(resultSet);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/checkIfUOMNamExists/{locationId}/{uomName}")
	public String checkIfUOMNamExists(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("locationId") String locationId, @PathParam("uomName") String uomName) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select uom from UnitOfMeasurement uom where uom.status !='D' and uom.locationId = ?  and uom.name = ?";
			TypedQuery<UnitOfMeasurement> query = em.createQuery(queryString, UnitOfMeasurement.class)
					.setParameter(1, locationId).setParameter(2, uomName);
			List<UnitOfMeasurement> resultSet = query.getResultList();
			if (resultSet != null && resultSet.size() > 0) {
				return "1";
			} else {
				return "0";
			}
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getSellableUnitOfMeasureByLocationIdAndStockId/{locationId}/{stockUomId}")
	public String getUnitOfMeasureByLocatioId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("locationId") String locationId, @PathParam("stockUomId") String stockUomId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UnitOfMeasurement> criteria = builder.createQuery(UnitOfMeasurement.class);
			Root<UnitOfMeasurement> r = criteria.from(UnitOfMeasurement.class);
			TypedQuery<UnitOfMeasurement> query = em
					.createQuery(criteria.select(r).where(builder.notEqual(r.get(UnitOfMeasurement_.status), "D"),
							builder.equal(r.get(UnitOfMeasurement_.stockUomId), stockUomId),
							builder.equal(r.get(UnitOfMeasurement_.locationId), locationId)));
			UnitOfMeasurement stockUom = (UnitOfMeasurement) new CommonMethods().getObjectById("UnitOfMeasurement", em,
					UnitOfMeasurement.class, stockUomId);
			List<UnitOfMeasurement> uomList = query.getResultList();
			if (uomList == null) {
				uomList = new ArrayList<UnitOfMeasurement>();
			}
			uomList.add(stockUom);
			return new JSONUtility(httpRequest).convertToJsonString(uomList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllUnitOfMeasurement")
	public String getAllUnitOfMeasurement(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select u from UnitOfMeasurement u where u.status !='D'";
			TypedQuery<UnitOfMeasurement> query = em.createQuery(queryString, UnitOfMeasurement.class);
			List<UnitOfMeasurement> resultSet = query.getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(resultSet);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/turnRealTimeInventoryOnOff/{isRealTimeInventoryRequired}/{locationId}/{merchantId}/{updatedBy}")
	public String getAllUnitOfMeasurement(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("isRealTimeInventoryRequired") int isRealTimeInventoryRequired,
			@PathParam("locationId") String locationId, @PathParam("merchantId") int merchantId,
			@PathParam("updatedBy") String updatedBy) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					locationId);
			;
			location.setIsRealTimeInventoryRequired(isRealTimeInventoryRequired);
			location.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			location.setUpdatedBy(updatedBy);
			EntityTransaction tx = em.getTransaction();
			try {
				// start transaction
				tx.begin();
				em.merge(location);
				tx.commit();
			} catch (RuntimeException e) {
				// on error, if transaction active,
				// rollback
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}

			// we are putting location service, as its part of sync cdo, client
			// will automatically pull through
			PostPacket postPacket = new PostPacket();
			postPacket.setMerchantId("" + merchantId);
			postPacket.setLocationId("" + locationId);
			sendPacketForBroadcast(POSNServiceOperations.LocationsService_update.name(), postPacket);

			return "1";
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getInventoryStatusForLocationId/{locationId}")
	public String getAllUnitOfMeasurement(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("locationId") String locationId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					locationId);
			;
			// 1 means its required , 0 means not required
			return "" + location.getIsRealTimeInventoryRequired();
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * @param sessionId
	 * @param locationId
	 * @param categoryId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getInventoryByLocationIdAndCategoryId/{locationId}/{categoryId}/{date}")
	public String getInventoryLocationIdAndCategoryId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("locationId") String locationId, @PathParam("categoryId") int categoryId,
			@PathParam("date") String date) throws Exception {
		EntityManager em = null;
		try {

			List<Inventory> inventoryOrderReceipts = new ArrayList<Inventory>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call p_get_inventory_list_by_category( ?,?,? )")
					.setParameter(1, locationId).setParameter(2, categoryId).setParameter(3, date).getResultList();
			for (Object[] objRow : resultList) {
				int i = 0;
				if ((String) objRow[i] != null) {
					Inventory inventory = new Inventory();
					inventory.setId((String) objRow[i++]);
					inventory.setItemId((String) objRow[i++]);
					inventory.setUnitOfMeasurementId((String) objRow[i++]);
					inventory.setPrimarySupplierId((String) objRow[i++]);
					inventory.setSecondarySupplierId((String) objRow[i++]);
					inventory.setTertiarySupplierId((String) objRow[i++]);
					inventory.setTotalAvailableQuanity((BigDecimal) objRow[i++]);
					inventory.setTotalUsedQuanity((BigDecimal) objRow[i++]);
					inventory.setLocationId((String) objRow[i++]);
					inventory.setDisplaySequence((Integer) objRow[i++]);
					// char casting issue reolved
					inventory.setStatus((Character) objRow[i++] + "");
					inventory.setLocalTime(
							new TimezoneTime().getLocationSpecificTimeToAdd(inventory.getLocationId(), em));
					inventory.setCreated((Timestamp) objRow[i++]);
					inventory.setUpdated((Timestamp) objRow[i++]);
					inventory.setCreatedBy((String) objRow[i++]);
					inventory.setUpdatedBy((String) objRow[i++]);
					i = i + 3;
					inventory.setUomName((String) objRow[i++]);
					inventory.setItemDisplayName((String) objRow[i++]);
					inventory.setItemTypeName((String) objRow[i++]);
					inventory.setSupplier1Name((String) objRow[i++]);
					inventory.setSupplier2Name((String) objRow[i++]);
					inventory.setSupplier3Name((String) objRow[i++]);
					// inventory.setPhysicaInventory((BigDecimal) objRow[i++]);
					i = i + 3;
					if ((objRow[i - 1] != null) && (((Character) objRow[i]).equals('A'))) {
						InventoryItemDefault inventoryItemDefault = new InventoryItemDefault();
						inventoryItemDefault.setMinimumOrderQuantity((BigDecimal) objRow[i - 3]);
						inventoryItemDefault.setEconomicOrderQuantity((BigDecimal) objRow[i - 2]);
						inventoryItemDefault.setD86Threshold((BigDecimal) objRow[i - 1]);
						inventory.setInventoryItemDefault(inventoryItemDefault);
					}
					i++;
					if (objRow[i] != null) {
						inventory.setPhysicalQuantity((BigDecimal) objRow[i]);
					} else {
						inventory.setPhysicalQuantity(new BigDecimal(0.00));
					}

					inventoryOrderReceipts.add(inventory);
				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(inventoryOrderReceipts);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param sessionId
	 * @param locationId
	 * @param categoryId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getInventoryByLocationId")
	public String getInventoryByLocationId(InventoryFetchPacket inventoryFetchPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			String temp = Utilities.convertAllSpecialCharForSearch(inventoryFetchPacket.getItemName());

			List<Inventory> inventoryOrderReceipts = new ArrayList<Inventory>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String locationsId=inventoryFetchPacket.getLocationId();
			String categoryId=inventoryFetchPacket.getCategoryId();
			String supplierId=inventoryFetchPacket.getSupplierId();
			if(categoryId==null){
				categoryId="";
			}
			if(supplierId==null){
				supplierId="";
			}
			if(locationsId==null){
				locationsId="";
			}
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em
					.createNativeQuery("call p_get_inventory_list_by_locationId( '"+locationsId+"',"+inventoryFetchPacket.getStart()+","+inventoryFetchPacket.getEnd()+",'"+inventoryFetchPacket.getDate()+"','"+temp+"','"+categoryId+"','"+supplierId+"' )")
					.getResultList();
			for (Object[] objRow : resultList) {
				int i = 0;
				
				if ((String) objRow[i] != null) {
					Inventory inventory = new Inventory();
					inventory.setId((String) objRow[i++]);
					inventory.setItemId((String) objRow[i++]);
					inventory.setUnitOfMeasurementId((String) objRow[i++]);
					inventory.setPrimarySupplierId((String) objRow[i++]);
					inventory.setSecondarySupplierId((String) objRow[i++]);
					inventory.setTertiarySupplierId((String) objRow[i++]);
					// convert for display
					Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class, inventory.getItemId());
					inventory.setTotalAvailableQuanity((BigDecimal) objRow[i++]);
					if (inventoryFetchPacket.getIsAdmin() == 0) {
						BigDecimal convertedUnit = convertUnit(item.getSellableUom(), item.getStockUom(), logger,
								inventory.getTotalAvailableQuanity(), em);
						inventory.setTotalAvailableQuanity(convertedUnit);
					}

					inventory.setTotalUsedQuanity((BigDecimal) objRow[i++]);
					if (inventoryFetchPacket.getIsAdmin() == 0) {
						BigDecimal totalUsedQuanity = convertUnit(item.getSellableUom(), item.getStockUom(), logger,
								inventory.getTotalUsedQuanity(), em);
						inventory.setTotalUsedQuanity(totalUsedQuanity);
					}

					inventory.setLocationId((String) objRow[i++]);
					inventory.setDisplaySequence((Integer) objRow[i++]);
					// char casting issue reolved
					inventory.setStatus((Character) objRow[i++] + "");
					inventory.setCreated((Timestamp) objRow[i++]);
					inventory.setLocalTime(
							new TimezoneTime().getLocationSpecificTimeToAdd(inventory.getLocationId(), em));
					inventory.setUpdated((Timestamp) objRow[i++]);
					inventory.setCreatedBy((String) objRow[i++]);
					inventory.setUpdatedBy((String) objRow[i++]);
					i = i + 3;
					inventory.setUomName((String) objRow[i++]);
					inventory.setItemDisplayName((String) objRow[i++]);
					inventory.setItemTypeName((String) objRow[i++]);
					inventory.setSupplier1Name((String) objRow[i++]);
					inventory.setSupplier2Name((String) objRow[i++]);
					inventory.setSupplier3Name((String) objRow[i++]);
					// inventory.setPhysicaInventory((BigDecimal) objRow[i++]);
					i = i + 3;
					if ((objRow[i - 1] != null) && (((Character) objRow[i]).equals('A'))) {
						InventoryItemDefault inventoryItemDefault = new InventoryItemDefault();
						inventoryItemDefault.setMinimumOrderQuantity((BigDecimal) objRow[i - 3]);
						inventoryItemDefault.setEconomicOrderQuantity((BigDecimal) objRow[i - 2]);
						inventoryItemDefault.setItemId(inventory.getItemId());

						inventoryItemDefault.setD86Threshold((BigDecimal) objRow[i - 1]);
						if (inventoryFetchPacket.getIsAdmin() == 0) {
							BigDecimal totalUsedQuanity = convertUnit(item.getSellableUom(), item.getStockUom(), logger,
									(BigDecimal) objRow[i - 1], em);
							inventoryItemDefault.setD86Threshold(totalUsedQuanity);
						}

						inventoryItemDefault.setStatus((Character) objRow[i++] + "");
						inventoryItemDefault.setCreated((Timestamp) objRow[i++]);
						inventoryItemDefault.setUpdated((Timestamp) objRow[i++]);
						inventoryItemDefault.setUpdatedBy((String) objRow[i++]);
						inventoryItemDefault.setCreatedBy((String) objRow[i++]);

						inventoryItemDefault.setId((Integer) objRow[i]);

						inventory.setInventoryItemDefault(inventoryItemDefault);
					}
					i++;
					if (objRow[i] != null) {
						inventory.setPhysicalQuantity((BigDecimal) objRow[i]);
					} else {
						inventory.setPhysicalQuantity(new BigDecimal(0.00));
					}
					i = i + 1;
					inventory.setCategoryName((String) objRow[i]);
					i = i + 1;
					inventory.setStockUomName((String) objRow[i]);
					i = i + 1;
					inventory.setSellableUomName((String) objRow[i]);
					
					inventoryOrderReceipts.add(inventory);
				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(inventoryOrderReceipts);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/getInventoryCountByLocationId")
	public BigInteger getInventoryCountByLocationId(InventoryFetchPacket inventoryFetchPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			String temp = Utilities.convertAllSpecialCharForSearch(inventoryFetchPacket.getItemName());

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			BigInteger resultList = (BigInteger) em
					.createNativeQuery("call p_get_inventory_count_by_locationId( ?,?,?,? )")
					.setParameter(1, inventoryFetchPacket.getLocationId()).setParameter(2, temp)
					.setParameter(3, inventoryFetchPacket.getCategoryId())
					.setParameter(4, inventoryFetchPacket.getSupplierId()).getSingleResult();

			return resultList;

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param sessionId
	 * @param attributeId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getOptionalAttributeByAttributeId/{attributeId}")
	public String getOptionalAttributeByAttributeId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("attributeId") String attributeId) throws Exception {
		// sessionId="683cf6eb4cd4ca6e7aeb8a312bf3975";
		EntityManager em = null;
		try {

			List<OptionalAttribtes> optionalAttribtesList = new ArrayList<OptionalAttribtes>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getOptionalAttributeForAttributeId(?)")
					.setParameter(1, attributeId).getResultList();
			for (Object[] objRow : resultList) {
				int i = 0;
				if ((String) objRow[i] != null) {
					OptionalAttribtes optionalAttribtes = new OptionalAttribtes();
					optionalAttribtes.setAttributeIdFg((String) objRow[i++]);
					optionalAttribtes.setAttributeDisplayName((String) objRow[i++]);
					if (objRow[i] != null)
						optionalAttribtes.setStatusAttributeToBOM(objRow[i].toString());

					if ((objRow[i]) != null && (objRow[i].toString()).equalsIgnoreCase("A")) {
						i++;

						optionalAttribtes.setAttributeBomId((Integer) objRow[i++]);
						i++;
						optionalAttribtes.setItemIdRm((String) objRow[i++]);
						optionalAttribtes.setQuantity((BigDecimal) objRow[i++]);
						optionalAttribtes.setRmSellableUom((String) objRow[i++]);
						optionalAttribtes.setItemDisplayName((String) objRow[i++]);
						optionalAttribtes.setUomDisplayName((String) objRow[i++]);

						if ((String) objRow[10] != null) {
							optionalAttribtes.setCategoryId((String) objRow[10]);
							optionalAttribtes.setCategoryName((String) objRow[11]);
						} else if ((String) objRow[12] != null) {
							optionalAttribtes.setCategoryId((String) objRow[12]);
							optionalAttribtes.setCategoryName((String) objRow[13]);
						} else {
							optionalAttribtes.setCategoryId((String) objRow[14]);
							optionalAttribtes.setCategoryName((String) objRow[15]);

						}

					}
					// to eliminate duplicate entires for d status inventory
					// attribute bom table
					if (optionalAttribtesList != null && optionalAttribtes != null) {
						if (optionalAttribtesList.contains(optionalAttribtes) == false) {
							optionalAttribtesList.add(optionalAttribtes);
						} else {
							if (optionalAttribtes.getStatusAttributeToBOM() != null
									&& optionalAttribtes.getStatusAttributeToBOM().equalsIgnoreCase("D") == false) {
								// means we need to add this one and
								// ignore previos one
								int index = optionalAttribtesList.indexOf(optionalAttribtes);
								if (index != -1) {
									optionalAttribtesList.remove(index);
									optionalAttribtesList.add(optionalAttribtes);
								}
							}
						}

					}

				}

			}

			return new JSONUtility(httpRequest).convertToJsonString(optionalAttribtesList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param sessionId
	 * @param itemId
	 * @param isForcedAttribute
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAttributeByItemId/{itemId}/{isForcedAttribute}")
	public String getAttributeByItemId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("itemId") String itemId, @PathParam("isForcedAttribute") int isForcedAttribute) throws Exception {

		EntityManager em = null;
		try {

			List<ItemsAttribute> attributeList = new ArrayList<ItemsAttribute>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getAttributeForItemId( ?,? )")
					.setParameter(1, itemId).setParameter(2, isForcedAttribute).getResultList();
			for (Object[] objRow : resultList) {
				int i = 0;
				if ((String) objRow[i] != null) {
					ItemsAttribute itemAttribute = new ItemsAttribute();
					itemAttribute.setId((String) objRow[i++]);
					itemAttribute.setName((String) objRow[i++]);
					itemAttribute.setSellingPrice((BigDecimal) objRow[i++]);
					itemAttribute.setDisplayName((String) objRow[i++]);
					itemAttribute.setShortName((String) objRow[i++]);
					itemAttribute.setLocationsId((String) objRow[i++]);
					itemAttribute.setMsrPrice((BigDecimal) objRow[i++]);
					itemAttribute.setMultiSelect((Byte) objRow[i++]);
					itemAttribute.setImageName((String) objRow[i++]);
					itemAttribute.setHexCodeValues((String) objRow[i++]);
					itemAttribute.setDescription((String) objRow[i++]);
					itemAttribute.setStatus((Character) objRow[i++] + "");
					itemAttribute.setIsActive((Byte) objRow[i++]);
					itemAttribute.setSortSequence((Integer) objRow[i++]);
					itemAttribute.setCreated((Timestamp) objRow[i++]);
					itemAttribute.setCreatedBy((String) objRow[i++]);
					itemAttribute.setUpdated((Timestamp) objRow[i++]);
					itemAttribute.setUpdatedBy((String) objRow[i++]);
					itemAttribute.setStockUom((String) objRow[i++]);
					itemAttribute.setSellableUom((String) objRow[i++]);
					attributeList.add(itemAttribute);

				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(attributeList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param sessionId
	 * @param itemId
	 * @param isForcedAttribute
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAttributeItemBOMUsingItemId/{itemId}/{isForcedAttribute}")
	public String getAttributeItemBOMUsingItemId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("itemId") int itemId, @PathParam("isForcedAttribute") int isForcedAttribute) throws Exception {

		EntityManager em = null;
		try {

			List<AttributeItemBomDisplayPacket> AttributeItemBomDisplayPacketList = new ArrayList<AttributeItemBomDisplayPacket>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getAttributeItemBOMUsingItemId( ?,? )")
					.setParameter(1, itemId).setParameter(2, isForcedAttribute).getResultList();
			for (Object[] objRow : resultList) {
				int i = 0;
				if ((Integer) objRow[i] != 0) {
					AttributeItemBomDisplayPacket attributeItemBomDisplayPacket = new AttributeItemBomDisplayPacket();

					InventoryAttributeBOM inventoryAttributeBOM = new InventoryAttributeBOM();
					inventoryAttributeBOM.setId((Integer) objRow[i++]);
					inventoryAttributeBOM.setAttributeIdFg((String) objRow[i++]);
					inventoryAttributeBOM.setItemIdRm((String) objRow[i++]);
					inventoryAttributeBOM.setRmSellableUom((String) objRow[i++]);
					inventoryAttributeBOM.setQuantity((BigDecimal) objRow[i++]);
					inventoryAttributeBOM.setStatus((String) objRow[i++]);

					inventoryAttributeBOM.setCreatedBy((String) objRow[i++]);
					inventoryAttributeBOM.setUpdatedBy((String) objRow[i++]);
					inventoryAttributeBOM.setCreated((Timestamp) objRow[i++]);
					inventoryAttributeBOM.setUpdated((Timestamp) objRow[i++]);

					attributeItemBomDisplayPacket.setInventoryAttributeBOM(inventoryAttributeBOM);

					attributeItemBomDisplayPacket.setItemName((String) objRow[i++]);
					attributeItemBomDisplayPacket.setItemDisplayName((String) objRow[i++]);

					if ((String) objRow[i + 1] != null) {
						attributeItemBomDisplayPacket.setCategoryId((String) objRow[i++]);// 13
						attributeItemBomDisplayPacket.setCategpryName((String) objRow[i++]);
					} else if ((String) objRow[i + 3] != null) {
						i = i + 2;
						attributeItemBomDisplayPacket.setCategoryId((String) objRow[i++]);// 15
						attributeItemBomDisplayPacket.setCategpryName((String) objRow[i++]);
					} else {
						i = i + 4;
						attributeItemBomDisplayPacket.setCategoryId((String) objRow[i++]);
						attributeItemBomDisplayPacket.setCategpryName((String) objRow[i++]);
					}

					AttributeItemBomDisplayPacketList.add(attributeItemBomDisplayPacket);

				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(AttributeItemBomDisplayPacketList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param sessionId
	 * @param attributeId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAttributeItemBOMUsingAttributeId/{attributeId}/")
	public String getAttributeItemBOMUsingAttributeId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("attributeId") String attributeId) throws Exception {

		EntityManager em = null;
		try {

			List<AttributeItemBomDisplayPacket> AttributeItemBomDisplayPacketList = new ArrayList<AttributeItemBomDisplayPacket>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getAttributeItemBOMUsingAttributeId(? )")
					.setParameter(1, attributeId).getResultList();
			for (Object[] objRow : resultList) {
				int i = 0;
				if ((Integer) objRow[i] != 0) {
					AttributeItemBomDisplayPacket attributeItemBomDisplayPacket = new AttributeItemBomDisplayPacket();
					InventoryAttributeBOM inventoryAttributeBOM = new InventoryAttributeBOM();
					inventoryAttributeBOM.setId((Integer) objRow[i++]);
					inventoryAttributeBOM.setAttributeIdFg((String) objRow[i++]);
					inventoryAttributeBOM.setItemIdRm((String) objRow[i++]);
					inventoryAttributeBOM.setRmSellableUom((String) objRow[i++]);
					inventoryAttributeBOM.setQuantity((BigDecimal) objRow[i++]);
					inventoryAttributeBOM.setStatus((String) "" + objRow[i++]);
					inventoryAttributeBOM.setCreatedBy((String) objRow[i++]);
					inventoryAttributeBOM.setUpdatedBy((String) objRow[i++]);
					inventoryAttributeBOM.setCreated((Timestamp) objRow[i++]);
					inventoryAttributeBOM.setUpdated((Timestamp) objRow[i++]);
					attributeItemBomDisplayPacket.setInventoryAttributeBOM(inventoryAttributeBOM);
					attributeItemBomDisplayPacket.setItemName((String) objRow[i++]);
					attributeItemBomDisplayPacket.setItemDisplayName((String) objRow[i++]);
					if ((String) objRow[i + 1] != null) {
						attributeItemBomDisplayPacket.setCategoryId((String) objRow[i++]);// 13
						attributeItemBomDisplayPacket.setCategpryName((String) objRow[i++]);
					} else if ((String) objRow[i + 3] != null) {
						i = i + 2;
						attributeItemBomDisplayPacket.setCategoryId((String) objRow[i++]);// 15
						attributeItemBomDisplayPacket.setCategpryName((String) objRow[i++]);
					} else {
						i = i + 4;
						attributeItemBomDisplayPacket.setCategoryId((String) objRow[i++]);
						attributeItemBomDisplayPacket.setCategpryName((String) objRow[i++]);
					}
					attributeItemBomDisplayPacket.setUomDisplayName((String) objRow[18]);
					AttributeItemBomDisplayPacketList.add(attributeItemBomDisplayPacket);
				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(AttributeItemBomDisplayPacketList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param sessionId
	 * @param attributeId
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAttributeItemBOMUsingAttributeIdAndItemId/{attributeId}/{itemId}")
	public String getAttributeItemBOMUsingAttributeIdAndItemId(
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("attributeId") int attributeId,
			@PathParam("itemId") int itemId) throws Exception {

		EntityManager em = null;
		try {

			List<AttributeItemBomDisplayPacket> AttributeItemBomDisplayPacketList = new ArrayList<AttributeItemBomDisplayPacket>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getAttributeItemBOMUsingAttributeIdAndItemId( ?,? )")
					.setParameter(1, attributeId).setParameter(2, itemId).getResultList();

			for (Object[] objRow : resultList) {
				int i = 0;
				if ((Integer) objRow[i] != 0) {
					AttributeItemBomDisplayPacket attributeItemBomDisplayPacket = new AttributeItemBomDisplayPacket();

					InventoryAttributeBOM inventoryAttributeBOM = new InventoryAttributeBOM();
					inventoryAttributeBOM.setId((Integer) objRow[i++]);
					inventoryAttributeBOM.setAttributeIdFg((String) objRow[i++]);
					inventoryAttributeBOM.setItemIdRm((String) objRow[i++]);
					inventoryAttributeBOM.setRmSellableUom((String) objRow[i++]);
					inventoryAttributeBOM.setQuantity(((BigDecimal) objRow[i++]));
					inventoryAttributeBOM.setStatus((String) objRow[i++]);

					inventoryAttributeBOM.setCreatedBy((String) objRow[i++]);
					inventoryAttributeBOM.setUpdatedBy((String) objRow[i++]);
					inventoryAttributeBOM.setCreated((Timestamp) objRow[i++]);
					inventoryAttributeBOM.setUpdated((Timestamp) objRow[i++]);

					attributeItemBomDisplayPacket.setInventoryAttributeBOM(inventoryAttributeBOM);

					attributeItemBomDisplayPacket.setItemName((String) objRow[i++]);
					attributeItemBomDisplayPacket.setItemDisplayName((String) objRow[i++]);

					if ((String) objRow[i + 1] != null) {
						attributeItemBomDisplayPacket.setCategoryId((String) objRow[i++]);// 13
						attributeItemBomDisplayPacket.setCategpryName((String) objRow[i++]);
					} else if ((String) objRow[i + 3] != null) {
						i = i + 2;
						attributeItemBomDisplayPacket.setCategoryId((String) objRow[i++]);// 15
						attributeItemBomDisplayPacket.setCategpryName((String) objRow[i++]);
					} else {
						i = i + 4;
						attributeItemBomDisplayPacket.setCategoryId((String) objRow[i++]);
						attributeItemBomDisplayPacket.setCategpryName((String) objRow[i++]);
					}
					attributeItemBomDisplayPacket.setUomDisplayName((String) objRow[18]);
					AttributeItemBomDisplayPacketList.add(attributeItemBomDisplayPacket);

				}
			}
			return new JSONUtility(httpRequest).convertToJsonString(AttributeItemBomDisplayPacketList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	// get all locations that are not supplier type
	/**
	 * @param sessionId
	 * @param locationId
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getInventoryOrderReceiptByFromAndToDate/{locationId}/{fromDate}/{toDate}")
	public String getInventoryOrderReceiptByToAndFromDate(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("locationId") String locationId, @PathParam("fromDate") String fromDate,
			@PathParam("toDate") String toDate) throws Exception {
		// sessionId = "bf1f878ff4386e53c0a4c79545494eea";

		EntityManager em = null;
		try {

			List<InventoryOrderReceipt> inventoryOrderReceipts = new ArrayList<InventoryOrderReceipt>();

			if (fromDate != null && fromDate.length() > 0 && toDate != null && toDate.length() > 0) {

				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
				@SuppressWarnings("unchecked")
				List<Object[]> resultList = em.createNativeQuery("call get_inventory_order_receipt( ?,?,? )")
						.setParameter(1, locationId).setParameter(2, fromDate).setParameter(3, toDate).getResultList();
				for (Object[] objRow : resultList) {
					int i = 0;
					if ((Integer) objRow[i] != 0) {
						InventoryOrderReceipt inventoryOrderReceipt = new InventoryOrderReceipt();
						inventoryOrderReceipt.setId((Integer) objRow[i++]);
						if (objRow[i] != null)
							// changed by Apoorva
							inventoryOrderReceipt.setPurchaseOrderSalesId(String.valueOf(objRow[i]));
						i++;
						inventoryOrderReceipt.setLocationId((String) objRow[i++]);
						inventoryOrderReceipt.setItemId((String) objRow[i++]);
						inventoryOrderReceipt.setCreatedBy((String) objRow[i++]);
						inventoryOrderReceipt.setStatus((Character) objRow[i++] + "");
						inventoryOrderReceipt.setReceivedDate((String) objRow[i++]);
						inventoryOrderReceipt.setSellByDate((String) objRow[i++]);

						inventoryOrderReceipt.setPurchasedQuantity((BigDecimal) objRow[i++]);
						inventoryOrderReceipt.setSupplierId((String) objRow[i++]);
						inventoryOrderReceipt.setUnitOfMeasure((String) objRow[i++]);

						inventoryOrderReceipt.setUnitOfMeasureName((String) objRow[i++]);
						inventoryOrderReceipt.setSupplierName((String) objRow[i++]);
						inventoryOrderReceipt.setItemName((String) objRow[i++]);
						// check if column 17 is not null, then its root
						// caterogy,
						// chcek column 16
						// check 15
						if ((String) objRow[i++] != null) {
							inventoryOrderReceipt.setCategoriesName((String) objRow[i - 1]);
						}

						if ((String) objRow[i++] != null) {
							inventoryOrderReceipt.setCategoriesName((String) objRow[i - 1]);
						}

						if ((String) objRow[i++] != null) {
							inventoryOrderReceipt.setCategoriesName((String) objRow[i - 1]);
						}

						inventoryOrderReceipt.setCreated((Timestamp) objRow[i++]);
						inventoryOrderReceipt.setPriceMsrp((BigDecimal) objRow[i++]);

						inventoryOrderReceipts.add(inventoryOrderReceipt);

					}
				}
				return new JSONUtility(httpRequest).convertToJsonString(inventoryOrderReceipts);
			} else {
				throw new Exception("to and from date cannot be null");
			}

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param sessionId
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getSupplierByLocationId/{locationId}")
	public String getSupplierByLocationId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("locationId") String locationId) throws Exception {
		EntityManager em = null;
		try {

			List<Location> suppliersList = new ArrayList<Location>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call p_get_supplier_by_locationid( ? )")
					.setParameter(1, locationId).getResultList();
			for (Object[] objRow : resultList) { // if this has primary key not
													// 0

				if ((String) objRow[0] != null) {
					Location supplier = new Location((String) objRow[0]);
					supplier.setName((String) objRow[1]);
					supplier.setLocationsTypeId((int) objRow[2]);
					suppliersList.add(supplier);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(suppliersList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param sessionId
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAllSupplierByLocationId/{locationId}")
	public String getAllSupplierByLocationId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("locationId") String locationId) throws Exception {
		EntityManager em = null;
		try {

			List<Location> suppliersList = new ArrayList<Location>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call p_get_supplier_by_locationid( ? )")
					.setParameter(1, locationId).getResultList();
			for (Object[] objRow : resultList) { // if this has primary key not
													// 0

				if ((String) objRow[0] != null) {
					Location supplier = new Location((String) objRow[0]);
					supplier.setName((String) objRow[1]);
					supplier.setLocationsTypeId((int) objRow[2]);
					suppliersList.add(supplier);
				}
			}

			suppliersList.addAll(getRootLocations(httpRequest, em));

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(suppliersList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	List<Location> getRootLocations(HttpServletRequest httpRequest, EntityManager em) {

		// get root locations where location type is not supplier

		// int supplierLocationType = getLocationTypeForSupplier();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Location> cl = builder.createQuery(Location.class);
		Root<Location> l = cl.from(Location.class);
		TypedQuery<Location> query = em
				.createQuery(cl.select(l).where(builder.equal(l.get(Location_.locationsId), "0"),
						builder.notEqual(l.get(Location_.status), "D"), builder.notEqual(l.get(Location_.status), "I"),
						builder.notEqual(l.get(Location_.isThirdPartyLocation), 1)));
		return query.getResultList();

	}

	/**
	 * @param sessionId
	 * @param itemId
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getItemToBOMForItemIdAndLocationId/{itemId}/{locationId}")
	public String getItemToBOMForItemId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("itemId") String itemId, @PathParam("locationId") String locationId) throws Exception {
		EntityManager em = null;
		try {
			List<InventoryItemToBOM> inventoryItemBomList = new ArrayList<InventoryItemToBOM>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getInventoryItemBOMByItemId(?,? )")
					.setParameter(1, itemId).setParameter(2, locationId).getResultList();
			for (Object[] objRow : resultList) { // if this has primary key not
													// 0

				if ((Integer) objRow[0] != 0) {
					InventoryItemToBOM inventoryItemBom = new InventoryItemToBOM();
					inventoryItemBom.setId((Integer) objRow[0]);
					inventoryItemBom.setItemFG((String) objRow[1]);
					inventoryItemBom.setItemRM((String) objRow[2]);
					inventoryItemBom.setSellableUomRM((String) objRow[3]);
					inventoryItemBom.setQuantity(((BigDecimal) objRow[4]));
					// 10,11
					inventoryItemBom.setItemNameRM((String) objRow[5]);
					inventoryItemBom.setItemNameRMDisplayName((String) objRow[6]);
					inventoryItemBom.setSellableUomRMName((String) objRow[8]);
					inventoryItemBom.setSellableUomRMDisplayName((String) objRow[9]);

					if ((String) objRow[15] != null) {
						inventoryItemBom.setCategoryId((String) objRow[14]);
						inventoryItemBom.setCategoryDisplayName((String) objRow[15]);
					} else if ((String) objRow[13] != null) {
						inventoryItemBom.setCategoryId((String) objRow[12]);
						inventoryItemBom.setCategoryDisplayName((String) objRow[13]);
					} else {
						inventoryItemBom.setCategoryId((String) objRow[10]);
						inventoryItemBom.setCategoryDisplayName((String) objRow[11]);
					}

					List<Location> LocationList = new InventoryServiceBean().getLocationList(httpRequest, em,
							inventoryItemBom);
					inventoryItemBom.setLocationList(LocationList);
					inventoryItemBomList.add(inventoryItemBom);
				}
			}

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(inventoryItemBomList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllSuppliers")
	public String getSupplierByLocationId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		try {

			List<Location> suppliersList = new ArrayList<Location>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call get_all_supplier() ").getResultList();
			for (Object[] objRow : resultList) { // if this has primary key not
													// 0
				if ((String) objRow[0] != null) {
					Location supplier = new Location((String) (objRow[0]));
					supplier.setName((String) (objRow[1]));
					suppliersList.add(supplier);
				}
			}

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(suppliersList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param sessionId
	 * @param locationId
	 * @param categoryId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getInventoryTypeItemByByLocationIdAndRootCategoryId/{locationId}/{categoryId}")
	public String getInventoryTypeItemByByLocationIdAndRootCategoryId(
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("locationId") String locationId,
			@PathParam("categoryId") String categoryId) throws Exception {

		EntityManager em = null;
		try {

			List<InventoryItem> itemsList = new ArrayList<InventoryItem>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call p_get_inventory_items( ?,?)")
					.setParameter(1, locationId).setParameter(2, categoryId).getResultList();
			for (Object[] objRow : resultList) {
				int i = 0;
				if ((String) objRow[i] != null) {

					InventoryItem item = new InventoryItem();
					item.setId((String) objRow[i++]);
					item.setName((String) objRow[i++]);
					item.setShortName((String) objRow[i++]);
					item.setStockUom((String) objRow[i++]);
					item.setSellableUom((String) objRow[i++]);
					if ((String) objRow[i] != null) {
						item.setSellableName((String) objRow[i]);

					}
					i++;
					if ((String) objRow[i] != null) {
						item.setStockName((String) objRow[i]);

					}
					i++;
					itemsList.add(item);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(itemsList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param sessionId
	 * @param locationId
	 * @param categoryId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getInventoryOnlyItemByByLocationIdAndRootCategoryId/{locationId}/{categoryId}")
	public String getInventoryOnlyItemByByLocationIdAndRootCategoryId(
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("locationId") String locationId,
			@PathParam("categoryId") int categoryId) throws Exception {

		EntityManager em = null;
		try {

			List<Item> itemsList = new ArrayList<Item>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call p_get_inventory_only_items(?,?)")
					.setParameter(1, locationId).setParameter(2, categoryId).getResultList();
			for (Object[] objRow : resultList) {

				int i = 0;
				if ((String) objRow[i] != null) {
					Item item = new Item();
					item.setId((String) objRow[i++]);
					item.setName((String) objRow[i++]);
					item.setShortName((String) objRow[i++]);
					item.setStockUom((String) objRow[i++]);
					item.setSellableUom((String) objRow[i++]);
					item.setImageName((String) objRow[i++]);
					itemsList.add(item);
				}
			}

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(itemsList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param sessionId
	 * @param locationId
	 * @param categoryId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getItemByByLocationIdAndRootCategoryId/{locationId}/{categoryId}")
	public String getItemByByLocationIdAndRootCategoryId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("locationId") String locationId, @PathParam("categoryId") String categoryId) throws Exception {

		EntityManager em = null;
		try {

			List<Item> itemsList = new ArrayList<Item>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call p_get_items_by_categoryid( ?,? )")
					.setParameter(1, locationId).setParameter(2, categoryId).getResultList();
			for (Object[] objRow : resultList) {

				int i = 0;
				if ((String) objRow[i] != null) {
					Item item = new Item();
					item.setId((String) objRow[i++]);
					item.setName((String) objRow[i++]);
					item.setShortName((String) objRow[i++]);
					item.setStockUom((String) objRow[i++]);
					item.setSellableUom((String) objRow[i++]);
					item.setImageName((String) objRow[i++]);
					if (objRow[i] != null)
						item.setItemType((Integer) objRow[i]);
					i++;
					item.setItemTypeName((String) objRow[i++]);
					item.setStockUomName((String) objRow[i++]);
					item.setSellableUomName((String) objRow[i++]);
					itemsList.add(item);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(itemsList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * { "inventory": { "id": 15, "updatedBy": 1385, "displaySequence": 0,
	 * "economicOrderQuantity": 0, "minimumOrderQuantity": 0,
	 * "primarySupplierId": 1, "secondarySupplierId": 0, "tertiarySupplierId":
	 * 0, "d86Threshold": 7 } }
	 * 
	 * @param inventoryPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/updateInventory")
	public String updateInventory(InventoryPacket inventoryPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// boolean shouldSendPush = false;
//			String json = new StoreForwardUtility().returnJsonPacket(inventoryPacket, "InventoryPacket", httpRequest);
			Inventory inventory = inventoryPacket.getInventory();
			Inventory inventoryFromDatabase=null;
			logger.severe("inventoryPacket.getLocalServerURL()=============================================================="+inventoryPacket.getLocalServerURL());
			if(inventoryPacket.getLocalServerURL()==0){
			 inventoryFromDatabase = (Inventory) new CommonMethods().getObjectById("Inventory", em,
					Inventory.class, inventory.getId());
			}else {
				
					String queryString = "select ig from Inventory ig where ig.itemId =?";
					TypedQuery<Inventory> query = em.createQuery(queryString, Inventory.class).setParameter(1, inventory.getItemId());
					try
					{
						inventoryFromDatabase = query.getSingleResult();
					}
					catch (Exception e)
					{
						logger.severe("Inventory No Record Found For Item Id" + inventory.getItemId() );
					}
			}

			if (inventoryFromDatabase == null) {
				POSExceptionMessage posExceptionMessage = new POSExceptionMessage(
						"Inventory does not exists for update", 1);
				return new JSONUtility(httpRequest).convertToJsonString(posExceptionMessage);
			}else {
				inventoryPacket.getPhysicalInventory().setInventoryId(inventoryFromDatabase.getId());				
			}

			InventoryItemDefault inventoryItemDefault = null;
			if (inventoryPacket.getInventoryItemDefault() != null) {
				inventoryItemDefault = inventoryPacket.getInventoryItemDefault();
			}

			InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();
			InventoryItemDefault inventoryItemDefaultFromDb = inventoryManagementHelper
					.getInventoryItemDefault(inventoryFromDatabase.getItemId(), em);

			if (inventoryPacket.getIsAdmin() == 1) {

			} else {

				Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
						inventoryFromDatabase.getItemId());
				BigDecimal convertedUnit = convertUnit(item.getStockUom(), item.getSellableUom(), logger,
						inventoryItemDefault.getD86Threshold(), em);

				if (convertedUnit != null) {
					inventoryItemDefault.setD86Threshold(convertedUnit);
				}

			}

			BigDecimal inventoruItemDefaultValueFromDb = inventoryItemDefaultFromDb.getD86Threshold();
			BigDecimal inventoryItemDefaultValue = inventoryItemDefault.getD86Threshold();

			if (inventoruItemDefaultValueFromDb == null) {
				inventoruItemDefaultValueFromDb = new BigDecimal(0);
			}

			if (inventoryItemDefaultValue == null) {
				inventoryItemDefaultValue = new BigDecimal(0);
			}

			/*
			 * if (inventoryItemDefaultFromDb != null && inventoryItemDefault !=
			 * null && inventoruItemDefaultValueFromDb.doubleValue() !=
			 * inventoryItemDefaultValue.doubleValue()) { shouldSendPush = true;
			 * }
			 */

			inventoryItemDefaultFromDb.setD86Threshold(inventoryItemDefault.getD86Threshold());
			inventoryFromDatabase.setUpdatedBy(inventory.getUpdatedBy());
			inventoryFromDatabase.setDisplaySequence(inventory.getDisplaySequence());

			inventoryItemDefaultFromDb.setEconomicOrderQuantity(inventoryItemDefault.getEconomicOrderQuantity());
			inventoryItemDefaultFromDb.setMinimumOrderQuantity(inventoryItemDefault.getMinimumOrderQuantity());
			inventoryFromDatabase.setPrimarySupplierId(inventory.getPrimarySupplierId());
			inventoryFromDatabase.setSecondarySupplierId(inventory.getSecondarySupplierId());
			// inventoryFromDatabase.setPhysicaInventory(inventory.getPhysicaInventory());
			inventoryFromDatabase.setTertiarySupplierId(inventory.getTertiarySupplierId());
			inventoryFromDatabase
					.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(inventoryPacket.getLocationId(), em));
			inventoryFromDatabase.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			PhysicalInventory physicalInventory = inventoryPacket.getPhysicalInventory();
			BigDecimal previousTotalAvailable = inventoryFromDatabase.getTotalAvailableQuanity();

			if (physicalInventory
					.getQuantity() != null /*
											 * && (!physicalInventory
											 * .getQuantity ().equals(new
											 * BigDecimal(0)))
											 */) {
				// if ((physicalInventory.getQuantity().doubleValue()>0))
				// {
				// conversion
				if (inventoryPacket.getIsAdmin() == 1) {
					inventoryFromDatabase.setTotalAvailableQuanity(physicalInventory.getQuantity());
				} else {
					Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
							inventoryFromDatabase.getItemId());
					BigDecimal convertedUnit = convertUnit(item.getStockUom(), item.getSellableUom(), logger,
							physicalInventory.getQuantity(), em);
					if (convertedUnit != null) {
						inventoryFromDatabase.setTotalAvailableQuanity(convertedUnit);
					}

				}

				// }
			}
			EntityTransaction tx = em.getTransaction();
			try {
				// start transaction
				tx.begin();
				em.merge(inventoryFromDatabase);
				tx.commit();
				new InsertIntoHistory().insertInventoryIntoHistory(httpRequest, inventoryFromDatabase, em);

			} catch (RuntimeException e) {
				// on error, if transaction active,
				// rollback
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}

			// save this relation also
			tx = em.getTransaction();
			try {
				// start transaction
				tx.begin();
				em.merge(inventoryItemDefaultFromDb);
				tx.commit();
			} catch (RuntimeException e) {
				// on error, if transaction active,
				// rollback
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}

			// Added by Apoorva for adding physical inventory in update service
			// :- 2016-01-05
			// 31182: Need physical inventory in update inventory
			InventoryServiceBean bean = new InventoryServiceBean();
		    String physicalInventoryId=	bean.addPhysicalInventory(httpRequest, em, inventoryPacket.getPhysicalInventory(), previousTotalAvailable).getId();

			// if (shouldSendPush)
			{
				// we need to send push to client that threshold has been
				// changed

				inventoryManagementHelper = new InventoryManagementHelper();
				if (inventoryFromDatabase.getTotalAvailableQuanity() != null) {
					double totalAvialQtyInDouble = inventoryFromDatabase.getTotalAvailableQuanity().doubleValue();

					if (totalAvialQtyInDouble < inventoryItemDefaultFromDb.getD86Threshold().doubleValue()) {
						boolean shouldMerge = false;
						if (inventoryFromDatabase.getIsBelowThreashold() == 0) {
							shouldMerge = true;
							inventoryFromDatabase.setIsBelowThreashold(1);
						}

						if (shouldMerge) {
							tx = em.getTransaction();
							try {
								// start transaction
								tx.begin();
								em.merge(inventoryFromDatabase);
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

					} else {
						boolean shouldMerge = false;
						if (inventoryFromDatabase.getIsBelowThreashold() == 1) {
							shouldMerge = true;
							inventoryFromDatabase.setIsBelowThreashold(0);
						}

						if (shouldMerge) {
							em.getTransaction().begin();
							em.merge(inventoryFromDatabase);
							em.getTransaction().commit();
						}
					}
				}

				InventoryPostPacket inventoryPostPacket = inventoryManagementHelper.getPushPacket(inventoryFromDatabase,
						null, inventoryItemDefaultFromDb.getD86Threshold());
				inventoryPostPacket.setMerchantId(inventoryPacket.getMerchantId());
				inventoryPostPacket.setLocationId(inventoryPacket.getLocationId());
				inventoryPostPacket.setClientId(inventoryPacket.getClientId());
				inventoryPostPacket.setEchoString(inventoryPacket.getEchoString());

				InventoryPushPacket inventoryPushPacket = new InventoryPushPacket();
				inventoryPushPacket.setInventoryPostPacket(inventoryPostPacket);
				inventoryPushPacket.setMerchantId(inventoryPacket.getMerchantId());
				inventoryPushPacket.setLocationId(inventoryPacket.getLocationId());
				inventoryPushPacket.setClientId(inventoryPacket.getClientId());
				inventoryPushPacket.setEchoString(inventoryPacket.getEchoString());

				inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryPostPacket,
						POSNServiceOperations.InventoryManagementService_inventoryManagement.toString());

			}

			Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
					inventoryFromDatabase.getItemId());
			BigDecimal convertedUnit = convertUnit(item.getSellableUom(), item.getStockUom(), logger,
					inventoryFromDatabase.getTotalUsedQuanity(), em);
			if (convertedUnit != null)
				inventoryFromDatabase.setTotalUsedQuanity(convertedUnit);
			inventoryFromDatabase.setTotalAvailableQuanity(physicalInventory.getQuantity());
			Location baseLocation = new CommonMethods().getBaseLocation(em);
			inventoryPacket.getInventory().setItemId(inventoryFromDatabase.getItemId());
			inventoryPacket.getInventoryItemDefault().setId(inventoryItemDefaultFromDb.getId());
			inventoryPacket.getPhysicalInventory().setId(physicalInventoryId);
			logger.severe("physicalInventoryId========================================================================"+physicalInventoryId);
			String json = new StoreForwardUtility().returnJsonPacket(inventoryPacket, "InventoryPacket", httpRequest);
			
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, baseLocation.getId(),
					Integer.parseInt(inventoryPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(inventoryFromDatabase);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/updateInventoryWithReasons")
	public String updateInventoryWithReasons(InventoryPacket inventoryPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// boolean shouldSendPush = false;
			String json = new StoreForwardUtility().returnJsonPacket(inventoryPacket, "InventoryPacket", httpRequest);

			Inventory inventory = inventoryPacket.getInventory();
			Inventory inventoryFromDatabase = (Inventory) new CommonMethods().getObjectById("Inventory", em,
					Inventory.class, inventory.getId());

			if (inventoryFromDatabase == null) {
				POSExceptionMessage posExceptionMessage = new POSExceptionMessage(
						"Inventory does not exists for update", 1);
				return new JSONUtility(httpRequest).convertToJsonString(posExceptionMessage);
			}

			InventoryItemDefault inventoryItemDefault = null;
			if (inventoryPacket.getInventoryItemDefault() != null) {
				inventoryItemDefault = inventoryPacket.getInventoryItemDefault();
			}

			InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();
			InventoryItemDefault inventoryItemDefaultFromDb = inventoryManagementHelper
					.getInventoryItemDefault(inventoryFromDatabase.getItemId(), em);

			if (inventoryPacket.getIsAdmin() == 1) {

			} else {

				Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
						inventoryFromDatabase.getItemId());
				BigDecimal convertedUnit = convertUnit(item.getStockUom(), item.getSellableUom(), logger,
						inventoryItemDefault.getD86Threshold(), em);

				if (convertedUnit != null) {
					inventoryItemDefault.setD86Threshold(convertedUnit);
				}

			}

			BigDecimal inventoruItemDefaultValueFromDb = inventoryItemDefaultFromDb.getD86Threshold();
			BigDecimal inventoryItemDefaultValue = inventoryItemDefault.getD86Threshold();

			if (inventoruItemDefaultValueFromDb == null) {
				inventoruItemDefaultValueFromDb = new BigDecimal(0);
			}

			if (inventoryItemDefaultValue == null) {
				inventoryItemDefaultValue = new BigDecimal(0);
			}

			/*
			 * if (inventoryItemDefaultFromDb != null && inventoryItemDefault !=
			 * null && inventoruItemDefaultValueFromDb.doubleValue() !=
			 * inventoryItemDefaultValue.doubleValue()) { shouldSendPush = true;
			 * }
			 */

			inventoryItemDefaultFromDb.setD86Threshold(inventoryItemDefault.getD86Threshold());
			inventoryFromDatabase.setUpdatedBy(inventory.getUpdatedBy());
			inventoryFromDatabase.setDisplaySequence(inventory.getDisplaySequence());

			inventoryItemDefaultFromDb.setEconomicOrderQuantity(inventoryItemDefault.getEconomicOrderQuantity());
			inventoryItemDefaultFromDb.setMinimumOrderQuantity(inventoryItemDefault.getMinimumOrderQuantity());
			inventoryFromDatabase.setPrimarySupplierId(inventory.getPrimarySupplierId());
			inventoryFromDatabase.setSecondarySupplierId(inventory.getSecondarySupplierId());
			// inventoryFromDatabase.setPhysicaInventory(inventory.getPhysicaInventory());
			inventoryFromDatabase.setTertiarySupplierId(inventory.getTertiarySupplierId());
			inventoryFromDatabase
					.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(inventoryPacket.getLocationId(), em));
			inventoryFromDatabase.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			PhysicalInventory physicalInventory = inventoryPacket.getPhysicalInventory();
			BigDecimal previousTotalAvailable = inventoryFromDatabase.getTotalAvailableQuanity();

			if (physicalInventory
					.getQuantity() != null /*
											 * && (!physicalInventory
											 * .getQuantity ().equals(new
											 * BigDecimal(0)))
											 */) {
				// if ((physicalInventory.getQuantity().doubleValue()>0))
				// {
				// conversion
				if (inventoryPacket.getIsAdmin() == 1) {
					inventoryFromDatabase.setTotalAvailableQuanity(
							inventoryFromDatabase.getTotalAvailableQuanity().subtract(physicalInventory.getQuantity()));
				} else {
					Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
							inventoryFromDatabase.getItemId());
					BigDecimal convertedUnit = convertUnit(item.getStockUom(), item.getSellableUom(), logger,
							physicalInventory.getQuantity(), em);
					if (convertedUnit != null) {
						inventoryFromDatabase.setTotalAvailableQuanity(
								inventoryFromDatabase.getTotalAvailableQuanity().subtract(convertedUnit));
					}

				}

				// }
			}
			EntityTransaction tx = em.getTransaction();
			try {
				// start transaction
				tx.begin();
				em.merge(inventoryFromDatabase);
				tx.commit();
				new InsertIntoHistory().insertInventoryIntoHistory(httpRequest, inventoryFromDatabase, em);

			} catch (RuntimeException e) {
				// on error, if transaction active,
				// rollback
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}

			// save this relation also
			tx = em.getTransaction();
			try {
				// start transaction
				tx.begin();
				em.merge(inventoryItemDefaultFromDb);
				tx.commit();
			} catch (RuntimeException e) {
				// on error, if transaction active,
				// rollback
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}

			// Added by Apoorva for adding physical inventory in update service
			// :- 2016-01-05
			// 31182: Need physical inventory in update inventory
			InventoryServiceBean bean = new InventoryServiceBean();
			bean.addPhysicalInventory(httpRequest, em, inventoryPacket.getPhysicalInventory(), previousTotalAvailable);

			// if (shouldSendPush)
			{
				// we need to send push to client that threshold has been
				// changed

				inventoryManagementHelper = new InventoryManagementHelper();
				if (inventoryFromDatabase.getTotalAvailableQuanity() != null) {
					double totalAvialQtyInDouble = inventoryFromDatabase.getTotalAvailableQuanity().doubleValue();

					if (totalAvialQtyInDouble < inventoryItemDefaultFromDb.getD86Threshold().doubleValue()) {
						boolean shouldMerge = false;
						if (inventoryFromDatabase.getIsBelowThreashold() == 0) {
							shouldMerge = true;
							inventoryFromDatabase.setIsBelowThreashold(1);
						}

						if (shouldMerge) {
							tx = em.getTransaction();
							try {
								// start transaction
								tx.begin();
								em.merge(inventoryFromDatabase);
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

					} else {
						boolean shouldMerge = false;
						if (inventoryFromDatabase.getIsBelowThreashold() == 1) {
							shouldMerge = true;
							inventoryFromDatabase.setIsBelowThreashold(0);
						}

						if (shouldMerge) {
							em.getTransaction().begin();
							em.merge(inventoryFromDatabase);
							em.getTransaction().commit();
						}
					}
				}

				InventoryPostPacket inventoryPostPacket = inventoryManagementHelper.getPushPacket(inventoryFromDatabase,
						null, inventoryItemDefaultFromDb.getD86Threshold());
				inventoryPostPacket.setMerchantId(inventoryPacket.getMerchantId());
				inventoryPostPacket.setLocationId(inventoryPacket.getLocationId());
				inventoryPostPacket.setClientId(inventoryPacket.getClientId());
				inventoryPostPacket.setEchoString(inventoryPacket.getEchoString());

				InventoryPushPacket inventoryPushPacket = new InventoryPushPacket();
				inventoryPushPacket.setInventoryPostPacket(inventoryPostPacket);
				inventoryPushPacket.setMerchantId(inventoryPacket.getMerchantId());
				inventoryPushPacket.setLocationId(inventoryPacket.getLocationId());
				inventoryPushPacket.setClientId(inventoryPacket.getClientId());
				inventoryPushPacket.setEchoString(inventoryPacket.getEchoString());

				inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryPostPacket,
						POSNServiceOperations.InventoryManagementService_inventoryManagement.toString());

			}

			Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
					inventoryFromDatabase.getItemId());
			BigDecimal convertedUnit = convertUnit(item.getSellableUom(), item.getStockUom(), logger,
					inventoryFromDatabase.getTotalUsedQuanity(), em);
			if (convertedUnit != null)
				inventoryFromDatabase.setTotalUsedQuanity(convertedUnit);
			inventoryFromDatabase.setTotalAvailableQuanity(physicalInventory.getQuantity());
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, inventoryPacket.getLocationId(),
					Integer.parseInt(inventoryPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(inventoryFromDatabase);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * { "UOMPostPacket": { "merchantId": 105, "locationId": 1,
	 * "unitOfMeasurement": { "id": 0, "updatedBy": 1385, "createdBy": 1385,
	 * "displayName": "Bottle", "name": "Bottle", "uomTypeId": 1, "locationId":
	 * 1, "status": "A" } } }
	 * 
	 * @param uomPostPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */

	@POST
	@Path("/addUnitOfMeasurement")
	public String addUnitOfMeasurement(UOMPostPacket uomPostPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String json = new StoreForwardUtility().returnJsonPacket(uomPostPacket, "UOMPostPacket", httpRequest);

			UnitOfMeasurement unitOfMeasurement = new InventoryServiceBean().addUnitOfMeasurement(em,
					uomPostPacket.getUnitOfMeasurement());
			uomPostPacket.setUnitOfMeasurement(unitOfMeasurement);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, uomPostPacket.getLocationId(),
					Integer.parseInt(uomPostPacket.getMerchantId()));
			sendPacketForBroadcast(POSNServiceOperations.InventoryManagementService_addUnitOfMeasurement.name(),
					uomPostPacket);
			return new JSONUtility(httpRequest).convertToJsonString(unitOfMeasurement);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * { "InventoryToBomPacket": { "inventoryItemToBOMs": [ { "status": "A",
	 * "created": 1248, "itemIdFg": 1, "itemIdRm": 2, "updatedBy": 2,
	 * "createdBy": 1, "rmSellableUom": 1 }, { "id": 2, "status": "A",
	 * "created": 1248, "itemIdFg": 1, "itemIdRm": 2, "updatedBy": 2,
	 * 
	 * @param inventoryAttributeToBomPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/updateAttributeToBOM")
	public String updateAttributeToBOM(InventoryAttributeToBomPacket inventoryAttributeToBomPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String json = new StoreForwardUtility().returnJsonPacket(inventoryAttributeToBomPacket,
					"InventoryAttributeToBomPacket", httpRequest);
			if (inventoryAttributeToBomPacket != null
					&& inventoryAttributeToBomPacket.getInventoryAttributeBOMList() != null
					&& inventoryAttributeToBomPacket.getInventoryAttributeBOMList().size() > 0) {

				for (InventoryAttributeBOM inventoryItemToBOM : inventoryAttributeToBomPacket
						.getInventoryAttributeBOMList()) {

					if (inventoryItemToBOM != null) {
						EntityTransaction tx = em.getTransaction();
						try {
							em.getTransaction().begin();
							inventoryItemToBOM.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							if (inventoryItemToBOM.getId() == 0) {
								em.persist(inventoryItemToBOM);
								inventoryItemToBOM.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
								em.merge(inventoryItemToBOM);
							} else {
								em.merge(inventoryItemToBOM);
							}

							em.getTransaction().commit();
						} catch (Exception e) {
							// on error, if transaction active,
							// rollback
							if (tx != null && tx.isActive()) {
								tx.rollback();
							}
							throw e;
						}

					}
				}
			}
			InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();
			logger.severe("inventoryAttributeToBomPacket.getLocationId()============================================================================="+inventoryAttributeToBomPacket.getLocationId());
			
			logger.severe("json============================================================================="+json);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					inventoryAttributeToBomPacket.getLocationId(),
					Integer.parseInt(inventoryAttributeToBomPacket.getMerchantId()));
			inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryAttributeToBomPacket,
					POSNServiceOperations.InventoryManagementService_updateInventoryAttributeToBOMManagement
							.toString());
			return new JSONUtility(httpRequest).convertToJsonString(inventoryAttributeToBomPacket);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * { { "InventoryAttributeToBomPacket": { "inventoryAttributeBOMList": [ {
	 * "id": 3, "updatedBy": 2 }, { "id": 2, "updatedBy": 2 } ] } }
	 * 
	 * @param inventoryAttributeToBomPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/deleteAttributeToBOM")
	public String deleteAttributeToBOM(InventoryAttributeToBomPacket inventoryAttributeToBomPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String json = new StoreForwardUtility().returnJsonPacket(inventoryAttributeToBomPacket,
					"InventoryAttributeToBomPacket", httpRequest);

			if (inventoryAttributeToBomPacket != null
					&& inventoryAttributeToBomPacket.getInventoryAttributeBOMList() != null
					&& inventoryAttributeToBomPacket.getInventoryAttributeBOMList().size() > 0) {

				for (InventoryAttributeBOM inventoryAttributeToBOM : inventoryAttributeToBomPacket
						.getInventoryAttributeBOMList()) {

					if (inventoryAttributeToBOM != null) {

						InventoryAttributeBOM inventoryAttributeToBOMInDb = em.find(InventoryAttributeBOM.class,
								inventoryAttributeToBOM.getId());

						if (inventoryAttributeToBOMInDb != null) {
							EntityTransaction tx = em.getTransaction();
							try {
								em.getTransaction().begin();
								inventoryAttributeToBOMInDb.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
								inventoryAttributeToBOMInDb.setUpdatedBy(inventoryAttributeToBOM.getUpdatedBy());
								inventoryAttributeToBOMInDb.setStatus("D");
								em.merge(inventoryAttributeToBOMInDb);
								// getting base location
								Location baseLocation = new CommonMethods().getBaseLocation(em);

								List<String> locationsId = new CommonMethods().getAllActiveLocations(httpRequest, em);
								for (String locationId : locationsId) {
									if (!locationId.equals(baseLocation.getId())) {

										ItemsAttribute attributeIdFg = null;
										Item itemIdRm = null;
										try {

											String queryString = "select s from ItemsAttribute s where s.globalId =? and s.locationsId=? ";
											TypedQuery<ItemsAttribute> query = em
													.createQuery(queryString, ItemsAttribute.class)
													.setParameter(1, inventoryAttributeToBOM.getAttributeIdFg())
													.setParameter(2, locationId);
											attributeIdFg = query.getSingleResult();

										} catch (Exception e) {

											logger.severe("No ItemsAttribute Found "
													+ inventoryAttributeToBOM.getAttributeIdFg());
										}

										try {

											String queryString = "select s from Item s where s.globalItemId =? and s.locationsId=? and s.status!='D'  ";
											TypedQuery<Item> query = em.createQuery(queryString, Item.class)
													.setParameter(1, inventoryAttributeToBOM.getItemIdRm())
													.setParameter(2, locationId);
											itemIdRm = query.getSingleResult();

										} catch (Exception e) {

											logger.severe("No Items Found " + inventoryAttributeToBOM.getItemIdRm());
										}

										if (attributeIdFg != null && itemIdRm != null) {
											InventoryAttributeBOM inventoryItemToBOMInDbLocal = null;

											try {
												String queryString = "select s from InventoryAttributeBOM s where s.attributeIdFg =? and s.itemIdRm=?  ";
												TypedQuery<InventoryAttributeBOM> query = em
														.createQuery(queryString, InventoryAttributeBOM.class)
														.setParameter(1, attributeIdFg.getId())
														.setParameter(2, itemIdRm.getId());
												inventoryItemToBOMInDbLocal = query.getSingleResult();
											} catch (Exception e) {

												logger.severe("No InventoryAttributeBOM Found " + attributeIdFg.getId()
														+ " " + itemIdRm.getId());
											}

											if (inventoryItemToBOMInDbLocal != null) {

												inventoryItemToBOMInDbLocal
														.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
												inventoryItemToBOMInDbLocal
														.setUpdatedBy(inventoryAttributeToBOM.getUpdatedBy());
												inventoryItemToBOMInDbLocal.setStatus("D");
												em.merge(inventoryItemToBOMInDbLocal);

											}
										}

									}

								}

								em.getTransaction().commit();
							} catch (Exception e) {
								// on error, if transaction active,
								// rollback
								if (tx != null && tx.isActive()) {
									tx.rollback();
								}
								throw e;
							}
						}

					}
				}
			}
			InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					inventoryAttributeToBomPacket.getLocationId(),
					Integer.parseInt(inventoryAttributeToBomPacket.getMerchantId()));
			inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryAttributeToBomPacket,
					POSNServiceOperations.InventoryManagementService_deleteInventoryAttributeToBOM.toString());
			return new JSONUtility(httpRequest).convertToJsonString(inventoryAttributeToBomPacket);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * { "InventoryToBomPacket": { "inventoryItemToBOMs": [ { "status": "A",
	 * "created": 1248, "itemIdFg": 1, "itemIdRm": 2, "updatedBy": 2,
	 * "createdBy": 1, "rmSellableUom": 1 }, { "id": 2, "status": "A",
	 * "created": 1248, "itemIdFg": 1, "itemIdRm": 2, "updatedBy": 2,
	 * "createdBy": 1, "rmSellableUom": 1 } ] } }
	 * 
	 * @param inventoryToBomPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/updateItemToBOM")
	public String updateItemToBOM(InventoryToBomPacket inventoryToBomPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			
			if (inventoryToBomPacket != null && inventoryToBomPacket.getInventoryItemToBOMs() != null
					&& inventoryToBomPacket.getInventoryItemToBOMs().size() > 0) {

				for (InventoryItemBom inventoryItemToBOM : inventoryToBomPacket.getInventoryItemToBOMs()) {

					if (inventoryItemToBOM != null) {
						EntityTransaction tx = em.getTransaction();
						try {
							em.getTransaction().begin();
							inventoryItemToBOM.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							if (inventoryItemToBOM.getId() == 0) {
								em.persist(inventoryItemToBOM);
								inventoryItemToBOM.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							} else {
								em.merge(inventoryItemToBOM);
							}

							em.getTransaction().commit();
						} catch (Exception e) {
							// on error, if transaction active,
							// rollback
							if (tx != null && tx.isActive()) {
								tx.rollback();
							}
							throw e;
						}

					}
				}
			}
			InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();
			String json = new StoreForwardUtility().returnJsonPacket(inventoryToBomPacket, "InventoryToBomPacket",
					httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					inventoryToBomPacket.getLocationId(), Integer.parseInt(inventoryToBomPacket.getMerchantId()));
			inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryToBomPacket,
					POSNServiceOperations.InventoryManagementService_updateInventoryItemToBOMManagement.toString());
			return new JSONUtility(httpRequest).convertToJsonString(inventoryToBomPacket);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * { "InventoryToBomPacket": { "inventoryItemToBOMs": [ { "id": 3,
	 * "updatedBy": 2 }, { "id": 2, "updatedBy": 2 } ] } }
	 * 
	 * @param inventoryToBomPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/deleteItemToBOM")
	public String deleteItemToBOM(InventoryToBomPacket inventoryToBomPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String json = new StoreForwardUtility().returnJsonPacket(inventoryToBomPacket, "InventoryToBomPacket",
					httpRequest);
			if (inventoryToBomPacket != null && inventoryToBomPacket.getInventoryItemToBOMs() != null
					&& inventoryToBomPacket.getInventoryItemToBOMs().size() > 0) {

				for (InventoryItemBom inventoryItemToBOM : inventoryToBomPacket.getInventoryItemToBOMs()) {

					if (inventoryItemToBOM != null) {

						InventoryItemBom inventoryItemToBOMInDb = em.find(InventoryItemBom.class,
								inventoryItemToBOM.getId());

						if (inventoryItemToBOMInDb != null) {
							EntityTransaction tx = em.getTransaction();
							try {
								em.getTransaction().begin();
								inventoryItemToBOMInDb.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
								inventoryItemToBOMInDb.setUpdatedBy(inventoryItemToBOM.getUpdatedBy());
								inventoryItemToBOMInDb.setStatus("D");

								// getting base location
								Location baseLocation = new CommonMethods().getBaseLocation(em);

								List<String> locationsId = new CommonMethods().getAllActiveLocations(httpRequest, em);
								for (String locationId : locationsId) {
									if (!locationId.equals(baseLocation.getId())) {

										Item itemIdFg = null;
										Item itemIdRm = null;
										try {

											String queryString = "select s from Item s where s.globalItemId =? and s.locationsId=? and s.status!='D' ";
											TypedQuery<Item> query = em.createQuery(queryString, Item.class)
													.setParameter(1, inventoryItemToBOMInDb.getItemIdFg())
													.setParameter(2, locationId);
											itemIdFg = query.getSingleResult();

										} catch (Exception e) {

											logger.severe(e);
										}

										try {

											String queryString = "select s from Item s where s.globalItemId =? and s.locationsId=? and s.status!='D'  ";
											TypedQuery<Item> query = em.createQuery(queryString, Item.class)
													.setParameter(1, inventoryItemToBOMInDb.getItemIdRm())
													.setParameter(2, locationId);
											itemIdRm = query.getSingleResult();

										} catch (Exception e) {

											logger.severe(e);
										}

										if (itemIdFg != null && itemIdRm != null) {
											InventoryItemBom inventoryItemToBOMInDbLocal = null;

											try {
												String queryString = "select s from InventoryItemBom s where s.itemIdFg =? and s.itemIdRm=?  ";
												TypedQuery<InventoryItemBom> query = em
														.createQuery(queryString, InventoryItemBom.class)
														.setParameter(1, itemIdFg.getId())
														.setParameter(2, itemIdRm.getId());
												inventoryItemToBOMInDbLocal = query.getSingleResult();
											} catch (Exception e) {

												logger.severe(e);
											}

											if (inventoryItemToBOMInDbLocal != null) {
												// em.getTransaction().begin();
												inventoryItemToBOMInDbLocal
														.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
												inventoryItemToBOMInDbLocal
														.setUpdatedBy(inventoryItemToBOM.getUpdatedBy());
												inventoryItemToBOMInDbLocal.setStatus("D");

											}
										}

									}

								}

								em.getTransaction().commit();
							} catch (Exception e) {
								// on error, if transaction active,
								// rollback
								if (tx != null && tx.isActive()) {
									tx.rollback();
								}
								throw e;
							}
						}

					}
				}
			}
			InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					inventoryToBomPacket.getLocationId(), Integer.parseInt(inventoryToBomPacket.getMerchantId()));
			inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryToBomPacket,
					POSNServiceOperations.InventoryManagementService_deleteInventoryItemToBOM.toString());
			return new JSONUtility(httpRequest).convertToJsonString(inventoryToBomPacket);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/deleteMultipleLocationsItemToBOM")
	public String deleteMultipleLocationsItemToBOM(InventoryToBomPacket inventoryToBomPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String json = new StoreForwardUtility().returnJsonPacket(inventoryToBomPacket, "InventoryToBomPacket",
					httpRequest);
			if (inventoryToBomPacket != null && inventoryToBomPacket.getInventoryItemToBOMs() != null
					&& inventoryToBomPacket.getInventoryItemToBOMs().size() > 0) {

				for (InventoryItemBom inventoryItemToBOM : inventoryToBomPacket.getInventoryItemToBOMs()) {

					if (inventoryItemToBOM != null) {

						InventoryItemBom inventoryItemToBOMInDb = em.find(InventoryItemBom.class,
								inventoryItemToBOM.getId());

						if (inventoryItemToBOMInDb != null) {
							EntityTransaction tx = em.getTransaction();
							try {
								em.getTransaction().begin();
								inventoryItemToBOMInDb.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
								inventoryItemToBOMInDb.setUpdatedBy(inventoryItemToBOM.getUpdatedBy());
								inventoryItemToBOMInDb.setStatus("D");
								em.getTransaction().commit();
							} catch (Exception e) {
								// on error, if transaction active,
								// rollback
								if (tx != null && tx.isActive()) {
									tx.rollback();
								}
								throw e;
							}
						}

						/*
						 * List<Integer> locationsId = new
						 * CommonMethods().getAllActiveLocations(httpRequest,
						 * em); for (Integer locationId : locationsId) {
						 * 
						 * InventoryItemBom inventoryItemToBOMInDbLocal = null;
						 * 
						 * try { String queryString =
						 * "select s from InventoryItemBom s where s.itemIdFg =? and s.itemIdRm=?  "
						 * ; TypedQuery<InventoryItemBom> query =
						 * em.createQuery(queryString,
						 * InventoryItemBom.class).setParameter(1,
						 * attributeIdFg).setParameter(2, itemIdRm);
						 * inventoryItemToBOMInDbLocal =
						 * query.getSingleResult(); } catch (Exception e) {
						 * 
						 * 
						 * logger.severe(e); }
						 * 
						 * if (inventoryItemToBOMInDbLocal != null) {
						 * em.getTransaction().begin();
						 * inventoryItemToBOMInDbLocal.setUpdated(new
						 * Date(new TimezoneTime().getGMTTimeInMilis()));
						 * inventoryItemToBOMInDbLocal.setUpdatedBy(
						 * inventoryItemToBOM.getUpdatedBy());
						 * inventoryItemToBOMInDbLocal.setStatus("D");
						 * em.getTransaction().commit();
						 * 
						 * }
						 * 
						 * }
						 */
					}
				}
			}
			InventoryManagementHelper inventoryManagementHelper = new InventoryManagementHelper();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					inventoryToBomPacket.getLocationId(), Integer.parseInt(inventoryToBomPacket.getMerchantId()));

			inventoryManagementHelper.sendPacketForBroadcast(httpRequest, inventoryToBomPacket,
					POSNServiceOperations.InventoryManagementService_deleteInventoryItemToBOM.toString());
			return new JSONUtility(httpRequest).convertToJsonString(inventoryToBomPacket);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 * { "UOMPostPacket": { "merchantId": 105, "locationId": 1,
	 * "unitOfMeasurement": { "id": 0, "updatedBy": 1385, "createdBy": 1385,
	 * "displayName": "Glass", "name": "Glass", "uomTypeId": 2, "locationId": 1,
	 * "status": "A", "stockUomId": 3, "stockQty": 1, "sellableQty": 4 } } }
	 * 
	 * @param uomPostPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/updateUnitOfMeasurement")
	public String updateUnitOfMeasurement(UOMPostPacket uomPostPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String json = new StoreForwardUtility().returnJsonPacket(uomPostPacket, "UOMPostPacket", httpRequest);
			tx = em.getTransaction();
			tx.begin();
			UnitOfMeasurement unitOfMeasurement = new InventoryServiceBean().addUnitOfMeasurement(em, httpRequest,
					uomPostPacket.getUnitOfMeasurement());
			tx.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, uomPostPacket.getLocationId(),
					Integer.parseInt(uomPostPacket.getMerchantId()));
			sendPacketForBroadcast(POSNServiceOperations.InventoryManagementService_updateUnitOfMeasurement.name(),
					uomPostPacket);
			return new JSONUtility(httpRequest).convertToJsonString(unitOfMeasurement);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 * 
	 * { "PhysicalInventoryPacket": { "merchantId": 105, "locationId": 1,
	 * "physicalInventory": {"updatedBy": 1, "inventoryId":12, "quantity":200.5,
	 * "date":"2015-07-09","createdBy": 1, "status":"A" } } }
	 * 
	 * @param physicalInventoryPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/addPhysicalInventory")
	public String addPhysicalInventory(PhysicalInventoryPacket physicalInventoryPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId,
					physicalInventoryPacket);
			PhysicalInventory physicalInventory = new InventoryServiceBean().addPhysicalInventory(httpRequest, em,
					physicalInventoryPacket.getPhysicalInventory(), new BigDecimal(0));
			physicalInventoryPacket.setPhysicalInventory(physicalInventory);
			String json = new StoreForwardUtility().returnJsonPacket(physicalInventoryPacket, "PhysicalInventoryPacket",
					httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					physicalInventoryPacket.getLocationId(), Integer.parseInt(physicalInventoryPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(physicalInventory);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 * 
	 * { "PhysicalInventoryPacket": { "merchantId": 105, "locationId": 1,
	 * "physicalInventory": {"id":34, "updatedBy": 1, "inventoryId":12,
	 * "quantity":200.5, "date":"2015-07-09","createdBy": 1, "status":"A" ,
	 * "created":"1323232344423" } } }
	 * 
	 * @param physicalInventoryPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */

	@POST
	@Path("/updatePhysicalInventory")
	public String updatePhysicalInventory(PhysicalInventoryPacket physicalInventoryPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId,
					physicalInventoryPacket);
			String json = new StoreForwardUtility().returnJsonPacket(physicalInventoryPacket, "PhysicalInventoryPacket",
					httpRequest);
			PhysicalInventory physicalInventory = new InventoryServiceBean().updatePhysicalInventory(httpRequest, em,
					physicalInventoryPacket.getPhysicalInventory());
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					physicalInventoryPacket.getLocationId(), Integer.parseInt(physicalInventoryPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(physicalInventory);
		} finally {

			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * { "PhysicalInventoryPacket": { "merchantId": 105, "locationId": 1,
	 * "physicalInventory": { "id": 34,"status":"D" } } }
	 * 
	 * @param physicalInventoryPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/deletePhysicalInventory")
	public String deletePhysicalInventory(PhysicalInventoryPacket physicalInventoryPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId,
					physicalInventoryPacket);
			String json = new StoreForwardUtility().returnJsonPacket(physicalInventoryPacket, "PhysicalInventoryPacket",
					httpRequest);

			PhysicalInventory physicalInventory = new InventoryServiceBean().deletePhysicalInventory(httpRequest, em,
					physicalInventoryPacket.getPhysicalInventory());
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					physicalInventoryPacket.getLocationId(), Integer.parseInt(physicalInventoryPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(physicalInventory);
		} finally {

			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 * @param sessionId
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getPhysicalInventoryByDate/{date}/{locationId}")
	public String getPhysicalInventoryByDate(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("date") String date, @PathParam("locationId") String locationId) throws Exception {

		EntityManager em = null;
		try {

			List<PhysicalInventory> physicalInventory = new ArrayList<PhysicalInventory>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call p_get_physical_inventory_by_date_location_id(?,?)")
					.setParameter(1, date).setParameter(2, locationId).getResultList();
			for (Object[] objRow : resultList) {
				int i = 0;
				if ((String) objRow[i] != null) {
					PhysicalInventory pInventory = new PhysicalInventory();
					pInventory.setId((String) objRow[i++]);
					pInventory.setItemDisplayName((String) objRow[i++]);
					pInventory.setInventoryId((String) objRow[i++]);
					pInventory.setQuantity((BigDecimal) objRow[i++]);
					pInventory.setLocationId((String) objRow[i++]);
					pInventory.setStatus((Character) objRow[i++] + "");
					pInventory.setLocalTime((String) objRow[i++] + "");
					physicalInventory.add(pInventory);
				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(physicalInventory);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	private void sendPacketForBroadcast(String operation, PostPacket postPacket) throws JMSException {

		MessageSender messageSender = new MessageSender();

		operation = ServiceOperationsUtility.getOperationName(operation);
		messageSender.sendMessage(httpRequest, postPacket.getClientId(), POSNServices.InventoryManagementService.name(),
				operation, null, postPacket.getMerchantId(), postPacket.getLocationId(), postPacket.getEchoString(),
				postPacket.getSessionId());

	}

	@GET
	@Path("/isAlive")
	public boolean isAlive() {
		return true;
	}

	@POST
	@Path("/deleteUnitOfMeasurement")
	public String deleteUnitOfMeasurement(UOMPostPacket uomPostPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			tx = em.getTransaction();
			tx.begin();
			UnitOfMeasurement unitOfMeasurement = new InventoryServiceBean()
					.deleteUnitOfMeasurement(uomPostPacket.getUnitOfMeasurement(), httpRequest, em);
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(uomPostPacket, "UOMPostPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, uomPostPacket.getLocationId(),
					Integer.parseInt(uomPostPacket.getMerchantId()));
			sendPacketForBroadcast(POSNServiceOperations.InventoryManagementService_deleteUnitOfMeasurement.name(),
					uomPostPacket);
			return new JSONUtility(httpRequest).convertToJsonString(unitOfMeasurement);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/addInventoryOrderReceipt")
	public String addInventoryOrderReceipt(InventoryOrderReceiptPacket inventoryOrderReceiptPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId,
					inventoryOrderReceiptPacket);
			String locationId = inventoryOrderReceiptPacket.getLocationId().trim();
			inventoryOrderReceiptPacket.getInventoryOrderReceipt().setLocationId(locationId);
			InventoryOrderReceipt inventoryOrderReceipt = new InventoryServiceBean().addInventoryOrderReceipt(
					httpRequest, em, inventoryOrderReceiptPacket.getInventoryOrderReceipt(),
					inventoryOrderReceiptPacket);
			String json = new StoreForwardUtility().returnJsonPacket(inventoryOrderReceiptPacket,
					"UOMPostPInventoryOrderReceiptPacketacket", httpRequest);
			inventoryOrderReceiptPacket.setInventoryOrderReceipt(inventoryOrderReceipt);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					inventoryOrderReceiptPacket.getLocationId(),
					Integer.parseInt(inventoryOrderReceiptPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(inventoryOrderReceipt);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/deleteInventoryOrderReceipt")
	public String deleteInventoryOrderReceipt(InventoryOrderReceiptPacket inventoryOrderReceiptPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId,
					inventoryOrderReceiptPacket);

			InventoryOrderReceipt inventoryOrderReceipt = new InventoryServiceBean().deleteInventoryOrderReceipt(
					httpRequest, em, inventoryOrderReceiptPacket.getInventoryOrderReceipt(),
					inventoryOrderReceiptPacket);
			String json = new StoreForwardUtility().returnJsonPacket(inventoryOrderReceiptPacket,
					"UOMPostPInventoryOrderReceiptPacketacket", httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					inventoryOrderReceiptPacket.getLocationId(),
					Integer.parseInt(inventoryOrderReceiptPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(inventoryOrderReceipt);
		} finally {

			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/updateInventoryOrderReceipt")
	public String updateInventoryOrderReceipt(InventoryOrderReceiptPacket inventoryOrderReceiptPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId,
					inventoryOrderReceiptPacket);

			InventoryOrderReceipt inventoryOrderReceipt = new InventoryServiceBean().updateInventoryOrderReceipt(
					httpRequest, em, inventoryOrderReceiptPacket.getInventoryOrderReceipt(),
					inventoryOrderReceiptPacket);
			String json = new StoreForwardUtility().returnJsonPacket(inventoryOrderReceiptPacket,
					"UOMPostPInventoryOrderReceiptPacketacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					inventoryOrderReceiptPacket.getLocationId(),
					Integer.parseInt(inventoryOrderReceiptPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(inventoryOrderReceipt);
		} finally {

			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/addInventoryOrderReceiptWithItems")
	public String addInventoryOrderReceiptWithItems(InventoryOrderReceiptPacket inventoryOrderReceiptPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId,
					inventoryOrderReceiptPacket);

			String locationId = inventoryOrderReceiptPacket.getLocationId().trim();
			inventoryOrderReceiptPacket.getInventoryOrderReceipt().setLocationId(locationId);

			InventoryOrderReceipt inventoryOrderReceipt = new InventoryServiceBean()

					.addInventoryOrderReceiptWithItems(httpRequest, em,
							inventoryOrderReceiptPacket.getInventoryOrderReceipt(),
							inventoryOrderReceiptPacket.getInventoryOrderReceiptForItemsList(),
							inventoryOrderReceiptPacket);
			String json = new StoreForwardUtility().returnJsonPacket(inventoryOrderReceiptPacket,
					"UOMPostPInventoryOrderReceiptPacketacket", httpRequest);
			inventoryOrderReceiptPacket.setInventoryOrderReceipt(inventoryOrderReceipt);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					inventoryOrderReceiptPacket.getLocationId(),
					Integer.parseInt(inventoryOrderReceiptPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(inventoryOrderReceipt);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/updateInventoryOrderReceiptWithItem")
	public String updateInventoryOrderReceiptWithItem(InventoryOrderReceiptPacket inventoryOrderReceiptPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId,
					inventoryOrderReceiptPacket);

			InventoryOrderReceipt inventoryOrderReceipt = new InventoryServiceBean()
					.updateInventoryOrderReceiptWithItem(httpRequest, em,
							inventoryOrderReceiptPacket.getInventoryOrderReceipt(),
							inventoryOrderReceiptPacket.getInventoryOrderReceiptForItemsList(),
							inventoryOrderReceiptPacket);

			String json = new StoreForwardUtility().returnJsonPacket(inventoryOrderReceiptPacket,
					"UOMPostPInventoryOrderReceiptPacketacket", httpRequest);
			inventoryOrderReceiptPacket.setInventoryOrderReceipt(inventoryOrderReceipt);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					inventoryOrderReceiptPacket.getLocationId(),
					Integer.parseInt(inventoryOrderReceiptPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(inventoryOrderReceipt);
		} finally {

			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getInventoryOrderReceiptWithItem/{id}")
	public String getInventoryOrderReceiptWithItem(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("id") int id) throws Exception {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			InventoryOrderReceipt inventoryOrderReceipt = em.find(InventoryOrderReceipt.class, id);

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<InventoryOrderReceiptForItem> criteria = builder
					.createQuery(InventoryOrderReceiptForItem.class);
			Root<InventoryOrderReceiptForItem> r = criteria.from(InventoryOrderReceiptForItem.class);
			TypedQuery<InventoryOrderReceiptForItem> query = em.createQuery(criteria.select(r)
					.where(builder.equal(r.get(InventoryOrderReceiptForItem_.inventoryOrderReceiptId),
							inventoryOrderReceipt.getId()),
							builder.notEqual(r.get(InventoryOrderReceiptForItem_.status), "D"))
					.orderBy(builder.asc(r.get(InventoryOrderReceiptForItem_.id))));
			List<InventoryOrderReceiptForItem> inventoryOrderReceiptForItemsList = query.getResultList();
			List<InventoryOrderReceiptForItem> newInventoryOrderReceiptForItemsList = new ArrayList<InventoryOrderReceiptForItem>();
			for (InventoryOrderReceiptForItem inventoryOrderReceiptForItem : inventoryOrderReceiptForItemsList) {
				inventoryOrderReceiptForItem
						.setCategoryId(getCategoryItem(inventoryOrderReceiptForItem.getItemId(), em).getCategoryId());
				newInventoryOrderReceiptForItemsList.add(0, inventoryOrderReceiptForItem);
			}
			InventoryOrderReceiptPacket inventoryOrderReceiptPacket = new InventoryOrderReceiptPacket();
			inventoryOrderReceiptPacket.setInventoryOrderReceipt(inventoryOrderReceipt);
			inventoryOrderReceiptPacket.setInventoryOrderReceiptForItemsList(newInventoryOrderReceiptForItemsList);

			return new JSONUtility(httpRequest).convertToJsonString(inventoryOrderReceiptPacket);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	public CategoryItem getCategoryItem(String itemId, EntityManager em) {
		if (itemId != null && em != null) {

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CategoryItem> criteria = builder.createQuery(CategoryItem.class);
			Root<CategoryItem> r = criteria.from(CategoryItem.class);
			TypedQuery<CategoryItem> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryItem_.itemsId), itemId)));
			return query.getSingleResult();

		}
		return null;

	}

	@POST
	@Path("/addMultipleLocationUnitOfMeasurement")
	public String addMultipleLocationUnitOfMeasurement(UOMPostPacket uomPostPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, uomPostPacket);
			tx = em.getTransaction();
			tx.begin();
			UnitOfMeasurement result = new InventoryServiceBean().addMultipleLocationUnitOfMeasurement(httpRequest,em,
					uomPostPacket.getUnitOfMeasurement(), uomPostPacket, httpRequest);
			tx.commit();
			uomPostPacket.setUnitOfMeasurement(result);
			// call synchPacket for store forward
			if(uomPostPacket.getLocationsListId()!=null && uomPostPacket.getLocationsListId().length()>0){
			String[] locationsId = uomPostPacket.getLocationsListId().split(",");
			for (String locationId : locationsId) {
				uomPostPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.InventoryManagementService_addUnitOfMeasurement.name(),
						uomPostPacket);
			}
			}
			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/updateMultipleLocationUnitOfMeasurement")
	public String updateMultipleLocationUnitOfMeasurement(UOMPostPacket uomPostPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, uomPostPacket);
			tx = em.getTransaction();
			tx.begin();
			UnitOfMeasurement result = new InventoryServiceBean().updateMultipleLocationUnitOfMeasurement(httpRequest,em,
					uomPostPacket.getUnitOfMeasurement(), uomPostPacket, httpRequest);
			tx.commit();
//			String json = new StoreForwardUtility().returnJsonPacket(uomPostPacket, "UOMPostPacket", httpRequest);
//			// call synchPacket for store forward
//			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, uomPostPacket.getLocationId(),
//					Integer.parseInt(uomPostPacket.getMerchantId()));
			String[] locationsId = uomPostPacket.getLocationsListId().split(",");
			for (String locationId : locationsId) {
				
				if (locationId != null && !locationId.equals("0") && locationId.length() > 0) {
					uomPostPacket.setLocationId(locationId);
				} else {
					uomPostPacket.setLocationId(uomPostPacket.getUnitOfMeasurement().getLocationId() + "");
				}
				sendPacketForBroadcast(POSNServiceOperations.InventoryManagementService_updateUnitOfMeasurement.name(),
						uomPostPacket);
			}
			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/deleteMultipleLocationUnitOfMeasurement")
	public String deleteMultipleLocationUnitOfMeasurement(UOMPostPacket uomPostPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, uomPostPacket);
			tx = em.getTransaction();
			tx.begin();
			UnitOfMeasurement result = new InventoryServiceBean().deleteMultipleLocationUnitOfMeasurement(em,
					uomPostPacket.getUnitOfMeasurement(), uomPostPacket, httpRequest);
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(uomPostPacket, "UOMPostPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, uomPostPacket.getLocationId(),
					Integer.parseInt(uomPostPacket.getMerchantId()));
			List<String> locationsId = new CommonMethods().getAllActiveLocations(httpRequest, em);
			for (String locationId : locationsId) {
				uomPostPacket.setLocationId(locationId.toString());
				sendPacketForBroadcast(POSNServiceOperations.InventoryManagementService_deleteUnitOfMeasurement.name(),
						uomPostPacket);
			}

			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/deleteMultipleLocationUnitConversion")
	public String deleteMultipleLocationUnitConversion(UnitConversionPostPacket unitConversionPostPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId,
					unitConversionPostPacket);
			tx = em.getTransaction();
			tx.begin();
			UnitConversion result = new InventoryServiceBean().deleteMultipleLocationUnitConversion(em,
					unitConversionPostPacket.getUnitConversion(), unitConversionPostPacket, httpRequest);
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(unitConversionPostPacket,
					"UnitConversionPostPacket", httpRequest);
			unitConversionPostPacket.setUnitConversion(result);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					unitConversionPostPacket.getLocationId(),
					Integer.parseInt(unitConversionPostPacket.getMerchantId()));
			List<String> locationsId = new CommonMethods().getAllActiveLocations(httpRequest, em);
			for (String locationId : locationsId) {
				unitConversionPostPacket.setLocationId(locationId.toString());
				sendPacketForBroadcast(POSNServiceOperations.InventoryManagementService_deleteUnitConvesion.name(),
						unitConversionPostPacket);
			}

			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getUnitOfMeasurementCountByLocationId/{locationId}")
	public BigInteger getUnitOfMeasurementCountByLocationId(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select count(*) from unit_of_measurement where location_id=? and status not in ('D')";

			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, locationId)
					.getSingleResult();

			return resultList;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getUnitOfMeasurementByLocationId/{locationId}/{startIndex}/{endIndex}")
	public String getUnitOfMeasurementByLocationId(@PathParam("locationId") String locationId,
			@PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			List<UnitOfMeasurementDisplayPacket> ans = new ArrayList<UnitOfMeasurementDisplayPacket>();

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select id,name,display_name,status from unit_of_measurement uom where uom.status !='D' and uom.location_id = ? limit "
					+ startIndex + "," + endIndex + "";
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationId).getResultList();
			for (Object[] objRow : resultList) {
				UnitOfMeasurementDisplayPacket detailDisplayPacket = new UnitOfMeasurementDisplayPacket();
				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setDisplayName((String) objRow[2]);
				detailDisplayPacket.setStatus(((char) objRow[3]) + "");

				ans.add(detailDisplayPacket);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getUnitOfMeasurementById/{id}")
	public String getUnitOfMeasurementById(@PathParam("id") String id,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UnitOfMeasurement> criteria = builder.createQuery(UnitOfMeasurement.class);
			Root<UnitOfMeasurement> uom = criteria.from(UnitOfMeasurement.class);
			TypedQuery<UnitOfMeasurement> query = em
					.createQuery(criteria.select(uom).where(builder.equal(uom.get(UnitOfMeasurement_.id), id)));
			UnitOfMeasurement p = query.getSingleResult();

			String queryString = "select l from Location l where l.id in (select p.locationId from UnitOfMeasurement p where p.globalId=?  and p.status not in ('D')) ";
			TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, p.getId());
			List<Location> resultSet = query2.getResultList();
			p.setLocationList(resultSet);

			String queryStringUC = "select l from UnitConversion l where l.fromUOMId =? and l.status!='D'";
			TypedQuery<UnitConversion> query3 = em.createQuery(queryStringUC, UnitConversion.class).setParameter(1,
					p.getId());
			List<UnitConversion> resultSetUC;
			try {
				resultSetUC = query3.getResultList();

				if (resultSetUC != null && resultSetUC.size() > 0) {
					for (UnitConversion conversion : resultSetUC) {
						if (conversion.getFromUOMId() != null) {
							CriteriaQuery<UnitOfMeasurement> criteriaUom = builder.createQuery(UnitOfMeasurement.class);
							Root<UnitOfMeasurement> uom1 = criteriaUom.from(UnitOfMeasurement.class);
							TypedQuery<UnitOfMeasurement> query1 = em.createQuery(criteriaUom.select(uom1)
									.where(builder.equal(uom1.get(UnitOfMeasurement_.id), conversion.getFromUOMId())));
							conversion.setFromUOMIdName(query1.getSingleResult().getDisplayName());

						}

						if (conversion.getToUOMId() != null) {
							CriteriaQuery<UnitOfMeasurement> criteriaUom = builder.createQuery(UnitOfMeasurement.class);
							Root<UnitOfMeasurement> uom1 = criteriaUom.from(UnitOfMeasurement.class);
							TypedQuery<UnitOfMeasurement> query1 = em.createQuery(criteriaUom.select(uom1)
									.where(builder.equal(uom1.get(UnitOfMeasurement_.id), conversion.getToUOMId())));
							conversion.setToUOMIdName(query1.getSingleResult().getDisplayName());

						}

					}
				}

				p.setUnitConversionList(resultSetUC);
			} catch (Exception e) {

				logger.severe(e);

			}

			return new JSONUtility(httpRequest).convertToJsonString(p);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/updateMultipleLocationInventoryItemToBOM")
	public String updateMultipleLocationInventoryToBom(InventoryToBomPacket packet,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, packet);
			tx = em.getTransaction();
			tx.begin();
			List<InventoryItemBom> result = new ArrayList<InventoryItemBom>();
			for (InventoryItemBom inventoryItemBom : packet.getInventoryItemToBOMs()) {
				InventoryItemBom bom = new InventoryServiceBean().updateMultipleLocationInventoryToBom(em,
						inventoryItemBom, packet, httpRequest);
				result.add(bom);
			}
			tx.commit();
			
			packet.setInventoryItemToBOMs(result);
			String[] locationsId = packet.getLocationsListId().split(",");
			for (String locationId : locationsId) {
				packet.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.LookupService_updateCourse.name(), packet);
			}

			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/updateMultipleLocationInventoryAttributeToBom")
	public String updateMultipleLocationInventoryAttributeToBom(InventoryAttributeToBomPacket packet,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, packet);
			tx = em.getTransaction();
			tx.begin();

			List<InventoryAttributeBOM> result = new ArrayList<InventoryAttributeBOM>();
			if (packet.getInventoryAttributeBOMList() != null) {
				for (InventoryAttributeBOM inventoryItemBom : packet.getInventoryAttributeBOMList()) {
					InventoryAttributeBOM bom = new InventoryServiceBean()
							.updateMultipleLocationInventoryAttributeBOM(em, inventoryItemBom, packet, httpRequest);
					result.add(bom);
				}
					tx.commit();
				packet.setInventoryAttributeBOMList(result);
				String[] locationsId = packet.getLocationsListId().split(",");
				for (String locationId : locationsId) {
					packet.setLocationId(locationId);
					sendPacketForBroadcast(POSNServiceOperations.LookupService_updateCourse.name(), packet);
					
				}
			}
			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/getItemsIngredientDisplayByLocationId")
	public String getItemsIngredientDisplayByLocationId(ItemByLocationIdPacket itemByLocationIdPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			String name = itemByLocationIdPacket.getItemName();
			if (name == null || name.equals("null") || name.equals(null)) {
				name = "";
			}
			name = name.trim();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return (new InventoryServiceBean().getItemsIngredientDisplayByLocationId(name, itemByLocationIdPacket, em,
					httpRequest));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/getItemsIngredientDisplayCountByLocationId")
	public BigInteger getItemsIngredientDisplayCountByLocationId(ItemByLocationIdPacket itemByLocationIdPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String name = itemByLocationIdPacket.getItemName();
			if (name == null || name.equals("null") || name.equals(null)) {
				name = "";
			}
			String temp = Utilities.convertAllSpecialCharForSearch(name);

			List<ItemsIngredientDisplay> ans = new ArrayList<ItemsIngredientDisplay>();

			/*
			 * String sql =
			 * "select  i.id,i.image_name,i.display_name as item_display_name,c.display_name as c_display_name,uom.display_name as uom_display_name"
			 * + " from   items i  " +
			 * " left join category_items ci on (ci.items_id = i.id  and ci.status !='D' ) "
			 * + " left join category c on c.id=ci.category_id  " +
			 * " left join unit_of_measurement uom on uom.id=i.stock_uom  "
			 * +" left join items_type it on it.id = i.item_type  " +
			 * " where c.display_name !='Raw Material' " +
			 * " and  i.display_name like '%" + temp +
			 * "%' and it.name != 'Inventory Only'";
			 */

			String sql = "select  i.id,i.image_name,i.display_name as item_display_name,c.display_name as c_display_name,uom.display_name as uom_display_name"
					+ " from   items i  "
					+ " left join category_items ci on (ci.items_id = i.id  and ci.status !='D' ) "
					+ " left join category c on c.id=ci.category_id  "
					+ " left join unit_of_measurement uom on uom.id=i.stock_uom  "
					+ " left join items_type it on it.id = i.item_type  " + " where i.display_name like '%" + temp
					+ "%' and it.name != 'Inventory Only'";

			if (itemByLocationIdPacket.getCategoryId() != null && itemByLocationIdPacket.getCategoryId().length() > 0) {
				sql += " and c.id in (" + itemByLocationIdPacket.getCategoryId() + ")";
			}
			sql += "  and i.locations_id=? and i.status !='D'";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql)
					.setParameter(1, itemByLocationIdPacket.getLocationId()).getResultList();
			for (Object[] objRow : resultList) {
				ItemsIngredientDisplay detailPacket = new ItemsIngredientDisplay();
				detailPacket.setId((String) objRow[0]);
				detailPacket.setImageName((String) objRow[1]);
				detailPacket.setItemDisplayName((String) objRow[2]);
				detailPacket.setCategorydisplayName((String) objRow[3]);
				detailPacket.setStockUOMDisplayName((String) objRow[4]);

				ans.add(detailPacket);
			}

			return BigInteger.valueOf(ans.size());
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAttributeToBOMCountByLocationIdAndAttributeType/{locationId}/{attributeTtypeId}/{attributeName}")
	public BigInteger getAttributeToBOMCountByLocationId(@PathParam("locationId") String locationId,
			@PathParam("attributeTtypeId") String attributeTtypeId, @PathParam("attributeName") String attributeName,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;

		if (attributeName == null || attributeName.equals("null") || attributeName.equals(null)) {
			attributeName = "";
		}
		String temp = Utilities.convertAllSpecialCharForSearch(attributeName);

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "  SELECT  iab.id, iat.display_name as attribute_type, ia.display_name as attribute, "
					+ "i.display_name as item,c.display_name as category,u.name as uom,iab.quantity, "
					+ " iab.status, ia.id as attribute_id   FROM items_attribute ia   "
					+ " left join `inventory_attribute_bom` iab on ia.id= iab.attribute_id_fg and iab.status!='D' "
					+ " left join `items` i on iab.item_id_rm= i.id "
					+ " left join `items_attribute_type_to_items_attribute` iaTiat on ia.id= iaTiat.items_attribute_id "
					+ " join `items_attribute_type` iat on iat.id= iaTiat.items_attribute_type_id "
					+ " left join `category_items` ci on ci.items_id= i.id  "
					+ " left join category c on c.id=ci.category_id "
					+ " left join unit_of_measurement u on u.id = iab.rm_sellable_uom "
					+ "  where ia.display_name like  '%" + temp + "%' and ia.locations_id=? ";
			if (attributeTtypeId !=null && !attributeTtypeId.equals("0")) {
				sql += " and  iat.id =  '" + attributeTtypeId+"'";
			}
			sql += " and iat.is_required=0 and ia.status!='D' ";

			logger.severe("sql============================================================================"+sql);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
			return BigInteger.valueOf(resultList.size());
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAttributeToBOMByLocationIdAndAttributeTypeAndAttributeName/{locationId}/{attributeTtypeId}/{attributeName}/{startIndex}/{endIndex}")
	public String getAttributeToBOMByLocationId(@PathParam("locationId") String locationId,
			@PathParam("attributeTtypeId") String attributeTtypeId, @PathParam("startIndex") int startIndex,
			@PathParam("endIndex") int endIndex, @PathParam("attributeName") String attributeName,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			List<AttributeToBOMDisplayPacket> ans = new ArrayList<AttributeToBOMDisplayPacket>();

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			if (attributeName == null || attributeName.equals("null") || attributeName.equals(null)) {
				attributeName = "";
			}
			String temp = Utilities.convertAllSpecialCharForSearch(attributeName);

			String sql = "  SELECT  iab.id, iat.display_name as attribute_type, ia.display_name as attribute, "
					+ "i.display_name as item,c.display_name as category,u.name as uom,iab.quantity, "
					+ " iab.status, ia.id as attribute_id , iab.item_id_rm, iab.attribute_id_fg "
					+ " FROM items_attribute ia   "
					+ " left join `inventory_attribute_bom` iab on ia.id= iab.attribute_id_fg and iab.status!='D' "
					+ " left join `items` i on iab.item_id_rm= i.id "
					+ " left join `items_attribute_type_to_items_attribute` iaTiat on ia.id= iaTiat.items_attribute_id "
					+ " join `items_attribute_type` iat on iat.id= iaTiat.items_attribute_type_id "
					+ " left join `category_items` ci on ci.items_id= i.id  "
					+ " left join category c on c.id=ci.category_id "
					+ " left join unit_of_measurement u on u.id = iab.rm_sellable_uom "
					+ "  where ia.display_name like  '%" + temp + "%' and ia.locations_id=? ";
			if (attributeTtypeId !=null && !attributeTtypeId.equals("0")) {
				sql += " and  iat.id =  '" + attributeTtypeId+"'";
			}
			sql += " and iat.is_required=0 and ia.status!='D' limit " + startIndex + "," + endIndex;
			logger.severe("locationId==========================22222222222222=================================================="+locationId);
			
			logger.severe("sql==========================22222222222222=================================================="+sql);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
			for (Object[] objRow : resultList) {
				AttributeToBOMDisplayPacket detailDisplayPacket = new AttributeToBOMDisplayPacket();
				if (objRow[0] != null)
					detailDisplayPacket.setId((int) objRow[0]);
				detailDisplayPacket.setAttributeType((String) objRow[1]);
				detailDisplayPacket.setAttributeName((String) objRow[2]);
				if (objRow[3] != null)
					detailDisplayPacket.setItemName((String) objRow[3]);
				if (objRow[4] != null)
					detailDisplayPacket.setCategoryName((String) objRow[4]);
				if (objRow[5] != null)
					detailDisplayPacket.setUomName((String) objRow[5]);
				if (objRow[6] != null)
					detailDisplayPacket.setQuantity((BigDecimal) objRow[6]);
				if (objRow[7] != null)
					detailDisplayPacket.setStatus(((char) objRow[7]) + "");

				detailDisplayPacket.setAttributeId((String) objRow[8]);
				if (objRow[9] != null) {
					detailDisplayPacket.setItemIdRm((String) objRow[9]);
				}

				if (objRow[10] != null) {
					detailDisplayPacket.setAttributeIdFg((String) objRow[10]);
				}

				ans.add(detailDisplayPacket);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAttributeToBOMById/{id}")
	public String getAttributeToBOMById(@PathParam("id") int id,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UnitOfMeasurement> criteria = builder.createQuery(UnitOfMeasurement.class);
			Root<UnitOfMeasurement> uom = criteria.from(UnitOfMeasurement.class);
			TypedQuery<UnitOfMeasurement> query = em
					.createQuery(criteria.select(uom).where(builder.equal(uom.get(UnitOfMeasurement_.id), id)));
			UnitOfMeasurement p = query.getSingleResult();
			String queryString = "select l from Location l where l.id in (select p.locationId from UnitOfMeasurement p where p.globalId=?  and p.status not in ('D')) ";
			TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, p.getId());
			List<Location> resultSet = query2.getResultList();
			p.setLocationList(resultSet);

			return new JSONUtility(httpRequest).convertToJsonString(p);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/addUpdateRequestOrder")
	public String addUpdateRequestOrder(RequestOrderPacket requestOrderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, requestOrderPacket);
			// em =
			// LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest,
			// null);
			tx = em.getTransaction();
			em.getTransaction().begin();
			RequestOrder requestOrder = new InventoryServiceBean().addRequestOrderWithItems(httpRequest, em,
					requestOrderPacket.getRequestOrder(), requestOrderPacket);
			em.getTransaction().commit();
			
			OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,
					OrderStatus.class, requestOrder.getStatusId());
			if (orderStatus != null && orderStatus.getName().equals("PO Created")) {
				try {
					sendEmailForAddUpdateRequestOrder(requestOrder.getId(), requestOrder.getLocationId(), sessionId);
				} catch (Exception e) {
					logger.severe(e);
				}

			}
			
			return new JSONUtility(httpRequest).convertToJsonString(requestOrder);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/addUpdateBulkRequestOrder")
	public String addUpdateBulkRequestOrder(RequestOrderPacket requestOrderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, requestOrderPacket);
			// em =
			// LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest,
			// null);
			tx = em.getTransaction();

			ArrayList<RequestOrder> requestOrders = new ArrayList<RequestOrder>();
			for (int i = 0; i < requestOrderPacket.getRequestOrders().size(); i++) {

				em.getTransaction().begin();

				RequestOrder requestOrder = new InventoryServiceBean().addRequestOrderWithItems(httpRequest, em,
						requestOrderPacket.getRequestOrders().get(i), requestOrderPacket);
				OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,
						OrderStatus.class, requestOrder.getStatusId());
				if (orderStatus != null && orderStatus.getName().equals("PO Created")) {
					try {
						sendEmailForAddUpdateRequestOrder(requestOrder.getId(), requestOrder.getLocationId(),
								sessionId);
					} catch (Exception e) {
						logger.severe(e);
					}

				}
				requestOrders.add(requestOrder);
				em.getTransaction().commit();
			}

			requestOrderPacket.setRequestOrders(requestOrders);
			String json = new StoreForwardUtility().returnJsonPacket(requestOrderPacket, "RequestOrderPacket",
					httpRequest);

			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, requestOrderPacket.getLocationId(),
					Integer.parseInt(requestOrderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(requestOrders);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/deleteRequestOrder")
	public String deleteRequestOrder(RequestOrderPacket requestOrderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, requestOrderPacket);
			tx = em.getTransaction();
			em.getTransaction().begin();
			RequestOrder requestOrder = new InventoryServiceBean().deleteRequestOrder(httpRequest, em,
					requestOrderPacket.getRequestOrder());
			em.getTransaction().commit();

			String json = new StoreForwardUtility().returnJsonPacket(requestOrderPacket, "RequestOrderPacket",
					httpRequest);

			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, requestOrderPacket.getLocationId(),
					Integer.parseInt(requestOrderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(requestOrder);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAttributeToBOMByIdWithDisplayPacket/{id}")
	public String getAttributeToBOMByIdWithDisplayPacket(@PathParam("id") int id,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			List<AttributeToBOMDisplayPacket> ans = new ArrayList<AttributeToBOMDisplayPacket>();

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select iab.id,iab.attribute_id_fg, iab.item_id_rm, iab.rm_sellable_uom, ci.category_id, iab.status"
					+ ",iab.quantity"
					+ "  from inventory_attribute_bom iab left join category_items ci on ci.items_id=iab.item_id_rm  "
					+ " where iab.id=? ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, id).getResultList();
			for (Object[] objRow : resultList) {
				AttributeToBOMDisplayPacket detailDisplayPacket = new AttributeToBOMDisplayPacket();
				detailDisplayPacket.setId((int) objRow[0]);
				detailDisplayPacket.setAttributeId((String) objRow[1]);
				detailDisplayPacket.setItemId((String) objRow[2]);
				detailDisplayPacket.setUomId((String) objRow[3]);
				detailDisplayPacket.setCategoryId((String) objRow[4]);
				detailDisplayPacket.setStatus(((char) objRow[5]) + "");
				detailDisplayPacket.setQuantity(((BigDecimal) objRow[6]));
				List<Location> LocationList = new InventoryServiceBean()
						.getLocationListForInventoryAttributeBOM(httpRequest, em, detailDisplayPacket);
				detailDisplayPacket.setLocationList(LocationList);
				ans.add(detailDisplayPacket);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getPOById/{id}")
	public String getPOById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException, InvalidSessionException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			RequestOrder requestOrder = new InventoryServiceBean().getRequestOrderById(em, id, false);
			return new JSONUtility(httpRequest).convertToJsonString(requestOrder);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllotmentById/{id}")
	public String getAllotmentById(@PathParam("id") String id,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			RequestOrder requestOrder = new InventoryServiceBean().getRequestOrderById(em, id, true);
			return new JSONUtility(httpRequest).convertToJsonString(requestOrder);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/updateQtyAndStatus")
	public String updateQtyAndStatus(RequestOrderDetailItemsPacket requestOrderDetailItemsPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			tx = em.getTransaction();
			em.getTransaction().begin();
			RequestOrderDetailItems updatedRequestOrderDetailItem = new InventoryServiceBean().updateQtyAndStatus(em,
					requestOrderDetailItemsPacket.getRequestOrderDetailItems());
			em.getTransaction().commit();
			return new JSONUtility(httpRequest).convertToJsonString(updatedRequestOrderDetailItem);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllPendingRequestOrderByLocationId/{locationId}")
	public String getAllPendingRequestOrder(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<RequestOrder> requestOrderList = new InventoryServiceBean().getAllPendingRequestOrder(em, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(requestOrderList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllPendingRequestOrderByLocationIdAllInOne/{locationId}/{date}")
	public String getAllPendingRequestOrderByLocationIdAllInOne(@PathParam("locationId") String locationId,
			@PathParam("date") String date, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException, InvalidSessionException {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<RequestOrder> requestOrderList = new InventoryServiceBean().getAllPendingRequestOrderAllInOne(em,
					locationId, date);
			return new JSONUtility(httpRequest).convertToJsonString(requestOrderList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllInProcessRequestOrderByLocationId/{locationId}")
	public String getAllInProcessRequestOrderByLocationId(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<RequestOrder> requestOrderList = new InventoryServiceBean().getAllInProcessRequestOrderByLocationId(em,
					locationId);
			return new JSONUtility(httpRequest).convertToJsonString(requestOrderList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/sendEmailForAddUpdateRequestOrder/{requestOrderId}/{locationId}")
	public String sendEmailForAddUpdateRequestOrder(@PathParam(value = "requestOrderId") String requestOrderId,
			@PathParam(value = "locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// em =
			// LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest,
			// null);

			// insert into order history
			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			RequestOrder requestOrderheader = (RequestOrder) new CommonMethods().getObjectById("RequestOrder", em,
					RequestOrder.class, requestOrderId);

			Location supplier = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					requestOrderheader.getSupplierId());
			// 0 because mail sending from dine in

			try {
				tx = em.getTransaction();
				tx.begin();

				Location foundLocation = null;
				if (locationId != null && !locationId.equals("0")) {
					String queryString = "select l from Location l where " + "l.id ='" + locationId
							+ "' and (l.locationsId = '0' or l.locationsId is null) and l.locationsTypeId = '1'";
					TypedQuery<Location> query = em.createQuery(queryString, Location.class);
					foundLocation = query.getSingleResult();
				}

				String fileName = foundLocation.getName() + " Purchase Order.pdf";

				String pdfData = receiptPDFFormat
						.createRequestOrderInvoicePDFString(em, httpRequest, requestOrderId, locationId, 2, "")
						.toString();

				String emailBody = receiptPDFFormat.createRequestOrderInvoiceBodyString().toString();

				String emailFooter = receiptPDFFormat.createRequestOrderInvoiceFooterString(em, locationId).toString();

				pdfData = pdfData.replace("</body>", emailFooter + "</body>");

				EmailTemplateKeys.sendRequestOrderConfirmationEmailToUser(httpRequest, em, locationId,
						requestOrderheader.getUpdatedBy(), pdfData, EmailTemplateKeys.REQUEST_ORDER_CONFIRMATION,
						requestOrderheader.getId(), fileName, emailBody, emailFooter, supplier, 2, "");

				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			return new JSONUtility(httpRequest).convertToJsonString(true);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/sendEmailForAddAlloment/{requestOrderId}/{locationId}/{grmNo}")
	public String sendEmailForAddAlloment(@PathParam(value = "requestOrderId") String requestOrderId,
			@PathParam(value = "locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam(value = "grmNo") String grmNo)
			throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			// insert into order history
			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			RequestOrder requestOrderheader = (RequestOrder) new CommonMethods().getObjectById("RequestOrder", em,
					RequestOrder.class, requestOrderId);

			Location supplier = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					requestOrderheader.getSupplierId());
			// 0 because mail sending from dine in

			try {
				tx = em.getTransaction();
				tx.begin();

				Location foundLocation = null;
				if (locationId != null && !locationId.equals("0")) {
					String queryString = "select l from Location l where " + "l.id ='" + locationId
							+ "' and (l.locationsId = '0' or l.locationsId is null) and l.locationsTypeId = '1'";
					TypedQuery<Location> query = em.createQuery(queryString, Location.class);
					foundLocation = query.getSingleResult();
				}

				String fileName = foundLocation.getName() + " Purchase Order.pdf";

				String pdfData = receiptPDFFormat
						.createRequestOrderInvoicePDFString(em, httpRequest, requestOrderId, locationId, 1, grmNo)
						.toString();
				String emailBody = receiptPDFFormat.createRequestOrderInvoiceBodyString().toString();

				String emailFooter = receiptPDFFormat.createRequestOrderInvoiceFooterString(em, locationId).toString();

				EmailTemplateKeys.sendRequestOrderConfirmationEmailToUser(httpRequest, em, locationId,
						requestOrderheader.getUpdatedBy(), pdfData, EmailTemplateKeys.REQUEST_ORDER_CONFIRMATION,
						requestOrderheader.getId(), fileName, emailBody, emailFooter, supplier, 1, grmNo);

				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			return new JSONUtility(httpRequest).convertToJsonString(true);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/sendEmailForPOCancelled/{requestOrderId}/{locationId}")
	public String sendEmailForPOCancelled(@PathParam(value = "requestOrderId") String requestOrderId,
			@PathParam(value = "locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			// insert into order history
			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			RequestOrder requestOrderheader = (RequestOrder) new CommonMethods().getObjectById("RequestOrder", em,
					RequestOrder.class, requestOrderId);

			Location supplier = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					requestOrderheader.getSupplierId());
			// 0 because mail sending from dine in

			try {
				tx = em.getTransaction();
				tx.begin();

				Location foundLocation = null;
				if (locationId != null && !locationId.equals("0")) {
					String queryString = "select l from Location l where " + "l.id ='" + locationId
							+ "' and (l.locationsId = '0' or l.locationsId is null) and l.locationsTypeId = '1'";
					TypedQuery<Location> query = em.createQuery(queryString, Location.class);
					foundLocation = query.getSingleResult();
				}

				String fileName = foundLocation.getName() + " Purchase Order(Cancelled).pdf";

				String pdfData = receiptPDFFormat
						.createRequestOrderInvoicePDFString(em, httpRequest, requestOrderId, locationId, 3, "")
						.toString();

				String emailBody = receiptPDFFormat.createRequestOrderInvoiceBodyString().toString();
				String emailFooter = receiptPDFFormat.createRequestOrderInvoiceFooterString(em, locationId).toString();

				EmailTemplateKeys.sendRequestOrderConfirmationEmailToUser(httpRequest, em, locationId,
						requestOrderheader.getUpdatedBy(), pdfData, EmailTemplateKeys.REQUEST_ORDER_CONFIRMATION,
						requestOrderheader.getId(), fileName, emailBody, emailFooter, supplier, 3, "");

				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			return new JSONUtility(httpRequest).convertToJsonString(true);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	public User createNewUserBeforeSendingEmail(EntityManager em, String emailAddress, String createdBy,
			String updatedBy, String locationId) throws Exception {
		EntityManager globalEM = null;
		User user = null;
		try {
			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
			GlobalUserUtil globalUserUtil = new GlobalUserUtil();
			com.nirvanaxp.global.types.entities.User user1 = globalUserUtil.getUserByEmail(globalEM, emailAddress);
			if (user1 != null) {
				user = new User();
				user.setAuthPin(user1.getAuthPin());
				user.setComments("");
				user.setDateofbirth(user1.getDateofbirth());
				user.setEmail(emailAddress);
				user.setFirstName(user1.getFirstName());
				user.setLastName(user1.getLastName());
				user.setPassword(user1.getPassword());
				user.setStatus("A");
				user.setUserColor("");
				user.setUsername(user1.getUsername());
				user.setVisitCount(0);
				user.setGlobalUsersId(user1.getId());
				user.setCreatedBy(createdBy);
				user.setUpdatedBy(updatedBy);
			} else {
				user = new User();
				user.setAuthPin("");
				user.setComments("");
				user.setDateofbirth("");
				user.setEmail(emailAddress);
				user.setFirstName("");
				user.setLastName("");
				user.setPassword("");
				user.setStatus("A");
				user.setUserColor("");
				user.setUsername(emailAddress);
				user.setVisitCount(0);
				user.setCreatedBy(createdBy);
				user.setUpdatedBy(updatedBy);
			}

			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
			user = globalUsermanagement.addUserToLocalDatabaseProgramatically(httpRequest, globalEM, em, user,
					locationId, null);
		} finally {
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}

		return user;
	}

	@GET
	@Path("/getAllPendingRequestOrderByDateAndLocationId/{date}/{locationId}")
	public String getAllPendingRequestOrderByDateAndLocationId(
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("date") String date,
			@PathParam("locationId") String locationId) throws IOException, InvalidSessionException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<RequestOrder> requestOrderList = new InventoryServiceBean().getAllPendingRequestOrderByDate(em,
					locationId, date);
			return new JSONUtility(httpRequest).convertToJsonString(requestOrderList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getOrderDetailStatusForInventory/{locationId}")
	public String getOrderDetailStatusForInventory(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("locationId") String locationId) throws IOException, InvalidSessionException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<OrderDetailStatus> orderDetailStatusList = new InventoryServiceBean()
					.getOrderDetailStatusForInventory(em, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(orderDetailStatusList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/getAllReceiveOrder")
	public String getAllReceiveOrder(RequestOrderGetPacket requestOrderGetPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		String supplierId = requestOrderGetPacket.getSupplierId();
		String poNumber = requestOrderGetPacket.getPoNumber();
		String businessId = requestOrderGetPacket.getBusinessId();
		try
		{
			List<RequestOrderDisplay> ans = new ArrayList<RequestOrderDisplay>();
			if (supplierId == null || supplierId.equals("null") || supplierId.equals(null))
			{
				supplierId = "";
			}
			if (poNumber == null || poNumber.equals("null") || poNumber.equals(null))
			{
				poNumber = "";
			}

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = " select ro.id,ro.location_id,ro.created, ro.grn_count,ro.supplier_id  from request_order ro  " + " left join order_status os on ro.status_id=os.id "
					+ "  where ro.location_id = ? ";
			if (supplierId != null && supplierId.length() > 0)
			{
				sql += " and ro.supplier_id in ('" + supplierId + "') ";
			}

			sql += " and ro.id like '%" + poNumber + "%'  and os.name in ('PO Created','PO Sent','PO Partially Received')"
					+ "ORDER BY ro.date DESC limit "+requestOrderGetPacket.getStartIndex()+","+requestOrderGetPacket.getEndIndex()+"  ";
			
			
			

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, businessId).getResultList();
			TimezoneTime timezoneTime = new TimezoneTime();
			for (Object[] objRow : resultList)
			{
				RequestOrderDisplay detailDisplayPacket = new RequestOrderDisplay();

				detailDisplayPacket.setId((String) objRow[0]);
				String locationdId = (String) objRow[4];
				if(locationdId !=null && locationdId.length()>0)
				{
					//select ro.id,ro.location_id,ro.created, l.name,l.locations_type_id, ro.grn_count  
					Location location = getLocationsById(em, locationdId);
					if(location!=null){
					detailDisplayPacket.setSupplierName(location.getName());
					detailDisplayPacket.setSupplierTypeId(location.getLocationsTypeId());	
					
				}}
				detailDisplayPacket.setDate(timezoneTime.getDateFromTimeStamp((Timestamp) objRow[2]));
				
				detailDisplayPacket.setPoNumber(detailDisplayPacket.getId() + "");
				
				
				/*boolean isAdd = false;
				if(((int) objRow[4]) == 1 && ((int) objRow[5]) == 0)
				{
					isAdd = true;
				}
				
				if(!isAdd)
				{*/
					ans.add(detailDisplayPacket);	
				//}
				
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	@POST
	@Path("/getAllReceiveOrderForLocation")
	public String getAllReceiveOrderForLocation(RequestOrderGetPacket requestOrderGetPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		
		EntityManager em = null;
		String supplierId = requestOrderGetPacket.getSupplierId();
		String poNumber = requestOrderGetPacket.getPoNumber();
		String businessId = requestOrderGetPacket.getBusinessId();
		try
		{
			List<RequestOrderDisplay> ans = new ArrayList<RequestOrderDisplay>();
			if (supplierId == null || supplierId.equals("null") || supplierId.equals(null))
			{
				supplierId = "";
			}
			if (poNumber == null || poNumber.equals("null") || poNumber.equals(null))
			{
				poNumber = "";
			}

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = " select ro.id,ro.location_id,ro.created, l.name,l.locations_type_id, ro.grn_count,ro.supplier_id  from request_order ro  " + " left join order_status os on ro.status_id=os.id "
					+ " left join locations l on l.id=ro.supplier_id where location_id = ? ";
			if (supplierId != null && supplierId.length() > 0)
			{
				sql += " and supplier_id in ('" + supplierId + "') ";
			}

			sql += " and ro.id like '%" + poNumber + "%'  and os.name in ('PO Created','PO Sent','PO Partially Received')"
					+ "ORDER BY ro.date DESC limit "+requestOrderGetPacket.getStartIndex()+","+requestOrderGetPacket.getEndIndex()+"  ";
			
			

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, businessId).getResultList();
			TimezoneTime timezoneTime = new TimezoneTime();
			for (Object[] objRow : resultList)
			{
				RequestOrderDisplay detailDisplayPacket = new RequestOrderDisplay();

				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setDate(timezoneTime.getDateFromTimeStamp((Timestamp) objRow[2]));
				detailDisplayPacket.setSupplierName((String) objRow[3]);
				detailDisplayPacket.setPoNumber(detailDisplayPacket.getId() + "");
				if(objRow[4] != null)
				{
					detailDisplayPacket.setSupplierTypeId((int) objRow[4]);	
				}
				
				if(/*(int) objRow[1] == (int) objRow[6] ||*/ (String) objRow[6] == null)
				{
					ans.add(detailDisplayPacket);
				}else if((int) objRow[4] == 3 || (int) objRow[4] == 4)
				{
					ans.add(detailDisplayPacket);
				}
					
				
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	

	@POST
	@Path("/getAllReceiveOrderCount")
	public BigInteger getAllReceiveOrderCount(RequestOrderGetPacket requestOrderGetPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		String supplierId = requestOrderGetPacket.getSupplierId();
		String poNumber = requestOrderGetPacket.getPoNumber();
		String businessId = requestOrderGetPacket.getBusinessId();
		try {
			List<RequestOrderDisplay> ans = new ArrayList<RequestOrderDisplay>();
			if (supplierId == null || supplierId.equals("null") || supplierId.equals(null)) {
				supplierId = "";
			}
			if (poNumber == null || poNumber.equals("null") || poNumber.equals(null)) {
				poNumber = "";
			}

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = " select ro.id,ro.location_id,ro.created, ro.grn_count,ro.supplier_id  from request_order ro  " + " left join order_status os on ro.status_id=os.id "
					+ "  where ro.location_id = ? ";
			if (supplierId != null && supplierId.length() > 0)
			{
				sql += " and supplier_id in ('" + supplierId + "') ";
			}

			sql += " and ro.id like '%" + poNumber + "%'  and os.name in ('PO Created','PO Sent','PO Partially Received')"
					+ "ORDER BY ro.date DESC limit "+requestOrderGetPacket.getStartIndex()+","+requestOrderGetPacket.getEndIndex()+"  ";
			
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, businessId).getResultList();
			TimezoneTime timezoneTime = new TimezoneTime();
			for (Object[] objRow : resultList) {
				RequestOrderDisplay detailDisplayPacket = new RequestOrderDisplay();

				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setDate(timezoneTime.getDateFromTimeStamp((Timestamp) objRow[2]));
				detailDisplayPacket.setSupplierName((String) objRow[3]);
				detailDisplayPacket.setPoNumber(detailDisplayPacket.getId() + "");
				if (objRow[4] != null) {
					detailDisplayPacket.setSupplierTypeId((int) objRow[4]);
				}

				/*boolean isAdd = false;
				if (((int) objRow[4]) == 1 && ((int) objRow[5]) == 0) {
					isAdd = true;
				}

				if (!isAdd) {
					ans.add(detailDisplayPacket);
				}*/
				ans.add(detailDisplayPacket);
			}

			BigInteger count = BigInteger.valueOf(ans.size());
			return count;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getOrderStatusForInventory/{locationId}")
	public String getOrderStatusForInventory(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("locationId") String locationId) throws IOException, InvalidSessionException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<OrderStatus> orderStatusList = new InventoryServiceBean().getOrderStatusForInventory(em, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(orderStatusList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllPOReceived/{businessId}")
	public String getAllPOReceived(@PathParam("businessId") String businessId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			List<RequestOrder> ans = new ArrayList<RequestOrder>();

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = " select distinct grn_number,ro.id ,ro.status_id,ro.date,ro.created,ro.updated "
					+ " from goods_receive_notes grn join request_order_detail_items g on g.id=grn.request_order_details_item_id "
					+ " join request_order ro on ro.id=g.request_id"
					+ " left join order_status os on ro.status_id=os.id where "
					+ " os.name in ('PO Partially Received','PO Received','Request In Process','Request Partially Processed') and grn.is_allotment=1 and grn.is_grn_close=0 and grn.status!='D' and ro.location_id = ? order by grn.grn_number";
			// Request Allocated removed from list and Request In Process
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, businessId).getResultList();
			for (Object[] objRow : resultList) {
				RequestOrder detailDisplayPacket = new RequestOrder();
				detailDisplayPacket.setChallanNumber((String) objRow[0]);
				detailDisplayPacket.setId((String) objRow[1]);
				detailDisplayPacket.setStatusId((String) objRow[2]);
				detailDisplayPacket.setDate((String) objRow[3]);
				detailDisplayPacket.setCreated((Timestamp) objRow[4]);

				OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,
						OrderStatus.class, detailDisplayPacket.getStatusId());
				if (orderStatus != null) {
					detailDisplayPacket.setLocalTime(
							new TimezoneTime().getLocationSpecificTimeToAdd(orderStatus.getLocationsId(), em));
				}

				detailDisplayPacket.setUpdated((Timestamp) objRow[5]);

				ans.add(detailDisplayPacket);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/receivePurchaseOrder")
	public String receivePurchaseOrder(RequestOrderPacket requestOrderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, requestOrderPacket);
			tx = em.getTransaction();
			tx.begin();

			RequestOrder requestOrder = requestOrderPacket.getRequestOrder();
			String orderStatusName = ((OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,
					OrderStatus.class, requestOrder.getStatusId())).getName();

			requestOrder = new InventoryServiceBean().receiveOrderWithItems(httpRequest, em,
					requestOrderPacket.getRequestOrder(), requestOrderPacket, requestOrderPacket);
			
			tx.commit();

			if (orderStatusName.equals("PO Cancelled")) {

				sendEmailForPOCancelled(requestOrder.getId(), requestOrder.getLocationId(), sessionId);
			}

			return new JSONUtility(httpRequest).convertToJsonString(requestOrder);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/updateItemToSupplier")
	public String updateItemToSupplier(ItemToSupplierPacket itemToSupplierPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemToSupplierPacket);
			tx = em.getTransaction();
			em.getTransaction().begin();
			ItemToSupplier itemToSupplier = new InventoryServiceBean().updateItemToSupplier(httpRequest, em,
					itemToSupplierPacket.getItemToSupplier());
			em.getTransaction().commit();
			String json = new StoreForwardUtility().returnJsonPacket(itemToSupplierPacket, "ItemToSupplierPacket",
					httpRequest);

			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					itemToSupplierPacket.getLocationId(), Integer.parseInt(itemToSupplierPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(itemToSupplier);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/addUpdateRequestOrderDetailForAdmin")
	public String addUpdateRequestOrderDetailForAdmin(RequestOrderPacket requestOrderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			tx = em.getTransaction();
			em.getTransaction().begin();

			String queryString = "select l from RequestOrder l where " + "l.locationId ="
					+ requestOrderPacket.getRequestOrder().getLocationId() + "and l.statusId ="
					+ requestOrderPacket.getRequestOrder().getStatusId();
			TypedQuery<RequestOrder> query = em.createQuery(queryString, RequestOrder.class);

			RequestOrder requestOrder = null;
			try {
				requestOrder = query.getSingleResult();
				requestOrder
						.setRequestOrderDetailItems(requestOrderPacket.getRequestOrder().getRequestOrderDetailItems());
			} catch (Exception e) {
				logger.severe(e);
			}
			if (requestOrder == null) {
				requestOrder = requestOrderPacket.getRequestOrder();
				requestOrder.setLocalTime(
						new TimezoneTime().getLocationSpecificTimeToAdd(requestOrder.getLocationId(), em));
				requestOrder.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			}

			requestOrder.getRequestOrderDetailItems().get(0).setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			requestOrder.getRequestOrderDetailItems().get(0)
					.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(requestOrder.getLocationId(), em));
			requestOrder.getRequestOrderDetailItems().get(0).setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			requestOrder = new InventoryServiceBean().addRequestOrderWithItems(httpRequest, em, requestOrder,
					requestOrderPacket);
			em.getTransaction().commit();

			String json = new StoreForwardUtility().returnJsonPacket(requestOrderPacket, "RequestOrderPacket",
					httpRequest);

			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, requestOrderPacket.getLocationId(),
					Integer.parseInt(requestOrderPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(requestOrder);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getPDFInHTMLFormatForAddUpdateRequestOrder/{requestOrderId}/{locationId}")
	public String getPDFInHTMLFormatForAddUpdateRequestOrder(@PathParam(value = "requestOrderId") String requestOrderId,
			@PathParam(value = "locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		PDFInHTMLFormatPacket htmlFormatPacket = new PDFInHTMLFormatPacket();
		// String fileString = "";

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			// RequestOrder requestOrderheader = (RequestOrder) new
			// CommonMethods().getObjectById("RequestOrder",
			// em,RequestOrder.class, requestOrderId);

			try {
				tx = em.getTransaction();
				tx.begin();

				Location foundLocation = null;
				if (locationId != null && !locationId.equals("0")) {
					String queryString = "select l from Location l where " + "l.id ='" + locationId
							+ "' and (l.locationsId = '0' or l.locationsId is null) and l.locationsTypeId = '1'";
					TypedQuery<Location> query = em.createQuery(queryString, Location.class);
					foundLocation = query.getSingleResult();
				}

				htmlFormatPacket.setFileName(foundLocation.getName() + " Purchase Order.pdf");

				String pdfString = receiptPDFFormat
						.createRequestOrderInvoicePDFString(em, httpRequest, requestOrderId, locationId, 2, "")
						.toString();

				String footer = receiptPDFFormat.createRequestOrderInvoiceFooterString(em, locationId).toString()
						.replace("\"", "&quot;");
				htmlFormatPacket.setEmailFooter(footer);

				pdfString = pdfString.replace("</body>", footer + "</body>");
				pdfString = pdfString.replace("\"", "&quot;");

				htmlFormatPacket.setPdfData(pdfString);
				htmlFormatPacket.setEmailBody(receiptPDFFormat.createRequestOrderInvoiceBodyString().toString());

				// fileString =
				// EmailTemplateKeys.getPDFForRequestOrderConfirmation(httpRequest,
				// em, locationId, requestOrderheader.getUpdatedBy(), pdfData,
				// EmailTemplateKeys.REQUEST_ORDER_CONFIRMATION,
				// requestOrderheader.getId(), fileName, emailBody,
				// emailFooter);

				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			return new JSONUtility(httpRequest).convertToJsonString(htmlFormatPacket);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getPDFInHTMLFormatForAllotmentPO/{grnRef}/{locationId}")
	public String getPDFInHTMLFormatForAllotmentPO(@PathParam(value = "grnRef") String grnRef,
			@PathParam(value = "locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		PDFInHTMLFormatPacket htmlFormatPacket = new PDFInHTMLFormatPacket();
		// String fileString = "";

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			// RequestOrder requestOrderheader = (RequestOrder) new
			// CommonMethods().getObjectById("RequestOrder",
			// em,RequestOrder.class, requestOrderId);

			try {
				tx = em.getTransaction();
				tx.begin();

				Location foundLocation = null;
				if (locationId != null && !locationId.equals("0")) {
					String queryString = "select l from Location l where " + "l.id ='" + locationId
							+ "' and (l.locationsId = '0' or l.locationsId is null) and l.locationsTypeId = '1'";
					TypedQuery<Location> query = em.createQuery(queryString, Location.class);
					foundLocation = query.getSingleResult();
				}

				htmlFormatPacket.setFileName(foundLocation.getName() + " Purchase Order.pdf");

				String pdfString = receiptPDFFormat
						.createRequestOrderInvoicePDFString(em, httpRequest, null, locationId, 1, grnRef).toString();
				pdfString = pdfString.replace("\"", "&quot;");

				String footer = receiptPDFFormat.createRequestOrderInvoiceFooterString(em, locationId).toString()
						.replace("\"", "&quot;");
				htmlFormatPacket.setEmailFooter(footer);

				pdfString = pdfString.replace("</body>", footer + "</body>");

				htmlFormatPacket.setPdfData(pdfString);
				htmlFormatPacket.setEmailBody(receiptPDFFormat.createRequestOrderInvoiceBodyString().toString());
				// fileString =
				// EmailTemplateKeys.getPDFForRequestOrderConfirmation(httpRequest,
				// em, locationId, requestOrderheader.getUpdatedBy(), pdfData,
				// EmailTemplateKeys.REQUEST_ORDER_CONFIRMATION,
				// requestOrderheader.getId(), fileName, emailBody,
				// emailFooter);

				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			return new JSONUtility(httpRequest).convertToJsonString(htmlFormatPacket);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getPDFInHTMLFormatForPOReceiveFromMasterLocation/{grnRef}/{locationId}")
	public String getPDFInHTMLFormatForPOReceiveFromMasterLocation(@PathParam(value = "grnRef") String grnRef,
			@PathParam(value = "locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		PDFInHTMLFormatPacket htmlFormatPacket = new PDFInHTMLFormatPacket();
		// String fileString = "";

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			// RequestOrder requestOrderheader = (RequestOrder) new
			// CommonMethods().getObjectById("RequestOrder",
			// em,RequestOrder.class, requestOrderId);

			try {
				tx = em.getTransaction();
				tx.begin();

				Location foundLocation = null;
				if (locationId != null && !locationId.equals("0")) {
					String queryString = "select l from Location l where " + "l.id ='" + locationId
							+ "' and (l.locationsId = '0' or l.locationsId is null) and l.locationsTypeId = '1'";
					TypedQuery<Location> query = em.createQuery(queryString, Location.class);
					foundLocation = query.getSingleResult();
				}

				htmlFormatPacket.setFileName(foundLocation.getName() + " Purchase Order.pdf");

				String pdfString = receiptPDFFormat
						.createRequestOrderInvoicePDFString(em, httpRequest, null, locationId, 4, grnRef).toString();
				pdfString = pdfString.replace("\"", "&quot;");

				String footer = receiptPDFFormat.createRequestOrderInvoiceFooterString(em, locationId).toString()
						.replace("\"", "&quot;");
				htmlFormatPacket.setEmailFooter(footer);

				pdfString = pdfString.replace("</body>", footer + "</body>");

				htmlFormatPacket.setPdfData(pdfString);
				htmlFormatPacket.setEmailBody(receiptPDFFormat.createRequestOrderInvoiceBodyString().toString());
				// fileString =
				// EmailTemplateKeys.getPDFForRequestOrderConfirmation(httpRequest,
				// em, locationId, requestOrderheader.getUpdatedBy(), pdfData,
				// EmailTemplateKeys.REQUEST_ORDER_CONFIRMATION,
				// requestOrderheader.getId(), fileName, emailBody,
				// emailFooter);

				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			return new JSONUtility(httpRequest).convertToJsonString(htmlFormatPacket);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/sendEmailByEmailAddrForAddUpdateRequestOrder/{requestOrderId}/{locationId}/{emailAddress}")
	public String sendEmailByEmailAddrForAddUpdateRequestOrder(
			@PathParam(value = "requestOrderId") String requestOrderId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "emailAddress") String emailAddress,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			// insert into order history
			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			RequestOrder requestOrderheader = (RequestOrder) new CommonMethods().getObjectById("RequestOrder", em,
					RequestOrder.class, requestOrderId);
			// 0 because mail sending from dine in

			try {
				tx = em.getTransaction();
				tx.begin();

				Location foundLocation = null;
				if (locationId != null && !locationId.equals("0")) {
					String queryString = "select l from Location l where " + "l.id ='" + locationId
							+ "' and(l.locationsId = '0' or l.locationsId is null) and l.locationsTypeId = '1'";
					TypedQuery<Location> query = em.createQuery(queryString, Location.class);
					foundLocation = query.getSingleResult();
				}

				String fileName = foundLocation.getName() + " Purchase Order.pdf";

				String pdfData = receiptPDFFormat
						.createRequestOrderInvoicePDFString(em, httpRequest, requestOrderId, locationId, 2, "")
						.toString();

				String emailBody = receiptPDFFormat.createRequestOrderInvoiceBodyString().toString();

				String emailFooter = receiptPDFFormat.createRequestOrderInvoiceFooterString(em, locationId).toString();

				EmailTemplateKeys.sendEmailByEmailAddrForAddUpdateRequestOrder(httpRequest, em, locationId,
						requestOrderheader.getUpdatedBy(), pdfData, EmailTemplateKeys.REQUEST_ORDER_CONFIRMATION,
						requestOrderheader.getId(), fileName, emailBody, emailFooter, emailAddress);

				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			return new JSONUtility(httpRequest).convertToJsonString(true);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllPublishedItemBySupplier/{supplierId}/{locationId}/{startIndex}/{endIndex}")
	public String getAllPublishedItemBySupplier(@PathParam("supplierId") String supplierId,
			@PathParam("locationId") String locationId, @PathParam("startIndex") int startIndex,
			@PathParam("endIndex") int endIndex, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {

//			/getAllPublishedItemBySupplier/0/d17c0568-925a-11e9-af33-001e8cf5405c/0/10
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<String> resultList = new ArrayList<String>();
			List<SupplierPacket> supplierPacketList = new ArrayList<SupplierPacket>();
			if (supplierId == null || supplierId.equals("0")) {
				// / get all supplier list
				String sql = " select distinct l.id from locations l  "
						+ "  join item_to_supplier its on (its.primary_supplier_id=l.id or its.secondary_supplier_id=l.id or its.tertiary_supplier_id=l.id ) "
						+ "   where ( its.item_id in (select global_item_id from items where id in  "
						+ "  ( select items_id from request_order_detail_items  rod  "
						+ " join order_detail_status ods on ods.id=rod.status_id where ods.name='Item Requested') ) "
						+ " or its.item_id in (select id from items where id in   "
						+ " ( select items_id from request_order_detail_items  rod  "
						+ " join order_detail_status ods on ods.id=rod.status_id where ods.name='Item Requested') and (global_item_id is null or global_item_id='0')))  ";
				
				logger.severe("sql=================================================================="+sql);
				
				resultList = (List<String>) em.createNativeQuery(sql).getResultList();

			} else {
				resultList.add(supplierId);
			}
//			resultList.add(null);
			
			for (String i : resultList) {
				
				SupplierPacket supplierPacket = new SupplierPacket();
				Location location = getLocationsById(em, i);
				if (location == null && i == null) {
					location = new Location();
					location.setName("No Supplier");
				}
				supplierPacket.setSuppliers(location);
				List<RequestOrderDetailItemsDisplayPacket> requestOrderDetailItemsDisplayPacketList = new ArrayList<RequestOrderDetailItemsDisplayPacket>();
				// get all global item
				String sqlForGlobalItemId =null;
				logger.severe("i=================================================================="+i);
				if( i==null || i.equals('0') ){
					 sqlForGlobalItemId = " select i.name,i.id,i.stock_uom  from items i where i.id in ( select item_id from item_to_supplier its join items i "
							+ " on its.item_id =i.id  where (its.primary_supplier_id is null  or its.secondary_supplier_id is null   or its.tertiary_supplier_id is null ) "
							+ "and (i.global_item_id is null or i.global_item_id='0'))";
					
				}else {
					 sqlForGlobalItemId = " select i.name,i.id,i.stock_uom  from items i where i.id in ( select item_id from item_to_supplier its join items i "
							+ " on its.item_id =i.id  where (its.primary_supplier_id ='" + i
							+ "' or its.secondary_supplier_id= '" + i + "'  or its.tertiary_supplier_id= '" + i
							+ "'  ) and (i.global_item_id is null or i.global_item_id='0'))";
					
				}
				
				@SuppressWarnings("unchecked")
				List<Object[]> resultList2 = em.createNativeQuery(sqlForGlobalItemId).getResultList();
				for (Object[] obj : resultList2) {
					RequestOrderDetailItemsDisplayPacket detailItemsDisplayPacket = new RequestOrderDetailItemsDisplayPacket();
					detailItemsDisplayPacket.setItemName((String) obj[0]);
					detailItemsDisplayPacket.setGlobleItemId((String) obj[1]);
					UnitOfMeasurement unitOfMeasurement = (UnitOfMeasurement) new CommonMethods()
							.getObjectById("UnitOfMeasurement", em, UnitOfMeasurement.class, (String) obj[2]);
					if (unitOfMeasurement != null) {
						detailItemsDisplayPacket.setUomName(unitOfMeasurement.getDisplayName());
					}

					// need to write get service
					detailItemsDisplayPacket
							.setSuppliers(getSupplierList(em, detailItemsDisplayPacket.getGlobleItemId()));

					// TODO Ankur - can an ordererd item have no quantity?

					
					detailItemsDisplayPacket.setQuantity(
							getQuantityOfItemOrdered(em, detailItemsDisplayPacket.getGlobleItemId(), locationId));
					
					BigDecimal availableQuantity = getAvailableQuantityFromInventory(em,
							detailItemsDisplayPacket.getGlobleItemId());
					
					detailItemsDisplayPacket.setAvailableQuantity(availableQuantity);
					if (detailItemsDisplayPacket.getQuantity() != null
							&& detailItemsDisplayPacket.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
						requestOrderDetailItemsDisplayPacketList.add(detailItemsDisplayPacket);
						supplierPacket.setRequestOrderDetailItems(requestOrderDetailItemsDisplayPacketList);
					}

				}
				if (supplierPacket != null && supplierPacket.getRequestOrderDetailItems() != null
						&& supplierPacket.getRequestOrderDetailItems().size() > 0) {
					supplierPacketList.add(supplierPacket);
				}
			}

			int fromIndex = startIndex;
			int toIndex = fromIndex + endIndex;
			if (supplierPacketList.size() < endIndex) {
				toIndex = supplierPacketList.size();
			}
			if (supplierPacketList != null && supplierPacketList.size() > 0) {
				supplierPacketList = supplierPacketList.subList(fromIndex, toIndex);
			}
			return new JSONUtility(httpRequest).convertToJsonString(supplierPacketList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllPublishedItemBySupplierForLocation/{supplierId}/{locationId}/{startIndex}/{endIndex}")
	public String getAllPublishedItemBySupplierForLocation(@PathParam("supplierId") String supplierId,
			@PathParam("locationId") String locationId, @PathParam("startIndex") int startIndex,
			@PathParam("endIndex") int endIndex) throws Exception {
		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			List<String> resultList = new ArrayList<String>();
			List<SupplierPacket> supplierPacketList = new ArrayList<SupplierPacket>();
			if (supplierId == null|| supplierId.equals("0") || supplierId.equals("null")) {
				// / get all supplier list
				String sql = " select distinct l.id from locations l  "
						+ "  join item_to_supplier its on (its.primary_supplier_id=l.id   or its.secondary_supplier_id=l.id or its.tertiary_supplier_id=l.id ) "
						+ "   where ( its.item_id in (select global_item_id from items where id in  "
						+ "  ( select items_id from request_order_detail_items  rod  "
						+ " join order_detail_status ods on ods.id=rod.status_id where ods.name in ('Item Requested', 'Item saved')) ) "
						+ " or its.item_id in (select id from items where id in   "
						+ " ( select items_id from request_order_detail_items  rod  "
						+ " join order_detail_status ods on ods.id=rod.status_id where ods.name in ('Item Requested','Item saved')) and (global_item_id='0' or global_item_id is null) ))  ";
				resultList = (List<String>) em.createNativeQuery(sql).getResultList();

			} else {
				resultList.add(supplierId);
			}
//			resultList.add(null);

			for (String i : resultList) {

				SupplierPacket supplierPacket = new SupplierPacket();
				Location location = getLocationsById(em, i);
				if (location == null && i == null) {
					location = new Location();
					location.setName("No Supplier");
				}
				supplierPacket.setSuppliers(location);
				List<RequestOrderDetailItemsDisplayPacket> requestOrderDetailItemsDisplayPacketList = new ArrayList<RequestOrderDetailItemsDisplayPacket>();
				// get all global item
				String sqlForGlobalItemId = " select i.name,i.id,i.stock_uom  from items i where i.id in ( select item_id from item_to_supplier its join items i "
						+ " on its.item_id =i.id  where (its.primary_supplier_id ='" + i
						+ "'  or its.secondary_supplier_id='" + i + "' or its.tertiary_supplier_id='" + i
						+ "')  and (i.global_item_id='0' or i.global_item_id is null))";

				@SuppressWarnings("unchecked")
				List<Object[]> resultList2 = em.createNativeQuery(sqlForGlobalItemId).getResultList();
				for (Object[] obj : resultList2) {
					RequestOrderDetailItemsDisplayPacket detailItemsDisplayPacket = new RequestOrderDetailItemsDisplayPacket();
					detailItemsDisplayPacket.setItemName((String) obj[0]);
					detailItemsDisplayPacket.setGlobleItemId((String) obj[1]);
					UnitOfMeasurement unitOfMeasurement = (UnitOfMeasurement) new CommonMethods()
							.getObjectById("UnitOfMeasurement", em, UnitOfMeasurement.class, (String) obj[2]);
					if (unitOfMeasurement != null) {
						detailItemsDisplayPacket.setUomName(unitOfMeasurement.getDisplayName());
					}

					// need to write get service
					detailItemsDisplayPacket
							.setSuppliers(getSupplierList(em, detailItemsDisplayPacket.getGlobleItemId()));

					// TODO Ankur - can an ordererd item have no quantity?

					detailItemsDisplayPacket.setQuantity(
							getQuantityOfItemOrderedForLocation(em, detailItemsDisplayPacket.getGlobleItemId(), i));

					BigDecimal availableQuantity = getAvailableQuantityFromInventory(em,
							detailItemsDisplayPacket.getGlobleItemId());
					detailItemsDisplayPacket.setAvailableQuantity(availableQuantity);

					if (detailItemsDisplayPacket.getQuantity() != null
							&& detailItemsDisplayPacket.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
						requestOrderDetailItemsDisplayPacketList.add(detailItemsDisplayPacket);
						supplierPacket.setRequestOrderDetailItems(requestOrderDetailItemsDisplayPacketList);
					}

				}
				if (supplierPacket != null && supplierPacket.getRequestOrderDetailItems() != null
						&& supplierPacket.getRequestOrderDetailItems().size() > 0) {
					supplierPacketList.add(supplierPacket);
				}

			}

			int fromIndex = startIndex;
			int toIndex = fromIndex + endIndex;
			if (supplierPacketList.size() < endIndex) {
				toIndex = supplierPacketList.size();
			}
			if (supplierPacketList != null && supplierPacketList.size() > 0) {
				supplierPacketList = supplierPacketList.subList(fromIndex, toIndex);
			}
			return new JSONUtility(httpRequest).convertToJsonString(supplierPacketList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllPublishedItemBySupplierCountForLocation/{supplierId}/{locationId}")
	public BigInteger getAllPublishedItemBySupplierCountForLocation(@PathParam("supplierId") String supplierId,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<String> resultList = new ArrayList<String>();
			List<SupplierPacket> supplierPacketList = new ArrayList<SupplierPacket>();
			if (supplierId == null || supplierId.equals("0") || supplierId.equals("null")) {
				// / get all supplier list
				String sql = " select distinct l.id from locations l  "
						+ "  join item_to_supplier its on (its.primary_supplier_id=l.id   or its.secondary_supplier_id=l.id or its.tertiary_supplier_id=l.id ) "
						+ "   where ( its.item_id in (select global_item_id from items where id in  "
						+ "  ( select items_id from request_order_detail_items  rod  "
						+ " join order_detail_status ods on ods.id=rod.status_id where ods.name in ('Item Requested', 'Item saved')) ) "
						+ " or its.item_id in (select id from items where id in   "
						+ " ( select items_id from request_order_detail_items  rod  "
						+ " join order_detail_status ods on ods.id=rod.status_id where ods.name in ('Item Requested','Item saved')) and (global_item_id='0' or global_item_id is null) ))  ";

				resultList = (List<String>) em.createNativeQuery(sql).getResultList();

			} else {
				resultList.add(supplierId);
			}
			resultList.add(null);

			for (String i : resultList) {
				SupplierPacket supplierPacket = new SupplierPacket();
				Location location = getLocationsById(em, i);
				if (location == null && i == null) {
					location = new Location();
					location.setName("No Supplier");
				}
				supplierPacket.setSuppliers(location);
				List<RequestOrderDetailItemsDisplayPacket> requestOrderDetailItemsDisplayPacketList = new ArrayList<RequestOrderDetailItemsDisplayPacket>();
				// get all global item
				String sqlForGlobalItemId = " select i.name,i.id,i.stock_uom  from items i where i.id in ( select item_id from item_to_supplier its join items i "
						+ " on its.item_id =i.id  where (its.primary_supplier_id ='" + i
						+ "'  or its.secondary_supplier_id= '" + i + "' or its.tertiary_supplier_id= '" + i
						+ "' ) and (i.global_item_id='0' or i.global_item_id is null))";

				@SuppressWarnings("unchecked")
				List<Object[]> resultList2 = em.createNativeQuery(sqlForGlobalItemId).getResultList();
				for (Object[] obj : resultList2) {
					RequestOrderDetailItemsDisplayPacket detailItemsDisplayPacket = new RequestOrderDetailItemsDisplayPacket();
					detailItemsDisplayPacket.setItemName((String) obj[0]);
					detailItemsDisplayPacket.setGlobleItemId((String) obj[1]);
					UnitOfMeasurement unitOfMeasurement = (UnitOfMeasurement) new CommonMethods()
							.getObjectById("UnitOfMeasurement", em, UnitOfMeasurement.class, (String) obj[2]);
					if (unitOfMeasurement != null) {
						detailItemsDisplayPacket.setUomName(unitOfMeasurement.getDisplayName());
					}

					// need to write get service
					detailItemsDisplayPacket
							.setSuppliers(getSupplierList(em, detailItemsDisplayPacket.getGlobleItemId()));

					// TODO Ankur - can an ordererd item have no quantity?

					detailItemsDisplayPacket.setQuantity(
							getQuantityOfItemOrderedForLocation(em, detailItemsDisplayPacket.getGlobleItemId(), i));

					BigDecimal availableQuantity = getAvailableQuantityFromInventory(em,
							detailItemsDisplayPacket.getGlobleItemId());
					detailItemsDisplayPacket.setAvailableQuantity(availableQuantity);

					if (detailItemsDisplayPacket.getQuantity() != null
							&& detailItemsDisplayPacket.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
						requestOrderDetailItemsDisplayPacketList.add(detailItemsDisplayPacket);
						supplierPacket.setRequestOrderDetailItems(requestOrderDetailItemsDisplayPacketList);
					}

				}
				if (supplierPacket != null && supplierPacket.getRequestOrderDetailItems() != null
						&& supplierPacket.getRequestOrderDetailItems().size() > 0) {
					supplierPacketList.add(supplierPacket);
				}

			}

			return BigInteger.valueOf(supplierPacketList.size());

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllPublishedItemBySupplierCount/{supplierId}/{locationId}")
	public BigInteger getAllPublishedItemBySupplierCount(@PathParam("supplierId") String supplierId,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<String> resultList = new ArrayList<String>();
			List<SupplierPacket> supplierPacketList = new ArrayList<SupplierPacket>();
			if (supplierId == null || supplierId.equals("0")) {

				// / get all supplier list
				String sql = " select distinct l.id from locations l  "
						+ "  join item_to_supplier its on (its.primary_supplier_id=l.id   or its.secondary_supplier_id=l.id or its.tertiary_supplier_id=l.id ) "
						+ "   where ( its.item_id in (select global_item_id from items where id in  "
						+ "  ( select items_id from request_order_detail_items  rod  "
						+ " join order_detail_status ods on ods.id=rod.status_id where ods.name='Item Requested') ) "
						+ " or its.item_id in (select id from items where id in   "
						+ " ( select items_id from request_order_detail_items  rod  "
						+ " join order_detail_status ods on ods.id=rod.status_id where ods.name='Item Requested') and (global_item_id='0' or global_item_id is null) ))  ";

				resultList = (List<String>) em.createNativeQuery(sql).getResultList();
			} else {
				resultList.add(supplierId);
			}
//			resultList.add(null);
			for (String i : resultList) {
				SupplierPacket supplierPacket = new SupplierPacket();
				Location location = getLocationsById(em, i);
				if (location == null && i == null) {
					location = new Location();
					location.setName("No Supplier");
				}
				supplierPacket.setSuppliers(location);
				List<RequestOrderDetailItemsDisplayPacket> requestOrderDetailItemsDisplayPacketList = new ArrayList<RequestOrderDetailItemsDisplayPacket>();
				// get all global item
				String sqlForGlobalItemId = " select i.name,i.id,i.stock_uom  from items i where i.id in ( select item_id from item_to_supplier its join items i "
						+ " on its.item_id =i.id  where (its.primary_supplier_id ='" + i
						+ "'  or its.secondary_supplier_id= '" + i + "'  or its.tertiary_supplier_id= '" + i
						+ "'  )  and (i.global_item_id='0' or i.global_item_id is null)) ";
				@SuppressWarnings("unchecked")
				List<Object[]> resultList2 = em.createNativeQuery(sqlForGlobalItemId).getResultList();
				for (Object[] obj : resultList2) {
					RequestOrderDetailItemsDisplayPacket detailItemsDisplayPacket = new RequestOrderDetailItemsDisplayPacket();
					detailItemsDisplayPacket.setItemName((String) obj[0]);
					detailItemsDisplayPacket.setGlobleItemId((String) obj[1]);
					UnitOfMeasurement unitOfMeasurement = (UnitOfMeasurement) new CommonMethods()
							.getObjectById("UnitOfMeasurement", em, UnitOfMeasurement.class, (String) obj[2]);
					if (unitOfMeasurement != null) {
						detailItemsDisplayPacket.setUomName(unitOfMeasurement.getDisplayName());
					}

					// need to write get service
					detailItemsDisplayPacket
							.setSuppliers(getSupplierList(em, detailItemsDisplayPacket.getGlobleItemId()));

					// TODO Ankur - can an ordererd item have no quantity?
					detailItemsDisplayPacket.setQuantity(
							getQuantityOfItemOrdered(em, detailItemsDisplayPacket.getGlobleItemId(), locationId));

					BigDecimal availableQuantity = getAvailableQuantityFromInventory(em,
							detailItemsDisplayPacket.getGlobleItemId());
					detailItemsDisplayPacket.setAvailableQuantity(availableQuantity);
					if (detailItemsDisplayPacket.getQuantity() != null
							&& detailItemsDisplayPacket.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
						requestOrderDetailItemsDisplayPacketList.add(detailItemsDisplayPacket);
						supplierPacket.setRequestOrderDetailItems(requestOrderDetailItemsDisplayPacketList);
					}

				}
				supplierPacketList.add(supplierPacket);
			}
			return BigInteger.valueOf(supplierPacketList.size());

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllPublishedItemBySupplierIdAndLocationIdCount/{supplierId}/{locationId}")
	public BigInteger getAllPublishedItemBySupplierIdAndLocationIdCount(@PathParam("supplierId") String supplierId,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<String> resultList = new ArrayList<String>();
			List<SupplierPacket> supplierPacketList = new ArrayList<SupplierPacket>();
			if (supplierId == null || supplierId.equals("0")) {

				// / get all supplier list
				String sql = " select distinct l.id from locations l  "
						+ "  join item_to_supplier its on (its.primary_supplier_id=l.id  or its.secondary_supplier_id=l.id or its.tertiary_supplier_id=l.id ) "
						+ "   where ( its.item_id in (select global_item_id from items where id in  "
						+ "  ( select items_id from request_order_detail_items  rod  "
						+ " join order_detail_status ods on ods.id=rod.status_id where ods.name='Item Requested') ) "
						+ " or its.item_id in (select id from items where id in   "
						+ " ( select items_id from request_order_detail_items  rod  "
						+ " join order_detail_status ods on ods.id=rod.status_id where ods.name='Item Requested') ))  ";

				resultList = (List<String>) em.createNativeQuery(sql).getResultList();
			} else {
				resultList.add(supplierId);
			}
			resultList.add(null);
			for (String i : resultList) {
				SupplierPacket supplierPacket = new SupplierPacket();
				Location location = getLocationsById(em, i);
				if (location == null && i == null) {
					location = new Location();
					location.setName("No Supplier");
				}
				supplierPacket.setSuppliers(location);
				List<RequestOrderDetailItemsDisplayPacket> requestOrderDetailItemsDisplayPacketList = new ArrayList<RequestOrderDetailItemsDisplayPacket>();
				// get all global item
				String sqlForGlobalItemId = " select i.name,i.id,i.stock_uom, i.locations_id  from items i where i.id in ( select item_id from item_to_supplier its join items i "
						+ " on its.item_id =i.id  where (its.primary_supplier_id =" + i
						+ "   or its.secondary_supplier_id= " + i + " or its.tertiary_supplier_id = " + i + " ) )";

				@SuppressWarnings("unchecked")
				List<Object[]> resultList2 = em.createNativeQuery(sqlForGlobalItemId).getResultList();
				for (Object[] obj : resultList2) {
					if ((String) obj[3] == locationId) {
						RequestOrderDetailItemsDisplayPacket detailItemsDisplayPacket = new RequestOrderDetailItemsDisplayPacket();
						detailItemsDisplayPacket.setItemName((String) obj[0]);
						detailItemsDisplayPacket.setGlobleItemId((String) obj[1]);
						UnitOfMeasurement unitOfMeasurement = (UnitOfMeasurement) new CommonMethods()
								.getObjectById("UnitOfMeasurement", em, UnitOfMeasurement.class, (String) obj[2]);
						if (unitOfMeasurement != null) {
							detailItemsDisplayPacket.setUomName(unitOfMeasurement.getDisplayName());
						}

						// need to write get service
						detailItemsDisplayPacket
								.setSuppliers(getSupplierList(em, detailItemsDisplayPacket.getGlobleItemId()));

						// TODO Ankur - can an ordererd item have no quantity?
						detailItemsDisplayPacket.setQuantity(
								getQuantityOfItemOrdered(em, detailItemsDisplayPacket.getGlobleItemId(), locationId));

						BigDecimal availableQuantity = getAvailableQuantityFromInventory(em,
								detailItemsDisplayPacket.getGlobleItemId());
						detailItemsDisplayPacket.setAvailableQuantity(availableQuantity);
						if (detailItemsDisplayPacket.getQuantity() != null
								&& detailItemsDisplayPacket.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
							requestOrderDetailItemsDisplayPacketList.add(detailItemsDisplayPacket);
							supplierPacket.setRequestOrderDetailItems(requestOrderDetailItemsDisplayPacketList);
						}
					}

				}
				supplierPacketList.add(supplierPacket);
			}
			return BigInteger.valueOf(supplierPacketList.size());

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	public BigDecimal getAvailableQuantityFromInventory(EntityManager em, String itemId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Inventory> criteria = builder.createQuery(Inventory.class);
			Root<Inventory> r = criteria.from(Inventory.class);
			TypedQuery<Inventory> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(Inventory_.itemId), itemId)));
			Inventory inventory = query.getSingleResult();
			return inventory.getTotalAvailableQuanity();
		} catch (Exception e) {

			logger.severe("No Result found for itemId " + itemId + " in Inventory");

		}
		return new BigDecimal(0);
	}

	public ItemToSupplier getItemToSupplierbyItemId(EntityManager em, String itemId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemToSupplier> criteria = builder.createQuery(ItemToSupplier.class);
			Root<ItemToSupplier> suppliers = criteria.from(ItemToSupplier.class);
			TypedQuery<ItemToSupplier> query = em.createQuery(
					criteria.select(suppliers).where(builder.equal(suppliers.get(ItemToSupplier_.itemId), itemId),
							builder.notEqual(suppliers.get(ItemToSupplier_.status), "D")));
			return query.getSingleResult();
		} catch (NoResultException e) {
			logger.severe(e, "No Result found");
		}
		return null;
	}

	private List<Location> getSupplierList(EntityManager em, String itemId) {
		List<Location> l = new ArrayList<Location>();
		ItemToSupplier supplier = getItemToSupplierbyItemId(em, itemId);
		if (supplier != null) {
			Location location1 = getLocationsById(em, supplier.getPrimarySupplierId());
			if (location1 != null) {
				l.add(location1);
			}
			Location location2 = getLocationsById(em, supplier.getSecondarySupplierId());
			if (location2 != null) {
				l.add(location2);
			}
			Location location3 = getLocationsById(em, supplier.getTertiarySupplierId());
			if (location3 != null) {
				l.add(location3);
			}
		}

		return l;
	}

	private BigDecimal getQuantityOfItemOrdered(EntityManager em, String itemId, String locationId) {
		BigDecimal resultList = null;
		try {
			String sql = " select sum(quantity) from request_order_detail_items rod "
					+ " join order_detail_status ods on ods.id=rod.status_id "
					+ " join request_order ro on ro.id =rod.request_id "
					+ " where  ( ro.supplier_id='"+locationId+"' and  rod.items_id in ( select id from items where global_item_id ='"+itemId+"' or (id ='"+itemId+"' and (global_item_id is null or global_item_id='0')))  ) "
					+ " and  ods.name='Item Requested'   ";

			resultList = (BigDecimal) em.createNativeQuery(sql).getSingleResult();

		} catch (Exception e) {
			logger.severe(e, "No Result found");
		}
		return resultList;
	}

	private BigDecimal getQuantityOfItemOrderedForLocation(EntityManager em, String itemId, String locationId) {
		BigDecimal resultList = null;
		try {
			String sql = " select sum(quantity) from request_order_detail_items rod "
					+ " join order_detail_status ods on ods.id=rod.status_id "
					+ " join request_order ro on ro.id =rod.request_id "
					+ " where  ( ro.supplier_id=? and  rod.items_id in ( select id from items where global_item_id =? or (id =? and (global_item_id is null or global_item_id='0')) )  ) "
					+ " and  ods.name in ('Item Requested', 'Item saved' )  ";

			resultList = (BigDecimal) em.createNativeQuery(sql).setParameter(1, locationId).setParameter(2, itemId)
					.setParameter(3, itemId).getSingleResult();

		} catch (Exception e) {
			logger.severe(e, "No Result found");
		}
		return resultList;
	}

	Location getLocationsById(EntityManager em, String id) {

		try {
			if (id != null) {
				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<Location> cl = builder.createQuery(Location.class);
				Root<Location> l = cl.from(Location.class);
				TypedQuery<Location> query = em.createQuery(cl.select(l).where(builder.equal(l.get(Location_.id), id)));

				return query.getSingleResult();
			} else {
				return null;
			}

		} catch (Exception e) {

			logger.severe("No Result found");
		}
		return null;
	}

	@GET
	@Path("/getGoodsReceiveNotesByNumber/{grnNumber}")
	public String getGoodsReceiveNotesByNumber(@PathParam("grnNumber") String grnNumber,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<GoodsReceiveNotes> goodsReceiveNotes = new InventoryServiceBean().getGoodsReceiveNotesByNumber(em,
					grnNumber);
			return new JSONUtility(httpRequest).convertToJsonString(goodsReceiveNotes);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/receiveRequestOrder")
	public String receiveRequestOrder(GoodsReceiveNotesPacket goodsReceiveNotesPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			tx = em.getTransaction();
			tx.begin();
			goodsReceiveNotesPacket = new InventoryServiceBean().receiveRequestOrder(httpRequest,
					em, goodsReceiveNotesPacket,false);
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(goodsReceiveNotesPacket, "GoodsReceiveNotesPacket",
					httpRequest);

			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					goodsReceiveNotesPacket.getLocationId(), Integer.parseInt(goodsReceiveNotesPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(goodsReceiveNotesPacket.getGoodsReceiveNotesList());
		} catch (Exception e) {
			logger.severe(e);
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllRequestForAllocation/{locationId}/{requestname}/{startIndex}/{endIndex}")
	public String getAllRequestForAllocation(@PathParam("locationId") String locationId,
			@PathParam("requestname") String requestname, @PathParam("startIndex") int startIndex,
			@PathParam("endIndex") int endIndex, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			List<RequestOrderDisplay> ans = new ArrayList<RequestOrderDisplay>();

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			if (requestname == null || requestname.equals("null") || requestname.equals(null)) {
				requestname = "";
			}

			String sql = " select ro.id,ro.name as requestName,ro.date, l.name as locationName,l.id as l_id "
					+ "from request_order ro " + "left join order_status os on ro.status_id=os.id "
					+ "left join locations l on l.id=ro.location_id  "
					+ " where os.name in ('Request Sent','Request Partially Processed') ";
			if (locationId != null && !locationId.equals("0")) {
				sql += "  and location_id= '" + locationId + "'";

			}

			sql += " and ro.isPOOrder=0 and ro.name like '%" + requestname + "%' ";

			sql += "   order by str_to_date(ro.date,'%Y-%m-%d')  limit " + startIndex + "," + endIndex;

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).getResultList();
			for (Object[] objRow : resultList) {
				RequestOrderDisplay detailDisplayPacket = new RequestOrderDisplay();

				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setDate((String) objRow[2]);
				detailDisplayPacket.setLocationName((String) objRow[3]);
				detailDisplayPacket.setLocationId((String) objRow[4]);
				ans.add(detailDisplayPacket);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllRequestForAllocationForLocation/{locationId}/{requestname}/{startIndex}/{endIndex}")
	public String getAllRequestForAllocationForLocation(@PathParam("locationId") String locationId,
			@PathParam("requestname") String requestname, @PathParam("startIndex") int startIndex,
			@PathParam("endIndex") int endIndex, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			List<RequestOrderDisplay> ans = new ArrayList<RequestOrderDisplay>();

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			if (requestname == null || requestname.equals("null") || requestname.equals(null)
					|| requestname.equals("(null)")) {
				requestname = "";
			}

			String sql = " select ro.id,ro.name as requestName,ro.date, l.name as locationName,l.id as l_id "
					+ "from request_order ro " + "left join order_status os on ro.status_id=os.id "
					+ "left join locations l on l.id=ro.location_id where os.name in ('PO Created','PO Partially Received','Request Partially Processed')  ";
			if (locationId != null && !locationId.equals("0")) {
				sql += "  and ro.supplier_id = '" + locationId + "'";

			}

			sql += " and ro.location_id != '" + locationId + "' and ro.isPOOrder=1 and ro.name like '%" + requestname
					+ "%' ";

			sql += "   order by str_to_date(ro.date,'%Y-%m-%d')  limit " + startIndex + "," + endIndex;

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).getResultList();
			for (Object[] objRow : resultList) {
				RequestOrderDisplay detailDisplayPacket = new RequestOrderDisplay();

				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setDate((String) objRow[2]);
				detailDisplayPacket.setLocationName((String) objRow[3]);
				detailDisplayPacket.setLocationId((String) objRow[4]);
				ans.add(detailDisplayPacket);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllRequestCountForAllocation/{locationId}/{requestname}")
	public BigInteger getAllRequestCountForAllocation(@PathParam("locationId") String locationId,
			@PathParam("requestname") String requestname, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			List<RequestOrderDisplay> ans = new ArrayList<RequestOrderDisplay>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			if (requestname == null || requestname.equals("null") || requestname.equals(null)) {
				requestname = "";
			}

			String sql = " select ro.id,ro.name as requestName,ro.date, l.name as locationName,l.id as l_id from request_order ro  "
					+ " left join order_status os on ro.status_id=os.id "
					+ " left join locations l on l.id=ro.location_id where os.name in ('Request Sent','Request Partially processed')  ";
			if (locationId != null && !locationId.equals("0")) {
				sql += "  and location_id= '" + locationId + "'";

			}

			sql += " and ro.isPOOrder=0 and ro.name like '%" + requestname + "%' ";

			sql += "   order by str_to_date(ro.date,'%Y-%m-%d') ";

			logger.severe("sql================================================================"+sql);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).getResultList();
			for (Object[] objRow : resultList) {
				RequestOrderDisplay detailDisplayPacket = new RequestOrderDisplay();

				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setDate((String) objRow[2]);
				detailDisplayPacket.setLocationName((String) objRow[3]);
				detailDisplayPacket.setLocationId((String) objRow[4]);
				ans.add(detailDisplayPacket);
			}

			return BigInteger.valueOf(ans.size());

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllRequestCountForAllocationForLocation/{locationId}/{requestname}")
	public BigInteger getAllRequestCountForAllocationForLocation(@PathParam("locationId") String locationId,
			@PathParam("requestname") String requestname, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			List<RequestOrderDisplay> ans = new ArrayList<RequestOrderDisplay>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			if (requestname == null || requestname.equals("null") || requestname.equals(null)) {
				requestname = "";
			}

			String sql = " select ro.id,ro.name as requestName,ro.date, l.name as locationName,l.id as l_id from request_order ro  "
					+ " left join order_status os on ro.status_id=os.id "
					+ " left join locations l on l.id=ro.location_id where os.name in ('PO Created','PO Partially Received')  ";
			if (locationId != null && !locationId.equals("0")) {
				sql += "  and ro.supplier_id= '" + locationId + "'";

			}

			sql += " and ro.isPOOrder=1 and ro.name like '%" + requestname + "%' ";

			sql += "   order by str_to_date(ro.date,'%Y-%m-%d') ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).getResultList();
			for (Object[] objRow : resultList) {
				RequestOrderDisplay detailDisplayPacket = new RequestOrderDisplay();

				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setName((String) objRow[1]);
				detailDisplayPacket.setDate((String) objRow[2]);
				detailDisplayPacket.setLocationName((String) objRow[3]);
				detailDisplayPacket.setLocationId((String) objRow[4]);
				ans.add(detailDisplayPacket);
			}

			return BigInteger.valueOf(ans.size());

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/allotmentRequestOrder")
	public String allotmentRequestOrder(RequestOrderPacket requestOrderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, requestOrderPacket);
			tx = em.getTransaction();
			tx.begin();
			String json = new StoreForwardUtility().returnJsonPacket(requestOrderPacket, "RequestOrderPacket",
					httpRequest);
			RequestOrder requestOrder = new InventoryServiceBean().allotmentRequestOrder(httpRequest, em,
					requestOrderPacket.getRequestOrder(), requestOrderPacket, requestOrderPacket);
			tx.commit();
			
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, requestOrderPacket.getLocationId(),
					Integer.parseInt(requestOrderPacket.getMerchantId()));

			sendEmailForAddAlloment(requestOrder.getId(), requestOrder.getLocationId(), sessionId,
					requestOrder.getChallanNumber());

			return new JSONUtility(httpRequest).convertToJsonString(requestOrder);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/allotmentRequestOrderForIntraTransfer")
	public String allotmentRequestOrderForIntraTransfer(RequestOrderPacket requestOrderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, requestOrderPacket);
			tx = em.getTransaction();
			tx.begin();
			RequestOrder requestOrder = new InventoryServiceBean().allotmentRequestOrderForIntra(httpRequest, em,
					requestOrderPacket.getRequestOrder(), requestOrderPacket, requestOrderPacket);
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(requestOrderPacket, "RequestOrderPacket",
					httpRequest);

			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, requestOrderPacket.getLocationId(),
					Integer.parseInt(requestOrderPacket.getMerchantId()));

			sendEmailForAddAlloment(requestOrder.getId(), requestOrder.getLocationId(), sessionId,
					requestOrder.getChallanNumber());

			return new JSONUtility(httpRequest).convertToJsonString(requestOrder);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllPOReceivedByDateAndLocationId/{businessId}/{date}")
	public String getAllPOReceivedByDateAndLocationId(@PathParam("date") String date,
			@PathParam("businessId") int businessId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			List<RequestOrder> ans = new ArrayList<RequestOrder>();

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select distinct grn_number,ro.id ,ro.status_id,ro.date, ro.created, ro.updated"
					+ " from goods_receive_notes grn join request_order_detail_items g on g.id=grn.request_order_details_item_id "
					+ " join request_order ro on ro.id=g.request_id"
					+ " left join order_status os on ro.status_id=os.id where "
					+ " os.name in ('PO Partially Received','PO Received','Request In Process','Request Partially Processed') and "
					+ "grn.is_allotment=1 and grn.is_grn_close=0 and grn.status!='D' and "
					+ "ro.date = ? and ro.location_id = ? order by grn.grn_number";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, date).setParameter(2, businessId)
					.getResultList();
			for (Object[] objRow : resultList) {
				RequestOrder detailDisplayPacket = new RequestOrder();
				detailDisplayPacket.setChallanNumber((String) objRow[0]);
				detailDisplayPacket.setId((String) objRow[1]);
				detailDisplayPacket.setStatusId((String) objRow[2]);
				detailDisplayPacket.setDate((String) objRow[3]);
				detailDisplayPacket.setCreated((Timestamp) objRow[4]);
				OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,
						OrderStatus.class, detailDisplayPacket.getStatusId());
				if (orderStatus != null) {
					detailDisplayPacket.setLocalTime(
							new TimezoneTime().getLocationSpecificTimeToAdd(orderStatus.getLocationsId(), em));
				}
				detailDisplayPacket.setUpdated((Timestamp) objRow[5]);

				ans.add(detailDisplayPacket);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/directRequestAllocation")
	public String directRequestAllocation(RequestOrderPacket requestOrderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
	 
		EntityTransaction tx = null;
		RequestOrder requestOrder= null;
		
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, requestOrderPacket);
			tx = em.getTransaction();
			tx.begin();
			requestOrder = new InventoryServiceBean().directRequestAllocation(httpRequest, em,
					requestOrderPacket);
			tx.commit();
			requestOrderPacket.setRequestOrder(requestOrder);
			String json = new StoreForwardUtility().returnJsonPacket(requestOrderPacket, "RequestOrderPacket",
					httpRequest);
			
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, requestOrderPacket.getLocationId(),
					Integer.parseInt(requestOrderPacket.getMerchantId()));
		 	return new JSONUtility(httpRequest).convertToJsonString(requestOrder);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
//		
//		EntityManager em2 = null;
//		EntityTransaction tx2 = null;
//		try {
//			em2 = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
//			tx2 = em2.getTransaction();
//			tx2.begin();
//			GoodsReceiveNotesPacket goodsReceiveNotesPacket = createPacket(em2, requestOrderPacket, requestOrder);
//			logger.severe("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@goodsReceiveNotesPacket:- "+goodsReceiveNotesPacket);
//			goodsReceiveNotesPacket = new InventoryServiceBean().receiveRequestOrder(httpRequest,
//					em2, goodsReceiveNotesPacket,true);
//			tx2.commit();
//		 
//			 
//
//			return new JSONUtility(httpRequest).convertToJsonString(requestOrder);
//		} catch (Exception e) {
//			logger.severe(e);
//			// on error, if transaction active,
//			// rollback
//			if (tx2 != null && tx2.isActive()) {
//				tx2.rollback();
//			}
//			throw e;
//		} finally {
//			LocalSchemaEntityManager.getInstance().closeEntityManager(em2);
//		}
		

	}

	
	@POST
	@Path("/directRequestAllocationWithoutReceive")
	public String directRequestAllocationWithoutReceive(RequestOrderPacket requestOrderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
	 
		EntityTransaction tx = null;
		RequestOrder requestOrder= null;
		
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, requestOrderPacket);
			String json = new StoreForwardUtility().returnJsonPacket(requestOrderPacket, "RequestOrderPacket",
					httpRequest);
			tx = em.getTransaction();
			tx.begin();
			requestOrder = new InventoryServiceBean().directRequestAllocation(httpRequest, em,
					requestOrderPacket);
			tx.commit();
		
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, requestOrderPacket.getLocationId(),
					Integer.parseInt(requestOrderPacket.getMerchantId()));
		//	return new JSONUtility(httpRequest).convertToJsonString(requestOrder);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		
		EntityManager em2 = null;
		EntityTransaction tx2 = null;
		try {
			em2 = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			tx2 = em2.getTransaction();
			tx2.begin();
			GoodsReceiveNotesPacket goodsReceiveNotesPacket = createPacket(em2, requestOrderPacket, requestOrder);
			logger.severe("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@goodsReceiveNotesPacket:- "+goodsReceiveNotesPacket);
			goodsReceiveNotesPacket = new InventoryServiceBean().receiveRequestOrder(httpRequest,
					em2, goodsReceiveNotesPacket,true);
			tx2.commit();
		 
			 

			return new JSONUtility(httpRequest).convertToJsonString(requestOrder);
		} catch (Exception e) {
			logger.severe(e);
			// on error, if transaction active,
			// rollback
			if (tx2 != null && tx2.isActive()) {
				tx2.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em2);
		}
		

	}

	@POST
	@Path("/updateDirectRequestAllocation")
	public String updateDirectRequestAllocation(RequestOrderPacket requestOrderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			tx = em.getTransaction();
			em.getTransaction().begin();
			RequestOrder updatedRequestOrder = new InventoryServiceBean()
					.updateDirectRequestAllocationStatus(httpRequest, em, requestOrderPacket.getRequestOrder());
			em.getTransaction().commit();
			String json = new StoreForwardUtility().returnJsonPacket(requestOrderPacket, "RequestOrderPacket",
					httpRequest);

			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, requestOrderPacket.getLocationId(),
					Integer.parseInt(requestOrderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(updatedRequestOrder);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/cancelGoodsReceiveNotes")
	public String cancelGoodsReceiveNotes(GoodsReceiveNotesPacket goodsReceiveNotesPackets,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			tx = em.getTransaction();
			em.getTransaction().begin();
			List<GoodsReceiveNotes> goodsReceiveNotes = new InventoryServiceBean().cancelGoodsReceiveNotes(httpRequest,
					em, goodsReceiveNotesPackets);
			em.getTransaction().commit();
//			goodsReceiveNotesPackets.setGoodsReceiveNotesList(goodsReceiveNotes);
			String json = new StoreForwardUtility().returnJsonPacket(goodsReceiveNotesPackets,
					"GoodsReceiveNotesPacket", httpRequest);

			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					goodsReceiveNotesPackets.getLocationId(),
					Integer.parseInt(goodsReceiveNotesPackets.getMerchantId()));
			

			return new JSONUtility(httpRequest).convertToJsonString(goodsReceiveNotes);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllPOReceivedByLocationIdAndDate/{businessId}/{date}/{startIndex}/{endIndex}")
	public String getAllPOReceivedByLocationIdAndDate(@PathParam("businessId") int businessId,
			@PathParam("date") String date, @PathParam("startIndex") int startIndex,
			@PathParam("endIndex") int endIndex, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			List<RequestOrderDisplay> ans = new ArrayList<RequestOrderDisplay>();

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select distinct grn_number,ro.id as request_order_id,grn.date as grn_date,l.name as location_date,l.id as location_id"
					+ " from goods_receive_notes grn join request_order_detail_items g on g.id=grn.request_order_details_item_id "
					+ " join request_order ro on ro.id=g.request_id" + " left join locations l on l.id=ro.location_id "
					+ " left join order_status os on ro.status_id=os.id where "
					+ " os.name in ('PO Partially Received','PO Received','Request In Process','Request Partially Processed') ";
			if (businessId > 0) {
				sql += " and ro.location_id = " + businessId;
			}
			sql += " and grn.date = '" + date
					+ "' and grn.is_allotment=1 and grn.is_grn_close=0 and grn.status != 'D'  order by grn.grn_number limit "
					+ startIndex + "," + endIndex;
			// Request Allocated removed from list and Request In Process

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).getResultList();
			for (Object[] objRow : resultList) {
				RequestOrderDisplay detailDisplayPacket = new RequestOrderDisplay();
				detailDisplayPacket.setGrnNumber((String) objRow[0]);
				detailDisplayPacket.setId((String) objRow[1]);
				detailDisplayPacket.setDate((String) objRow[2]);
				detailDisplayPacket.setLocationName((String) objRow[3]);
				;
				detailDisplayPacket.setLocationId((String) objRow[4]);

				ans.add(detailDisplayPacket);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllPOReceivedByLocationIdAndDateForLocations/{businessId}/{date}/{startIndex}/{endIndex}")
	public String getAllPOReceivedByLocationIdAndDateForLocations(@PathParam("businessId") int businessId,
			@PathParam("date") String date, @PathParam("startIndex") int startIndex,
			@PathParam("endIndex") int endIndex, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			List<RequestOrderDisplay> ans = new ArrayList<RequestOrderDisplay>();

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select distinct grn_number,ro.id as request_order_id,grn.date as grn_date,l.name as location_date,l.id as location_id"
					+ " from goods_receive_notes grn join request_order_detail_items g on g.id=grn.request_order_details_item_id "
					+ " join request_order ro on ro.id=g.request_id" + " left join locations l on l.id=ro.location_id "
					+ " left join order_status os on ro.status_id=os.id where "
					+ " os.name in ('PO Sent','PO Partially Received') ";
			if (businessId > 0) {
				sql += " and ro.supplier_id = " + businessId;
			}
			sql += " and grn.date = '" + date
					+ "' and grn.is_allotment=1 and grn.is_grn_close=0 and grn.status != 'D'  order by grn.grn_number limit "
					+ startIndex + "," + endIndex;
			// Request Allocated removed from list and Request In Process

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).getResultList();
			for (Object[] objRow : resultList) {
				RequestOrderDisplay detailDisplayPacket = new RequestOrderDisplay();
				detailDisplayPacket.setGrnNumber((String) objRow[0]);
				detailDisplayPacket.setId((String) objRow[1]);
				detailDisplayPacket.setDate((String) objRow[2]);
				detailDisplayPacket.setLocationName((String) objRow[3]);
				;
				detailDisplayPacket.setLocationId((String) objRow[4]);

				ans.add(detailDisplayPacket);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllPOReceivedCountByLocationIdAndDate/{businessId}/{date}")
	public int getAllPOReceivedCountByLocationIdAndDate(@PathParam("businessId") int businessId,
			@PathParam("date") String date, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select distinct grn_number,ro.id as request_order_id,grn.date as grn_date,l.name as location_date,l.id as location_id"
					+ " from goods_receive_notes grn join request_order_detail_items g on g.id=grn.request_order_details_item_id "
					+ " join request_order ro on ro.id=g.request_id" + " left join locations l on l.id=ro.location_id "
					+ " left join order_status os on ro.status_id=os.id where "
					+ " os.name in ('PO Partially Received','PO Received','Request In Process','Request Partially Processed') ";
			if (businessId > 0) {
				sql += " and ro.location_id = " + businessId;
			}
			sql += " and grn.date = '" + date
					+ "' and grn.is_allotment=1 and grn.is_grn_close=0 and grn.status != 'D'  order by grn.grn_number";
			// Request Allocated removed from list and Request In Process
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).getResultList();
			return resultList.size();

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllPOReceivedCountByLocationIdAndDateForLocations/{businessId}/{date}")
	public int getAllPOReceivedCountByLocationIdAndDateForLocations(@PathParam("businessId") int businessId,
			@PathParam("date") String date, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select distinct grn_number,ro.id as request_order_id,grn.date as grn_date,l.name as location_date,l.id as location_id"
					+ " from goods_receive_notes grn join request_order_detail_items g on g.id=grn.request_order_details_item_id "
					+ " join request_order ro on ro.id=g.request_id" + " left join locations l on l.id=ro.location_id "
					+ " left join order_status os on ro.status_id=os.id where "
					+ " os.name in ('PO Sent','PO Partially Received') ";
			if (businessId > 0) {
				sql += " and ro.supplier_id = " + businessId;
			}
			sql += " and grn.date = '" + date
					+ "' and grn.is_allotment=1 and grn.is_grn_close=0 and grn.status != 'D'  order by grn.grn_number";
			// Request Allocated removed from list and Request In Process
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).getResultList();
			return resultList.size();

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/cancelRequestOrderDetailItems")
	public String cancelRequestOrderDetailItems(RequestOrderDetailItemsPacket requestOrderDetailItemsPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			tx = em.getTransaction();
			em.getTransaction().begin();
			RequestOrderDetailItems orderDetailItems = new InventoryServiceBean().cancelRequestOrderDetailItems(
					httpRequest, em, requestOrderDetailItemsPacket.getRequestOrderDetailItems().getId());
			em.getTransaction().commit();
			return new JSONUtility(httpRequest).convertToJsonString(orderDetailItems);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	public UnitConversion getUnitConversionByFromIdAndToId(EntityManager em, String fromUOMId, String toUOMId,
			NirvanaLogger logger) {

		try {
			String queryString = "select l from UnitConversion l where l.fromUOMId=? and l.toUOMId=? and l.status not in ('I','D') ";
			TypedQuery<UnitConversion> query = em.createQuery(queryString, UnitConversion.class)
					.setParameter(1, fromUOMId).setParameter(2, toUOMId);
			return query.getSingleResult();
		} catch (Exception e) {

			logger.severe(e);
		}
		return null;
	}

	public BigDecimal convertUnit(String fromUOMId, String toUOMId, NirvanaLogger logger, BigDecimal sellable,
			EntityManager em) {

		UnitConversion conversion = getUnitConversionByFromIdAndToId(em, fromUOMId, toUOMId, logger);

		BigDecimal conversionAmount = null;

		if (conversion != null) {
			conversionAmount = sellable.multiply(conversion.getConversionRatio());
			
		} else {
			// find in reverse order toUOMID to FromUOMID
			conversion = getUnitConversionByFromIdAndToId(em, toUOMId, fromUOMId, logger);
			if (conversion != null) {
				// conversionAmount = sellable.multiply(new
				// BigDecimal(1).divide(conversion.getConversionRatio(),2,BigDecimal.ROUND_HALF_DOWN));
				double temp = 1 / conversion.getConversionRatio().doubleValue();
				conversionAmount = new BigDecimal(sellable.doubleValue() * temp);
			}

		}
			 if(conversionAmount!=null){
				 return	conversionAmount.setScale(2, conversionAmount.ROUND_HALF_UP);
			}else {
				return null;
			}
	}

	@GET
	@Path("/getUOMByLocationIdAndName/{locationId}/{name}")
	public String getUOMByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UnitOfMeasurement> criteria = builder.createQuery(UnitOfMeasurement.class);
			Root<UnitOfMeasurement> root = criteria.from(UnitOfMeasurement.class);
			TypedQuery<UnitOfMeasurement> query = em.createQuery(
					criteria.select(root).where(builder.equal(root.get(UnitOfMeasurement_.locationId), locationId),
							builder.equal(root.get(UnitOfMeasurement_.name), name),
							builder.notEqual(root.get(UnitOfMeasurement_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/*
	 * @GET
	 * 
	 * @Path("/getAllReceivedByDateAndLocationId/{date}/{businessId}") public
	 * String getAllReceivedByDateAndLocationId(@PathParam("date") String
	 * date,@PathParam("businessId") int
	 * businessId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
	 * throws Exception { EntityManager em = null; try { List<RequestOrder> ans
	 * = new ArrayList<RequestOrder>();
	 * 
	 * em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest,
	 * sessionId); String sql
	 * ="select grn_number,ro.id ,ro.status_id,ro.date, ro.created, ro.updated"
	 * +
	 * " from goods_receive_notes grn join request_order_detail_items g on g.id=grn.request_order_details_item_id "
	 * +" join request_order ro on ro.id=g.request_id"
	 * +" left join order_status os on ro.status_id=os.id where "
	 * +" os.name in ('PO Partially Received','PO Received','Request In Process','Request Partially Processed') and "
	 * + " grn.is_grn_close=0 and grn.status!='D' and " +
	 * " ro.date = ? and ro.location_id = ? order by grn.grn_number";
	 * 
	 * @SuppressWarnings("unchecked") List<Object[]> resultList =
	 * em.createNativeQuery(sql).setParameter(1, date).setParameter(2,
	 * businessId).getResultList(); for (Object[] objRow : resultList) {
	 * RequestOrder detailDisplayPacket = new RequestOrder();
	 * detailDisplayPacket.setChallanNumber((String) objRow[0]);
	 * detailDisplayPacket.setId((int) objRow[1]);
	 * detailDisplayPacket.setStatusId((int) objRow[2]);
	 * detailDisplayPacket.setDate((String) objRow[3]);
	 * detailDisplayPacket.setCreated((Timestamp) objRow[4]);
	 * detailDisplayPacket.setUpdated((Timestamp) objRow[5]);
	 * 
	 * 
	 * ans.add(detailDisplayPacket); } return new
	 * JSONUtility(httpRequest).convertToJsonString(ans);
	 * 
	 * } finally {
	 * LocalSchemaEntityManager.getInstance().closeEntityManager(em); }
	 * 
	 * }
	 */
	@POST
	@Path("/getAllReceivedByDateAndLocationId")
	public String getAllReceivedByDateAndLocationId(RequestOrderGetPacket requestOrderGetPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		String supplierId = requestOrderGetPacket.getSupplierId();
		String poNumber = requestOrderGetPacket.getPoNumber();
		String businessId = requestOrderGetPacket.getBusinessId();
		try {
			List<RequestOrderDisplay> ans = new ArrayList<RequestOrderDisplay>();
			if (supplierId == null || supplierId.equals("null") || supplierId.equals(null)) {
				supplierId = "";
			}
			if (poNumber == null || poNumber.equals("null") || poNumber.equals(null)) {
				poNumber = "";
			}

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = " select ro.id,ro.location_id,ro.created, l.name  from request_order ro  "
					+ " left join order_status os on ro.status_id=os.id "
					+ " left join locations l on l.id=ro.supplier_id  where location_id = ? ";
			if (supplierId != null && supplierId.length() > 0) {
				sql += " and supplier_id in (" + supplierId + ") ";
			}

			sql += " and ro.date ='" + requestOrderGetPacket.getDate() + "' and ro.id like '%" + poNumber
					+ "%'  and os.name in ('PO Created','PO Sent','PO Partially Received')"
					+ "ORDER BY ro.date DESC limit " + requestOrderGetPacket.getStartIndex() + ","
					+ requestOrderGetPacket.getEndIndex();

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, businessId).getResultList();

			TimezoneTime timezoneTime = new TimezoneTime();
			for (Object[] objRow : resultList) {
				RequestOrderDisplay detailDisplayPacket = new RequestOrderDisplay();

				detailDisplayPacket.setId((String) objRow[0]);
				String locationdId = (String) objRow[1];
				detailDisplayPacket.setDate(timezoneTime.getDateFromTimeStamp((Timestamp) objRow[2]));
				detailDisplayPacket.setSupplierName((String) objRow[3]);
				detailDisplayPacket.setPoNumber(detailDisplayPacket.getId() + "");

				ans.add(detailDisplayPacket);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/getAllReceivedByDateAndLocationIdForDirectSupplier")
	public String getAllReceivedByDateAndLocationIdForDirectSupplier(RequestOrderGetPacket requestOrderGetPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		String supplierId = requestOrderGetPacket.getSupplierId();
		String poNumber = requestOrderGetPacket.getPoNumber();
		String businessId = requestOrderGetPacket.getBusinessId();
		try {
			List<RequestOrderDisplay> ans = new ArrayList<RequestOrderDisplay>();
			if (supplierId == null || supplierId.equals("null") || supplierId.equals(null)) {
				supplierId = "";
			}
			if (poNumber == null || poNumber.equals("null") || poNumber.equals(null)) {
				poNumber = "";
			}

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = " select ro.id,ro.location_id,ro.created, l.name,l.locations_type_id, ro.grn_count,ro.supplier_id  from request_order ro  "
					+ " left join order_status os on ro.status_id=os.id "
					+ " left join locations l on l.id=ro.supplier_id  where location_id = '"+businessId+"'";
			if (supplierId != null && supplierId.length() > 0) {
				sql += " and supplier_id in ('" + supplierId + "') ";
			}

			sql += " and ro.date ='" + requestOrderGetPacket.getDate() + "' and ro.id like '%" + poNumber
					+ "%'  and os.name in ('PO Created','PO Sent','PO Partially Received')"
					+ "ORDER BY ro.date DESC limit " + requestOrderGetPacket.getStartIndex() + ","
					+ requestOrderGetPacket.getEndIndex();

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).getResultList();

			TimezoneTime timezoneTime = new TimezoneTime();
			for (Object[] objRow : resultList) {
				RequestOrderDisplay detailDisplayPacket = new RequestOrderDisplay();

				detailDisplayPacket.setId((String) objRow[0]);
				detailDisplayPacket.setDate(timezoneTime.getDateFromTimeStamp((Timestamp) objRow[2]));
				detailDisplayPacket.setSupplierName((String) objRow[3]);
				detailDisplayPacket.setPoNumber(detailDisplayPacket.getId() + "");

				if (objRow[4] != null) {
					detailDisplayPacket.setSupplierTypeId((int) objRow[4]);
				}

				if (/* (int) objRow[1] == (int) objRow[6] || */ (String) objRow[6] == null) {
					ans.add(detailDisplayPacket);
				} else if ((int) objRow[4] == 3 || (int) objRow[4] == 4) {
					ans.add(detailDisplayPacket);
				}
				// ans.add(detailDisplayPacket);
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllReceivedByDateAndLocationIdForIntra/{date}/{businessId}")
	public String getAllReceivedByDateAndLocationIdForIntra(@PathParam("date") String date,
			@PathParam("businessId") String businessId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {

		EntityManager em = null;
		try {
			List<RequestOrder> ans = new ArrayList<RequestOrder>();

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			/*
			 * String sql =
			 * " select ro.id,ro.location_id,ro.created, l.name,l.locations_type_id, ro.grn_count,ro.supplier_id  from request_order ro  "
			 * + " left join order_status os on ro.status_id=os.id " +
			 * " left join locations l on l.id=ro.supplier_id  where location_id = ? "
			 * ; if (supplierId != null && supplierId.length() > 0) { sql +=
			 * " and supplier_id in (" + supplierId + ") "; }
			 * 
			 * sql += " and ro.date ='" +requestOrderGetPacket.getDate()+
			 * "' and ro.id like '%" + poNumber +
			 * "%'  and os.name in ('PO Created','PO Sent','PO Partially Received')"
			 * + "ORDER BY ro.date DESC limit "+requestOrderGetPacket.
			 * getStartIndex()+","+requestOrderGetPacket.getEndIndex();
			 * 
			 */

			String sql = " select distinct grn_number,ro.id ,ro.status_id,ro.date,ro.created,ro.updated "
					+ " from goods_receive_notes grn join request_order_detail_items g on g.id=grn.request_order_details_item_id "
					+ " join request_order ro on ro.id=g.request_id"
					+ " left join order_status os on ro.status_id=os.id where "
					+ " os.name in ('PO Partially Received','PO Received','Request In Process','Request Partially Processed') and "
					+ " ro.date ='" + date
					+ "' and grn.is_allotment=1 and grn.is_grn_close=0 and grn.status!='D' and ro.location_id = '"
					+ businessId + "' order by grn.grn_number";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).getResultList();

			for (Object[] objRow : resultList) {
				RequestOrder detailDisplayPacket = new RequestOrder();
				detailDisplayPacket.setChallanNumber((String) objRow[0]);
				detailDisplayPacket.setId((String) objRow[1]);
				detailDisplayPacket.setStatusId((String) objRow[2]);
				detailDisplayPacket.setDate((String) objRow[3]);
				detailDisplayPacket.setCreated((Timestamp) objRow[4]);

				OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,
						OrderStatus.class, detailDisplayPacket.getStatusId());
				if (orderStatus != null) {
					detailDisplayPacket.setLocalTime(
							new TimezoneTime().getLocationSpecificTimeToAdd(orderStatus.getLocationsId(), em));
				}

				detailDisplayPacket.setUpdated((Timestamp) objRow[5]);

				ans.add(detailDisplayPacket);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllPendingPOByLocationId/{locationId}")
	public String getAllPendingPOByLocationId(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<RequestOrder> requestOrderList = new InventoryServiceBean().getAllPendingPO(em, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(requestOrderList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllPendingPOByLocationIdAndDate/{locationId}/{date}")
	public String getAllPendingPOByLocationIdAndDate(@PathParam("locationId") String locationId,
			@PathParam("date") String date, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException, InvalidSessionException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<RequestOrder> requestOrderList = new InventoryServiceBean().getAllPendingPOByDate(em, locationId,
					date);
			return new JSONUtility(httpRequest).convertToJsonString(requestOrderList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/getAllIngredientByItemIdAndAttributeId")
	public String getAllIngredientByItemIdAndAttributeId(
			ItemAttributeIngrediantGetPacket itemAttributeIngrediantGetPacket)
			throws IOException, InvalidSessionException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			ItemAttributeIngrediantDisplayPacket itemAttributeIngrediantDisplayPacket = new InventoryServiceBean()
					.getAllIngredientByItemIdAndAttributeId(em, itemAttributeIngrediantGetPacket);
			return new JSONUtility(httpRequest).convertToJsonString(itemAttributeIngrediantDisplayPacket);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getPDFInHTMLFormatForReceivedPO/{grnRef}/{locationId}")
	public String getPDFInHTMLFormatForReceivedPO(@PathParam(value = "grnRef") String grnRef,
			@PathParam(value = "locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		PDFInHTMLFormatPacket htmlFormatPacket = new PDFInHTMLFormatPacket();
		// String fileString = "";

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			// RequestOrder requestOrderheader = (RequestOrder) new
			// CommonMethods().getObjectById("RequestOrder",
			// em,RequestOrder.class, requestOrderId);

			try {
				tx = em.getTransaction();
				tx.begin();

				Location foundLocation = null;
				if (locationId != null && !locationId.equals("0")) {
					String queryString = "select l from Location l where " + "l.id ='" + locationId
							+ "' and (l.locationsId = '0' or l.locationsId is null) and l.locationsTypeId = '1'";
					TypedQuery<Location> query = em.createQuery(queryString, Location.class);
					foundLocation = query.getSingleResult();
				}

				htmlFormatPacket.setFileName(foundLocation.getName() + " Purchase Order.pdf");

				String pdfString = receiptPDFFormat
						.createRequestOrderInvoicePDFString(em, httpRequest, null, locationId, 2, grnRef).toString();
				pdfString = pdfString.replace("\"", "&quot;");

				String footer = receiptPDFFormat.createRequestOrderInvoiceFooterString(em, locationId).toString()
						.replace("\"", "&quot;");
				htmlFormatPacket.setEmailFooter(footer);

				pdfString = pdfString.replace("</body>", footer + "</body>");

				htmlFormatPacket.setPdfData(pdfString);
				htmlFormatPacket.setEmailBody(receiptPDFFormat.createRequestOrderInvoiceBodyString().toString());
				// fileString =
				// EmailTemplateKeys.getPDFForRequestOrderConfirmation(httpRequest,
				// em, locationId, requestOrderheader.getUpdatedBy(), pdfData,
				// EmailTemplateKeys.REQUEST_ORDER_CONFIRMATION,
				// requestOrderheader.getId(), fileName, emailBody,
				// emailFooter);

				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			return new JSONUtility(httpRequest).convertToJsonString(htmlFormatPacket);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getPDFInHTMLFormatForOldPO/{grnRef}/{locationId}")
	public String getPDFInHTMLFormatForOldPO(@PathParam(value = "grnRef") String grnRef,
			@PathParam(value = "locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		PDFInHTMLFormatPacket htmlFormatPacket = new PDFInHTMLFormatPacket();
		// String fileString = "";

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			// RequestOrder requestOrderheader = (RequestOrder) new
			// CommonMethods().getObjectById("RequestOrder",
			// em,RequestOrder.class, requestOrderId);

			try {
				tx = em.getTransaction();
				tx.begin();

				Location foundLocation = null;
				if (locationId != null && !locationId.equals("0")) {
					String queryString = "select l from Location l where " + "l.id ='" + locationId
							+ "' and (l.locationsId = '0' or l.locationsId is null) and l.locationsTypeId = '1'";
					TypedQuery<Location> query = em.createQuery(queryString, Location.class);
					foundLocation = query.getSingleResult();
				}

				htmlFormatPacket.setFileName(foundLocation.getName() + " Purchase Order.pdf");

				String pdfString = receiptPDFFormat
						.createRequestOrderInvoicePDFString(em, httpRequest, null, locationId, 4, grnRef).toString();
				pdfString = pdfString.replace("\"", "&quot;");

				String footer = receiptPDFFormat.createRequestOrderInvoiceFooterString(em, locationId).toString()
						.replace("\"", "&quot;");
				htmlFormatPacket.setEmailFooter(footer);

				pdfString = pdfString.replace("</body>", footer + "</body>");

				htmlFormatPacket.setPdfData(pdfString);
				htmlFormatPacket.setEmailBody(receiptPDFFormat.createRequestOrderInvoiceBodyString().toString());
				// fileString =
				// EmailTemplateKeys.getPDFForRequestOrderConfirmation(httpRequest,
				// em, locationId, requestOrderheader.getUpdatedBy(), pdfData,
				// EmailTemplateKeys.REQUEST_ORDER_CONFIRMATION,
				// requestOrderheader.getId(), fileName, emailBody,
				// emailFooter);

				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			return new JSONUtility(httpRequest).convertToJsonString(htmlFormatPacket);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllPOByLocationIdAndToAndFromDate/{businessId}/{fromDate}/{toDate}/{startIndex}/{endIndex}")
	public String getAllPOByLocationIdAndToAndFromDate(@PathParam("businessId") int businessId,
			@PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate,
			@PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			List<RequestOrderDisplay> ans = new ArrayList<RequestOrderDisplay>();

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select distinct grn_number,ro.id as request_order_id,grn.date as grn_date,l.name as location_date,l.id as location_id"
					+ " from goods_receive_notes grn join request_order_detail_items g on g.id=grn.request_order_details_item_id "
					+ " join request_order ro on ro.id=g.request_id" + " left join locations l on l.id=ro.location_id ";
			// +" left join order_status os on ro.status_id=os.id where "
			// +" os.name in ('PO Partially Received','PO Received','Request In
			// Process','Request Partially Processed') ";
			if (businessId > 0) {
				sql += " and ro.location_id = " + businessId;
			}
			sql += " and grn.date between  '" + fromDate + "'  and  '" + toDate
					+ "'  and grn.is_allotment=1 and grn.is_grn_close=0 and grn.status != 'D'  order by grn.grn_number limit "
					+ startIndex + "," + endIndex;
			// Request Allocated removed from list and Request In Process

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).getResultList();
			for (Object[] objRow : resultList) {
				RequestOrderDisplay detailDisplayPacket = new RequestOrderDisplay();
				detailDisplayPacket.setGrnNumber((String) objRow[0]);
				detailDisplayPacket.setId((String) objRow[1]);
				detailDisplayPacket.setDate((String) objRow[2]);
				detailDisplayPacket.setLocationName((String) objRow[3]);
				;
				detailDisplayPacket.setLocationId((String) objRow[4]);

				ans.add(detailDisplayPacket);
			}
			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllPOCountByLocationIdAndToAndFromDate/{businessId}/{fromDate}/{toDate}")
	public int getAllPOCountByLocationIdAndToAndFromDate(@PathParam("businessId") int businessId,
			@PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			List<RequestOrderDisplay> ans = new ArrayList<RequestOrderDisplay>();

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select distinct grn_number,ro.id as request_order_id,grn.date as grn_date,l.name as location_date,l.id as location_id"
					+ " from goods_receive_notes grn join request_order_detail_items g on g.id=grn.request_order_details_item_id "
					+ " join request_order ro on ro.id=g.request_id" + " left join locations l on l.id=ro.location_id ";
			// +" left join order_status os on ro.status_id=os.id where "
			// +" os.name in ('PO Partially Received','PO Received','Request In
			// Process','Request Partially Processed') ";
			if (businessId > 0) {
				sql += " and ro.location_id = " + businessId;
			}
			sql += " and grn.date between  '" + fromDate + "'  and  '" + toDate
					+ "'  and grn.is_allotment=1 and grn.is_grn_close=0 and grn.status != 'D' ";
			// Request Allocated removed from list and Request In Process

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).getResultList();
			return resultList.size();

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	 
	
	private GoodsReceiveNotesPacket createPacket(EntityManager em,RequestOrderPacket requestOrderPacket, RequestOrder ro) {
		GoodsReceiveNotesPacket  pac= new GoodsReceiveNotesPacket();
		pac.setMerchantId(requestOrderPacket.getMerchantId());
		pac.setLocationId(requestOrderPacket.getLocationId());
		pac.setLocalServerURL(requestOrderPacket.getLocalServerURL());
		pac.setSessionId(requestOrderPacket.getSessionId());

		InventoryServiceBean r= new InventoryServiceBean();
		List<GoodsReceiveNotes> grn = r.getGoodsReceiveNotesByNumber(em, ro.getChallanNumber());
		 
		pac.setGoodsReceiveNotesList(grn);
		return pac;
	}
	

}
