/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.math.BigInteger;
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

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.Utilities;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.ItemByLocationIdPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemGroupPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemListPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemToDatePacket;
import com.nirvanaxp.services.jaxrs.packets.ItemsAttributePacket;
import com.nirvanaxp.services.jaxrs.packets.ItemsToLocationPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.packet.ItemDetailDisplayPacket;
import com.nirvanaxp.services.packet.ItemToSupplierPacket;
import com.nirvanaxp.services.util.ItemsServiceBean;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.FutureUpdate;
import com.nirvanaxp.types.entities.catalog.category.Category;
import com.nirvanaxp.types.entities.catalog.category.ItemToDate;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.ItemGroup;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
import com.nirvanaxp.types.entities.custom.CatalogDisplayService;
import com.nirvanaxp.types.entities.custom.ItemInformation;
import com.nirvanaxp.types.entities.orders.ItemToSupplier;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class ItemsService extends AbstractNirvanaService
{

	@Context
	HttpServletRequest httpRequest;

	private final static NirvanaLogger logger = new NirvanaLogger(ItemsService.class.getName());

	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
	}

	@GET
	@Path("/getItemById/{id}")
	public String getItemById(@PathParam("id") String itemId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ItemsServiceBean().getItemById(em, itemId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllItemType")
	public String getItemById(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return new JSONUtility(httpRequest).convertToJsonString(new ItemsServiceBean().getAllItemType(em));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
  
	}

	@GET
	@Path("/getItemByIdForCustomer/{itemId}")
	public String getItemByIdForCustomer(@PathParam("itemId") String itemId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @CookieParam(value = "SchemaName") String schemaName) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForCustomer(httpRequest, sessionId, schemaName);
			return new JSONUtility(httpRequest).convertToJsonString(new ItemsServiceBean().getItemByIdForCustomer(em, itemId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getItemsByCourseId/{id}")
	public String getItemsByCourseId(@PathParam("id") String courseId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return new JSONUtility(httpRequest).convertToJsonString(new ItemsServiceBean().getItemsByCourseId(em, courseId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllItemsToSupplier")
	public String getAllItemsToSupplier(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "SELECT its FROM ItemToSupplier its";
			TypedQuery<ItemToSupplier> query = em.createQuery(queryString, ItemToSupplier.class);
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getItemInfoById/{itemId}")
	public String getItemInfoById(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("itemId") String itemId) throws Exception
	{

		EntityManager em = null;
		try
		{

			List<ItemInformation> itemInformations = new ArrayList<ItemInformation>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "SELECT i.name, i.item_number, i.short_name, i.display_name, i.description, c.display_name as course_name, "
					+ " cat.display_name as category_name, iat.id as attribute_type_id, " + " iat.display_name as attribute_type_name,  iat.is_required FROM items i"
					+ " LEFT JOIN items_to_items_attribute_type i_iat on i_iat.items_id = i.id" + " LEFT JOIN course c on c.id = i.course_id" + " LEFT JOIN category_items ci on ci.items_id = i.id"
					+ " LEFT JOIN category cat on cat.id = ci.category_id" + " LEFT JOIN items_attribute_type iat on iat.id = i_iat.items_attribute_type_id" + " where i.id=? ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, itemId).getResultList();
			for (Object[] objRow : resultList)
			{

				int i = 0;
				ItemInformation itemInformation = new ItemInformation();
				if (objRow[i] != null)
					itemInformation.setName((String) objRow[i]);
				i++;
				if (objRow[i] != null)
					itemInformation.setItemNo(Long.parseLong((String) objRow[i]));
				i++;
				if (objRow[i] != null)
					itemInformation.setShortName((String) objRow[i]);
				i++;
				if (objRow[i] != null)
					itemInformation.setDisplayName((String) objRow[i]);
				i++;
				if (objRow[i] != null)
					itemInformation.setDescription((String) objRow[i]);
				i++;
				if (objRow[i] != null)
					itemInformation.setCourseName((String) objRow[i]);
				i++;
				if (objRow[i] != null)
					itemInformation.setCategoryName((String) objRow[i]);
				i++;
				if (objRow[i] != null)
					itemInformation.setAttributeTypeId((int) objRow[i]);
				i++;
				if (objRow[i] != null)
					itemInformation.setAttributeTypeName((String) objRow[i]);
				i++;
				if (objRow[i] != null)
					itemInformation.setIsRequired((int) objRow[i]);
				itemInformations.add(itemInformation);

			}

			return new JSONUtility(httpRequest).convertToJsonString(itemInformations);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getItemsByCategoryId/{categoryId}/{locationId}")
	public String getItemsByCategoryId(@PathParam("categoryId") String categoryId, @PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@CookieParam(value = "SchemaName") String schemaName) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForCustomer(httpRequest, sessionId, schemaName);

			return new JSONUtility(httpRequest).convertToJsonString(new ItemsServiceBean().getItemsByCategoryId(em, categoryId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * @param locationId
	 * @param categoryId
	 * @param name
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAllItemsByLocationIdAndCategoryIdAndName/{locationId}/{categoryId}/{name}")
	public String getAllItemsByLocationIdAndCategoryIdAndName(@PathParam("locationId") String locationId, @PathParam("categoryId") String categoryId, @PathParam("name") String name,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			Item item = new ItemsServiceBean().getAllItemsByLocationIdAndCategoryIdAndName(em, locationId, categoryId, name);

			return new JSONUtility(httpRequest).convertToJsonString(item);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllItemsByLocationIdAndCategoryIdAndDisplaySequence/{locationId}/{categoryId}/{displaySequence}")
	public String getAllItemsByLocationIdAndCategoryIdAndDisplaySequence(@PathParam("locationId") String locationId, @PathParam("categoryId") String categoryId,
			@PathParam("displaySequence") int displaySequence, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ItemsServiceBean().getAllItemsByLocationIdAndCategoryIdAndDisplaySequence(em, locationId, categoryId, displaySequence));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getItemsByItemNumber/{itemNumber}/{locationId}")
	public String getItemsByItemNumber(@PathParam("itemNumber") String itemNumber, @PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return new JSONUtility(httpRequest).convertToJsonString(new ItemsServiceBean().getItemsByItemNumber(em, itemNumber, locationId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/add")
	public String add(ItemPacket itemPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemPacket);
			tx = em.getTransaction();
			tx.begin();
			new ItemsServiceBean().addUpdateItem(em, itemPacket.getItem(), itemPacket,itemPacket.getIsRawMaterialUpdate(),
					itemPacket,httpRequest,true);
			
			String json = new StoreForwardUtility().returnJsonPacket(itemPacket, "ItemPacket",httpRequest);
			
			tx.commit();
			Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemPacket.getItem().getId());
			itemPacket.setItem(item);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					itemPacket.getLocationId(), Integer.parseInt(itemPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.ItemsService_add.name(), itemPacket);
			return new JSONUtility(httpRequest).convertToJsonString(item);
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
	@Path("/addUpdateItemLater")
	public String addUpdateItemLater(ItemPacket itemPacket) throws Exception
	{
		EntityTransaction tx = null;
		EntityManager em = null;


		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest,null);
			tx = em.getTransaction();
			tx.begin();
			Item item= new ItemsServiceBean().addUpdateItemLater(httpRequest,em, itemPacket,httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));
			tx.commit();
//			Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemPacket.getItem().getId());
//
//			sendPacketForBroadcast(POSNServiceOperations.ItemsService_add.name(), itemPacket);
			itemPacket.setItem(item);
			String json = new StoreForwardUtility().returnJsonPacket(itemPacket, "ItemPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					itemPacket.getLocationId(), Integer.parseInt(itemPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(item);
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
	@Path("/update")
	public String update(ItemPacket itemPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemPacket);
			tx = em.getTransaction();
			tx.begin();
			String json = new StoreForwardUtility().returnJsonPacket(itemPacket, "ItemPacket",httpRequest);
			new ItemsServiceBean().addUpdateItem(em, itemPacket.getItem(), itemPacket,itemPacket.getIsRawMaterialUpdate(),itemPacket,httpRequest,false);
			tx.commit();
			
//			Item result = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemPacket.getItem().getId());
			Item result =new ItemsServiceBean().getItemById(em, itemPacket.getItem().getId());
			itemPacket.setItem(result);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					itemPacket.getLocationId(), Integer.parseInt(itemPacket.getMerchantId()));
			sendPacketForBroadcast(POSNServiceOperations.ItemsService_update.name(), itemPacket);
			return new JSONUtility(httpRequest).convertToJsonString(result);
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
	@Path("/delete")
	public String delete(ItemPacket itemPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemPacket);
			tx = em.getTransaction();
			tx.begin();
			Item result = new ItemsServiceBean().delete(em, itemPacket.getItem());
			tx.commit();
			itemPacket.setItem(result);
			String json = new StoreForwardUtility().returnJsonPacket(itemPacket, "ItemPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					itemPacket.getLocationId(), Integer.parseInt(itemPacket.getMerchantId()));
			sendPacketForBroadcast(POSNServiceOperations.ItemsService_delete.name(), itemPacket);
			return new JSONUtility(httpRequest).convertToJsonString(result);
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
	@Path("/getItemSpecificSalesTax/{locationId}")
	public String getItemSpecificSalesTax(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return new JSONUtility(httpRequest).convertToJsonString(new ItemsServiceBean().getItemSpecificSalesTax(em, locationId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private void sendPacketForBroadcast(String operation, PostPacket postPacket)
	{

		operation = ServiceOperationsUtility.getOperationName(operation);
		MessageSender messageSender = new MessageSender();

		messageSender.sendMessage(httpRequest, postPacket.getClientId(), POSNServices.ItemsService.name(), operation, null, postPacket.getMerchantId(), postPacket.getLocationId(),
				postPacket.getEchoString(), postPacket.getSchemaName());

	}

	@POST
	@Path("/updateItemsAttributePrice")
	public String updateItemsAttributePrice(ItemsAttributePacket itemAttributePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemAttributePacket);
			tx = em.getTransaction();
			tx.begin();
			ItemsAttribute result = new ItemsServiceBean().updateItemsAttributePrice(em, itemAttributePacket.getItemsAttribute());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.ItemsService_updateItemsAttributePrice.name(), itemAttributePacket);
			itemAttributePacket.setItemsAttribute(result);
			String json = new StoreForwardUtility().returnJsonPacket(itemAttributePacket, "ItemsAttributePacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					itemAttributePacket.getLocationId(), Integer.parseInt(itemAttributePacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(result);
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
	@Path("/updateItemsById")
	public String updateItemsById(ItemPacket itemPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemPacket);
			tx = em.getTransaction();
			tx.begin();
			Item result = new ItemsServiceBean().updateItemsById(em, itemPacket);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.ItemsService_update.name(), itemPacket);
			itemPacket.setItem(result);
			String json = new StoreForwardUtility().returnJsonPacket(itemPacket, "ItemPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					itemPacket.getLocationId(), Integer.parseInt(itemPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
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
	@Path("/updateItemForInventory")
	public String updateItemForInventory(ItemPacket itemPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemPacket);
			tx = em.getTransaction();
			tx.begin();
			Item result = new ItemsServiceBean().updateMultipleItemForInventory(em, itemPacket);
			tx.commit();

			sendPacketForBroadcast(POSNServiceOperations.ItemsService_update.name(), itemPacket);
			itemPacket.setItem(result);
			String json = new StoreForwardUtility().returnJsonPacket(itemPacket, "ItemPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					itemPacket.getLocationId(), Integer.parseInt(itemPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(result);

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
	@Path("/getAllItemsByRootCategoryIdAndNameAndLocationd/{categoryId}/{name}/{locationId}")
	public String getAllItemsByRootCategoryIdAndNameAndLocationd(@PathParam("categoryId") String categoryId, @PathParam("name") String name, @PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em =null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			ItemsServiceBean bean= new ItemsServiceBean();
			String temp = Utilities.convertAllSpecialCharForSearch(name);
			
			List<CatalogDisplayService> ans = bean.getAllItemsByRootCategoryIdAndNameAndLocationd(categoryId, temp, locationId, em, httpRequest);
			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{

			LocalSchemaEntityManager.getInstance().closeEntityManager(em);

		}

	}

	@POST
	@Path("/updateItemsDisplaySequenceByItemsId")
	public String updateItemsDisplaySequenceByItemsId(ItemListPacket itemListPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemListPacket);
			tx = em.getTransaction();
			tx.begin();
			List<Item> result = new ItemsServiceBean().updateItemsDisplaySequenceByItemsId(em, itemListPacket.getItem());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.ItemsService_update.name(), itemListPacket);
			itemListPacket.setItem(result);
			String json = new StoreForwardUtility().returnJsonPacket(itemListPacket, "ItemListPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					itemListPacket.getLocationId(), Integer.parseInt(itemListPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
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
	@Path("/addItemGroup")
	public String addItemGroup(ItemGroupPacket itemGroupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemGroupPacket);
			tx = em.getTransaction();
			tx.begin();
			ItemGroup result = new ItemsServiceBean().addItemGroup(em, itemGroupPacket.getItemGroup());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.CatalogService_addItemGroup.name(), itemGroupPacket);
			itemGroupPacket.setItemGroup(result);
			String json = new StoreForwardUtility().returnJsonPacket(itemGroupPacket, "ItemGroupPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					itemGroupPacket.getLocationId(), Integer.parseInt(itemGroupPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (RuntimeException t)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw t;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/updateItemGroup")
	public String updateItemGroup(ItemGroupPacket ItemGroupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, ItemGroupPacket);
			tx = em.getTransaction();
			tx.begin();
			ItemGroup result = new ItemsServiceBean().updateItemGroup(em, ItemGroupPacket.getItemGroup());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.CatalogService_updateItemGroup.name(), ItemGroupPacket);
			ItemGroupPacket.setItemGroup(result);
			String json = new StoreForwardUtility().returnJsonPacket(ItemGroupPacket, "ItemGroupPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					ItemGroupPacket.getLocationId(), Integer.parseInt(ItemGroupPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (RuntimeException t)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw t;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/deleteItemGroup")
	public String deleteItemGroup(ItemGroupPacket ItemGroupPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, ItemGroupPacket);
			tx = em.getTransaction();
			tx.begin();
			ItemGroup result = new ItemsServiceBean().deleteItemGroup(em, ItemGroupPacket.getItemGroup());
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.CatalogService_deleteItemGroup.name(), ItemGroupPacket);
			ItemGroupPacket.setItemGroup(result);
			String json = new StoreForwardUtility().returnJsonPacket(ItemGroupPacket, "ItemGroupPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					ItemGroupPacket.getLocationId(), Integer.parseInt(ItemGroupPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (RuntimeException t)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw t;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getItemGroupByLocationId/{locationId}")
	public String getItemGroupByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return new JSONUtility(httpRequest).convertToJsonString(new ItemsServiceBean().getItemGroupByLocationId(em, locationId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	@GET
	@Path("/getItemGroupByLocationIdAndDStatus/{locationId}")
	public String getItemGroupByLocationIdAndDStatus(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return new JSONUtility(httpRequest).convertToJsonString(new ItemsServiceBean().getItemGroupByLocationIdAndDStatus(em, locationId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	

	@GET
	@Path("/getSubItemGroupByIdAndLocationId/{id}/{locationId}")
	public String getSubItemGroupByIdAndLocationId(@PathParam("id") String id, @PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return new JSONUtility(httpRequest).convertToJsonString(new ItemsServiceBean().getSubItemGroupByIdAndLocationId(em, locationId, id));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive()
	{
		return true;
	}

	@POST
	@Path("/addMultipleLocationsItems")
	public String addMultipleLocationsItems(ItemPacket itemPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemPacket);
			tx = em.getTransaction();
			tx.begin();
			logger.severe("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+itemPacket.getLocationsListId());
			String locationList = itemPacket.getLocationsListId();
			new ItemsServiceBean().addMultipleLocationsItems(httpRequest, em, itemPacket.getItem(), itemPacket);
			tx.commit();
       	
         
			Item item   = new ItemsServiceBean().getItemById(em, itemPacket.getItem().getId());
				// call synchPacket for store forward
			 
			String[] locationsId = locationList.split(",");
			
			for (String locationId : locationsId)
			{
				itemPacket.setLocationId(locationId);
				sendPacketForBroadcast(POSNServiceOperations.ItemsService_add.name(), itemPacket);
			}

		
			return new JSONUtility(httpRequest).convertToJsonString(item);
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
	@Path("/updateMultipleLocationsItems")
	public String updateMultipleLocationsItems(ItemPacket itemPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemPacket);
			tx = em.getTransaction();
			tx.begin();
//			String json = new StoreForwardUtility().returnJsonPacket(itemPacket, "ItemPacket",httpRequest);
			new ItemsServiceBean().updateMultipleLocationsItems(httpRequest, em, itemPacket.getItem(), itemPacket);
			tx.commit();
			
			Item item   = new ItemsServiceBean().getItemById(em, itemPacket.getItem().getId());
			// call synchPacket for store forward
//			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
//					itemPacket.getLocationId(), Integer.parseInt(itemPacket.getMerchantId()));
			String[] locationsId = itemPacket.getLocationsListId().split(",");
			for (String locationId : locationsId)
			{
				if(locationId!= null && locationId.length()>0){
					itemPacket.setLocationId(locationId);
				}else{
					itemPacket.setLocationId(itemPacket.getItem().getLocationsId()+"");
				}
				 
				sendPacketForBroadcast(POSNServiceOperations.ItemsService_update.name(), itemPacket);
			}

			return new JSONUtility(httpRequest).convertToJsonString(item);
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
	@Path("/getItemByLocationId/{locationId}/{itemName}/{startIndex}/{endIndex}/{categoryId}")
	public String getItemByLocationId(@PathParam("locationId") String locationId, @PathParam("itemName") String itemName, @PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex,
			@PathParam("categoryId") String categoryId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (itemName == null || itemName.equals("null") || itemName.equals(null))
			{
				itemName = "";
			}
			itemName = itemName.trim();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			ItemsServiceBean resultList = new ItemsServiceBean();
			return resultList.getItemByLocationId(locationId, startIndex, endIndex, itemName.trim(), categoryId, em, httpRequest);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/deleteMultipleLocationsItems")
	public String deleteMultipleLocationsItems(ItemPacket itemPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemPacket);
			tx = em.getTransaction();
			tx.begin();
			new ItemsServiceBean().deleteMultipleLocationItem(em, itemPacket.getItem(), itemPacket, httpRequest);
			tx.commit();
			Item item = (Item) new CommonMethods().getObjectById("Item", em,Item.class, itemPacket.getItem().getId());

			List<String> locationsId = new CommonMethods().getAllActiveLocations(httpRequest, em);
			for (String locationId : locationsId)
			{
				itemPacket.setLocationId(locationId.toString());
				sendPacketForBroadcast(POSNServiceOperations.ItemsService_delete.name(), itemPacket);
			}

			String json = new StoreForwardUtility().returnJsonPacket(itemPacket, "ItemPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					itemPacket.getLocationId(), Integer.parseInt(itemPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(item);
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
	@Path("/getItemUomById/{id}")
	public String getItemUomById(@PathParam("id") String itemId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			ItemsServiceBean resultList = new ItemsServiceBean();
			return resultList.getItemUomById(itemId, em, httpRequest);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/getRawMaterialItemByStatusAndLocationId")
	public String getRawMaterialItemByStatusAndLocationId(ItemByLocationIdPacket packet, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;

		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<ItemDetailDisplayPacket> itemList = null;
			String itemName = packet.getItemName();
			if (itemName == null || itemName.equals("null") || itemName.equals(null))
			{
				itemName = "";
			}
			itemList = new ItemsServiceBean().getRawMaterialItemByStatusAndLocationId(em, packet.getLocationId(), packet.getStartIndex(), packet.getEndIndex(), packet.getCategoryId(), itemName);

			return new JSONUtility(httpRequest).convertToJsonString(itemList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getRawMaterialItemCountByLocationId/{locationId}")
	public int getRawMaterialItemCountByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;

		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			int count = new ItemsServiceBean().getRawMaterialItemCountByLocationId(em, locationId);

			return count;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getItemCountByLocationId/{locationId}/{itemName}/{categoryId}")
	public BigInteger getItemCountByLocationId(@PathParam("locationId") String locationId, @PathParam("itemName") String itemName, @PathParam("categoryId") int categoryId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (itemName == null || itemName.equals("null") || itemName.equals(null))
			{
				itemName = "";
			}
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select  count(i.id) " 
					+ "  from items i left join course c on i.course_id=c.id "
					+ " left join category_items ci on ci.items_id =i.id left join category co on co.id=ci.category_id " + " left join items_type itt on itt.id=i.item_type "
					+ "  where i.locations_id= ? " + "";
					if(categoryId>0){
						sql+= " and ci.id in ("+categoryId+")";
					}
				
					sql += " and  i.status not in ('D','R') and i.display_name like '%"
					+ itemName + "%'  ";
			

			 
			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, locationId).getSingleResult();

			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	@POST
	@Path("/getItemCountByLocationId")
	public BigInteger getItemCountByLocationId(ItemByLocationIdPacket itemByLocationIdPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (itemByLocationIdPacket.getItemName() == null || itemByLocationIdPacket.getItemName().equals("null") || itemByLocationIdPacket.getItemName().equals(null))
			{
				itemByLocationIdPacket.setItemName("");
			}
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select  count(distinct i.id) " 
					+ "  from items i left join course c on i.course_id=c.id "
					+ " left join category_items ci on ci.items_id =i.id left join category co on co.id=ci.category_id " 
					+ " left join items_type itt on itt.id=i.item_type "
					+ "  where i.locations_id= ? " + "";
					if(itemByLocationIdPacket.getCategoryId() != null && 
							!itemByLocationIdPacket.getCategoryId().isEmpty()){
						sql+= " and ci.category_id in ("+itemByLocationIdPacket.getCategoryId()+")";
					}
				
					sql += " and  i.status not in ('D','R') and i.display_name like '%"
					+ itemByLocationIdPacket.getItemName() + "%'  ";
			
			 

			/*
			 * if (categoryId != 0) {
			 * 
			 * }
			 */
			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, itemByLocationIdPacket.getLocationId()).getSingleResult();
			
			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	@POST
	@Path("/getItemCountByLocationIdAndSaleOnlyAndSaleAndInventoryOnly")
	public BigInteger getItemCountByLocationIdAndSaleOnlyAndSaleAndInventoryOnly(ItemByLocationIdPacket itemByLocationIdPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (itemByLocationIdPacket.getItemName() == null || itemByLocationIdPacket.getItemName().equals("null") || itemByLocationIdPacket.getItemName().equals(null))
			{
				itemByLocationIdPacket.setItemName("");
			}
			String temp = Utilities.convertAllSpecialCharForSearch(itemByLocationIdPacket.getItemName());
					
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String sql = "select  count(distinct i.id) " 
					+ "  from items i left join course c on i.course_id=c.id "
					+ " left join category_items ci on (ci.items_id =i.id and ci.status !='D' ) left join category co on co.id=ci.category_id " 
					+ " left join items_type itt on itt.id=i.item_type "
					+ "  where i.locations_id= ? and itt.name in ('Sale Only','Sale And Inventory','Semi Finished Goods')" + "";
					if(itemByLocationIdPacket.getCategoryId() != null && 
							!itemByLocationIdPacket.getCategoryId().isEmpty()){
						sql+= " and ci.category_id in ("+itemByLocationIdPacket.getCategoryId()+")";
					}
				
					sql += " and  i.status not in ('D','R') and i.display_name like '%"
					+ temp + "%'  ";
			
			 

			/*
			 * if (categoryId != 0) {
			 * 
			 * }
			 */
					BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, itemByLocationIdPacket.getLocationId()).getSingleResult();
			
			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}


	@POST
	@Path("/getItemByLocationId")
	public String getItemByLocationId(ItemByLocationIdPacket itemByLocationIdPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (itemByLocationIdPacket.getItemName() == null || itemByLocationIdPacket.getItemName().equals("null") || itemByLocationIdPacket.getItemName().equals(null))
			{
				itemByLocationIdPacket.setItemName("");
			}
			itemByLocationIdPacket.setItemName(itemByLocationIdPacket.getItemName().trim());
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			ItemsServiceBean resultList = new ItemsServiceBean();
			return resultList.getItemByLocationId(itemByLocationIdPacket, em, httpRequest);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	@POST
	@Path("/getItemByLocationIdAndSaleOnlyAndSaleAndInventory")
	public String getItemByLocationIdAndSaleOnlyAndSaleAndInventory(ItemByLocationIdPacket itemByLocationIdPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (itemByLocationIdPacket.getItemName() == null || itemByLocationIdPacket.getItemName().equals("null") || itemByLocationIdPacket.getItemName().equals(null))
			{
				itemByLocationIdPacket.setItemName("");
			}
			
			itemByLocationIdPacket.setItemName(itemByLocationIdPacket.getItemName());
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			ItemsServiceBean resultList = new ItemsServiceBean();
			return resultList.getItemByLocationIdAndSaleOnlyAndSaleAndInventory(itemByLocationIdPacket, em, httpRequest);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/getRawMaterialItemByLocationId")
	public String getRawMaterialItemByLocationId(ItemByLocationIdPacket itemByLocationIdPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (itemByLocationIdPacket.getItemName() == null || itemByLocationIdPacket.getItemName().equals("null") || itemByLocationIdPacket.getItemName().equals(null))
			{
				itemByLocationIdPacket.setItemName("");
			}
			itemByLocationIdPacket.setItemName(itemByLocationIdPacket.getItemName().trim());
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			ItemsServiceBean resultList = new ItemsServiceBean();
			return resultList.getRawMaterialItemByLocationId(itemByLocationIdPacket, em, httpRequest);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/getRawMaterialItemByLocationIdAndInventoryAndSaleAndInventory")
	public String getRawMaterialItemByLocationIdAndInventoryAndSaleAndInventory(ItemByLocationIdPacket itemByLocationIdPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (itemByLocationIdPacket.getItemName() == null || itemByLocationIdPacket.getItemName().equals("null") || itemByLocationIdPacket.getItemName().equals(null))
			{
				itemByLocationIdPacket.setItemName("");
			}
			itemByLocationIdPacket.setItemName(itemByLocationIdPacket.getItemName().trim());
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			ItemsServiceBean resultList = new ItemsServiceBean();
			return resultList.getRawMaterialItemByLocationIdAndInventoryAndSaleAndInventory(itemByLocationIdPacket, em, httpRequest);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/getRawMaterialItemCountByLocationIdAndInventoryAndSaleAndInventory")
	public int getRawMaterialItemCountByLocationIdAndInventoryAndSaleAndInventory(ItemByLocationIdPacket itemByLocationIdPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (itemByLocationIdPacket.getItemName() == null || itemByLocationIdPacket.getItemName().equals("null") || itemByLocationIdPacket.getItemName().equals(null))
			{
				itemByLocationIdPacket.setItemName("");
			}

			itemByLocationIdPacket.setItemName(itemByLocationIdPacket.getItemName().trim());
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			ItemsServiceBean resultList = new ItemsServiceBean();
			return resultList.getRawMaterialItemCountByLocationIdAndInventoryAndSaleAndInventory(itemByLocationIdPacket, em, httpRequest);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getParentCategory/{locationId}/{category_id}")
	public Category getParentCategory(@PathParam("locationId") String locationId, @PathParam("category_id") String category_id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			Category childCategory = (Category) new CommonMethods().getObjectById("Category", em,Category.class, category_id);
			Category parentCategory = (Category) new CommonMethods().getObjectById("Category", em,Category.class, childCategory.getGlobalCategoryId());
			if (parentCategory == null)
			{
				childCategory = parentCategory;
			}
			return parentCategory;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	@Path("/getItemsBySupplier/{supplierId}")
	public String getItemsBySupplier(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId
			,@PathParam("supplierId") String supplierId) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<ItemToSupplierPacket> supplierPacket = new ItemsServiceBean().getItemBySupplierId(em, supplierId);
			return new JSONUtility(httpRequest).convertToJsonString(supplierPacket);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@GET
	@Path("/getAllGlobalItemsByLocationsId/{locationsId}/{supplierId}")
	public String getAllGlobalItemsByLocationsId(@PathParam("locationsId") String locationsId,@PathParam("supplierId") String supplierId ) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			List<ItemToSupplierPacket> supplierPacket = new ItemsServiceBean().getAllGlobalItemsByLocationsId(em, locationsId,supplierId);
			return new JSONUtility(httpRequest).convertToJsonString(supplierPacket);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@GET
	@Path("/getItemBySupplierIdAndLocationId/{supplierId}/{locationId}")
	public String getItemBySupplierIdAndLocationId (@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId
			,@PathParam("supplierId") String supplierId,@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<ItemToSupplierPacket> supplierPacket = new ItemsServiceBean().getItemBySupplierIdAndLocationId (em, supplierId,locationId);
			return new JSONUtility(httpRequest).convertToJsonString(supplierPacket);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	@Path("/getItemByLocationIdSelfIntraProduction/{locationId}")
	public String getItemByLocationIdSelfIntraProduction(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId
			,@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<ItemToSupplierPacket> supplierPacket = new ItemsServiceBean().getItemByLocationIdSelfIntraProduction (em,locationId);
			return new JSONUtility(httpRequest).convertToJsonString(supplierPacket);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	@Path("/getItemByLocationIdProductionManagement/{locationId}")
	public String getItemByLocationIdProductionManagement(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId
			,@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<ItemToSupplierPacket> supplierPacket = new ItemsServiceBean().getItemByLocationIdProductionMGT (em,locationId);
			return new JSONUtility(httpRequest).convertToJsonString(supplierPacket);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	@Path("/getItemsByCategory/{categoryId}")
	public String getItemsByCategory(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId
			,@PathParam("categoryId") String categoryId) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return new ItemsServiceBean().getItemsByCategory(categoryId, em, httpRequest);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@GET
	@Path("/getItemBySupplierIdAndLocationIdForBusiness/{supplierId}/{locationId}")
	public String getItemBySupplierIdAndLocationIdForBusiness (@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId
			,@PathParam("supplierId") String supplierId,@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<ItemToSupplierPacket> supplierPacket = new ItemsServiceBean().getItemBySupplierIdAndLocationIdForBusiness (em, supplierId,locationId);
			return new JSONUtility(httpRequest).convertToJsonString(supplierPacket);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@POST
	@Path("/addUpdateItemsToLocations")
	public String addUpdateItemsToLocations(ItemsToLocationPacket itemsToLocationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, itemsToLocationPacket);
			tx = em.getTransaction();
			tx.begin();
			  
			itemsToLocationPacket=new ItemsServiceBean().addUpdateItemsToLocations( em, itemsToLocationPacket);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.ItemsService_update.name(), itemsToLocationPacket);

			String json = new StoreForwardUtility().returnJsonPacket(itemsToLocationPacket, "ItemsToLocationPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					itemsToLocationPacket.getLocationId(), Integer.parseInt(itemsToLocationPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(itemsToLocationPacket);
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
	@Path("/getItemsToLocations/{itemId}/{locationId}/{fromLocationId}")
	public String getItemsToLocations (@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId
			,@PathParam("itemId") String itemId,@PathParam("locationId") String locationId,@PathParam("fromLocationId") String fromLocationId) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<Item> items = new ItemsServiceBean().getItemsByToAndFromLocationId (em, itemId,locationId,fromLocationId);
			return new JSONUtility(httpRequest).convertToJsonString(items);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
   }
	@GET
	@Path("/getAddLaterStatusByItemIdAndLocation/{globalItemId}/{locationId}")
	public boolean getAddLaterStatusByItemIdAndLocation(@PathParam("globalItemId") String globalItemId, @PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			FutureUpdate futureUpdate = new ItemsServiceBean().getAddLaterStatusByItemIdAndLocation(em, globalItemId, locationId);
			if(futureUpdate!=null){
				return true;
			} 
		 
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return false;
	}
	@POST
	@Path("/addUpdateItemToDate")
	public String addUpdateItemToDate(ItemToDatePacket packet) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			
			//#46374  I am able to add same item in the same date for scheduling
			try {
				
				String sqlForGlobalItemId = " select i from ItemToDate i where i.locationId =? and i.itemId = ? and i.status not in ('D','I') "
						+ "and i.date = ?";
				@SuppressWarnings("unchecked")
				List<ItemToDate> resultList = em.createQuery(sqlForGlobalItemId)
						.setParameter(1, packet.getItemToDate().getLocationId()).setParameter(2, packet.getItemToDate().getItemId())
						.setParameter(3, packet.getItemToDate().getDate())
						.getResultList();
				
				
				if(resultList != null && resultList.size() > 0)
				{
					return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_DUPLICATE_ITEMS_SCHEDULE_ON_SAME_DATE, MessageConstants.ERROR_MESSAGE_DUPLICATE_ITEMS_SCHEDULE_ON_SAME_DATE, null).toString();
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				logger.severe(e);
			}
			
			ItemToDatePacket result = new ItemsServiceBean().addUpdateItemToDate(em, packet);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.ItemsService_addUpdateItemToDate.name(), packet);
			
			String json = new StoreForwardUtility().returnJsonPacket(result, "ItemToDatePacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					result.getLocationId(), Integer.parseInt(result.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
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
	@Path("/deleteItemToDate")
	public String deleteItemToDate(ItemToDatePacket packet) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			ItemToDatePacket result = new ItemsServiceBean().deleteItemToDate(em, packet);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.ItemsService_deleteItemToDate.name(), packet);
			
			String json = new StoreForwardUtility().returnJsonPacket(result, "ItemToDatePacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					result.getLocationId(), Integer.parseInt(result.getMerchantId()));
			
		
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
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
	@Path("/getItemToDate/{categoryId}/{locationId}")
	public String getItemToDate (@PathParam("categoryId") String categoryId,@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			List<ItemToDate> items = new ItemsServiceBean().getItemToDate(em, categoryId,locationId);
			return new JSONUtility(httpRequest).convertToJsonString(items);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
   }
	
	@GET
	@Path("/getItemToDate/{locationId}")
	public String getItemToDate (@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			List<ItemToDate> items = new ItemsServiceBean().getItemToDate(em, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(items);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
   }
	
	@POST
	@Path("/updateItemAvailability")
	public String updateItemAvailability(ItemPacket itemPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			Item item = new ItemsServiceBean().updateItemAvailability(em, itemPacket.getItem());
			tx.commit();
			itemPacket.setItem(item);
			sendPacketForBroadcast(POSNServiceOperations.ItemsService_update.name(), itemPacket);
			itemPacket.setItem(item);
			String json = new StoreForwardUtility().returnJsonPacket(itemPacket, "ItemPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					itemPacket.getLocationId(), Integer.parseInt(itemPacket.getMerchantId()));
			
		
			return new JSONUtility(httpRequest).convertToJsonString(item);
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
	@Path("/itemToDate/{itemId}/{date}")
	public String itemToDate (@PathParam("itemId") String itemId,@PathParam("date") String date) throws Exception
	{
		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			List<ItemToDate> items = new ItemsServiceBean().getItemToDateRecords(em, itemId,date);
			return new JSONUtility(httpRequest).convertToJsonString(items);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
   }
	
	@GET
	@Path("/getItemsBySupplierAndLocationIdForLocation/{supplierId}/{locationId}")
	public String getItemsBySupplierAndLocationIdForLocation(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId
	,@PathParam("supplierId") String supplierId,@PathParam("locationId") String locationId) throws Exception
	{
	EntityManager em = null;

	try
	{
	em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
	List<ItemToSupplierPacket> supplierPacket = new ItemsServiceBean().getItemBySupplierId(em, supplierId,locationId,true);
	return new JSONUtility(httpRequest).convertToJsonString(supplierPacket);
	}
	finally
	{
	LocalSchemaEntityManager.getInstance().closeEntityManager(em);
	}
	}
}
