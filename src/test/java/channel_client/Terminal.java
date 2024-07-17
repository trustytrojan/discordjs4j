package channel_client;

public final class Terminal {
	private static final char ESCAPE = 27;

	static String cursorUp(final int lines) {
		return ESCAPE + "[" + lines + 'A';
	}

	static final String
		CLEAR_LINE = ESCAPE + "[2K",
		FG_GRAY = ESCAPE + "[30m",
		FG_DEFAULT = ESCAPE + "[39m",
		FG_RESET = ESCAPE + "[0m";
	
	private Terminal() {}
}
