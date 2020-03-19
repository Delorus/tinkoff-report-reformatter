package ru.sherb.tinkrep.report;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.AmountFormatParams;
import ru.sherb.tinkrep.model.Count;
import ru.sherb.tinkrep.model.Operation;
import ru.sherb.tinkrep.model.OperationType;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author maksim
 * @since 17.03.2020
 */
public class DetailedDoubleEntryReportBuilder {

    private static final MonetaryAmountFormat MONETARY_FORMAT = MonetaryFormats
            .getAmountFormat(AmountFormatQueryBuilder.of(Locale.getDefault())
                    .set(AmountFormatParams.PATTERN, "0.00")
                    .build());

    private final List<Operation> operations;

    public DetailedDoubleEntryReportBuilder(List<Operation> operations) {
        this.operations = operations;
    }

    public DetailedDoubleEntryReport build() {
        Map<String, List<Operation>> groupByTool = operations.stream()
                .collect(Collectors.groupingBy(Operation::getToolId));

        List<DetailedDoubleEntryReport.Row> result = new ArrayList<>();
        groupByTool.forEach((id, ops) -> {
            CurrencyUnit currency = ops.get(0).getPrice().getCurrency();

            ops.sort(Comparator.comparing(Operation::getDate));

            List<Operation> sells = new ArrayList<>();
            for (Operation op : ops) {
                if (op.getType() == OperationType.SELL) {
                    sells.add(op);
                }
            }
            MonetaryAmount sell = sells.stream()
                    .map(Operation::getSummary)
                    .reduce(MonetaryAmount::add)
                    .orElse(Money.zero(currency));

            int sellCount = sells.stream()
                    .map(Operation::getCount)
                    .mapToInt(Count::intValue)
                    .sum();

            List<Operation> buys = new ArrayList<>();
            for (Operation op : ops) {
                if (op.getType() == OperationType.BUY) {
                    if (op.getCount().intValue() <= sellCount) {
                        buys.add(op);
                        sellCount -= op.getCount().intValue();
                    } else {
                        int rest = sellCount - op.getCount().intValue();
                        if (rest < 0) {
                            break;
                        }
                        buys.add(op.part(new Count(rest)));
                        sellCount -= rest;
                    }
                }
            }

            MonetaryAmount buy = buys.stream()
                    .map(Operation::getSummary)
                    .reduce(MonetaryAmount::add)
                    .orElse(Money.zero(currency));

            MonetaryAmount commission = Stream.concat(sells.stream(), buys.stream())
                    .map(Operation::getCommission)
                    .reduce(MonetaryAmount::add)
                    .orElse(Money.zero(currency));

            MonetaryAmount res = sell.subtract(buy).subtract(commission);

            if (!buys.isEmpty()) {
                result.add(new DetailedDoubleEntryReport.Row(id,
                        MONETARY_FORMAT.format(sell),
                        MONETARY_FORMAT.format(buy),
                        MONETARY_FORMAT.format(commission),
                        MONETARY_FORMAT.format(res),
                        currency.getCurrencyCode()));
            }
        });

        return new DetailedDoubleEntryReport(result);
    }

}
