package discord.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
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
		DiscordAPIException(HttpRequestWithBody requestWrapper, HttpResponse<String> response) {
			super(requestWrapper.request.method() + ' ' + requestWrapper.path + " -> " + response.statusCode()
			    + "\nResponse body: " + response.body()
				+ "\nRequest body: " + requestWrapper.body);
		}
	}

	private static record HttpRequestWithBody(HttpRequest request, String path, String body) {}
	private static enum HttpMethod { GET, POST, PUT, PATCH, DELETE };

	private static final BodyHandler<String> BODY_HANDLER = BodyHandlers.ofString();

	private static final HttpClient HTTP_CLIENT;
	static {
		final var hcb = HttpClient.newBuilder();
		if (Integer.parseInt(System.getProperty("java.version").split("\\.")[0]) >= 19)
			hcb.executor(Executors.newVirtualThreadPerTaskExecutor());
		HTTP_CLIENT = hcb.build();
	}

	private static final String BASE_URL = "https://discord.com/api/v10";

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

	public static record MultipartFormdataPart(String name, byte[] body, String filename) {}

	private final String token;
	private final boolean debug;

	APIClient(String token, boolean bot, boolean debug) {
		Objects.requireNonNull(token);
		if (token.isEmpty() || token.isBlank())
			throw new IllegalArgumentException("Token cannot be empty or blank");
		this.debug = debug;
		this.token = bot ? ("Bot " + token) : token;
	}

	private HttpRequestWithBody buildRequest(HttpMethod method, String path, String body, String contentType) {
		final var requestBuilder = HttpRequest.newBuilder(URI.create(BASE_URL + path));

		BodyPublisher bp = null;
		if (body != null) {
			requestBuilder.header("Content-Type", contentType);
			bp = BodyPublishers.ofString(body);
		}

		switch (method) {
			case GET -> requestBuilder.GET();
			case POST -> requestBuilder.POST(bp);
			case PATCH -> requestBuilder.method("PATCH", bp);
			case PUT -> requestBuilder.PUT(bp);
			case DELETE -> requestBuilder.DELETE();
		}

		requestBuilder.header("Authorization", token);
		return new HttpRequestWithBody(requestBuilder.build(), path, body);
	}

	private HttpRequestWithBody buildRequest(HttpMethod method, String path, String body) {
		return buildRequest(method, path, body, "application/json");
	}

	private CompletableFuture<JsonResponse> sendRequest(HttpRequestWithBody requestWrapper) {
		return HTTP_CLIENT.sendAsync(requestWrapper.request, BODY_HANDLER)
			.thenApply(response -> {
				final var statusCode = response.statusCode();
				final var responseBody = response.body();
				if (debug)
					Logger.log(requestWrapper.request.method() + ' ' + requestWrapper.path + " -> " + statusCode);
				if (statusCode == 429)
					return retryAfter(requestWrapper, responseBody).join();
				else if (statusCode >= 400)
					throw new DiscordAPIException(requestWrapper, response);
				return new JsonResponse(response.body());
			}).exceptionally(Util::printStackTrace);
	}

	private CompletableFuture<JsonResponse> retryAfter(HttpRequestWithBody requestWrapper, String responseBody) {
		final var retryAfter = (int) (1000 * Sj.parseObject(responseBody).getDouble("retry_after"));
		if (debug)
			Logger.log("Being rate limited for " + retryAfter + "ms");
		return CompletableFuture.completedFuture(requestWrapper)
			.thenComposeAsync(
				this::sendRequest,
		    	CompletableFuture.delayedExecutor(retryAfter, TimeUnit.MILLISECONDS)
			);
	}

	private CompletableFuture<JsonResponse> buildAndSend(HttpMethod method, String path, String body) {
		return sendRequest(buildRequest(method, path, body));
	}

	public CompletableFuture<JsonResponse> get(String path) throws DiscordAPIException {
		return buildAndSend(HttpMethod.GET, path, null);
	}

	public CompletableFuture<JsonResponse> post(String path, String body) throws DiscordAPIException {
		return buildAndSend(HttpMethod.POST, path, body);
	}

	// public CompletableFuture<JsonResponse> post(String path, List<MultipartFormdataPart> parts) throws DiscordAPIException {
	// 	final var sb = new StringBuilder("");
	// 	// TODO: Finish this: https://discord.com/developers/docs/reference#uploading-files
	// }

	public CompletableFuture<JsonResponse> put(String path, String body) throws DiscordAPIException {
		return buildAndSend(HttpMethod.PUT, path, body);
	}

	public CompletableFuture<JsonResponse> patch(String path, String body) throws DiscordAPIException {
		return buildAndSend(HttpMethod.PATCH, path, body);
	}

	public CompletableFuture<JsonResponse> delete(String path) throws DiscordAPIException {
		return buildAndSend(HttpMethod.DELETE, path, null);
	}
}
