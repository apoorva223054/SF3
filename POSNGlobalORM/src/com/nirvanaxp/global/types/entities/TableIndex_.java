package com.nirvanaxp.global.types.entities;

import java.math.BigInteger;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-31T10:23:32.211+0530")
@StaticMetamodel(TableIndex.class)
public class TableIndex_ {
	public static volatile SingularAttribute<TableIndex, BigInteger> id;
	public static volatile SingularAttribute<TableIndex, String> tableName;
	public static volatile SingularAttribute<TableIndex, BigInteger> indexing;
}
