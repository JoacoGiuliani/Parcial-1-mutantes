package org.parcial_1.dtos;


import lombok.*;
import org.parcial_1.validators.ValidDna;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class DnaRequest {

    @ValidDna
    private String[] dna;

}
