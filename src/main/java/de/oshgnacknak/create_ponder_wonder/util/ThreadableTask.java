package de.oshgnacknak.create_ponder_wonder.util;

@FunctionalInterface
public interface ThreadableTask<T> extends AutoCloseable {
	void runTask(T t);

	default void close(){}
}
