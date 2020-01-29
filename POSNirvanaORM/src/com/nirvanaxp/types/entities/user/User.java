/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.user;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.tip.EmployeeMaster;

/**
 * The persistent class for the users database table.
 * 
 */
@Entity
@Table(name = "users")
@XmlRootElement(name = "users")
public class User implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Column(length = 2000)
	private String comments;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(length = 10)
	private String dateofbirth;

	@Column(length = 64)
	private String email;

	@Column(name = "auth_pin", length = 64)
	private String authPin;

	@Column(name = "first_name", nullable = false, length = 40)
	private String firstName;

	@Column(name = "last_login_ts")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLoginTs;

	@Column(name = "last_name", nullable = false, length = 40)
	private String lastName;

	@Column(nullable = false, length = 255)
	private String password;

	@Column(name = "phone")
	private String phone;

	@Column(name = "status", nullable = false, length = 1)
	private String status;

	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(nullable = false, length = 32)
	private String username;

	@Column(name = "user_color")
	private String userColor;

	@Column(name = "visit_count")
	private int visitCount;

	@Column(name = "country_id")
	private int countryId;
	

	@Column(name = "is_tipped_employee")
	int isTippedEmployee;
	
	private transient EmployeeMaster employeeMaster;

	public EmployeeMaster getEmployeeMaster()
	{
		return employeeMaster;
	}

	public void setEmployeeMaster(EmployeeMaster employeeMaster)
	{
		this.employeeMaster = employeeMaster;
	}
	
	// uni-directional many-to-many association to Location
	@JoinColumn(name = "users_id")
	@OneToMany(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<UsersToLocation> usersToLocations;

	// uni-directional many-to-many association to Discount
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "users_to_address", joinColumns =
	{ @JoinColumn(name = "users_id", nullable = false) }, inverseJoinColumns =
	{ @JoinColumn(name = "address_id", nullable = false) })
	private Set<Address> addressesSet;

	// uni-directional many-to-many association to Role
	@JoinColumn(name = "users_id")
	@OneToMany(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<UsersToRole> usersToRoles;

	// bi-directional many-to-many association to SocialMedia
	@JoinColumn(name = "users_id")
	@OneToMany(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<UsersToSocialMedia> usersToSocialMedias;

	@Column(name = "global_users_id")
	private String globalUsersId;
	
	@Column(name = "is_allowed_login")
	private int isAllowedLogin;

	@JoinColumn(name = "users_id")
	@OneToMany(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<UsersToFeebackDetail> usersToFeebackDetail;

	@Column(name = "company_name")
	private String companyName;
	
	@Column(name = "tax_no")
	private String taxNo;
	
	@Column(name = "tax_display_name")
	private String taxDisplayName;
	
	private transient List<UsersToDiscount> usersToDiscounts;
	

	public User(String createdBy, String dateofbirth, String email, String authPin, String firstName, String lastName, String password, String phone, String status, String updatedBy,
			String username, String globalUsersId,int countryId)
	{
		super();
		this.createdBy = createdBy;
		this.dateofbirth = dateofbirth;
		this.email = email;
		this.authPin = authPin;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.phone = phone;
		this.status = status;
		this.updatedBy = updatedBy;
		this.username = username;
		this.globalUsersId = globalUsersId;
		this.countryId = countryId;
	}

	public User()
	{

	}

	/*public User(Object[] obj, int startIndex)
	{

		int index = startIndex;
		if (obj[index] != null)
		{
			setId((Integer) obj[index]);
		}
		index++;
		if (obj[index] != null)
		{
			setFirstName((String) obj[index]);
		}
		index++;
		if (obj[index] != null)
		{
			setLastName((String) obj[index]);
		}
		index++;
		// USERS
		if (obj[index] != null)
		{
			setEmail((String) obj[index]);
		}
		index++;
		if (obj[index] != null)
		{
			setPhone((String) obj[index]);
		}
		index++;
		if (obj[index] != null)
		{
			setUsername((String) obj[index]);
		}
		index++;
		index++;
		if (obj[index] != null)
		{
			setGlobalUsersId((Integer) obj[index]);
		}

	}*/

	

	public String getComments()
	{
		return this.comments;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setComments(String comments)
	{
		this.comments = comments;
	}

	public String getDateofbirth()
	{
		return this.dateofbirth;
	}

	public void setDateofbirth(String dateofbirth)
	{
		this.dateofbirth = dateofbirth;
	}

	public String getEmail()
	{
		return this.email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getFirstName()
	{
		return this.firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return this.lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getPassword()
	{
		return this.password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getPhone()
	{
		return this.phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getUsername()
	{
		return this.username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Set<UsersToLocation> getUsersToLocations()
	{
		return usersToLocations;
	}

	public void setUsersToLocations(Set<UsersToLocation> usersToLocations)
	{
		this.usersToLocations = usersToLocations;
	}

	public Set<UsersToRole> getUsersToRoles()
	{
		return usersToRoles;
	}

	public void setUsersToRoles(Set<UsersToRole> usersToRoles)
	{
		this.usersToRoles = usersToRoles;
	}

	public Set<UsersToSocialMedia> getUsersToSocialMedias()
	{
		return usersToSocialMedias;
	}

	public void setUsersToSocialMedias(Set<UsersToSocialMedia> usersToSocialMedias)
	{
		this.usersToSocialMedias = usersToSocialMedias;
	}

	public String getAuthPin()
	{
		return authPin;
	}

	public void setAuthPin(String authPin)
	{
		this.authPin = authPin;
	}

	public String getGlobalUsersId()
	{
		return globalUsersId;
	}

	public void setGlobalUsersId(String globalUsersId)
	{
		this.globalUsersId = globalUsersId;
	}

	public Set<UsersToFeebackDetail> getUsersToFeebackDetail()
	{
		return usersToFeebackDetail;
	}

	public void setUsersToFeebackDetail(Set<UsersToFeebackDetail> usersToFeebackDetail)
	{
		this.usersToFeebackDetail = usersToFeebackDetail;
	}

	public User getUserByResultSetForOrder(Object[] objRow, User user, int index) throws SQLException
	{

		if ((String) objRow[index] != null)
		{
			user.setId((String) objRow[index]);
			index++;
			user.setFirstName((String) objRow[index]);
			index++;
			user.setLastName((String) objRow[index]);
			index++;
			user.setEmail((String) objRow[index]);
			index++;
			user.setPhone((String) objRow[index]);
			index++;
			user.setUsername((String) objRow[index]);
			index++;
			if(objRow[index]!=null)
			user.setGlobalUsersId((String) objRow[index]);
		}

		return user;
	}


	public String getUserColor()
	{
		return userColor;
	}

	public void setUserColor(String userColor)
	{
		this.userColor = userColor;
	}

	public Set<Address> getAddressesSet()
	{
		return addressesSet;
	}

	public void setAddressesSet(Set<Address> addressesSet)
	{
		this.addressesSet = addressesSet;
	}

	/**
	 * @return the visitCount
	 */
	public int getVisitCount()
	{
		return visitCount;
	}

	/**
	 * @param visitCount
	 *            the visitCount to set
	 */
	public void setVisitCount(int visitCount)
	{
		this.visitCount = visitCount;
	}

	public Date getCreated()
	{
		return created;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public Date getLastLoginTs()
	{
		return lastLoginTs;
	}

	public void setLastLoginTs(Date lastLoginTs)
	{
		this.lastLoginTs = lastLoginTs;
	}

	public Date getUpdated()
	{
		return updated;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}
	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}
	

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getTaxNo() {
		return taxNo;
	}

	public void setTaxNo(String taxNo) {
		this.taxNo = taxNo;
	}

	public String getTaxDisplayName() {
		return taxDisplayName;
	}

	public void setTaxDisplayName(String taxDisplayName) {
		this.taxDisplayName = taxDisplayName;
	}
	
	public int getIsAllowedLogin() {
		return isAllowedLogin;
	}

	public void setIsAllowedLogin(int isAllowedLogin) {
		this.isAllowedLogin = isAllowedLogin;
	}

	public int getIsTippedEmployee()
	{
		return isTippedEmployee;
	}

	public void setIsTippedEmployee(int isTippedEmployee)
	{
		this.isTippedEmployee = isTippedEmployee;
	}

	@Override
	public String toString()
	{
		return "User [id=" + id + ", comments=" + comments + ", created=" + created + ", createdBy=" + createdBy + ", dateofbirth=" + dateofbirth + ", email=" + email + ", authPin=" + authPin
				+ ", firstName=" + firstName + ", lastLoginTs=" + lastLoginTs + ", lastName=" + lastName + ", password=" + password + ", phone=" + phone + ", status=" + status + ", updated="
				+ updated + ", updatedBy=" + updatedBy + ", username=" + username + ", userColor=" + userColor + ", visitCount=" + visitCount + ", countryId=" + countryId + ", isTippedEmployee="
				+ isTippedEmployee + ", usersToLocations=" + usersToLocations + ", addressesSet=" + addressesSet + ", usersToRoles=" + usersToRoles + ", usersToSocialMedias=" + usersToSocialMedias
				+ ", globalUsersId=" + globalUsersId + ", isAllowedLogin=" + isAllowedLogin + ", usersToFeebackDetail=" + usersToFeebackDetail + ", companyName=" + companyName + ", taxNo=" + taxNo
				+ ", taxDisplayName=" + taxDisplayName + "]";
	}
	
	public List<UsersToDiscount> getUsersToDiscounts() {
		return usersToDiscounts;
	}

	public void setUsersToDiscounts(List<UsersToDiscount> usersToDiscounts) {
		this.usersToDiscounts = usersToDiscounts;
	}
	
	

	
	

}