package com.nirvanaxp.services.jaxrs;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.Business;
import com.nirvanaxp.global.types.entities.accounts.DaylightSavingTime;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.jaxrs.packets.LocationPacket;
import com.nirvanaxp.services.jaxrs.packets.RootLocationPacket;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.Location_;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

public class DaylightSavingThread implements Runnable{

	@Context
	HttpServletRequest httpRequest;

	private static final NirvanaLogger logger = new NirvanaLogger(DaylightSavingThread.class.getName());
	 
	@Override
	public void run() {
		// TODO Auto-generated method stub
		updateDaylightSaving();
//		try {
//            Thread.sleep(1000*60*60);
//            updateDaylightSaving();
//        } catch (InterruptedException ie) {
//        }
	}
	
	private void updateDaylightSaving(){
		EntityManager em=null;
		try{
			em =GlobalSchemaEntityManager.getInstance().getEntityManager();
			// check executiong time .
			TimezoneTime timezoneTime = new TimezoneTime();
			String currentDateTime= timezoneTime.getDateFromTimeStamp(new Timestamp(new TimezoneTime().getGMTTimeInMilis()));
			String queryString = "select l from DaylightSavingTime l where l.executionTimeGmt= ?" ;
			TypedQuery<DaylightSavingTime> query = em.createQuery(queryString, DaylightSavingTime.class).setParameter(1, currentDateTime);
			List<DaylightSavingTime> resultSet = query.getResultList();
			
			for(DaylightSavingTime daylightSavingTime:resultSet){
				// get all business in which we need to update timing
				String queryString1 = "select b from Business b where b.isAutoDatlightSaving=1 " ;
				TypedQuery<Business> query1 = em.createQuery(queryString1, Business.class).setParameter(1, currentDateTime);
				List<Business> businessList = query1.getResultList();
				//connect with local db by schemaname
				
				for(Business business:businessList){
					EntityManager localEm=null;
					try{
						localEm = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(business.getSchemaName());
						
						//fetch location by business id and businness type id
						CriteriaBuilder builder = localEm.getCriteriaBuilder();
						CriteriaQuery<Location> cl = builder.createQuery(Location.class);
						Root<Location> l = cl.from(Location.class);
						TypedQuery<Location> queryLocation = localEm.createQuery(cl.select(l).where(
								builder.and(builder.equal(l.get(Location_.locationsId), null), builder.equal(l.get(Location_.isGlobalLocation), null),
										builder.equal(l.get(Location_.locationsTypeId), 1))));

						List<Location> list= queryLocation.getResultList();
						// update
						for(Location location:list){
							if(location.getTimezoneId()==daylightSavingTime.getFromTimeZoneId()){
								location.setTimezoneId(daylightSavingTime.getToTimeZoneId());
								em.getTransaction().begin();
								location =em.merge(location);
								em.getTransaction().commit();
								// send push
								// get latest login in user session
								String querySQL = "SELECT session_id FROM  user_session "
										+ " where merchant_id=? and login_time=logout_time "
										+ " order by id desc limit 0,1 ";

								@SuppressWarnings("unchecked")
								String clientId =(String) em.createNativeQuery(querySQL).setParameter(1, business.getAccountId()).getSingleResult();
								
								RootLocationPacket rootlocationPacket = new RootLocationPacket();
								rootlocationPacket.setClientId(clientId);
								rootlocationPacket.setEchoString(clientId+"location-update");
								rootlocationPacket.setMerchantId(business.getAccountId()+"");
								rootlocationPacket.setLocationId(location.getId()+"");
								rootlocationPacket.setSchemaName(business.getSchemaName());
								
								sendPacketForBroadcast(POSNServiceOperations.LocationsService_update.name(), rootlocationPacket);
							}
						}
					}catch (Exception e) {
						logger.severe(e);
					}finally {
						LocalSchemaEntityManager.getInstance().closeEntityManager(localEm);
					}
				}
				
			}
			
			
		}catch (Exception e) {
			logger.severe(e);
		}finally {
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	private void sendPacketForBroadcast(String operation, LocationPacket locationPacket)
	{
		try
		{
			operation = ServiceOperationsUtility.getOperationName(operation);
	
			MessageSender messageSender = new MessageSender();
	
			messageSender.sendMessage(httpRequest, locationPacket.getClientId(), POSNServices.LocationsService.name(), operation, null, locationPacket.getMerchantId(), locationPacket.getLocationId(),
				locationPacket.getEchoString(), locationPacket.getSchemaName());
		}
		catch(Exception e)
		{
			logger.severe(e);
		}

	}
}
