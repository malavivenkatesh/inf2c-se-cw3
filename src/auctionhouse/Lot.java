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
}
