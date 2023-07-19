package discord.util;

import discord.util.BitFlagSet.BitFlag;

public class BitFlagSet<T extends BitFlag> {
	public static interface BitFlag {
		long value();
	}

	private long bitset;

	public BitFlagSet(long bitset) {
		this.bitset = bitset;
	}

	@Override
	public String toString() {
		return Long.toBinaryString(bitset);
	}

	public void toggle(T flag) {
		bitset ^= flag.value();
	}

	public void toggle(int bitIndex) {
		bitset ^= (1 << bitIndex);
	}

	public boolean has(T flag) {
		return (bitset & flag.value()) != 0;
	}

	public long bitAt(int bitIndex) {
		return bitset & (1 << bitIndex);
	}

	public long asLong() {
		return bitset;
	}

	public static void main(String[] args) {
		final var bs = new BitFlagSet<>(0b1001);
		bs.toggle(2);
		System.out.println(bs);
		bs.toggle(2);
		System.out.println(bs);
	}
}
