/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.common.utils.relationalentity.helper;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.nirvanaxp.types.entities.roles.RolesToFunction;
import com.nirvanaxp.types.entities.roles.RolesToFunction_;

public class RoleRelationsHelper
{
	public boolean shouldEliminateDStatus = false;

	public boolean isShouldEliminateDStatus()
	{
		return shouldEliminateDStatus;
	}

	public void setShouldEliminateDStatus(boolean shouldEliminateDStatus)
	{
		this.shouldEliminateDStatus = shouldEliminateDStatus;
	}

	public List<RolesToFunction> getRoleToFunctions(String roleId, EntityManager em)
	{
		if (roleId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<RolesToFunction> criteria = builder.createQuery(RolesToFunction.class);
			Root<RolesToFunction> r = criteria.from(RolesToFunction.class);
			if (shouldEliminateDStatus)
			{
				TypedQuery<RolesToFunction> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(RolesToFunction_.rolesId), roleId),
						builder.notEqual(r.get(RolesToFunction_.status), "D")));
				return query.getResultList();
			}
			else
			{
				TypedQuery<RolesToFunction> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(RolesToFunction_.rolesId), roleId)));
				return query.getResultList();
			}
		}
		return null;

	}

}
