package com.alkemy.java.budgetManager.Utils;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.alkemy.java.budgetManager.entities.FixedTermEntity;
import com.alkemy.java.budgetManager.entities.OperationEntity;
import com.alkemy.java.budgetManager.models.Type;

@Component
public class FixedTermUtil {

	public BigDecimal getAmountGenerated(FixedTermEntity fixedTerm) {

		final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
		final double factor = 0.01;
		fixedTerm.setFinalDate(new Date());

		long deltaDays = ((fixedTerm.getFinalDate().getTime() - fixedTerm.getStartDate().getTime()) / MILLSECS_PER_DAY);

		BigDecimal amountGenerated = fixedTerm.getAmountFixedTerm()
				.multiply(new BigDecimal(1).add(new BigDecimal(deltaDays).multiply(new BigDecimal(factor))));

		return amountGenerated;

	}

	public OperationEntity getOperation(Type type, FixedTermEntity fixedTerm) {

		OperationEntity operationFixedTerm = new OperationEntity();
		operationFixedTerm.setDate(new Date());
		operationFixedTerm.setPerson(fixedTerm.getPerson());

		if (type.equals(Type.EXPENSES)) {

			operationFixedTerm.setConcept("INVERSIÓN A PLAZO FIJO");
			operationFixedTerm.setAmount(fixedTerm.getAmountFixedTerm());
			operationFixedTerm.setType(Type.EXPENSES);
		} else {

			operationFixedTerm.setConcept("DEPOSITO DE LA INVERSIÓN");
			operationFixedTerm.setAmount(getAmountGenerated(fixedTerm));
			operationFixedTerm.setType(Type.INGRESS);
		}

		return operationFixedTerm;
	}

}
