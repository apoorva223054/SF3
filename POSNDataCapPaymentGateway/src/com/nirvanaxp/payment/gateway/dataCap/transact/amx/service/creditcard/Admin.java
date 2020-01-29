package com.nirvanaxp.payment.gateway.dataCap.transact.amx.service.creditcard;

public class Admin {
	private String MerchantId;
	private String TerminalID;
	private String OperatorID;
	private String UserTrace;
	private String TranType;
	private String TranCode;
	private String SecureDevice;
	private String SequenceNo;
	private String TranDeviceID;
	private String PinPadIpAddress;
	private String PinPadIpPort;
	
	public String getMerchantId() {
		return MerchantId;
	}
	public void setMerchantId(String merchantId) {
		MerchantId = merchantId;
	}
	public String getTerminalID() {
		return TerminalID;
	}
	public void setTerminalID(String terminalID) {
		TerminalID = terminalID;
	}
	public String getOperatorID() {
		return OperatorID;
	}
	public void setOperatorID(String operatorID) {
		OperatorID = operatorID;
	}
	public String getUserTrace() {
		return UserTrace;
	}
	public void setUserTrace(String userTrace) {
		UserTrace = userTrace;
	}
	public String getTranType() {
		return TranType;
	}
	public void setTranType(String tranType) {
		TranType = tranType;
	}
	public String getTranCode() {
		return TranCode;
	}
	public void setTranCode(String tranCode) {
		TranCode = tranCode;
	}
	public String getSecureDevice() {
		return SecureDevice;
	}
	public void setSecureDevice(String secureDevice) {
		SecureDevice = secureDevice;
	}
	public String getSequenceNo() {
		return SequenceNo;
	}
	public void setSequenceNo(String sequenceNo) {
		SequenceNo = sequenceNo;
	}
	public String getTranDeviceID() {
		return TranDeviceID;
	}
	public void setTranDeviceID(String tranDeviceID) {
		TranDeviceID = tranDeviceID;
	}
	public String getPinPadIpAddress() {
		return PinPadIpAddress;
	}
	public void setPinPadIpAddress(String pinPadIpAddress) {
		PinPadIpAddress = pinPadIpAddress;
	}
	public String getPinPadIpPort() {
		return PinPadIpPort;
	}
	public void setPinPadIpPort(String pinPadIpPort) {
		PinPadIpPort = pinPadIpPort;
	}
	
	@Override
	public String toString() {
		return "Admin [MerchantId=" + MerchantId + ", TerminalID=" + TerminalID
				+ ", OperatorID=" + OperatorID + ", UserTrace=" + UserTrace
				+ ", TranType=" + TranType + ", TranCode=" + TranCode
				+ ", SecureDevice=" + SecureDevice + ", SequenceNo="
				+ SequenceNo + ", TranDeviceID=" + TranDeviceID
				+ ", PinPadIpAddress=" + PinPadIpAddress + ", PinPadIpPort="
				+ PinPadIpPort + "]";
	}
 
	
	
}
