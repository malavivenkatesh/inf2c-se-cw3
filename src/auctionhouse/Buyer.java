package auctionhouse;

public class Buyer {
    
    private String name;
    private String address;
    private String bankAccount;
    private String bankAuthCode;
    private Parameters parameters;
    
    public Buyer(String name,String address, String bankAccount, String bankAuthCode) {
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
    
    public Status makeBid(Lot currentLot, Money bid, Buyer currentBidder) {
        
        Money hammerPrice = currentLot.getHammerPrice();
        
            if (currentLot.getLotStatus() != LotStatus.IN_AUCTION) {
                return Status.error("Lot not on auction");
            }
            else if (bid.lessEqual(hammerPrice)) {
                return Status.error("Bid less than hammer price");
            }
            else if (bid.compareTo(hammerPrice) > 0) {
                currentLot.setHammerPrice(bid);
                currentLot.setLotBuyer(currentBidder);
                return Status.OK();
            }
            
            return Status.OK();
    }

}
