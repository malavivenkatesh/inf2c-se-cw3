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
    /**
     * Subtracts the values of one instance of Money from another
     * @param m    the value to be subtracted from the instance Money is called on
     * @return     a new instance of Money with its value as the result of the calculation
     */
    public Money subtract(Money m) {
        return new Money(value - m.value);
    }
    /**
     * Adds a percentage of the value of the instance Money is called on to the original value 
     * @param percent    the percentage to add to the value of Money
     * @return  a new instance of Money with the new increased value
     */
    public Money addPercent(double percent) {
        return new Money(normalise(value * (1 + percent/100.0)));
    }
    /**
     * Converts the instance of Money to a string of the specified format
     *@return   a String of the format "value"
     */
    @Override
    public String toString() {
        return String.format("%.2f", value);
    /**
     * Compares two instances of Money by calling getNearestPence on their values
     * @param m    the value to be compared against
     * @return     an int specified by the compare function for Longs
     */
    }
    public int compareTo(Money m) {
        return Long.compare(getNearestPence(value),  getNearestPence(m.value)); 
    }
    /**
     * Checks if one instance of Money is greater than the other
     * @param m    the instance of Money to be compared against
     * @return     True if m is greater than the instance of Money the function was called on
     */
    public Boolean lessEqual(Money m) {
        return compareTo(m) <= 0;
    }
    /**
     * Checks if the parameter o has a monetary value of 0 and returns false if it does not
     * or if it is not 
     * an instance of Money 
     * @param o    Object to be checked
     * @return     true if the object is an instance of Money and if it has a monetary 
     *             value of 0 
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Money)) return false;
        Money oM = (Money) o;
        return compareTo(oM) == 0;       
    }
    /**
     * Returns the hashcode of the monetary value of the instance of Money
     * @return    hashcode of value
     */
    @Override
    public int hashCode() {
        return Long.hashCode(getNearestPence(value));
    }
      

}
