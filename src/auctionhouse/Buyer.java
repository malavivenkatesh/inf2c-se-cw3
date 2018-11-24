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

}
