/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
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

import com.nirvanaxp.common.utils.Utilities;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.CatalogPacket;
import com.nirvanaxp.services.jaxrs.packets.CategoryIdPacket;
import com.nirvanaxp.services.jaxrs.packets.GetRawMaterialCategoryPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.catalog.category.Category;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
import com.nirvanaxp.types.entities.custom.AttributeDisplayService;
import com.nirvanaxp.types.entities.custom.CatalogDisplayService;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

/**
 * @author NirvanaXP
 *
 */
/**
 * @author NirvanaXP
 *
 */
@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class CatalogService extends AbstractNirvanaService {

	@Context
	HttpServletRequest httpRequest;

	private static final NirvanaLogger logger = new NirvanaLogger(CatalogService.class.getName());

	/**
	 * This method is used to get root categories from that schema
	 * 
	 * @return - root categories list
	 * @throws Exception
	 */
	@GET
	@Path("/getRootCategories")
	public String getRootCategories(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<Category> categoriesList = new CatalogServiceBean().getRootCategories(em);
			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get all sub categories by category id
	 * 
	 * @param categoryId
	 * @return - sub categories list
	 * @throws Exception
	 */
	@GET
	@Path("/getCategoriesByCategoryId/{categoryId}")
	public String getCategoriesByCategoryId(@PathParam("categoryId") String categoryId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<Category> categoriesList = null;
			categoriesList = new CatalogServiceBean().getCategoriesByCategoryId(em, categoryId);
			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		}
		// TODO uzma - handle exception for sql query
		finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get all sub categories by category id and location
	 * id
	 * 
	 * @param categoryId
	 * @param locationId
	 * @return - sub categories list
	 * @throws Exception
	 */
	@GET
	@Path("/getCategoriesByCategoryIdAndLocationId/{categoryId}/{locationId}")
	public String getCategoriesByCategoryIdAndLocationId(@PathParam("categoryId") String categoryId,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<Category> categoriesList = null;
			categoriesList = new CatalogServiceBean().getCategoriesByCategoryIdAndLocationId(em, categoryId,
					locationId);
			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get root categories by location id
	 * 
	 * @param locationId
	 * @param null
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getRootCategoriesByLocationId/{locationId}")
	public String getRootCategoriesByLocationId(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<Category> categoriesList = null;
			categoriesList = new CatalogServiceBean().getRootCategoriesByLocationId(em, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get root category by location id and name of
	 * category for category name validation
	 * 
	 * @param locationId
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getRootCategoriesByLocationIdAndName/{locationId}/{name}")
	public String getRootCategoriesByLocationIdAndName(@PathParam("locationId") String locationId,
			@PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			Category category = new CatalogServiceBean().getRootCategoriesByLocationIdAndName(em, locationId, name);
			return new JSONUtility(httpRequest).convertToJsonString(category);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get all categories (root & sub categories) by
	 * location id
	 * 
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getCategoriesByLocationId/{locationId}")
	public String getCategoriesByLocationId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("locationId") String locationId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<Category> categoriesList = new CatalogServiceBean().getCategoriesByLocationId(em, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get All Categories And Item By Category Id And
	 * Location Id
	 * 
	 * @param categoryId
	 * @param locationId
	 * @return - list of category display packet
	 * @throws Exception
	 */
	@GET
	@Path("/getAllCategoriesAndItemByCategoryIdAndLocationId/{categoryId}/{locationId}")
	public String getAllCategoriesAndItemByCategoryId(@PathParam("categoryId") String categoryId,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			List<CatalogDisplayService> ans = new CatalogServiceBean().getAllCategoriesAndItemByCategoryId(em,
					categoryId, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} // TODO uzma - handle exception for sql query
		finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get All Categories And Item For Customer By
	 * Category Id And Location Id
	 * 
	 * @param categoryId
	 * @param locationId
	 * @return - list of category display packet
	 * @throws Exception
	 */
	@GET
	@Path("/getAllCategoriesAndItemForCustomerByCategoryIdAndLocationId/{categoryId}/{locationId}")
	public String getAllCategoriesAndItemForCustomerByCategoryIdAndLocationId(@PathParam("categoryId") String categoryId,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			List<CatalogDisplayService> ans = new CatalogServiceBean()
					.getAllCategoriesAndItemForCustomerByCategoryIdAndLocationId(em, categoryId, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(ans);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get Root Categories By Location Id And Display
	 * Sequence for sequence validation
	 * 
	 * @param locationId
	 * @param displaySequence
	 * @return- list of category
	 * @throws Exception
	 */
	@GET
	@Path("/getRootCategoriesByLocationIdAndDisplaySequence/{locationId}/{displaySequence}")
	public String getRootCategoriesByLocationIdAndDisplaySequence(@PathParam("locationId") String locationId,
			@PathParam("displaySequence") int displaySequence,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			Category category = new CatalogServiceBean().getRootCategoriesByLocationIdAndDisplaySequence(em, locationId,
					displaySequence);
			return new JSONUtility(httpRequest).convertToJsonString(category);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get sub category by category id
	 * 
	 * @param categoryId
	 * @return - list of category
	 * @throws Exception
	 */
	@GET
	@Path("/getSubCategories/{categoryId}")
	public String getSubCategories(@PathParam("categoryId") String categoryId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<Category> categoriesList = new CatalogServiceBean().getSubCategories(em, categoryId);
			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get all items for category id
	 * 
	 * @param categoryId
	 * @return - list of item
	 * @throws Exception
	 */
	@GET
	@Path("/getAllItemsForCategory/{categoryId}")
	public String getAllItemsForCategory(@PathParam("categoryId") String categoryId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<Item> itemList = new CatalogServiceBean().getAllItemsForCategory(em, categoryId);
			return new JSONUtility(httpRequest).convertToJsonString(itemList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get category by id
	 * 
	 * @param id
	 * @return - category object
	 * @throws Exception
	 */
	@GET
	@Path("/getCategoriesById/{id}")
	public String getCategoriesById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			Category p = new CatalogServiceBean().getCategoriesById(em, id);

			// get location list to which global category is attached
			// TODO uzma - handle if location dosen't attached to category
			String queryString = "select l from Location l where l.id in  (select p.locationsId from Category p where p.globalCategoryId=? and p.status!='D') ";
			TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, p.getId());
			List<Location> resultSet = query2.getResultList();
			p.setLocationList(resultSet);
			return new JSONUtility(httpRequest).convertToJsonString(p);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get Category By Id And Location Id
	 * 
	 * @param id
	 * @param locationId
	 * @return - categpory object
	 * @throws Exception
	 */
	@GET
	@Path("/getCategoryByIdAndLocationId/{id}/{locationId}")
	public String getCategoriesByIdAndLocationId(@PathParam("id") String id, @PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			Category category = new CatalogServiceBean().getCategoriesByIdAndLocationId(em, id, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(category);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get sub categories by location id and category id
	 * 
	 * @param locationId
	 * @param categoryId
	 * @param schemaName
	 * @return - list of category
	 * @throws Exception
	 */
	@GET
	@Path("/getCategoriesByLocationIdAndCategoryId/{locationId}/{categoryId}")
	public String getCategoriesByLocationIdAndCategoryId(@PathParam("locationId") String locationId,
			@PathParam("categoryId") String categoryId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@CookieParam(value = "SchemaName") String schemaName) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForCustomer(httpRequest, sessionId, schemaName);
			List<Category> categoriesList = new CatalogServiceBean().getCategoriesByLocationIdAndCategoryId(em,
					locationId, categoryId);
			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get Category By Location Id And Root Category Id
	 * And Name for sub category name validation
	 * 
	 * @param locationId
	 * @param categoryId
	 * @param name
	 * @return - category object
	 * @throws Exception
	 */
	@GET
	@Path("/getCategoryByLocationIdAndRootCategoryIdAndName/{locationId}/{categoryId}/{name}")
	public String getCategoryByLocationIdAndRootCategoryIdAndName(@PathParam("locationId") String locationId,
			@PathParam("categoryId") String categoryId, @PathParam("name") String name,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			Category category = new CatalogServiceBean().getCategoryByLocationIdAndRootCategoryIdAndName(em, locationId,
					categoryId, name);
			return new JSONUtility(httpRequest).convertToJsonString(category);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get Category By Location Id And Root Category Id
	 * And Display Sequence
	 * 
	 * @param locationId
	 * @param categoryId
	 * @param displaySequence
	 * @return - category object
	 * @throws Exception
	 */
	@GET
	@Path("/getCategoryByLocationIdAndRootCategoryIdAndDisplaySequence/{locationId}/{categoryId}/{displaySequence}")
	public String getCategoryByLocationIdAndRootCategoryIdAndDisplaySequence(@PathParam("locationId") String locationId,
			@PathParam("categoryId") String categoryId, @PathParam("displaySequence") String displaySequence,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			Category category = new CatalogServiceBean().getCategoryByLocationIdAndRootCategoryIdAndDisplaySequence(em,
					locationId, categoryId, displaySequence);
			return new JSONUtility(httpRequest).convertToJsonString(category);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to add category
	 * 
	 * @param catalogPacket
	 * @return - category object
	 * @throws Exception
	 */
	@POST
	@Path("/add")
	public String add(CatalogPacket catalogPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, catalogPacket);
			tx = em.getTransaction();
			tx.begin();
			String json = new StoreForwardUtility().returnJsonPacket(catalogPacket, "CatalogPacket",httpRequest);
			Category result = new CatalogServiceBean().add(em, catalogPacket);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.CatalogService_add.name(), catalogPacket);
			catalogPacket.setCategory(result);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					catalogPacket.getLocationId(), Integer.parseInt(catalogPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * This method is used to update category
	 * 
	 * @param catalogPacket
	 * @return- category object
	 * @throws Exception
	 */
	@POST
	@Path("/update")
	public String update(CatalogPacket catalogPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, catalogPacket);
			tx = em.getTransaction();
			tx.begin();
			String json = new StoreForwardUtility().returnJsonPacket(catalogPacket, "CatalogPacket",httpRequest);
			 
			Category result = new CatalogServiceBean().update(em, catalogPacket);
			tx.commit();
			catalogPacket.setCategory(result);
			logger.severe("catalogPacket=======================11111111111111==================================="+catalogPacket.getPrinterList());
			logger.severe("catalogPacket========================22222222222222=================================="+catalogPacket.getCategory().getCategoryToPrinters());

			sendPacketForBroadcast(POSNServiceOperations.CatalogService_update.name(), catalogPacket);
			
		// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					 catalogPacket.getLocationId(), Integer.parseInt(catalogPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to delete the category
	 * 
	 * @param catalogPacket
	 * @return - category object
	 * @throws Exception
	 */
	@POST
	@Path("/delete")
	public String delete(CatalogPacket catalogPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, catalogPacket);
			tx = em.getTransaction();
			tx.begin();
			Category result = new CatalogServiceBean().delete(em, catalogPacket.getCategory());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.CatalogService_delete.name(), catalogPacket);
			catalogPacket.setCategory(result);
			String json = new StoreForwardUtility().returnJsonPacket(catalogPacket, "CatalogPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					catalogPacket.getLocationId(), Integer.parseInt(catalogPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (RuntimeException t) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw t;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * @param operation
	 * @param postPacket
	 */
	private void sendPacketForBroadcast(String operation, PostPacket postPacket) {
		try {
			operation = ServiceOperationsUtility.getOperationName(operation);
			MessageSender messageSender = new MessageSender();
			messageSender.sendMessage(httpRequest, postPacket.getClientId(), POSNServices.CatalogService.name(),
					operation, null, postPacket.getMerchantId(), postPacket.getLocationId(), postPacket.getEchoString(),
					postPacket.getSchemaName());

		} catch (Exception e) {
			logger.severe(httpRequest, e, e.getMessage());
		}
	}

	/**
	 * This method is used to get all category by location id
	 * 
	 * @param locationId
	 * @return - list of category
	 * @throws Exception
	 */
	@GET
	@Path("/getAllCategoriesByLocationId/{locationId}")
	public String getAllCategoriesByLocationId(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<Category> categoriesList = new CatalogServiceBean().getAllCategoriesByLocationId(em, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get attribute by item id and attribute type id
	 * 
	 * @param itemId
	 * @param attTypeId
	 * @return - attribute list
	 * @throws Exception
	 */
	@GET
	@Path("/getAttributeByItemIdAndAttTypeId/{itemId}/{attTypeId}")
	public String getAttributeByItemIdAndAttTypeId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("itemId") int itemId, @PathParam("attTypeId") int attTypeId) throws Exception {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<ItemsAttribute> attributeList = new ArrayList<ItemsAttribute>();

			String queryString = "select ia.id,ia.name,ia.selling_price,ia.display_name, ia.short_name,ia.locations_id,ia.msr_price, ia.multi_select,ia.image_name,ia.hex_code_values,"
					+ "ia.description,ia.status,ia.is_active, ia.sort_sequence,ia.created,ia.created_by,ia.updated, ia.updated_by,ia.stock_uom,ia.sellable_uom"
					+ " from items_attribute ia where ia.status != 'D' and  ia.id in(select itia.items_attribute_id from items_to_items_attribute itia where itia.items_id =? "
					+ " and itia.items_attribute_id in(select iattia.items_attribute_id from items_attribute_type_to_items_attribute iattia where iattia.items_attribute_type_id =?));";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, itemId)
					.setParameter(2, attTypeId).getResultList();
			for (Object[] objRow : resultList) {

				if ((String) objRow[0] != null) {
					int i = 0;

					ItemsAttribute itemAttribute = new ItemsAttribute();
					itemAttribute.setId((String) objRow[i++]);
					itemAttribute.setName((String) objRow[i++]);
					itemAttribute.setSellingPrice((BigDecimal) objRow[i++]);
					itemAttribute.setDisplayName((String) objRow[i++]);
					itemAttribute.setShortName((String) objRow[i++]);
					itemAttribute.setLocationsId((String) objRow[i++]);
					itemAttribute.setMsrPrice((BigDecimal) objRow[i++]);
					itemAttribute.setMultiSelect((int) objRow[i++]);
					itemAttribute.setImageName((String) objRow[i++]);
					itemAttribute.setHexCodeValues((String) objRow[i++]);
					itemAttribute.setDescription((String) objRow[i++]);
					itemAttribute.setStatus((String) objRow[i++]);
					itemAttribute.setIsActive((int) objRow[i++]);
					itemAttribute.setSortSequence((int) objRow[i++]);
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
		}  
		finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get business type for service plan by country id
	 * from nirvanaxp
	 * 
	 * @param countryId
	 * @return - category list
	 * @throws Exception
	 */
	@GET
	@Path("/getBusinessTypeForServicPlanByCountryId/{countryId}")
	public String getBusinessTypeForServicPlanByCountryId(@PathParam("countryId") int countryId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);

			countryId = isValidCountry(em, countryId);
			List<Category> category = null;
			category = new CatalogServiceBean().getBusinessTypeForServicPlanByCountryId(em, countryId);
			return new JSONUtility(httpRequest).convertToJsonString(category);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to check it country is present in db or not.
	 * 
	 * @param em
	 * @param countryId
	 * @return - integer value
	 */
	private int isValidCountry(EntityManager em, int countryId) {
		// TODO uzma - handle exception for sql query
		String queryString = "SELECT a.country_id FROM address a JOIN locations l " + " ON l.address_id = a.id "
				+ " WHERE a.country_id = ? " + " AND (l.locations_id ='0' or l.locations_id is null) AND l.status =  'A'";

		@SuppressWarnings("rawtypes")
		List resultList = em.createNativeQuery(queryString).setParameter(1, countryId).getResultList();
		int validCountryId = 0;
		if (resultList.size() > 0) {
			for (Object objRow : resultList) {
				validCountryId = (int) objRow;
			}
		} else {
			// TODO uzma - handle exception for sql query

			queryString = "select c.id from countries c where c.name='UNITED STATES'";
			@SuppressWarnings("rawtypes")
			List resultListDefault = em.createNativeQuery(queryString).getResultList();
			if (resultListDefault.size() > 0) {
				for (Object objRow : resultListDefault) {
					validCountryId = (int) objRow;
				}
			}
		}
		return validCountryId;
	}

	/**
	 * This method is used to get sub category by category id
	 * 
	 * @param categoryId
	 * @return - category list
	 * @throws Exception
	 */
	@GET
	@Path("/getSubCategoryByCategoryId/{categoryId}")
	public String getSubCategoryByCategoryId(@PathParam("categoryId") String categoryId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		// sessionId = "bf1f878ff4386e53c0a4c79545494eea";
		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			List<Category> category = null;
			category = new CatalogServiceBean().getSubCategoryByCategoryId(em, categoryId);
			return new JSONUtility(httpRequest).convertToJsonString(category);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get all item by category id
	 * 
	 * @param categoryId
	 * @return - item list
	 * @throws Exception
	 */
	@GET
	@Path("/getAllItemByCategoryId/{categoryId}")
	public String getAllItemByCategoryId(@PathParam("categoryId") String categoryId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		// sessionId = "bf1f878ff4386e53c0a4c79545494eea";
		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			List<Item> item = null;
			item = new CatalogServiceBean().getAllItemByCategoryId(em, categoryId);
			return new JSONUtility(httpRequest).convertToJsonString(item);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive() {
		return true;
	}

	@Override
	protected NirvanaLogger getNirvanaLogger() {
		return logger;
	}

	/**
	 * This method is used to get all item attribute by name and location id
	 * 
	 * @param name
	 * @param locationId
	 * @return - AttributeDisplay packet list
	 * @throws Exception
	 */
	@GET
	@Path("/getAllItemAttributeByNameAndLocationId/{name}/{locationId}")
	public String getAllItemAttributeByNameAndLocationId(@PathParam("name") String name,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<AttributeDisplayService> attributeDisplayServicesList = new ArrayList<AttributeDisplayService>();

			String queryString = "SELECT it.`id` as it_id , it.`display_name` as items_attribute_type_name, i.`id` as i_id, i.`display_name` as items_attribute_name"
					+ ", i.`image_name`, i.`selling_price`,it.`is_required`,  i.name as i_name , i.short_name as i_short_name  "
					+ "FROM `items_attribute_type` it  "
					+ "LEFT JOIN `items_attribute_type_to_items_attribute` iti ON iti.`items_attribute_type_id` = it.`id` "
					+ "LEFT JOIN `items_attribute` i ON iti.`items_attribute_id` = i.`id` "
					+ "WHERE it.`locations_id` = ? and (i.display_name like ? "
					+ "or  it.display_name like ? ) and it.status not in ('I','D') and i.status not in ('I','D')";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationId)
					.setParameter(2, "%" + name + "%").setParameter(3, "%" + name + "%").getResultList();
			for (Object[] objRow : resultList) {

				if ((String) objRow[0] != null) {

					AttributeDisplayService attributeDisplayService = new AttributeDisplayService();
					attributeDisplayService.setId((String) objRow[0]);
					attributeDisplayService.setItemsAttributeTypeName((String) objRow[1]);
					attributeDisplayService.setItemsAttributeId((String) objRow[2]);
					attributeDisplayService.setItemsAttributeDisplayName((String) objRow[3]);
					attributeDisplayService.setImageName((String) objRow[4]);
					attributeDisplayService.setPrice((BigDecimal) objRow[5]);
					if ((Boolean) objRow[6]) {
						attributeDisplayService.setIsRequired(1);
					} else {
						attributeDisplayService.setIsRequired(0);
					}
					attributeDisplayService.setItemsAttributeName((String) objRow[7]);
					attributeDisplayService.setItemsAttributeShortName((String) objRow[8]);
					attributeDisplayServicesList.add(attributeDisplayService);
				}

			}
			return new JSONUtility(httpRequest).convertToJsonString(attributeDisplayServicesList);
		} // TODO uzma- handle if no result found
		finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get categories by category id
	 * 
	 * @param categoryIdPacket
	 * @return - list of category
	 * @throws Exception
	 */
	@POST
	@Path("/getCategoriesByCategoryIdPacket")
	public String getCategoriesByCategoryIdPacket(CategoryIdPacket categoryIdPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<Category> categoriesList = null;
			categoriesList = new CatalogServiceBean().getCategoriesByCategoryIdPacket(em, categoryIdPacket);
			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get Category And Sub Category And Item By Category
	 * Id Packet And Location Id
	 * 
	 * @param categoryIdPacket
	 * @param locationId
	 * @return - list category display packet
	 * @throws Exception
	 */
	@POST
	@Path("/getCategoryAndSubCategoryAndItemByCategoryIdPacketAndLocationId/{locationId}")
	public String getCategoryAndSubCategoryAndItemByCategoryIdPacketAndLocationId(CategoryIdPacket categoryIdPacket,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			List<Item> ans = new CatalogServiceBean().getCategoryAndSubCategoryAndItemByCategoryIdPacketAndLocationId(em, categoryIdPacket, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get Category And Sub Category And Item By Category
	 * Id And Location Id
	 * 
	 * @param categoryId
	 * @param locationId
	 * @return - list of catalog display packet
	 * @throws Exception
	 */
	@GET
	@Path("/getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId/{categoryId}/{locationId}")
	public String getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(@PathParam("categoryId") String categoryId,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			List<CatalogDisplayService> ans = new CatalogServiceBean()
					.getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(em, categoryId, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(ans);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to add category for multiple location
	 * 
	 * @param catalogPacket
	 * @return - category object of master location
	 * @throws Exception
	 */
	@POST
	@Path("/addMultipleLocationsCategory")
	public String addMultipleLocationsCategory(CatalogPacket catalogPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, catalogPacket);
			tx = em.getTransaction();
			tx.begin();
			
			Category result = new CatalogServiceBean().addMultipleLocationsCategory(em, catalogPacket.getCategory(),
					catalogPacket, httpRequest);
			tx.commit();
			// call synchPacket for store forward
//			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
//					catalogPacket.getLocationId(), Integer.parseInt(catalogPacket.getMerchantId()));
			
			String[] locationsId = catalogPacket.getLocationsListId().split(",");
			for (String locationId : locationsId) {
				catalogPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.CatalogService_add.name(), catalogPacket);
			}
			catalogPacket.setCategory(result);
			
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

	/**
	 * This method is used to update category for multiple location
	 * 
	 * @param catalogPacket
	 * @return - category object of master location
	 * @throws Exception
	 */
	@POST
	@Path("/updateMultipleLocationsCategory")
	public String updateMultipleLocationsCategory(CatalogPacket catalogPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, catalogPacket);

			tx = em.getTransaction();
			tx.begin();
			String json = new StoreForwardUtility().returnJsonPacket(catalogPacket, "CatalogPacket",httpRequest);
			 
			Category result = new CatalogServiceBean().updateMultipleLocationsCategory(em, catalogPacket.getCategory(),
					catalogPacket, httpRequest);
			tx.commit();
			catalogPacket.setCategory(result);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					catalogPacket.getLocationId(), Integer.parseInt(catalogPacket.getMerchantId()));
			String[] locationsId = catalogPacket.getLocationsListId().split(",");
			for (String locationId : locationsId) {
				if (locationId != null && locationId.length() > 0) {
					catalogPacket.setLocationId(locationId);
				} else {
					catalogPacket.setLocationId(catalogPacket.getCategory().getLocationsId() + "");
				}
				sendPacketForBroadcast(POSNServiceOperations.CatalogService_update.name(), catalogPacket);
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

	/**
	 * This method is used to get count of category by location id
	 * 
	 * @param locationId
	 * @param categoryName
	 * @return - big integer
	 * @throws Exception
	 */
	@GET
	@Path("/getCategoryCountByLocationId/{locationId}/{categoryName}")
	public BigInteger getCategoryCountByLocationId(@PathParam("locationId") String locationId,
			@PathParam("categoryName") String categoryName, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			if (categoryName == null || categoryName.equals("null") || categoryName.equals(null) || categoryName.equals("0")) {
				categoryName = "";
			}
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String temp = Utilities.convertAllSpecialCharForSearch(categoryName);

			BigInteger count = new CatalogServiceBean().getCategoryCountByLocationId(locationId, temp, em, httpRequest);

			return count;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get Category By LocationId by start end index for
	 * lazy loading
	 * 
	 * @param locationId
	 * @param startIndex
	 * @param endIndex
	 * @param categoryName
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getCategoryByLocationId/{locationId}/{startIndex}/{endIndex}/{categoryName}")
	public String getCategoryByLocationId(@PathParam("locationId") String locationId,
			@PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex,
			@PathParam("categoryName") String categoryName, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			if (categoryName == null || categoryName.equals("null") || categoryName.equals(null)) {
				categoryName = "";
			}

			categoryName = categoryName.trim();
			String temp = Utilities.convertAllSpecialCharForSearch(categoryName);

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return (new CatalogServiceBean().getCategoryByLocationId(locationId, startIndex, endIndex, temp, em,
					httpRequest));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get Raw Material category By Name And Location Id
	 * 
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getRawMaterialByNameAndLocationId/{locationId}")
	public String getRawMaterialByNameAndLocationId(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<Category> categoriesList = null;
			categoriesList = new CatalogServiceBean().getRawMaterialByNameAndLocationId(em, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get Raw Material Category By Status And Location
	 * Id
	 * 
	 *
	 * @param packet
	 * @return- list of category detail display packet
	 * @throws Exception
	 */
	@POST
	@Path("/getRawMaterialCategoryByStatusAndLocationId")
	public String getRawMaterialCategoryByStatusAndLocationId(GetRawMaterialCategoryPacket packet,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<CategoryDetailDisplayPacket> categoriesList = null;
			String categoryName = packet.getCategoryName();
			if (categoryName == null || categoryName.equals("null") || categoryName.equals(null)) {
				categoryName = "";
			}
			categoriesList = new CatalogServiceBean().getRawMaterialCategoryByStatusAndLocationId(em,
					packet.getLocationId(), packet.getStartIndex(), packet.getEndIndex(), categoryName);

			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get Raw Material Category Count By Location Id
	 * 
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getRawMaterialCategoryCountByLocationId/{locationId}")
	public int getRawMaterialCategoryCountByLocationId(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			int count = (int) new CatalogServiceBean().getRawMaterialCategoryCountByLocationId(em, locationId);

			return count;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get Raw Material Category Count By Location Id and
	 * category name
	 * 
	 * @param locationId
	 * @param categoryName
	 * @return- big integer for count
	 * @throws Exception
	 */
	@GET
	@Path("/getRawMaterialCategoryCountByStatusAndLocationId/{locationId}/{categoryName}")
	public int getRawMaterialCategoryCountByStatusAndLocationId(@PathParam("locationId") String locationId,
			@PathParam("categoryName") String categoryName, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			if (categoryName == null || categoryName.equals("null") || categoryName.equals(null)) {
				categoryName = "";
			}
			int count = new CatalogServiceBean().getRawMaterialCategoryCountByStatusAndLocationId(em, locationId,
					categoryName);

			return count;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get Category And Sub Category By Location Id
	 * Category Name
	 * 
	 * @param locationId
	 * @param categoryName
	 * @return - category detail display packet
	 * @throws Exception
	 */
	@GET
	@Path("/getCategoryAndSubCategoryByLocationIdCategoryName/{locationId}/{categoryName}")
	public String getCategoryAndSubCategoryByLocationIdCategoryName(@PathParam("locationId") String locationId,
			@PathParam("categoryName") String categoryName, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			categoryName = categoryName.trim();
			if (categoryName == null || categoryName.equals("null") || categoryName.equals(null)) {
				categoryName = "";
			}

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return (new CatalogServiceBean().getCategoryAndSubCategoryByLocationIdCategoryName(locationId,
					categoryName.trim(), em, httpRequest));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get Raw Material Category By And Location Id
	 * 
	 * @param locationId
	 * @return - categoriesList
	 * @throws Exception
	 */
	@GET
	@Path("/getRawMaterialCategoryByAndLocationId/{locationId}")
	public String getRawMaterialCategoryByAndLocationId(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<CategoryDetailDisplayPacket> categoriesList = null;
			categoriesList = new CatalogServiceBean().getRawMaterialCategoryByAndLocationId(em, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * This method is used to get All Categories And Sub Categories By Location
	 * Id
	 * 
	 * @param locationId
	 * @return - list of category
	 * @throws Exception
	 */
	@GET
	@Path("/getAllCategoriesAndSubCategoriesByLocationId/{locationId}")
	public String getAllCategoriesAndSubCategoriesByLocationId(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<Category> categoriesList = new CatalogServiceBean().getAllCategoriesAndSubCategoriesByLocationId(em,
					locationId);
			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

}
