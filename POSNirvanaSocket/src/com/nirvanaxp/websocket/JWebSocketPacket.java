/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.websocket;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JWebSocketPacket
{

	private String ns;
	private String type;
	private String targetId;
	private String clientType;
	private String clientName;
	private String clientVersion;
	private String clientInfo;
	private String jwsType;
	private String sourceId;
	private String sender;
	private String data;
	private int utid;
	private String jwsVersion;
	private String encodingFormats;

	public JWebSocketPacket()
	{

	}

	public String getNs()
	{
		return ns;
	}

	public void setNs(String ns)
	{
		this.ns = ns;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getTargetId()
	{
		return targetId;
	}

	public void setTargetId(String targetId)
	{
		this.targetId = targetId;
	}

	public String getSourceId()
	{
		 if(sourceId != null && (sourceId.length()==0 || sourceId.equals("0"))){return null;}else{	return sourceId;}
	}

	public void setSourceId(String sourceId)
	{
		this.sourceId = sourceId;
	}

	public String getSender()
	{
		return sender;
	}

	public void setSender(String sender)
	{
		this.sender = sender;
	}

	public String getData()
	{
		// remove all escape chars
		return data;
	}

	public void setData(String data)
	{
		this.data = data;
	}

	public int getUtid()
	{
		return utid;
	}

	public void setUtid(int utid)
	{
		this.utid = utid;
	}

	public String getClientType()
	{
		return clientType;
	}

	public void setClientType(String clientType)
	{
		this.clientType = clientType;
	}

	public String getClientName()
	{
		return clientName;
	}

	public void setClientName(String clientName)
	{
		this.clientName = clientName;
	}

	public String getClientVersion()
	{
		return clientVersion;
	}

	public void setClientVersion(String clientVersion)
	{
		this.clientVersion = clientVersion;
	}

	public String getClientInfo()
	{
		return clientInfo;
	}

	public void setClientInfo(String clientInfo)
	{
		this.clientInfo = clientInfo;
	}

	public String getJwsType()
	{
		return jwsType;
	}

	public void setJwsType(String jwsType)
	{
		this.jwsType = jwsType;
	}

	public String getJwsVersion()
	{
		return jwsVersion;
	}

	public void setJwsVersion(String jwsVersion)
	{
		this.jwsVersion = jwsVersion;
	}

	public String getEncodingFormats()
	{
		return encodingFormats;
	}

	public void setEncodingFormats(String encodingFormats)
	{
		this.encodingFormats = encodingFormats;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientInfo == null) ? 0 : clientInfo.hashCode());
		result = prime * result + ((clientName == null) ? 0 : clientName.hashCode());
		result = prime * result + ((clientType == null) ? 0 : clientType.hashCode());
		result = prime * result + ((clientVersion == null) ? 0 : clientVersion.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((encodingFormats == null) ? 0 : encodingFormats.hashCode());
		result = prime * result + ((jwsType == null) ? 0 : jwsType.hashCode());
		result = prime * result + ((jwsVersion == null) ? 0 : jwsVersion.hashCode());
		result = prime * result + ((ns == null) ? 0 : ns.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
		result = prime * result + ((targetId == null) ? 0 : targetId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + utid;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JWebSocketPacket other = (JWebSocketPacket) obj;
		if (clientInfo == null)
		{
			if (other.clientInfo != null)
				return false;
		}
		else if (!clientInfo.equals(other.clientInfo))
			return false;
		if (clientName == null)
		{
			if (other.clientName != null)
				return false;
		}
		else if (!clientName.equals(other.clientName))
			return false;
		if (clientType == null)
		{
			if (other.clientType != null)
				return false;
		}
		else if (!clientType.equals(other.clientType))
			return false;
		if (clientVersion == null)
		{
			if (other.clientVersion != null)
				return false;
		}
		else if (!clientVersion.equals(other.clientVersion))
			return false;
		if (data == null)
		{
			if (other.data != null)
				return false;
		}
		else if (!data.equals(other.data))
			return false;
		if (encodingFormats == null)
		{
			if (other.encodingFormats != null)
				return false;
		}
		else if (!encodingFormats.equals(other.encodingFormats))
			return false;
		if (jwsType == null)
		{
			if (other.jwsType != null)
				return false;
		}
		else if (!jwsType.equals(other.jwsType))
			return false;
		if (jwsVersion == null)
		{
			if (other.jwsVersion != null)
				return false;
		}
		else if (!jwsVersion.equals(other.jwsVersion))
			return false;
		if (ns == null)
		{
			if (other.ns != null)
				return false;
		}
		else if (!ns.equals(other.ns))
			return false;
		if (sender == null)
		{
			if (other.sender != null)
				return false;
		}
		else if (!sender.equals(other.sender))
			return false;
		if (sourceId == null)
		{
			if (other.sourceId != null)
				return false;
		}
		else if (!sourceId.equals(other.sourceId))
			return false;
		if (targetId == null)
		{
			if (other.targetId != null)
				return false;
		}
		else if (!targetId.equals(other.targetId))
			return false;
		if (type == null)
		{
			if (other.type != null)
				return false;
		}
		else if (!type.equals(other.type))
			return false;
		if (utid != other.utid)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "JWebSocketPacket [ns=" + ns + ", type=" + type + ", targetId=" + targetId + ", clientType=" + clientType + ", clientName=" + clientName + ", clientVersion=" + clientVersion
				+ ", clientInfo=" + clientInfo + ", jwsType=" + jwsType + ", sourceId=" + sourceId + ", sender=" + sender + ", data=" + data + ", utid=" + utid + ", jwsVersion=" + jwsVersion
				+ ", encodingFormats=" + encodingFormats + "]";
	}

}
