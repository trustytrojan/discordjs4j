package channel_client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

final class FzfProcess {
	final Process process;
	final BufferedWriter stdin;
	final CompletableFuture<String> result;

	FzfProcess(String header) throws IOException {
		process = new ProcessBuilder("fzf", "--header", header).start();
		stdin = process.outputWriter();
		result = CompletableFuture.supplyAsync(() -> {
			try { return process.inputReader().readLine(); }
			catch (final IOException e) { e.printStackTrace(); System.exit(1); return null; }
		});
	}

	void addChoice(String choice) throws IOException {
		stdin.write(choice + '\n');
		stdin.flush();
	}
}
