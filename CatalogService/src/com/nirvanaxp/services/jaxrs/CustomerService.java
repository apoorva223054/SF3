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
import javax.persistence.NoResultException;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.types.entities.catalog.category.Category;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
import com.nirvanaxp.types.entities.custom.CatalogDisplayService;

// TODO: Auto-generated Javadoc
/**
 * The Class CustomerService.
 */
@WebListener
@Path("/CustomerService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class CustomerService extends AbstractNirvanaService
{

	/** The http request. */
	@Context
	HttpServletRequest httpRequest;

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(CustomerService.class.getName());

	/**
	 * Gets the root categories.
	 *
	 * @return the root categories
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getRootCategories")
	public String getRootCategories() throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			List<Category> categoriesList = new CatalogServiceBean().getRootCategories(em);

			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the root categories by location id.
	 *
	 * @param locationId the location id
	 * @return the root categories by location id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getRootCategoriesByLocationId/{locationId}")
	public String getRootCategoriesByLocationId(@PathParam("locationId") String locationId ) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			List<Category> categoriesList = new CatalogServiceBean().getRootCategoriesByLocationIdForCustomer(em, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all categories and item by category id.
	 *
	 * @param categoryId the category id
	 * @param locationId the location id
	 * @return the all categories and item by category id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getAllCategoriesAndItemByCategoryIdAndLocationId/{categoryId}/{locationId}")
	public String getAllCategoriesAndItemByCategoryId(@PathParam("categoryId") String categoryId, @PathParam("locationId") String locationId )
			throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			List<CatalogDisplayService> ans = new CatalogServiceBean().getAllCategoriesAndItemByCategoryId(em, categoryId, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(ans);

		}

		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the sub categories.
	 *
	 * @param categoryId the category id
	 * @return the sub categories
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getSubCategories/{categoryId}")
	public String getSubCategories(@PathParam("categoryId") String categoryId) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			List<Category> categoriesList = new CatalogServiceBean().getSubCategories(em, categoryId);
			return new JSONUtility(httpRequest).convertToJsonString(categoriesList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the attribute by item id and att type id.
	 *
	 * @param itemId the item id
	 * @param attTypeId the att type id
	 * @return the attribute by item id and att type id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getAttributeByItemIdAndAttTypeId/{itemId}/{attTypeId}")
	public String getAttributeByItemIdAndAttTypeId(@PathParam("itemId") int itemId, @PathParam("attTypeId") int attTypeId)
			throws Exception
	{

		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			List<ItemsAttribute> attributeList = new ArrayList<ItemsAttribute>();

			String queryString = "select ia.id,ia.name,ia.selling_price,ia.display_name," + "ia.short_name,ia.locations_id,ia.msr_price," + "ia.multi_select,ia.image_name,ia.hex_code_values,"
					+ "ia.description,ia.status,ia.is_active," + "ia.sort_sequence,ia.created,ia.created_by,ia.updated," + "ia.updated_by,ia.stock_uom,ia.sellable_uom"
					+ " from items_attribute ia where ia.status != 'D' and  ia.id in(" + "select itia.items_attribute_id from items_to_items_attribute itia where itia.items_id =" + itemId + " "
					+ " and" + " itia.items_attribute_id in(" + "select iattia.items_attribute_id from items_attribute_type_to_items_attribute iattia" + " where iattia.items_attribute_type_id = "
					+ attTypeId + " " + ") );";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, itemId).setParameter(2, attTypeId).getResultList();
			for (Object[] objRow : resultList)
			{

				if ((String) objRow[0] != null)
				{
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
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the category and sub category and item by category id and location id.
	 *
	 * @param categoryId the category id
	 * @param locationId the location id
	 * @return the category and sub category and item by category id and location id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId/{categoryId}/{locationId}")
	public String getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(@PathParam("categoryId") String categoryId, @PathParam("locationId") String locationId
			) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			List<CatalogDisplayService> ans = new CatalogServiceBean().getCategoryAndSubCategoryAndItemByCategoryIdAndLocationId(em, categoryId, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(ans);

		}
		catch (NoResultException nre)
		{
			NirvanaXPException ne = new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NO_RESULT_EXCEPTION, MessageConstants.ERROR_MESSAGE_NO_RESULT_DISPLAY_MESSAGE,
					null));
			logger.severe(httpRequest, MessageConstants.ERROR_MESSAGE_NO_RESULT_DISPLAY_MESSAGE);
			return ne.toString();
		}
		catch (InvalidSessionException e)
		{
			NirvanaXPException nirvanaXPException = new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_INVALID_SESSION_EXCEPTION,
					MessageConstants.ERROR_MESSAGE_INVALID_SESSION_DISPLAY_MESSAGE, null));
			logger.severe(httpRequest, MessageConstants.ERROR_MESSAGE_INVALID_SESSION_DISPLAY_MESSAGE);
			return nirvanaXPException.toString();
		}
		catch (Throwable t)
		{
			logger.severe(httpRequest, t);
			return new NirvanaXPException(t).toString();
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	/**
	 * Gets the category and sub category and item by location id.
	 *
	 * @param locationId the location id
	 * @return the category and sub category and item by location id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getCategoryAndSubCategoryAndItemByLocationId/{locationId}")
	public String getCategoryAndSubCategoryAndItemByLocationId(@PathParam("locationId") String locationId
			) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			List<CatalogDisplayService> ans = new CatalogServiceBean().getCategoryAndSubCategoryAndItemByLocationId(em, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(ans);

		}
		catch (NoResultException nre)
		{
			NirvanaXPException ne = new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NO_RESULT_EXCEPTION, MessageConstants.ERROR_MESSAGE_NO_RESULT_DISPLAY_MESSAGE,
					null));
			logger.severe(httpRequest, MessageConstants.ERROR_MESSAGE_NO_RESULT_DISPLAY_MESSAGE);
			return ne.toString();
		}
		catch (InvalidSessionException e)
		{
			NirvanaXPException nirvanaXPException = new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_INVALID_SESSION_EXCEPTION,
					MessageConstants.ERROR_MESSAGE_INVALID_SESSION_DISPLAY_MESSAGE, null));
			logger.severe(httpRequest, MessageConstants.ERROR_MESSAGE_INVALID_SESSION_DISPLAY_MESSAGE);
			return nirvanaXPException.toString();
		}
		catch (Throwable t)
		{
			// TODO add code and message for this method
			logger.severe(httpRequest, t);
			return new NirvanaXPException(t).toString();
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the category item by category id and location id.
	 *
	 * @param categoryId the category id
	 * @param locationId the location id
	 * @return the category item by category id and location id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getCategoryItemByCategoryIdAndLocationId/{categoryId}/{locationId}")
	public String getCategoryItemByCategoryIdAndLocationId(@PathParam("categoryId") String categoryId, @PathParam("locationId") String locationId
			) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			List<CatalogDisplayService> ans = new CatalogServiceBean().getCategoryItemByCategoryIdAndLocationId(em, categoryId, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(ans);

		}
		catch (NoResultException nre)
		{
			NirvanaXPException ne = new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NO_RESULT_EXCEPTION, MessageConstants.ERROR_MESSAGE_NO_RESULT_DISPLAY_MESSAGE,
					null));
			logger.severe(httpRequest, MessageConstants.ERROR_MESSAGE_NO_RESULT_DISPLAY_MESSAGE);
			return ne.toString();
		}
		catch (InvalidSessionException e)
		{
			NirvanaXPException nirvanaXPException = new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_INVALID_SESSION_EXCEPTION,
					MessageConstants.ERROR_MESSAGE_INVALID_SESSION_DISPLAY_MESSAGE, null));
			logger.severe(httpRequest, MessageConstants.ERROR_MESSAGE_INVALID_SESSION_DISPLAY_MESSAGE);
			return nirvanaXPException.toString();
		}
		catch (Throwable t)
		{
			logger.severe(httpRequest, t);
			return new NirvanaXPException(t).toString();
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	/**
	 * Gets the categoryanditemslist.
	 *
	 * @param categoryId the category id
	 * @param locationId the location id
	 * @return the categoryanditemslist
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getcategoryanditemslist/{categoryId}/{locationId}")
	public String getcategoryanditemslist(@PathParam("categoryId") String categoryId, @PathParam("locationId") String locationId
			) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			List<CatalogDisplayService> ans = new CatalogServiceBean().getcategoryanditemslist(em, categoryId, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(ans);

		}
		catch (NoResultException nre)
		{
			NirvanaXPException ne = new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NO_RESULT_EXCEPTION, MessageConstants.ERROR_MESSAGE_NO_RESULT_DISPLAY_MESSAGE,
					null));
			logger.severe(httpRequest, MessageConstants.ERROR_MESSAGE_NO_RESULT_DISPLAY_MESSAGE);
			return ne.toString();
		}
		catch (InvalidSessionException e)
		{
			NirvanaXPException nirvanaXPException = new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_INVALID_SESSION_EXCEPTION,
					MessageConstants.ERROR_MESSAGE_INVALID_SESSION_DISPLAY_MESSAGE, null));
			logger.severe(httpRequest, MessageConstants.ERROR_MESSAGE_INVALID_SESSION_DISPLAY_MESSAGE);
			return nirvanaXPException.toString();
		}
		catch (Throwable t)
		{
			// TODO add code and message for this method
			logger.severe(httpRequest, t);
			return new NirvanaXPException(t).toString();
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	
	

	/* (non-Javadoc)
	 * @see com.nirvanaxp.services.jaxrs.INirvanaService#isAlive()
	 */
	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see com.nirvanaxp.services.jaxrs.AbstractNirvanaService#getNirvanaLogger()
	 */
	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
	}

}
