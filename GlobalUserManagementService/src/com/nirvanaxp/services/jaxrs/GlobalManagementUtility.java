/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.nirvanaxp.global.types.entities.User;
import com.nirvanaxp.global.types.entities.User_;

public class GlobalManagementUtility
{

	public User getUserByPhoneNumber(String globalPhoneNumber, EntityManager globalEntityManager) throws NoResultException
	{

		CriteriaBuilder builder = globalEntityManager.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> root = criteria.from(User.class);
		TypedQuery<User> query = globalEntityManager.createQuery(criteria.select(root).where(builder.equal(root.get(User_.phone), globalPhoneNumber)));
		List<User> list = query.getResultList();
		User result = null;
		if (list != null && list.size() > 0)
		{
			result = list.get(0);
		}
		return result;

	}

	public User getUserByEmail(String globalEmailId, EntityManager globalEntityManager) throws NoResultException
	{

		CriteriaBuilder builder = globalEntityManager.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> root = criteria.from(User.class);
		TypedQuery<User> query = globalEntityManager.createQuery(criteria.select(root).where(builder.equal(root.get(User_.email), globalEmailId)));
		List<User> list = query.getResultList();
		User result = null;
		if (list != null && list.size() > 0)
		{
			result = list.get(0);
		}
		return result;

	}

	public User getUserByUserName(String username, EntityManager globalEntityManager) throws NoResultException
	{

		CriteriaBuilder builder = globalEntityManager.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> root = criteria.from(User.class);
		TypedQuery<User> query = globalEntityManager.createQuery(criteria.select(root).where(builder.equal(root.get(User_.username), username)));
		List<User> list = query.getResultList();
		User result = null;
		if (list != null && list.size() > 0)
		{
			result = list.get(0);
		}
		return result;

	}
}
