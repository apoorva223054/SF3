/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.email;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.relationalentity.helper.ItemRelationsHelper;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.data.SettleCreditCardBatchPacket;
import com.nirvanaxp.services.jaxrs.CalculatedPaymentSummary;
import com.nirvanaxp.services.jaxrs.OrderManagementServiceBean;
import com.nirvanaxp.services.util.email.SendEmail;
import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.catalog.course.Course;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.Item_;
import com.nirvanaxp.types.entities.discounts.DiscountWays;
import com.nirvanaxp.types.entities.employee.CashRegisterRunningBalance;
import com.nirvanaxp.types.entities.employee.EmployeeOperation;
import com.nirvanaxp.types.entities.employee.EmployeeOperationToCashRegister;
import com.nirvanaxp.types.entities.inventory.GoodsReceiveNotes;
import com.nirvanaxp.types.entities.inventory.RequestOrder;
import com.nirvanaxp.types.entities.inventory.RequestOrderDetailItems;
import com.nirvanaxp.types.entities.inventory.RequestOrderDetailItems_;
import com.nirvanaxp.types.entities.inventory.RequestOrder_;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.orders.BatchDetail;
import com.nirvanaxp.types.entities.orders.DeliveryOption;
import com.nirvanaxp.types.entities.orders.ItemToSupplier;
import com.nirvanaxp.types.entities.orders.OrderDetailAttribute;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus_;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup;
import com.nirvanaxp.types.entities.payment.PaymentMethod;
import com.nirvanaxp.types.entities.payment.PaymentMethodType;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType;
import com.nirvanaxp.types.entities.payment.PaymentType;
import com.nirvanaxp.types.entities.payment.TransactionStatus;
import com.nirvanaxp.types.entities.printers.Printer;
import com.nirvanaxp.types.entities.salestax.SalesTax;
import com.nirvanaxp.types.entities.salestax.SalesTax_;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.UsersToPaymentHistory;

// TODO: Auto-generated Javadoc
/**
 * The Class ReceiptPDFFormat.
 * @param <itemGroupId>
 */
public class ReceiptPDFFormat
{

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(SendEmail.class.getName());

	/**
	 * Creates the receipt PDF string.
	 *
	 * @param em
	 *            the em
	 * @param httpRequest
	 *            the http request
	 * @param orderId
	 *            the order id
	 * @param takeoutDelivery
	 *            the takeout delivery
	 * @param isFromReceived
	 *            the is from received
	 * @return the string builder
	 */
	public StringBuilder createReceiptPDFString(EntityManager em, HttpServletRequest httpRequest, String orderId, int takeoutDelivery, boolean isFromReceived,boolean quotation)
	{

		// todo shlok need
		// modularise method
		String locationpath = null;
		String qrcodePath = null;
		String serverURL = null;
		String feedbackUrl = null;

		try
		{
			locationpath = ConfigFileReader.getWebsiteLogoPath();
			qrcodePath = ConfigFileReader.getQRCodeUploadPathFromFile();
			serverURL = ConfigFileReader.getQRCodeServerName();
			feedbackUrl = ConfigFileReader.getAdminFeedbackURL();
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, "getWebsiteLogoPath not found in database for" + orderId);
		}

		// new OrderManagementServiceBean(httpRequest);
		OrderManagementServiceBean managementServiceBean = new OrderManagementServiceBean();
		String discountDisplayName = "";

		OrderHeader orderHeader = managementServiceBean.getOrderById(em, orderId);
		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderHeader.getOrderSourceId());
		OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
		if (orderSourceGroup.getName().equals("In Store"))
		{
			takeoutDelivery = 0;
		}
		else
		{
			takeoutDelivery = 1;
		}

		if (orderHeader.getDiscountsId() != null && orderHeader.getDiscountsId() != null && orderHeader.getDiscountDisplayName() != null)
		{
			discountDisplayName = orderHeader.getDiscountDisplayName();
		}

		Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, orderHeader.getLocationsId());
		Address localAddress = null;
		// com.nirvanaxp.global.types.entities.Address address = null;
		List<Location> result = null;
		Location foundLocation = null;
		List<Course> courses = null;
		if (location != null && location.getLocationsId() != null)
		{
			String queryString = "select l from Location l where (l.locationsId ='0' or l.locationsId is null ) and l.businessId=" + location.getBusinessId() + " order by l.id asc";
			TypedQuery<Location> query = em.createQuery(queryString, Location.class);
			result = query.getResultList();
			if (result != null && result.size() > 0)
			{
				foundLocation = result.get(0);
				if(foundLocation != null){
					localAddress = foundLocation.getAddress();
				}
				
			}
			
		}
		else
		{
			localAddress = location.getAddress();
			foundLocation = location;

		}
		String datetime = new TimezoneTime().getDateTimeFromGMTToLocation(em, orderHeader.getScheduleDateTime(), foundLocation.getId());
		SimpleDateFormat toformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat fromFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");

		try
		{
			java.util.Date date = toformatter.parse(datetime);
			datetime = fromFormatter.format(date);
		}
		catch (ParseException e)
		{
			logger.severe("Unable to parse date", datetime, "while generating pdf receipt");
		}
		SimpleDateFormat toformatterForAdditionalQuations = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");
		SimpleDateFormat fromFormatterForAdditionalQuations = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss");
		String datetimeForAdditionalQuation = "";
		try
		{
			java.util.Date date = toformatterForAdditionalQuations.parse(datetime);
			datetimeForAdditionalQuation = fromFormatterForAdditionalQuations.format(date);
		}
		
		catch (ParseException e)
		{
			logger.severe("Unable to parse date", datetime, "while generating pdf receipt");
		}

		qrcodePath = "http://" + serverURL + orderHeader.getQrcode() + ".png";
		feedbackUrl = feedbackUrl + "refno=" + orderHeader.getReferenceNumber() + "&order_id=" + orderHeader.getId();

		if (foundLocation != null)
		{
			String queryString = "select c from Course c where c.locationsId =? and c.status!='D'";
			TypedQuery<Course> query = em.createQuery(queryString, Course.class);
			query.setParameter(1, foundLocation.getId());
			courses = query.getResultList();
		}
		StringBuilder receipt = null;
		if (orderHeader != null)
		{
			String address1 = null;
			String address2 = null;
			String city = null;
			String state = null;
			String phone = null;
			if(localAddress!=null){
				address1 = localAddress.getAddress1();
				address2 = localAddress.getAddress2();
				city = localAddress.getCity();
				state = localAddress.getState();
				phone = localAddress.getPhone();
			}
			

			receipt = new StringBuilder().append("<html>                                                              ").append("<head>")

			.append("</head>                                                             ").append("<body>                                                              ")
					.append("                                                                       ").append("<center>                                                            ")
					.append("                                                                       ").append("<div id=\"header\">                                                ")
					.append("<img src=\"" + locationpath + foundLocation.getImageUrl() + "\" alt=\"" + foundLocation.getName() + "\"><br>                         ");
			if (address1 != null)
				receipt.append("" + address1 + "<br>                                             ");
			if (address2 != null && address2.length() > 0)
				receipt.append("" + address2 + "<br>                                              ");
			if (city != null)
				receipt.append("" + city + "<br>                                              ");
			if (state != null)
				receipt.append("" + state + "<br>                                              ");

			if (phone != null)
				receipt.append("" + phone + "<br>                                                    ");

			if (isFromReceived)
			{
				receipt.append("<div align=\"center\">  <br />	You order below is received and currently processing.<br />  Please contact " + foundLocation.getName() + ""
						+ " if you do not receive an email confirmation within 5 mins. <br />Thank you for placing an order with " + foundLocation.getName() + "<br /><br />" + "</div>");
			}
			if (orderSourceGroup!=null && orderSourceGroup.getName().equals("Catering")) {
				receipt.append("<a href=\"" + foundLocation.getWebsite() + "\">" + foundLocation.getWebsite() + "</a>").append("</div>                                                             "
						 +" Your "+orderSourceGroup.getDisplayName()+" order will be ready by "+ orderHeader.getStartDate());
			}else{
				receipt.append("<a href=\"" + foundLocation.getWebsite() + "\">" + foundLocation.getWebsite() + "</a>").append("</div>                                                              ")
//				" Your "+orderSourceGroup.getDisplayName()+" order will be ready by"
						.append("<div id=\"date\">                                                     ")
						.append( datetime)
						.append("</div>                                                              ");
			}
			
			if (quotation) {
				receipt.append(" <div>Quote # " + orderHeader.getOrderNumber()
						+ " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Guest Count: " + orderHeader.getPointOfServiceCount()
						+ " </div>                                                              ");

			} else {
				if (takeoutDelivery == 1) {
					receipt.append(
							" <div>Order #: " + orderHeader.getOrderNumber() + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
									+ orderSourceGroup.getDisplayName() + ":-" + orderSource.getDisplayName()
									+ " </div>                                                              ");
				} else {
					receipt.append(" <div>Order #: " + orderHeader.getOrderNumber()
							+ " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Table:-" + location.getName()
							+ " </div>                                                              ");
				}
			}
	
			receipt.append("</center>                                                           ");

			receipt.append("  </div>    <div align=\"center\">                                                     ");
			receipt.append("<table ><tr><td  colspan=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br/></td>");
			BigDecimal discountValue = new BigDecimal(0);
			BigDecimal orderLevelDiscountSubtotal = new BigDecimal(0);
			List<DiscountRow> discountRows = new ArrayList<DiscountRow>();

			if (courses != null && courses.size() > 0)
			{
				int display = 0;

				for (Course course : courses)
				{
					display = 0;

					for (OrderDetailItem detailItem : orderHeader.getOrderDetailItems())
					{
						BigDecimal qty = detailItem.getItemsQty();
						if (qty.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0)
						{
							qty = qty.setScale(0, BigDecimal.ROUND_HALF_DOWN);

						}
						else
						{
							qty = qty.setScale(2, BigDecimal.ROUND_HALF_DOWN);

						}
						if (detailItem.getOrderDetailStatusId() != 0)
						{
							OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class, detailItem.getOrderDetailStatusId());

							if (orderDetailStatus != null && !(orderDetailStatus.getName().equalsIgnoreCase("Item Removed")) && !(orderDetailStatus.getName().equalsIgnoreCase("Recall")))
							{
								if (detailItem.getSentCourseId().equals(course.getId()))
								{
									List<OrderDetailAttribute> orderDetailAttributes = null;
									if (detailItem != null)
									{
										String queryString = "select c from OrderDetailAttribute c where c.orderDetailItemId =?" ;
										TypedQuery<OrderDetailAttribute> query = em.createQuery(queryString, OrderDetailAttribute.class).setParameter(1, detailItem.getId());
										orderDetailAttributes = query.getResultList();
									}
									if (display == 0)
									{
										receipt.append("<tr align=\"left\">    <td><u>" + course.getCourseName() + "</u></td>  	<td align=\"right\">&nbsp;</td> </tr>  ");
									}
									display = 1;
									BigDecimal price = detailItem.getPriceSelling().multiply(qty);

									receipt.append("<tr align=\"left\">                                                   ")
											.append("<td >&nbsp;&nbsp;" + qty + " &nbsp;" + detailItem.getItemsShortName() + "</td>		                    ")
											.append("<td align=\"right\">" + price.setScale(2, BigDecimal.ROUND_HALF_DOWN) + " </td>	                                        ")
											.append("                                                                ").append("</tr>                                                               ");

									for (OrderDetailAttribute detailAttribute : orderDetailAttributes)
									{
										OrderDetailStatus orderDetailStatus1 = em.find(OrderDetailStatus.class, detailAttribute.getOrderDetailStatusId());

										if (orderDetailStatus1 != null && !(orderDetailStatus1.getName().equalsIgnoreCase("Attribute Removed"))
												&& !(orderDetailStatus1.getName().equalsIgnoreCase("Recall")))
										{

											BigDecimal attributePrice = detailAttribute.getPriceSelling().multiply(qty);
											receipt.append("<tr align=\"left\">                                                   ")
													.append("<td >&nbsp;&nbsp;- " + detailAttribute.getItemsAttributeName() + " &nbsp;</td>		                    ")
													.append("<td align=\"right\">" + attributePrice + " </td>	                                        ")
													.append("                                                                ")
													.append("</tr>                                                               ");

										}
									}
									if (detailItem.getDiscountWaysId() != 0)
									{
										DiscountWays discountWays = em.find(DiscountWays.class, detailItem.getDiscountWaysId());

										if (discountWays != null && (discountWays.getName().equals("Item Level") || discountWays.getName().equals("Seat Level")))
										{
											String discountReasonForItem = detailItem.getDiscountReasonName();
											discountValue = detailItem.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_DOWN);

											if (discountValue.compareTo(BigDecimal.ZERO) != 0)
											{
												receipt.append("<tr align=\"left\">                                                   ")
														.append("<td >&nbsp;&nbsp;-" + "(" + discountReasonForItem + ")" + " &nbsp;</td>		                    ")
														.append("<td align=\"right\">-" + discountValue + " </td>	                                        ")
														.append("                                                                ")
														.append("</tr>                                                               ")
														.append("                                                                       ")
														.append("<br/>                                                               ");

											}

											DiscountRow temp = new DiscountRow();
											temp.setDiscountDisplayName(discountReasonForItem);
											temp.setDiscountId(detailItem.getDiscountId());
											temp.setDiscountValue(detailItem.getDiscountValue());
											if (detailItem.getDiscountName().equals("Custom Discount"))
											{
												temp.setDiscountSubTotal(detailItem.getPriceDiscount());
											}
											else
											{
												temp.setDiscountSubTotal(detailItem.getSubTotal());
											}

											if (detailItem.getDiscountTypeName().equals("Amount Off"))
											{
												temp.setAmountOffPriceDis(detailItem.getPriceDiscount());
											}

											temp.setDiscountTypeName(detailItem.getDiscountTypeName());
											temp.setDiscountName(detailItem.getDiscountName());

											int indexOf = discountRows.indexOf(temp);
											if (indexOf == -1)
											{
												discountRows.add(temp);
											}
											else
											{

												DiscountRow temp1 = discountRows.get(indexOf);
												temp1.setDiscountSubTotal(temp1.getDiscountSubTotal().add(temp.getDiscountSubTotal()));

												if (detailItem.getDiscountTypeName().equals("Amount Off"))
												{
													temp1.setAmountOffPriceDis(temp1.getAmountOffPriceDis().add(temp.getAmountOffPriceDis()));
												}

											}

										}

										if (discountWays != null && discountWays.getName().equals("Order Level"))
										{
											orderLevelDiscountSubtotal = orderLevelDiscountSubtotal.add(detailItem.getSubTotal());
										}
									}

								}
							}
						}
					}
				}
			}

			BigDecimal subTotal = orderHeader.getSubTotal().setScale(2, BigDecimal.ROUND_HALF_DOWN);

			receipt.append("     <tr>                                                                  ").append("<td ><b>Subtotal</b></td>		                                        ")
					.append("<td align=\"right\">" + subTotal + "</td>	                                    ").append("</tr> " + "");

			if (orderHeader.getDiscountsId() != null && orderHeader.getDiscountsId() != null)
			{
				if (orderHeader.getDiscountsTypeName().equals("Percentage Off"))
				{
					receipt.append("                                                                       ")
							.append("<tr align=\"left\">                                                   ")
							.append("<td ><b> DISCOUNT " + discountDisplayName + " (" + orderHeader.getDiscountsValue() + " % of " + orderLevelDiscountSubtotal.setScale(2, BigDecimal.ROUND_HALF_DOWN)
									+ ")</b></td>		                    ").append("<td align=\"right\">-" + orderHeader.getPriceDiscount() + "</td>	                                        ")
							.append("</tr>                                                               ").append("                                                                       ");
				}
				else
				{
					receipt.append("                                                                       ")
							.append("<tr align=\"left\">                                                   ")
							.append("<td ><b>DISCOUNT " + discountDisplayName + " (" + orderHeader.getDiscountsValue() + " off of " + orderLevelDiscountSubtotal.setScale(2, BigDecimal.ROUND_HALF_DOWN)
									+ ")</b></td>		                    ").append("<td align=\"right\">-" + orderHeader.getPriceDiscount() + "</td>	                                        ")
							.append("</tr>                                                               ").append("                                                                       ");
				}
			}

			if (discountRows != null && discountRows.size() > 0)
			{
				for (DiscountRow discountRow : discountRows)
				{
					if (discountRow.getDiscountTypeName().equals("Percentage Off"))
					{
						if (discountRow.getDiscountName().equals("Custom Discount"))
						{
							receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
									.append("<td> <b>" + discountRow.getDiscountName() + "</b></td>		                    ")
									.append("<td align=\"right\">-" + (discountRow.getDiscountSubTotal().setScale(2, BigDecimal.ROUND_HALF_DOWN)) + "</td>	                                        ")
									.append("</tr>                                                               ").append("                                                                       ");
						}
						else
						{
							receipt.append("                                                                       ")
									.append("<tr align=\"left\">                                                   ")
									.append("<td ><b>" + discountRow.getDiscountDisplayName() + " (" + discountRow.getDiscountValue() + " % of "
											+ discountRow.getDiscountSubTotal().setScale(2, BigDecimal.ROUND_HALF_DOWN) + ")</b></td>		                    ")
									.append("<td align=\"right\">-"
											+ new BigDecimal(discountRow.getDiscountSubTotal().doubleValue() * (Double.parseDouble(discountRow.getDiscountValue() + "") / Double.parseDouble(100 + "")))
													.setScale(2, BigDecimal.ROUND_HALF_DOWN) + "</td>	                                        ")
									.append("</tr>                                                               ").append("                                                                       ");
						}

					}
					else
					{
						if (discountRow.getDiscountName().equals("Custom Discount"))
						{
							receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
									.append("<td ><b>" + discountRow.getDiscountName() + "</b></td>		                    ")
									.append("<td align=\"right\">-" + (discountRow.getDiscountSubTotal().setScale(2, BigDecimal.ROUND_HALF_DOWN)) + "</td>	                                        ")
									.append("</tr>                                                               ").append("                                                                       ");
						}
						else
						{
							receipt.append("                                                                       ")
									.append("<tr align=\"left\">                                                   ")
									.append("<td ><b>" + discountRow.getDiscountDisplayName() + " (" + discountRow.getDiscountValue() + " off of "
											+ discountRow.getDiscountSubTotal().setScale(2, BigDecimal.ROUND_HALF_DOWN) + ")</b></td>		                    ")
									.append("<td align=\"right\">-" + discountRow.getAmountOffPriceDis().setScale(2, BigDecimal.ROUND_HALF_DOWN) + "</td>	                                        ")
									.append("</tr>                                                               ").append("                                                                       ");
						}

					}

				}
			}

			if (orderHeader.getDeliveryCharges() != null && orderHeader.getDeliveryCharges().doubleValue() > 0)
			{
				if(orderHeader.getDeliveryOptionId()!=null){
					String string = ((DeliveryOption) new CommonMethods().getObjectById("DeliveryOption", em,DeliveryOption.class, orderHeader.getDeliveryOptionId())).getDisplayName();
					
					BigDecimal deliveryCharges = orderHeader.getDeliveryCharges().setScale(2, BigDecimal.ROUND_HALF_DOWN);

					receipt.append("     <tr>                                                                  ").append("<td ><b>" + string + "</b></td>		                                        ")
							.append("<td align=\"right\">" + deliveryCharges + "</td>	                                    ").append("</tr> " + "");
				}
				
			}

			if (orderHeader.getServiceCharges() != null && orderHeader.getServiceCharges().doubleValue() > 0)
			{

				try
				{
					String string = getSalesTaxByNameAndLocationId(em, "Service Charge", foundLocation.getId()).getDisplayName();
					BigDecimal serviceCharges = orderHeader.getServiceCharges().setScale(2, BigDecimal.ROUND_HALF_DOWN);

					receipt.append("     <tr>                                                                  ").append("<td ><b>" + string + "</b></td>		                                        ")
							.append("<td align=\"right\">" + serviceCharges + "</td>	                                    ").append("</tr> " + "");
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					logger.severe(e);
				}
			}

			if (orderHeader.getIsGratuityApplied() != 0)
			{
				BigDecimal newSubTotal = subTotal;
				// added by Shlok by 02-04-2018 #43935,#43934
				newSubTotal = newSubTotal.subtract(orderHeader.getPriceDiscount().add(orderHeader.getPriceDiscountItemLevel()));
				SalesTax gratuity = getGratuty(em, foundLocation.getId());

				double gratuityPrice = (gratuity.getRate().doubleValue() * newSubTotal.doubleValue()) / 100;
				if (gratuityPrice != 0)
				{
					receipt.append("<tr align=\"left\">")
							.append("<td ><b>" + gratuity.getDisplayName() + " ( " + gratuity.getRate() + "% of " + String.format("%.2f", newSubTotal) + ")</b></td>		                        ")
							.append("<td align=\"right\">" + String.format("%.2f", gratuityPrice) + "</td>	                                    ")
							.append("</tr>                                                               ");
				}
			}
			subTotal = subTotal.subtract(orderHeader.getPriceDiscount());
			BigDecimal newPrice;
			if (orderHeader.getTaxDisplayName1() != null && !orderHeader.getTaxDisplayName1().equals("") && orderHeader.getTaxRate1() != new BigDecimal(0))
			{
				BigDecimal itemPrice = getItemLevelTax(em, orderHeader, foundLocation.getId(), orderHeader.getTaxName1());
				if (itemPrice.compareTo(BigDecimal.ZERO)== 0)
				{
					newPrice = subTotal;
				}
				else
				{
					newPrice = itemPrice;
				}
				receipt.append("<tr align=\"left\">").append("<td ><b>" + orderHeader.getTaxDisplayName1() + " ( " + orderHeader.getTaxRate1() + "% of " + newPrice + ")</b></td>		                        ")
						.append("<td align=\"right\">" + orderHeader.getPriceTax1() + "</td>	                                    ")
						.append("</tr>                                                               ");
			}
			if (orderHeader.getTaxDisplayName2() != null && !orderHeader.getTaxDisplayName2().equals("") && orderHeader.getTaxRate2() != new BigDecimal(0))
			{
				BigDecimal itemPrice = getItemLevelTax(em, orderHeader, foundLocation.getId(), orderHeader.getTaxName2());
				if (itemPrice.compareTo(BigDecimal.ZERO) == 0)
				{
					newPrice = subTotal;
				}
				else
				{
					newPrice = itemPrice;
				}
				receipt.append("<tr align=\"left\">").append("<td ><b>" + orderHeader.getTaxDisplayName2() + " ( " + orderHeader.getTaxRate2() + "% of " + newPrice + ")</b></td>		                        ")
						.append("<td align=\"right\">" + orderHeader.getPriceTax2() + "</td>	                                    ")
						.append("</tr>                                                               ");
			}
			if (orderHeader.getTaxDisplayName3() != null && !orderHeader.getTaxDisplayName3().equals("") && orderHeader.getTaxRate3() != new BigDecimal(0))
			{
				BigDecimal itemPrice = getItemLevelTax(em, orderHeader, foundLocation.getId(), orderHeader.getTaxName3());
				
				if (itemPrice.compareTo(BigDecimal.ZERO) == 0)
				{
					newPrice = subTotal;
				}
				else
				{
					newPrice = itemPrice;
				}
				receipt.append("<tr align=\"left\">").append("<td ><b>" + orderHeader.getTaxDisplayName3() + " ( " + orderHeader.getTaxRate3() + "% of " + newPrice + ")</b></td>		                        ")
						.append("<td align=\"right\">" + orderHeader.getPriceTax3() + "</td>	                                    ")
						.append("</tr>                                                               ");
			}
			if (orderHeader.getTaxDisplayName4() != null && !orderHeader.getTaxDisplayName4().equals("") && orderHeader.getTaxRate4() != new BigDecimal(0))
			{
				BigDecimal itemPrice = getItemLevelTax(em, orderHeader, foundLocation.getId(), orderHeader.getTaxName4());
				if (itemPrice.compareTo(BigDecimal.ZERO)== 0)
				{
					newPrice = subTotal;
				}
				else
				{
					newPrice = itemPrice;
				}
				receipt.append("<tr align=\"left\">").append("<td ><b>" + orderHeader.getTaxDisplayName4() + " ( " + orderHeader.getTaxRate4() + "% of " + newPrice + ")</b></td>		                        ")
						.append("<td align=\"right\">" + orderHeader.getPriceTax4() + "</td>	                                    ")
						.append("</tr>                                                               ");
			}

			BigDecimal totalHeader = new BigDecimal(0);
			if (location.getIsRoundOffRequired() == 1)
			{
				totalHeader = orderHeader.getTotal();
			}
			else
			{
				totalHeader = orderHeader.getRoundOffTotal();
			}

			receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
					.append("<td ><b>Total</b></td>		                                        ").append("<td align=\"right\"><b>" + totalHeader + "</b></td>	                                ")
					.append("</tr>                                                               ").append("<tr align=\"left\">                                                   ")
					.append("<td colspan=\"2\" align=\"center\"><b>&nbsp;</b></td>	                                ").append("</tr>                                                               ")
					.append("</tr>                                                               ");

			receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
					.append("<td ><b>Payment</b></td>		                                        ").append("<td align=\"right\"><b>" + "Amount Paid" + "</b></td>	                                ")
					.append("</tr>                                                               ");
			// adding order payment details
			BigDecimal total = new BigDecimal("0.00");
			BigDecimal totalTotal = new BigDecimal("0.00");
			BigDecimal totalTip = new BigDecimal("0.00");
			BigDecimal totalDue = new BigDecimal("0.00");
			BigDecimal cashTipAmount = new BigDecimal("0.00");
			BigDecimal creditTipAmount = new BigDecimal("0.00");
			BigDecimal creditTermTipAmount = new BigDecimal("0.00");

			for (OrderPaymentDetail orderPaymentDetail : orderHeader.getOrderPaymentDetails())
			{
				PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();
				PaymentMethodType methodType = (PaymentMethodType) new CommonMethods().getObjectById("PaymentMethodType", em,PaymentMethodType.class, paymentMethod.getPaymentMethodTypeId());
				PaymentTransactionType paymentTransactionType = orderPaymentDetail.getPaymentTransactionType();
				if (paymentTransactionType !=null && (paymentTransactionType.getDisplayName().equals("CaptureAll") || paymentTransactionType.getDisplayName().equals("Force")))
				{
					break;
				}
				String paymentMethodTypeName = "";
				if (methodType.getName().equals("Credit Card") || methodType.getName().equals("Manual CC Entry") 
						|| methodType.getName().equals("Manual Credit Card") 
						|| methodType.getName().equals("PreAuth Credit Card")
						|| methodType.getName().equals("PreAuth Manual CC Entry"))
				{
					if(orderPaymentDetail.getCardType() != null)
					{
						paymentMethodTypeName += orderPaymentDetail.getCardType();	
					}
					
					if( orderPaymentDetail.getCardNumber() != null)
					{
						paymentMethodTypeName +=  " " + orderPaymentDetail.getCardNumber();
					}
					 
					if (orderPaymentDetail.getPaymentTransactionType().getName().equals("Void"))
					{
						paymentMethodTypeName += "-V";
						// totalTotal =
						// totalTotal.subtract(orderPaymentDetail.getAmountPaid());

						// cashTipAmount =
						// cashTipAmount.subtract(orderPaymentDetail.getCashTipAmt());
						// creditTipAmount =
						// creditTipAmount.subtract(orderPaymentDetail.getCreditcardTipAmt());
					}
					else if (orderPaymentDetail.getIsRefunded() == 0)
					{
						total = total.add(orderPaymentDetail.getAmountPaid());
						totalTotal = totalTotal.add(orderPaymentDetail.getAmountPaid());
						cashTipAmount = cashTipAmount.add(orderPaymentDetail.getCashTipAmt());
					
						if (creditTipAmount != null && orderPaymentDetail.getCreditcardTipAmt() != null)
						{
							creditTipAmount = creditTipAmount.add(orderPaymentDetail.getCreditcardTipAmt());
						}
						if (creditTermTipAmount != null && orderPaymentDetail.getCreditTermTip() != null)
						{
							creditTermTipAmount = creditTermTipAmount.add(orderPaymentDetail.getCreditTermTip());
						}
					}
					
				}
				else if (!methodType.getName().equalsIgnoreCase("Discount"))
				{
					paymentMethodTypeName = methodType.getDisplayName();
					if (orderPaymentDetail.getPaymentTransactionType().getName().equals("Refund"))
					{
						paymentMethodTypeName += "-R";
						// added by Shlok by 02-04-2018 #43935,#43934
						// totalTotal =
						// totalTotal.subtract(orderPaymentDetail.getAmountPaid());
						// cashTipAmount =
						// cashTipAmount.subtract(orderPaymentDetail.getCashTipAmt());
					}
					else if (orderPaymentDetail.getIsRefunded() == 0)
					{
						total = total.add(orderPaymentDetail.getAmountPaid());
						totalTotal = totalTotal.add(orderPaymentDetail.getAmountPaid());
						cashTipAmount = cashTipAmount.add(orderPaymentDetail.getCashTipAmt());

					}
				}
				else if (methodType.getName().equalsIgnoreCase("Discount"))
				{
					paymentMethodTypeName = methodType.getDisplayName();
					if (orderPaymentDetail.getIsRefunded() == 1)
					{
						paymentMethodTypeName += "-R";

					}

				}
				
				String dbTime = orderPaymentDetail.getDate() + " " + orderPaymentDetail.getTime();

				String locationTime = "";

				try
				{
					java.util.Date date = toformatter.parse(dbTime);
					locationTime = fromFormatter.format(date);
				}
				catch (ParseException e)
				{
					logger.severe("Could not parse date", dbTime, " while generating pdf receipt");
				}

				if (orderPaymentDetail.getCashTipAmt().compareTo(new BigDecimal("0.00")) != 0
						|| (orderPaymentDetail.getCreditcardTipAmt() != null && orderPaymentDetail.getCreditcardTipAmt().compareTo(new BigDecimal("0.00")) != 0))
				{
					receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
							.append("<td >" + locationTime + " " + paymentMethodTypeName + "</td>		                                        ")
							.append("<td align=\"right\">" + orderPaymentDetail.getAmountPaid() + "</td>	                                ")
							.append("</tr>                                                               ");
					receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
							.append("<td >Tips</td>		                                        ")
							.append("<td align=\"right\">" + orderPaymentDetail.getCashTipAmt().add(orderPaymentDetail.getCreditcardTipAmt()) + "</td>	                                ")
							.append("</tr>                                                               ");
				}
				else
				{
					if (methodType.getName().equalsIgnoreCase("Discount"))
					{
						receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
								.append("<td >" + locationTime + " " + paymentMethodTypeName + "</td>		                                        ")
								.append("<td align=\"right\">" + orderPaymentDetail.getPriceDiscount() + "</td>	                                ")
								.append("</tr>                                                               ");
					}
					else
					{
						receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
								.append("<td >" + locationTime + " " + paymentMethodTypeName + "</td>		                                        ")
								.append("<td align=\"right\">" + orderPaymentDetail.getAmountPaid() + "</td>	                                ")
								.append("</tr>                                                               ");
					}

				}

			}
			totalDue = totalHeader.subtract(total);
			totalTip = cashTipAmount.add(creditTipAmount).add(creditTermTipAmount);

			receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
					.append("<td ><b>Total</b></td>		                                        ").append("<td align=\"right\">" + "" + totalTotal+ "</td>	                                ")
					.append("</tr>                                                               ");

			receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
					.append("<td ><b>Total Tips</b></td>		                                        ").append("<td align=\"right\">" + totalTip + "</td>	                                ")
					.append("</tr>                                                               ");

			receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
					.append("<td ><b>Total Due</b></td>		                                        ").append("<td align=\"right\">" + totalDue + "</td>	                                ")
					.append("</tr>                                                               ");
			// total due


			String queryString = "select ft.field_type_name,oaq.question,aqa.answer_value "
					+ " from additional_question_answer aqa "
					+ " join order_additional_question oaq on oaq.id = aqa.question_id "
					+ " join field_type ft on ft.id=oaq.field_type_id where order_header_id=? order by oaq.display_sequence ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString)
					.setParameter(1, orderId).getResultList();
			if(resultList!=null && resultList.size()>0){
				
				receipt.append("</center>                                                           ");
				receipt.append("</center>                                                           ");
				receipt.append("<br/>                                                           ");
				
//				receipt.append(" </div> <div align=\"center\">                                                           ");
//				receipt.append("<table ><tr><td  colspan=\"2\"><br/></td>");
////				
//				receipt.append(" <div>"+"-----------------------------------------------------------------"+ " </div> ");
////				Your Delivery Order will be ready by Tue, 16 11:44 AM'
//				receipt.append("<div>" + "Sched On :- "+datetimeForAdditionalQuation +  " </div> ");
//				receipt.append("<div>" + "End On :-"+orderHeader.getEndDate() +  " </div> ");
//				receipt.append(" <div>"+"-----------------------------------------------------------------"+ " </div> ");
//				
//				receipt.append(" <div>"+"-------------------------------------------"+ " </div> ");
//				receipt.append(" <div>"+"Order Additional Information"+ " </div> ");
//				receipt.append(" <div>"+"-------------------------------------------"+ " </div> ");
				
				receipt.append("</tr>                                                               ").append("<tr align=\"left\">                                                   ")
				.append("<td colspan=\"2\" align=\"center\">-----------------------------------------------------------------------------------</td>	                                ").append("</tr>                                                               ")
				.append("</tr>");  
				receipt.append("</tr>                                                               ").append("<tr align=\"left\">                                                   ")
				.append("<td colspan=\"2\" align=\"center\">Sched On :- "+orderHeader.getStartDate() +  "</td>	                                ").append("</tr>                                                               ")
				.append("</tr>");  
				receipt.append("</tr>                                                               ").append("<tr align=\"left\">                                                   ")
				.append("<td colspan=\"2\" align=\"center\">End On :-"+orderHeader.getEndDate() +  " </td>	                                ").append("</tr>                                                               ")
				.append("</tr>");
				receipt.append("</tr>                                                               ").append("<tr align=\"left\">                                                   ")
				.append("<td colspan=\"2\" align=\"center\">-----------------------------------------------------------------------------------------------------</td>	                                ").append("</tr>                                                               ")
				.append("</tr>");  
				receipt.append("</tr>                                                               ").append("<tr align=\"left\">                                                   ")
				.append("<td colspan=\"2\" align=\"center\">-----------------------------------------------------------------</td>	                                ").append("</tr>                                                               ")
				.append("</tr>");  
				receipt.append("</tr>                                                               ").append("<tr align=\"left\">                                                   ")
				.append("<td colspan=\"2\" align=\"center\">Order Additional Information</td>	                                ").append("</tr>                                                               ")
				.append("</tr>");  
				receipt.append("</tr>                                                               ").append("<tr align=\"left\">                                                   ")
				.append("<td colspan=\"2\" align=\"center\">-----------------------------------------------------------------</td>	                                ").append("</tr>                                                               ")
				.append("</tr>");  
			 

			for (Object[] objRow : resultList) {

				String fieldTypeName = (String) objRow[0];
				String question = (String) objRow[1];
				String answer = (String) objRow[2];

				if(answer==null || answer.equalsIgnoreCase("null")){
					answer="";
				}
				if (fieldTypeName != null && fieldTypeName.equals("Check Box")) {
						if (answer!=null && answer.equals("1")) {
							answer = "<b>Yes";
						}
//						else if(answer!=null && (answer.length()>1 || answer.length()==0)) {
//							answer = "<b>"+answer;
//						}
						else {
							answer = "<b>No";
						}
						
						if(answer.length()<7){
							receipt.append("<tr align=\"left\"> ").append("<td >&nbsp;&nbsp;" + question + " &nbsp;:"+ answer + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>	").append("</tr>");
								
						}else {
							receipt.append("<tr align=\"left\"> ").append("<td >&nbsp;&nbsp;" + question + " &nbsp;:" + "</td>	").append("</tr>");
							if(answer.length()>30){
								receipt.append("<tr align=\"left\"> ").append("<td >&nbsp;&nbsp;" + answer.substring(0,30) + "</td>	").append("</tr>");
								receipt.append("<tr align=\"left\"> ").append("<td >&nbsp;&nbsp;<b>" + answer.substring(30,(answer.length())) + "</td>	").append("</tr>");
								
							}else {
								receipt.append("<tr align=\"left\"> ").append("<td >&nbsp;&nbsp;" + answer +"</td>	").append("</tr>");
								
							}
							}
						}
				if (fieldTypeName != null && fieldTypeName.equals("Test Box")) {
					if (answer != null) {
						if (answer.equals("1")) {
							answer = "<b>Yes";
						}
//						else if(answer!=null && (answer.length()>1 || answer.length()==0)) {
//							answer = "<b>"+answer;
//						} 
						else {
							answer = "<b>No";
						}
					}
					
					if(answer.length()<7){
						receipt.append("<tr align=\"left\"> ").append("<td >&nbsp;&nbsp;" + question + " &nbsp;:" + answer + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>	").append("</tr>");
					}else {
						receipt.append("<tr align=\"left\"> ").append("<td >&nbsp;&nbsp;" + question + " &nbsp;:" + "</td>	").append("</tr>");
						if(answer.length()>30){
							receipt.append("<tr align=\"left\"> ").append("<td >&nbsp;&nbsp;" + answer.substring(0,30) + "</td>	").append("</tr>");
							receipt.append("<tr align=\"left\"> ").append("<td >&nbsp;&nbsp;<b>" + answer.substring(30,(answer.length())) + "</td>	").append("</tr>");
							
						}else {
							receipt.append("<tr align=\"left\"> ").append("<td >&nbsp;&nbsp;" + answer +"</td>	").append("</tr>");
							
						}
					}
					
				}
				
				if (fieldTypeName != null
							&& fieldTypeName.equals("Textarea Box")) {
						if (answer != null && answer.length() > 0) {
							receipt.append("<tr align=\"left\"> ")
									.append("<td >&nbsp;&nbsp;" + question + " &nbsp;: <b>" + answer + "</b></td>	")
									.append("</tr>");
						}

					}
			}
			receipt.append("</tr>                                                               ").append("<tr align=\"left\">                                                   ")
			.append("<td colspan=\"2\" align=\"center\"><b>&nbsp;</b></td>	                                ").append("</tr>                                                               ")
			.append("</tr>");   
			receipt.append("</tr>                                                               ").append("<tr align=\"left\">                                                   ")
			.append("<td colspan=\"2\" align=\"center\"><a href=\"#\">Click here to confirm and pay</a></td>	                                ").append("</tr>                                                               ")
			.append("</tr>");   
			}
			receipt.append("</tr>                                                               ").append("<tr align=\"left\">                                                   ")
			.append("<td colspan=\"2\" align=\"center\"><b>&nbsp;</b></td>	                                ").append("</tr>                                                               ")
			.append("</tr>                                                               ");
			User user = (User) new CommonMethods().getObjectById("User", em,User.class, orderHeader.getUsersId());

			if (user != null)
			{
				receipt.append("<tr align=\"center\">                                                   ");
				if (user.getPhone() != null && user.getPhone().length() > 0)
					receipt.append("<td colspan=\"2\" align=\"center\">" + user.getPhone() + "</td>	 </tr>                               ");
				if (user.getEmail() != null && user.getEmail().length() > 0)

					receipt.append("<tr>	<td colspan=\"2\" align=\"center\">" + user.getEmail() + "</td>	 </tr>                                  ");

				if (user.getFirstName() != null && user.getFirstName().length() > 0)

					receipt.append("<tr>	<td colspan=\"2\" align=\"center\">" + user.getFirstName() + " " + user.getLastName() + "</td>	 </tr>                               ");

				// address set was of zero length so checked the condition of
				// size of make code work By Ap 2015-12-29

				logger.severe("- Address For sendEmailForOrder orderHeader.getId() " + orderHeader.getId() + " orderHeader.getAddressShipping() " + orderHeader.getAddressShipping());
				Address userAddress = null;

				if (orderHeader.getAddressShipping() != null)
				{
					userAddress = orderHeader.getAddressShipping();

				}
				else if (orderHeader.getAddressBilling() != null)
				{
					userAddress = orderHeader.getAddressBilling();

				}
				else if (user.getAddressesSet() != null && user.getAddressesSet().size() > 0)
				{
					List<Address> addressList = new ArrayList<Address>(user.getAddressesSet());
					if (addressList != null && addressList.get(0) != null)
					{
						userAddress = addressList.get(0);
					}
				}

				if (userAddress != null)
				{
					if (userAddress.getAddress1() != null)
						receipt.append("<tr>	<td colspan=\"2\" align=\"center\">" + userAddress.getAddress1() + "</td>	</tr>                                ");

					if (userAddress.getCity() != null)
						receipt.append("<tr>	<td colspan=\"2\" align=\"center\">" + userAddress.getCity() + "</td> </tr>	                                ");

					if (userAddress.getState() != null)
						receipt.append("<tr>	<td colspan=\"2\" align=\"center\">" + userAddress.getState() + "</td></tr>	                                ");

					if (userAddress.getZip() != null)
						receipt.append("<tr>	<td colspan=\"2\" align=\"center\">" + userAddress.getZip() + "</td>	 </tr>                               ");

					if (userAddress.getAddress2() != null)
						receipt.append(" <tr>	<td colspan=\"2\" align=\"center\">" + userAddress.getAddress2() + "</td></tr>	                                ");
				}

			}
						

			if (foundLocation.getDisplayQrcode() == 1 && !quotation){
				receipt.append("<tr align=\"center\">                                                   ")
						.append("<td colspan=\"2\" align=\"center\"><b>Please Scan Or Click</b></td>	                                ")
						.append("</tr>                                                               ").append("<tr align=\"center\">                                                   ")
						.append("<td colspan=\"2\" align=\"center\"><a href=\"" + feedbackUrl + "\"><img src=\"" + qrcodePath + "\"></a></td>	                                ")
						.append("</tr>                                                               ").append("<tr align=\"center\">                                                   ")
						.append("<td colspan=\"2\" align=\"center\"><b>For Feedback</b></td>	                                ")
						.append("</tr>                                                               ").append("</tr>                                                               ");
			}
			receipt.append("<tr align=\"center\">                                                   ").append("<td colspan=\"2\" align=\"center\"><b>&nbsp;</b></td>	                                ")
					.append("</tr>                                                               ").append("</tr>                                                               ")
					.append("<tr align=\"center\">                                                   ");

			receipt.append("<td colspan=\"2\" align=\"center\"><b>Powered By</b></td>	                                ")
					.append("</tr>                                                               ")
					.append("<tr align=\"center\">                                                   ")
					.append("<td colspan=\"2\" align=\"center\"><a href=\"http://www.nirvanaxp.com\"><img src=\"https://live.nirvanaxp.com/images/nirvanaxp_Blackap.png\" width=\"136\" height=\"40\"></a></td>	                                ")
					.append("</tr>                                                               ");

			receipt.append("</table>   </div>                                                         ").append("</div>                                                              ")
					.append(" <br></div>		</body>                                                             ").append("</html>                                                             ");

		}

		return receipt;
	}

	private SalesTax getSalesTaxByNameAndLocationId(EntityManager em, String name, String locationId)
	{
		try
		{
			String queryString = "select s from SalesTax s where s.taxName =? and s.locationsId=?  ";
			TypedQuery<SalesTax> query = em.createQuery(queryString, SalesTax.class).setParameter(1, name).setParameter(2, locationId);
			return query.getSingleResult();
		}
		catch (Exception e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);

		}
		return null;
	}

	/**
	 * Check attribute print.
	 *
	 * @param attributeId
	 *            the attribute id
	 * @param em
	 *            the em
	 * @return true, if successful
	 */
	public boolean checkAttributePrint(int attributeId, EntityManager em)
	{
		boolean result = false;

		String queryString = " SELECT COUNT( * ) FROM  `order_detail_attribute` oda JOIN items_attribute_type_to_items_attribute itt ON oda.items_attribute_id = itt.items_attribute_id JOIN items_attribute_type iat ON iat.id = itt.items_attribute_type_id "
				+ " WHERE iat.is_required =1 AND oda.id =" + attributeId + "  ";
		Object resultList = em.createNativeQuery(queryString).getSingleResult();
		int count = (Integer) resultList;
		if (count > 0)
			result = true;

		return result;
	}

	/**
	 * Creates the payment receipt PDF string.
	 *
	 * @param em
	 *            the em
	 * @param httpRequest
	 *            the http request
	 * @param orderPaymentDetail
	 *            the order payment detail
	 * @param orderHeader
	 *            the order header
	 * @param takeoutDelivery
	 *            the takeout delivery
	 * @return the string builder
	 */
	public StringBuilder createPaymentReceiptPDFString(EntityManager em, HttpServletRequest httpRequest, OrderPaymentDetail orderPaymentDetail, OrderHeader orderHeader, int takeoutDelivery)
	{

		// todo shlok need
		// modularise method
		String locationpath = null;
		try
		{
			locationpath = ConfigFileReader.getWebsiteLogoPath();
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, "getWebsiteLogoPath not found in database for" + orderHeader.getId());
		}

		User server = (User) new CommonMethods().getObjectById("User", em,User.class, orderHeader.getServerId());
		String serverName = "";
		if (server != null)
		{
			serverName = server.getFirstName() + " " + server.getLastName();
		}
		User cashier = (User) new CommonMethods().getObjectById("User", em,User.class, orderHeader.getCashierId());
		String cashierName = "";
		if (cashier != null)
		{
			cashierName = cashier.getFirstName() + " " + cashier.getLastName();
		}

		Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, orderHeader.getLocationsId());
		Address address = null;
		List<Location> result = null;
		Location resultSet = null;
		// List<Course> courses = null;
		if (location != null && location.getLocationsId() != null)
		{
			String queryString = "select l from Location l where (l.locationsId ='0' or l.locationsId is null ) and l.businessId=" + location.getBusinessId() + " order by l.id asc";
			TypedQuery<Location> query = em.createQuery(queryString, Location.class);
			result = query.getResultList();
			if (result != null && result.size() > 0)
			{
				resultSet = result.get(0);
			}
			address = resultSet.getAddress();
		}
		else
		{
			address = location.getAddress();
			resultSet = location;

		}
		String datetime = "";

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
			logger.severe("Could not parse date", datetime, "while generating pdf receipt");
		}

		StringBuilder receipt = null;
		if (orderHeader != null)
		{
			String address1 = address.getAddress1();
			String address2 = address.getAddress2();
			String city = address.getCity();
			String phone = address.getPhone();
			String state = address.getState();

			receipt = new StringBuilder().append("<html>                                                              ").append("<head>")

			.append("</head>                                                             ").append("<body>                                                              ")
					.append("                                                                       ").append("<center>                                                            ")
					.append("                                                                       ").append("<div id=\"header\">                                                ")
					.append("<img src=\"" + locationpath + resultSet.getImageUrl() + "\" alt=\"" + resultSet.getName() + "\"><br>                         ");
			if (address1 != null && address1.length() > 0)
				receipt.append("" + address1 + "<br>                                             ");
			if (address2 != null && address2.length() > 0)
				receipt.append("" + address2 + "<br>                                              ");
			if (city != null && city.length() > 0)
				receipt.append("" + city + "<br>                                              ");
			if (state != null && state.length() > 0)
				receipt.append("" + state + "<br>                                              ");
			if (phone != null)
				receipt.append("" + phone + "<br>                                                    ");
			receipt.append("<a href=\"" + resultSet.getWebsite() + "\">" + resultSet.getWebsite() + "</a>").append("</div>                                                              ")
					.append("<div id=\"date\">                                                     ").append(" " + datetime + "                                         ")
					.append("</div>                                                              ");
			String transactionType = "";
			if (orderPaymentDetail.getTransactionStatus().getName().equals("Cash Sale"))
			{
				transactionType = "Cash Sale";
			}
			else if (orderPaymentDetail.getTransactionStatus().getName().equals("CC Auth"))
			{
				transactionType = "Credit Card Sale";
			}
			if (transactionType != null)
				receipt.append(" <div>" + transactionType + " </div>      ");
			if (takeoutDelivery == 1)
			{
				receipt.append(" <div>Order #: " + orderHeader.getOrderNumber() + " </div>                                                              ");
			}
			else
			{
				receipt.append(" <div>Order #: " + orderHeader.getOrderNumber() + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Table:-" + location.getName()
						+ " </div>                                                              ");
				receipt.append(" <div>Server: " + serverName + " </div>                                                              ");
				receipt.append(" <div>Cashier: " + cashierName + " </div>                                                              ");
			}
			receipt.append("</center>                                                           ");

			receipt.append("  </div>    <div align=\"center\">                                                     ");
			receipt.append("<table ><tr><td  colspan=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"

					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + "&nbsp;&nbsp;&nbsp;&nbsp;<br/></td></tr>");

			BigDecimal tip = orderPaymentDetail.getCashTipAmt().add(orderPaymentDetail.getCreditcardTipAmt());
			BigDecimal subTotal = tip.add(orderPaymentDetail.getAmountPaid()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
			receipt.append("     <tr>                                                                  ").append("<td >Amount Paid</td>		                                        ")
					.append("<td align=\"right\">" + orderPaymentDetail.getAmountPaid() + "</td>	                                    ")
					.append("</tr>                                                               ");
			receipt.append("     <tr>                                                                  ").append("<td >Tip</td>		                                        ")
					.append("<td align=\"right\">" + orderPaymentDetail.getCashTipAmt().add(orderPaymentDetail.getCreditcardTipAmt()) + "</td>	                                    ")
					.append("</tr>                                                               ");
			receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
					.append("<td ><b>Total</b></td>		                                        ").append("<td align=\"right\"><b>" + subTotal + "</b></td>	                                ")
					.append("</tr>                                                               ").append("<tr align=\"left\">                                                   ")
					.append("<td colspan=\"2\" align=\"center\"><b>&nbsp;</b></td>	                                ").append("</tr>                                                               ")
					.append("</tr>                                                               ");
			receipt.append("<tr align=\"center\">                                                   ")
					.append("<td colspan=\"2\" align=\"center\"><b>&nbsp;</b></td>	                                ")
					.append("</tr>                                                               ")
					.append("</tr>                                                               ")
					.append("<tr align=\"center\">                                                   ")
					.append("<td colspan=\"2\" align=\"center\"><b>Powered By</b></td>	                                ")
					.append("</tr>                                                               ")
					.append("<tr align=\"center\">                                                   ")
					.append("<td colspan=\"2\" align=\"center\"><a href=\"http://www.nirvanaxp.com\"><img src=\"https://live.nirvanaxp.com/images/nirvanaxp_Blackap.png\" width=\"136\" height=\"40\"></a></td>	                                ")
					.append("</tr>                                                               ")
					.append("</table>   </div>                                                         ")
					.append("</div>                                                              ")
					.append("<br><div align=\"center\">Please note: This email was sent from a notification-only address that cannot accept incoming email. Please do not reply to this message.</div>	</body>                                                             ")
					.append("</html>                                                             ");

		}
		return receipt;
	}

	/**
	 * Gets the gratuty.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return the gratuty
	 */
	public SalesTax getGratuty(EntityManager em, String locationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<SalesTax> criteria = builder.createQuery(SalesTax.class);
		Root<SalesTax> r = criteria.from(SalesTax.class);
		TypedQuery<SalesTax> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(SalesTax_.taxName), "Gratuity"), builder.equal(r.get(SalesTax_.locationsId), locationId),
				builder.notEqual(r.get(SalesTax_.status), "D")));

		return query.getSingleResult();
	}

	/**
	 * Creates the request order invoice PDF string.
	 *
	 * @param em
	 *            the em
	 * @param httpRequest
	 *            the http request
	 * @param requestOrderId
	 *            the request order id
	 * @param locationId
	 *            the location id
	 * @param pOFor
	 *            the o for
	 * @param grnRef
	 *            the grn ref
	 * @return the string builder
	 */
	public StringBuilder createRequestOrderInvoicePDFString(EntityManager em, HttpServletRequest httpRequest, String requestOrderId, String locationId, int pOFor, String grnRef)
	{

		String locationpath = null;
		// todo shlok need
		// remove unused
		// todo shlok need
		// modularise method
		String qrcodePath = null;
		String serverURL = null;
		String feedbackUrl = null;

		String logoImagePath = "";

		try
		{
			locationpath = ConfigFileReader.getWebsiteLogoPath();
			qrcodePath = ConfigFileReader.getQRCodeUploadPathFromFile();
			serverURL = ConfigFileReader.getQRCodeServerName();
			feedbackUrl = ConfigFileReader.getAdminFeedbackURL();
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, "getWebsiteLogoPath not found in database for" + requestOrderId);
		}

		Location foundLocation = null;
		Address localAddress = null;

		Location supplierFoundLocation = null;
		Address supplierLocalAddress = null;

		List<RequestOrderDetailItems> requestOrderDetailItemsList = new ArrayList<RequestOrderDetailItems>();
		if (pOFor == 1 || pOFor == 4)
		{

			String queryString = "select rodi from GoodsReceiveNotes rodi where rodi.grnNumber ='" + grnRef + "'";
			TypedQuery<GoodsReceiveNotes> query2 = em.createQuery(queryString, GoodsReceiveNotes.class);

			List<GoodsReceiveNotes> goodsReceiveNotes = query2.getResultList();

			for (GoodsReceiveNotes receiveNotes : goodsReceiveNotes)
			{
				String queryItemString = "select rodi from RequestOrderDetailItems rodi where rodi.id ='" + receiveNotes.getRequestOrderDetailsItemId()+"'";
				TypedQuery<RequestOrderDetailItems> query = em.createQuery(queryItemString, RequestOrderDetailItems.class);
				RequestOrderDetailItems detailItems = query.getSingleResult();
				requestOrderId = detailItems.getRequestId();
				requestOrderDetailItemsList.add(detailItems);
			}

		}
		else if (pOFor == 2)
		{
			// Data for po created
			requestOrderDetailItemsList = getRequestOrderDetailItemsByIdAndLocationId(em, requestOrderId);

		}
		else if (pOFor == 3)
		{
			// Data for po cancelled
			requestOrderDetailItemsList = getRequestOrderDetailItemsByIdAndLocationId(em, requestOrderId);

		}

		RequestOrder requestOrder = getRequestOrderByIdAndLocationId(em, requestOrderId);

		if (locationId != null)
		{
			String queryString = "select l from Location l where " + "l.id ='" + locationId + "' and (l.locationsId = '0' or l.locationsId is null)  and l.locationsTypeId = '1'";
			TypedQuery<Location> query = em.createQuery(queryString, Location.class);
			foundLocation = query.getSingleResult();
			localAddress = foundLocation.getAddress();
		}

		StringBuilder receipt = null;
		if (requestOrder != null)
		{

			String address1 = localAddress.getAddress1();
			String address2 = localAddress.getAddress2();
			String city = localAddress.getCity();
			String state = localAddress.getState();
			String phone = localAddress.getPhone();
			String zip = localAddress.getZip();

			String suppName = "";
			String suppAddress1 = "";
			String suppAddress2 = "";
			String suppCity = "";
			String suppState = "";
			String suppPhone = "";
			String suppZip = "";

			if (pOFor == 1 )
			{

				if (requestOrder.getLocationId() != null)
				{

					String queryString = "select l from Location l where " + "l.id ='" + requestOrder.getLocationId() + "' and (l.locationsId = '0' or l.locationsId is null)";
					TypedQuery<Location> query = em.createQuery(queryString, Location.class);
					supplierFoundLocation = query.getSingleResult();
					supplierLocalAddress = supplierFoundLocation.getAddress();
				}

				// Data for allotment
				if (supplierFoundLocation != null)
				{
					suppName = supplierFoundLocation.getName();
				}
				if (supplierLocalAddress != null)
				{

					suppAddress1 = supplierLocalAddress.getAddress1();
					suppAddress2 = supplierLocalAddress.getAddress2();
					suppCity = supplierLocalAddress.getCity();
					suppState = supplierLocalAddress.getState();
					suppPhone = supplierLocalAddress.getPhone();
					suppZip = supplierLocalAddress.getZip();
				}

			}
			else
			{
				// Data for po created and
				// Data for po cancelled
				
				if (requestOrder.getSupplierId() != null)
				{
					try
					{
						String queryString = "select l from Location l where " + "l.id ='" + requestOrder.getSupplierId() +"' and l.locationsTypeId in (3,4)";
						TypedQuery<Location> query = em.createQuery(queryString, Location.class);
						supplierFoundLocation = query.getSingleResult();
						supplierLocalAddress = supplierFoundLocation.getAddress();
						

					}
					catch (Exception e)
					{
						// TODO: handle exception
						logger.severe(httpRequest, "No supplier Found Location for location id " + requestOrder.getSupplierId());
					}
				}

				if (supplierFoundLocation != null)
				{
					suppName = supplierFoundLocation.getName();
				}
				if (supplierLocalAddress != null)
				{

					suppAddress1 = supplierLocalAddress.getAddress1();
					suppAddress2 = supplierLocalAddress.getAddress2();
					suppCity = supplierLocalAddress.getCity();
					suppState = supplierLocalAddress.getState();
					suppPhone = supplierLocalAddress.getPhone();
					suppZip = supplierLocalAddress.getZip();
				}
			}

			String poDate = new TimezoneTime().getTimeFromCreatedUpdated(requestOrder.getUpdated().getTime(), em, requestOrder.getLocationId());
			receipt = new StringBuilder();

			receipt.append("<!DOCTYPE html>").append("<html>").append("<head>").append("</head>").append("<body>");

			if (logoImagePath != null && !logoImagePath.isEmpty())
			{
				receipt.append("<img src=\"" + locationpath + foundLocation.getImageUrl() + "\" alt=\"" + foundLocation.getName() + "\"> <br/>");
			}

			String title = "";
			if (pOFor == 1)
			{

				// Data for allotment
				title = "Allotment Order";
			}
			else if (pOFor == 4)
			{
				title = "PO Receive from Supplier";
			}
			else
			{
				// Data for po created and
				// Data for po cancelled
				if (pOFor == 3)
				{
					title = "Purchase Order (Cancelled)";
				}
				else
				{
					title = "Purchase Order";
				}

			}

			String senderaddrStr = "";
			if (pOFor == 1)
			{

				// Data for allotment
				senderaddrStr = "Sender Address";
			}
			else
			{
				// Data for po created and
				// Data for po cancelled
				senderaddrStr = "Sender Address";
			}

			receipt.append("<h2 style=\"width: 100%; text-align:center; \">" + title + "</h2>").append("").append("<table style=\"font-family: arial, sans-serif;")
					.append("border-collapse: collapse;").append("width: 100%;\">").append("<tr >").append("<th width=\"50%\" style=\"border: 1px solid #dddddd;	padding: 8px;\" align=\"left\" >")
					.append("<div id=\"project\">").append("<div>").append("<span>" + senderaddrStr + "</span> ").append("</div>");

			if (foundLocation.getName() != null && !foundLocation.getName().isEmpty())
			{
				receipt.append("<div>").append("<span>" + foundLocation.getName() + "</span> ").append("</div>");
			}

			if (address1 != null && !address1.isEmpty())
			{
				receipt.append("<div>").append("<span>" + address1 + "</span>").append("</div>");
			}

			if (address2 != null && !address2.isEmpty())
			{
				receipt.append("<div>").append("<span>" + address2 + "</span>").append("</div>");
			}

			receipt.append("<div>");
			if (zip != null && city != null && !zip.isEmpty() && !city.isEmpty())
			{
				receipt.append("<span>" + city + "-" + zip + "</span>");
			}
			else if (city != null && !city.isEmpty())
			{
				receipt.append("<span>" + city + "</span>");
			}
			else if (zip != null && !zip.isEmpty())
			{
				receipt.append("<span>" + zip + "</span>");
			}

			String numberStr = "";
			String poNumberStr = "";
			if (pOFor == 1)
			{

				// Data for allotment
				numberStr = "GRN No.";
				poNumberStr = grnRef;

			}
			else
			{
				// Data for po created and
				// Data for po cancelled
				numberStr = "PO No.";
				poNumberStr = requestOrder.getId() + "";
			}

			String dateStr = "";
			if (pOFor == 1)
			{

				// Data for allotment
				dateStr = "Date And Time";
			}
			else
			{
				// Data for po created and
				// Data for po cancelled
				dateStr = "PO Date And Time";
			}

			String refString = "";
			if (pOFor == 1)
			{

				// Data for allotment
				refString = "Location's Ref";
			}
			else
			{
				// Data for po created and
				// Data for po cancelled
				refString = "Supplier's Ref";
			}

			String addrStr = "";
			if (pOFor == 1)
			{

				// Data for allotment
				addrStr = "Delivery Address -";
			}
			else
			{
				// Data for po created and
				// Data for po cancelled
				addrStr = "Supplier Address -";
			}

			receipt.append("</div>");

			if (state != null && !state.isEmpty())
			{
				receipt.append("<div>").append("<span>" + state + "</span>").append("</div>");
			}

			if (phone != null && !phone.isEmpty())
			{
				receipt.append("<div>").append("<span>" + phone + "</span>").append("</div>");
			}

			receipt.append("</div>").append("</th>").append("<th  width=\"50%\" style=\"border: 1px solid #dddddd;	padding: 8px;\">").append("<table style=\"font-family: arial, sans-serif;")
					.append("border-collapse: collapse;").append("width: 100%;\">").append("").append("<tr>")
					.append("<td style=\"border-top: 1px solid #FFFFFF; border-right: 1px solid #dddddd;border-bottom: 1px solid #dddddd;").append("    border-left: 1px solid #FFFFFF;")
					.append("padding: 8px;\" align=\"left\">" + numberStr + "<br/>" + poNumberStr).append("</td>").append("<td  style=\"border-top: 1px solid #FFFFFF;")
					.append("    border-right: 1px solid #FFFFFF; border-left: 1px solid #dddddd; border-bottom: 1px solid #dddddd;")
					.append("padding: 8px;\" align=\"left\">" + dateStr + " <br/> " + poDate).append("</td>").append("</tr>").append("<tr align=\"left\">")
					.append("<td style=\"border-left: 1px solid #FFFFFF;").append("    border-bottom: 1px solid #FFFFFF; border-right: 1px solid #dddddd;")
					.append("padding: 8px;\"> " + refString + "</td>").append("<td style=\"border-right: 1px solid #FFFFFF;").append("    border-bottom: 1px solid #FFFFFF;")
					.append("padding: 8px;\"  >Other Reference(s)</td>").append("</tr>").append("</table>").append("").append("</th>").append("").append("</tr>").append("<tr>")
					.append("<th style=\"border-right: 1px solid #dddddd; border-left: 1px solid #dddddd;	padding: 8px;\" align=\"left\">").append("<div id=\"project\">").append("<div>")
					.append("<span> " + addrStr + "</span>");

			
			if (!suppName.isEmpty())
			{
				receipt.append("<br/>" + suppName);
			}

			if (!suppAddress1.isEmpty())
			{
				receipt.append("<br/>" + suppAddress1);
			}

			if (!suppAddress2.isEmpty())
			{
				receipt.append("<br/>" + suppAddress2);
			}

			if (!suppZip.isEmpty() && !suppCity.isEmpty())
			{
				receipt.append("<br/>" + suppCity + "-" + suppZip);
			}
			else if (!suppCity.isEmpty())
			{
				receipt.append("<br/>" + suppCity);
			}
			else if (!suppZip.isEmpty())
			{
				receipt.append("<br/>" + suppZip);
			}

			if (!suppState.isEmpty())
			{
				receipt.append("<br/>" + suppState);
			}

			if (!suppPhone.isEmpty())
			{
				receipt.append("<br/>" + suppPhone);
			}

			receipt.append("</div>").append("").append("</div>").append("</th>").append("<th style=\"border-right: 1px solid #dddddd;	padding: 8px;\">").append("<div id=\"project\">")
					.append("</div>").append("</th>").append("</tr>").append("</table>")

					.append("<table style=\"font-family: arial, sans-serif;").append("border-collapse: collapse;")
					.append("width: 100%; page-break-inside:always; \"  repeat_header=\"1\" page-break-after:auto;>");

			String qytStr = "";
			if (pOFor == 1)
			{

				// Data for allotment
				receipt.append(" <thead style=\"display: table-header-group\"> <tr>").append("<th style=\"border: 1px solid #dddddd;	padding: 8px;\" width=\"10%\">Sl No.</th>")
						.append("<th style=\"border: 1px solid #dddddd;	padding: 8px;\" width=\"40\">Description of Goods</th>")
						.append("<th style=\"border: 1px solid #dddddd;	padding: 8px;\" width=\"10%\">UOM</th>")
						.append("<th style=\"border: 1px solid #dddddd;	padding: 8px;\" width=\"10%\">Ordered Quantity</th>")
						.append("<th style=\"border: 1px solid #dddddd;	padding: 8px;\" width=\"10%\">Alloted Quantity</th>").append("</tr>  </thead>").append("");

			}
			else if (pOFor == 4)
			{

				// Data for allotment
				receipt.append(" <thead style=\"display: table-header-group\"> <tr>").append("<th style=\"border: 1px solid #dddddd;	padding: 8px;\" width=\"10%\">Sl No.</th>")
						.append("<th style=\"border: 1px solid #dddddd;	padding: 8px;\" width=\"40\">Description of Goods</th>")
						.append("<th style=\"border: 1px solid #dddddd;	padding: 8px;\" width=\"10%\">UOM</th>")
						.append("<th style=\"border: 1px solid #dddddd;	padding: 8px;\" width=\"10%\">Receive Quantity</th>")

						.append("</tr>  </thead>").append("");

			}
			else
			{
				// Data for po created and
				// Data for po cancelled

				receipt.append(" <thead style=\"display: table-header-group\"> <tr>").append("<th style=\"border: 1px solid #dddddd;	padding: 8px;\" width=\"10%\">Sl No.</th>")
						.append("<th style=\"border: 1px solid #dddddd;	padding: 8px;\" width=\"40\">Description of Goods</th>")
						.append("<th style=\"border: 1px solid #dddddd;	padding: 8px;\" width=\"10%\">UOM</th>")
						.append("<th style=\"border: 1px solid #dddddd;	padding: 8px;\" width=\"10%\">Quantity</th>")
						.append("<th style=\"border: 1px solid #dddddd;	padding: 8px;\" width=\"10%\">Rate</th>")
						.append("<th style=\"border: 1px solid #dddddd;	padding: 8px;\" width=\"20%\">Amount</th>").append("</tr>  </thead>").append("");
			}

			BigDecimal totalAmount = new BigDecimal(0);
			BigDecimal totalQuantity = new BigDecimal(0);
			BigDecimal totalRate = new BigDecimal(0);
			BigDecimal totalPer = new BigDecimal(0);
			int srNo = 0;
			if (requestOrderDetailItemsList != null && requestOrderDetailItemsList.size() > 0)
			{

				for (RequestOrderDetailItems detailItems : requestOrderDetailItemsList)
				{

					srNo++;
					String itemName = "";
					String itemUOM = "";
					Item item = getItemById(em, detailItems.getItemsId());
					BigDecimal rate;
					if (pOFor == 1)
					{
						// quantity for allotment
						rate = detailItems.getQuantity();
					}
					else
					{
						// quantity cancel po and po
						rate = item.getPurchasingRate();
					}

					BigDecimal quantity;
					if (pOFor == 1)
					{
						// quantity for allotment
						String queryString = "select rodi from GoodsReceiveNotes rodi where rodi.grnNumber ='" + grnRef + "'" + "and rodi.requestOrderDetailsItemId = '" + detailItems.getId()+"'";
						TypedQuery<GoodsReceiveNotes> query2 = em.createQuery(queryString, GoodsReceiveNotes.class);

						GoodsReceiveNotes goodsReceiveNotes = query2.getSingleResult();
						quantity = goodsReceiveNotes.getAllotmentQty();
					}
					else
					{
						// quantity cancel po and po
						quantity = detailItems.getQuantity();
					}

					BigDecimal per = new BigDecimal(0);
					BigDecimal amount = quantity.multiply(rate);

					totalQuantity = totalQuantity.add(quantity);
					totalAmount = totalAmount.add(amount);
					totalRate = totalRate.add(rate);
					totalPer = totalPer.add(per);

					if (detailItems.getItemName() != null && !detailItems.getItemName().isEmpty())
					{

						itemName = detailItems.getItemName();

					}
					else if (item.getDisplayName() != null && !item.getDisplayName().isEmpty())
					{
						itemName = item.getDisplayName();
					}
					else if (item.getName() != null && !item.getName().isEmpty())
					{
						itemName = item.getName();
					}

					if (detailItems.getUomName() != null && !detailItems.getUomName().isEmpty())
					{
						itemUOM = detailItems.getUomName();
					}

					if (quantity != null && quantity != new BigDecimal(0))
					{
						quantity = quantity.setScale(2, BigDecimal.ROUND_HALF_DOWN);
					}

					if (amount != null && amount != new BigDecimal(0))
					{
						amount = amount.setScale(2, BigDecimal.ROUND_HALF_DOWN);
					}

					if (rate != null && rate != new BigDecimal(0))
					{
						rate = rate.setScale(2, BigDecimal.ROUND_HALF_DOWN);
					}

					if (per != null && per != new BigDecimal(0))
					{
						per = per.setScale(2, BigDecimal.ROUND_HALF_DOWN);
					}

					if (pOFor == 1)
					{

						// Data for allotment

						receipt.append("<tr >").append("<td style=\"border-bottom: 1px solid #FFFFFF; border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
								.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\" >" + srNo + "</td>")
								.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
								.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + itemName + "</td>")
								.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
								.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + itemUOM + "</td>")
								.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
								.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\" align=\"right\">" + rate + "</td>")
								.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
								.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\" align=\"right\">" + quantity + "</td>").append("").append("</tr>");

					}
					else if (pOFor == 4)
					{

						// PO Receive from Master Location

						receipt.append("<tr >").append("<td style=\"border-bottom: 1px solid #FFFFFF; border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
								.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\" >" + srNo + "</td>")
								.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
								.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + itemName + "</td>")
								.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
								.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + itemUOM + "</td>")
								.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
								.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\" align=\"right\">" + quantity + "</td>").append("").append("</tr>");

					}
					else
					{
						// Data for po created and
						// Data for po cancelled

						receipt.append("<tr >").append("<td style=\"border-bottom: 1px solid #FFFFFF; border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
								.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\" >" + srNo + "</td>")
								.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
								.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + itemName + "</td>")
								.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
								.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + itemUOM + "</td>")
								.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
								.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\" align=\"right\">" + quantity + "</td>")
								.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
								.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\" align=\"right\">" + rate + "</td>")
								.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
								.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\" align=\"right\">" + amount + "</td>").append("").append("</tr>");
					}

				}
			}
			else
			{

				if (pOFor == 1)
				{
					// quantity for allotment
					receipt.append("<tr >").append("<td style=\"border-bottom: 1px solid #FFFFFF; border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
							.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\" >" + "</td>")
							.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
							.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + "No Item Found" + "</td>")
							.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
							.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + "</td>")
							.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
							.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + "</td>")
							.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
							.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + "</td>")
							// .append("<td style=\"border-bottom: 1px solid
							// #FFFFFF;border-left: 1px solid #dddddd;
							// border-right: 1px solid #dddddd;")
							// .append("padding-top: 3px; padding-bottom: 3px;
							// padding-right: 8px; padding-left: 8px;\">"
							// + "</td>")
							.append("").append("</tr>");
				}
				else if (pOFor == 4)
				{
					// PO Receive from Master Location
					receipt.append("<tr >").append("<td style=\"border-bottom: 1px solid #FFFFFF; border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
							.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\" >" + "</td>")
							.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
							.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + "No Item Found" + "</td>")
							.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
							.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + "</td>")
							.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
							.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + "</td>").append("").append("</tr>");
				}
				else
				{
					// quantity cancel po and po
					receipt.append("<tr >").append("<td style=\"border-bottom: 1px solid #FFFFFF; border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
							.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\" >" + "</td>")
							.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
							.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + "No Item Found" + "</td>")
							.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
							.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + "</td>")
							.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
							.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + "</td>")
							.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
							.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + "</td>")
							.append("<td style=\"border-bottom: 1px solid #FFFFFF;border-left: 1px solid #dddddd; border-right: 1px solid #dddddd;")
							.append("padding-top: 3px; padding-bottom: 3px; padding-right: 8px; padding-left: 8px;\">" + "</td>").append("").append("</tr>");
				}

			}

			if (totalAmount != null && totalAmount != new BigDecimal(0))
			{
				totalAmount = totalAmount.setScale(2, BigDecimal.ROUND_HALF_DOWN);
			}

			if (totalQuantity != null && totalQuantity != new BigDecimal(0))
			{
				totalQuantity = totalQuantity.setScale(2, BigDecimal.ROUND_HALF_DOWN);
			}
			if (totalRate != null && totalRate != new BigDecimal(0))
			{
				totalRate = totalRate.setScale(2, BigDecimal.ROUND_HALF_DOWN);
			}
			if (totalPer != null && totalPer != new BigDecimal(0))
			{
				totalPer = totalPer.setScale(2, BigDecimal.ROUND_HALF_DOWN);
			}

			if (pOFor == 1)
			{
				// quantity for allotment
				receipt.append("").append("<tr >")
						.append("<td style=\"border-top: 2px solid #dddddd;border-bottom: 2px solid #dddddd; border-left: 2px solid #dddddd; border-right: 2px solid #dddddd;")
						.append("padding: 8px;\" >" + "</td>")
						.append("<td style=\"border-top: 2px solid #dddddd;border-bottom: 2px solid #dddddd;border-left: 2px solid #dddddd; border-right: 2px solid #dddddd;")
						.append("padding: 8px;\"  align=\"right\">Total</td>")
						.append("<td style=\"border-top: 2px solid #dddddd;border-bottom: 2px solid #dddddd;border-left: 2px solid #dddddd; border-right: 2px solid #dddddd;")
						.append("padding: 8px;\" align=\"right\"></td>")
						.append("<td style=\"border-top: 2px solid #dddddd;border-bottom: 2px solid #dddddd;border-left: 2px solid #dddddd; border-right: 2px solid #dddddd;")
						.append("padding: 8px;\" align=\"right\">" + totalRate + "</td>")
						.append("<td style=\"border-top: 2px solid #dddddd;border-bottom: 2px solid #dddddd;border-left: 2px solid #dddddd; border-right: 2px solid #dddddd;")
						.append("padding: 8px;\" align=\"right\">" + totalQuantity + "</td>").append("").append("</tr>");

			}
			else if (pOFor == 4)
			{
				// quantity for allotment
				receipt.append("").append("<tr >")
						.append("<td style=\"border-top: 2px solid #dddddd;border-bottom: 2px solid #dddddd; border-left: 2px solid #dddddd; border-right: 2px solid #dddddd;")
						.append("padding: 8px;\" >" + "</td>")
						.append("<td style=\"border-top: 2px solid #dddddd;border-bottom: 2px solid #dddddd;border-left: 2px solid #dddddd; border-right: 2px solid #dddddd;")
						.append("padding: 8px;\"  align=\"right\">Total</td>")
						.append("<td style=\"border-top: 2px solid #dddddd;border-bottom: 2px solid #dddddd;border-left: 2px solid #dddddd; border-right: 2px solid #dddddd;")
						.append("padding: 8px;\" align=\"right\"></td>")
						.append("<td style=\"border-top: 2px solid #dddddd;border-bottom: 2px solid #dddddd;border-left: 2px solid #dddddd; border-right: 2px solid #dddddd;")
						.append("padding: 8px;\" align=\"right\">" + totalQuantity + "</td>").append("").append("</tr>");

			}
			else
			{
				// quantity cancel po and po
				receipt.append("").append("<tr >")
						.append("<td style=\"border-top: 2px solid #dddddd;border-bottom: 2px solid #dddddd; border-left: 2px solid #dddddd; border-right: 2px solid #dddddd;")
						.append("padding: 8px;\" >" + "</td>")
						.append("<td style=\"border-top: 2px solid #dddddd;border-bottom: 2px solid #dddddd;border-left: 2px solid #dddddd; border-right: 2px solid #dddddd;")
						.append("padding: 8px;\"  align=\"right\">Total</td>")
						.append("<td style=\"border-top: 2px solid #dddddd;border-bottom: 2px solid #dddddd;border-left: 2px solid #dddddd; border-right: 2px solid #dddddd;")
						.append("padding: 8px;\" align=\"right\"></td>")
						.append("<td style=\"border-top: 2px solid #dddddd;border-bottom: 2px solid #dddddd;border-left: 2px solid #dddddd; border-right: 2px solid #dddddd;")
						.append("padding: 8px;\" align=\"right\">" + totalQuantity + "</td>")
						.append("<td style=\"border-top: 2px solid #dddddd;border-bottom: 2px solid #dddddd;border-left: 2px solid #dddddd; border-right: 2px solid #dddddd;")
						.append("padding: 8px;\" align=\"right\">" + totalRate + "</td>")
						.append("<td style=\"border-top: 2px solid #dddddd;border-bottom: 2px solid #dddddd;border-left: 2px solid #dddddd; border-right: 2px solid #dddddd;")
						.append("padding: 8px;\" align=\"right\">" + totalAmount + "</td>").append("").append("</tr>");
			}

			receipt.append("</table>").append("").append("</body>").append("</html>");

		}

		return receipt;
	}

	/**
	 * Gets the request order by id and location id.
	 *
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the request order by id and location id
	 */
	public RequestOrder getRequestOrderByIdAndLocationId(EntityManager em, String id)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RequestOrder> criteria = builder.createQuery(RequestOrder.class);
		Root<RequestOrder> r = criteria.from(RequestOrder.class);
		TypedQuery<RequestOrder> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(RequestOrder_.id), id)));
		RequestOrder requestOrderList = query.getSingleResult();
		return requestOrderList;
	}

	/**
	 * Gets the request order detail items by id and location id.
	 *
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the request order detail items by id and location id
	 */
	public List<RequestOrderDetailItems> getRequestOrderDetailItemsByIdAndLocationId(EntityManager em, String id)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<RequestOrderDetailItems> criteria = builder.createQuery(RequestOrderDetailItems.class);
		Root<RequestOrderDetailItems> r = criteria.from(RequestOrderDetailItems.class);
		TypedQuery<RequestOrderDetailItems> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(RequestOrderDetailItems_.requestId), id)));
		List<RequestOrderDetailItems> requestOrderList = query.getResultList();
		return requestOrderList;
	}

	/**
	 * Gets the request order detail items by id and location id and status.
	 *
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @param status
	 *            the status
	 * @return the request order detail items by id and location id and status
	 */
	public List<RequestOrderDetailItems> getRequestOrderDetailItemsByIdAndLocationIdAndStatus(EntityManager em, int id, String status)
	{

		String queryString = "select rodi from RequestOrderDetailItems rodi where rodi.requestId = ? and rodi.statusId in (" + status + ")";
		TypedQuery<RequestOrderDetailItems> query2 = em.createQuery(queryString, RequestOrderDetailItems.class).setParameter(1, id);
		return query2.getResultList();
	}

	/**
	 * Gets the item by id.
	 *
	 * @param em
	 *            the em
	 * @param itemId
	 *            the item id
	 * @return the item by id
	 */
	Item getItemById(EntityManager em, String itemId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Item> criteria = builder.createQuery(Item.class);
		Root<Item> r = criteria.from(Item.class);
		TypedQuery<Item> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Item_.id), itemId)));
		Item item = query.getSingleResult();

		String queryString = "select l from Location l where l.id in   (select p.locationsId from Item p where p.globalItemId=? and p.status !='D') ";
		TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, item.getId());
		List<Location> resultSet = query2.getResultList();
		item.setLocationList(resultSet);

		ItemRelationsHelper itemRelationsHelper = new ItemRelationsHelper();
		itemRelationsHelper.setShouldEliminateDStatus(true);

		item.setItemsToItemsAttributes(itemRelationsHelper.getItemsToItemsAttribute(item.getId(), em));
		item.setItemsToDiscounts(itemRelationsHelper.getItemToDiscounts(item.getId(), em));
		item.setItemsToItemsAttributesAttributeTypes(itemRelationsHelper.getItemsToItemsAttributeType(item.getId(), em));
		item.setItemsToItemsChars(itemRelationsHelper.getItemsToItemsChar(item.getId(), em));
		item.setItemsToPrinters(itemRelationsHelper.getItemToPrinter(item.getId(), em));
		item.setCategoryItems(itemRelationsHelper.getCategoryItem(item.getId(), em));

		ItemToSupplier itemToSupliers = itemRelationsHelper.getItemToSupplier(item.getId(), em);
		if (itemToSupliers != null)
		{
			item.setItemToSuppliers(itemToSupliers);
		}

		return item;

	}

	/**
	 * Creates the request order invoice body string.
	 *
	 * @return the string builder
	 */
	public StringBuilder createRequestOrderInvoiceBodyString()
	{

		StringBuilder receipt = new StringBuilder("FYI");

		return receipt;
	}

	/**
	 * Creates the request order invoice footer string.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return the string builder
	 */
	public StringBuilder createRequestOrderInvoiceFooterString(EntityManager em, String locationId)
	{

		String note = "";

		Location foundLocation = null;
		// todo shlok need
		// remove unused
		Address localAddress = null;
		if (locationId != null)
		{
			String queryString = "select l from Location l where " + "l.id ='" + locationId + "' and (l.locationsId = '0' or l.locationsId is null) and l.locationsTypeId = '1'";
			TypedQuery<Location> query = em.createQuery(queryString, Location.class);
			foundLocation = query.getSingleResult();
			localAddress = foundLocation.getAddress();
		}

		StringBuilder receipt = new StringBuilder();
		receipt.append("<table width=\"100%\"  style=\"font-family: arial, sans-serif;\">")
				.append("<tr>")
				.append("<td align=\"left|top\" width=\"70%\" style=\"border-top: 2px solid #dddddd; border-bottom: 1px solid #dddddd; border-top: 2px solid #dddddd; border-left: 1px solid #dddddd; \">")
				.append("<u>Note: </u>").append("<br/>" + note).append("</td>")
				.append("<td width=\"30%\" style=\"border-top: 2px solid #dddddd; border-right: 1px solid #dddddd; border-bottom: 1px solid #dddddd; padding: 8px;\" align=\"right\" >")
				.append("<b>for " + foundLocation.getName() + " </b>").append("<br/>").append("<br/>").append("<br/> Authorised Signatory").append("</td>").append("</tr></table>");

		return receipt;
	}

	/**
	 * Gets the order detail status by name and location id.
	 *
	 * @param em
	 *            the em
	 * @param statusName
	 *            the status name
	 * @param locationsId
	 *            the locations id
	 * @return the order detail status by name and location id
	 */
	public OrderDetailStatus getOrderDetailStatusByNameAndLocationId(EntityManager em, String statusName, String locationsId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderDetailStatus> criteria = builder.createQuery(OrderDetailStatus.class);
		Root<OrderDetailStatus> r = criteria.from(OrderDetailStatus.class);
		TypedQuery<OrderDetailStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderDetailStatus_.name), statusName),
				builder.equal(r.get(OrderDetailStatus_.locationsId), locationsId)));
		return query.getSingleResult();
	}

	/**
	 * Send EOD settledment mail string.
	 *
	 * @param em
	 *            the em
	 * @param httpRequest
	 *            the http request
	 * @param headers
	 *            the headers
	 * @param location
	 *            the location
	 * @param userId
	 *            the user id
	 * @param activeBatchDetail
	 *            the active batch detail
	 * @return the string builder
	 */
	public StringBuilder sendEODSettledmentMailString(EntityManager em, HttpServletRequest httpRequest, List<OrderHeader> headers, Location location, String userId, BatchDetail activeBatchDetail)
	{
		StringBuilder receipt = null;
		try
		{
			String cashierName = null;
			User user = (User) new CommonMethods().getObjectById("User", em,User.class, userId);
			
			if (user != null)
			{

				if (user.getFirstName() != null)
				{
					cashierName = user.getFirstName().charAt(0) + "";
				}

				if (user.getLastName() != null)
				{
					cashierName = cashierName + user.getLastName().charAt(0) + "";
				}

				cashierName = cashierName.toUpperCase();

			}

			Address localAddress = location.getAddress();

			TimezoneTime timezoneTime = new TimezoneTime();
			String datetime = "";
			String currentDateTime[] = timezoneTime.getCurrentTimeofLocation(location.getId(), em);
			if (currentDateTime != null && currentDateTime.length > 0)
			{
				SimpleDateFormat toformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat fromFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");

				try
				{
					java.util.Date date = toformatter.parse(currentDateTime[2]);
					datetime = fromFormatter.format(date);

				}
				catch (ParseException e)
				{
					logger.severe("Unable to parse date", currentDateTime[2], "while generating pdf receipt");
				}
			}

			String address1 = "";
			String address2 = "";
			String city = "";
			String state = "";
			String phone = "";
			if (localAddress != null)
			{
				address1 = localAddress.getAddress1();
				address2 = localAddress.getAddress2();
				city = localAddress.getCity();
				state = localAddress.getState();
				phone = localAddress.getPhone();
			}

			SettleCreditCardBatchPacket settleCreditCardBatchPacket = calculateBatchSettlementData(httpRequest, em, location.getId(), headers, activeBatchDetail, currentDateTime[2].split(" ")[0]);
			if (settleCreditCardBatchPacket != null)
			{

				
				List<RegisterInfo> registerInfos = getCashRegister(location.getId(), em, true, activeBatchDetail);
				LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, location.getId());
				
				StringBuilder builder = printBatchSettleReceipt(settleCreditCardBatchPacket, registerInfos,locationSetting,location.getBusinessId(),activeBatchDetail,em);

				// HTML code

				receipt = new StringBuilder().append("<html>                                                              ")
						.append("<body>                                                              ").append("<center>                                                            ");
				receipt.append("" + location.getName() + "<br>");
				if (address1 != null)
					receipt.append("" + address1 + "<br>                                             ");
				if (address2 != null && address2.length() > 0)
					receipt.append("" + address2 + "<br>                                              ");
				if (city != null)
					receipt.append("" + city + "<br>                                              ");
				if (state != null)
					receipt.append("" + state + "<br>                                              ");

				if (phone != null)
					receipt.append("" + phone + "<br>                                                    ");

				receipt.append("<a href=\"" + location.getWebsite() + "\">" + location.getWebsite() + "</a>").append("<div id=\"date\">                                                     ")
						.append(" " + datetime + "<br>");
				if (cashierName != null)
					receipt.append("Cashier: " + cashierName + "<br> <br> ");

				receipt.append("<b> End Of Day Summary</b><br> <br>")

				.append("</div>                                                              ");
				receipt.append("</center>                                                           ").append("<div align=\"center\">                                                     ")
						.append("" + builder).append("<div align=\"center\"><br><br><b>Powered By Nirvana XP</b>	                                ").append("</div>")
						.append(" <br></div></body>                                                          ").append("</html>");

			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}

		return receipt;
	}

	public StringBuilder sendEODMailForTipSettlementFromEOD(EntityManager em, HttpServletRequest httpRequest, List<String> userList, String batchId, String locationId)
	{
		StringBuilder receipt = null;
		try
		{

			BatchDetail batchDetail = em.find(BatchDetail.class, batchId);
			Date newDate = new Date(new TimezoneTime().getGMTTimeInMilis());
			TimezoneTime time = new TimezoneTime();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat newDateFormat = new SimpleDateFormat("MM/dd/yyyy");

			String batchStartDate = time.getDateTimeFromGMTToLocation(em, dateFormat.format(batchDetail.getStartTime()), locationId);
			Date batchStart = dateFormat.parse(batchStartDate);

			// increase date by 1
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateFormat.parse(batchStartDate));
			cal.add(Calendar.DATE, 1);
			String convertedDate = newDateFormat.format(cal.getTime());

			Date sundayDate = time.getSunday(batchStart);

			String weekEndDate = newDateFormat.format(sundayDate);

			receipt = new StringBuilder().append("<html>").append("<body>");
			receipt.append(" List of Partner(s) currently clocked in for batch Id : " + batchId + " (" + newDateFormat.format(batchStart) + ")  <br> <br>");

			for (int i = 0; i < userList.size(); i++)
			{
				receipt.append((i + 1) + ". " + userList.get(i));
				receipt.append(" <br>                                             ");
			}
			receipt.append(" <br>                                             ");

			receipt.append("<b>Please review clock in/out time for all tipped Partners and make any necessary corrections by 8am, (" + convertedDate
					+ ").<br/>  Incorrect clock in/out times will affect Payroll.</b><br> <br>");
			receipt.append("<b>Payroll will not be processed until all tipped Partners are clocked out for the pay week ending " + weekEndDate + ".</b><br> <br>");

			receipt.append("</div>");
			receipt.append("</body>").append("</html>");

		}
		catch (Exception e)
		{
			logger.severe(e);
		}

		return receipt;
	}

	public static StringBuilder generateEmailContentForEmployeesStillClockedIn(List<String> userList, String batchStartDate, String locationId) throws ParseException
	{		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = dateFormat.parse(batchStartDate);
		DateFormat newDateFormat = new SimpleDateFormat("MM/dd/yyyy");

		// increase date by 6
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateFormat.parse(batchStartDate));

		Date sundayDate = TimezoneTime.getSunday(cal.getTime());
		String convertedDate = newDateFormat.format(sundayDate);
			//(sundayDate.getMonth() + 1) + "/" + sundayDate.getDate() + "/" + (sundayDate.getYear() + 1900);

		StringBuilder receipt = new StringBuilder().append("<html>").append("<body>");
		receipt.append(" List of Partner(s) currently clocked in for this pay week : (" + newDateFormat.format(startDate) + ") - (" + convertedDate + ")  <br> <br>");

		for (int i = 0; i < userList.size(); i++)
		{
			receipt.append((i + 1) + ". " + userList.get(i));
			receipt.append(" <br>                                             ");
		}
		receipt.append(" <br>                                             ");

		receipt.append("<b>Please review clock in/out time for all tipped Partners and make any necessary corrections by 8am, (" + convertedDate
				+ ").<br/>  Incorrect clock in/out times will affect Payroll.</b><br> <br>");
		receipt.append("<b>Payroll will not be processed until all tipped Partners are clocked out for the pay week ending " + convertedDate + ".</b><br> <br>");

		receipt.append("</div>");
		receipt.append("</body>").append("</html>");

		return receipt;
	}

	/**
	 * Inits the.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param orderHeaderList
	 *            the order header list
	 * @param activeBatchDetail
	 *            the active batch detail
	 * @param currentDateOfLocation
	 *            the current date of location
	 * @return the settle credit card batch packet
	 */
	private SettleCreditCardBatchPacket calculateBatchSettlementData(HttpServletRequest httpRequest, EntityManager em, String locationId, List<OrderHeader> orderHeaderList,
			BatchDetail activeBatchDetail, String currentDateOfLocation)
	{
		// todo shlok need
		// modularise method
		try
		{
			BigDecimal calculatedAmountPaid = new BigDecimal(0);
			BigDecimal calculatedTotal = new BigDecimal(0);
			BigDecimal calculatedBalDue = new BigDecimal(0);
			BigDecimal calculatedDiscount = new BigDecimal(0);
			BigDecimal calculatedSubTotal = new BigDecimal(0);
			BigDecimal calculatedNonChargeable = new BigDecimal(0);

			BigDecimal calculatedCashTotal = new BigDecimal(0);
			BigDecimal calculatedTotalTaxOnCash = new BigDecimal(0);
			BigDecimal calculatedTotalGraduityOnCash = new BigDecimal(0);
			BigDecimal calculatedTotalCashGrossSales = new BigDecimal(0);
			BigDecimal calculatedCashTip = new BigDecimal(0);
			BigDecimal calculatedCashWithTipNTaxOnCash = new BigDecimal(0);
			
			BigDecimal calculatedChequeTotal = new BigDecimal(0);
			BigDecimal calculatedTotalTaxOnCheque = new BigDecimal(0);
			BigDecimal calculatedTotalGraduityOnCheque = new BigDecimal(0);
			BigDecimal calculatedTotalChequeGrossSales = new BigDecimal(0);
			BigDecimal calculatedChequeTip = new BigDecimal(0);
			BigDecimal calculatedChequeWithTipNTaxOnCheque = new BigDecimal(0);
			
			
			
			BigDecimal calculatedTotalTaxOnCard = new BigDecimal(0);
			BigDecimal calculatedTotalGraduityOnCard = new BigDecimal(0);
			BigDecimal calculatedTotalCardGrossSales = new BigDecimal(0);

			BigDecimal calculatedTotalTaxOnCreditTerm = new BigDecimal(0);
			BigDecimal calculatedTotalGraduityOnCreditTerm = new BigDecimal(0);
			BigDecimal calculatedTotalCreditTermGross = new BigDecimal(0);
			BigDecimal calculatedCreditTermTip = new BigDecimal(0);
			BigDecimal calculatedCreditTermTotal = new BigDecimal(0);

			

			BigDecimal calculatedCardTip = new BigDecimal(0);
			BigDecimal calculatedCreditTotal = new BigDecimal(0);
			BigDecimal calculatedGraduity = new BigDecimal(0);
			BigDecimal calculatedTax = new BigDecimal(0);
			BigDecimal calculatedCardTotal = new BigDecimal(0);
			BigDecimal calculatedCreditTerm = new BigDecimal(0);
			BigDecimal guestCount = new BigDecimal(0);
			BigDecimal orderCount = new BigDecimal(0);
			BigDecimal calculatedDeliveryCharge = new BigDecimal(0);
			BigDecimal calculatedDeliveryTax = new BigDecimal(0);
			BigDecimal serviceCharge = new BigDecimal(0);

			for (OrderHeader order : orderHeaderList)
			{
				
				if (order.getOrderPaymentDetails() != null && order.getOrderPaymentDetails().size() > 0)
				{
					if (activeBatchDetail != null && order.getNirvanaXpBatchNumber().equals(activeBatchDetail.getId()))
					{
						calculatedTotal = calculatedTotal.add(order.getTotal());
						if(order.getDiscountsName()!=null && (order.getDiscountsName().equals("NC1") || order.getDiscountsName().equals("NC2") || order.getDiscountsName().equals("NC3") || order.getDiscountsName().equals("NC4"))){	
							calculatedNonChargeable=calculatedNonChargeable.add(order.getPriceDiscount());
							
						}else {
							calculatedDiscount = (calculatedDiscount.add(order.getPriceDiscount()).add(order.getPriceDiscountItemLevel()));
						}
						calculatedSubTotal = calculatedSubTotal.add(order.getSubTotal());

						if (order.getDeliveryCharges() != null)
						{
							calculatedDeliveryCharge = calculatedDeliveryCharge.add(order.getDeliveryCharges());

						}
						if (order.getDeliveryTax() != null)
						{
							calculatedDeliveryTax = calculatedDeliveryTax.add(order.getDeliveryTax());

						}
						if (order.getServiceCharges() != null)
						{
							serviceCharge = serviceCharge.add(order.getServiceCharges());

						}

						if (order.getTotalTax() != null)
						{
							calculatedTax = calculatedTax.add(order.getTotalTax());
						}

					}

				}
				else
				{
					if (activeBatchDetail != null && order.getNirvanaXpBatchNumber().equals(activeBatchDetail.getId()))
					{

						calculatedTotal = calculatedTotal.add(order.getTotal());
						if(order.getDiscountsName()!=null && (order.getDiscountsName().equals("NC1") || order.getDiscountsName().equals("NC2") || order.getDiscountsName().equals("NC3") || order.getDiscountsName().equals("NC4"))){
								
							calculatedNonChargeable=calculatedNonChargeable.add(order.getPriceDiscount());
						}else {
							    calculatedDiscount = (calculatedDiscount.add(order.getPriceDiscount()).add(order.getPriceDiscountItemLevel()));
						}
						calculatedSubTotal = calculatedSubTotal.add(order.getSubTotal());

						if (order.getDeliveryCharges() != null)
						{
							calculatedDeliveryCharge = calculatedDeliveryCharge.add(order.getDeliveryCharges());
						}
						if (order.getDeliveryTax() != null)
						{
							calculatedDeliveryTax = calculatedDeliveryTax.add(order.getDeliveryTax());

						}

						if (order.getServiceCharges() != null)
						{
							serviceCharge = serviceCharge.add(order.getServiceCharges());

						}

						calculatedTax = calculatedTax.add(order.getTotalTax());
					}

				}

				calculatedCashTip = calculatedCashTip.add(calculateTotalCashTip(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail));
				calculatedCardTip = calculatedCardTip.add(calculateTotalCardTip(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail));

				calculatedCashTotal = calculatedCashTotal.add(calculateCashAmountTotal(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail, true));
				
				// Cheque Calculation
				calculatedChequeTotal = calculatedChequeTotal.add(calculateChequeAmountTotal(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail, true));
				calculatedTotalChequeGrossSales = calculatedChequeTotal;
				calculatedTotalTaxOnCheque = calculatedTotalTaxOnCheque.add(calculateTotalTaxOnChequeSale(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail));
				calculatedTotalGraduityOnCheque = calculatedTotalGraduityOnCheque.add(calculateTotalGraduityOnChequeSale(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail));
				calculatedChequeTip = calculatedChequeTip.add(calculateTotalChequeTip(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail));
				
				
				
				calculatedTotalTaxOnCash = calculatedTotalTaxOnCash.add(calculateTotalTaxOnCashSale(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail));
				calculatedTotalGraduityOnCash = calculatedTotalGraduityOnCash.add(calculateTotalGraduityOnCashSale(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail));
				calculatedTotalCashGrossSales = calculatedCashTotal;
				

				calculatedTotalTaxOnCard = calculatedTotalTaxOnCard.add(calculateTotalTaxOnCardSale(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail));
				calculatedTotalGraduityOnCard = calculatedTotalGraduityOnCard.add(calculateTotalGraduityOnCardSale(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail));

				// Credit Term Cal
				calculatedCreditTermTip = calculatedCreditTermTip.add(calculateTotalCreditTermTip(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail));
				calculatedCreditTerm = calculatedCreditTerm.add(calculateCreditTerm(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail, true));
				calculatedTotalTaxOnCreditTerm = calculatedTotalTaxOnCreditTerm.add(calculateTotalTaxOnCreditTerm(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail));
				calculatedTotalGraduityOnCreditTerm = calculatedTotalGraduityOnCreditTerm.add(calculateTotalGraduityOnCreditTerm(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail));
				calculatedTotalCreditTermGross = calculatedCreditTerm;
				calculatedCreditTermTotal = calculatedCreditTerm.add(calculatedCreditTermTip);

				calculatedCreditTotal = calculatedCreditTotal.add(calculateCardAmountTotal(em, locationId, order.getOrderPaymentDetails(), activeBatchDetail, true));

				calculatedTotalCardGrossSales = calculatedCreditTotal;

				calculatedCashWithTipNTaxOnCash = calculatedCashTip.add(calculatedCashTotal);
				
				calculatedChequeWithTipNTaxOnCheque = calculatedChequeTip.add(calculatedChequeTotal);
				

				calculatedCardTotal = calculatedCreditTotal.add(calculatedCardTip);

				if (activeBatchDetail != null && order.getNirvanaXpBatchNumber().equals(activeBatchDetail.getId()))
				{
					if (order.getPriceGratuity() != null)
					{
						calculatedGraduity = calculatedGraduity.add(order.getPriceGratuity());
					}

					guestCount = guestCount.add(BigDecimal.valueOf(Double.parseDouble(order.getPointOfServiceCount() + "")));
					orderCount = orderCount.add(BigDecimal.valueOf(Double.parseDouble(Integer.parseInt(1 + "") + "")));
					calculatedBalDue = calculatedBalDue.add(order.getBalanceDue());
				}

				calculatedAmountPaid = calculatedCashWithTipNTaxOnCash.add(calculatedCardTotal.add(calculatedCreditTermTotal))
						.add(calculatedChequeWithTipNTaxOnCheque);
			}
			// End of order payment details for loop

			BigDecimal avgTotalPerGuest = new BigDecimal(0);
			if (guestCount.doubleValue() != 0)
			{
				BigDecimal temp = calculatedSubTotal.subtract(calculatedDiscount).subtract(calculatedNonChargeable);
				avgTotalPerGuest = temp.setScale(2, RoundingMode.CEILING).divide(guestCount, BigDecimal.ROUND_HALF_UP);
			}

			BigDecimal avgGuestPerOrder = new BigDecimal(0);
			if (orderCount.doubleValue() != 0)
			{
				avgGuestPerOrder = guestCount.divide(orderCount, RoundingMode.HALF_UP);
			}

			SettleCreditCardBatchPacket settleCreditCardBatchPacket = new SettleCreditCardBatchPacket();

			BigDecimal temp = (calculatedCreditTerm.subtract(calculatedTotalTaxOnCreditTerm.add(calculatedTotalGraduityOnCreditTerm)).setScale(2, BigDecimal.ROUND_HALF_DOWN));
			settleCreditCardBatchPacket.setCreditTerm("" + temp);
			settleCreditCardBatchPacket.setBalanceDue("" + (calculatedBalDue.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setTotalCard("" + (calculatedCardTotal.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setTotalCheque("" + (calculatedChequeWithTipNTaxOnCheque.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setTotalCash("" + (calculatedCashWithTipNTaxOnCash.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setAverageGuestPerOrder("" + (avgGuestPerOrder.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setOrderCount("" + (orderCount.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setAverageAmountPerGuest("" + (avgTotalPerGuest.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setGuestCount("" + (guestCount.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setCardTips("" + (calculatedCardTip.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setCashTips("" + (calculatedCashTip.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setChequeTips("" + (calculatedChequeTip.setScale(2, BigDecimal.ROUND_HALF_DOWN)));

			temp = (calculatedCreditTotal.subtract(calculatedTotalTaxOnCard.add(calculatedTotalGraduityOnCard)).setScale(2, BigDecimal.ROUND_HALF_DOWN));
			settleCreditCardBatchPacket.setCardSales("" + temp);

			temp = calculatedCashTotal.subtract(calculatedTotalTaxOnCash.add(calculatedTotalGraduityOnCash)).setScale(2, BigDecimal.ROUND_HALF_DOWN);
			settleCreditCardBatchPacket.setCashSales("" + temp);
			settleCreditCardBatchPacket.setAutoGratuity("" + (calculatedGraduity.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setTax("" + (calculatedTax.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setTotalSales2("" + (calculatedTotal.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			
			
			temp = calculatedSubTotal.subtract(calculatedDiscount.setScale(2, BigDecimal.ROUND_HALF_DOWN)).subtract((calculatedNonChargeable.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setTotalSales1("" + temp);
			
			
			temp = calculatedChequeTotal.subtract(calculatedTotalTaxOnCheque.add(calculatedTotalGraduityOnCheque)).setScale(2, BigDecimal.ROUND_HALF_DOWN);
			settleCreditCardBatchPacket.setChequeSales("" + temp);
			
			
			settleCreditCardBatchPacket.setTotalPaid("" + (calculatedAmountPaid.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setSales("" + (calculatedSubTotal.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setDiscounts("" + (calculatedDiscount.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setNonChargeable("" + (calculatedNonChargeable.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			
			settleCreditCardBatchPacket.setGiftCertificatesRedeemed("0.00");

			settleCreditCardBatchPacket.setGraduityOnCashSale("" + (calculatedTotalGraduityOnCash.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setGraduityOnChequeSale("" + (calculatedTotalGraduityOnCheque.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setTaxOnCashSale("" + (calculatedTotalTaxOnCash.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setTaxOnChequeSale("" + (calculatedTotalTaxOnCheque.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setCashGrossSales("" + (calculatedTotalCashGrossSales.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setChequeGrossSales("" + (calculatedTotalChequeGrossSales.setScale(2, BigDecimal.ROUND_HALF_DOWN)));

			settleCreditCardBatchPacket.setGraduityOnCardSales("" + (calculatedTotalGraduityOnCard.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setTaxOnCardSales("" + (calculatedTotalTaxOnCard.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setCardGrossSales("" + (calculatedTotalCardGrossSales.setScale(2, BigDecimal.ROUND_HALF_DOWN)));

			settleCreditCardBatchPacket.setGraduityOnCreditTerm("" + (calculatedTotalGraduityOnCreditTerm.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setTaxOnCreditTerm("" + (calculatedTotalTaxOnCreditTerm.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setCreditTermGross("" + (calculatedTotalCreditTermGross.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setCreditTermTips("" + (calculatedCreditTermTip.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setTotalCredit("" + (calculatedCreditTermTotal.setScale(2, BigDecimal.ROUND_HALF_DOWN)));

			settleCreditCardBatchPacket.setTotalDeliveryCharges("" + (calculatedDeliveryCharge.setScale(2, BigDecimal.ROUND_HALF_DOWN)));

			settleCreditCardBatchPacket.setTotalDeliveryTax("" + (calculatedDeliveryTax.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
			settleCreditCardBatchPacket.setTotalServiceCharge("" + (serviceCharge.setScale(2, BigDecimal.ROUND_HALF_DOWN)));

			settleCreditCardBatchPacket.setTotalPaidIn("0.00");
			settleCreditCardBatchPacket.setTotalPaidOut("0.00");
			settleCreditCardBatchPacket.setTotalCashInAllRegister("" + (calculatedCashWithTipNTaxOnCash.setScale(2, BigDecimal.ROUND_HALF_DOWN)));

			// Paid in Paid out calculation

			BigDecimal totalPaidIn1 = new BigDecimal(0);
			BigDecimal totalPaidOut1 = new BigDecimal(0);
			String totalPaidInDName = "";
			String serviceChargeName = "";

			String totalPaidOutDName = "";

			List<EmployeeOperationToCashRegister> employeeOperationToCashRegisterList = getAllEmployeeOperationToCashRegisterByLocationId(httpRequest, em, locationId, activeBatchDetail);
			int len = employeeOperationToCashRegisterList.size();
			EmployeeOperation employeeOperations1;
			for (int i = 0; i < len; i++)
			{
				employeeOperations1 = getActiveEmployeeOperationsById(em, locationId, employeeOperationToCashRegisterList.get(i).getEmployeeOperationId());

				if (employeeOperations1 != null && employeeOperations1.getOperationName().equals("Paid In"))
				{
					totalPaidIn1 = totalPaidIn1.add(new BigDecimal(employeeOperationToCashRegisterList.get(i).getAmount()));
				}
				else
				{
					totalPaidOut1 = totalPaidOut1.add(new BigDecimal(employeeOperationToCashRegisterList.get(i).getAmount()));
				}

			}

			EmployeeOperation employeeOperations = getEmployeeOperation(em, locationId, "Paid In");
			if (employeeOperations != null)
			{
				totalPaidInDName = employeeOperations.getOperationDisplayName();
			}
			else
			{
				totalPaidInDName = "Paid In";
			}

			employeeOperations = getEmployeeOperation(em, locationId, "Paid Out");

			if (employeeOperations != null)
			{
				totalPaidOutDName = employeeOperations.getOperationDisplayName();
			}
			else
			{
				totalPaidOutDName = "Paid Out";
			}

			SalesTax salesTax = getSalesTaxByNameAndLocationId(em, "Service Charge", locationId);
			if (salesTax != null)
			{
				serviceChargeName = salesTax.getDisplayName();
				settleCreditCardBatchPacket.setTotalServiceChargeConstant(serviceChargeName);
			}

			BigDecimal totalCashInAllRegister = calculatedCashWithTipNTaxOnCash.add(totalPaidIn1.subtract(totalPaidOut1));
			settleCreditCardBatchPacket.setTotalPaidInConstant("Total " + totalPaidInDName + ": ");
			settleCreditCardBatchPacket.setTotalPaidOutConstant("Total " + totalPaidOutDName + ": ");
			settleCreditCardBatchPacket.setTotalPaidIn((totalPaidIn1.setScale(2, BigDecimal.ROUND_HALF_DOWN)) + "");
			settleCreditCardBatchPacket.setTotalPaidOut((totalPaidOut1.setScale(2, BigDecimal.ROUND_HALF_DOWN)) + "");
			settleCreditCardBatchPacket.setTotalCashInAllRegister((totalCashInAllRegister.setScale(2, BigDecimal.ROUND_HALF_DOWN)) + "");

			SalesTax gratuity = getSalesTax(em, locationId, "Gratuity");
			if (gratuity != null)
			{
				settleCreditCardBatchPacket.autoGratuityConstant = gratuity.getDisplayName() + ":";
			}
			else
			{
				settleCreditCardBatchPacket.autoGratuityConstant = "Gratuity";
			}

			// Wallet calculation

			try
			{
				BigDecimal total = new BigDecimal(0.0);
				BigDecimal walletCash = new BigDecimal(0.0);
				BigDecimal manualCC = new BigDecimal(0.0);
				List<UsersToPaymentHistory> usersToPaymentHistories = getUsersToPaymentHistoryByLocationId(httpRequest, em, locationId, activeBatchDetail);
				if (usersToPaymentHistories != null && usersToPaymentHistories.size() > 0)
				{
					for (UsersToPaymentHistory usersToPaymentHistory : usersToPaymentHistories)
					{

						PaymentType paymentType = getPaymentType(em, usersToPaymentHistory.getPaymentTypeId());

						if (paymentType.getName().equals("cash"))
						{
							total = total.subtract(usersToPaymentHistory.getAmountPaid());
							walletCash = walletCash.subtract(usersToPaymentHistory.getAmountPaid());
						}
						else if (paymentType.getName().equals("Manual Credit Card"))
						{
							total = total.subtract(usersToPaymentHistory.getAmountPaid());
							manualCC = manualCC.subtract(usersToPaymentHistory.getAmountPaid());

						}
					}

					totalCashInAllRegister = calculatedCashWithTipNTaxOnCash.add(totalPaidIn1.subtract(totalPaidOut1)).add(walletCash);

					settleCreditCardBatchPacket.setTotalWallet("" + (total.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
					settleCreditCardBatchPacket.setTotalWalletCash("" + (walletCash.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
					settleCreditCardBatchPacket.setTotalWalletManualCC("" + (manualCC.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
					settleCreditCardBatchPacket.setTotalCashInAllRegister("" + (totalCashInAllRegister.setScale(2, BigDecimal.ROUND_HALF_DOWN)));
					settleCreditCardBatchPacket.setTotalCC("" + ((calculatedCardTotal.add(manualCC)).setScale(2, BigDecimal.ROUND_HALF_DOWN)));
					
				}

			}
			catch (Exception e)
			{
				// todo shlok need
				// handel proper exception
				logger.severe(e);
			}

			return settleCreditCardBatchPacket;

		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);

		}

		return null;

	}

	/**
	 * Calculate total cash tip.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param orderList
	 *            the order list
	 * @param activeBatchDetail
	 *            the active batch detail
	 * @return the double
	 */
	public BigDecimal calculateTotalCashTip(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail activeBatchDetail)
	{
		try
		{
			BigDecimal tipAmount = new BigDecimal(0);
			if (orderList != null)
			{
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{
					if (orderPaymentDetail != null && activeBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(activeBatchDetail.getId()))
					{

						int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

						if (orderPaymentDetail.getIsRefunded() == 0 && transectionTypeId != getPaymentTransactionType(em, locationId, "Void").getId()
								&& transectionTypeId != getPaymentTransactionType(em, locationId, "Refund").getId() && getPaymentTransactionType(em, locationId, "Credit Refund") != null
								&& transectionTypeId != getPaymentTransactionType(em, locationId, "Credit Refund").getId())
						{

							if (transectionTypeId == getPaymentTransactionType(em, locationId, "Sale").getId()
									|| (getPaymentTransactionType(em, locationId, "Credit") != null && transectionTypeId == getPaymentTransactionType(em, locationId, "Credit").getId()))
							{
								tipAmount = tipAmount.add(orderPaymentDetail.getCashTipAmt());

							}
							else if ((getTransactionStatus(em, locationId, "CC Pre Capture") != null
									&& orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Pre Capture").getId() && orderPaymentDetail
									.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Force").getId()))
							{
								tipAmount = tipAmount.add(orderPaymentDetail.getCashTipAmt());
							}
							else if ((getTransactionStatus(em, locationId, "CC Settled") != null && orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId,
									"CC Settled").getId())
									&& (orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Capture").getId() || orderPaymentDetail
											.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "CaptureAll").getId()))
							{
								tipAmount = tipAmount.add(orderPaymentDetail.getCashTipAmt());
							}
							else if (((getTransactionStatus(em, locationId, "Tip Saved") != null && orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId,
									"Tip Saved").getId())
									|| (getTransactionStatus(em, locationId, "CC Auth") != null && orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Auth")
											.getId()) || (getTransactionStatus(em, locationId, "Manual CC Auth") != null && orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(
									em, locationId, "Manual CC Auth").getId()))
									&& orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Auth").getId())
							{
								tipAmount = tipAmount.add(orderPaymentDetail.getCashTipAmt());
							}
							else if (orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "discount").getId())
							{
								tipAmount = tipAmount.add(orderPaymentDetail.getCashTipAmt());
							}
						}
					}

				}
			}

			return tipAmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}
	
	public BigDecimal calculateTotalChequeTip(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail activeBatchDetail)
	{
		try
		{
			BigDecimal tipAmount = new BigDecimal(0);
			if (orderList != null)
			{
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{
					if (orderPaymentDetail != null && activeBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(activeBatchDetail.getId()))
					{
						
						PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();

						if (paymentMethod.getPaymentMethodTypeId() == null)
						{

							paymentMethod = (PaymentMethod) new CommonMethods().getObjectById("PaymentMethod", em,PaymentMethod.class, paymentMethod.getPaymentMethodTypeId());
						}

						if (paymentMethod != null)
						{
							int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

							if (transectionTypeId == getPaymentTransactionType(em, locationId, "Auth").getId() && 
									orderPaymentDetail.getIsRefunded() == 0
									&& paymentMethod.getPaymentMethodTypeId().equals(getPaymentMethodType(em, locationId, "cheque").getId()))
						{
							tipAmount = tipAmount.add(orderPaymentDetail.getChequeTip());
						}
						
						}
					}

				}
			}

			return tipAmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	/**
	 * Gets the payment transaction type.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the payment transaction type
	 */
	private PaymentTransactionType getPaymentTransactionType(EntityManager em, String locationId, String name)
	{

		try
		{
			String queryString = "select l from PaymentTransactionType l where   l.name='" + name + "'";
			TypedQuery<PaymentTransactionType> query = em.createQuery(queryString, PaymentTransactionType.class);
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new PaymentTransactionType();
	}

	/**
	 * Gets the payment method type.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the payment method type
	 */
	private PaymentMethodType getPaymentMethodType(EntityManager em, String locationId, String name)
	{

		try
		{
			String queryString = "select l from PaymentMethodType l where l.locationsId =? and l.name=? ";
			TypedQuery<PaymentMethodType> query = em.createQuery(queryString, PaymentMethodType.class).setParameter(1, locationId).setParameter(2, name);
			return query.getSingleResult();
		}catch (NoResultException e) {
			logger.severe("No PaymentMethodType found for locationId :- "+locationId+" and name :- "+name);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new PaymentMethodType();
	}

	/**
	 * Gets the payment method type by id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param id
	 *            the id
	 * @return the payment method type by id
	 */
	private PaymentMethodType getPaymentMethodTypeById(EntityManager em, String locationId, String id)
	{

		try
		{
			String queryString = "select l from PaymentMethodType l where l.locationsId ='" + locationId + "' and l.id='" + id+"'";
			TypedQuery<PaymentMethodType> query = em.createQuery(queryString, PaymentMethodType.class);
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new PaymentMethodType();
	}

	/**
	 * Gets the payment method.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param id
	 *            the id
	 * @return the payment method
	 */
	private PaymentMethod getPaymentMethod(EntityManager em, String locationId, String id)
	{

		try
		{
			PaymentMethod paymentMethod = (PaymentMethod) new CommonMethods().getObjectById("PaymentMethod", em,PaymentMethod.class, id);
			return paymentMethod;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new PaymentMethod();
	}

	/**
	 * Gets the transaction status.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the transaction status
	 */
	private TransactionStatus getTransactionStatus(EntityManager em, String locationId, String name)
	{

		try
		{
			String queryString = "select l from TransactionStatus l where l.name='" + name + "'";
			TypedQuery<TransactionStatus> query = em.createQuery(queryString, TransactionStatus.class);
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new TransactionStatus();
	}

	/**
	 * Gets the employee operation.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param operationName
	 *            the operation name
	 * @return the employee operation
	 */
	private EmployeeOperation getEmployeeOperation(EntityManager em, String locationId, String operationName)
	{

		String queryString = "select l from EmployeeOperation l where l.locationsId ='" + locationId + "' and l.operationName='" + operationName + "' and status NOT IN ('D', 'I')";
		TypedQuery<EmployeeOperation> query = em.createQuery(queryString, EmployeeOperation.class);
		return query.getSingleResult();

	}

	/**
	 * Gets the active employee operations by id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param id
	 *            the id
	 * @return the active employee operations by id
	 */
	private EmployeeOperation getActiveEmployeeOperationsById(EntityManager em, String locationId, String id)
	{

		String queryString = "select l from EmployeeOperation l where l.locationsId ='" + locationId + "' and l.id=" + id + " and status NOT IN ('D', 'I')";
		TypedQuery<EmployeeOperation> query = em.createQuery(queryString, EmployeeOperation.class);
		return query.getSingleResult();

	}

	/**
	 * Gets the payment type.
	 *
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the payment type
	 */
	private PaymentType getPaymentType(EntityManager em, int id)
	{

		String queryString = "select l from PaymentType l where l.id=" + id;
		TypedQuery<PaymentType> query = em.createQuery(queryString, PaymentType.class);
		return query.getSingleResult();

	}

	/**
	 * Gets the sales tax.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the sales tax
	 */
	private SalesTax getSalesTax(EntityManager em, String locationId, String name)
	{

		String queryString = "select l from SalesTax l where l.locationsId ='" + locationId + "' and l.taxName='" + name + "'";
		TypedQuery<SalesTax> query = em.createQuery(queryString, SalesTax.class);
		return query.getSingleResult();

	}

	/**
	 * Calculate total card tip.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param orderList
	 *            the order list
	 * @param activeBatchDetail
	 *            the active batch detail
	 * @return the double
	 */
	public BigDecimal calculateTotalCardTip(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail activeBatchDetail)
	{
		try
		{
			BigDecimal tipAmount = new BigDecimal(0);
			if (orderList != null)
			{
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{
					if (orderPaymentDetail != null && activeBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(activeBatchDetail.getId()))
					{
						
						PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();

						if (paymentMethod.getPaymentMethodTypeId() == null)
						{

							paymentMethod = (PaymentMethod) new CommonMethods().getObjectById("PaymentMethod", em,PaymentMethod.class, paymentMethod.getPaymentMethodTypeId());
						}

						String paymentMethodTypeId = null;
						if (paymentMethod != null)
						{
							paymentMethodTypeId = paymentMethod.getPaymentMethodTypeId();
						}
						
						
								
						
						if ((orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Auth").getId()
							|| orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "Manual CC Auth").getId() 
							|| orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "Tip Saved").getId()) 
								 
								&& orderPaymentDetail.getIsRefunded() != 1)
						{
							if(!paymentMethodTypeId.equals(getPaymentMethodType(em, locationId, "cheque").getId()))
							{
								tipAmount = tipAmount.add(orderPaymentDetail.getCreditcardTipAmt());	
							}
							
						}
						else if (orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Pre Capture").getId()
								&& orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Force").getId() && orderPaymentDetail.getIsRefunded() != 1)
						{
							tipAmount = tipAmount.add(orderPaymentDetail.getCreditcardTipAmt());
						}
						else if (orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Settled").getId()
								&& (orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Capture").getId() || orderPaymentDetail
										.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "CaptureAll").getId()))
						{
							tipAmount = tipAmount.add(orderPaymentDetail.getCreditcardTipAmt());
						}
					}

				}
			}

			return tipAmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	/**
	 * Calculate total cash tip.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param orderList
	 *            the order list
	 * @param activeBatchDetail
	 *            the active batch detail
	 * @return the double
	 */
	private BigDecimal calculateTotalTaxOnCashSale(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail activeBatchDetail)
	{
		try
		{
			BigDecimal tipAmount = new BigDecimal(0);
			if (orderList != null)
			{
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{
					if (orderPaymentDetail != null && activeBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(activeBatchDetail.getId()))
					{

						int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

						if (transectionTypeId == getPaymentTransactionType(em, locationId, "Sale").getId() && orderPaymentDetail.getIsRefunded() == 0)
						{
							tipAmount = tipAmount.add(orderPaymentDetail.getPriceTax1().add(
									orderPaymentDetail.getPriceTax2().add(orderPaymentDetail.getPriceTax3().add(orderPaymentDetail.getPriceTax4()))));

						}

					}

				}
			}

			return tipAmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}
	private BigDecimal calculateTotalTaxOnChequeSale(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail activeBatchDetail)
	{
		try
		{
			BigDecimal tipAmount = new BigDecimal(0);
			if (orderList != null)
			{
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{
					if (orderPaymentDetail != null && activeBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(activeBatchDetail.getId()))
					{
						PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();

						if (paymentMethod.getPaymentMethodTypeId() == null)
						{

							paymentMethod = (PaymentMethod) new CommonMethods().getObjectById("PaymentMethod", em,PaymentMethod.class, paymentMethod.getPaymentMethodTypeId());
						}

						if (paymentMethod != null)
						{
							int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

							if (transectionTypeId == getPaymentTransactionType(em, locationId, "Auth").getId() && 
									orderPaymentDetail.getIsRefunded() == 0
									&& paymentMethod.getPaymentMethodTypeId().equals(getPaymentMethodType(em, locationId, "cheque").getId()))
							{
								tipAmount = tipAmount.add(orderPaymentDetail.getPriceTax1().add(
										orderPaymentDetail.getPriceTax2().add(orderPaymentDetail.getPriceTax3().add(orderPaymentDetail.getPriceTax4()))));

							}	
						}
						

					}

				}
			}

			return tipAmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	/**
	 * Calculate total graduity on cash sale.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param orderList
	 *            the order list
	 * @param activeBatchDetail
	 *            the active batch detail
	 * @return the big decimal
	 */
	private BigDecimal calculateTotalGraduityOnCashSale(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail activeBatchDetail)
	{
		try
		{
			BigDecimal tipAmount = new BigDecimal(0);
			if (orderList != null)
			{
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{
					if (orderPaymentDetail != null && activeBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(activeBatchDetail.getId()))
					{

						int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

						if (transectionTypeId == getPaymentTransactionType(em, locationId, "Sale").getId() && orderPaymentDetail.getIsRefunded() == 0)
						{
							tipAmount = tipAmount.add(orderPaymentDetail.getPriceGratuity());

						}

					}

				}
			}

			return tipAmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}
	
	private BigDecimal calculateTotalGraduityOnChequeSale(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail activeBatchDetail)
	{
		try
		{
			BigDecimal tipAmount = new BigDecimal(0);
			if (orderList != null)
			{
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{
					if (orderPaymentDetail != null && activeBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(activeBatchDetail.getId()))
					{
						PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();

						if (paymentMethod.getPaymentMethodTypeId() == null)
						{

							paymentMethod = (PaymentMethod) new CommonMethods().getObjectById("PaymentMethod", em,PaymentMethod.class, paymentMethod.getPaymentMethodTypeId());
						}

						if (paymentMethod != null)
						{
							int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

							if (transectionTypeId == getPaymentTransactionType(em, locationId, "Auth").getId() && 
									orderPaymentDetail.getIsRefunded() == 0
									&& paymentMethod.getPaymentMethodTypeId().equals(getPaymentMethodType(em, locationId, "cheque").getId()))
							{
								tipAmount = tipAmount.add(orderPaymentDetail.getPriceGratuity());

							}	
						}

						

					}

				}
			}

			return tipAmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	/**
	 * Calculate total cash tip.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param orderList
	 *            the order list
	 * @param activeBatchDetail
	 *            the active batch detail
	 * @return the double
	 */
	private BigDecimal calculateTotalTaxOnCardSale(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail activeBatchDetail)
	{
		try
		{
			{
				BigDecimal totalAmmount = new BigDecimal(0);
				if (orderList != null)
				{
					for (OrderPaymentDetail orderPaymentDetail : orderList)
					{
						if (orderPaymentDetail != null && activeBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(activeBatchDetail.getId()))
						{

							PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();

							if (paymentMethod.getPaymentMethodTypeId() == null)
							{

								paymentMethod = (PaymentMethod) new CommonMethods().getObjectById("PaymentMethod", em,PaymentMethod.class, paymentMethod.getPaymentMethodTypeId());
							}

							String paymentMethodTypeId = null;
							if (paymentMethod != null)
							{
								paymentMethodTypeId = paymentMethod.getPaymentMethodTypeId();
							}
							
							if ((orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Auth").getId()
									|| orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "Manual CC Auth").getId() || (orderPaymentDetail
									.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "Tip Saved").getId())
									&& (orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Auth").getId())
									 )
									&& orderPaymentDetail.getIsRefunded() != 1)
							{
								if(!paymentMethodTypeId.equals(getPaymentMethodType(em, locationId, "cheque").getId()))
								{
									totalAmmount = totalAmmount.add(orderPaymentDetail.getPriceTax1().add(
											orderPaymentDetail.getPriceTax2().add(orderPaymentDetail.getPriceTax3().add(orderPaymentDetail.getPriceTax4()))));	
								}
								
							}
							else if (orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Pre Capture").getId()
									&& orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Force").getId() && orderPaymentDetail.getIsRefunded() != 1)
							{
								totalAmmount = totalAmmount.add(orderPaymentDetail.getPriceTax1().add(
										orderPaymentDetail.getPriceTax2().add(orderPaymentDetail.getPriceTax3().add(orderPaymentDetail.getPriceTax4()))));
							}
							else if (orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Settled").getId()
									&& (orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Capture").getId() || orderPaymentDetail
											.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "CaptureAll").getId()))
							{
								totalAmmount = totalAmmount.add(orderPaymentDetail.getPriceTax1().add(
										orderPaymentDetail.getPriceTax2().add(orderPaymentDetail.getPriceTax3().add(orderPaymentDetail.getPriceTax4()))));
							}
						}

					}
				}

				return totalAmmount;
			}
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	/**
	 * Calculate total graduity on card sale.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param orderList
	 *            the order list
	 * @param actBatchDetail
	 *            the act batch detail
	 * @return the big decimal
	 */
	private BigDecimal calculateTotalGraduityOnCardSale(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail actBatchDetail)
	{
		try
		{
			BigDecimal tipAmount = new BigDecimal(0);
			if (orderList != null)
			{
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{
					if (orderPaymentDetail != null && actBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(actBatchDetail.getId()))
					{

						PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();

						if (paymentMethod.getPaymentMethodTypeId() == null)
						{

							paymentMethod = (PaymentMethod) new CommonMethods().getObjectById("PaymentMethod", em,PaymentMethod.class, paymentMethod.getPaymentMethodTypeId());
						}

						String paymentMethodTypeId = null;
						if (paymentMethod != null)
						{
							paymentMethodTypeId = paymentMethod.getPaymentMethodTypeId();
						}
						
						int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

						if (transectionTypeId == getPaymentTransactionType(em, locationId, "Auth").getId() 
								
								&& orderPaymentDetail.getIsRefunded() == 0)
						{
							if(!paymentMethodTypeId.equals(getPaymentMethodType(em, locationId, "cheque").getId()))
							{
								tipAmount = tipAmount.add(orderPaymentDetail.getPriceGratuity());	
							}

							

						}

					}

				}
			}

			return tipAmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	/**
	 * Calculate total cash tip.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param orderList
	 *            the order list
	 * @param acBatchDetail
	 *            the ac batch detail
	 * @return the double
	 */
	public BigDecimal calculateTotalCreditTermTip(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail acBatchDetail)
	{
		try
		{
			BigDecimal tipAmount = new BigDecimal(0);
			if (orderList != null)
			{
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{
					if (orderPaymentDetail != null && acBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(acBatchDetail.getId()))
					{

						{
							int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();
							if (orderPaymentDetail.getIsRefunded() == 0 && getPaymentTransactionType(em, locationId, "Credit Refund") != null
									&& transectionTypeId != getPaymentTransactionType(em, locationId, "Credit Refund").getId())
							{

								if ((getPaymentTransactionType(em, locationId, "Credit") != null && transectionTypeId == getPaymentTransactionType(em, locationId, "Credit").getId()))
								{
									tipAmount = tipAmount.add(orderPaymentDetail.getCreditTermTip());

								}

							}
						}

					}

				}
			}

			return tipAmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	/**
	 * Calculate total cash tip.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param orderList
	 *            the order list
	 * @param acBatchDetail
	 *            the ac batch detail
	 * @return the double
	 */
	private BigDecimal calculateTotalTaxOnCreditTerm(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail acBatchDetail)
	{
		try
		{
			BigDecimal tipAmount = new BigDecimal(0);
			if (orderList != null)
			{
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{
					if (orderPaymentDetail != null && acBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(acBatchDetail.getId()))
					{

						int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

						if (transectionTypeId == getPaymentTransactionType(em, locationId, "Credit").getId() && orderPaymentDetail.getIsRefunded() == 0)
						{
							tipAmount = tipAmount.add(orderPaymentDetail.getPriceTax1().add(
									orderPaymentDetail.getPriceTax2().add(orderPaymentDetail.getPriceTax3().add(orderPaymentDetail.getPriceTax4()))));

						}

					}

				}
			}

			return tipAmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	/**
	 * Calculate total graduity on credit term.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param orderList
	 *            the order list
	 * @param actBatchDetail
	 *            the act batch detail
	 * @return the big decimal
	 */
	private BigDecimal calculateTotalGraduityOnCreditTerm(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail actBatchDetail)
	{
		try
		{
			BigDecimal tipAmount = new BigDecimal(0);
			if (orderList != null)
			{
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{
					if (orderPaymentDetail != null && actBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(actBatchDetail.getId()))
					{

						int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

						if (transectionTypeId == getPaymentTransactionType(em, locationId, "Credit").getId() && orderPaymentDetail.getIsRefunded() == 0)
						{
							tipAmount = tipAmount.add(orderPaymentDetail.getPriceGratuity());

						}

					}

				}
			}

			return tipAmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	/**
	 * Calculate cash ammount total.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param orderList
	 *            the order list
	 * @param actBatchDetail
	 *            the act batch detail
	 * @return the double
	 */
	public BigDecimal calculateCashAmountTotal(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail actBatchDetail, boolean needToConsiderTax)
	{

		try
		{
			BigDecimal totalAmmount = new BigDecimal(0);
			if (orderList != null)
			{
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{
					// todo shlok need
					// unused else remove
					if (true)
					{
						if (orderPaymentDetail != null && actBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(actBatchDetail.getId()))
						{

							int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

							PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();

							if (paymentMethod.getPaymentMethodTypeId() == null)
							{

								paymentMethod = (PaymentMethod) new CommonMethods().getObjectById("PaymentMethod", em,PaymentMethod.class, paymentMethod.getPaymentMethodTypeId());
							}

							if (paymentMethod != null)
							{
								String paymentMethodTypeId = paymentMethod.getPaymentMethodTypeId();

								if (orderPaymentDetail.getIsRefunded() == 0 && paymentMethodTypeId.equals(getPaymentMethodType(em, locationId, "Cash").getId())
										&& transectionTypeId != getPaymentTransactionType(em, locationId, "Refund").getId())
								{

									if (orderPaymentDetail.getAmountPaid() != null)
									{
										totalAmmount = totalAmmount.add(orderPaymentDetail.getAmountPaid());

										if (!needToConsiderTax)
										{
											BigDecimal totalTax = orderPaymentDetail.getPriceGratuity().add(orderPaymentDetail.getPriceTax1()).add(orderPaymentDetail.getPriceTax2())
													.add(orderPaymentDetail.getPriceTax3()).add(orderPaymentDetail.getPriceTax4());
											totalAmmount = totalAmmount.subtract(totalTax);

										}
									}

								}
							}

						}
					}
					else
					{

						int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

						PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();

						if (paymentMethod.getPaymentMethodTypeId() == null)
						{

							paymentMethod = getPaymentMethod(em, locationId, orderPaymentDetail.getPaymentMethod().getId());
						}
						if (paymentMethod != null)
						{

							String paymentMethodTypeId = paymentMethod.getPaymentMethodTypeId();
							if (orderPaymentDetail.getIsRefunded() == 0 && paymentMethodTypeId.equals(getPaymentMethodType(em, locationId, "Cash").getId())
									&& transectionTypeId != getPaymentTransactionType(em, locationId, "Refund").getId())
							{
								if (orderPaymentDetail.getAmountPaid() != null)
								{
									totalAmmount = totalAmmount.add(orderPaymentDetail.getAmountPaid());
								}

							}
						}

					}

				}
			}

			return totalAmmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}
	
	public BigDecimal calculateChequeAmountTotal(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail actBatchDetail, boolean needToConsiderTax)
	{

		try
		{
			BigDecimal totalAmmount = new BigDecimal(0);
			if (orderList != null)
			{
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{
					// todo shlok need
					// unused else remove
					if (true)
					{
						if (orderPaymentDetail != null && actBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber() .equals(actBatchDetail.getId()))
						{


							PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();

							if (paymentMethod.getPaymentMethodTypeId() == null)
							{

								paymentMethod = (PaymentMethod) new CommonMethods().getObjectById("PaymentMethod", em,PaymentMethod.class, paymentMethod.getPaymentMethodTypeId());
							}

							if (paymentMethod != null)
							{
								int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

								if (transectionTypeId == getPaymentTransactionType(em, locationId, "Auth").getId() && 
										orderPaymentDetail.getIsRefunded() == 0
										&& paymentMethod.getPaymentMethodTypeId().equals(getPaymentMethodType(em, locationId, "cheque").getId()))
								{

									if (orderPaymentDetail.getAmountPaid() != null)
									{
										totalAmmount = totalAmmount.add(orderPaymentDetail.getAmountPaid());

										if (!needToConsiderTax)
										{
											BigDecimal totalTax = orderPaymentDetail.getPriceGratuity().add(orderPaymentDetail.getPriceTax1()).add(orderPaymentDetail.getPriceTax2())
													.add(orderPaymentDetail.getPriceTax3()).add(orderPaymentDetail.getPriceTax4());
											totalAmmount = totalAmmount.subtract(totalTax);

										}
									}

								}
							}

						}
					}
					else
					{

						int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

						PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();

						if (paymentMethod.getPaymentMethodTypeId() == null)
						{

							paymentMethod = getPaymentMethod(em, locationId, orderPaymentDetail.getPaymentMethod().getId());
						}
						if (paymentMethod != null)
						{

							String paymentMethodTypeId = paymentMethod.getPaymentMethodTypeId();
							if (orderPaymentDetail.getIsRefunded() == 0 && paymentMethodTypeId.equals(getPaymentMethodType(em, locationId, "Cash").getId())
									&& transectionTypeId != getPaymentTransactionType(em, locationId, "Refund").getId())
							{
								if (orderPaymentDetail.getAmountPaid() != null)
								{
									totalAmmount = totalAmmount.add(orderPaymentDetail.getAmountPaid());
								}

							}
						}

					}

				}
			}

			return totalAmmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	/**
	 * Calculate cash ammount total.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param orderList
	 *            the order list
	 * @param actBatchDetail
	 *            the act batch detail
	 * @return the double
	 */
	public BigDecimal calculateCreditTerm(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail actBatchDetail, boolean needToConsiderTax)
	{
		try
		{
			BigDecimal totalAmmount = new BigDecimal(0);
			if (orderList != null)
			{
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{

					// todo shlok need
					// unused else remove
					if (true)
					{
						if (orderPaymentDetail != null && actBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(actBatchDetail.getId()))
						{
							int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();
							PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();

							if (paymentMethod.getPaymentMethodTypeId() == null)
							{
								paymentMethod = getPaymentMethod(em, locationId, orderPaymentDetail.getPaymentMethod().getId());
							}

							if (paymentMethod != null)
							{
								String paymentMethodTypeId = paymentMethod.getPaymentMethodTypeId();

								PaymentMethodType paymentMethodType = getPaymentMethodTypeById(em, locationId, paymentMethodTypeId);
								PaymentType paymentType = null;
								if (paymentMethodType != null)
								{
									paymentType = getPaymentType(em, paymentMethodType.getPaymentTypeId());
								}

								if ((orderPaymentDetail.getIsRefunded() == 0
										&& (paymentMethodTypeId.equals(getPaymentMethodType(em, locationId, "Credit Term").getId() )|| (paymentType != null && paymentType.getName().equals("Credit Term"))) && transectionTypeId != getPaymentTransactionType(
											em, locationId, "Credit Refund").getId()))
								{
									if (orderPaymentDetail.getAmountPaid() != null)
									{
										totalAmmount = totalAmmount.add(orderPaymentDetail.getAmountPaid());
										if (!needToConsiderTax)
										{
											BigDecimal totalTax = orderPaymentDetail.getPriceGratuity().add(orderPaymentDetail.getPriceTax1()).add(orderPaymentDetail.getPriceTax2())
													.add(orderPaymentDetail.getPriceTax3()).add(orderPaymentDetail.getPriceTax4());
											totalAmmount = totalAmmount.subtract(totalTax);
										}
									}

								}
							}

						}

					}
					else
					{

						int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

						PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();

						if (paymentMethod.getPaymentMethodTypeId() == null)
						{
							paymentMethod = getPaymentMethod(em, locationId, orderPaymentDetail.getPaymentMethod().getId());
						}
						if (paymentMethod != null)
						{
							String paymentMethodTypeId = paymentMethod.getPaymentMethodTypeId();

							PaymentMethodType paymentMethodType = getPaymentMethodTypeById(em, locationId, paymentMethodTypeId);
							;
							PaymentType paymentType = getPaymentType(em, paymentMethodType.getPaymentTypeId());
							if ((orderPaymentDetail.getIsRefunded() == 0 && (paymentMethodTypeId == getPaymentMethodType(em, locationId, "Credit Term").getId() || paymentType.getName().equals(
									"Credit Term")
									&& transectionTypeId != getPaymentTransactionType(em, locationId, "Credit Refund").getId())))
							{
								if (orderPaymentDetail.getAmountPaid() != null)
								{
									totalAmmount = totalAmmount.add(orderPaymentDetail.getAmountPaid());
									if (!needToConsiderTax)
									{
										BigDecimal totalTax = orderPaymentDetail.getPriceGratuity().add(orderPaymentDetail.getPriceTax1()).add(orderPaymentDetail.getPriceTax2())
												.add(orderPaymentDetail.getPriceTax3()).add(orderPaymentDetail.getPriceTax4());
										totalAmmount = totalAmmount.subtract(totalTax);
									}
								}

							}
						}

					}

				}
			}

			return totalAmmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception

			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	/**
	 * Calculate card ammount total.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param orderList
	 *            the order list
	 * @param actBatchDetail
	 *            the act batch detail
	 * @return the double
	 */
	public BigDecimal calculateCardAmountTotal(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail actBatchDetail, boolean needToConsiderTax)
	{
		try
		{
			BigDecimal totalAmmount = new BigDecimal(0);
			if (orderList != null)
			{
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{
					// todo shlok need
					// unused if remove
					if (true)
					{
						if (orderPaymentDetail != null && actBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(actBatchDetail.getId()))
						{
							PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();

							if (paymentMethod.getPaymentMethodTypeId() == null)
							{

								paymentMethod = (PaymentMethod) new CommonMethods().getObjectById("PaymentMethod", em,PaymentMethod.class, paymentMethod.getPaymentMethodTypeId());
							}

							String paymentMethodTypeId = null;
							if (paymentMethod != null)
							{
								paymentMethodTypeId = paymentMethod.getPaymentMethodTypeId();
							}
							
							
									
							if ((orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Auth").getId()
									|| orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "Manual CC Auth").getId() || (orderPaymentDetail
									.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "Tip Saved").getId())
									&& (orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Auth").getId())
									 )
									&& orderPaymentDetail.getIsRefunded() != 1)
							{

								if(!paymentMethodTypeId.equals(getPaymentMethodType(em, locationId, "cheque").getId()))
								{
									totalAmmount = totalAmmount.add(orderPaymentDetail.getAmountPaid());
									if (!needToConsiderTax)
									{
										BigDecimal totalTax = orderPaymentDetail.getPriceGratuity().add(orderPaymentDetail.getPriceTax1()).add(orderPaymentDetail.getPriceTax2())
												.add(orderPaymentDetail.getPriceTax3()).add(orderPaymentDetail.getPriceTax4());
										totalAmmount = totalAmmount.subtract(totalTax);
									}	
								}
								

							}
							else if (orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Pre Capture").getId()
									&& orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Force").getId() && orderPaymentDetail.getIsRefunded() != 1)
							{
								totalAmmount = totalAmmount.add(orderPaymentDetail.getAmountPaid());
								if (!needToConsiderTax)
								{
									BigDecimal totalTax = orderPaymentDetail.getPriceGratuity().add(orderPaymentDetail.getPriceTax1()).add(orderPaymentDetail.getPriceTax2())
											.add(orderPaymentDetail.getPriceTax3()).add(orderPaymentDetail.getPriceTax4());
									totalAmmount = totalAmmount.subtract(totalTax);
								}

							}
							else if (orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Settled").getId()
									&& (orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Capture").getId() || orderPaymentDetail
											.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "CaptureAll").getId()))
							{
								if (!needToConsiderTax)
								{
									BigDecimal totalTax = orderPaymentDetail.getPriceGratuity().add(orderPaymentDetail.getPriceTax1()).add(orderPaymentDetail.getPriceTax2())
											.add(orderPaymentDetail.getPriceTax3()).add(orderPaymentDetail.getPriceTax4());
									totalAmmount = totalAmmount.subtract(totalTax);
								}
								totalAmmount = totalAmmount.add(orderPaymentDetail.getAmountPaid());
							}
						}
					}

				}
			}

			return totalAmmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception

			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	/**
	 * This is used to get all employeeOperationToCashRegister for a given
	 * location.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param locationsId
	 *            the locations id
	 * @param activeBatchDetail
	 *            the active batch detail
	 * @return the all employee operation to cash register by location id
	 * @throws ParseException
	 *             the parse exception
	 */
	List<EmployeeOperationToCashRegister> getAllEmployeeOperationToCashRegisterByLocationId(HttpServletRequest httpRequest, EntityManager em, String locationsId, BatchDetail activeBatchDetail)
			throws ParseException
	{
		List<EmployeeOperationToCashRegister> resultSet = new ArrayList<EmployeeOperationToCashRegister>();

		String queryString = "select eo from EmployeeOperationToCashRegister eo where eo.status  not in ('D') and eo.locationsId=? and eo.created between ? and ? ";
		TypedQuery<EmployeeOperationToCashRegister> query = em.createQuery(queryString, EmployeeOperationToCashRegister.class).setParameter(1, locationsId)
				.setParameter(2, new Date(activeBatchDetail.getStartTime())).setParameter(3, new Date(new TimezoneTime().getGMTTimeInMilis()));
		resultSet = query.getResultList();
		return resultSet;
	}

	/**
	 * Gets the users to payment history by location id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param locationsId
	 *            the locations id
	 * @param activeBatchDetail
	 *            the active batch detail
	 * @return the users to payment history by location id
	 * @throws ParseException
	 *             the parse exception
	 */
	List<UsersToPaymentHistory> getUsersToPaymentHistoryByLocationId(HttpServletRequest httpRequest, EntityManager em, String locationsId, BatchDetail activeBatchDetail) throws ParseException
	{
		List<UsersToPaymentHistory> resultSet = new ArrayList<UsersToPaymentHistory>();
		String queryString = "select eo from UsersToPaymentHistory eo where eo.status  not in ('D') and eo.amountPaid<0 and eo.locationId=? and eo.created between ? and ?";
		TypedQuery<UsersToPaymentHistory> query = em.createQuery(queryString, UsersToPaymentHistory.class).setParameter(1, locationsId).setParameter(2, new Date(activeBatchDetail.getStartTime()))
				.setParameter(3, new Date(new TimezoneTime().getGMTTimeInMilis()));
		resultSet = query.getResultList();
		return resultSet;
	}

	/**
	 * Prints the batch settle receipt.
	 *
	 * @param settleCreditCardBatchPacket
	 *            the settle credit card batch packet
	 * @param registerInfos
	 *            the register infos
	 * @return the string builder
	 */
	private StringBuilder printBatchSettleReceipt(SettleCreditCardBatchPacket settleCreditCardBatchPacket, List<RegisterInfo> registerInfos,LocationSetting locationSetting,int businessId, BatchDetail batchDetail,EntityManager em)
	{

		StringBuilder receipt = new StringBuilder();
		try
		{
			receipt.append("<table style=\"border: 0px; width: 320px; border-spacing: 0; border-width: 0; padding: 0; border-width: 0;\"> " + "<tr> <td style=\"width: 70%; text-align:left; \">"
					+ settleCreditCardBatchPacket.orderCountConstant + "</td> <td style=\"width: 30%; text-align:right; \">" + settleCreditCardBatchPacket.getOrderCount() + "</td></tr> ");

			receipt.append("<tr> <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.guestCountConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getGuestCount() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.averageAmountPerGuestConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getAverageAmountPerGuest() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.averageGuestPerOrderConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getAverageGuestPerOrder() + "</td></tr> ");

			receipt.append("<tr> <td style=\"width: 70%; text-align: left;\" colspan=\"2\">-------------------------------------------------------------------------------" + "	</td> </tr>");

			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.salesConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getSales() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.discountsConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getDiscounts() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.nonChargeableConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getNonChargeable() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.netSalesConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTotalSales1() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.taxConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTax() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.autoGratuityConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getAutoGratuity() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.totalDeliveryChargesConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTotalDeliveryCharges() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.totalDeliveryTaxConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTotalDeliveryTax() + "</td></tr> ");

			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.getTotalServiceChargeConstant() + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTotalServiceCharge() + "</td></tr> ");

			receipt.append("<tr> <td style=\"width: 70%; text-align: left;\" colspan=\"2\" >-------------------------------------------------------------------------------" + "	</td> </tr>");

			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.totalSalesConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTotalSales2() + "</td></tr> ");

			receipt.append("<tr> <td style=\"width: 70%; text-align: left;\" colspan=\"2\" >-------------------------------------------------------------------------------" + "	</td> </tr>");

			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.balanceDueConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getBalanceDue() + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.cashNetSalesConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getCashSales() + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.taxOnCashSaleConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTaxOnCashSale() + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.graduityOnCashSaleConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getGraduityOnCashSale() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.cashGrossSalesConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getCashGrossSales() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.cashTipsConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getCashTips() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.totalCashConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTotalCash() + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + "<br>");
			
			
			receipt.append("<tr><td   style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.cardNetSalesConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getCardSales() + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.taxOnCardSalesConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTaxOnCardSales() + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.graduityOnCardSalesConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getGraduityOnCardSales() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.cardGrossSalesConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getCardGrossSales() + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.cardTipsConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getCardTips() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.totalCardConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTotalCard() + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> <br></td></tr> ");
			
			
			receipt.append("<tr><td   style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.chequeNetSalesConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getChequeSales() + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.taxOnChequeSaleConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTaxOnChequeSale() + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.graduityOnChequeSaleConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getGraduityOnChequeSale() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.chequGrossSalesConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getChequeGrossSales() + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.chequeTipsConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getChequeTips() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.totalChequeConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTotalCheque() + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> <br></td></tr> ");
			
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.creditTermNetSalesConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getCreditTerm() + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.taxonCreditTermConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTaxOnCreditTerm() + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.graduityonCreditTermConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getGraduityOnCreditTerm() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.creditTermGrossConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getCreditTermGross() + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.creditTermTipsConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getCreditTermTips() + "</td></tr> ");
			receipt.append("<tr><td   style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.totalCreditConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTotalCredit() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + "<br>" + "</td></tr> ");
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.giftCertificatesRedeemedConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getGiftCertificatesRedeemed() + "</td></tr> ");

			List<Object[]> resultListPaymentMethodType = em
					.createNativeQuery("call p_revenue_payment_method_receipt(?,?,?)").setParameter(1, businessId)
					.setParameter(2, new Date(batchDetail.getStartTime())).setParameter(3, new Date(new TimezoneTime().getGMTTimeInMilis())).getResultList();
			logger.severe("businessId=========================================================="+businessId);
			logger.severe("new Date(batchDetail.getStartTime())=========================================================="+new Date(batchDetail.getStartTime()));
			logger.severe("new Date(new TimezoneTime().getGMTTimeInMilis())=========================================================="+new Date(new TimezoneTime().getGMTTimeInMilis()));
			
			if (resultListPaymentMethodType != null && resultListPaymentMethodType.size() > 0) {
				String total=null;
				String paymentMethodTypeName=null;
				
				for (Object[] objRow : resultListPaymentMethodType) {
					// if this has primary key not 0
					int i = 0;
					if (objRow[i] != null){
					     paymentMethodTypeName=((String) objRow[i]);
					}
					i++;
					i++;
					
					if (objRow[i] != null){
						  total=((new BigDecimal((double) objRow[i]).setScale(2, RoundingMode.HALF_UP)) + "");
					}
					
					if(total!=null && !total.equals('0')){
					receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + paymentMethodTypeName + "</td> <td style=\"width: 30%; text-align:right; \">"
							+ total + "</td></tr> ");
				}
				}
			}
		

			receipt.append("<tr> <td style=\"width: 70%; text-align: left;\" colspan=\"2\" >-------------------------------------------------------------------------------" + "	</td> </tr>");

			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.totalPaidConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTotalPaid() + "</td></tr> ");

			receipt.append("<tr> <td style=\"width: 70%; text-align: left;\" colspan=\"2\" >-------------------------------------------------------------------------------" + "	</td> </tr>");

			if(locationSetting.getIsCreditTermAllowed()==1){
			// wallet
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.totalWalletCashConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTotalWalletCash() + "</td></tr> ");
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.totalWalletManualCCConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTotalWalletManualCC() + "</td></tr> ");

			receipt.append("<tr> <td style=\"width: 70%; text-align: left;\" colspan=\"2\" >-------------------------------------------------------------------------------" + "	</td> </tr>");

			
			
			receipt.append("<tr> <td  style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.totalWalletConstant + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTotalWallet() + "</td></tr> ");

			receipt.append("<tr> <td style=\"width: 70%; text-align: left;\" colspan=\"2\" >-------------------------------------------------------------------------------" + "	</td> </tr>");
			}
			// Paid in
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.getTotalPaidInConstant() + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTotalPaidIn() + "</td></tr> ");
			// Paid out
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.getTotalPaidOutConstant() + "</td> <td style=\"width: 30%; text-align:right; \">"
					+ settleCreditCardBatchPacket.getTotalPaidOut() + "</td></tr> ");

			receipt.append("<tr> <td style=\"width: 70%; text-align: left;\" colspan=\"2\" >-------------------------------------------------------------------------------" + "	</td> </tr>");

			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.getTotalCashInAllRegisterConstant()
					+ "</td> <td style=\"width: 30%; text-align:right; \">" + settleCreditCardBatchPacket.getTotalCashInAllRegister() + "</td></tr> ");

			
			if (registerInfos != null && !registerInfos.isEmpty() && locationSetting.getIsPaidInPaidOutValidation()==1)
			{
				for (RegisterInfo registerInfo : registerInfos)
				{
					receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + registerInfo.name + "</td> <td style=\"width: 30%; text-align:right; \">" + registerInfo.total
							+ "</td></tr> ");
				}
			}
			if(locationSetting.getIsCreditTermAllowed()==1){
				if(settleCreditCardBatchPacket.getTotalCC()==null){
					settleCreditCardBatchPacket.setTotalCC(""+BigDecimal.ZERO);
				}
			receipt.append("<tr>  <td style=\"width: 70%; text-align:left; \"> " + settleCreditCardBatchPacket.getTotalCCConstant()
					+ "</td> <td style=\"width: 30%; text-align:right; \">" + settleCreditCardBatchPacket.getTotalCC() + "</td></tr> ");
			}
			receipt.append("</table>");

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block

			// todo shlok need
			// handel proper exception

			logger.severe(e);
		}

		return receipt;
	}

	/**
	 * Gets the item level tax.
	 *
	 * @param em
	 *            the em
	 * @param orderHeader
	 *            the order header
	 * @param locationId
	 *            the location id
	 * @param taxName
	 *            the tax name
	 * @return the item level tax
	 */
	public BigDecimal getItemLevelTax(EntityManager em, OrderHeader orderHeader, String locationId, String taxName)
	{
		double itemPrice = 0;
		try
		{
			SalesTax salesTax = getSalesTax(em, locationId, taxName);
			if (salesTax != null && salesTax.getIsItemSpecific() == 1)
			{
				if (orderHeader.getOrderDetailItems() != null)
				{

					for (OrderDetailItem orderDetailItem : orderHeader.getOrderDetailItems())
					{
						OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class, orderDetailItem.getOrderDetailStatusId());

						if (orderDetailStatus != null && !(orderDetailStatus.getName().equalsIgnoreCase("Item Removed")) && !(orderDetailStatus.getName().equalsIgnoreCase("Recall")))
						{
							double price = 0;
							if (orderDetailItem.getPriceDiscount().compareTo(BigDecimal.ZERO) != 0)
							{
								price = orderDetailItem.getSubTotal().doubleValue()-(orderDetailItem.getPriceDiscount()).doubleValue();
							}
							else
							{
								price = orderDetailItem.getSubTotal().doubleValue();

							}
							if (orderDetailItem.getTaxName1() != null && orderDetailItem.getTaxName1().equals(salesTax.getTaxName()))
							{
								itemPrice += price;

							}
							 if (orderDetailItem.getTaxName2() != null && orderDetailItem.getTaxName2().equals(salesTax.getTaxName()))
							{
								 itemPrice += price;
							}
							 if (orderDetailItem.getTaxName3() != null && orderDetailItem.getTaxName3().equals(salesTax.getTaxName()))
							{
								 itemPrice += price;
								
							}
							 if (orderDetailItem.getTaxName4() != null && orderDetailItem.getTaxName4().equals(salesTax.getTaxName()))
							{
								 itemPrice += price;
								
							}
							 
						}

					}
				}
			}
			return new BigDecimal(itemPrice).setScale(2, BigDecimal.ROUND_HALF_DOWN) ;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			// todo shlok need
			// handel proper exception

			logger.severe(e);
		}
		return new BigDecimal(itemPrice).setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}

	/**
	 * The Class DiscountRow.
	 */
	class DiscountRow
	{

		/** The discount display name. */
		String discountDisplayName;

		/** The discount value. */
		int discountValue;

		/** The discount sub total. */
		BigDecimal discountSubTotal;

		/** The discount id. */
		String discountId;

		/** The discount type name. */
		String discountTypeName;

		/** The discount name. */
		String discountName;

		/** The amount off price dis. */
		BigDecimal amountOffPriceDis;

		/**
		 * Gets the amount off price dis.
		 *
		 * @return the amount off price dis
		 */
		public BigDecimal getAmountOffPriceDis()
		{
			return amountOffPriceDis;
		}

		/**
		 * Sets the amount off price dis.
		 *
		 * @param amountOffPriceDis
		 *            the new amount off price dis
		 */
		public void setAmountOffPriceDis(BigDecimal amountOffPriceDis)
		{
			this.amountOffPriceDis = amountOffPriceDis;
		}

		/**
		 * Gets the discount name.
		 *
		 * @return the discount name
		 */
		public String getDiscountName()
		{
			return discountName;
		}

		/**
		 * Sets the discount name.
		 *
		 * @param discountName
		 *            the new discount name
		 */
		public void setDiscountName(String discountName)
		{
			this.discountName = discountName;
		}

		/**
		 * Gets the discount type name.
		 *
		 * @return the discount type name
		 */
		public String getDiscountTypeName()
		{
			return discountTypeName;
		}

		/**
		 * Sets the discount type name.
		 *
		 * @param discountTypeName
		 *            the new discount type name
		 */
		public void setDiscountTypeName(String discountTypeName)
		{
			this.discountTypeName = discountTypeName;
		}

		/**
		 * Gets the discount display name.
		 *
		 * @return the discount display name
		 */
		public String getDiscountDisplayName()
		{
			return discountDisplayName;
		}

		/**
		 * Sets the discount display name.
		 *
		 * @param discountDisplayName
		 *            the new discount display name
		 */
		public void setDiscountDisplayName(String discountDisplayName)
		{
			this.discountDisplayName = discountDisplayName;
		}

		/**
		 * Gets the discount value.
		 *
		 * @return the discount value
		 */
		public int getDiscountValue()
		{
			return discountValue;
		}

		/**
		 * Sets the discount value.
		 *
		 * @param discountPercentage
		 *            the new discount value
		 */
		public void setDiscountValue(int discountPercentage)
		{
			this.discountValue = discountPercentage;
		}

		/**
		 * Gets the discount sub total.
		 *
		 * @return the discount sub total
		 */
		public BigDecimal getDiscountSubTotal()
		{
			return discountSubTotal;
		}

		/**
		 * Sets the discount sub total.
		 *
		 * @param discountSubTotal
		 *            the new discount sub total
		 */
		public void setDiscountSubTotal(BigDecimal discountSubTotal)
		{
			this.discountSubTotal = discountSubTotal;
		}

		/**
		 * Gets the discount id.
		 *
		 * @return the discount id
		 */
		public String getDiscountId()
		{
			 if(discountId != null && (discountId.length()==0 || discountId.equals("0"))){return null;}else{	return discountId;}
		}

		/**
		 * Sets the discount id.
		 *
		 * @param discountId
		 *            the new discount id
		 */
		public void setDiscountId(String discountId)
		{
			this.discountId = discountId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object object)
		{
			if (object != null && object instanceof ReceiptPDFFormat.DiscountRow)
			{
				DiscountRow discountRow = (DiscountRow) object;
				return getDiscountId() == (discountRow.getDiscountId());

			}

			return super.equals(object);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return "DiscountRow [discountDisplayName=" + discountDisplayName + ", discountValue=" + discountValue + ", discountSubTotal=" + discountSubTotal + ", discountId=" + discountId
					+ ", discountTypeName=" + discountTypeName + ", discountName=" + discountName + "]";
		}

	}

	/**
	 * Creates the receipt PDF string for COD.
	 *
	 * @param em
	 *            the em
	 * @param httpRequest
	 *            the http request
	 * @param orderId
	 *            the order id
	 * @param takeoutDelivery
	 *            the takeout delivery
	 * @param isFromReceived
	 *            the is from received
	 * @return the string builder
	 */
	public StringBuilder createReceiptPDFStringForCOD(EntityManager em, HttpServletRequest httpRequest, String orderId, int takeoutDelivery, boolean isFromReceived)
	{

		// todo shlok need
		// modularise code

		String locationpath = null;
		String qrcodePath = null;
		String serverURL = null;
		String feedbackUrl = null;

		try
		{
			locationpath = ConfigFileReader.getWebsiteLogoPath();
			qrcodePath = ConfigFileReader.getQRCodeUploadPathFromFile();
			serverURL = ConfigFileReader.getQRCodeServerName();
			feedbackUrl = ConfigFileReader.getAdminFeedbackURL();
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, "getWebsiteLogoPath not found in database for" + orderId);
		}

		OrderManagementServiceBean managementServiceBean = new OrderManagementServiceBean();
		String discountDisplayName = "";

		OrderHeader orderHeader = managementServiceBean.getOrderById(em, orderId);
		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderHeader.getOrderSourceId());
		OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
		if (orderSourceGroup.getName().equals("In Store"))
		{
			takeoutDelivery = 0;
		}
		else
		{
			takeoutDelivery = 1;
		}

		if (orderHeader.getDiscountsId() != null && orderHeader.getDiscountsId() != null && orderHeader.getDiscountDisplayName() != null)
		{
			discountDisplayName = orderHeader.getDiscountDisplayName();
		}

		Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, orderHeader.getLocationsId());
		Address localAddress = null;
		List<Location> result = null;
		Location foundLocation = null;
		List<Course> courses = null;
		if (location != null && location.getLocationsId() != null)
		{
			String queryString = "select l from Location l where  (l.locationsId ='0' or l.locationsId is null ) and l.businessId=" + location.getBusinessId() + " order by l.id asc";
			TypedQuery<Location> query = em.createQuery(queryString, Location.class);
			result = query.getResultList();
			if (result != null && result.size() > 0)
			{
				foundLocation = result.get(0);
			}
			localAddress = foundLocation.getAddress();
		}
		else
		{
			localAddress = location.getAddress();
			foundLocation = location;

		}
		String datetime = new TimezoneTime().getDateTimeFromGMTToLocation(em, orderHeader.getScheduleDateTime(), foundLocation.getId());
		SimpleDateFormat toformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat fromFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");

		try
		{
			java.util.Date date = toformatter.parse(datetime);
			datetime = fromFormatter.format(date);

		}
		catch (ParseException e)
		{
			logger.severe("Unable to parse date", datetime, "while generating pdf receipt");
		}

		qrcodePath = "http://" + serverURL + orderHeader.getQrcode() + ".png";
		feedbackUrl = feedbackUrl + "refno=" + orderHeader.getReferenceNumber() + "&order_id=" + orderHeader.getId();

		if (foundLocation != null)
		{
			String queryString = "select c from Course c where c.locationsId =? and c.status!='D'";
			TypedQuery<Course> query = em.createQuery(queryString, Course.class);
			query.setParameter(1, foundLocation.getId());
			courses = query.getResultList();
		}
		StringBuilder receipt = null;
		if (orderHeader != null)
		{
			String address1 = localAddress.getAddress1();
			String address2 = localAddress.getAddress2();
			String city = localAddress.getCity();
			String state = localAddress.getState();
			String phone = localAddress.getPhone();

			receipt = new StringBuilder().append("<html>                                                              ").append("<head>")

			.append("</head>                                                             ").append("<body>                                                              ")
					.append("                                                                       ").append("<center>                                                            ")
					.append("                                                                       ").append("<div id=\"header\">                                                ")
					.append("<img src=\"" + locationpath + foundLocation.getImageUrl() + "\" alt=\"" + foundLocation.getName() + "\"><br>                         ");
			if (address1 != null)
				receipt.append("" + address1 + "<br>                                             ");
			if (address2 != null && address2.length() > 0)
				receipt.append("" + address2 + "<br>                                              ");
			if (city != null)
				receipt.append("" + city + "<br>                                              ");
			if (state != null)
				receipt.append("" + state + "<br>                                              ");

			if (phone != null)
				receipt.append("" + phone + "<br>                                                    ");

			if (isFromReceived)
			{
				receipt.append("<div align=\"center\">  <br />	You order below is received and currently processing.<br />  Please contact " + foundLocation.getName() + ""
						+ " if you do not receive an email confirmation within 5 mins. <br />Thank you for placing an order with " + foundLocation.getName() + "<br /><br />" + "</div>");
			}

			receipt.append("<a href=\"" + foundLocation.getWebsite() + "\">" + foundLocation.getWebsite() + "</a>").append("</div>                                                              ")
					.append("<div id=\"date\">                                                     ").append(" " + datetime + "                                         ")
					.append("</div>                                                              ");

			if (takeoutDelivery == 1)
			{
				receipt.append(" <div>Order #: " + orderHeader.getOrderNumber() + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + orderSourceGroup.getDisplayName() + ":-" + orderSource.getDisplayName()
						+ " </div>                                                              ");
			}
			else
			{
				receipt.append(" <div>Order #: " + orderHeader.getOrderNumber() + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Table:-" + location.getName()
						+ " </div>                                                              ");
			}
			receipt.append("</center>                                                           ");

			receipt.append("  </div>    <div align=\"center\">                                                     ");
			receipt.append("<table ><tr><td  colspan=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br/></td>");
			BigDecimal discountValue = new BigDecimal(0);
			BigDecimal orderLevelDiscountSubtotal = new BigDecimal(0);
			List<DiscountRow> discountRows = new ArrayList<DiscountRow>();

			if (courses != null && courses.size() > 0)
			{
				int display = 0;

				for (Course course : courses)
				{
					display = 0;

					for (OrderDetailItem detailItem : orderHeader.getOrderDetailItems())
					{
						BigDecimal qty = detailItem.getItemsQty();
						if (qty.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0)
						{
							qty = qty.setScale(0, BigDecimal.ROUND_HALF_DOWN);

						}
						else
						{
							qty = qty.setScale(2, BigDecimal.ROUND_HALF_DOWN);

						}
						if (detailItem.getOrderDetailStatusId() != 0)
						{
							OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class, detailItem.getOrderDetailStatusId());

							if (orderDetailStatus != null && !(orderDetailStatus.getName().equalsIgnoreCase("Item Removed")) && !(orderDetailStatus.getName().equalsIgnoreCase("Recall")))
							{
								if (detailItem.getSentCourseId().equals(course.getId()))
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
										receipt.append("<tr align=\"left\">    <td><u>" + course.getCourseName() + "</u></td>  	<td align=\"right\">&nbsp;</td> </tr>  ");
									}
									display = 1;
									BigDecimal price = detailItem.getPriceSelling().multiply(qty);

									receipt.append("<tr align=\"left\">                                                   ")
											.append("<td >&nbsp;&nbsp;" + qty + " &nbsp;" + detailItem.getItemsShortName() + "</td>		                    ")
											.append("<td align=\"right\">" + price.setScale(2, BigDecimal.ROUND_HALF_DOWN) + " </td>	                                        ")
											.append("                                                                ").append("</tr>                                                               ");

									for (OrderDetailAttribute detailAttribute : orderDetailAttributes)
									{
										BigDecimal attributePrice = detailAttribute.getPriceSelling().multiply(qty);
										receipt.append("<tr align=\"left\">                                                   ")
												.append("<td >&nbsp;&nbsp;- " + detailAttribute.getItemsAttributeName() + " &nbsp;</td>		                    ")
												.append("<td align=\"right\">" + attributePrice + " </td>	                                        ")
												.append("                                                                ")
												.append("</tr>                                                               ");

									}
									if (detailItem.getDiscountWaysId() != 0)
									{
										DiscountWays discountWays = em.find(DiscountWays.class, detailItem.getDiscountWaysId());

										if (discountWays != null && (discountWays.getName().equals("Item Level") || discountWays.getName().equals("Seat Level")))
										{
											String discountReasonForItem = detailItem.getDiscountReasonName();
											discountValue = detailItem.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_DOWN);

											if (discountValue.compareTo(BigDecimal.ZERO) != 0)
											{
												receipt.append("<tr align=\"left\">                                                   ")
														.append("<td >&nbsp;&nbsp;-" + "(" + discountReasonForItem + ")" + " &nbsp;</td>		                    ")
														.append("<td align=\"right\">-" + discountValue + " </td>	                                        ")
														.append("                                                                ")
														.append("</tr>                                                               ")
														.append("                                                                       ")
														.append("<br/>                                                               ");

											}

											DiscountRow temp = new DiscountRow();
											temp.setDiscountDisplayName(discountReasonForItem);
											temp.setDiscountId(detailItem.getDiscountId());
											temp.setDiscountValue(detailItem.getDiscountValue());
											if (detailItem.getDiscountName().equals("Custom Discount"))
											{
												temp.setDiscountSubTotal(detailItem.getPriceDiscount());
											}
											else
											{
												temp.setDiscountSubTotal(detailItem.getSubTotal());
											}

											if (detailItem.getDiscountTypeName().equals("Amount Off"))
											{
												temp.setAmountOffPriceDis(detailItem.getPriceDiscount());
											}

											temp.setDiscountTypeName(detailItem.getDiscountTypeName());
											temp.setDiscountName(detailItem.getDiscountName());

											int indexOf = discountRows.indexOf(temp);
											if (indexOf == -1)
											{
												discountRows.add(temp);
											}
											else
											{

												DiscountRow temp1 = discountRows.get(indexOf);
												temp1.setDiscountSubTotal(temp1.getDiscountSubTotal().add(temp.getDiscountSubTotal()));

												if (detailItem.getDiscountTypeName().equals("Amount Off"))
												{
													temp1.setAmountOffPriceDis(temp1.getAmountOffPriceDis().add(temp.getAmountOffPriceDis()));
												}

											}

										}

										if (discountWays != null && discountWays.getName().equals("Order Level"))
										{
											orderLevelDiscountSubtotal = orderLevelDiscountSubtotal.add(detailItem.getSubTotal());
										}
									}

								}
							}
						}
					}
				}
			}

			BigDecimal subTotal = orderHeader.getSubTotal().setScale(2, BigDecimal.ROUND_HALF_DOWN);

			receipt.append("     <tr>                                                                  ").append("<td >Subtotal</td>		                                        ")
					.append("<td align=\"right\">" + subTotal + "</td>	                                    ").append("</tr>                                                               ");

			if (orderHeader.getDiscountsId() != null && orderHeader.getDiscountsId() != null)
			{
				if (orderHeader.getDiscountsTypeName().equals("Percentage Off"))
				{
					receipt.append("                                                                       ")
							.append("<tr align=\"left\">                                                   ")
							.append("<td >DISCOUNT " + discountDisplayName + " (" + orderHeader.getDiscountsValue() + " % of " + orderLevelDiscountSubtotal.setScale(2, BigDecimal.ROUND_HALF_DOWN)
									+ ")</td>		                    ").append("<td align=\"right\">-" + orderHeader.getPriceDiscount() + "</td>	                                        ")
							.append("</tr>                                                               ").append("                                                                       ");
				}
				else
				{
					receipt.append("                                                                       ")
							.append("<tr align=\"left\">                                                   ")
							.append("<td >DISCOUNT " + discountDisplayName + " (" + orderHeader.getDiscountsValue() + " off of " + orderLevelDiscountSubtotal.setScale(2, BigDecimal.ROUND_HALF_DOWN)
									+ ")</td>		                    ").append("<td align=\"right\">-" + orderHeader.getPriceDiscount() + "</td>	                                        ")
							.append("</tr>                                                               ").append("                                                                       ");
				}
			}

			if (discountRows != null && discountRows.size() > 0)
			{
				for (DiscountRow discountRow : discountRows)
				{
					if (discountRow.getDiscountTypeName().equals("Percentage Off"))
					{
						if (discountRow.getDiscountName().equals("Custom Discount"))
						{
							receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
									.append("<td >" + discountRow.getDiscountName() + "</td>		                    ")
									.append("<td align=\"right\">-" + (discountRow.getDiscountSubTotal().setScale(2, BigDecimal.ROUND_HALF_DOWN)) + "</td>	                                        ")
									.append("</tr>                                                               ").append("                                                                       ");
						}
						else
						{
							receipt.append("                                                                       ")
									.append("<tr align=\"left\">                                                   ")
									.append("<td >" + discountRow.getDiscountDisplayName() + " (" + discountRow.getDiscountValue() + " % of "
											+ discountRow.getDiscountSubTotal().setScale(2, BigDecimal.ROUND_HALF_DOWN) + ")</td>		                    ")
									.append("<td align=\"right\">-"
											+ new BigDecimal(discountRow.getDiscountSubTotal().doubleValue() * (Double.parseDouble(discountRow.getDiscountValue() + "") / Double.parseDouble(100 + "")))
													.setScale(2, BigDecimal.ROUND_HALF_DOWN) + "</td>	                                        ")
									.append("</tr>                                                               ").append("                                                                       ");
						}

					}
					else
					{
						if (discountRow.getDiscountName().equals("Custom Discount"))
						{
							receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
									.append("<td >" + discountRow.getDiscountName() + "</td>		                    ")
									.append("<td align=\"right\">-" + (discountRow.getDiscountSubTotal().setScale(2, BigDecimal.ROUND_HALF_DOWN)) + "</td>	                                        ")
									.append("</tr>                                                               ").append("                                                                       ");
						}
						else
						{
							receipt.append("                                                                       ")
									.append("<tr align=\"left\">                                                   ")
									.append("<td >" + discountRow.getDiscountDisplayName() + " (" + discountRow.getDiscountValue() + " off of "
											+ discountRow.getDiscountSubTotal().setScale(2, BigDecimal.ROUND_HALF_DOWN) + ")</td>		                    ")
									.append("<td align=\"right\">-" + discountRow.getAmountOffPriceDis().setScale(2, BigDecimal.ROUND_HALF_DOWN) + "</td>	                                        ")
									.append("</tr>                                                               ").append("                                                                       ");
						}

					}

				}
			}

			if (orderHeader.getIsGratuityApplied() != 0)
			{
				BigDecimal newSubTotal = subTotal;
				newSubTotal = newSubTotal.subtract(orderHeader.getPriceDiscount());
				SalesTax gratuity = getGratuty(em, foundLocation.getId());
				double gratuityPrice = (gratuity.getRate().doubleValue() * newSubTotal.doubleValue()) / 100;
				if (gratuityPrice != 0)
				{
					receipt.append("<tr align=\"left\">").append("<td >" + gratuity.getDisplayName() + " ( " + gratuity.getRate() + "% of " + newSubTotal + ")</td>		                        ")
							.append("<td align=\"right\">" + String.format("%.2f", gratuityPrice) + "</td>	                                    ")
							.append("</tr>                                                               ");
				}
			}
			subTotal = subTotal.subtract(orderHeader.getPriceDiscount());
			BigDecimal newPrice;
			if (orderHeader.getTaxDisplayName1() != null && !orderHeader.getTaxDisplayName1().equals("") && orderHeader.getTaxRate1() != new BigDecimal(0))
			{
				BigDecimal itemPrice = getItemLevelTax(em, orderHeader, foundLocation.getId(), orderHeader.getTaxName1());
				if (itemPrice.compareTo(BigDecimal.ZERO)== 0)
				{
					newPrice = subTotal;
				}
				else
				{
					newPrice = itemPrice;
				}
				receipt.append("<tr align=\"left\">").append("<td >" + orderHeader.getTaxDisplayName1() + " ( " + orderHeader.getTaxRate1() + "% of " + newPrice + ")</td>		                        ")
						.append("<td align=\"right\">" + orderHeader.getPriceTax1() + "</td>	                                    ")
						.append("</tr>                                                               ");
			}
			if (orderHeader.getTaxDisplayName2() != null && !orderHeader.getTaxDisplayName2().equals("") && orderHeader.getTaxRate2() != new BigDecimal(0))
			{
				BigDecimal itemPrice = getItemLevelTax(em, orderHeader, foundLocation.getId(), orderHeader.getTaxName2());
				if (itemPrice.compareTo(BigDecimal.ZERO) == 0)
				{
					newPrice = subTotal;
				}
				else
				{
					newPrice = itemPrice;
				}
				receipt.append("<tr align=\"left\">").append("<td >" + orderHeader.getTaxDisplayName2() + " ( " + orderHeader.getTaxRate2() + "% of " + newPrice + ")</td>		                        ")
						.append("<td align=\"right\">" + orderHeader.getPriceTax2() + "</td>	                                    ")
						.append("</tr>                                                               ");
			}
			if (orderHeader.getTaxDisplayName3() != null && !orderHeader.getTaxDisplayName3().equals("") && orderHeader.getTaxRate3() != new BigDecimal(0))
			{
				BigDecimal itemPrice = getItemLevelTax(em, orderHeader, foundLocation.getId(), orderHeader.getTaxName3());
				if (itemPrice.compareTo(BigDecimal.ZERO) == 0)
				{
					newPrice = subTotal;
				}
				else
				{
					newPrice = itemPrice;
				}
				receipt.append("<tr align=\"left\">").append("<td >" + orderHeader.getTaxDisplayName3() + " ( " + orderHeader.getTaxRate3() + "% of " + newPrice + ")</td>		                        ")
						.append("<td align=\"right\">" + orderHeader.getPriceTax3() + "</td>	                                    ")
						.append("</tr>                                                               ");
			}
			if (orderHeader.getTaxDisplayName4() != null && !orderHeader.getTaxDisplayName4().equals("") && orderHeader.getTaxRate4() != new BigDecimal(0))
			{
				BigDecimal itemPrice = getItemLevelTax(em, orderHeader, foundLocation.getId(), orderHeader.getTaxName4());
				if (itemPrice.compareTo(BigDecimal.ZERO)== 0)
				{
					newPrice = subTotal;
				}
				else
				{
					newPrice = itemPrice;
				}
				receipt.append("<tr align=\"left\">").append("<td >" + orderHeader.getTaxDisplayName4() + " ( " + orderHeader.getTaxRate4() + "% of " + newPrice + ")</td>		                        ")
						.append("<td align=\"right\">" + orderHeader.getPriceTax4() + "</td>	                                    ")
						.append("</tr>                                                               ");
			}

			BigDecimal totalHeader = new BigDecimal(0);
			if (location.getIsRoundOffRequired() == 1)
			{
				totalHeader = orderHeader.getTotal();
			}
			else
			{
				totalHeader = orderHeader.getRoundOffTotal();
			}

			// adding order payment details
			BigDecimal total = new BigDecimal("0.00");
			BigDecimal totalTip = new BigDecimal("0.00");
			BigDecimal totalDue = new BigDecimal("0.00");
			BigDecimal cashTipAmount = new BigDecimal("0.00");
			BigDecimal creditTipAmount = new BigDecimal("0.00");

			totalDue = totalHeader.subtract(total);
			totalTip = cashTipAmount.add(creditTipAmount);
			receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
					.append("<td ><b>Total</b></td>		                                        ").append("<td align=\"right\"><b>" + totalHeader + "</b></td>	                                ")
					.append("</tr>                                                               ").append("<tr align=\"left\">                                                   ")
					.append("<td colspan=\"2\" align=\"center\"><b>&nbsp;</b></td>	                                ").append("</tr>                                                               ")
					.append("</tr>                                                               ");

			receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
					.append("<td ><b>Payment</b></td>		                                        ").append("<td align=\"right\"><b>" + "Amount Paid" + "</b></td>	                                ")
					.append("</tr>                                                               ");

			receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
					.append("<td ><b>Total</b></td>		                                        ").append("<td align=\"right\">" + totalHeader + "</td>	                                ")
					.append("</tr>                                                               ");

			receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
					.append("<td ><b>Total Tips</b></td>		                                        ").append("<td align=\"right\">" + totalTip + "</td>	                                ")
					.append("</tr>                                                               ");

			receipt.append("                                                                       ").append("<tr align=\"left\">                                                   ")
					.append("<td ><b>Total Due</b></td>		                                        ").append("<td align=\"right\">" + totalDue + "</td>	                                ")
					.append("</tr>                                                               ");
			// total due

			User user = (User) new CommonMethods().getObjectById("User", em,User.class, orderHeader.getUsersId());

			if (user != null)
			{
				receipt.append("<tr align=\"center\">                                                   ");
				if (user.getPhone() != null && user.getPhone().length() > 0)
					receipt.append("<td colspan=\"2\" align=\"center\">" + user.getPhone() + "</td>	 </tr>                               ");
				if (user.getEmail() != null && user.getEmail().length() > 0)

					receipt.append("<tr>	<td colspan=\"2\" align=\"center\">" + user.getEmail() + "</td>	 </tr>                                  ");

				if (user.getFirstName() != null && user.getFirstName().length() > 0)

					receipt.append("<tr>	<td colspan=\"2\" align=\"center\">" + user.getFirstName() + " " + user.getLastName() + "</td>	 </tr>                               ");

				// address set was of zero length so checked the condition of
				// size of make code work By Ap 2015-12-29

				logger.severe("- Address For sendEmailForOrder orderHeader.getId() " + orderHeader.getId() + " orderHeader.getAddressShipping() " + orderHeader.getAddressShipping());
				Address userAddress = null;

				if (orderHeader.getAddressShipping() != null)
				{
					userAddress = orderHeader.getAddressShipping();

				}
				else if (orderHeader.getAddressBilling() != null)
				{
					userAddress = orderHeader.getAddressBilling();

				}
				else if (user.getAddressesSet() != null && user.getAddressesSet().size() > 0)
				{
					List<Address> addressList = new ArrayList<Address>(user.getAddressesSet());
					if (addressList != null && addressList.get(0) != null)
					{
						userAddress = addressList.get(0);
					}
				}

				if (userAddress != null)
				{
					if (userAddress.getAddress1() != null)
						receipt.append("<tr>	<td colspan=\"2\" align=\"center\">" + userAddress.getAddress1() + "</td>	</tr>                                ");

					if (userAddress.getAddress2() != null)
						receipt.append(" <tr>	<td colspan=\"2\" align=\"center\">" + userAddress.getAddress2() + "</td></tr>	                                ");

					if (userAddress.getCity() != null)
						receipt.append("<tr>	<td colspan=\"2\" align=\"center\">" + userAddress.getCity() + "</td> </tr>	                                ");

					if (userAddress.getState() != null)
						receipt.append("<tr>	<td colspan=\"2\" align=\"center\">" + userAddress.getState() + "</td></tr>	                                ");

					if (userAddress.getZip() != null)
						receipt.append("<tr>	<td colspan=\"2\" align=\"center\">" + userAddress.getZip() + "</td>	 </tr>                               ");
				}

			}
			if (foundLocation.getDisplayQrcode() == 1)
				receipt.append("<tr align=\"center\">                                                   ")
						.append("<td colspan=\"2\" align=\"center\"><b>Please Scan Or Click</b></td>	                                ")
						.append("</tr>                                                               ").append("<tr align=\"center\">                                                   ")
						.append("<td colspan=\"2\" align=\"center\"><a href=\"" + feedbackUrl + "\"><img src=\"" + qrcodePath + "\"></a></td>	                                ")
						.append("</tr>                                                               ").append("<tr align=\"center\">                                                   ")
						.append("<td colspan=\"2\" align=\"center\"><b>For Feedback</b></td>	                                ")
						.append("</tr>                                                               ").append("</tr>                                                               ");

			receipt.append("<tr align=\"center\">                                                   ").append("<td colspan=\"2\" align=\"center\"><b>&nbsp;</b></td>	                                ")
					.append("</tr>                                                               ").append("</tr>                                                               ")
					.append("<tr align=\"center\">                                                   ");

			receipt.append("<td colspan=\"2\" align=\"center\"><b>Powered By</b></td>	                                ")
					.append("</tr>                                                               ")
					.append("<tr align=\"center\">                                                   ")
					.append("<td colspan=\"2\" align=\"center\"><a href=\"http://www.nirvanaxp.com\"><img src=\"https://live.nirvanaxp.com/images/nirvanaxp_Blackap.png\" width=\"136\" height=\"40\"></a></td>	                                ")
					.append("</tr>                                                               ");

			receipt.append("</table>   </div>                                                         ").append("</div>                                                              ")
					.append(" <br></div>		</body>                                                             ").append("</html>                                                             ");

		}

		return receipt;
	}

	/**
	 * Gets the cash register.
	 *
	 * @param locationsId
	 *            the locations id
	 * @param em
	 *            the em
	 * @param isDateWise
	 *            the is date wise
	 * @param activeBatchDetail
	 *            the active batch detail
	 * @return the cash register
	 * @throws ParseException
	 *             the parse exception
	 */
	List<RegisterInfo> getCashRegister(String locationsId, EntityManager em, boolean isDateWise, BatchDetail activeBatchDetail) throws ParseException
	{

		RegisterInfo revenue = null;
		List<RegisterInfo> registerInfoList = new ArrayList<RegisterInfo>();

		Long endTime;
		if (activeBatchDetail.getCloseTime() != 0)
		{
			endTime = activeBatchDetail.getCloseTime();
		}
		else
		{
			endTime = new TimezoneTime().getGMTTimeInMilis();
		}

		@SuppressWarnings("unchecked")
		List<Object> resultListPaidInOut = em
				.createNativeQuery(
						"select distinct register_id from cash_register_running_balance crrb " + " left join printers p on p.id = crrb.register_id "
								+ " where crrb.created between ? and ? and p.locations_id = '" + locationsId+"'").setParameter(1, new Date(activeBatchDetail.getStartTime()))
				.setParameter(2, new Date(endTime)).getResultList();

		if (resultListPaidInOut != null)
		{
			// if this has primary key not 0
			for (Object object : resultListPaidInOut)
			{
				String registerId = (String) object;
				Printer printer = (Printer) new CommonMethods().getObjectById("Printer", em,Printer.class, registerId);
				revenue = new RegisterInfo();

				if (printer != null)
					revenue.setName(printer.getDisplayName());

				CashRegisterRunningBalance resultSet = null;
				List<CashRegisterRunningBalance> result = null;
				try
				{
					String queryString = "select l from CashRegisterRunningBalance l where l.registerId =? order by id asc ";
					TypedQuery<CashRegisterRunningBalance> query = em.createQuery(queryString, CashRegisterRunningBalance.class).setParameter(1, registerId);
					result = query.getResultList();
				}
				catch (Exception e)
				{
					logger.severe(e);
				}
				for (CashRegisterRunningBalance c : result)
				{
					resultSet = c;
					continue;
				}
				if (resultSet != null)
					revenue.setTotal(resultSet.getRunningBalance() + "");

				registerInfoList.add(revenue);

			}

		}

		return registerInfoList;
	}

	/**
	 * The Class RegisterInfo.
	 */
	class RegisterInfo
	{

		/** The name. */
		private String name;

		/** The total. */
		private String total;

		/**
		 * Gets the name.
		 *
		 * @return the name
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Sets the name.
		 *
		 * @param name
		 *            the new name
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * Gets the total.
		 *
		 * @return the total
		 */
		public String getTotal()
		{
			return total;
		}

		/**
		 * Sets the total.
		 *
		 * @param total
		 *            the new total
		 */
		public void setTotal(String total)
		{
			this.total = total;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return "RegisterInfo [name=" + name + ", total=" + total + "]";
		}

	}

	public CalculatedPaymentSummary getPaymentSummary(EntityManager em, String locationId, List<OrderPaymentDetail> orderList, BatchDetail actBatchDetail, boolean needToConsiderTax)
	{
		CalculatedPaymentSummary calculatedPaymentSummary = new CalculatedPaymentSummary();
		try
		{
			if (orderList != null)
			{
				BigDecimal totalCashAmmount = new BigDecimal(0);
				BigDecimal totalCardAmmount = new BigDecimal(0);
				BigDecimal totalCreditTermAmmount = new BigDecimal(0);
				BigDecimal cashTipAmount = new BigDecimal(0);
				BigDecimal cardTipAmount = new BigDecimal(0);
				BigDecimal creditTermTipAmount = new BigDecimal(0);
				for (OrderPaymentDetail orderPaymentDetail : orderList)
				{

					PaymentMethod paymentMethod = orderPaymentDetail.getPaymentMethod();

					if (paymentMethod.getPaymentMethodTypeId() == null)
					{
						paymentMethod = getPaymentMethod(em, locationId, orderPaymentDetail.getPaymentMethod().getId());
					}
					if (orderPaymentDetail != null && actBatchDetail != null && orderPaymentDetail.getNirvanaXpBatchNumber().equals(actBatchDetail.getId()) && paymentMethod != null)
					{
						String paymentMethodTypeId = paymentMethod.getPaymentMethodTypeId();
						if (paymentMethodTypeId.equals(getPaymentMethodType(em, locationId, "Cash").getId()))
						{
							totalCashAmmount = totalCashAmmount.add(calculateCashTotalForOPD(em, locationId, orderPaymentDetail, needToConsiderTax, paymentMethod));
						}
						else if (paymentMethodTypeId.equals(getPaymentMethodType(em, locationId, "Credit Card").getId())
								|| paymentMethodTypeId.equals(getPaymentMethodType(em, locationId, "Manual Credit Card").getId())
								|| paymentMethodTypeId.equals(getPaymentMethodType(em, locationId, "Manual CC Entry").getId()))
						{
							totalCardAmmount = totalCardAmmount.add(calculateCardTotalForOPD(em, locationId, orderPaymentDetail, needToConsiderTax));
						}
						else if (paymentMethodTypeId.equals(getPaymentMethodType(em, locationId, "Credit Term").getId()))
						{
							totalCreditTermAmmount = totalCreditTermAmmount.add(calculateCreditTermTotalForOPD(em, locationId, orderPaymentDetail, needToConsiderTax,paymentMethod));
						}

						if (orderPaymentDetail.getCashTipAmt().doubleValue() > 0)
						{
							cashTipAmount = cashTipAmount.add(calculateTotalCashTipForOPD(em, locationId, orderPaymentDetail));
						}
						if (orderPaymentDetail.getCreditcardTipAmt().doubleValue() > 0)
						{
							cardTipAmount = cardTipAmount.add(calculateTotalCardTipForOPD(em, locationId, orderPaymentDetail));
						}
						if (orderPaymentDetail.getCreditTermTip().doubleValue() > 0)
						{
							creditTermTipAmount = creditTermTipAmount.add(calculateTotalCreditTermTipForOPD(em, locationId, orderPaymentDetail));
						}
					}
				}
				calculatedPaymentSummary.setCardTipAmount(cardTipAmount);
				calculatedPaymentSummary.setCashTipAmount(cashTipAmount);
				calculatedPaymentSummary.setCreditTermTipAmount(creditTermTipAmount);
				calculatedPaymentSummary.setTotalCardAmmount(totalCardAmmount);
				calculatedPaymentSummary.setTotalCashAmmount(totalCashAmmount);
				calculatedPaymentSummary.setTotalCreditTermAmmount(totalCreditTermAmmount);
			}

			return calculatedPaymentSummary;
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return calculatedPaymentSummary;
	}

	public BigDecimal calculateCashTotalForOPD(EntityManager em, String locationId, OrderPaymentDetail orderPaymentDetail, boolean needToConsiderTax, PaymentMethod paymentMethod)
	{

		try
		{
			BigDecimal totalAmmount = new BigDecimal(0);

			int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

			if (paymentMethod != null)
			{
				String paymentMethodTypeId = paymentMethod.getPaymentMethodTypeId();

				if (orderPaymentDetail.getIsRefunded() == 0 && paymentMethodTypeId.equals(getPaymentMethodType(em, locationId, "Cash").getId())
						&& transectionTypeId != getPaymentTransactionType(em, locationId, "Refund").getId())
				{

					if (orderPaymentDetail.getAmountPaid() != null)
					{
						totalAmmount = orderPaymentDetail.getAmountPaid();

						if (!needToConsiderTax)
						{
							BigDecimal totalTax = orderPaymentDetail.getPriceGratuity().add(orderPaymentDetail.getPriceTax1()).add(orderPaymentDetail.getPriceTax2())
									.add(orderPaymentDetail.getPriceTax3()).add(orderPaymentDetail.getPriceTax4());
							totalAmmount = totalAmmount.subtract(totalTax);

						}
					}

				}
			}

			return totalAmmount;
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	public BigDecimal calculateCardTotalForOPD(EntityManager em, String locationId, OrderPaymentDetail orderPaymentDetail, boolean needToConsiderTax)
	{

		try
		{
			BigDecimal totalAmmount = new BigDecimal(0);

			if ((orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Auth").getId()
					|| orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "Manual CC Auth").getId() || (orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(
					em, locationId, "Tip Saved").getId())
					&& (orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Auth").getId()))
					&& orderPaymentDetail.getIsRefunded() != 1)
			{

				totalAmmount = orderPaymentDetail.getAmountPaid();
				if (!needToConsiderTax)
				{
					BigDecimal totalTax = orderPaymentDetail.getPriceGratuity().add(orderPaymentDetail.getPriceTax1()).add(orderPaymentDetail.getPriceTax2()).add(orderPaymentDetail.getPriceTax3())
							.add(orderPaymentDetail.getPriceTax4());
					totalAmmount = totalAmmount.subtract(totalTax);
				}

			}
			else if (orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Pre Capture").getId()
					&& orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Force").getId() && orderPaymentDetail.getIsRefunded() != 1)
			{
				totalAmmount = orderPaymentDetail.getAmountPaid();
				if (!needToConsiderTax)
				{
					BigDecimal totalTax = orderPaymentDetail.getPriceGratuity().add(orderPaymentDetail.getPriceTax1()).add(orderPaymentDetail.getPriceTax2()).add(orderPaymentDetail.getPriceTax3())
							.add(orderPaymentDetail.getPriceTax4());
					totalAmmount = totalAmmount.subtract(totalTax);
				}

			}
			else if (orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Settled").getId()
					&& (orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Capture").getId() || orderPaymentDetail.getPaymentTransactionType()
							.getId() == getPaymentTransactionType(em, locationId, "CaptureAll").getId()))
			{
				totalAmmount = orderPaymentDetail.getAmountPaid();
				if (!needToConsiderTax)
				{
					BigDecimal totalTax = orderPaymentDetail.getPriceGratuity().add(orderPaymentDetail.getPriceTax1()).add(orderPaymentDetail.getPriceTax2()).add(orderPaymentDetail.getPriceTax3())
							.add(orderPaymentDetail.getPriceTax4());
					totalAmmount = totalAmmount.subtract(totalTax);
				}
				
			}

			return totalAmmount;
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	public BigDecimal calculateCreditTermTotalForOPD(EntityManager em, String locationId, OrderPaymentDetail orderPaymentDetail, boolean needToConsiderTax,PaymentMethod paymentMethod)
	{

		try
		{
			BigDecimal totalAmmount = new BigDecimal(0);
			int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

			if (paymentMethod != null)
			{
				String paymentMethodTypeId = paymentMethod.getPaymentMethodTypeId();

				PaymentMethodType paymentMethodType = getPaymentMethodTypeById(em, locationId, paymentMethodTypeId);
				PaymentType paymentType = null;
				if (paymentMethodType != null)
				{
					paymentType = getPaymentType(em, paymentMethodType.getPaymentTypeId());
				}

				if ((orderPaymentDetail.getIsRefunded() == 0
						&& (paymentMethodTypeId.equals(getPaymentMethodType(em, locationId, "Credit Term").getId()) || (paymentType != null && paymentType.getName().equals("Credit Term"))) && transectionTypeId != getPaymentTransactionType(
							em, locationId, "Credit Refund").getId()))
				{
					if (orderPaymentDetail.getAmountPaid() != null)
					{
						totalAmmount = orderPaymentDetail.getAmountPaid();
						if (!needToConsiderTax)
						{
							BigDecimal totalTax = orderPaymentDetail.getPriceGratuity().add(orderPaymentDetail.getPriceTax1()).add(orderPaymentDetail.getPriceTax2())
									.add(orderPaymentDetail.getPriceTax3()).add(orderPaymentDetail.getPriceTax4());
							totalAmmount = totalAmmount.subtract(totalTax);
						}
					}

				}
			}
			return totalAmmount;
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	public BigDecimal calculateTotalCashTipForOPD(EntityManager em, String locationId, OrderPaymentDetail orderPaymentDetail)
	{
		try
		{
			BigDecimal tipAmount = new BigDecimal(0);
			int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();

			if (orderPaymentDetail.getIsRefunded() == 0 && transectionTypeId != getPaymentTransactionType(em, locationId, "Void").getId()
					&& transectionTypeId != getPaymentTransactionType(em, locationId, "Refund").getId() && getPaymentTransactionType(em, locationId, "Credit Refund") != null
					&& transectionTypeId != getPaymentTransactionType(em, locationId, "Credit Refund").getId())
			{

				if (transectionTypeId == getPaymentTransactionType(em, locationId, "Sale").getId()
						|| (getPaymentTransactionType(em, locationId, "Credit") != null && transectionTypeId == getPaymentTransactionType(em, locationId, "Credit").getId()))
				{
					tipAmount = tipAmount.add(orderPaymentDetail.getCashTipAmt());

				}
				else if ((getTransactionStatus(em, locationId, "CC Pre Capture") != null
						&& orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Pre Capture").getId() && orderPaymentDetail.getPaymentTransactionType()
						.getId() == getPaymentTransactionType(em, locationId, "Force").getId()))
				{
					tipAmount = tipAmount.add(orderPaymentDetail.getCashTipAmt());
				}
				else if ((getTransactionStatus(em, locationId, "CC Settled") != null && orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Settled").getId())
						&& (orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Capture").getId() || orderPaymentDetail.getPaymentTransactionType()
								.getId() == getPaymentTransactionType(em, locationId, "CaptureAll").getId()))
				{
					tipAmount = tipAmount.add(orderPaymentDetail.getCashTipAmt());
				}
				else if (((getTransactionStatus(em, locationId, "Tip Saved") != null && orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "Tip Saved").getId())
						|| (getTransactionStatus(em, locationId, "CC Auth") != null && orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Auth").getId()) || (getTransactionStatus(
						em, locationId, "Manual CC Auth") != null && orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "Manual CC Auth").getId()))
						&& orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Auth").getId())
				{
					tipAmount = tipAmount.add(orderPaymentDetail.getCashTipAmt());
				}
				else if (orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "discount").getId())
				{
					tipAmount = tipAmount.add(orderPaymentDetail.getCashTipAmt());
				}
			}

			return tipAmount;
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	public BigDecimal calculateTotalCardTipForOPD(EntityManager em, String locationId, OrderPaymentDetail orderPaymentDetail)
	{
		try
		{
			BigDecimal tipAmount = new BigDecimal(0);

			if ((orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Auth").getId()
					|| orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "Manual CC Auth").getId() || orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(
					em, locationId, "Tip Saved").getId())
					&& orderPaymentDetail.getIsRefunded() != 1)
			{
				tipAmount = tipAmount.add(orderPaymentDetail.getCreditcardTipAmt());
			}
			else if (orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Pre Capture").getId()
					&& orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Force").getId() && orderPaymentDetail.getIsRefunded() != 1)
			{
				tipAmount = tipAmount.add(orderPaymentDetail.getCreditcardTipAmt());
			}
			else if (orderPaymentDetail.getTransactionStatus().getId() == getTransactionStatus(em, locationId, "CC Settled").getId()
					&& (orderPaymentDetail.getPaymentTransactionType().getId() == getPaymentTransactionType(em, locationId, "Capture").getId() || orderPaymentDetail.getPaymentTransactionType()
							.getId() == getPaymentTransactionType(em, locationId, "CaptureAll").getId()))
			{
				tipAmount = tipAmount.add(orderPaymentDetail.getCreditcardTipAmt());
			}

			return tipAmount;
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	public BigDecimal calculateTotalCreditTermTipForOPD(EntityManager em, String locationId, OrderPaymentDetail orderPaymentDetail)
	{
		try
		{
			BigDecimal tipAmount = new BigDecimal(0);
			int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();
			if (orderPaymentDetail.getIsRefunded() == 0 && getPaymentTransactionType(em, locationId, "Credit Refund") != null
					&& transectionTypeId != getPaymentTransactionType(em, locationId, "Credit Refund").getId())
			{

				if ((getPaymentTransactionType(em, locationId, "Credit") != null && transectionTypeId == getPaymentTransactionType(em, locationId, "Credit").getId()))
				{
					tipAmount = tipAmount.add(orderPaymentDetail.getCreditTermTip());

				}

			}

			return tipAmount;
		}
		catch (Exception e)
		{
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}
	public StringBuilder createEmailReceiptForAdditionalQuestion(EntityManager em, HttpServletRequest httpRequest, String orderId, int takeoutDelivery, boolean isFromReceived,boolean quotation)
 {

		// todo shlok need
		// modularise method
		String locationpath = null;
		String qrcodePath = null;
		String serverURL = null;
		String feedbackUrl = null;

		try {
			locationpath = ConfigFileReader.getWebsiteLogoPath();
			qrcodePath = ConfigFileReader.getQRCodeUploadPathFromFile();
			serverURL = ConfigFileReader.getQRCodeServerName();
			feedbackUrl = ConfigFileReader.getAdminFeedbackURL();
		} catch (Exception e) {
			logger.severe(httpRequest,
					"getWebsiteLogoPath not found in database for" + orderId);
		}

		
		// new OrderManagementServiceBean(httpRequest);
		OrderManagementServiceBean managementServiceBean = new OrderManagementServiceBean();
		String discountDisplayName = "";

		OrderHeader orderHeader = managementServiceBean.getOrderById(em,
				orderId);
		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class,
				orderHeader.getOrderSourceId());
		OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class,
				orderSource.getOrderSourceGroupId());

		Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class,
				orderHeader.getLocationsId());
		Address localAddress = null;
		// com.nirvanaxp.global.types.entities.Address address = null;
		List<Location> result = null;
		Location foundLocation = null;
		if (location != null && location.getLocationsId() != null) {
			String queryString = "select l from Location l where  (l.locationsId ='0' or l.locationsId is null ) and l.businessId="
					+ location.getBusinessId() + " order by l.id asc";
			TypedQuery<Location> query = em.createQuery(queryString,
					Location.class);
			result = query.getResultList();
			if (result != null && result.size() > 0) {
				foundLocation = result.get(0);
			}
			localAddress = foundLocation.getAddress();
		} else {
			localAddress = location.getAddress();
			foundLocation = location;
		}
		String date = new TimezoneTime().getCurrentDate(em,
				foundLocation.getId());
		String time = new TimezoneTime().getCurrentTime(em,
				foundLocation.getId());
		
		String datetime = new TimezoneTime().getDateTimeFromGMTToLocation(em, orderHeader.getScheduleDateTime(), foundLocation.getId());
		SimpleDateFormat toformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat fromFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm aa");

		try
		{
			java.util.Date myDate = toformatter.parse(datetime);
			datetime = fromFormatter.format(myDate);
		}
		catch (ParseException e)
		{
			logger.severe("Unable to parse date", datetime, "while generating pdf receipt");
		}

		qrcodePath = "http://" + serverURL + orderHeader.getQrcode() + ".png";
		feedbackUrl = feedbackUrl + "refno=" + orderHeader.getReferenceNumber()
				+ "&order_id=" + orderHeader.getId();

		StringBuilder receipt = null;
		if (orderHeader != null) {

			receipt = new StringBuilder()
					.append("<html> ")
					.append("<head>")

					.append("</head> ")
					.append("<body> ")
					.append(" ")
					.append("<center> ")
					.append(" ")
					.append("<div id=\"header\"> ")
					.append("<img src=\"" + locationpath
							+ foundLocation.getImageUrl() + "\" alt=\""
							+ foundLocation.getName() + "\"><br> ");
			if(orderSourceGroup!=null && orderSourceGroup.getName().equals("Catering")){
				receipt.append(foundLocation.getName() + " ").append("</div>")
				.append("<div>")
				.append("Your "+orderSourceGroup.getDisplayName()+" order will be ready by " + orderHeader.getStartDate()  + "").append("</div> ");
			}else{
				receipt.append(foundLocation.getName() + " ").append("</div>")
				.append("<div id=\"date\">")
				.append(" " + date + " " + time + "").append("</div> ");
			}
			
			if (quotation) {
				receipt.append(" <div>Quote # " + orderHeader.getOrderNumber()
						+ " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "
						+ orderSourceGroup.getDisplayName() + " </div> ");

			}
			receipt.append(" <div>"+"-------------------------------------------"+ " </div> ");
			receipt.append(" <div>"+"Order Additional Information"+ " </div> ");

			receipt.append(" <div>"+"-------------------------------------------"+ " </div> ");

			receipt.append("</center> ");
			
			receipt.append(" </div> <div align=\"center\"> ");
			receipt.append("<table ><tr><td colspan=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br/></td>");

			String queryString = "select ft.field_type_name,oaq.question,aqa.answer_value "
					+ " from additional_question_answer aqa "
					+ " join order_additional_question oaq on oaq.id = aqa.question_id "
					+ " join field_type ft on ft.id=oaq.field_type_id where order_header_id=? order by oaq.display_sequence ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString)
					.setParameter(1, orderId).getResultList();
			for (Object[] objRow : resultList) {

				String fieldTypeName = (String) objRow[0];
				String question = (String) objRow[1];
				String answer = (String) objRow[2];

				if(answer==null || answer.equalsIgnoreCase("null")){
					answer="";
				}
				if (fieldTypeName != null && fieldTypeName.equals("Check Box")) {
						if (answer!=null && answer.equals("1")) {
							answer = "<b>Yes";
						} 
//						else if(answer!=null && (answer.length()>1 || answer.length()==0)) {
//							answer = "<b>"+answer;
//						} 
						else {
							answer = "<b>No";
						}
					receipt.append("<tr align=\"left\"> ")
							.append("<td >&nbsp;&nbsp;" + question + " &nbsp;:"
									+ answer + "</td>	").append("</tr>");
				}
				if (fieldTypeName != null && fieldTypeName.equals("Test Box")) {
					if (answer != null) {
						if (answer.equals("1")) {
							answer = "<b>Yes";
						}
						else if(answer!=null && (answer.length()>1 || answer.length()==0)) {
							answer = "<b>"+answer;
						} else {
							answer = "<b>No";
						}
					}
					receipt.append("<tr align=\"left\"> ")
							.append("<td >&nbsp;&nbsp;" + question + " &nbsp;:"
									+ answer + "</td>	").append("</tr>");
				}
				if (fieldTypeName != null
						&& fieldTypeName.equals("Textarea Box")) {
					if (answer != null && answer.length() > 0) {
						receipt.append("<tr align=\"left\"> ")
								.append("<td >&nbsp;&nbsp;" + question + " &nbsp;: <b>" + answer + "</b></td>	")
								.append("</tr>");
					}

				}
			}
			
			receipt.append("<tr align=\"center\"> ")
			.append("<td colspan=\"2\" align=\"center\"><b>&nbsp;</b></td>	")
			.append("</tr> ");
			
			
			
			receipt.append("<tr align=\"center\"> ")
			.append("<td colspan=\"2\" align=\"center\"><b>&nbsp;</b></td>	")
			.append("</tr> ");

			receipt.append("<tr align=\"center\"> ")
					.append("<td colspan=\"2\" align=\"center\"><b>&nbsp;</b></td>	")
					.append("</tr> ").append("</tr> ")
					.append("<tr align=\"center\"> ");

			receipt.append(
					"<td colspan=\"2\" align=\"center\"><b>Powered By</b></td>	")
					.append("</tr> ")
					.append("<tr align=\"center\"> ")
					.append("<td colspan=\"2\" align=\"center\"><a href=\"http://www.nirvanaxp.com\"><img src=\"https://live.nirvanaxp.com/images/nirvanaxp_Blackap.png\" width=\"136\" height=\"40\"></a></td>	")
					.append("</tr> ");

			receipt.append("</table> </div> ").append("</div> ")
					.append(" <br></div>	</body> ").append("</html> ");

		}

		return receipt;
	}
	

}
