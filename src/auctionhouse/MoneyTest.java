/**
 * 
 */
package auctionhouse;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author pbj
 *
 */
public class MoneyTest {

    @Test    
    public void testAdd() {
        Money val1 = new Money("12.34");
        Money val2 = new Money("0.66");
        Money result = val1.add(val2);
        assertEquals("13.00", result.toString());
    }

    /*
     ***********************************************************************
     * BEGIN MODIFICATION AREA
     ***********************************************************************
     * Add all your JUnit tests for the Money class below.
     */
     @Test
     public void testSubtract() {
    	 Money val1 = new Money("12.50");
    	 Money val2 = new Money("0.50");
    	 Money result = val1.subtract(val2);
    	 assertEquals("12.00", result.toString());
     }
     
     @Test
     public void testAddPercent() {
    	 Money val1 = new Money("10.00");
    	 double percent = 10;
    	 Money result = val1.addPercent(percent);
    	 assertEquals("11.00", result.toString());
     }
     
     @Test
     public void testCompareTo

    /*
     * Put all class modifications above.
     ***********************************************************************
     * END MODIFICATION AREA
     ***********************************************************************
     */


}
