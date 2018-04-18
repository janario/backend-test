package me.janario.util;

import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

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

	public static <R> R safeGet(Supplier<R> supplier) {
		try {
			return supplier.get();
		} catch (NoSuchElementException e) {
			return null;
		}
	}
}
