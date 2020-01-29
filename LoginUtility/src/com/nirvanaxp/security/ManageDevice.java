/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.security;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.nirvanaxp.global.types.entities.accounts.AccountsToBusiness;
import com.nirvanaxp.global.types.entities.accounts.AccountsToBusiness_;
import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfoToAccounts;
import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfoToAccounts_;
import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfoToBusiness;
import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfoToBusiness_;

public class ManageDevice
{

	public List<DeviceInfoToAccounts> getDeviceInfoToAccountsForDeviceIdAndAccountId(EntityManager em, int accountid, int deviceInfoId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<DeviceInfoToAccounts> criteria = builder.createQuery(DeviceInfoToAccounts.class);
		Root<DeviceInfoToAccounts> root = criteria.from(DeviceInfoToAccounts.class);
		TypedQuery<DeviceInfoToAccounts> query = em.createQuery(criteria.select(root).where(builder.equal(root.get(DeviceInfoToAccounts_.accountId), accountid),
				builder.equal(root.get(DeviceInfoToAccounts_.deviceInfoId), deviceInfoId), builder.equal(root.get(DeviceInfoToAccounts_.status), "A")));
		return query.getResultList();
	}

	public List<DeviceInfoToBusiness> getDeviceInfoToBusinessForDeviceIdAndBusinessId(int businessId, int deviceInfoId, EntityManager em)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<DeviceInfoToBusiness> criteria = builder.createQuery(DeviceInfoToBusiness.class);
		Root<DeviceInfoToBusiness> root = criteria.from(DeviceInfoToBusiness.class);
		TypedQuery<DeviceInfoToBusiness> query = em.createQuery(criteria.select(root).where(builder.equal(root.get(DeviceInfoToBusiness_.businessId), businessId),
				builder.equal(root.get(DeviceInfoToBusiness_.deviceInfoId), deviceInfoId), builder.equal(root.get(DeviceInfoToBusiness_.status), "A")));
		return query.getResultList();
	}

	public List<AccountsToBusiness> getAcccountToBusinessForAccountId(EntityManager em, int accountId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<AccountsToBusiness> criteria = builder.createQuery(AccountsToBusiness.class);
		Root<AccountsToBusiness> root = criteria.from(AccountsToBusiness.class);
		TypedQuery<AccountsToBusiness> query = em.createQuery(criteria.select(root).where(builder.equal(root.get(AccountsToBusiness_.accountsId), accountId)));

		return query.getResultList();
	}

}
