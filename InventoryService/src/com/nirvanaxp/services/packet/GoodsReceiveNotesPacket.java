package com.nirvanaxp.services.packet;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.types.entities.inventory.GoodsReceiveNotes;
@XmlRootElement(name = "GoodsReceiveNotesPacket")
public class GoodsReceiveNotesPacket extends PostPacket {
	
	String grnNumber;
	String date;
	
	private List<GoodsReceiveNotes> goodsReceiveNotesList;
	 

	public List<GoodsReceiveNotes> getGoodsReceiveNotesList() {
		return goodsReceiveNotesList;
	}

	public void setGoodsReceiveNotesList(List<GoodsReceiveNotes> goodsReceiveNotesList) {
		this.goodsReceiveNotesList = goodsReceiveNotesList;
	}

	public String getGrnNumber() {
		 if(grnNumber != null && (grnNumber.length()==0 || grnNumber.equals("0"))){return null;}else{	return grnNumber;}
	}

	public void setGrnNumber(String grnNumber) {
		this.grnNumber = grnNumber;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "GoodsReceiveNotesPacket [grnNumber=" + grnNumber + ", date=" + date + ", goodsReceiveNotesList="
				+ goodsReceiveNotesList + "]";
	}

	 
	
	
}
