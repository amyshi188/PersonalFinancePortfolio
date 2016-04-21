package pmk6vc_ds2ca.personalportfoliomanager;

/**
 * Created by varunkulkarni122 on 4/7/16.
 *
 * General idea
 * Abstract away all issues of querying DB
 * This class will be used to hold the data queried from DB for easy use
 *
 * FIELDS
 * Name
 * Ticker
 * currentPrice (needs to be updated on query)
 * # of shares
 * % change
 * 52 week low, high
 * EPS
 * PE
 *
 * METHODS
 * Constructor with all parameters
 * All values should be updated on query
 * Handle query updates in Activity instead of this class
 * Getters
 */
public class Stock {

    // Fields
    private String name;
    private String ticker;
    private double currentPrice;
    private int numShares;
    private double pctchange;
    private double yearlow, yearhigh;
    private double eps, peratio;

    // Full constructor
    public Stock(String name, String ticker, double currentPrice, int numShares, double pctchange, double yearlow, double yearhigh, double eps, double peratio) {
        this.name = name;
        this.ticker = ticker;
        this.currentPrice = currentPrice;
        this.numShares = numShares;
        this.pctchange = pctchange;
        this.yearlow = yearlow;
        this.yearhigh = yearhigh;
        this.eps = eps;
        this.peratio = peratio;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getTicker() {
        return ticker;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public int getNumShares() {
        return numShares;
    }

    public double getPctchange() {
        return pctchange;
    }

    public double getYearlow() {
        return yearlow;
    }

    public double getYearhigh() {
        return yearhigh;
    }

    public double getEps() {
        return eps;
    }

    public double getPEratio() {
        return peratio;
    }
}
