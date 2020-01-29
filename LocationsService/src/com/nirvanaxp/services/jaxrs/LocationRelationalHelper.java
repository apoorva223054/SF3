/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.nirvanaxp.types.entities.inventory.LocationsToSupplier;
import com.nirvanaxp.types.entities.inventory.LocationsToSupplier_;

public class LocationRelationalHelper
{

	public List<LocationsToSupplier> getAllLocationForSupplierId(String supplierId, EntityManager em)
	{
		if (supplierId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<LocationsToSupplier> criteria = builder.createQuery(LocationsToSupplier.class);
			Root<LocationsToSupplier> r = criteria.from(LocationsToSupplier.class);
			TypedQuery<LocationsToSupplier> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(LocationsToSupplier_.supplierId), supplierId)));
			return query.getResultList();

		}
		return null;

	}

}
