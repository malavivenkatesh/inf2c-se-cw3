package auctionhouse;

import java.util.HashMap;
import java.util.logging.Logger;

public class Auctioneer {
    private static Logger logger = Logger.getLogger("auctionhouse");
    
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
	    logger.fine("Entering");
	    if(currentLot.getLotStatus() != LotStatus.UNSOLD) {
	        return Status.error("Lot not available for auction");
	    }
	    currentLot.setLotStatus(LotStatus.IN_AUCTION);
	    
	    return Status.OK();
	}
	
	public Status closeAuction(Lot currentLot, Buyer buyer, Seller seller,
	        Money reservePrice, Money hammerPrice) {
	    logger.fine("Entering");
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
            return saleStatus;
        } 
        else {          
            hammerPrice.addPercent(parameters.buyerPremium);
            Status buyerTransferStatus = getBuyerPayment(buyer, hammerPrice, parameters);
            
            if (buyerTransferStatus.kind != Status.Kind.OK) {
                currentLot.setLotStatus(LotStatus.SOLD_PENDING_PAYMENT);
                Status saleStatus = new Status(Status.Kind.SALE_PENDING_PAYMENT);
                return saleStatus;
            }
            
 
            Status sellerTransferStatus = paySeller(seller, hammerPrice, parameters);
            
            if (sellerTransferStatus.kind != Status.Kind.OK) {
                Status saleStatus = new Status(Status.Kind.SALE_PENDING_PAYMENT);
                currentLot.setLotStatus(LotStatus.SOLD_PENDING_PAYMENT);
                return saleStatus;
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
        logger.fine("Entering");
        
        String sellerAccount  = buyer.getBankAccount();
        String sellerAuthCode = buyer.getBankAuthCode();
        String houseAccount = parameters.houseBankAccount;
        Money buyerPrice = hammerPrice.addPercent(parameters.buyerPremium);
        auctionhouse.Status transferStatus = 
                parameters.bankingService.transfer(sellerAccount, sellerAuthCode, houseAccount, buyerPrice);
        return transferStatus;
        
    }
    
    public Status paySeller(Seller seller, Money hammerPrice, Parameters parameters) {
        logger.fine("Entering");
        
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
