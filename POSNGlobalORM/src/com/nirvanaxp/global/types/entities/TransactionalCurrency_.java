package com.nirvanaxp.global.types.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:22.785+0530")
@StaticMetamodel(TransactionalCurrency.class)
public class TransactionalCurrency_ {
	public static volatile SingularAttribute<TransactionalCurrency, Integer> id;
	public static volatile SingularAttribute<TransactionalCurrency, Date> created;
	public static volatile SingularAttribute<TransactionalCurrency, String> createdBy;
	public static volatile SingularAttribute<TransactionalCurrency, String> currencyName;
	public static volatile SingularAttribute<TransactionalCurrency, String> displayName;
	public static volatile SingularAttribute<TransactionalCurrency, Integer> displaySequence;
	public static volatile SingularAttribute<TransactionalCurrency, Date> updated;
	public static volatile SingularAttribute<TransactionalCurrency, String> updatedBy;
	public static volatile SingularAttribute<TransactionalCurrency, String> symbol;
}
