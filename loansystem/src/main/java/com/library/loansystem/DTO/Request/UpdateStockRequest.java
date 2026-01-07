package com.library.loansystem.DTO.Request;

import jakarta.validation.constraints.Min;

public record UpdateStockRequest(@Min(0) int stock) {
}
