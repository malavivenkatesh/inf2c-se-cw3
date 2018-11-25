package auctionhouse;

import java.util.HashMap;

public class Auctioneer {
	private String auctioneerName;
	private String auctioneerAddress;
	private Parameters parameters;
	
	public Auctioneer(String auctionID, String auctioneerAddress, 
	        Parameters parameters) {
		this.auctioneerName = auctionID;
		this.auctioneerAddress = auctioneerAddress;
		this.parameters = parameters;
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
	    
	    return Status.OK();
	}
	
	public Status closeAuction(Lot currentLot, Buyer buyer, Seller seller,
	        Money reservePrice, Money hammerPrice) {
	    int lotNumber = currentLot.getLotNumber();
	    
	    //if the lot is not in auction then return an error
        if (currentLot.getLotStatus() != LotStatus.IN_AUCTION) {
            return Status.error("Lot not in Auction.");
        }
        //checks for the hammer price is greater than the reserve price
        else if (hammerPrice.lessEqual(reservePrice) && !(hammerPrice.equals(reservePrice))) {
            currentLot.setLotStatus(LotStatus.UNSOLD);
            Status saleStatus = new Status(Status.Kind.NO_SALE);
            for (Buyer interestedBuyer : currentLot.getInterestedBuyers().values()) {
                parameters.messagingService.lotUnsold(interestedBuyer.getAddress(), currentLot.lotNumber);
            }
            parameters.messagingService.lotUnsold(currentLot.getLotSeller().getAddress(), lotNumber);
            
        } 
        else {          
            hammerPrice.addPercent(parameters.buyerPremium);
            Status buyerTransferStatus = getBuyerPayment(buyer, hammerPrice, parameters);
            
            if (buyerTransferStatus.kind != Status.Kind.OK) {
                currentLot.setLotStatus(LotStatus.SOLD_PENDING_PAYMENT);
                Status saleStatus = new Status(Status.Kind.NO_SALE);
                return Status.error("Buyer's transfer failed");
            }
            
 
            Status sellerTransferStatus = paySeller(seller, hammerPrice, parameters);
            
            if (sellerTransferStatus.kind != Status.Kind.OK) {
                Status saleStatus = new Status(Status.Kind.NO_SALE);
                currentLot.setLotStatus(LotStatus.SOLD_PENDING_PAYMENT);
                return Status.error("Transfer to seller failed");
            }       
            
            currentLot.setLotStatus(LotStatus.SOLD);
            for (Buyer interestedBuyer : currentLot.getInterestedBuyers().values()) {
                parameters.messagingService.lotSold(interestedBuyer.getAddress(), lotNumber);
            }
            parameters.messagingService.lotSold(currentLot.getLotSeller().getAddress(), lotNumber);
        }
        
        Status saleStatus = new Status(Status.Kind.SALE);
        return saleStatus;
}
	    
	
	
    public Status getBuyerPayment(Buyer buyer, Money hammerPrice, Parameters parameters) {
        String sellerAccount  = buyer.getBankAccount();
        String sellerAuthCode = buyer.getBankAuthCode();
        String houseAccount = parameters.houseBankAccount;
        Money buyerPrice = hammerPrice.addPercent(parameters.buyerPremium);
        auctionhouse.Status transferStatus = 
                parameters.bankingService.transfer(sellerAccount, sellerAuthCode, houseAccount, buyerPrice);
        return transferStatus;
        
    }
    
    public Status paySeller(Seller seller, Money hammerPrice, Parameters parameters) {
        String sellerAccount  = seller.getBankAccount();
        String houseAccount = parameters.houseBankAccount;
        String houseAuthCode = parameters.houseBankAuthCode;
        Double commission = parameters.commission;
        Money sellerPayment = hammerPrice.addPercent(-commission);
    
        Status transferStatus = 
                parameters.bankingService.transfer(houseAccount, houseAuthCode, sellerAccount, sellerPayment);
 
        return transferStatus;
        
    }
}
