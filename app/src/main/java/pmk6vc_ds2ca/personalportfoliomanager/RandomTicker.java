package pmk6vc_ds2ca.personalportfoliomanager;

import java.util.Random;

/**
 * Created by Ted on 4/28/2016.
 */
public class RandomTicker {
    private String[] tickers = {
            "FOXA", "FOX", "ATVI", "ADBE", "AKAM", "ALXN",
            "GOOGL", "GOOG", "AMZN", "AAL", "AMGN",
            "ADI", "AAPL", "AMAT", "ADSK", "ADP",
            "BIDU", "BBBY", "BIIB", "BMRN", "AVGO",
            "CA", "CELG", "CERN", "CHTR", "CHKP",
            "CSCO", "CTXS", "CTSH", "CMCSA", "COST",
            "CSX", "CTRP", "DISCA", "DISCK", "DISH",
            "DLTR", "EBAY", "EA", "ENDP", "EXPE",
            "ESRX", "FB", "FAST", "FISV", "GILD",
            "HSIC", "ILMN", "INCY", "INTC", "INTU",
            "ISRG", "JD", "KHC", "LRCX", "BATRA",
            "BATRK", "LBTYA", "LBTYK", "QVCA", "LMCK",
            "LMCA", "LVNTA", "LLTC", "MAR", "MAT",
            "MXIM", "MU", "MSFT", "MDLZ", "MNST",
            "MYL", "NTAP", "NTES", "NFLX", "NCLH",
            "NVDA", "NXPI", "ORLY", "PCAR", "PYPL",
            "PAYX", "QCOM", "REGN", "ROST", "SBAC",
            "STX", "SIRI", "SWKS", "SBUX", "SRCL",
            "SYMC", "TSLA", "TXN", "PCLN", "TMUS",
            "TSCO", "TRIP", "ULTA", "VRSK", "VRTX",
            "VIAB", "VOD", "WBA", "WDC", "WFM",
            "XLNX", "YHOO"
    };

    public RandomTicker() {

    }

    public String get() {
        int idx = (int)(Math.random() * this.tickers.length);
        return this.tickers[idx].toLowerCase();
    }
}
