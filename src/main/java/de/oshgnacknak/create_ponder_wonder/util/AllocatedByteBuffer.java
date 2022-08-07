package de.oshgnacknak.create_ponder_wonder.util;

import org.lwjgl.system.MemoryUtil;

public class AllocatedByteBuffer implements AutoCloseable {
	private final long capacity;
	private long address;

	// constructor taking the size
	public AllocatedByteBuffer(long capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("capacity must not be empty");
		}
		this.capacity = capacity;
	}

	public long getAllocatedAddress() {
		if (address == 0) {
			allocateAddress();
		}
		return address;
	}

	public void assertSize(long size) {
		if (size > capacity) {
			throw new IllegalArgumentException("size must not be greater than capacity");
		}
	}

	private void allocateAddress() {
		address = MemoryUtil.nmemAlloc(capacity);
	}

	@Override
	public void close() {
		if (address != 0) {
			MemoryUtil.nmemFree(address);
			address = 0;
		}
	}

	public long getSize() {
		return capacity;
	}
}
