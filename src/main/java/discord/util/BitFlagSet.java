package discord.util;

import discord.util.BitFlagSet.BitFlagEnum;

/**
 * A simple recreation of {@code java.util.BitSet} that
 * supports the use of enums that implement {@code BitFlag}.
 */
public class BitFlagSet<T extends BitFlagEnum> {
	/**
	 * An interface requiring enums to return a {@code long}
	 * that has only one bit as a {@code 1}, for use with
	 * {@code BitFlagSet}.
	 */
	public static interface BitFlagEnum {
		long getBit();
	}

	private long bitset;

	public BitFlagSet(long bitset) {
		this.bitset = bitset;
	}

	@Override
	public String toString() {
		return Long.toBinaryString(bitset);
	}

	public void set(T flag) {
		bitset |= flag.getBit();
	}

	public void set(int bitIndex) {
		bitset |= (1 << bitIndex);
	}

	public void unset(T flag) {
		bitset &= ~flag.getBit();
	}

	public void unset(int bitIndex) {
		bitset &= ~(1 << bitIndex);
	}

	public void toggle(T flag) {
		bitset ^= flag.getBit();
	}

	public void toggle(int bitIndex) {
		bitset ^= (1 << bitIndex);
	}

	public boolean has(T flag) {
		return (bitset & flag.getBit()) != 0;
	}

	public long bitAt(int bitIndex) {
		return bitset & (1 << bitIndex);
	}

	public long asLong() {
		return bitset;
	}

	public static void main(String[] args) {
		final var bs = new BitFlagSet<>(0b1001);
		bs.set(2);
		System.out.println(bs);
		bs.unset(2);
		System.out.println(bs);
	}
}
