package auctionhouse;

import java.util.logging.Logger;

public class Buyer {
    private static Logger logger = Logger.getLogger("auctionhouse");
    
    private String name;
    private String address;
    private String bankAccount;
    private String bankAuthCode;
    private Parameters parameters;
    
    public Buyer(String name,String address, String bankAccount, String bankAuthCode, Parameters parameters) {
        this.name = name;
        this.address = address;
        this.bankAccount = bankAccount;
        this.bankAuthCode = bankAuthCode;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getBankAuthCode() {
        return bankAuthCode;
    }

    public String getBankAccount() {
        return bankAccount;
    }
    
    public Status noteInterest(Lot interestedLot, String buyerName
            , Buyer interestedBuyer) {
        logger.fine("Entering");
        
        interestedLot.getInterestedBuyers().put(buyerName, interestedBuyer);
        return Status.OK();
    }
    
    public Status makeBid(Lot currentLot, Money bid, Buyer currentBidder) {
        logger.fine("Entering");
        
        Money hammerPrice = currentLot.getHammerPrice();
        Money increment = parameters.increment;
        Money incrementBid = increment.add(hammerPrice);
        logger.finer(incrementBid.toString() + " " + hammerPrice.toString());
        
            if (currentLot.getLotStatus() != LotStatus.IN_AUCTION) {
                return Status.error("Lot not on auction");
            }
            else if (bid.lessEqual(incrementBid)) {
                return Status.error("Jump bid less than incremental bid");
            }
            else {
                currentLot.setHammerPrice(bid);
                currentLot.setLotBuyer(currentBidder);
                return Status.OK();
            }
    }

}
