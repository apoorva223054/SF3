/*
 * 
 */
package com.nirvanaxp.services.jaxrs;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.PrinterUtility;
import com.nirvanaxp.services.jaxrs.packets.KDSToOrderDetailItemStatusPacket;
import com.nirvanaxp.types.entities.orders.KDSToOrderDetailItemStatus;

// TODO: Auto-generated Javadoc
/**
 * The Class KDSSingletonUtil.
 */
public class KDSSingletonUtil
{
	private final static NirvanaLogger logger = new NirvanaLogger(KDSSingletonUtil.class.getName());

	/**
	 * The Class SingletonHolder.
	 */
	private static class SingletonHolder
	{

		/** The Constant THE_SINGLETON. */
		public static final KDSSingletonUtil THE_SINGLETON = new KDSSingletonUtil();
	}

	/**
	 * Instantiates a new KDS singleton util.
	 */
	private KDSSingletonUtil()
	{

	}

	/**
	 * Gets the single instance of KDSSingletonUtil.
	 *
	 * @return single instance of KDSSingletonUtil
	 */
	public static KDSSingletonUtil getInstance()
	{
		return SingletonHolder.THE_SINGLETON;
	}

	/**
	 * Update KDS to order detail item status.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param packet
	 *            the packet
	 */
	public synchronized KDSToOrderDetailItemStatusPacket updateKDSToOrderDetailItemStatus(HttpServletRequest httpRequest, EntityManager em, KDSToOrderDetailItemStatusPacket packet)
	{
		EntityTransaction tx = em.getTransaction();
		try
		{
			List<KDSToOrderDetailItemStatus> kdsToOrderDetailItemStatusList;
			tx.begin();
			kdsToOrderDetailItemStatusList = new PrinterUtility().insertIntoKDSToOrderDetailItemStatusForUpdate(httpRequest, em, packet);
			if (kdsToOrderDetailItemStatusList != null && kdsToOrderDetailItemStatusList.size() != 0)
			{
				packet = new OrderServiceForPost().updateOderDetailItemStatus(em, httpRequest, packet, kdsToOrderDetailItemStatusList, false);
					try {
						if(packet.getIsAllowedToUpdateInventory()==1){
						new OrderManagementServiceBean().manageInventoryForKDSOrders(httpRequest, packet, em, packet.getLocationId());
						
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
			tx.commit();
			packet.setKdsToOrderDetailItemStatusList(kdsToOrderDetailItemStatusList);
			return packet;
		}
		catch (RuntimeException e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}

	}

	/**
	 * Adds the KDS to order detail item status.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param packet
	 *            the packet
	 * @return the list
	 */
	public synchronized List<KDSToOrderDetailItemStatus> addKDSToOrderDetailItemStatus(HttpServletRequest httpRequest, EntityManager em, KDSToOrderDetailItemStatusPacket packet)
	{
		EntityTransaction tx = em.getTransaction();
		try
		{
			List<KDSToOrderDetailItemStatus> kdsToOrderDetailItemStatusList;

			tx.begin();
			kdsToOrderDetailItemStatusList = new PrinterUtility().insertIntoKDSToOrderDetailItemStatus(httpRequest, em, packet.getKdsToOrderDetailItemStatusList());
			if (kdsToOrderDetailItemStatusList != null)
			{
				new OrderServiceForPost().updateOderDetailItemStatus(em, httpRequest, packet, kdsToOrderDetailItemStatusList, true);
			}
			tx.commit();

			return kdsToOrderDetailItemStatusList;
		}
		catch (RuntimeException e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
	}

}
