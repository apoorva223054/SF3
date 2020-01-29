package com.nirvanaxp.types.entities.orders;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.nirvanaxp.common.methods.CommonMethodsForORM;
import com.nirvanaxp.types.entities.catalog.course.Course;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.StorageType;
import com.nirvanaxp.types.entities.inventory.UnitOfMeasurement;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.user.User;

public class OrderHeaderForKDS extends OrderHeader
{


	private String orderSourceName;
	private String orderStatusName;
	private String orderSourceDisplayName;
	private String orderSourceGroupDisplayName;
	private String orderSourceGroupName;
	private String userFirstName;
	private String userLastName;
	private String mergedLocationsName;
	private String phoneNo;
	private String emailId;
	private String poRequestName;
	private boolean isLablePrinter;
	private String ipAddress;

	public String getPoRequestName()
	{
		return poRequestName;
	}

	public void setPoRequestName(String poRequestName)
	{
		this.poRequestName = poRequestName;
	}

	public String getOrderSourceName()
	{
		return orderSourceName;
	}

	public void setOrderSourceName(String orderSourceName)
	{
		this.orderSourceName = orderSourceName;
	}

	public String getOrderStatusName()
	{
		return orderStatusName;
	}

	public void setOrderStatusName(String orderStatusName)
	{
		this.orderStatusName = orderStatusName;
	}

	public String getOrderSourceGroupName()
	{
		return orderSourceGroupName;
	}

	public void setOrderSourceGroupName(String orderSourceGroupName)
	{
		this.orderSourceGroupName = orderSourceGroupName;
	}

	public String getUserFirstName()
	{
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName)
	{
		this.userFirstName = userFirstName;
	}

	public String getUserLastName()
	{
		return userLastName;
	}

	public void setUserLastName(String userLastName)
	{
		this.userLastName = userLastName;
	}

	public String getPhoneNo()
	{
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo)
	{
		this.phoneNo = phoneNo;
	}

	public String getEmailId()
	{
		return emailId;
	}

	public void setEmailId(String emailId)
	{
		this.emailId = emailId;
	}

	public String getMergedLocationsName()
	{
		return mergedLocationsName;
	}

	public void setMergedLocationsName(String mergedLocationsName)
	{
		this.mergedLocationsName = mergedLocationsName;
	}

	private OrderSource getOrderSourceName(EntityManager em, String orderSourceId)
	{
		OrderSource orderSource = null;

		try
		{
			orderSource = (OrderSource) new CommonMethodsForORM().getObjectById("OrderSource", em,OrderSource.class, orderSourceId);

			return orderSource;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private OrderSourceGroup getOrderSourceGroupName(EntityManager em, String orderSourceGroupId)
	{
		OrderSourceGroup orderSourceGroup = null;

		try
		{
			orderSourceGroup = (OrderSourceGroup) new CommonMethodsForORM().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSourceGroupId);
			if (orderSourceGroup != null)
			{
				return orderSourceGroup;
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String getOrderStatusName(EntityManager em, String orderStatusId)
	{
		OrderStatus orderStatus = null;

		try
		{
			orderStatus = (OrderStatus) new CommonMethodsForORM().getObjectById("OrderStatus", em,OrderStatus.class, orderStatusId);
			if (orderStatus != null)
			{
				return orderStatus.getName();
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private User getUser(EntityManager em, String userId)
	{
		User user = null;

		try
		{
			user = (User) new CommonMethodsForORM().getObjectById("User", em,User.class, userId);

			return user;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String getLocationName(EntityManager em, String locationsId)
	{
		Location location = null;

		try
		{

			if (locationsId != null && locationsId.length() > 0)
			{
				String locationId = locationsId;
				location = (Location) new CommonMethodsForORM().getObjectById("Location", em,Location.class, locationId);;
				if (location != null)
				{
					return location.getName();
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String getOrderDetailStatusName(EntityManager em, int statusId)
	{
		OrderDetailStatus detailStatus = em.find(OrderDetailStatus.class, statusId);
		return detailStatus.getName();
	}

	private String getCourseName(EntityManager em, String courseId)
	{
		try
		{
			Course course = (Course) new CommonMethodsForORM().getObjectById("Course", em,Course.class, courseId);
			return course.getCourseName();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String getupdatedByName(EntityManager em, String userId)
	{
		try
		{
			if(userId !=null && !userId.equals("")){
				String queryString = "select u from User u where u.id = '"+userId+"'";
				TypedQuery<User> query = em.createQuery(queryString, User.class);
				User user = query.getSingleResult();
				if(user != null)
				{	
			return user.getFirstName() + " " + user.getLastName();
			}
		}}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public OrderHeaderForKDS getOrderHeaderWithLimitedFields(EntityManager em,OrderHeader header,String locationId, String time,
			String poRequestName) {
		
		try {
			List<OrderDetailItem>orderdetailItemKDSList=new ArrayList<OrderDetailItem>();
			OrderHeaderForKDS kds=new OrderHeaderForKDS();
			String locationsId=null;
			if(locationId!=null && locationId.length()>0){
			 locationsId=locationId;
			}
			kds.setId(header.getId());
			kds.setOrderSourceId(header.getOrderSourceId());
			kds.setOrderStatusId(header.getOrderStatusId());
			kds.setPointOfServiceCount(header.getPointOfServiceCount());
			kds.setReservationsId(header.getReservationsId());
			kds.setCreated(header.getCreated());
			kds.setCreatedBy(header.getCreatedBy());
			kds.setUpdated(header.getUpdated());
			kds.setUpdatedBy(header.getUpdatedBy());
			kds.setLocationsId(locationsId);
			kds.setScheduleDateTime(header.getScheduleDateTime());
			kds.setFirstName(header.getFirstName());
			kds.setLastName(header.getLastName());
			kds.setServerId(header.getServerId());
			kds.setMergedLocationsId(header.getMergedLocationsId());
			kds.setOpenTime(header.getOpenTime());
			kds.setIsTabOrder(header.getIsTabOrder());
			kds.setIsOrderReopened(header.getIsOrderReopened());
			kds.setShiftSlotId(header.getShiftSlotId());
			kds.setServername(header.getServername());
			kds.setOrderNumber(header.getOrderNumber());
			kds.setIsSeatWiseOrder(header.getIsSeatWiseOrder());
			kds.setMergeOrderId(header.getMergeOrderId());
			kds.setPoRequestName(poRequestName);
			kds.setMergedLocationsId(header.getMergedLocationsId());
			kds.setLocationName(getLocationName(em, ""+header.getLocationsId()));
			kds.setPartySizeUpdated(header.isPartySizeUpdated());
			kds.setOrderHeaderToSeatDetails(header.getOrderHeaderToSeatDetails());
			kds.setOrderTypeId(header.getOrderTypeId());
			if(header.getUsersId()!=null){
				kds.setUsersId(header.getUsersId());
				User user=getUser(em, header.getUsersId());
				if(user!=null){
				kds.setUserFirstName(user.getFirstName());
				kds.setUserLastName(user.getLastName());;
				kds.setPhoneNo(user.getPhone());
				kds.setEmailId(user.getEmail());
				}
			}
		
			OrderSource orderSource=getOrderSourceName(em, header.getOrderSourceId());
			if(orderSource!=null){
			kds.setOrderSourceName(orderSource.getName());
			kds.setOrderSourceGroupName(getOrderSourceGroupName(em, orderSource.getOrderSourceGroupId()).getName());
			kds.setOrderSourceDisplayName(orderSource.getDisplayName());
			kds.setOrderSourceGroupDisplayName(getOrderSourceGroupName(em, orderSource.getOrderSourceGroupId()).getDisplayName());
			}
			kds.setOrderStatusName(getOrderStatusName(em, header.getOrderStatusId()));
			kds.setMergedLocationsName(getLocationName(em, header.getMergedLocationsId()));
			if(header.getOrderDetailItems()!=null){
			for(OrderDetailItem detailItem:header.getOrderDetailItems()){
				OrderDetailItem orderDetailItemKDS=new OrderDetailItem();
				orderDetailItemKDS.setId(detailItem.getId());
				orderDetailItemKDS.setItemsId(detailItem.getItemsId());
				BigDecimal itemsQty = (detailItem.getItemsQty()).setScale(2, RoundingMode.HALF_DOWN);
				orderDetailItemKDS.setItemsQty(itemsQty);
				if(!detailItem.getItemsShortName().equals("Pour My Beer")){
					orderDetailItemKDS.setItemsShortName(detailItem.getItemsShortName());
				}else {
					orderDetailItemKDS.setItemsShortName(detailItem.getItemsShortName()+"-"+detailItem.getPlu());
				}
				
				orderDetailItemKDS.setOrderDetailStatusId(detailItem.getOrderDetailStatusId());
				orderDetailItemKDS.setUpdated(detailItem.getUpdated());
				orderDetailItemKDS.setCreated(detailItem.getCreated());
				orderDetailItemKDS.setUpdatedBy(detailItem.getUpdatedBy());
				orderDetailItemKDS.setParentCategoryId(detailItem.getParentCategoryId());
				orderDetailItemKDS.setRootCategoryId(detailItem.getRootCategoryId());
				orderDetailItemKDS.setIsTabOrderItem(detailItem.getIsTabOrderItem());
				orderDetailItemKDS.setSentCourseId(detailItem.getSentCourseId());
				orderDetailItemKDS.setOrderDetailStatusName(getOrderDetailStatusName(em, detailItem.getOrderDetailStatusId()));
				orderDetailItemKDS.setCourseName(getCourseName(em, detailItem.getSentCourseId()));
				orderDetailItemKDS.setUpdatedByName(getupdatedByName(em, detailItem.getUpdatedBy()));
				
				try
				{
					String queryString = "select uom from UnitOfMeasurement uom where uom.id in (select i.stockUom from Item i where i.id = ?)";
					TypedQuery<UnitOfMeasurement> query = em.createQuery(queryString, UnitOfMeasurement.class).setParameter(1, detailItem.getItemsId());
					UnitOfMeasurement uom = query.getSingleResult();
					if(uom != null)
					{
						orderDetailItemKDS.setStockUomName(uom.getName());	
					}
					
				}
				catch (Exception e)
				{
					// TODO: handle exception
					e.printStackTrace();
				}
				try
				{
					String queryString = "select i from Item i where i.id = ?)";
					TypedQuery<Item> query = em.createQuery(queryString, Item.class).setParameter(1, detailItem.getItemsId());
					Item item = query.getSingleResult();
					if(item != null)
					{
						orderDetailItemKDS.setLabelIngredients(item.getLabelIngredients());
						orderDetailItemKDS.setContains(item.getContains());
					}
					
				}
				catch (Exception e)
				{
					// TODO: handle exception
					e.printStackTrace();
				}
				try
				{
					String queryString = "select st from StorageType st where st.id in (select i.storageTypeId from Item i where i.id = ?)";
					TypedQuery<StorageType> query = em.createQuery(queryString, StorageType.class).setParameter(1, detailItem.getItemsId());
					StorageType st = query.getSingleResult();
					if(st != null)
					{
						orderDetailItemKDS.setStorageType(st);
					}
					
				}
				catch (Exception e)
				{
					// TODO: handle exception
					e.printStackTrace();
				}
				
				
				if(detailItem.getSeatId()!=null){
				orderDetailItemKDS.setSeatId(detailItem.getSeatId());
				}
				orderDetailItemKDS.setDeviceToKDSIds(detailItem.getDeviceToKDSIds());
				List<OrderDetailAttribute>OrderDetailAttributeKDSList=new ArrayList<OrderDetailAttribute>();
				if(detailItem.getOrderDetailAttributes()!=null){
				for(OrderDetailAttribute attribute:detailItem.getOrderDetailAttributes()){
					OrderDetailAttribute attributeKDS=new OrderDetailAttribute();
					attributeKDS.setId(attribute.getId());
					attributeKDS.setItemsAttributeId(attribute.getItemsAttributeId());
					attributeKDS.setOrderDetailStatusId(attribute.getOrderDetailStatusId());
					attributeKDS.setItemsAttributeName(attribute.getItemsAttributeName());
					attributeKDS.setOrderDetailStatusName(getOrderDetailStatusName(em, detailItem.getOrderDetailStatusId()));
					attributeKDS.setLocalTime(time);
					attributeKDS.setCreated(attribute.getCreated());
					OrderDetailAttributeKDSList.add(attributeKDS);
				}
				}
				orderDetailItemKDS.setOrderDetailAttributes(OrderDetailAttributeKDSList);
				orderdetailItemKDSList.add(orderDetailItemKDS);
			}}
			if(orderdetailItemKDSList!=null){
			kds.setOrderDetailItems(orderdetailItemKDSList);
			}
			return kds;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	public String getOrderSourceDisplayName()
	{
		return orderSourceDisplayName;
	}

	public void setOrderSourceDisplayName(String orderSourceDisplayName)
	{
		this.orderSourceDisplayName = orderSourceDisplayName;
	}

	public String getOrderSourceGroupDisplayName()
	{
		return orderSourceGroupDisplayName;
	}

	public void setOrderSourceGroupDisplayName(String orderSourceGroupDisplayName)
	{
		this.orderSourceGroupDisplayName = orderSourceGroupDisplayName;
	}
	public OrderHeaderForKDS getOrderHeaderWithLimitedFieldsForUserAndSchedule(EntityManager em,OrderHeader header,String printString) throws JsonParseException, JsonMappingException, IOException  {
		
		try {
			OrderHeaderForKDS kds = new ObjectMapper().readValue(printString, OrderHeaderForKDS.class);
			
			kds.setId(header.getId());
			kds.setUpdated(header.getUpdated());
			kds.setUpdatedBy(header.getUpdatedBy());
			kds.setOrderSourceId(header.getOrderSourceId());
			kds.setOrderStatusId(header.getOrderStatusId());
			kds.setPointOfServiceCount(header.getPointOfServiceCount());
			kds.setScheduleDateTime(header.getScheduleDateTime());
			kds.setFirstName(header.getFirstName());
			kds.setLastName(header.getLastName());
			kds.setIsTabOrder(header.getIsTabOrder());
			kds.setShiftSlotId(header.getShiftSlotId());
			kds.setPartySizeUpdated(header.isPartySizeUpdated());
			kds.setOrderTypeId(header.getOrderTypeId());
			if(header.getUsersId()!=null){
				kds.setUsersId(header.getUsersId());
				User user=getUser(em, header.getUsersId());
				if(user!=null){
				kds.setUserFirstName(user.getFirstName());
				kds.setUserLastName(user.getLastName());;
				kds.setPhoneNo(user.getPhone());
				kds.setEmailId(user.getEmail());
				}
			}
		
			OrderSource orderSource=getOrderSourceName(em, header.getOrderSourceId());
			if(orderSource!=null){
			kds.setOrderSourceName(orderSource.getName());
			kds.setOrderSourceGroupName(getOrderSourceGroupName(em, orderSource.getOrderSourceGroupId()).getName());
			kds.setOrderSourceDisplayName(orderSource.getDisplayName());
			kds.setOrderSourceGroupDisplayName(getOrderSourceGroupName(em, orderSource.getOrderSourceGroupId()).getDisplayName());
			}
			kds.setOrderStatusName(getOrderStatusName(em, header.getOrderStatusId()));
			
			return kds;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	public boolean isLablePrinter() {
		return isLablePrinter;
	}

	public void setLablePrinter(boolean isLablePrinter) {
		this.isLablePrinter = isLablePrinter;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
}
