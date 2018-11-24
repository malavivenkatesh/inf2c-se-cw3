package auctionhouse;

import java.util.HashMap;

public class Auctioneer {
	private String auctioneerName;
	private String auctioneerAddress;
	private Parameters parameters;
	
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
	
	public Status openAuction(Lot currentLot) {
	    
	    
	    if(currentLot.getLotStatus() != LotStatus.UNSOLD) {
	        return Status.error("Lot not available for auction");
	    }
	    
	    currentLot.setLotStatus(LotStatus.IN_AUCTION);
	    
	    HashMap<String, Buyer> lotinterestedBuyers = currentLot.getInterestedBuyers();
	    
	    for (String buyerName : lotinterestedBuyers.keySet()) {
	        Buyer currentBuyer = lotinterestedBuyers.get(buyerName);
	        String currentAddress = currentBuyer.getAddress();
	        
	        parameters.messagingService.auctionOpened(currentAddress, currentLot.lotNumber);
	        
	    }
	    String sellerAddress = currentLot.getLotSeller().getAddress();
	    parameters.messagingService.auctionOpened(sellerAddress, currentLot.lotNumber);
	    
	    return Status.OK();
	}
}
