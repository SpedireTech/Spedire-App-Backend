package com.spedire.Spedire.dtos.responses;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
@Setter
public class SavedAddressResponse {

    private List<String> savedAddress;

}
