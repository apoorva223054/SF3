package com.nirvanaxp.services.jaxrs;

import java.math.BigDecimal;

public class FeedbackReport {
	private String orderNumber;
	private String firstName;
	private String lastName;
	private String date;
	private String time;
	private Integer partySize;
	private String userId;
	private String phoneOrEmail;
	private String comment;
	private String tableName;
	private String feedbackTypeName;
	private String imageName;
	private BigDecimal rating;
	private String managerResponse;
	private String dateOfBirth;
	private String dateOfAnniversary;
	private String created;
	
	private String id;
	private String email;
	private String reservationStatus;
	private String phoneNumber;
	private String requestName;
		
	public String getOrderNumber() {
		 if(orderNumber != null && (orderNumber.length()==0 || orderNumber.equals("0"))){return null;}else{	return orderNumber;}
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Integer getPartySize() {
		return partySize;
	}
	public void setPartySize(Integer partySize) {
		this.partySize = partySize;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPhoneOrEmail() {
		return phoneOrEmail;
	}
	public void setPhoneOrEmail(String phoneOrEmail) {
		this.phoneOrEmail = phoneOrEmail;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getFeedbackTypeName() {
		return feedbackTypeName;
	}
	public void setFeedbackTypeName(String feedbackTypeName) {
		this.feedbackTypeName = feedbackTypeName;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public BigDecimal getRating() {
		return rating;
	}
	public void setRating(BigDecimal rating) {
		this.rating = rating;
	}
	public String getManagerResponse() {
		return managerResponse;
	}
	public void setManagerResponse(String managerResponse) {
		this.managerResponse = managerResponse;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getDateOfAnniversary() {
		return dateOfAnniversary;
	}
	public void setDateOfAnniversary(String dateOfAnniversary) {
		this.dateOfAnniversary = dateOfAnniversary;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getReservationStatus() {
		return reservationStatus;
	}
	public void setReservationStatus(String reservationStatus) {
		this.reservationStatus = reservationStatus;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getRequestName() {
		return requestName;
	}
	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	
	
	

}
