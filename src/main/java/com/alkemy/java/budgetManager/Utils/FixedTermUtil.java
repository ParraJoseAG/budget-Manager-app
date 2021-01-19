package com.alkemy.java.budgetManager.Utils;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.alkemy.java.budgetManager.entities.FixedTermEntity;

@Component
public class FixedTermUtil {

	public BigDecimal getAmountGenerated(FixedTermEntity fixedTerm) {

		final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
		final double factor = 0.01;
		fixedTerm.setFinalDate(new Date());

		long deltaDays = (fixedTerm.getFinalDate().getTime() - fixedTerm.getStartDate().getTime()) / MILLSECS_PER_DAY;

		BigDecimal amountGenerated = fixedTerm.getAmountFixedTerm()
				.multiply(new BigDecimal(1).add(new BigDecimal(deltaDays).multiply(new BigDecimal(factor))));

		return amountGenerated;

	}

}
