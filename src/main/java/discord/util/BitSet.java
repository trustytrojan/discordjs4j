package discord.util;

import discord.util.BitSet.Enum;

public class BitSet<T extends Enum> {
	public static interface Enum {
		long value();
	}

	private long bitset;

	public BitSet(long bitset) {
		this.bitset = bitset;
	}

	public void toggle(T flag) {
		bitset ^= flag.value();
	}

	public boolean has(T flag) {
		return (bitset & flag.value()) != 0;
	}

	public long asLong() {
		return bitset;
	}
}
