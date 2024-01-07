package discord.util;

import discord.util.BitFlagSet.BitFlag;

public class MutableBitFlagSet<T extends BitFlag> extends BitFlagSet<T> {
	public MutableBitFlagSet() {
		super(0);
	}

	public MutableBitFlagSet(long bitset) {
		super(bitset);
	}

	public void setBitset(long bitset) {
		this.bitset = bitset;
	}

	public void set(T flag) {
		set(flag.getBitIndex());
	}

	public void set(long bitIndex) {
		bitset |= (1 << bitIndex);
	}

	public void unset(T flag) {
		unset(flag.getBitIndex());
	}

	public void unset(long bitIndex) {
		bitset &= ~(1 << bitIndex);
	}

	public void toggle(T flag) {
		toggle(flag.getBitIndex());
	}

	public void toggle(long bitIndex) {
		bitset ^= (1 << bitIndex);
	}
}
