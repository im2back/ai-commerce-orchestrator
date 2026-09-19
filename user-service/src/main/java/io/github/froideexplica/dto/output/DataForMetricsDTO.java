package io.github.froideexplica.dto.output;

import java.util.List;

public record DataForMetricsDTO(
		Double totalValueForLastMonth,
		Double partialValueForCurrentMonth,
		Double partialValueForCurrentDay,
		Double totalOutstandingAmount,
		List<DailyTotalDTO> dataGraphicSevenDays 
		) {

}
