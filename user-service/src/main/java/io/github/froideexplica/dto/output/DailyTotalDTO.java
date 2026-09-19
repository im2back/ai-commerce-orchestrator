package io.github.froideexplica.dto.output;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyTotalDTO(LocalDate purchaseDate, BigDecimal totalValue)

{

}