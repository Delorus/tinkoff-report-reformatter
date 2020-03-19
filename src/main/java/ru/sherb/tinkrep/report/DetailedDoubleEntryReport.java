package ru.sherb.tinkrep.report;

import lombok.Builder;
import lombok.Value;
import ru.sherb.tinkrep.model.Operation;

import java.util.List;

/**
 * | Инструмент | Продажа | Покупка | Частично | Комиссия | Результат |
 * | ---------- | ------- | ------- | -------- | -------- | --------- |
 * |     Лукойл |    100р |     50р |       Да |       1p |       49p |
 * |        ММК |     50p |     50p |      Нет |       1p |       -1p |
 * | Итого:     |    150p |    100p |          |       2p |       48p |
 *
 * @author maksim
 * @since 17.03.2020
 */
@Value
public class DetailedDoubleEntryReport {

    public static DetailedDoubleEntryReport build(List<Operation> operations) {
        return new DetailedDoubleEntryReportBuilder(operations).build();
    }

    @Value
    @Builder
    public static class Row {
        String tradingToolCode;
        String sell;
        String buy;
        String commission;
        String result;
        String currency;
    }

    List<Row> rows;

    DetailedDoubleEntryReport(List<Row> rows) {
        this.rows = rows;
    }


}
