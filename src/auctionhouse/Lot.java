package auctionhouse;

import java.util.HashMap;
import java.util.logging.Logger;

public class Lot extends CatalogueEntry {
    private static Logger logger = Logger.getLogger("auctionhouse");
    
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

    public Buyer getLotBuyer() {
        return lotBuyer;
    }

    public void setLotBuyer(Buyer lotBuyer) {
        this.lotBuyer = lotBuyer;
    }

    public void setHammerPrice(Money bid) {
        this.hammerPrice = bid;
    }

}
