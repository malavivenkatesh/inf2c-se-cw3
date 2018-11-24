/**
 * 
 */
package auctionhouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author pbj
 *
 */
public class AuctionHouseImp implements AuctionHouse {

    private static Logger logger = Logger.getLogger("auctionhouse");
    private static final String LS = System.lineSeparator();
    
    private HashMap<String, Buyer> registeredBuyers;
    private HashMap<String, Seller> registeredSellers;
    private HashMap<Integer, Lot> allLots;
    private Parameters parameters;
    
    private String startBanner(String messageName) {
        return  LS 
          + "-------------------------------------------------------------" + LS
          + "MESSAGE IN: " + messageName + LS
          + "-------------------------------------------------------------";
    }
   
    public AuctionHouseImp(Parameters parameters) {
        registeredBuyers = new HashMap<>();
        registeredSellers = new HashMap<>();
        allLots = new HashMap<>();
        this.parameters = parameters;
        
    }

    public Status registerBuyer(
            String name,
            String address,
            String bankAccount,
            String bankAuthCode) {
        logger.fine(startBanner("registerBuyer " + name));
        
        if (registeredBuyers.get(name) != null) {
            return Status.error("Buyer already registered");
        }
        
        Buyer newBuyer = new Buyer(name, address, bankAccount, bankAuthCode);
        registeredBuyers.put(name, newBuyer);
        
        return Status.OK();
    }

    public Status registerSeller(
            String name,
            String address,
            String bankAccount) {
        logger.fine(startBanner("registerSeller " + name));
        
        if(registeredSellers.get(name) != null) {
            return Status.error("Seller already registered.");
        }
        
        Seller newSeller = new Seller(name, address, bankAccount);
        registeredSellers.put(name, newSeller);
        
        return Status.OK();      
    }

    public Status addLot(
            String sellerName,
            int number,
            String description,
            Money reservePrice) {
        logger.fine(startBanner("addLot " + sellerName + " " + number));
        
        if (allLots.get(number) != null) {
            return Status.error("Lot ID already in use");
        }
        
        Seller lotSeller = registeredSellers.get(sellerName);
        Lot newLot = new Lot(lotSeller, number, description, reservePrice, parameters);
        allLots.put(number, newLot);
        
        return Status.OK();    
    }

    public List<CatalogueEntry> viewCatalogue() {
        logger.fine(startBanner("viewCatalog"));
        
        List<CatalogueEntry> catalogue = new ArrayList<CatalogueEntry>();
        logger.fine("Catalogue: " + catalogue.toString());
        
        Set<Integer> lotNumberList = allLots.keySet();
        ArrayList<Integer> lotNumberListSorted = new ArrayList<>(lotNumberList);
        Collections.sort(lotNumberListSorted);
        
        for(Integer i : lotNumberListSorted) {
            catalogue.add(allLots.get(i));
        }
        
        return catalogue;
    }

    public Status noteInterest(
            String buyerName,
            int lotNumber) {
        logger.fine(startBanner("noteInterest " + buyerName + " " + lotNumber));
        
        Buyer intBuyer = registeredBuyers.get(buyerName);
        
        if (intBuyer == null) {
            return Status.error("User not registered as buyer");
        }
        
        Lot intLot = allLots.get(lotNumber);
        intLot.noteInterest(buyerName, intBuyer);
        
        return Status.OK();   
    }

    public Status openAuction(
            String auctioneerName,
            String auctioneerAddress,
            int lotNumber) {
        logger.fine(startBanner("openAuction " + auctioneerName + " " + lotNumber));
        
        Lot currentLot = allLots.get(lotNumber);
        Auctioneer currentAuctioneer = new Auctioneer(auctioneerName, auctioneerAddress, parameters);
        currentLot.setLotAuctioneer(currentAuctioneer);
        //need to change so status of lot is checked first
        return currentAuctioneer.openAuction(currentLot);
        
    }

    public Status makeBid(
            String buyerName,
            int lotNumber,
            Money bid) {
        logger.fine(startBanner("makeBid " + buyerName + " " + lotNumber + " " + bid));
        
        Buyer currentBidder = registeredBuyers.get(buyerName);
        Lot currentLot = allLots.get(lotNumber);
        
        Status bidStatus = currentLot.makeBid(currentBidder, bid);
        if (bidStatus.kind == Status.Kind.OK) {
            for (Buyer buyer : currentLot.getInterestedBuyers().values()) {
                if(!(buyer.getName().equals(buyerName))) {
                    parameters.messagingService.bidAccepted(buyer.getAddress(), 
                            lotNumber, currentLot.getHammerPrice());
                }
            }
            String sellerAddress = currentLot.getLotSeller().getAddress();
            String auctioneerAdd = currentLot.getLotAuctioneer().getAddress();
            parameters.messagingService.bidAccepted(sellerAddress, lotNumber, bid);
            parameters.messagingService.bidAccepted(auctioneerAdd, lotNumber, bid);
        }
        
        return Status.OK();    
    }

    public Status closeAuction(
            String auctioneerName,
            int lotNumber) {
        logger.fine(startBanner("closeAuction " + auctioneerName + " " + lotNumber));
        //check if hammerprice is null to check if there were no (valid) bids made 
        return Status.OK();  
    }
    
}
