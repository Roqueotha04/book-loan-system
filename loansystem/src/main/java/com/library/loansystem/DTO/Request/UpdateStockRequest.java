package com.library.loansystem.DTO.Request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateStockRequest(@Min(0) @Max(100) int stock) {
}
