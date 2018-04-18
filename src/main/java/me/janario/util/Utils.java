package me.janario.util;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

import me.janario.transaction.domain.TransactionResponseDto;

public final class Utils {
	private Utils() {
	}

	public static void doWithLock(Lock locker, Runnable run) {
		try {
			locker.lock();
			run.run();
		} finally {
			locker.unlock();
		}
	}

	public static BigDecimal safeGet(Supplier<TransactionResponseDto> supplier) {
		try {
			return supplier.get().getAmount();
		} catch (NoSuchElementException e) {
			return null;
		}
	}
}
