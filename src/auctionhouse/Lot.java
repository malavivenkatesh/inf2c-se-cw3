package auctionhouse;

import java.util.HashMap;

public class Lot extends CatalogueEntry {
    
    private Seller lotSeller;
    private Money reservePrice;
    private HashMap<String, Buyer> interestedBuyers = new HashMap<>();
    
    public Lot(Seller lotSeller, int number, String description, Money reservePrice) {

        super(number, description, LotStatus.UNSOLD);
        this.reservePrice = reservePrice;
        this.lotSeller = lotSeller;
    }
    
    public Status noteInterest(String buyerName, Buyer intBuyer){
        interestedBuyers.put(buyerName, intBuyer);
        return Status.OK();
    }
    
    public HashMap<String, Buyer> getInterestedBuyers(){
        return interestedBuyers;
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

}
