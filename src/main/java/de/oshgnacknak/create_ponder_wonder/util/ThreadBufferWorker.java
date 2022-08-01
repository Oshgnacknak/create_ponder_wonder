package de.oshgnacknak.create_ponder_wonder.util;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadBufferWorker<T> implements AutoCloseable {
	private ThreadableTask<T> taskConsumer;

	private final ThreadPoolExecutor executor;

	// constructor taking a taskConsumer of T and a thread number
	public ThreadBufferWorker(int threadNumber, ThreadableTask<T> taskConsumer) {

		this.taskConsumer = taskConsumer;
		executor = new ScheduledThreadPoolExecutor(threadNumber);
	}

	public void runTask(T task) {
		// FIXME
		while (executor.getActiveCount() >= executor.getCorePoolSize()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		executor.submit(() -> taskConsumer.runTask(task));
	}

	@Override
	public void close() {
		// Fixme
		while (executor.getActiveCount() > 0) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		taskConsumer.close();
		executor.shutdown();
	}
}
