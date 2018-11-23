package auctionhouse;

public class Seller {
    private String name;
    private String address;
    private String bankAccount;
    
    public Seller(String name,String address, String bankAccount, String bankAuthCode) {
        this.name = name;
        this.address = address;
        this.bankAccount = bankAccount;
    }
}
