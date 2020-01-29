package com.nirvanaxp.services.jaxrs;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.catalog.course.Course;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.receipt.PrinterReceipt;
import com.nirvanaxp.types.entities.orders.OrderDetailAttribute;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup;
import com.nirvanaxp.types.entities.payment.PaymentMethod;
import com.nirvanaxp.types.entities.payment.PaymentMethodType;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType;
import com.nirvanaxp.types.entities.salestax.SalesTax;
import com.nirvanaxp.types.entities.user.User;

// TODO: Auto-generated Javadoc
/**
 * The Class CloudReceiptFormat.
 */
public class CloudReceiptFormat {


	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(CloudReceiptFormat.class.getName());

	/**
	 * Creates the receipt PDF string.
	 *
	 * @param em the em
	 * @param httpRequest the http request
	 * @param orderId the order id
	 * @param takeoutDelivery the takeout delivery
	 * @param globalEM the global EM
	 * @param printerId the printer id
	 * @return the string
	 */
	public String createReceiptPDFString(EntityManager em, HttpServletRequest httpRequest, String orderId, int takeoutDelivery,EntityManager globalEM,String printerId)
	{
		
		// todo shlok need
		// modularise code for method
		String req = null;
		String feedbackUrl = null;
	
		try
		{
			feedbackUrl = ConfigFileReader.getAdminFeedbackURL();
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, "getWebsiteLogoPath not found in database for" + orderId);
		}

		OrderManagementServiceBean managementServiceBean = new OrderManagementServiceBean();
		String discountName = "";

		OrderHeader orderHeader = managementServiceBean.getOrderById(em, orderId);
		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderHeader.getOrderSourceId());
		OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
		if(orderSourceGroup.getName().equals("In Store")){
			takeoutDelivery =0;
		}else{
			takeoutDelivery =1;
		}

		if (orderHeader.getDiscountsId() != null && orderHeader.getDiscountsName() != null)
		{
			discountName = orderHeader.getDiscountsName();
		}
		
		
		
		User server = (User) new CommonMethods().getObjectById("User", em,User.class, orderHeader.getServerId());
		String serverName = "";
		if (server != null)
		{
			serverName = (server.getFirstName().substring(0,1) + " " + server.getLastName().substring(0,1)).toUpperCase();
		}
		User cashier = (User) new CommonMethods().getObjectById("User", em,User.class,  orderHeader.getCashierId());
		String cashierName = "";
		if (cashier != null)
		{
			cashierName = (cashier.getFirstName().substring(0,1) + " " + cashier.getLastName().substring(0,1)).toUpperCase();
		}
		Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, orderHeader.getLocationsId());
		Address localAddress = null;
		List<Location> result = null;
		Location resultSet = null;
		List<Course> courses = null;
		if (location != null && location.getLocationsId() != null)
		{
			String queryString = "select l from Location l where (l.locationsId ='0' or l.locationsId is null) and l.businessId=" + location.getBusinessId() + " order by l.id asc";
			TypedQuery<Location> query = em.createQuery(queryString, Location.class);
			result = query.getResultList();
			if (result != null && result.size() > 0)
			{
				resultSet = result.get(0);
			}
			localAddress = resultSet.getAddress();
		}
		else
		{
			localAddress = location.getAddress();
			resultSet = location;

		}
		
		String queryString = "select p from PrinterReceipt p where p.locationId>=?  order by displaySequence asc ";
		TypedQuery<PrinterReceipt> query2 = em.createQuery(queryString, PrinterReceipt.class).setParameter(1, resultSet.getId());
		List<PrinterReceipt> printerReceiptList = query2.getResultList();
		
		String datetime = "";

		try
		{
			TimezoneTime time = new TimezoneTime();
			datetime = time.getDateTimeFromGMTToLocation(em, orderHeader.getScheduleDateTime(), resultSet.getId());
			SimpleDateFormat toformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat fromFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss aa");

			try
			{
				java.util.Date date = toformatter.parse(datetime);
				datetime = fromFormatter.format(date);

			}
			catch (ParseException e)
			{
				// todo shlok need
				// handle proper Exception
				 logger.severe(e);
			}
		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			
			 logger.severe(e);
		}
		Address userAddress = null;
		User orderUser = null;
		if(orderHeader.getUsersId() != null && orderHeader.getUsersId()!=null){
			
			queryString = "select u from User u where id =? ";
			TypedQuery<User> query3 = em.createQuery(queryString, User.class).setParameter(1,orderHeader.getUsersId());
			 orderUser= query3.getSingleResult();
			 for(Address address2:orderUser.getAddressesSet()){
				 if(address2.getIsDefaultAddress()==1){
					 userAddress = address2;
				 }
			 }
		}

		feedbackUrl = feedbackUrl + "refno=" + orderHeader.getReferenceNumber() + "&order_id=" + orderHeader.getId();

		if (resultSet != null)
		{
			 queryString = "select c from Course c where c.locationsId =" + resultSet.getId() + " and c.status!='D'";
			TypedQuery<Course> query = em.createQuery(queryString, Course.class);
			courses = query.getResultList();
		}
		if (orderHeader != null)
		{
			String address1 = localAddress.getAddress1();
			String address2 = localAddress.getAddress2();
			String city = localAddress.getCity();
			String state = localAddress.getState();
			String phone = localAddress.getPhone();
		
		 req = 
				"<?xml version=\"1.0\" encoding=\"utf-8\"?> <PrintRequestInfo>"
				+ " 	<ePOSPrint> 		<Parameter> 			<devid>"+printerId+"</devid> 			<timeout>60000</timeout> 		</Parameter> 	"
				+ "	<PrintData>" + 
						"<epos-print xmlns='http://www.epson-pos.com/schemas/2011/03/epos-print'>" +
							"  <text lang='en' align=\"center\">"+resultSet.getName()+"&#10;</text> ";
		if (address1 != null && address1.length()>0)
						req +=	"  <text lang='en' align=\"center\">"+address1+"&#10;</text> " ;
		if (address2 != null && address2.length()>0)
						req +=	"  <text lang='en' align=\"center\">"+address2+"&#10;</text> " ;
		if (city != null && city.length()>0)
						req +=	"  <text lang='en' align=\"center\">"+city+" </text> " ;
		if (state != null && state.length()>0)
						req +=	"  <text lang='en' align=\"center\">"+state+"&#10;</text> " ;
		if (phone != null && phone.length()>0)				
						req +=	"  <text lang='en' align=\"center\">"+phone+"&#10;</text> " ;
		for(PrinterReceipt printerReceipt : printerReceiptList){
			if(printerReceipt.getPosition().equals("TOP"))
						req +=	printAlignFunction(printerReceipt);
		}
						req +=	"  <text lang='en' align=\"center\">"+datetime+"&#10;</text> " ;
						req +=	"  <text lang='en' align=\"center\">Order#: "+orderHeader.getOrderNumber()+"&#9;"+orderSourceGroup.getDisplayName()+"&#10;</text> " ;
						req +=	"  <text lang='en' align=\"center\">Server: "+serverName+"&#9;Cashier:"+cashierName+"&#10;</text> "
							+ "<page> ";
			int height= 30;
			int width =620;
			int x=35;
			int x2= 40;
			int y=30;
			int y1=30;
			int priceXValue=380;
			List<SalesTax> salesTaxs = new ArrayList<SalesTax>();
			if (courses != null && courses.size() > 0)
			{
				int display = 0;
				for (Course course : courses)
				{
					display = 0;
					for (OrderDetailItem detailItem : orderHeader.getOrderDetailItems())
					{
						BigDecimal qty = detailItem.getItemsQty();
						if (qty.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
							qty = qty.setScale(0, BigDecimal.ROUND_HALF_DOWN);

						} else {
							qty = qty.setScale(2, BigDecimal.ROUND_HALF_DOWN);

						}
						if (detailItem.getSentCourseId() == course.getId())
						{
							List<OrderDetailAttribute> orderDetailAttributes = null;
							if (detailItem != null)
							{
								queryString = "select c from OrderDetailAttribute c where c.orderDetailItemId =" + detailItem.getId();
								TypedQuery<OrderDetailAttribute> query = em.createQuery(queryString, OrderDetailAttribute.class);
								orderDetailAttributes = query.getResultList();
							}
							if (display == 0)
							{
								y +=y1;
									req +=
											"<area x=\""+x+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
											"  <text lang='en' ul=\"1\" align=\"right\">"+course.getCourseName()+"&#10;</text>";
								
							}
							display = 1;
							BigDecimal price = detailItem.getPriceSelling().multiply(qty).setScale(2, BigDecimal.ROUND_HALF_DOWN);
							y +=y1;
			
							req +=			
									"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
									"  <text lang='en'  ul=\"0\"  align=\"left\">" + qty+ "&#10;</text>"+
									" " ;
							String itemName=detailItem.getItemsShortName();
							String itemName1 ="";
							String itemName2 = "";
							String itemName3 = "";
							String itemName4 = "";
							if(itemName!= null ){
								if(itemName.length()<26){
									itemName1 = itemName.substring(0);
								}else{
									itemName1 = itemName.substring(0,26);
								}
								if(itemName.length()<52){
									if(itemName.length()>26){
										itemName2 = itemName.substring(26);
									}
								}else{
									itemName2 = itemName.substring(26,52);
								}

								if(itemName.length()<78){
									if(itemName.length()>52){
										itemName3 = itemName.substring(52);
									}
									
								}else{
									itemName3 = itemName.substring(52,78);
								}
								if(itemName.length()<100){
									if(itemName.length()>78){
										itemName4 = itemName.substring(78);
									}
									
								}else{
									itemName4 = itemName.substring(78,100);
								}

							req +=			
										"<area x=\""+(x2+50)+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
										"  <text lang='en'  ul=\"0\"  align=\"left\">"+ itemName1 + "&#10;</text>"+
										" " ;
							
							req +=			
									"<area x=\""+(x2+priceXValue)+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
									"  <text lang='en'  ul=\"0\"  align=\"right\">"+createPriceString(price)+price.setScale(2, BigDecimal.ROUND_HALF_DOWN)+"&#10;</text>"+
									" " ;
							if(itemName2 != null && itemName2.length()>0){
								y +=y1;
								req +=			
											"<area x=\""+(x2+50)+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
											"  <text lang='en'  ul=\"0\"  align=\"left\">"+ itemName2 + "&#10;</text>"+
											" " ;
								
								}
							
							if(itemName3 != null && itemName3.length()>0){
								y +=y1;
								req +=			
											"<area x=\""+(x2+50)+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
											"  <text lang='en'  ul=\"0\"  align=\"left\">"+ itemName3 + "&#10;</text>"+
											" " ;
								
								}
							if(itemName4 != null && itemName4.length()>0){
								y +=y1;
								req +=			
											"<area x=\""+(x2+50)+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
											"  <text lang='en'  ul=\"0\"  align=\"left\">"+ itemName4 + "&#10;</text>"+
											" " ;
								
								}
							}
							for (OrderDetailAttribute detailAttribute : orderDetailAttributes)
							{
								ItemsAttribute attribute = (ItemsAttribute) new CommonMethods().getObjectById("ItemsAttribute", em,ItemsAttribute.class, detailAttribute.getItemsAttributeId());
								
								attribute.getItemsAttributeTypeId();
								if(true){
									y +=y1;
									req +=			
										"<area x=\""+(x2+60)+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
										"  <text lang='en'  ul=\"0\"  align=\"left\">-" + detailAttribute.getItemsAttributeName() + "&#10;</text>"+
										" " ;
									
									req +=			
											"<area x=\""+(x2+priceXValue)+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
											"  <text lang='en'  ul=\"0\"  align=\"right\">" + createPriceString(detailAttribute.getPriceSelling())+detailAttribute.getPriceSelling() + "&#10;</text>"+
											" " ;
								}	
								
							}
						
						if (salesTaxs.size() == 0)
						{

							if (detailItem.getTaxName1() != null)
							{
								
								SalesTax tax = new SalesTax();
								tax.setTaxName(detailItem.getTaxName1());
								tax.setRate(detailItem.getPriceTax1());
//								tax.setSubTotal(price);
								salesTaxs.add(tax);
							}
							if (detailItem.getTaxName2() != null)
							{
								if (salesTaxs.size() == 0)
								{
									SalesTax tax = new SalesTax();
									tax.setTaxName(detailItem.getTaxName2());
									tax.setRate(detailItem.getPriceTax2());
//									tax.setSubTotal(price);
									salesTaxs.add(tax);
								}
								else
								{
									int isAdded = 0;
									for (int i = 0; i < salesTaxs.size(); i++)
									{
										SalesTax salesTax = salesTaxs.get(i);

										if (salesTax.getTaxName().equals(detailItem.getTaxName2()))
										{
											BigDecimal rate = detailItem.getPriceTax2().add(salesTax.getRate());
//											BigDecimal subtotalForTax = price.add(salesTax.getSubTotal());
//											salesTax.setSubTotal(subtotalForTax);
											salesTax.setRate(rate);
											salesTaxs.add(i, salesTax);
											isAdded = 1;
										}

									}
									if (isAdded == 0)
									{
										SalesTax tax = new SalesTax();
										tax.setTaxName(detailItem.getTaxName2());
										tax.setRate(detailItem.getPriceTax2());
//										tax.setSubTotal(price);
										salesTaxs.add(tax);
									}

								}
							}
							if (detailItem.getTaxName3() != null)
							{
								if (salesTaxs.size() == 0)
								{
									SalesTax tax = new SalesTax();
									tax.setTaxName(detailItem.getTaxName3());
									tax.setRate(detailItem.getPriceTax3());
//									tax.setSubTotal(price);
									salesTaxs.add(tax);
								}
								else
								{
									int isAdded = 0;
									for (int i = 0; i < salesTaxs.size(); i++)
									{
										SalesTax salesTax = salesTaxs.get(i);

										if (salesTax.getTaxName().equals(detailItem.getTaxName3()))
										{
											BigDecimal rate = detailItem.getPriceTax3().add(salesTax.getRate());
//											BigDecimal subtotalForTax = price.add(salesTax.getSubTotal());
//											salesTax.setSubTotal(subtotalForTax);
											salesTax.setRate(rate);
											salesTaxs.add(i, salesTax);
											isAdded = 1;
										}

									}
									if (isAdded == 0)
									{
										SalesTax tax = new SalesTax();
										tax.setTaxName(detailItem.getTaxName3());
										tax.setRate(detailItem.getPriceTax3());
//										tax.setSubTotal(price);
										salesTaxs.add(tax);
									}

								}
							}
							if (detailItem.getTaxName4() != null)
							{
								if (salesTaxs.size() == 0)
								{
									SalesTax tax = new SalesTax();
									tax.setTaxName(detailItem.getTaxName4());
									tax.setRate(detailItem.getPriceTax4());
//									tax.setSubTotal(price);
									salesTaxs.add(tax);
								}
								else
								{
									int isAdded = 0;
									for (int i = 0; i < salesTaxs.size(); i++)
									{
										SalesTax salesTax = salesTaxs.get(i);

										if (salesTax.getTaxName().equals(detailItem.getTaxName4()))
										{
											BigDecimal rate = detailItem.getPriceTax4().add(salesTax.getRate());
//											BigDecimal subtotalForTax = price.add(salesTax.getSubTotal());
//											salesTax.setSubTotal(subtotalForTax);
											salesTax.setRate(rate);
											salesTaxs.add(i, salesTax);
											isAdded = 1;
										}

									}
									if (isAdded == 0)
									{
										SalesTax tax = new SalesTax();
										tax.setTaxName(detailItem.getTaxName4());
										tax.setRate(detailItem.getPriceTax4());
//										tax.setSubTotal(price);
										salesTaxs.add(tax);
									}

								}
							}

						}
						else
						{
							// TODO - Ankur - this code is wrong, ask Apoorva
							for (int i = 0; i < salesTaxs.size(); ++i)
							{
								SalesTax salesTax = salesTaxs.get(i);
								if (salesTax.getTaxName().equals(detailItem.getTaxName1()))
								{
									BigDecimal rate = detailItem.getPriceTax1().add(salesTax.getRate());
//									BigDecimal subtotalForTax = price.add(salesTax.getSubTotal());
//									salesTax.setSubTotal(subtotalForTax);
									salesTax.setRate(rate);
									salesTaxs.remove(i);
									salesTaxs.add(i, salesTax);
								}
								else
								{
									if (detailItem.getTaxName1() != null)
									{
										SalesTax tax = new SalesTax();
										tax.setTaxName(detailItem.getTaxName1());
										tax.setRate(detailItem.getPriceTax1());
//										tax.setSubTotal(price);
										salesTaxs.add(tax);
									}
								}
								
								if (salesTax.getTaxName().equals(detailItem.getTaxName2()))
								{
									BigDecimal rate = detailItem.getPriceTax2().add(salesTax.getRate());
//									BigDecimal subtotalForTax = price.add(salesTax.getSubTotal());
//									salesTax.setSubTotal(subtotalForTax);
									salesTax.setRate(rate);
									salesTaxs.remove(i);
									salesTaxs.add(i, salesTax);
								}
								else
								{
									if (detailItem.getTaxName2() != null)
									{
										SalesTax tax = new SalesTax();
										tax.setTaxName(detailItem.getTaxName2());
										tax.setRate(detailItem.getPriceTax2());
//										tax.setSubTotal(price);
										salesTaxs.add(tax);
									}
								}
								if (salesTax.getTaxName().equals(detailItem.getTaxName3()))
								{
									BigDecimal rate = detailItem.getPriceTax3().add(salesTax.getRate());
//									BigDecimal subtotalForTax = price.add(salesTax.getSubTotal());
//									salesTax.setSubTotal(subtotalForTax);
									salesTax.setRate(rate);
									salesTaxs.remove(i);
									salesTaxs.add(i, salesTax);
								}
								else
								{
									if (detailItem.getTaxName3() != null)
									{
										SalesTax tax = new SalesTax();
										tax.setTaxName(detailItem.getTaxName3());
										tax.setRate(detailItem.getPriceTax3());
//										tax.setSubTotal(price);
										salesTaxs.add(tax);
									}
								}
								if (salesTax.getTaxName().equals(detailItem.getTaxName4()))
								{
									BigDecimal rate = detailItem.getPriceTax4().add(salesTax.getRate());
//									BigDecimal subtotalForTax = price.add(salesTax.getSubTotal());
//									salesTax.setSubTotal(subtotalForTax);
									salesTax.setRate(rate);
									salesTaxs.remove(i);
									salesTaxs.add(i, salesTax);
								}
								else
								{
									if (detailItem.getTaxName4() != null)
									{
										SalesTax tax = new SalesTax();
										tax.setTaxName(detailItem.getTaxName4());
										tax.setRate(detailItem.getPriceTax1());
//										tax.setSubTotal(price);
										salesTaxs.add(tax);
									}
								}
								break;

							}
						}
					}
					}
				}
			}
			
			for (int i = 0; i < salesTaxs.size(); i++)
			{
				SalesTax salesTax = salesTaxs.get(i);
				for (int j = i + 1; j < salesTaxs.size(); j++)
				{
					SalesTax localTax = salesTaxs.get(j);
					if (salesTax.getTaxName().equals(localTax.getTaxName()))
					{
						BigDecimal rate = localTax.getRate().add(salesTax.getRate());
//						BigDecimal subtotalForTax = localTax.getSubTotal().add(salesTax.getSubTotal());
//						salesTax.setSubTotal(subtotalForTax);
						salesTax.setRate(rate);
						salesTaxs.remove(j);
						salesTaxs.remove(i);
						salesTaxs.add(i, salesTax);
					}
				}
			}
			
			y +=y1;
			x2=40;
			req +=			
				"<area x=\""+x+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
				"  <text lang='en'  ul=\"0\"  align=\"left\">--------------------------------------------------------------------------------&#10;</text>"+
				"  " ;
			
			BigDecimal subTotal = orderHeader.getSubTotal().setScale(2, BigDecimal.ROUND_HALF_DOWN);
			y +=y1;
			x2=40;
			req +=			
				"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
				"  <text lang='en'  ul=\"0\"  align=\"left\">Sub Total&#10;</text>"+
				" " ;
			x2+= priceXValue;
			req +=			
					"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'   align=\"right\">"+createPriceString(subTotal)+subTotal.setScale(2, BigDecimal.ROUND_HALF_DOWN)+"&#10;</text>"+
					"  " ;
			
			for (SalesTax salesTax : salesTaxs)
			{
				y +=y1;
				x2=40;
				req +=			
					"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'  ul=\"0\"  align=\"left\">"+salesTax.getTaxName()+"&#10;</text>"+
					" " ;
				x2+= priceXValue;
				req +=			
						"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
						"  <text lang='en'   align=\"right\">"+createPriceString(salesTax.getRate())+salesTax.getRate().setScale(2, BigDecimal.ROUND_HALF_DOWN)+"&#10;</text>"+
						"  " ;
			}
			if (orderHeader.getDiscountsId() != null){
				y +=y1;
				x2=40;
				req +=			
					"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'  ul=\"0\"  align=\"left\">"+discountName+"&#10;</text>"+
					" " ;
				x2+= priceXValue;
				req +=			
						"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
						"  <text lang='en'   align=\"right\">"+createPriceString(orderHeader.getDiscountsValue())+orderHeader.getDiscountsValue().setScale(2, BigDecimal.ROUND_HALF_DOWN)+"&#10;</text>"+
						"  " ;
			}
			
			if (orderHeader.getIsGratuityApplied() != 0){
				y +=y1;
				x2=40;
				req +=			
					"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'  ul=\"0\"  align=\"left\">GRATUITY&#10;</text>"+
					" " ;
				x2+= priceXValue;
				req +=			
						"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
						"  <text lang='en'   align=\"right\">"+createPriceString(orderHeader.getGratuity())+orderHeader.getGratuity().setScale(2, BigDecimal.ROUND_HALF_DOWN)+"&#10;</text>"+
						"  " ;
			}
				
			if (orderHeader.getTaxDisplayName1() != null)
			{

				y +=y1;
				x2=40;
				req +=			
					"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'  ul=\"0\"  align=\"left\">" + orderHeader.getTaxDisplayName1() + " ( " + orderHeader.getTaxRate1() + "% of " + subTotal + ")&#10;</text>"+
					" " ;
				x2+= priceXValue;
				req +=			
						"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
						"  <text lang='en'   align=\"right\">"+createPriceString(orderHeader.getPriceTax1())+orderHeader.getPriceTax1().setScale(2, BigDecimal.ROUND_HALF_DOWN)+"&#10;</text>"+
						"  " ;
				
			}
			if (orderHeader.getTaxDisplayName2() != null)
			{

				y +=y1;
				x2=40;
				req +=			
					"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'  ul=\"0\"  align=\"left\">" + orderHeader.getTaxDisplayName2() + " ( " + orderHeader.getTaxRate2() + "% of " + subTotal + ")&#10;</text>"+
					" " ;
				x2+= priceXValue;
				req +=			
						"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
						"  <text lang='en'   align=\"right\">"+createPriceString(orderHeader.getPriceTax2())+orderHeader.getPriceTax2().setScale(2, BigDecimal.ROUND_HALF_DOWN)+"&#10;</text>"+
						"  " ;
			}
			if (orderHeader.getTaxDisplayName3() != null)
			{
				y +=y1;
				x2=40;
				req +=			
					"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'  ul=\"0\"  align=\"left\">" + orderHeader.getTaxDisplayName3() + " ( " + orderHeader.getTaxRate3() + "% of " + subTotal + ")&#10;</text>"+
					" " ;
				x2+= priceXValue;
				req +=			
						"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
						"  <text lang='en'   align=\"right\">"+createPriceString(orderHeader.getPriceTax3())+orderHeader.getPriceTax3().setScale(2, BigDecimal.ROUND_HALF_DOWN)+"&#10;</text>"+
						"  " ;
			}
			if (orderHeader.getTaxDisplayName4() != null)
			{
				y +=y1;
				x2=40;
				req +=			
					"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'  ul=\"0\"  align=\"left\">" + orderHeader.getTaxDisplayName4() + " ( " + orderHeader.getTaxRate4() + "% of " + subTotal + ")&#10;</text>"+
					" " ;
				x2+= priceXValue;
				req +=			
						"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
						"  <text lang='en'   align=\"right\">"+createPriceString(orderHeader.getPriceTax4())+orderHeader.getPriceTax4().setScale(2, BigDecimal.ROUND_HALF_DOWN)+"&#10;</text>"+
						"  " ;
			}
			BigDecimal totalHeader = new BigDecimal(0);
			if(location.getIsRoundOffRequired() ==0){
				totalHeader =  orderHeader.getTotal();
			}else{
				totalHeader =  orderHeader.getTotal().setScale(2, BigDecimal.ROUND_HALF_DOWN);
			}
			y +=y1;
			x2=40;
			req +=			
				"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
				"  <text lang='en'  ul=\"0\"  align=\"left\">Total&#10;</text>"+
				" " ;
			x2+= priceXValue;
			req +=			
					"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'   align=\"right\">"+createPriceString(totalHeader)+totalHeader.setScale(2, BigDecimal.ROUND_HALF_DOWN)+"&#10;</text>"+
					"  " ;
	
				y +=y1;
				x2=40;
				req +=			
					"<area x=\""+x+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'  ul=\"0\"  align=\"left\">--------------------------------------------------------------------------&#10;</text>"+
					"  " ;
				
				y +=y1;
				x2=40;
				req +=			
					"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'  ul=\"0\"  align=\"left\">Payment&#10;</text>"+
					" " ;
				x2+= 402;
				req +=			
						"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
						"  <text lang='en'   align=\"right\">Amount Paid&#10;</text>"+
						"  " ;
				// adding order payment details
				BigDecimal total = new BigDecimal("0.00");
				BigDecimal totalTip = new BigDecimal("0.00");
				BigDecimal totalDue = new BigDecimal("0.00");
				BigDecimal cashTipAmount = new BigDecimal("0.00");
				BigDecimal creditTipAmount = new BigDecimal("0.00");
				
				for(OrderPaymentDetail orderPaymentDetail:orderHeader.getOrderPaymentDetails()){
//					String paymentMethodType = "";
					PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();
					PaymentMethodType methodType= (PaymentMethodType) new CommonMethods().getObjectById("PaymentMethodType", em,PaymentMethodType.class, paymentMethod.getPaymentMethodTypeId());
					PaymentTransactionType paymentTransactionType = orderPaymentDetail.getPaymentTransactionType();
					if(paymentTransactionType.getDisplayName().equals("CaptureAll") || paymentTransactionType.getDisplayName().equals("Force")){
						break;
					}
					String paymentMethodTypeName = "";
					if(methodType.getDisplayName().equals("Credit Card") || methodType.getDisplayName().equals("Manual CC Entry") || methodType.getDisplayName().equals("Manual Credit Card")){
						paymentMethodTypeName = orderPaymentDetail.getCardType()+" "+orderPaymentDetail.getCardNumber();
						if(orderPaymentDetail.getIsRefunded()==1){
							paymentMethodTypeName +="-V";
							total = total.subtract(orderPaymentDetail.getAmountPaid());
						}else{
							total = total.add(orderPaymentDetail.getAmountPaid());
							cashTipAmount = cashTipAmount.add(orderPaymentDetail.getCashTipAmt());
							creditTipAmount = creditTipAmount.add(orderPaymentDetail.getCreditcardTipAmt());
						}
					}else{
						paymentMethodTypeName = methodType.getDisplayName();
						if(orderPaymentDetail.getIsRefunded()==1){
							paymentMethodTypeName +="-R";
							total = total.subtract(orderPaymentDetail.getAmountPaid());
						}else{
							total = total.add(orderPaymentDetail.getAmountPaid());
						}
					}
					
					String dbTime = orderPaymentDetail.getDate() +" "+orderPaymentDetail.getTime();
					
					String locationTime= "";
					try
					{
						SimpleDateFormat toformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						SimpleDateFormat fromFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");

						try
						{
							java.util.Date date = toformatter.parse(dbTime);
							locationTime = fromFormatter.format(date);
						}
						catch (ParseException e)
						{
							// todo shlok need
							// handle proper Exception
							 logger.severe(e);
						}
					}
					
					catch (Exception e)
					{
						// todo shlok need
						// handle proper Exception
						 logger.severe(e);
					}
					if(paymentMethodTypeName == null || paymentMethodTypeName.trim().length()==0){
						paymentMethodTypeName = "CASH";
					}
					if(orderPaymentDetail.getCashTipAmt().compareTo(new BigDecimal("0.00")) != 0 || orderPaymentDetail.getCreditcardTipAmt().compareTo(new BigDecimal("0.00")) != 0 ){
						y +=y1;
						x2=40;
						req +=			
							"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
							"  <text lang='en'  ul=\"0\"  align=\"left\">"+ locationTime +" " +paymentMethodTypeName+"&#10;</text>"+
							" " ;
						x2+= priceXValue;
						req +=			
								"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
								"  <text lang='en'   align=\"right\">"+createPriceString(orderPaymentDetail.getAmountPaid())+orderPaymentDetail.getAmountPaid().setScale(2, BigDecimal.ROUND_HALF_DOWN)+ "&#10;</text>"+
								"  " ;
						
						y +=y1;
						x2=40;
						req +=			
							"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
							"  <text lang='en'  ul=\"0\"  align=\"left\">Tips&#10;</text>"+
							" " ;
						x2+= priceXValue;
						req +=			
								"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
								"  <text lang='en'   align=\"right\">"+createPriceString(orderPaymentDetail.getCashTipAmt().add(orderPaymentDetail.getCreditcardTipAmt()))+(orderPaymentDetail.getCashTipAmt().add(orderPaymentDetail.getCreditcardTipAmt())).setScale(2, BigDecimal.ROUND_HALF_DOWN)+  "&#10;</text>"+
								"  " ;
						
						
						total = total.add(orderPaymentDetail.getAmountPaid());
						total = total.subtract(orderPaymentDetail.getAmountPaid());
					}else{
						y +=y1;
						x2=40;
						req +=			
							"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
							"  <text lang='en'  ul=\"0\"  align=\"left\">"+ locationTime +" " +paymentMethodTypeName+"&#10;</text>"+
							" " ;
						x2+= priceXValue;
						req +=			
								"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
								"  <text lang='en'   align=\"right\">" +createPriceString(orderPaymentDetail.getAmountPaid())+orderPaymentDetail.getAmountPaid().setScale(2, BigDecimal.ROUND_HALF_DOWN)+  "&#10;</text>"+
								"  " ;
						
					}
					
				}
				totalDue = totalHeader.subtract(total);
				totalTip = cashTipAmount.add(creditTipAmount);

				
				
				
				y +=y1;
				x2=40;
				req +=			
					"<area x=\""+x+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'  ul=\"0\"  align=\"left\">--------------------------------------------------------------------------&#10;</text>"+
					"  " ;
				
				// total due 
				y +=y1;
				x2=40;
				req +=			
					"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'  ul=\"0\"  align=\"left\">Total&#10;</text>"+
					" " ;
				x2+= priceXValue;
				req +=			
						"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
						"  <text lang='en'   align=\"right\">"+createPriceString(total)+total.setScale(2, BigDecimal.ROUND_HALF_DOWN)+"&#10;</text>"+
						"  " ;
				
				y +=y1;
				x2=40;
				req +=			
					"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'  ul=\"0\"  align=\"left\">Total Tips&#10;</text>"+
					" " ;
				x2+= priceXValue;
				req +=			
						"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
						"  <text lang='en'   align=\"right\">"+createPriceString(totalTip)+totalTip.setScale(2, BigDecimal.ROUND_HALF_DOWN)+"&#10;</text>"+
						"  " ;
				
				y +=y1;
				x2=40;
				req +=			
					"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'  ul=\"0\"  align=\"left\">Total Due&#10;</text>"+
					" " ;
				x2+= priceXValue;
				req +=			
						"<area x=\""+x2+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
						"  <text lang='en'   align=\"right\">"+createPriceString(totalDue)+totalDue.setScale(2, BigDecimal.ROUND_HALF_DOWN)+"&#10;</text>"+
						"  " ;
				
				y +=y1;
				x2=40;
				req +=			
					"<area x=\""+x+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\" />" +
					"  <text lang='en'  ul=\"0\"  align=\"left\">--------------------------------------------------------------------------&#10;</text>"+
					"  " ;
				
				
				req += "  </page > " ;
				
				req += "  <text lang='en' align=\"center\">Scheduled On :-"+datetime+"&#10;</text> " ;
				req += "  <text lang='en' align=\"center\">&#10;</text> " ;
				// printing user info in bottom of receipt
				if (orderUser.getPhone() != null && orderUser.getPhone().length() >0)				
					req +=	"  <text lang='en' align=\"center\">"+orderUser.getPhone()+"&#10;</text> " ;
				if (orderUser.getEmail() != null && orderUser.getEmail().length() >0)				
					req +=	"  <text lang='en' align=\"center\">"+orderUser.getEmail()+"&#10;</text> " ;
				if ( orderUser.getFirstName()!= null && orderUser.getFirstName().length()>0 )				
					req +=	"  <text lang='en' align=\"center\">"+orderUser.getFirstName()+"</text> " ;
				if ( orderUser.getLastName()!= null && orderUser.getLastName().length()>0)				
					req +=	"  <text lang='en' align=\"center\">&#32;"+orderUser.getLastName()+"&#10;</text> " ;
				if(userAddress!= null){
					if (userAddress.getAddress1() != null && userAddress.getAddress1().length() >0)
						req +=	"  <text lang='en' align=\"center\">"+userAddress.getAddress2()+"&#10;</text> " ;
					if (userAddress.getCity() != null && userAddress.getCity().length()>0)
									req +=	"  <text lang='en' align=\"center\">"+userAddress.getCity()+" </text> " ;
					if (userAddress.getState() != null && userAddress.getState().length()>0)
									req +=	"  <text lang='en' align=\"center\">"+userAddress.getState()+"&#10;</text> " ;
					if (userAddress.getAddress2() != null && userAddress.getAddress2().length()>0)
						req +=	"  <text lang='en' align=\"center\">"+userAddress.getAddress2()+"&#10;</text> " ;
				}
				for(PrinterReceipt printerReceipt : printerReceiptList){
					if(printerReceipt.getPosition().equals("BOTTOM"))
								req +=	printAlignFunction(printerReceipt);
				}
				req += "  <text lang='en' align=\"left\">&#10;</text> " ;
				req += "  <text lang='en' width=\"1\" height=\"2\" align=\"center\">POWERED BY NIRVANA XP&#10;</text> " ;
				req += " <cut /> " +
						"</epos-print></PrintData> 	</ePOSPrint> </PrintRequestInfo> ";
		
		}
		return req;
	}
	

	/**
	 * Creates the kitchen receipt PDF string.
	 *
	 * @param em the em
	 * @param httpRequest the http request
	 * @param orderHeader the order header
	 * @param takeoutDelivery the takeout delivery
	 * @param printerName the printer name
	 * @return the string
	 */
	public String createKitchenReceiptPDFString(EntityManager em, HttpServletRequest httpRequest, OrderHeader orderHeader, int takeoutDelivery,String printerName)
	{
		String req = null;
		
		printerName=printerName.replace(" ", "_");
		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderHeader.getOrderSourceId());
		OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
		Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, orderHeader.getLocationsId());
		List<Location> result = null;
		Location resultSet = null;
		List<Course> courses = null;
		if (location != null && location.getLocationsId() != null)
		{
			String queryString = "select l from Location l where (l.locationsId ='0' or l.locationsId is null) and l.businessId=" + location.getBusinessId() + " order by l.id asc";
			TypedQuery<Location> query = em.createQuery(queryString, Location.class);
			result = query.getResultList();
			if (result != null && result.size() > 0)
			{
				resultSet = result.get(0);
			}
		}
		else
		{
			resultSet = location;
		}

		String datetime = "";

		try
		{
			TimezoneTime time = new TimezoneTime();
			datetime = time.getDateTimeFromGMTToLocation(em, orderHeader.getScheduleDateTime(), resultSet.getId());
			SimpleDateFormat toformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat fromFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss aa");
			try
			{
				java.util.Date date = toformatter.parse(datetime);
				datetime = fromFormatter.format(date);

			}
			catch (ParseException e)
			{
				// todo shlok need
				// handle proper Exception
				 logger.severe(e);
			}
		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			
			 logger.severe(e);
		}

		Address userAddress = null;
		User orderUser = null;
		// getting user and address
		if(orderHeader.getUsersId() != null && orderHeader.getUsersId()!=null){
			String queryString = "select u from User u where id =? ";
			TypedQuery<User> query3 = em.createQuery(queryString, User.class).setParameter(1,orderHeader.getUsersId());
			 orderUser= query3.getSingleResult();
			 for(Address address2:orderUser.getAddressesSet()){
				 if(address2.getIsDefaultAddress()==1){
					 userAddress = address2;
				 }
			 }
		}

		if (resultSet != null)
		{
			String queryString = "select c from Course c where c.locationsId =" + resultSet.getId() + " and c.status!='D'";
			TypedQuery<Course> query = em.createQuery(queryString, Course.class);
			courses = query.getResultList();
		}

		if (orderHeader != null)
		{
			
			req = 
					"<?xml version=\"1.0\" encoding=\"utf-8\"?> <PrintRequestInfo>"
					+ " 	<ePOSPrint> 		<Parameter> 			<devid>"+printerName+"</devid> 			<timeout>60000</timeout> 		</Parameter> 	"
					+ "	<PrintData>" + 
							"<epos-print xmlns='http://www.epson-pos.com/schemas/2011/03/epos-print'>" +
								"  <text lang='en' dh=\"true\" align=\"left\">&#32;&#32;&#32;"+datetime+"&#10;</text> ";
							req +=	"  <text lang='en' align=\"left\">&#32;&#32;&#32;Order# "+orderHeader.getOrderNumber()+"&#10;</text> " ;
							req +=	"  <text lang='en' align=\"left\">&#32;&#32;&#32;"+orderSourceGroup.getDisplayName()+" - "+orderSource.getDisplayName()+"&#10;</text> " ;
							req +=	"  <text lang='en' align=\"left\">&#32;&#32;&#32;Guests:"+orderHeader.getPointOfServiceCount()+"&#10;</text> " ;
	
			if (courses != null && courses.size() > 0)
			{
				int display = 0;
				for (Course course : courses)
				{
					display = 0;
					for (OrderDetailItem detailItem : orderHeader.getOrderDetailItems())
					{
						BigDecimal qty = detailItem.getItemsQty();
						
						
						
						if (qty.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
							qty = qty.setScale(0, BigDecimal.ROUND_HALF_DOWN);

						} else {
							qty = qty.setScale(2, BigDecimal.ROUND_HALF_DOWN);

						}
						int quantity=qty.intValue();
						for(int i=0;i<quantity;i++){
						 						
						if (detailItem.getSentCourseId() == course.getId())
						{
							List<OrderDetailAttribute> orderDetailAttributes = null;
							if (detailItem != null)
							{
								String queryString = "select c from OrderDetailAttribute c where c.orderDetailItemId =" + detailItem.getId();
								TypedQuery<OrderDetailAttribute> query = em.createQuery(queryString, OrderDetailAttribute.class);
								orderDetailAttributes = query.getResultList();
							}
							if (display == 0)
							{
								req +=			
									"  <text>------------------------------------------------</text>"+
									"  " ;
									req +=
											"  <text lang='en' ul=\"0\" align=\"center\">&#32;&#32;&#32;"+course.getCourseName()+"&#10;</text>";
									
									req +=			
										"  <text>------------------------------------------------</text>"+
										"  " ;
								
							}
							display = 1;
							String itemName=detailItem.getItemsShortName();
							String itemName1 ="";
							String itemName2 = "";
							if(itemName!= null ){
								if(itemName.length()<52){
									itemName1 = itemName.substring(0);
								}else{
									itemName1 = itemName.substring(0,52);
								}
								
								if(itemName.length()<120){
									if(itemName.length()>52){
										itemName2 = itemName.substring(52);
									}
									
								}else{
									itemName2 = itemName.substring(52,100);
								}

							req +=			
										"  <text lang='en'  ul=\"0\"  align=\"left\">&#32;&#32;&#32;"+qty+" "+ itemName1 + "&#10;</text>"+
										" " ;
							
							if(itemName2 != null && itemName2.length()>0){
								req +=			
											"  <text lang='en'  ul=\"0\"  align=\"left\">&#32;&#32;&#32;"+ itemName2 + "&#10;</text>"+
											" " ;
								
								}
							}
							
							for (OrderDetailAttribute detailAttribute : orderDetailAttributes)
							{
									req +=			
										"  <text lang='en'  ul=\"0\"  align=\"left\">- &#32;&#32;&#32;" + detailAttribute.getItemsAttributeName() + "&#10;</text>"+
										" " ;
							}
							// eligible for garbage collections
							orderDetailAttributes = null;
						}
						}
					}
				}
			}
			
			
				req +=			
					"  <text>------------------------------------------------</text>"+
					"  " ;
				
				req += "  <text lang='en' align=\"center\">&#32;&#32;&#32; Scheduled On :-"+datetime+" &#10;</text> " ;
				req += "  <text lang='en' align=\"center\">&#10;</text> " ;
				// printing user info in bottom of receipt
				if (orderUser.getPhone() != null && orderUser.getPhone().length() >0)				
					req +=	"  <text lang='en' align=\"center\">"+orderUser.getPhone()+"&#10;</text> " ;
				if (orderUser.getEmail() != null && orderUser.getEmail().length() >0)				
					req +=	"  <text lang='en' align=\"center\">"+orderUser.getEmail()+"&#10;</text> " ;
				if ( orderHeader.getFirstName()!= null && orderHeader.getFirstName().length()>0 )				
					req +=	"  <text lang='en' align=\"center\">"+orderHeader.getFirstName()+"</text> " ;
				if ( orderHeader.getLastName()!= null && orderHeader.getLastName().length()>0)				
					req +=	"  <text lang='en' align=\"center\">&#32;"+orderHeader.getLastName()+"&#10;</text> " ;
				if(userAddress!= null){
					if (userAddress.getAddress1() != null && userAddress.getAddress1().length() >0)
						req +=	"  <text lang='en' align=\"center\">"+userAddress.getAddress2()+"&#10;</text> " ;
					if (userAddress.getCity() != null && userAddress.getCity().length()>0)
									req +=	"  <text lang='en' align=\"center\">"+userAddress.getCity()+" </text> " ;
					if (userAddress.getState() != null && userAddress.getState().length()>0)
									req +=	"  <text lang='en' align=\"center\">"+userAddress.getState()+"&#10;</text> " ;
					if (userAddress.getAddress2() != null && userAddress.getAddress2().length()>0)
						req +=	"  <text lang='en' align=\"center\">"+userAddress.getAddress2()+"&#10;</text> " ;
				}
				req += "  <text lang='en' align=\"left\">&#10;</text> " ;
				
				req += "  <text lang='en' width=\"1\" height=\"2\" align=\"center\">Powered By Nirvana XP&#10;</text> " ;
				
				req += " <cut /> " +
						"</epos-print></PrintData> 	</ePOSPrint> </PrintRequestInfo> ";
		
		}
		orderSource = null;
		orderSourceGroup = null;
		orderHeader = null;
		
				
		return req;
	}
	
	/**
	 * Creates the kitchen receipt PDF string for U 220.
	 *
	 * @param em the em
	 * @param httpRequest the http request
	 * @param orderHeader the order header
	 * @param takeoutDelivery the takeout delivery
	 * @param printerName the printer name
	 * @return the string
	 */
	public String createKitchenReceiptPDFStringForU220(EntityManager em, HttpServletRequest httpRequest, OrderHeader orderHeader, int takeoutDelivery,String printerName)
	{
		String req = null;
		
		printerName=printerName.replace(" ", "_");
		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderHeader.getOrderSourceId());
		OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
		Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, orderHeader.getLocationsId());
		List<Location> result = null;
		Location resultSet = null;
		List<Course> courses = null;
		if (location != null && location.getLocationsId() != null)
		{
			String queryString = "select l from Location l where (l.locationsId ='0' or l.locationsId is null) and l.businessId=" + location.getBusinessId() + " order by l.id asc";
			TypedQuery<Location> query = em.createQuery(queryString, Location.class);
			result = query.getResultList();
			if (result != null && result.size() > 0)
			{
				resultSet = result.get(0);
			}
		}
		else
		{
			resultSet = location;
		}

		String datetime = "";

		try
		{
			TimezoneTime time = new TimezoneTime();
			datetime = time.getDateTimeFromGMTToLocation(em, orderHeader.getScheduleDateTime(), resultSet.getId());
			SimpleDateFormat toformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat fromFormatter = new SimpleDateFormat("MMM dd,yyyy HH:mmaa");
			try
			{
				java.util.Date date = toformatter.parse(datetime);
				datetime = fromFormatter.format(date);

			}
			catch (ParseException e)
			{
				// todo shlok need
				// handle proper Exception
				 logger.severe(e);
			}
		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			 logger.severe(e);
		}

		Address userAddress = null;
		User orderUser = null;
		if(orderHeader.getUsersId() != null && orderHeader.getUsersId()!=null){
			String queryString = "select u from User u where id =? ";
			TypedQuery<User> query3 = em.createQuery(queryString, User.class).setParameter(1,orderHeader.getUsersId());
			 orderUser= query3.getSingleResult();
			 for(Address address2:orderUser.getAddressesSet()){
				 if(address2.getIsDefaultAddress()==1){
					 userAddress = address2;
				 }
			 }
		}

		if (resultSet != null)
		{
			String queryString = "select c from Course c where c.locationsId =" + resultSet.getId() + " and c.status!='D'";
			TypedQuery<Course> query = em.createQuery(queryString, Course.class);
			courses = query.getResultList();
		}

		if (orderHeader != null)
		{
		 req = 
				"<?xml version=\"1.0\" encoding=\"utf-8\"?> <PrintRequestInfo>"
				+ " 	<ePOSPrint> 		<Parameter> 			<devid>"+printerName+"</devid> 			<timeout>60000</timeout> 		</Parameter> 	"
				+ "	<PrintData>" + 
						"<epos-print xmlns='http://www.epson-pos.com/schemas/2011/03/epos-print'>" +
							"  <text lang='en' dh=\"true\" align=\"left\">"+datetime+"&#10;</text> ";
						req +=	"  <text lang='en' align=\"left\">Order# "+orderHeader.getOrderNumber()+"&#10;</text> " ;
						req +=	"  <text lang='en'  color=\"color_2\" align=\"left\">"+orderSourceGroup.getDisplayName()+" - "+orderSource.getDisplayName()+"&#10;</text> " ;
						req +=	"  <text lang='en' color=\"color_1\" align=\"left\">Guests:"+orderHeader.getPointOfServiceCount()+"&#10;</text> " ;
						req	+= "<page> ";
			
			
			if (courses != null && courses.size() > 0)
			{
				int display = 0;
				for (Course course : courses)
				{
					display = 0;
					for (OrderDetailItem detailItem : orderHeader.getOrderDetailItems())
					{

						BigDecimal qty = detailItem.getItemsQty();
						if (qty.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
							qty = qty.setScale(0, BigDecimal.ROUND_HALF_DOWN);

						} else {
							qty = qty.setScale(2, BigDecimal.ROUND_HALF_DOWN);

						}
						if (detailItem.getSentCourseId() == course.getId())
						{
							List<OrderDetailAttribute> orderDetailAttributes = null;
							if (detailItem != null)
							{
								String queryString = "select c from OrderDetailAttribute c where c.orderDetailItemId =" + detailItem.getId();
								TypedQuery<OrderDetailAttribute> query = em.createQuery(queryString, OrderDetailAttribute.class);
								orderDetailAttributes = query.getResultList();
							}
							if (display == 0)
							{
								req +="  <text>---------------------------------</text>"+
									"  " ;
									req +="  <text lang='en' ul=\"0\" align=\"right\">"+course.getCourseName()+"&#10;</text>";
									
									req +="  <text>---------------------------------</text>"+
										"  " ;
								
							}
							display = 1;
						
							String itemName=detailItem.getItemsShortName();
							String itemName1 ="";
							String itemName2 = "";
							if(itemName!= null ){
								if(itemName.length()<52){
									itemName1 = itemName.substring(0);
								}else{
									itemName1 = itemName.substring(0,52);
								}
								
								if(itemName.length()<120){
									if(itemName.length()>52){
										itemName2 = itemName.substring(52);
									}
									
								}else{
									itemName2 = itemName.substring(52,100);
								}

							req +="  <text lang='en' color=\"color_1\"  ul=\"0\"  align=\"left\">"+qty+" " +itemName1 + "&#10;</text>"+
										" " ;
							
							if(itemName2 != null && itemName2.length()>0){
								req +="  <text lang='en'  ul=\"0\"  align=\"left\">"+ itemName2 + "&#10;</text>"+
											" " ;
								
								}
							}
							
							for (OrderDetailAttribute detailAttribute : orderDetailAttributes)
							{
									req +="  <text lang='en' color=\"color_2\"  ul=\"0\"  align=\"left\">- " + detailAttribute.getItemsAttributeName() + "&#10;</text>"+
										" " ;
							}
						}
						
					}
				}
			}
			
			
				req +="  <text lang='en' color=\"color_1\">---------------------------------</text>"+
					"  " ;
				req += "  </page > " ;
				req += "  <text lang='en' align=\"center\">Scheduled On:-"+datetime+" &#10;</text> " ;
				req += "  <text lang='en' align=\"center\">&#10;</text> " ;
				// printing user info in bottom of receipt
				if (orderUser.getPhone() != null && orderUser.getPhone().length() >0)				
					req +=	"  <text lang='en' align=\"center\">"+orderUser.getPhone()+"&#10;</text> " ;
				if (orderUser.getEmail() != null && orderUser.getEmail().length() >0)				
					req +=	"  <text lang='en' align=\"center\">"+orderUser.getEmail()+"&#10;</text> " ;
				if ( orderHeader.getFirstName()!= null && orderHeader.getFirstName().length()>0 )				
					req +=	"  <text lang='en' align=\"center\">"+orderHeader.getFirstName()+"</text> " ;
				if ( orderHeader.getLastName()!= null && orderHeader.getLastName().length()>0)				
					req +=	"  <text lang='en' align=\"center\">&#32;"+orderHeader.getLastName()+"&#10;</text> " ;
				if(userAddress!= null){
					if (userAddress.getAddress1() != null && userAddress.getAddress1().length() >0)
						req +=	"  <text lang='en' align=\"center\">"+userAddress.getAddress2()+"&#10;</text> " ;
					if (userAddress.getCity() != null && userAddress.getCity().length()>0)
									req +=	"  <text lang='en' align=\"center\">"+userAddress.getCity()+" </text> " ;
					if (userAddress.getState() != null && userAddress.getState().length()>0)
									req +=	"  <text lang='en' align=\"center\">"+userAddress.getState()+"&#10;</text> " ;
					if (userAddress.getAddress2() != null && userAddress.getAddress2().length()>0)
						req +=	"  <text lang='en' align=\"center\">"+userAddress.getAddress2()+"&#10;</text> " ;
				}
				req += "  <text lang='en' align=\"left\">&#10;</text> " ;
				
				req += "  <text lang='en' width=\"1\" height=\"2\" align=\"center\">Powered By Nirvana XP&#10;</text> " ;
				
				req += " <cut /> " +
						"</epos-print></PrintData> 	</ePOSPrint> </PrintRequestInfo> ";
		
		}
		// eligible for garbage collection
		orderSource = null;
		orderSourceGroup = null;
		orderHeader = null;
		
		return req;
	}
	
	/**
	 * Creates the price string.
	 *
	 * @param bigDecimal the big decimal
	 * @return the string
	 */
	private String createPriceString(BigDecimal bigDecimal){
		String value = 
				bigDecimal.toPlainString();
		String price="";
		for(int i=(value.length());i<13;i++){
			price += "&#32;";
		}
		
		return price;
	}
	
	private BigDecimal createTotalAmount(BigDecimal finalAmount, BigDecimal percentageOfTax){
		
		BigDecimal totalAmount = finalAmount.multiply(new BigDecimal(100)).divide(percentageOfTax); 
		
		return totalAmount;
	}
	
	/**
	 * Prints the align function.
	 *
	 * @param printerReceipt the printer receipt
	 * @return the string
	 */
	private String printAlignFunction(PrinterReceipt printerReceipt ){
		String receipt="";
		if(printerReceipt.getAlignment().equals("LEFT")){
			receipt +=	"  <text lang='en' align=\""+printerReceipt.getAlignment().toLowerCase()+"\">&#32;&#32;&#32;&#32;"+printerReceipt.getDisplayName()+":"+printerReceipt.getValue()+"&#10;</text> " ;
		}else if(printerReceipt.getAlignment().equals("CENTER")){
			receipt +=	"  <text lang='en' align=\""+printerReceipt.getAlignment().toLowerCase()+"\">"+printerReceipt.getDisplayName()+":"+printerReceipt.getValue()+"&#10;</text> " ;
		}else{
			receipt +=	"  <text lang='en' align=\""+printerReceipt.getAlignment().toLowerCase()+"\">"+printerReceipt.getDisplayName()+":"+printerReceipt.getValue()+"&#32;&#32;&#10;</text> " ;
		}
		
		return receipt;
	}
	
	/**
	 * Creates the kitchen receipt PDF string for TMT 88 VI.
	 *
	 * @param em the em
	 * @param httpRequest the http request
	 * @param orderHeader the order header
	 * @param takeoutDelivery the takeout delivery
	 * @param printerName the printer name
	 * @return the string
	 */
	public String createKitchenReceiptPDFStringForTMT88VI(EntityManager em, HttpServletRequest httpRequest, OrderHeader orderHeader, int takeoutDelivery,String printerName)
	{
		String req = null;
		
		printerName=printerName.replace(" ", "_");
		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderHeader.getOrderSourceId());
		OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
		Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, orderHeader.getLocationsId());
		List<Location> result = null;
		Location resultSet = null;
		List<Course> courses = null;
		if (location != null && location.getLocationsId() != null)
		{
			String queryString = "select l from Location l where (l.locationsId ='0' or l.locationsId is null) and l.businessId=" + location.getBusinessId() + " order by l.id asc";
			TypedQuery<Location> query = em.createQuery(queryString, Location.class);
			result = query.getResultList();
			if (result != null && result.size() > 0)
			{
				resultSet = result.get(0);
			}
		}
		else
		{
			resultSet = location;
		}

		String datetime = "";

		try
		{
			TimezoneTime time = new TimezoneTime();
			datetime = time.getDateTimeFromGMTToLocation(em, orderHeader.getScheduleDateTime(), resultSet.getId());
			SimpleDateFormat toformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat fromFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss aa");
			try
			{
				java.util.Date date = toformatter.parse(datetime);
				datetime = fromFormatter.format(date);

			}
			catch (ParseException e)
			{
				// todo shlok need
				// handle proper Exception
				 logger.severe(e);
			}
		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			 logger.severe(e);
		}

		Address userAddress = null;
		User orderUser = null;
		// getting user and address
		if(orderHeader.getUsersId() != null && orderHeader.getUsersId()!=null){
			try {
				String queryString = "select u from User u where id =? ";
				TypedQuery<User> query3 = em.createQuery(queryString, User.class).setParameter(1,orderHeader.getUsersId());
				 orderUser= query3.getSingleResult();
				 for(Address address2:orderUser.getAddressesSet()){
					 if(address2.getIsDefaultAddress()==1){
						 userAddress = address2;
					 }
				 }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.severe(e);
			}
		}

		if (resultSet != null)
		{
			String queryString = "select c from Course c where c.locationsId =" + resultSet.getId() + " and c.status!='D'";
			TypedQuery<Course> query = em.createQuery(queryString, Course.class);
			courses = query.getResultList();
		}

		if (orderHeader != null)
		{
		 req = 
				"<?xml version=\"1.0\" encoding=\"utf-8\"?> <PrintRequestInfo>"
				+ " 	<ePOSPrint> 		<Parameter> 			<devid>"+printerName+"</devid> 			<timeout>60000</timeout> 		</Parameter> 	"
				+ "	<PrintData>" + 
						"<epos-print xmlns='http://www.epson-pos.com/schemas/2011/03/epos-print'>" +
							"  <text lang='en' dh=\"true\" align=\"left\">&#32;&#32;&#32;"+datetime+"&#10;</text> ";
						req +=	"  <text lang='en' align=\"left\">&#32;&#32;&#32;Order# "+orderHeader.getOrderNumber()+"&#10;</text> " ;
						req +=	"  <text lang='en' align=\"left\">&#32;&#32;&#32;"+orderSourceGroup.getDisplayName()+" - "+orderSource.getDisplayName()+"&#10;</text> " ;
						req +=	"  <text lang='en' align=\"left\">&#32;&#32;&#32;Guests:"+orderHeader.getPointOfServiceCount()+"&#10;</text> " ;
						
			if (courses != null && courses.size() > 0)
			{
				int display = 0;
				for (Course course : courses)
				{
					display = 0;
					for (OrderDetailItem detailItem : orderHeader.getOrderDetailItems())
					{

						BigDecimal qty = detailItem.getItemsQty();
						if (qty.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
							qty = qty.setScale(0, BigDecimal.ROUND_HALF_DOWN);

						} else {
							qty = qty.setScale(2, BigDecimal.ROUND_HALF_DOWN);

						}
						if (detailItem.getSentCourseId() == course.getId())
						{
							List<OrderDetailAttribute> orderDetailAttributes = null;
							if (detailItem != null)
							{
								String queryString = "select c from OrderDetailAttribute c where c.orderDetailItemId =" + detailItem.getId();
								TypedQuery<OrderDetailAttribute> query = em.createQuery(queryString, OrderDetailAttribute.class);
								orderDetailAttributes = query.getResultList();
							}
							if (display == 0)
							{
								req +=			
									"  <text>------------------------------------------</text>"+
									"  " ;
									req +=
											"  <text lang='en' ul=\"0\" align=\"center\">&#32;&#32;&#32;"+course.getCourseName()+"&#10;</text>";
									
									req +=			
										"  <text>------------------------------------------</text>"+
										"  " ;
								
							}
							display = 1;
							String itemName=detailItem.getItemsShortName();
							String itemName1 ="";
							String itemName2 = "";
							if(itemName!= null ){
								if(itemName.length()<52){
									itemName1 = itemName.substring(0);
								}else{
									itemName1 = itemName.substring(0,52);
								}
								
								if(itemName.length()<120){
									if(itemName.length()>52){
										itemName2 = itemName.substring(52);
									}
									
								}else{
									itemName2 = itemName.substring(52,100);
								}

							req +=			
										"  <text lang='en' align=\"left\">&#32;&#32;&#32;"+qty+" "+ itemName1 + "&#10;</text>"+
										" " ;
							
							if(itemName2 != null && itemName2.length()>0){
								req +=			
											"  <text lang='en'  align=\"left\">&#32;&#32;&#32;"+ itemName2 + "&#10;</text>"+
											" " ;
								
								}
							}
							
							for (OrderDetailAttribute detailAttribute : orderDetailAttributes)
							{
									req +=			
										"  <text lang='en' align=\"left\">&#32;&#32;&#32;-" + detailAttribute.getItemsAttributeName() + "&#10;</text>"+
										" " ;
							}
							// eligible for garbage collections
							orderDetailAttributes = null;
						}
						
					}
				}
			}
			
			
				req +=			
					"  <text>------------------------------------------</text>"+
					"  " ;
				
				req += "  <text lang='en' align=\"center\">&#32;&#32; Scheduled On :-"+datetime+" &#10;</text> " ;
				req += "  <text lang='en' align=\"center\">&#10;</text> " ;
				// printing user info in bottom of receipt
				if (orderUser.getPhone() != null && orderUser.getPhone().length() >0)				
					req +=	"  <text lang='en' align=\"center\">"+orderUser.getPhone()+"&#10;</text> " ;
				if (orderUser.getEmail() != null && orderUser.getEmail().length() >0)				
					req +=	"  <text lang='en' align=\"center\">"+orderUser.getEmail()+"&#10;</text> " ;
				if ( orderHeader.getFirstName()!= null && orderHeader.getFirstName().length()>0 )				
					req +=	"  <text lang='en' align=\"center\">"+orderHeader.getFirstName()+"</text> " ;
				if ( orderHeader.getLastName()!= null && orderHeader.getLastName().length()>0)				
					req +=	"  <text lang='en' align=\"center\">&#32;"+orderHeader.getLastName()+"&#10;</text> " ;
				if(userAddress!= null){
					if (userAddress.getAddress1() != null && userAddress.getAddress1().length() >0)
						req +=	"  <text lang='en' align=\"center\">"+userAddress.getAddress2()+"&#10;</text> " ;
					if (userAddress.getCity() != null && userAddress.getCity().length()>0)
									req +=	"  <text lang='en' align=\"center\">"+userAddress.getCity()+" </text> " ;
					if (userAddress.getState() != null && userAddress.getState().length()>0)
									req +=	"  <text lang='en' align=\"center\">"+userAddress.getState()+"&#10;</text> " ;
					if (userAddress.getAddress2() != null && userAddress.getAddress2().length()>0)
						req +=	"  <text lang='en' align=\"center\">"+userAddress.getAddress2()+"&#10;</text> " ;
				}
				req += "  <text lang='en' align=\"left\">&#10;</text> " ;
				
				req += "  <text lang='en' width=\"1\" height=\"2\" align=\"center\">Powered By Nirvana XP&#10;</text> " ;
				
				req += " <cut /> " +
						"</epos-print></PrintData> 	</ePOSPrint> </PrintRequestInfo> ";
		
		}
		orderSource = null;
		orderSourceGroup = null;
		orderHeader = null;
		
				
		return req;
	}
	public String createLabelPDFString (EntityManager em, HttpServletRequest httpRequest, OrderHeader orderHeader, int takeoutDelivery,String printerName)
	{
		String req = null;
		printerName=printerName.replace(" ", "_");
		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderHeader.getOrderSourceId());
		OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
		Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, orderHeader.getLocationsId());
		List<Location> result = null;
		Location foundLocation = null;
		if (location != null && location.getLocationsId() != null)
		{
			String queryString = "select l from Location l where (l.locationsId ='0' or l.locationsId is null) and l.businessId=" + location.getBusinessId() + " order by l.id asc";
			TypedQuery<Location> query = em.createQuery(queryString, Location.class);
			result = query.getResultList();
			if (result != null && result.size() > 0)
			{
				foundLocation = result.get(0);
			}
		}
		else
		{
			foundLocation = location;
		}

		String datetime = "";

		try
		{
			TimezoneTime time = new TimezoneTime();
			datetime = time.getDateTimeFromGMTToLocation(em, orderHeader.getScheduleDateTime(), foundLocation.getId());
			SimpleDateFormat toformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat fromFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm aa");
			try
			{
				java.util.Date date = toformatter.parse(datetime);
				datetime = fromFormatter.format(date);

			}
			catch (ParseException e)
			{
				// todo shlok need
				// handle proper Exception
				 logger.severe(e);
			}
		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			
			 logger.severe(e);
		}

		String locationpath = null;
		try {
			locationpath = ConfigFileReader.getWebsiteLogoPath();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (orderHeader != null)
		{
			
			String url=locationpath + foundLocation.getImageUrl();
			
			String image=convertLogo(url);
			req = 
				"<?xml version=\"1.0\" encoding=\"utf-8\"?> <PrintRequestInfo>"
				+ " 	<ePOSPrint> 		<Parameter> 			<devid>"+printerName+"</devid> 			<timeout>60000</timeout> 		</Parameter> 	"
				+ "	<PrintData>" + 
						"<epos-print xmlns='http://www.epson-pos.com/schemas/2011/03/epos-print'>   " ;
				 int itemNo=0;
					for (OrderDetailItem detailItem : orderHeader.getOrderDetailItems())
					{
						 itemNo++;
						BigDecimal qty = detailItem.getItemsQty();
						if (qty.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
							qty = qty.setScale(0, BigDecimal.ROUND_HALF_DOWN);

						} else {
							qty = qty.setScale(2, BigDecimal.ROUND_HALF_DOWN);

						}
						int quantity=qty.intValue();
							for(int i=1;i<=quantity;i++){
//								 req +=  "     <image width=\"300\" height=\"140\" color=\"color_1\" mode=\"mono\" align=\"center\">" + image + "</image>";
								 req+=image;
								 req +=	" <text lang='en' align=\"center\">&#32;&#32;&#32;&#10;</text> " ;
								 req +=	"  <text lang='en' dh=\"false\" align=\"center\">Order# "+orderHeader.getOrderNumber()+":("+orderSourceGroup.getDisplayName()+")&#10;</text> " ;
								req += "  <text lang='en' align=\"center\">&#32;&#32;&#32; Sched On :-"+datetime+" &#10;</text> " ;
								
												if ( orderHeader.getFirstName()!= null && orderHeader.getFirstName().length()>0 )				
													req +=	"  <text lang='en' align=\"center\">"+orderHeader.getFirstName()+"</text> " ;
												if ( orderHeader.getLastName()!= null && orderHeader.getLastName().length()>0)				
													req +=	"  <text lang='en' align=\"center\">&#32;"+orderHeader.getLastName()+"&#10;</text> " ;
												req +="  <text >------------------------------------------</text>";
							
							List<OrderDetailAttribute> orderDetailAttributes = null;
							if (detailItem != null)
							{
								String queryString = "select c from OrderDetailAttribute c where c.orderDetailItemId =" + detailItem.getId();
								TypedQuery<OrderDetailAttribute> query = em.createQuery(queryString, OrderDetailAttribute.class);
								orderDetailAttributes = query.getResultList();
							}
							String itemName=detailItem.getItemsShortName();
							String itemName1 ="";
							String itemName2 = "";
							if(itemName!= null ){
								if(itemName.length()<41){
									itemName1 = itemName.substring(0);
								}else{
									itemName1 = itemName.substring(0,41);
								}
								
								if(itemName.length()<120){
									if(itemName.length()>41){
										itemName2 = itemName.substring(41);
									}
									
								}else{
									itemName2 = itemName.substring(41,100);
								}
								req +=			
										"  <text lang='en' dh=\"true\"  align=\"left\">"+""+"&#10;</text>";
							
							req +=			
										"  <text lang='en' dh=\"true\"  align=\"left\">"+ itemName1 +     "   ("+itemNo+")---"+i+"of"+quantity+ "&#10;</text>";
							
							if(itemName2 != null && itemName2.length()>0){
								req +=			
											"  <text lang='en' align=\"left\">"+ itemName2 + "&#10;</text>" ;
								
								}
							}
							
							for (OrderDetailAttribute detailAttribute : orderDetailAttributes)
							{
								
								if(!detailAttribute.getItemsAttributeName().contains("No")&& !detailAttribute.getItemsAttributeName().contains("Extra") && !detailAttribute.getItemsAttributeName().contains("Plus")){
									String itemsAttributeType=getItemsAttributeType(detailAttribute.getItemsAttributeId(), em);
									
									req +=			
											"  <text lang='en'  align=\"left\">-" +itemsAttributeType+" "+detailAttribute.getItemsAttributeName() + "&#10;</text>";
								
								}else {
									req +=			
											"  <text lang='en'  align=\"left\">-" +detailAttribute.getItemsAttributeName() + "&#10;</text>";
							
								}
										}
							// eligible for garbage collections
							orderDetailAttributes = null;
							req += " <cut /> ";
						}
										
					}
				
			
			
					
				req += "</epos-print></PrintData> 	</ePOSPrint> </PrintRequestInfo> ";
		
		}
		orderSource = null;
		orderSourceGroup = null;
		orderHeader = null;
		
				return req;
	}
	public String createLabelPDFStringForTMT88VI(EntityManager em, HttpServletRequest httpRequest, OrderHeader orderHeader, int takeoutDelivery,String printerName)
	{
		String req = null;
		
		printerName=printerName.replace(" ", "_");
		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderHeader.getOrderSourceId());
		OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
		Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, orderHeader.getLocationsId());
		List<Location> result = null;
		Location resultSet = null;
		if (location != null && location.getLocationsId() != null)
		{
			String queryString = "select l from Location l where (l.locationsId ='0' or l.locationsId is null) and l.businessId=" + location.getBusinessId() + " order by l.id asc";
			TypedQuery<Location> query = em.createQuery(queryString, Location.class);
			result = query.getResultList();
			if (result != null && result.size() > 0)
			{
				resultSet = result.get(0);
			}
		}
		else
		{
			resultSet = location;
		}

		String datetime = "";

		try
		{
			TimezoneTime time = new TimezoneTime();
			datetime = time.getDateTimeFromGMTToLocation(em, orderHeader.getScheduleDateTime(), resultSet.getId());
			SimpleDateFormat toformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat fromFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm aa");
			try
			{
				java.util.Date date = toformatter.parse(datetime);
				datetime = fromFormatter.format(date);

			}
			catch (ParseException e)
			{
				// todo shlok need
				// handle proper Exception
				 logger.severe(e);
			}
		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			 logger.severe(e);
		}

		/*Address userAddress = null;
		User orderUser = null;
		// getting user and address
		if(orderHeader.getUsersId() != null && orderHeader.getUsersId() != 0){
			String queryString = "select u from User u where id =? ";
			TypedQuery<User> query3 = em.createQuery(queryString, User.class).setParameter(1,orderHeader.getUsersId());
			 orderUser= query3.getSingleResult();
			 for(Address address2:orderUser.getAddressesSet()){
				 if(address2.getIsDefaultAddress()==1){
					 userAddress = address2;
				 }
			 }
		}*/

		if (orderHeader != null)
		{
			 req = 
						"<?xml version=\"1.0\" encoding=\"utf-8\"?> <PrintRequestInfo>"
						+ " 	<ePOSPrint> 		<Parameter> 			<devid>"+printerName+"</devid> 			<timeout>60000</timeout> 		</Parameter> 	"
						+ "	<PrintData>" + 
								"<epos-print xmlns='http://www.epson-pos.com/schemas/2011/03/epos-print'>" ;
									
					int itemNo=0;
					for (OrderDetailItem detailItem : orderHeader.getOrderDetailItems())
					{
						itemNo++;

						BigDecimal qty = detailItem.getItemsQty();
						if (qty.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
							qty = qty.setScale(0, BigDecimal.ROUND_HALF_DOWN);

						} else {
							qty = qty.setScale(2, BigDecimal.ROUND_HALF_DOWN);
						}
						int quantity=qty.intValue();
						for(int i=1;i<=quantity;i++){
						req +=	"  <text lang='en' dh=\"false\" align=\"center\">Order# "+orderHeader.getOrderNumber()+":("+orderSourceGroup.getDisplayName()+")&#10;</text> " ;
						req += "  <text lang='en' align=\"center\">&#32;&#32; Sched On :-"+datetime+" &#10;</text> " ;
						if ( orderHeader.getFirstName()!= null && orderHeader.getFirstName().length()>0 )				
							req +=	"  <text lang='en' align=\"center\">"+orderHeader.getFirstName()+"</text> " ;
						if ( orderHeader.getLastName()!= null && orderHeader.getLastName().length()>0)				
							req +=	"  <text lang='en' align=\"center\">&#32;"+orderHeader.getLastName()+"&#10;</text> " ;
						req +=			
								"  <text align=\"left\">------------------------------------------</text>";	
							List<OrderDetailAttribute> orderDetailAttributes = null;
							if (detailItem != null)
							{
								String queryString = "select c from OrderDetailAttribute c where c.orderDetailItemId =" + detailItem.getId();
								TypedQuery<OrderDetailAttribute> query = em.createQuery(queryString, OrderDetailAttribute.class);
								orderDetailAttributes = query.getResultList();
							}
							String itemName=detailItem.getItemsShortName();
							String itemName1 ="";
							String itemName2 = "";
							if(itemName!= null ){
								if(itemName.length()<52){
									itemName1 = itemName.substring(0);
								}else{
									itemName1 = itemName.substring(0,52);
								}
								
								if(itemName.length()<120){
									if(itemName.length()>52){
										itemName2 = itemName.substring(52);
									}
									
								}else{
									itemName2 = itemName.substring(52,100);
								}
								
							req +=			
										"  <text lang='en' dh=\"true\" align=\"left\"> "+ itemName1 +"    ("+itemNo+")---"+i+"of"+quantity+ "&#10;</text>";
							
							if(itemName2 != null && itemName2.length()>0){
								req +=			
											"  <text lang='en' dh=\"true\" align=\"left\">"+ itemName2 + "&#10;</text>";
								
								}
							}
							
							for (OrderDetailAttribute detailAttribute : orderDetailAttributes)
							{
									req +=			
										"  <text lang='en'  ul=\"0\"  align=\"left\">- &#32" + detailAttribute.getItemsAttributeName() + "&#10;</text>"+
										" " ;
							}
							// eligible for garbage collections
							orderDetailAttributes = null;
							req +="  <text lang='en'  ul=\"0\"  align=\"left\">" + " "+ "&#10;</text>"+
							" " ;
						req += "<cut />";
						
						}
					}
				req += "</epos-print></PrintData> 	</ePOSPrint> </PrintRequestInfo> ";
		
		}
		orderSource = null;
		orderSourceGroup = null;
		orderHeader = null;
		
				
		return req;
	}
	public String createLabelPDFStringForU220(EntityManager em, HttpServletRequest httpRequest, OrderHeader orderHeader, int takeoutDelivery,String printerName)
	{
		String req = null;
		
		printerName=printerName.replace(" ", "_");
		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderHeader.getOrderSourceId());
		OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
		Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, orderHeader.getLocationsId());
		List<Location> result = null;
		Location resultSet = null;
		List<Course> courses = null;
		if (location != null && location.getLocationsId() != null)
		{
			String queryString = "select l from Location l where (l.locationsId ='0' or l.locationsId is null) and l.businessId=" + location.getBusinessId() + " order by l.id asc";
			TypedQuery<Location> query = em.createQuery(queryString, Location.class);
			result = query.getResultList();
			if (result != null && result.size() > 0)
			{
				resultSet = result.get(0);
			}
		}
		else
		{
			resultSet = location;
		}

		String datetime = "";

		try
		{
			TimezoneTime time = new TimezoneTime();
			datetime = time.getDateTimeFromGMTToLocation(em, orderHeader.getScheduleDateTime(), resultSet.getId());
			SimpleDateFormat toformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat fromFormatter = new SimpleDateFormat("MMM dd,yyyy HH:mmaa");
			try
			{
				java.util.Date date = toformatter.parse(datetime);
				datetime = fromFormatter.format(date);

			}
			catch (ParseException e)
			{
				// todo shlok need
				// handle proper Exception
				 logger.severe(e);
			}
		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			 logger.severe(e);
		}

		Address userAddress = null;
		User orderUser = null;
		if(orderHeader.getUsersId() != null && orderHeader.getUsersId() != null){
			String queryString = "select u from User u where id =? ";
			TypedQuery<User> query3 = em.createQuery(queryString, User.class).setParameter(1,orderHeader.getUsersId());
			 orderUser= query3.getSingleResult();
			 for(Address address2:orderUser.getAddressesSet()){
				 if(address2.getIsDefaultAddress()==1){
					 userAddress = address2;
				 }
			 }
		}

		if (resultSet != null)
		{
			String queryString = "select c from Course c where c.locationsId =" + resultSet.getId() + " and c.status!='D'";
			TypedQuery<Course> query = em.createQuery(queryString, Course.class);
			courses = query.getResultList();
		}

		if (orderHeader != null)
		{
		
			
			if (courses != null && courses.size() > 0)
			{
				int display = 0;
				for (Course course : courses)
				{
					display = 0;
					for (OrderDetailItem detailItem : orderHeader.getOrderDetailItems())
					{

						BigDecimal qty = detailItem.getItemsQty();
						if (qty.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
							qty = qty.setScale(0, BigDecimal.ROUND_HALF_DOWN);

						} else {
							qty = qty.setScale(2, BigDecimal.ROUND_HALF_DOWN);

						}
						double quantity=qty.doubleValue();
						for(double i=0;i<quantity;i++){
							
							 req = 
										"<?xml version=\"1.0\" encoding=\"utf-8\"?> <PrintRequestInfo>"
										+ " 	<ePOSPrint> 		<Parameter> 			<devid>"+printerName+"</devid> 			<timeout>60000</timeout> 		</Parameter> 	"
										+ "	<PrintData>" + 
												"<epos-print xmlns='http://www.epson-pos.com/schemas/2011/03/epos-print'>" +
													"  <text lang='en' dh=\"true\" align=\"left\">"+datetime+"&#10;</text> ";
												req +=	"  <text lang='en' align=\"left\">Order# "+orderHeader.getOrderNumber()+"&#10;</text> " ;
												req +=	"  <text lang='en'  color=\"color_2\" align=\"left\">"+orderSourceGroup.getDisplayName()+" - "+orderSource.getDisplayName()+"&#10;</text> " ;
												req +=	"  <text lang='en' color=\"color_1\" align=\"left\">Guests:"+orderHeader.getPointOfServiceCount()+"&#10;</text> " ;
												req	+= "<page> ";
									
						
						if (detailItem.getSentCourseId() == course.getId())
						{
							List<OrderDetailAttribute> orderDetailAttributes = null;
							if (detailItem != null)
							{
								String queryString = "select c from OrderDetailAttribute c where c.orderDetailItemId =" + detailItem.getId();
								TypedQuery<OrderDetailAttribute> query = em.createQuery(queryString, OrderDetailAttribute.class);
								orderDetailAttributes = query.getResultList();
							}
							if (display == 0)
							{
								req +="  <text>---------------------------------</text>"+
									"  " ;
									req +="  <text lang='en' ul=\"0\" align=\"right\">"+course.getCourseName()+"&#10;</text>";
									
									req +="  <text>---------------------------------</text>"+
										"  " ;
								
							}
							display = 1;
						
							String itemName=detailItem.getItemsShortName();
							String itemName1 ="";
							String itemName2 = "";
							if(itemName!= null ){
								if(itemName.length()<52){
									itemName1 = itemName.substring(0);
								}else{
									itemName1 = itemName.substring(0,52);
								}
								
								if(itemName.length()<120){
									if(itemName.length()>52){
										itemName2 = itemName.substring(52);
									}
									
								}else{
									itemName2 = itemName.substring(52,100);
								}

							req +="  <text lang='en' color=\"color_1\"  ul=\"0\"  align=\"left\">"+qty+" " +itemName1 + "&#10;</text>"+
										" " ;
							
							if(itemName2 != null && itemName2.length()>0){
								req +="  <text lang='en'  ul=\"0\"  align=\"left\">"+ itemName2 + "&#10;</text>"+
											" " ;
								
								}
							}
							
							for (OrderDetailAttribute detailAttribute : orderDetailAttributes)
							{
									req +="  <text lang='en' color=\"color_2\"  ul=\"0\"  align=\"left\">- " + detailAttribute.getItemsAttributeName() + "&#10;</text>"+
										" " ;
							}
						}
						}
					}
				}
			}
			
			
				req +="  <text lang='en' color=\"color_1\">---------------------------------</text>"+
					"  " ;
				req += "  </page > " ;
				req += "  <text lang='en' align=\"center\">Scheduled On:-"+datetime+" &#10;</text> " ;
				req += "  <text lang='en' align=\"center\">&#10;</text> " ;
				// printing user info in bottom of receipt
				if (orderUser.getPhone() != null && orderUser.getPhone().length() >0)				
					req +=	"  <text lang='en' align=\"center\">"+orderUser.getPhone()+"&#10;</text> " ;
				if (orderUser.getEmail() != null && orderUser.getEmail().length() >0)				
					req +=	"  <text lang='en' align=\"center\">"+orderUser.getEmail()+"&#10;</text> " ;
				if ( orderHeader.getFirstName()!= null && orderHeader.getFirstName().length()>0 )				
					req +=	"  <text lang='en' align=\"center\">"+orderHeader.getFirstName()+"</text> " ;
				if ( orderHeader.getLastName()!= null && orderHeader.getLastName().length()>0)				
					req +=	"  <text lang='en' align=\"center\">&#32;"+orderHeader.getLastName()+"&#10;</text> " ;
				if(userAddress!= null){
					if (userAddress.getAddress1() != null && userAddress.getAddress1().length() >0)
						req +=	"  <text lang='en' align=\"center\">"+userAddress.getAddress2()+"&#10;</text> " ;
					if (userAddress.getCity() != null && userAddress.getCity().length()>0)
									req +=	"  <text lang='en' align=\"center\">"+userAddress.getCity()+" </text> " ;
					if (userAddress.getState() != null && userAddress.getState().length()>0)
									req +=	"  <text lang='en' align=\"center\">"+userAddress.getState()+"&#10;</text> " ;
					if (userAddress.getAddress2() != null && userAddress.getAddress2().length()>0)
						req +=	"  <text lang='en' align=\"center\">"+userAddress.getAddress2()+"&#10;</text> " ;
				}
				req += "  <text lang='en' align=\"left\">&#10;</text> " ;
				
				req += "  <text lang='en' width=\"1\" height=\"2\" align=\"center\">Powered By Nirvana XP&#10;</text> " ;
				
				req += " <cut /> " +
						"</epos-print></PrintData> 	</ePOSPrint> </PrintRequestInfo> ";
		
		}
		// eligible for garbage collection
		orderSource = null;
		orderSourceGroup = null;
		orderHeader = null;
		
		return req;
	}
	
	public String getItemsAttributeType(String itemsAttributeId, EntityManager em)
	{

		try
		{
			if (itemsAttributeId != null && em != null)
			{
/*
				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<ItemsAttributeTypeToItemsAttribute> criteria = builder.createQuery(ItemsAttributeTypeToItemsAttribute.class);
				Root<ItemsAttributeTypeToItemsAttribute> r = criteria.from(ItemsAttributeTypeToItemsAttribute.class);
				TypedQuery<ItemsAttributeTypeToItemsAttribute> query = em
						.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsAttributeTypeToItemsAttribute_.itemsAttributeId), itemsAttributeId),
								builder.notEqual(r.get(ItemsAttributeTypeToItemsAttribute_.status), "D")));
				ItemsAttributeTypeToItemsAttribute itemsAttributeTypeToItemsAttribute= query.getSingleResult();
				ItemsAttributeType attributeType=	(ItemsAttributeType) new CommonMethods().getObjectById("ItemsAttributeType", em,ItemsAttributeType.class,  itemsAttributeTypeToItemsAttribute.getItemsAttributeTypeId());
			*/
				String queryString = "select iiatt.name from items_attribute_type iiatt left join items_attribute_type_to_items_attribute "
						+ "iatta on iatta.items_attribute_type_id = iiatt.id left join items_attribute iatt on iatt.id = "
						+ "iatta.items_attribute_id where iatt.id = "+itemsAttributeId+" and iiatt.status = 'A' and iiatt.name in ('No','Extra','Plus')";
				List resultListDefault = em.createNativeQuery(queryString).getResultList();
				String name = "";
				if (resultListDefault.size() > 0) {
					for (Object objRow : resultListDefault) {
						name = (String) objRow;
					}
				}
				
				return name;
			}
		}
		catch (Exception e)
		{
			// todo shlok need to handle exception in below line
			logger.severe(e);
		}

		return "";

	}
	public String  convertLogo(String imageUrl) {

		 BufferedImage image = null;
		 try {
		     URL url = new URL(imageUrl);
		     image = ImageIO.read(url);
			    return generateXMLElement(image, image.getWidth());
		 } catch (IOException e) {
			 logger.severe(e);
		 }
		return null;

	    }
	
	private static String generateXMLElement(BufferedImage img, int width) {
        try {
			int height = img.getHeight() * width / img.getWidth();
//        	int height = 200;
//      	width=200;
			Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
			Graphics2D g2d = resized.createGraphics();
			g2d.drawImage(tmp, 0, 0, null);
			g2d.dispose();
			final byte[] pixels = ((DataBufferByte) resized.getRaster().getDataBuffer()).getData();
			final byte[] b64data = Base64.getEncoder().encode(pixels);
			final String b64String = new String(b64data, java.nio.charset.StandardCharsets.UTF_8);

			String output = String.format("<image width=\"%d\" height=\"%d\" color=\"color_1\" mode=\"mono\" align=\"center\">%s</image>", resized.getWidth(), resized.getHeight(), b64String);
			return output;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
    }

}
