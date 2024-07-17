package discord.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import discord.util.Logger;
import discord.util.Util;
import sj.Sj;
import sj.SjObject;

/**
 * An HttpClient wrapper for making requests to the Discord API.
 */
public final class APIClient {
	private static class DiscordAPIException extends RuntimeException {
		DiscordAPIException(Request req, HttpResponse<String> res) {
			super(req.request.method() + ' ' + req.path + " -> " + res.statusCode()
			    + "\nResponse body: " + res.body()
				+ ((req.body != null) ? ("\nRequest body: " + req.body) : ""));
		}
	}

	private static record Request(HttpRequest request, String path, String body) {}
	private static final String MULTIPART_FORMDATA_BOUNDARY = "discordjs4j";
	private static final BodyHandler<String> BODY_HANDLER = BodyHandlers.ofString();
	private static final HttpClient HTTP_CLIENT;
	private static final String BASE_URL = "https://discord.com/api/v10";

	static {
		final var hcb = HttpClient.newBuilder();
		if (Integer.parseInt(System.getProperty("java.version").split("\\.")[0]) >= 19)
			hcb.executor(Executors.newVirtualThreadPerTaskExecutor());
		HTTP_CLIENT = hcb.build();
	}

	public static class JsonResponse {
		public final String rawText;

		private JsonResponse(String text) {
			this.rawText = text;
		}

		public SjObject asObject() {
			return Sj.parseObject(rawText);
		}

		public List<SjObject> asObjectArray() {
			return Sj.parseObjectArray(rawText);
		}
	}

	public static class MultipartFormdataPart {
		public final String name;
		public String filename, contentType;
		public final byte[] body;

		public MultipartFormdataPart(final String name, final byte[] body) {
			this.name = Objects.requireNonNull(name);
			this.body = Objects.requireNonNull(body);
		}

		public byte[] toByteArray() throws IOException {
			final var baos = new ByteArrayOutputStream();
			baos.write("Content-Disposition: form-data; name=\"%s\"%s%s\n\n".formatted(
				name,
				(filename != null) ? ("; filename=\"" + filename + '"') : "",
				(contentType != null) ? ("\nContent-Type: " + contentType) : ""
			).getBytes());
			baos.write(body);
			return baos.toByteArray();
		}
	}

	private final String token;
	private final boolean debug;

	APIClient(String token, boolean bot, boolean debug) {
		if (token.isEmpty() || token.isBlank())
			throw new IllegalArgumentException("Token cannot be empty or blank");
		this.debug = debug;
		this.token = bot ? ("Bot " + token) : token;
	}

	private HttpRequest.Builder beginRequest(String path) {
		return HttpRequest.newBuilder(URI.create(BASE_URL + path))
			.header("Authorization", token);
	}

	private CompletableFuture<JsonResponse> sendRequest(Request req) {
		return HTTP_CLIENT.sendAsync(req.request, BODY_HANDLER)
			.thenApply(res -> {
				if (debug)
					Logger.log(req.request.method() + ' ' + req.path + " -> " + res.statusCode());

				if (res.statusCode() == 429)
					return retryAfter(req, res.body()).join();
				else if (res.statusCode() >= 400)
					throw new DiscordAPIException(req, res);

				return new JsonResponse(res.body());
			}).exceptionally(Util::printStackTrace);
	}

	private CompletableFuture<JsonResponse> sendRequest(HttpRequest request, String path, String body) {
		return sendRequest(new Request(request, path, body));
	}

	private CompletableFuture<JsonResponse> retryAfter(Request req, String resBody) {
		final var retryAfterSec = Sj.parseObject(resBody).getInteger("retry_after");
		if (debug)
			Logger.log("Being rate limited for " + retryAfterSec + " seconds");
		return CompletableFuture.completedFuture(req)
			.thenComposeAsync(
				this::sendRequest,
				CompletableFuture.delayedExecutor(retryAfterSec, TimeUnit.SECONDS)
			);
	}

	public CompletableFuture<JsonResponse> get(String path) throws DiscordAPIException {
		return sendRequest(beginRequest(path).build(), path, null);
	}

	public CompletableFuture<JsonResponse> post(String path, String body) throws DiscordAPIException {
		return sendRequest(
			beginRequest(path)
				.POST(BodyPublishers.ofString(body))
				.header("Content-Type", "application/json")
				.build(),
			path, body
		);
	}

	// https://discord.com/developers/docs/reference#uploading-files
	public CompletableFuture<JsonResponse> post(String path, List<MultipartFormdataPart> parts) throws DiscordAPIException, IOException {
		final var baos = new ByteArrayOutputStream();
		baos.write(("--" + MULTIPART_FORMDATA_BOUNDARY).getBytes());
		for (final var p : parts) {
			baos.write(p.toByteArray());
			baos.write(("\n--" + MULTIPART_FORMDATA_BOUNDARY + '\n').getBytes());
		}
		baos.write("--".getBytes());
		return sendRequest(
			beginRequest(path)
				.POST(BodyPublishers.ofByteArray(baos.toByteArray()))
				.header("Content-Type", "multipart/form-data; boundary=\"" + MULTIPART_FORMDATA_BOUNDARY + '"')
				.build(),
			path, "<contains binary data>"
		);
	}

	public CompletableFuture<JsonResponse> put(String path, String body) throws DiscordAPIException {
		return sendRequest(
			beginRequest(path)
				.PUT(BodyPublishers.ofString(body))
				.header("Content-Type", "application/json")
				.build(),
			path, body
		);
	}

	public CompletableFuture<JsonResponse> patch(String path, String body) throws DiscordAPIException {
		return sendRequest(
			beginRequest(path)
				.method("PATCH", BodyPublishers.ofString(body))
				.header("Content-Type", "application/json")
				.build(),
			path, body
		);
	}

	public CompletableFuture<JsonResponse> delete(String path) throws DiscordAPIException {
		return sendRequest(beginRequest(path).DELETE().build(), path, null);
	}
}
