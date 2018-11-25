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
        
        Buyer interestedBuyer = registeredBuyers.get(buyerName);
        
        //checks if buyer is registered
        if (interestedBuyer == null) {
            return Status.error("User not registered as buyer");
        }
        
        Lot interestedLot = allLots.get(lotNumber);
        interestedBuyer.noteInterest(interestedLot, buyerName, interestedBuyer);
        
        return Status.OK();   
    }

    public Status openAuction(
            String auctioneerName,
            String auctioneerAddress,
            int lotNumber) {
        logger.fine(startBanner("openAuction " + auctioneerName + " " + lotNumber));
        
        //gets information on the lot and creates an auctioneer for this lot.
        Lot currentLot = allLots.get(lotNumber);
        Auctioneer currentAuctioneer = 
                new Auctioneer(auctioneerName, auctioneerAddress, parameters);
        currentLot.setLotAuctioneer(currentAuctioneer);
        
        //calls openAuction from Auctioneer class to change lot status
        Status auctionStatus = currentAuctioneer.openAuction(currentLot);
        
        //sends messages to lot seller and interested buyers if lot is available for auction
        if (auctionStatus.kind == Status.Kind.OK) {
            for (Buyer buyer : currentLot.getInterestedBuyers().values()) {
                parameters.messagingService.auctionOpened(buyer.getAddress(), lotNumber);
            }
            Seller lotSeller = currentLot.getLotSeller();
            parameters.messagingService.auctionOpened(lotSeller.getAddress(), lotNumber);
        }
        
        return auctionStatus;
        
    }

    public Status makeBid(
            String buyerName,
            int lotNumber,
            Money bid) {
        logger.fine(startBanner("makeBid " + buyerName + " " + lotNumber + " " + bid));
        
        //getting information needed on buyer and lot
        Buyer currentBidder = registeredBuyers.get(buyerName);
        Lot currentLot = allLots.get(lotNumber);
        
        //calls makeBid from Buyer class
        Status bidStatus = currentBidder.makeBid(currentLot, bid, currentBidder);
        
        /* checks that bidStatus is valid before sending out messages to relevant actors that
         * there is a new bid
        */
        if (bidStatus.kind == Status.Kind.OK) {
            for (Buyer buyer : currentLot.getInterestedBuyers().values()) {
                if(!(buyer.getName().equals(buyerName))) {
                    parameters.messagingService.bidAccepted(buyer.getAddress(), 
                            lotNumber, currentLot.getHammerPrice());
                }
            }
            Seller lotSeller = currentLot.getLotSeller();
            Auctioneer lotAuctioneer = currentLot.getLotAuctioneer();
            parameters.messagingService.bidAccepted(lotSeller.getAddress(), lotNumber, bid);
            parameters.messagingService.bidAccepted(lotAuctioneer.getAddress(), lotNumber, bid);
        }
        return bidStatus;    
    }

   public Status closeAuction(
            String auctioneerName,
            int lotNumber) {
        logger.fine(startBanner("closeAuction " + auctioneerName + " " + lotNumber));
        
        //getting all the information needed
        Lot currentLot = allLots.get(lotNumber);
        Money hammerPrice = currentLot.getHammerPrice();
        Money reservePrice = currentLot.getReservePrice();
        Buyer buyer = allLots.get(lotNumber).getLotBuyer();
        Seller seller = allLots.get(lotNumber).getLotSeller();
        Auctioneer auctioneer = currentLot.getLotAuctioneer();
        
        if (!(auctioneer.getName().equals(auctioneerName))) {
            return Status.error("This auctioneer did not open this lot for auction");
        }
        
        //calling auctioneer class to close the auction
        Status soldStatus = auctioneer.closeAuction(currentLot, buyer, seller,
                reservePrice, hammerPrice);
        return soldStatus;
        
    }
    

    
}
