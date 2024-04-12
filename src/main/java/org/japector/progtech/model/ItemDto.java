package org.japector.progtech.model;

import java.util.StringJoiner;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ItemDto {

    @NotNull
    private String name;
    @Positive
    private Double price;
    @PositiveOrZero
    private Integer quantity;

    @Override
    public String toString() {
        return new StringJoiner(", ", ItemDto.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("price=" + price)
                .add("quantity=" + quantity)
                .toString();
    }
}
