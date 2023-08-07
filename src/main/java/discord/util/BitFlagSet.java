package discord.util;

import discord.util.BitFlagSet.BitFlag;

/**
 * A simple recreation of {@code java.util.BitSet} that
 * supports the use of enums that implement {@code BitFlag}.
 */
public class BitFlagSet<T extends BitFlag> {
	/**
	 * An interface requiring enums to return a {@code long}
	 * that has only one bit as a {@code 1}, for use with
	 * {@code BitFlagSet}.
	 */
	public static interface BitFlag {
		int getBitIndex();
	}

	protected long bitset;

	public BitFlagSet(long bitset) {
		this.bitset = bitset;
	}

	@Override
	public String toString() {
		return Long.toBinaryString(bitset);
	}

	public boolean has(T flag) {
		return is1(flag.getBitIndex());
	}

	public boolean is1(int bitIndex) {
		return (bitset & (1 << bitIndex)) != 0;
	}

	public long getBitset() {
		return bitset;
	}
}
