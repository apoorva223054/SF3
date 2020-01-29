package com.nirvanaxp.services.exceptions.ewards;

public class Redemption {
	
	private String redeemed_amount;
	private String reward_id;
	public String getRedeemed_amount() {
		return redeemed_amount;
	}
	public void setRedeemed_amount(String redeemed_amount) {
		this.redeemed_amount = redeemed_amount;
	}
	public String getReward_id() {
		return reward_id;
	}
	public void setReward_id(String reward_id) {
		this.reward_id = reward_id;
	}
	@Override
	public String toString() {
		return "Redemption [redeemed_amount=" + redeemed_amount
				+ ", reward_id=" + reward_id + "]";
	}
	
	
}
