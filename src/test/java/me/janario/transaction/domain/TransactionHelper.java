package me.janario.transaction.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionHelper {
	@Autowired
	private TransactionService transactionService;

	public void cleanAll() {
		//keep it package level to use only on test
		transactionService.cleanAll();
	}
}