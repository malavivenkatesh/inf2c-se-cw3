/**
 * 
 */
package auctionhouse;

/**
 * Money is an object which encapsulates a double or String value that 
 * represents a monetary value in pounds. It allows arithmetic and comparisons
 * between different instances.
 * 
 * @author pbj
 */
public class Money implements Comparable<Money> {
    /**
     * Holds the value of the instance of Money
     */
    private double value;
    
    /**
     * Converts the input value from pounds to a value in pence, rounding
     * 
     * @param  pounds   the input value in pounds as a double
     * @return the value of the method parameter multiplied by 100
     */
    private static long getNearestPence(double pounds) {
        return Math.round(pounds * 100.0);
    }
    
    /**
     * Converts the input value from pounds as a double back to pounds as a double,
     * rounding in the process so there are only 2 decimal places in the return value
     * 
     * @param  pounds    the input value in pounds as a double
     * @return the value of pounds as a double, rounded to 2 decimal 
     *         places by getNearestPence
     */
    private static double normalise(double pounds) {
        return getNearestPence(pounds)/100.0;
        
    }
 
    public Money(String pounds) {
        value = normalise(Double.parseDouble(pounds));
    }
    
    private Money(double pounds) {
        value = pounds;
    }
    
    public Money add(Money m) {
        return new Money(value + m.value);
    }
    
    public Money subtract(Money m) {
        return new Money(value - m.value);
    }
 
    public Money addPercent(double percent) {
        return new Money(normalise(value * (1 + percent/100.0)));
    }
     
    @Override
    public String toString() {
        return String.format("%.2f", value);
        
    }
    public int compareTo(Money m) {
        return Long.compare(getNearestPence(value),  getNearestPence(m.value)); 
    }
    
    public Boolean lessEqual(Money m) {
        return compareTo(m) <= 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Money)) return false;
        Money oM = (Money) o;
        return compareTo(oM) == 0;       
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(getNearestPence(value));
    }
      

}
