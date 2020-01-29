package com.nirvanaxp.types.entities.pnglobal;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:26.557+0530")
@StaticMetamodel(PnglobalBusiness.class)
public class PnglobalBusiness_ {
	public static volatile SingularAttribute<PnglobalBusiness, Integer> id;
	public static volatile SingularAttribute<PnglobalBusiness, Integer> billingAddressId;
	public static volatile SingularAttribute<PnglobalBusiness, String> businessName;
	public static volatile SingularAttribute<PnglobalBusiness, Integer> businessTypeId;
	public static volatile SingularAttribute<PnglobalBusiness, Date> created;
	public static volatile SingularAttribute<PnglobalBusiness, String> createdBy;
	public static volatile SingularAttribute<PnglobalBusiness, String> email;
	public static volatile SingularAttribute<PnglobalBusiness, String> logo;
	public static volatile SingularAttribute<PnglobalBusiness, String> schemaName;
	public static volatile SingularAttribute<PnglobalBusiness, Integer> shippingAddressId;
	public static volatile SingularAttribute<PnglobalBusiness, Date> updated;
	public static volatile SingularAttribute<PnglobalBusiness, String> updatedBy;
	public static volatile SingularAttribute<PnglobalBusiness, String> website;
}
