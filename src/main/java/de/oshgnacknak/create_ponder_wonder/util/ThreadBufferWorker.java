package de.oshgnacknak.create_ponder_wonder.util;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class ThreadBufferWorker<T> implements AutoCloseable {
	private final ThreadPoolExecutor executor;

	// constructor taking a taskConsumer of T and a thread number
	protected ThreadBufferWorker(int threadNumber) {

		executor = new ScheduledThreadPoolExecutor(threadNumber);
	}

	// default constructor calculating thread number based on available processors (min(1, available processors-1))
	protected ThreadBufferWorker() {
		this(Math.min(1, Runtime.getRuntime().availableProcessors() - 1));
	}

	public void submitTask(T task) {
		// FIXME work with promises
		while (executor.getActiveCount() >= getThreadSize()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		executor.submit(() -> this.runTask(task));
	}

	public abstract void runTask(T task);

	// get thread size
	public int getThreadSize() {
		return executor.getCorePoolSize();
	}

	@Override
	public void close() {
		// Fixme work with promises
		while (executor.getActiveCount() > 0) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		executor.shutdown();
	}
}
