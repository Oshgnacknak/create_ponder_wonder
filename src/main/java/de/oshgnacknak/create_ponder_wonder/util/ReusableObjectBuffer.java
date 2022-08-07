package de.oshgnacknak.create_ponder_wonder.util;

import de.oshgnacknak.create_ponder_wonder.CreatePonderWonder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class ReusableObjectBuffer<T> {
	private final int maxSize;
	private final Supplier<T> supplier;
	private final Queue<T> queue;

	public ReusableObjectBuffer(int maxSize, Supplier<T> supplier) {
		this.maxSize = maxSize;
		this.supplier = supplier;
		this.queue = new ConcurrentLinkedQueue<>();
	}

	public T get() {
		T object = queue.poll();
		if (object == null) {
			object = supplier.get();
		}
		return object;
	}

	public void put(T object) {
		if (queue.size() < maxSize) {
			queue.add(object);
		} else if (object instanceof AutoCloseable ac) {
			try {
				ac.close();
			} catch (Exception e) {
				CreatePonderWonder.LOGGER.error("Error closing object", e);
			}
		}
	}

	public void clear() {
		// if queue contains auto-closables, close them
		while (!queue.isEmpty()) {
			T object = queue.poll();
			if (object instanceof AutoCloseable ac) {
				try {
					ac.close();
				} catch (Exception e) {
					CreatePonderWonder.LOGGER.error("Error closing object", e);
				}
			}
		}
	}
}
