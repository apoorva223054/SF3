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

import com.nirvanaxp.types.entities.catalog.category.Category;
import com.nirvanaxp.types.entities.catalog.category.CategoryToDiscount;
import com.nirvanaxp.types.entities.catalog.category.CategoryToDiscount_;
import com.nirvanaxp.types.entities.catalog.category.CategoryToPrinter;
import com.nirvanaxp.types.entities.catalog.category.CategoryToPrinter_;
import com.nirvanaxp.types.entities.catalog.category.Category_;

public class CategoryRelationsHelper
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

	public List<CategoryToPrinter> getCategoryToPrinter(String categoryId, EntityManager em)
	{
		if (categoryId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CategoryToPrinter> criteria = builder.createQuery(CategoryToPrinter.class);
			Root<CategoryToPrinter> r = criteria.from(CategoryToPrinter.class);
			if (shouldEliminateDStatus)
			{
				TypedQuery<CategoryToPrinter> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryToPrinter_.categoryId), categoryId),
						builder.notEqual(r.get(CategoryToPrinter_.status), "D")));
				return query.getResultList();
			}
			else
			{
				TypedQuery<CategoryToPrinter> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryToPrinter_.categoryId), categoryId)));
				return query.getResultList();
			}

		}
		return null;

	}

	public List<CategoryToPrinter> getCategoryToPrinterAlongWithDStatus(String categoryId, EntityManager em)
	{
		// TODO uzma - handle result set for category to printer

		if (categoryId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CategoryToPrinter> criteria = builder.createQuery(CategoryToPrinter.class);
			Root<CategoryToPrinter> r = criteria.from(CategoryToPrinter.class);
			if (shouldEliminateDStatus)
			{
				TypedQuery<CategoryToPrinter> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryToPrinter_.categoryId), categoryId)));
				return query.getResultList();
			}
			else
			{
				TypedQuery<CategoryToPrinter> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryToPrinter_.categoryId), categoryId)));
				return query.getResultList();
			}

		}
		return null;

	}

	public List<CategoryToDiscount> getCategoryToDiscountAlongWithDStatus(String categoryId, EntityManager em)
	{
		// TODO uzma - handle result set for category to discount

		if (categoryId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CategoryToDiscount> criteria = builder.createQuery(CategoryToDiscount.class);
			Root<CategoryToDiscount> r = criteria.from(CategoryToDiscount.class);
			if (shouldEliminateDStatus)
			{
				try {
					TypedQuery<CategoryToDiscount> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryToDiscount_.categoryId), categoryId)));
					return query.getResultList();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				try {
					TypedQuery<CategoryToDiscount> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryToDiscount_.categoryId), categoryId)));
					return query.getResultList();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return null;

	}

	public List<CategoryToDiscount> getCategoryToDiscounts(String categoryId, EntityManager em)
	{
		// TODO uzma - handle result set for category to discount

		if (categoryId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CategoryToDiscount> criteria = builder.createQuery(CategoryToDiscount.class);
			Root<CategoryToDiscount> r = criteria.from(CategoryToDiscount.class);
			if (shouldEliminateDStatus)
			{
				TypedQuery<CategoryToDiscount> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryToDiscount_.categoryId), categoryId),
						builder.notEqual(r.get(CategoryToDiscount_.status), "D")));
				return query.getResultList();
			}
			else
			{
				TypedQuery<CategoryToDiscount> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryToDiscount_.categoryId), categoryId)));
				return query.getResultList();
			}

		}
		return null;

	}

	public List<Category> getSubCategoriesForCategoryId(String parentCategoryId, EntityManager em)
	{
		if (parentCategoryId != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
			Root<Category> r = criteria.from(Category.class);
			try {
				TypedQuery<Category> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Category_.categoryId), parentCategoryId)));
				return query.getResultList();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return null;

	}

	public List<CategoryToPrinter> getCategoryToPrintersForCategories(List<String> categoryIds, EntityManager em)
	{
		String questionMark = "";
		if (categoryIds.size() > 0)
		{
			for (int i = 1; i <= categoryIds.size(); i++)
			{
				if (i == categoryIds.size())
				{
					questionMark = questionMark + "?";
				}
				else
				{
					questionMark = questionMark + "?,";
				}
			}

			String queryString = "select ctp from CategoryToPrinter ctp where ctp.categoryId IN (" + questionMark + ")";
			TypedQuery<CategoryToPrinter> query = em.createQuery(queryString, CategoryToPrinter.class);
			for (int i = 1; i <= categoryIds.size(); i++)
			{
				query.setParameter(i, categoryIds.get(i - 1));
			}

			return query.getResultList();
		}
		else
		{
			return null;
		}
	}

	public List<CategoryToDiscount> getCategoryToDiscountsForCategories(List<String> categoryIds, EntityManager em)
	{
		String questionMark = "";
		if (categoryIds.size() > 0)
		{
			for (int i = 1; i <= categoryIds.size(); i++)
			{
				if (i == categoryIds.size())
				{
					questionMark = questionMark + "?";
				}
				else
				{
					questionMark = questionMark + "?,";
				}
			}
			String queryString = "select ctp from CategoryToDiscount ctp where ctp.categoryId IN (" + questionMark + ")";
			TypedQuery<CategoryToDiscount> query = em.createQuery(queryString, CategoryToDiscount.class);
			for (int i = 1; i <= categoryIds.size(); i++)
			{
				query.setParameter(i, categoryIds.get(i - 1));
			}

			return query.getResultList();
		}
		else
		{
			return null;
		}
	}

 
}
