package com.nirvanaxp.services.jaxrs.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.types.entities.salestax.OrderSourceToSalesTax;
import com.nirvanaxp.types.entities.salestax.OrderSourceToSalesTax_;
import com.nirvanaxp.types.entities.salestax.SalesTax;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderSourceToSalesTaxHelper.
 */
public class OrderSourceToSalesTaxHelper
{

	/** The should eliminate D status. */
	public boolean shouldEliminateDStatus = false;

	/**
	 * Update order source to sales tax.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderSourceToSalesTaxs
	 *            the order source to sales taxs
	 * @return the list
	 */
	public List<OrderSourceToSalesTax> updateOrderSourceToSalesTax(HttpServletRequest httpRequest, EntityManager em, List<OrderSourceToSalesTax> orderSourceToSalesTaxs)
	{
		List<OrderSourceToSalesTax> list = new ArrayList<OrderSourceToSalesTax>();
		for (OrderSourceToSalesTax salesTax : orderSourceToSalesTaxs)
		{
			SalesTax salesTaxDB = null;
			if (salesTax.getId() > 0)
			{
				salesTaxDB = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class, salesTax.getTaxId().getId());
				if (salesTaxDB != null)
				{
					salesTax.setCreated(salesTaxDB.getCreated());
				}
				else
				{
					salesTax.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}
			}
			salesTax.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			salesTax = em.merge(salesTax);
			list.add(salesTax);

		}
		return list;
	}

	/**
	 * Gets the order source to sales tax.
	 *
	 * @param id
	 *            the id
	 * @param em
	 *            the em
	 * @return the order source to sales tax
	 */
	public List<OrderSourceToSalesTax> getOrderSourceToSalesTax(int id, EntityManager em)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderSourceToSalesTax> criteria = builder.createQuery(OrderSourceToSalesTax.class);
		Root<OrderSourceToSalesTax> r = criteria.from(OrderSourceToSalesTax.class);
		if (shouldEliminateDStatus)
		{
			TypedQuery<OrderSourceToSalesTax> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceToSalesTax_.sourceId), id),
					builder.notEqual(r.get(OrderSourceToSalesTax_.status), "D")));
			return query.getResultList();
		}
		else
		{
			TypedQuery<OrderSourceToSalesTax> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceToSalesTax_.sourceId), id)));
			return query.getResultList();
		}

	}

	/**
	 * Gets the order source to sales tax by order source id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the order source to sales tax by order source id
	 * @throws SQLException
	 *             the SQL exception
	 */
	public List<OrderSourceToSalesTax> getOrderSourceToSalesTaxByOrderSourceId(HttpServletRequest httpRequest, EntityManager em, int id) throws SQLException
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderSourceToSalesTax> criteria = builder.createQuery(OrderSourceToSalesTax.class);
		Root<OrderSourceToSalesTax> r = criteria.from(OrderSourceToSalesTax.class);
		TypedQuery<OrderSourceToSalesTax> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceToSalesTax_.sourceId), id),
				builder.notEqual(r.get(OrderSourceToSalesTax_.status), "D"), builder.notEqual(r.get(OrderSourceToSalesTax_.status), "D")));
		return query.getResultList();

	}
}
