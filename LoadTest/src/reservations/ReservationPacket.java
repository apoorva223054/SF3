package reservations;



public class ReservationPacket extends PostPacket{

	private Reservation reservation;

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	@Override
	public String toString() {
		return "ReservationPacket [reservation=" + reservation + "]";
	}

	
}
