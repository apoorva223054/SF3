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

import com.nirvanaxp.types.entities.user.UsersToLocation;
import com.nirvanaxp.types.entities.user.UsersToLocation_;
import com.nirvanaxp.types.entities.user.UsersToRole;
import com.nirvanaxp.types.entities.user.UsersToRole_;


/**
 * The Class UserRelationHelper.
 */
public class UserRelationHelper
{

	/**
	 * Gets the users to location.
	 *
	 * @param userId the user id
	 * @param em the em
	 * @param shouldEliminateDStatus the should eliminate D status
	 * @return the users to location
	 */
	public List<UsersToLocation> getUsersToLocation(String userId, EntityManager em, boolean shouldEliminateDStatus)
	{
		if (userId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UsersToLocation> criteria = builder.createQuery(UsersToLocation.class);
			Root<UsersToLocation> r = criteria.from(UsersToLocation.class);
			if (shouldEliminateDStatus)
			{
				TypedQuery<UsersToLocation> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(UsersToLocation_.usersId), userId),
						builder.notEqual(r.get(UsersToLocation_.status), "D")));
				return query.getResultList();
			}
			else
			{
				TypedQuery<UsersToLocation> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(UsersToLocation_.usersId), userId)));
				return query.getResultList();
			}
		}
		return null;

	}

	/**
	 * Gets the users to role.
	 *
	 * @param userId the user id
	 * @param em the em
	 * @param shouldEliminateDStatus the should eliminate D status
	 * @return the users to role
	 */
	public List<UsersToRole> getUsersToRole(String userId, EntityManager em, boolean shouldEliminateDStatus)
	{
		if (userId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UsersToRole> criteria = builder.createQuery(UsersToRole.class);
			Root<UsersToRole> r = criteria.from(UsersToRole.class);
			if (shouldEliminateDStatus)
			{
				TypedQuery<UsersToRole> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(UsersToRole_.usersId), userId), builder.notEqual(r.get(UsersToRole_.status), "D")));
				return query.getResultList();
			}
			else
			{
				TypedQuery<UsersToRole> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(UsersToRole_.usersId), userId)));
				return query.getResultList();
			}
		}
		return null;

	}

}
