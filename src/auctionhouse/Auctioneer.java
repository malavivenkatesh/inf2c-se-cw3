package auctionhouse;

public class Auctioneer {
	private String auctioneerName;
	private String auctioneerAddress;
	
	public Auctioneer(String auctionID, String auctioneerAddress) {
		this.auctioneerName = auctionID;
		this.auctioneerAddress = auctioneerAddress;
	}
	
	public String getName() {
		return auctioneerName;
	}
	
	public String getAddress() {
		return auctioneerAddress;
	}
}
