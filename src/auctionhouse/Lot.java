package auctionhouse;

public class Lot {
    
    private Seller lotSeller;
    private int lotID;
    private String description;
    private Money reservePrice;
    
    public Lot(String sellerName, int number, String description, Money reservePrice) {
        lotID = number;
        this.description = description;
        this.reservePrice = reservePrice;
        
    }
}
