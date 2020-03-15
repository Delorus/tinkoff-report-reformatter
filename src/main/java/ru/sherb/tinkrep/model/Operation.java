package ru.sherb.tinkrep.model;

import javax.money.MonetaryAmount;
import java.time.LocalDate;

/**
 * @author maksim
 * @since 15.03.2020
 */
public class Operation {

    private LocalDate date;
    private OperationType type;
    private MonetaryAmount price;
    private TradingTool item;
    private Count count;
    private MonetaryAmount summary;

}
