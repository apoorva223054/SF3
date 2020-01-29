package com.nirvanaxp.types.entities.user;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.global.types.entities.partners.TimezoneTime;
import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
@Entity
@Table(name = "heb_employee_exceptional_report")
@XmlRootElement(name = "heb_employee_exceptional_report")
public class HEBEmployeeExceptionalReport extends POSNirvanaBaseClass{

	@Column(name = "date")
	private Date date;

	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "username")
	private String username;

	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "employee_status")
	private String employeeStatus;

	@Column(name = "nirvana_xp_status")
	private String nirvanaXpStatus;
	
	@Column(name = "term_date")
	private Date termDate;
	
	@Column(name = "job_title")
	private String jobTitle;
	
	@Column(name = "error_message")
	private String errorMessage;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}

	public String getNirvanaXpStatus() {
		return nirvanaXpStatus;
	}

	public void setNirvanaXpStatus(String nirvanaXpStatus) {
		this.nirvanaXpStatus = nirvanaXpStatus;
	}

	public Date getTermDate() {
		return termDate;
	}

	public void setTermDate(Date termDate) {
		this.termDate = termDate;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "HEBEmployeeExceptionalReport [date=" + date + ", user_id=" + userId + ", username=" + username
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", employeeStatus=" + employeeStatus
				+ ", nirvanaXpStatus=" + nirvanaXpStatus + ", termDate=" + termDate + ", jobTitle=" + jobTitle + "]";
	}
	
	public HEBEmployeeExceptionalReport insertHEBEmployeeExceptionalReport( String date,String employeeStatus,String firstName,String lastName,
			String jobTitle,String nirvanaXpStatus,String termDate,String userId,String status,String userName,String errorMessage) throws ParseException{
		Date reportDate=null;
		Date reportTermDate= null;
		if(date !=null && date.length()>0){
			DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			reportDate = formatter.parse(date);
		}
		if(termDate !=null && termDate.length()>0){
			DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
			reportTermDate =formatter2.parse(termDate);
		}
	 
		HEBEmployeeExceptionalReport employeeExceptionalReport = new HEBEmployeeExceptionalReport();
		employeeExceptionalReport.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeExceptionalReport.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		employeeExceptionalReport.setUpdatedBy("21");
		employeeExceptionalReport.setCreatedBy("21");
		employeeExceptionalReport.setDate(reportDate);
		employeeExceptionalReport.setEmployeeStatus(employeeStatus);
		employeeExceptionalReport.setFirstName(firstName);
		employeeExceptionalReport.setLastName(lastName);
		employeeExceptionalReport.setJobTitle(jobTitle);
		employeeExceptionalReport.setNirvanaXpStatus(nirvanaXpStatus);
		employeeExceptionalReport.setTermDate(reportTermDate);
		employeeExceptionalReport.setUserId(userId);
		employeeExceptionalReport.setStatus(status);
		employeeExceptionalReport.setUsername(userName);
		employeeExceptionalReport.setErrorMessage(errorMessage);
		
		return employeeExceptionalReport;
	}
}
