package ru.sherb.tinkrep.model;

import javax.money.MonetaryAmount;
import java.util.List;

/**
 * @author maksim
 * @since 15.03.2020
 */
public class Portfolio {

    private List<PortfolioItem> tools;

}

final class PortfolioItem {
    private final TradingTool tool;
    private final MonetaryAmount avgCost;

    PortfolioItem(TradingTool tool, MonetaryAmount avgCost) {
        this.tool = tool;
        this.avgCost = avgCost;
    }
}
