/**
 * 
 */
package auctionhouse;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author pbj
 *
 */
public class AuctionHouseTest {

    private static final double BUYER_PREMIUM = 10.0;
    private static final double COMMISSION = 15.0;
    private static final Money INCREMENT = new Money("10.00");
    private static final String HOUSE_ACCOUNT = "AH A/C";
    private static final String HOUSE_AUTH_CODE = "AH-auth";

    private AuctionHouse house;
    private MockMessagingService messagingService;
    private MockBankingService bankingService;

    /*
     * Utility methods to help shorten test text.
     */
    private static void assertOK(Status status) { 
        assertEquals(Status.Kind.OK, status.kind);
    }
    private static void assertError(Status status) { 
        assertEquals(Status.Kind.ERROR, status.kind);
    }
    private static void assertSale(Status status) { 
        assertEquals(Status.Kind.SALE, status.kind);
    }
    
    private static void assertPendingPayment(Status status) { 
        assertEquals(Status.Kind.SALE_PENDING_PAYMENT, status.kind);
    }
    
    private static void assertNoSale(Status status) { 
        assertEquals(Status.Kind.NO_SALE, status.kind);
    }
    /*
     * Logging functionality
     */

    // Convenience field.  Saves on getLogger() calls when logger object needed.
    private static Logger logger;

    // Update this field to limit logging.
    public static Level loggingLevel = Level.ALL;

    private static final String LS = System.lineSeparator();

    @BeforeClass
    public static void setupLogger() {

        logger = Logger.getLogger("auctionhouse"); 
        logger.setLevel(loggingLevel);

        // Ensure the root handler passes on all messages at loggingLevel and above (i.e. more severe)
        Logger rootLogger = Logger.getLogger("");
        Handler handler = rootLogger.getHandlers()[0];
        handler.setLevel(loggingLevel);
    }

    private String makeBanner(String testCaseName) {
        return  LS 
                + "#############################################################" + LS
                + "TESTCASE: " + testCaseName + LS
                + "#############################################################";
    }

    @Before
    public void setup() {
        messagingService = new MockMessagingService();
        bankingService = new MockBankingService();

        house = new AuctionHouseImp(
                    new Parameters(
                        BUYER_PREMIUM,
                        COMMISSION,
                        INCREMENT,
                        HOUSE_ACCOUNT,
                        HOUSE_AUTH_CODE,
                        messagingService,
                        bankingService));

    }
    /*
     * Setup story running through all the test cases.
     * 
     * Story end point is made controllable so that tests can check 
     * story prefixes and branch off in different ways. 
     */
    private void runStory(int endPoint) {
        assertOK(house.registerSeller("SellerY", "@SellerY", "SY A/C"));       
        assertOK(house.registerSeller("SellerZ", "@SellerZ", "SZ A/C")); 
        if (endPoint == 1) return;
        
        assertOK(house.addLot("SellerY", 2, "Painting", new Money("200.00")));
        assertOK(house.addLot("SellerY", 1, "Bicycle", new Money("80.00")));
        assertOK(house.addLot("SellerZ", 5, "Table", new Money("100.00")));
        if (endPoint == 2) return;
        
        assertOK(house.registerBuyer("BuyerA", "@BuyerA", "BA A/C", "BA-auth"));       
        assertOK(house.registerBuyer("BuyerB", "@BuyerB", "BB A/C", "BB-auth"));
        assertOK(house.registerBuyer("BuyerC", "@BuyerC", "BC A/C", "BC-auth"));
        if (endPoint == 3) return;
        
        assertOK(house.noteInterest("BuyerA", 1));
        assertOK(house.noteInterest("BuyerA", 5));
        assertOK(house.noteInterest("BuyerB", 1));
        assertOK(house.noteInterest("BuyerB", 2));
        if (endPoint == 4) return;
        
        assertOK(house.openAuction("Auctioneer1", "@Auctioneer1", 1));

        messagingService.expectAuctionOpened("@BuyerA", 1);
        messagingService.expectAuctionOpened("@BuyerB", 1);
        messagingService.expectAuctionOpened("@SellerY", 1);
        messagingService.verify(); 
        if (endPoint == 5) return;
        
        Money m70 = new Money("70.00");
        assertOK(house.makeBid("BuyerA", 1, m70));
        
        messagingService.expectBidReceived("@BuyerB", 1, m70);
        messagingService.expectBidReceived("@Auctioneer1", 1, m70);
        messagingService.expectBidReceived("@SellerY", 1, m70);
        messagingService.verify();
        if (endPoint == 6) return;
        
        Money m100 = new Money("100.00");
        assertOK(house.makeBid("BuyerB", 1, m100));

        messagingService.expectBidReceived("@BuyerA", 1, m100);
        messagingService.expectBidReceived("@Auctioneer1", 1, m100);
        messagingService.expectBidReceived("@SellerY", 1, m100);
        messagingService.verify();
        if (endPoint == 7) return;
        
        assertSale(house.closeAuction("Auctioneer1",  1));
        messagingService.expectLotSold("@BuyerA", 1);
        messagingService.expectLotSold("@BuyerB", 1);
        messagingService.expectLotSold("@SellerY", 1);
        messagingService.verify();       

        bankingService.expectTransfer("BB A/C",  "BB-auth",  "AH A/C", new Money("110.00"));
        bankingService.expectTransfer("AH A/C",  "AH-auth",  "SY A/C", new Money("85.00"));
        bankingService.verify();
        if (endPoint == 8) return;
        //testing our implementation
        //testing having duplicated buyers
        assertError(house.registerBuyer("BuyerA", "@BuyerA", "BA A/C", "BA-auth"));
        if (endPoint == 9) return;
        
        //test having duplicate lots
        assertError(house.addLot("SellerY", 2, "Painting", new Money("200.00")));
        if (endPoint == 10) return;
       
        //adding new lots and buyers noting interest in those lots. this has already been tested
        assertOK(house.registerBuyer("BuyerD", "@BuyerD", "BAD", "BA-auth"));
        assertOK(house.addLot("SellerY", 3, "Painting", new Money("150.00")));
        assertOK(house.addLot("SellerY", 6, "Painting", new Money("150.00")));
        assertOK(house.addLot("SellerY", 7, "Painting", new Money("150.00")));
        assertOK(house.addLot("SellerY", 8, "Painting", new Money("50.00")));
        assertOK(house.noteInterest("BuyerA", 3));
        assertOK(house.noteInterest("BuyerB", 3));
        assertOK(house.noteInterest("BuyerA", 6));
        assertOK(house.noteInterest("BuyerB", 6));
        assertOK(house.noteInterest("BuyerA", 7));
        assertOK(house.noteInterest("BuyerD", 7));
        assertOK(house.noteInterest("BuyerA", 8));
        assertOK(house.noteInterest("BuyerB", 8));
        if (endPoint == 11) return;
        
        //testing bid is lower than previous
        assertOK(house.openAuction("Auctioneer7", "@Auctioneer7", 3));

        messagingService.expectAuctionOpened("@BuyerA", 3);
        messagingService.expectAuctionOpened("@BuyerB", 3);
        messagingService.expectAuctionOpened("@SellerY", 3);
        messagingService.verify();
        
        
        Money m90 = new Money("90.00");
        assertOK(house.makeBid("BuyerA", 3, m90));
        messagingService.expectBidReceived("@BuyerB", 3, m90);
        messagingService.expectBidReceived("@Auctioneer7", 3, m90);
        messagingService.expectBidReceived("@SellerY", 3, m90);
        messagingService.verify();
        
        Money m80 = new Money("80.00");        
        assertError(house.makeBid("BuyerB", 3, m80));
        if (endPoint == 12) return;
        
        //checking case when hammerPrice is lower than reservePrice
        assertOK(house.openAuction("Auctioneer2", "@Auctioneer2", 6));

        messagingService.expectAuctionOpened("@BuyerA", 6);
        messagingService.expectAuctionOpened("@BuyerB", 6);
        messagingService.expectAuctionOpened("@SellerY", 6);
        messagingService.verify();
        
        assertOK(house.makeBid("BuyerA", 6, m80));
        messagingService.expectBidReceived("@BuyerB", 6, m80);
        messagingService.expectBidReceived("@Auctioneer2", 6, m80);
        messagingService.expectBidReceived("@SellerY", 6, m80);
        messagingService.verify();
        
        assertNoSale(house.closeAuction("Auctioneer2",  6));
        messagingService.expectLotUnsold("@BuyerA", 6);
        messagingService.expectLotUnsold("@BuyerB", 6);
        messagingService.expectLotUnsold("@SellerY", 6);
        messagingService.verify();       
        
        if (endPoint == 13) return;
        
        //checking case where buyer payment doesn't go through
        assertOK(house.openAuction("Auctioneer2", "@Auctioneer2", 7));

        messagingService.expectAuctionOpened("@BuyerA", 7);
        messagingService.expectAuctionOpened("@BuyerD", 7);
        messagingService.expectAuctionOpened("@SellerY", 7);
        messagingService.verify();
        
        Money m200 = new Money("200.00");
        assertOK(house.makeBid("BuyerD", 7, m200));
        messagingService.expectBidReceived("@BuyerA", 7, m200);
        messagingService.expectBidReceived("@Auctioneer2", 7, m200);
        messagingService.expectBidReceived("@SellerY", 7, m200);
        messagingService.verify();
        
        bankingService.setBadAccount("BAD");
        assertPendingPayment(house.closeAuction("Auctioneer2",  7));
        if (endPoint == 14) return;
        
        assertOK(house.openAuction("Auctioneer2", "@Auctioneer2", 8));

        messagingService.expectAuctionOpened("@BuyerA", 8);
        messagingService.expectAuctionOpened("@BuyerB", 8);
        messagingService.expectAuctionOpened("@SellerY", 8);
        messagingService.verify();
        
        assertOK(house.makeBid("BuyerA", 8, m80));
        messagingService.expectBidReceived("@BuyerB", 8, m80);
        messagingService.expectBidReceived("@Auctioneer2", 8, m80);
        messagingService.expectBidReceived("@SellerY", 8, m80);
        messagingService.verify();
        
        assertError(house.closeAuction("Auctioneer1",  8));
    }
    
    @Test
    public void testEmptyCatalogue() {
        logger.info(makeBanner("emptyLotStore"));

        List<CatalogueEntry> expectedCatalogue = new ArrayList<CatalogueEntry>();
        List<CatalogueEntry> actualCatalogue = house.viewCatalogue();

        assertEquals(expectedCatalogue, actualCatalogue);

    }

    @Test
    public void testRegisterSeller() {
        logger.info(makeBanner("testRegisterSeller"));
        runStory(1);
    }

    @Test
    public void testRegisterSellerDuplicateNames() {
        logger.info(makeBanner("testRegisterSellerDuplicateNames"));
        runStory(1);     
        assertError(house.registerSeller("SellerY", "@SellerZ", "SZ A/C"));       
    }

    @Test
    public void testAddLot() {
        logger.info(makeBanner("testAddLot"));
        runStory(2);
    }
    
    @Test
    public void testViewCatalogue() {
        logger.info(makeBanner("testViewCatalogue"));
        runStory(2);
        
        List<CatalogueEntry> expectedCatalogue = new ArrayList<CatalogueEntry>();
        expectedCatalogue.add(new CatalogueEntry(1, "Bicycle", LotStatus.UNSOLD)); 
        expectedCatalogue.add(new CatalogueEntry(2, "Painting", LotStatus.UNSOLD));
        expectedCatalogue.add(new CatalogueEntry(5, "Table", LotStatus.UNSOLD));

        List<CatalogueEntry> actualCatalogue = house.viewCatalogue();

        assertEquals(expectedCatalogue, actualCatalogue);
    }

    @Test
    public void testRegisterBuyer() {
        logger.info(makeBanner("testRegisterBuyer"));
        runStory(3);       
    }

    @Test
    public void testNoteInterest() {
        logger.info(makeBanner("testNoteInterest"));
        runStory(4);
    }
      
    @Test
    public void testOpenAuction() {
        logger.info(makeBanner("testOpenAuction"));
        runStory(5);       
    }
      
    @Test
    public void testMakeBid() {
        logger.info(makeBanner("testMakeBid"));
        runStory(7);
    }
  
    @Test
    public void testCloseAuctionWithSale() {
        logger.info(makeBanner("testCloseAuctionWithSale"));
        runStory(8);
    }
    
    @Test
    public void testDuplicateBuyer() {
    	logger.info(makeBanner("testDuplicateBuyer"));
    	runStory(9);
    }
    
    @Test
    public void testDuplicateLotNumber() {
    	logger.info(makeBanner("testDuplicateLotNumber"));
    	runStory(10);
    }
    
    @Test
    public void testNoteInterestOnNewLot() {
    	logger.info(makeBanner("testNoteInterestOnNewLot"));
    	runStory(11);
    }
    
    @Test
    public void testBidTooLow() {
    	logger.info(makeBanner("testBidTooLow"));
    	runStory(12);
    }
    
    @Test
    public void testHammerPriceTooLow() {
    	logger.info(makeBanner("testHammerPriceTooLow"));
    	runStory(13);
    }
    
    @Test
    public void testBadAccount() {
    	logger.info(makeBanner("testBadAccount"));
    	runStory(14);
    }
    
    @Test
    public void testWrongAuctioneerCloses() {
    	logger.info(makeBanner("testBadAccount"));
    	runStory(15);
    }
     
}
