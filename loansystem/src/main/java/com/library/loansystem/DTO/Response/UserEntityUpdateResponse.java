package com.library.loansystem.DTO.Response;

public record UserEntityUpdateResponse (UserEntityResponse user, String newToken) {
}
