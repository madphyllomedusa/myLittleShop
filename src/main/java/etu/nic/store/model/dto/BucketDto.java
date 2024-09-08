package etu.nic.store.model.dto;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class BucketDto {
	private Long amountProducts;
	private BigDecimal sum;
}
