# PersonalFinancePortfolio

The purpose of this Android application is to provide a mobile tool that allows users to quickly and conveniently manage a hypothetical personal portfolio. The app allows users to quickly look up a given stock’s latest financial information and key metrics, such as current price, percent change, price to earnings ratio, earnings per share, etc. The app also allows users to add a given stock position (where a position is defined by a stock and number of shares) into a portfolio, which is a central hub of aggregated stock data. Users can then look at a breakdown of their portfolio positions by weight as well as basic portfolio metrics. 

Key Features

1) Pulling stock information from Yahoo! Finance

This feature, accessible by clicking the “Manage Portfolio” button from the start screen, translates ticker symbols to stock information. The user is given a field in which to enter a stock’s ticker symbol. He/she can then click the “Find Data” button to get a range of information about the stock. The application programmatically validates the input to save users from themselves.



2) Adding positions to a central database

If the user has entered an appropriate ticker and a valid number of shares (both are checked by the program), then he/she has the option of adding the proposed position to the central portfolio. Only positions that the user chooses to add to the portfolio will be saved in core data, meaning that intermittent searches are not saved. 


3) Summarizing the portfolio stored in the core database

The app summarizes some of the information related to the positions stored in the portfolio. Specifically, the initial screen displays a pie chart that breaks down each position by its relative weight in the portfolio. The app also posts the portfolio’s total hypothetical value, the total price to earnings ratio, and the total earnings per share in the portfolio. 


4) Finding the nearest financial advisors

Because the user of the application would realistically be someone who invests time into studying the stock market and other financial matters, he/she would be able to seek help if they want. The device would determine the current location of the user and list nearby financial advisers as well as their rating. 


5) Shaking for random stock information

The device can be shaken on the Manage Portfolio screen to display a random stock’s information from a prepopulated list. The shaking functionality is made possible with use of another external API from Square. It is called ShakeListener and it allows the device to detect the shaking movement and then carry out an action.
