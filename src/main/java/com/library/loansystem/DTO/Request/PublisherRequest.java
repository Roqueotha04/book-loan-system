package com.library.loansystem.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record PublisherRequest(@Size(max = 50) @NotBlank String name) {

}
