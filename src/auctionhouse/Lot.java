package auctionhouse;

import java.util.HashMap;

public class Lot extends CatalogueEntry {
    
    private Seller lotSeller;
    private Money reservePrice;
    private HashMap<String, Buyer> interestedBuyers = new HashMap<>();
    private Parameters parameters;
    private Money hammerPrice;
    private Buyer lotBuyer;
    private Auctioneer lotAuctioneer;
    
    public Lot(Seller lotSeller, int number, String description, Money reservePrice, Parameters parameters) {

        super(number, description, LotStatus.UNSOLD);
        this.reservePrice = reservePrice;
        this.lotSeller = lotSeller;
        this.parameters = parameters;
        this.hammerPrice = new Money("0");
    }
    
    public Status noteInterest(String buyerName, Buyer intBuyer){
        interestedBuyers.put(buyerName, intBuyer);
        return Status.OK();
    }
    
    public Status makeBid(Buyer currentBidder, Money bid) {
        if (status != LotStatus.IN_AUCTION) {
            return Status.error("Lot not on auction");
        }
        else if (bid.lessEqual(hammerPrice)) {
            return Status.error("Bid less than hammer price");
        }
        else if (bid.compareTo(hammerPrice) > 0) {
            hammerPrice = bid;
            return Status.OK();
        }
        return Status.OK();
        
    }
    
    public HashMap<String, Buyer> getInterestedBuyers(){
        return interestedBuyers;
    }
    
    public int getLotNumber() {
        return lotNumber;
    }
    
    public Money getHammerPrice() {
        return hammerPrice;
    }
    
    public LotStatus getLotStatus() {
        return status;
    }
    
    public void setLotStatus(LotStatus status) {
        this.status = status;
    }

    public Money getReservePrice() {
        return reservePrice;
    }

    public Seller getLotSeller() {
        return lotSeller;
    }

    public Auctioneer getLotAuctioneer() {
        return lotAuctioneer;
    }

    public void setLotAuctioneer(Auctioneer lotAuctioneer) {
        this.lotAuctioneer = lotAuctioneer;
    }

}
