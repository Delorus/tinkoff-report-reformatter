package ru.sherb.tinkrep.model;

import lombok.Builder;
import lombok.Value;

import javax.money.MonetaryAmount;
import java.time.LocalDate;

/**
 * @author maksim
 * @since 15.03.2020
 */
@Builder
@Value
public class Operation {

    LocalDate date;
    OperationType type;
    MonetaryAmount price;
    String toolId;
    Count count;
    MonetaryAmount summary;
    MonetaryAmount commission;

    public Operation part(Count count) {
        return new Operation(date, type, price, toolId, count, price.multiply(count), commission);
    }
}
