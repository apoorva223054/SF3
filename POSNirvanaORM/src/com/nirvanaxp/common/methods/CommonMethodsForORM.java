package com.nirvanaxp.common.methods;

import javax.persistence.EntityManager;

/**
 * @author XPERT
 *
 */
public class CommonMethodsForORM 
{
 	public Object getObjectById(String objClass,EntityManager em,Class resultClass,String id){
		try {
			String queryString = "select l from "+objClass+" l where l.id =? ";
			return em.createQuery(queryString, resultClass).setParameter(1,id).getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
}