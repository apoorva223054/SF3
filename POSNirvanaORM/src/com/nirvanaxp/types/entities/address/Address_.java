package com.nirvanaxp.types.entities.address;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-20T11:47:30.977+0530")
@StaticMetamodel(Address.class)
public class Address_ {
	public static volatile SingularAttribute<Address, String> id;
	public static volatile SingularAttribute<Address, String> address1;
	public static volatile SingularAttribute<Address, String> address2;
	public static volatile SingularAttribute<Address, Integer> countryId;
	public static volatile SingularAttribute<Address, Date> created;
	public static volatile SingularAttribute<Address, String> createdBy;
	public static volatile SingularAttribute<Address, String> fax;
	public static volatile SingularAttribute<Address, String> latValue;
	public static volatile SingularAttribute<Address, String> longValue;
	public static volatile SingularAttribute<Address, String> phone;
	public static volatile SingularAttribute<Address, String> state;
	public static volatile SingularAttribute<Address, String> city;
	public static volatile SingularAttribute<Address, Integer> stateId;
	public static volatile SingularAttribute<Address, Integer> cityId;
	public static volatile SingularAttribute<Address, Date> updated;
	public static volatile SingularAttribute<Address, String> updatedBy;
	public static volatile SingularAttribute<Address, String> zip;
	public static volatile SingularAttribute<Address, Integer> isDefaultAddress;
}
