package com.nirvanaxp.global.types.entities.devicemgmt;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:22.246+0530")
@StaticMetamodel(EncryptionKey.class)
public class EncryptionKey_ {
	public static volatile SingularAttribute<EncryptionKey, Integer> id;
	public static volatile SingularAttribute<EncryptionKey, Date> created;
	public static volatile SingularAttribute<EncryptionKey, String> createdBy;
	public static volatile SingularAttribute<EncryptionKey, Date> updated;
	public static volatile SingularAttribute<EncryptionKey, String> updatedBy;
	public static volatile SingularAttribute<EncryptionKey, String> encryptionKey;
	public static volatile SingularAttribute<EncryptionKey, Integer> accountId;
}
