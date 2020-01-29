package com.nirvanaxp.services.jaxrs;

// TODO: Auto-generated Javadoc
/**
 * The Class RegisterInfo.
 */
public class RegisterInfo {

	/** The name. */
	private String name;
	
	/** The total. */
	private String total;
		
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the total.
	 *
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}
	
	/**
	 * Sets the total.
	 *
	 * @param total the new total
	 */
	public void setTotal(String total) {
		this.total = total;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RegisterInfo [name=" + name + ", total=" + total +"]";
	}	
	
}
