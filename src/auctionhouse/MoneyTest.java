/**
 * 
 */
package auctionhouse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
     
     Money val1 = new Money("5.00");
     Money val2 = new Money("6.00");
     Money val3 = new Money("5.00");
     
     @Test
     public void testCompareToLessThan() {
         int result = val1.compareTo(val2);
         assertTrue(result < 0);
     }
     
     @Test
     public void testCompareToEqual() {
         int result = val1.compareTo(val3);
         assertTrue(result == 0);
     }
     
     @Test
     public void testCompareToGreaterThan() {
         int result = val2.compareTo(val1);
         assertTrue(result > 0);
     }
     
     @Test
     public void testLessEqualLessThan() {
         Boolean result = val1.lessEqual(val2);
         assertEquals("true", result.toString());
     }
     
     @Test
     public void testLessEqualEqual() {
         Boolean result = val1.lessEqual(val3);
         assertEquals("true", result.toString());
     }
     
     @Test
     public void testLessEqualGreaterThan() {
         Boolean result = val2.lessEqual(val1);
         assertEquals("false", result.toString());
     }
     
     @Test
     public void testEqualsNotEqual() {
         Boolean result = val1.equals(val2);
         assertEquals("false", result.toString());
     }
     
     @Test
     public void testEqualsEqual() {
         Boolean result = val1.equals(val3);
         assertEquals("true", result.toString()); 
     }

    /*
     * Put all class modifications above.
     ***********************************************************************
     * END MODIFICATION AREA
     ***********************************************************************
     */


}
