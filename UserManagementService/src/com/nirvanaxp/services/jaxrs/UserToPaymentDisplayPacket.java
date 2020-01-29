package com.nirvanaxp.services.jaxrs;

import java.util.List;

import com.nirvanaxp.types.entities.user.UsersToPayment;

public class UserToPaymentDisplayPacket {

	 
	private List<UserToPaymentHistoryDisplayPacket> usersToPaymentHistoryDisplayPacket;
	private UsersToPayment usersToPayment;

	public List<UserToPaymentHistoryDisplayPacket> getUsersToPaymentHistoryDisplayPacket() {
		return usersToPaymentHistoryDisplayPacket;
	}

	public void setUsersToPaymentHistoryDisplayPacket(
			List<UserToPaymentHistoryDisplayPacket> usersToPaymentHistoryDisplayPacket) {
		this.usersToPaymentHistoryDisplayPacket = usersToPaymentHistoryDisplayPacket;
	}

	public UsersToPayment getUsersToPayment() {
		return usersToPayment;
	}

	public void setUsersToPayment(UsersToPayment usersToPayment) {
		this.usersToPayment = usersToPayment;
	}


	@Override
	public String toString()
	{
		return "UserToPaymentDisplayPacket [usersToPayment=" + usersToPayment + ", usersToPaymentHistoryDisplayPacket=" + usersToPaymentHistoryDisplayPacket 
				+ "]";
	}


	
}
