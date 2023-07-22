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
	private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
	private static final String BASE_URL = "https://discord.com/api/v10";

	private static void log(String message) {
		System.out.println("[APIClient] " + message);
	}

	public static class JsonResponse {
		public final String text;

		private JsonResponse(String text) {
			this.text = text;
		}

		public SjObject toJsonObject() {
			return Sj.parseObject(text);
		}

		public List<SjObject> toJsonObjectArray() {
			return Sj.parseObjectArray(text);
		}
	}

	private static CompletableFuture<JsonResponse> sendRequest(HttpRequestWithBody requestWrapper) {
		return HTTP_CLIENT.sendAsync(requestWrapper.request, BODY_HANDLER)
			.thenApply(response -> {
				final var statusCode = response.statusCode();
				final var responseBody = response.body();
				log(requestWrapper.request.method() + ' ' + requestWrapper.path + " -> " + statusCode);
				if (statusCode == 429)
					return retryAfter(requestWrapper, responseBody).join();
				else if (statusCode >= 400)
					throw new DiscordAPIException(requestWrapper, response);
				return new JsonResponse(response.body());
			}).exceptionally(Util::printStackTrace);
	}

	private static CompletableFuture<JsonResponse> retryAfter(HttpRequestWithBody requestWrapper, String responseBody) {
		final var retryAfter = (int) (1000 * Sj.parseObject(responseBody).getDouble("retry_after"));
		log("Being rate limited for " + retryAfter + "ms");
		try { Thread.sleep(retryAfter); }
		catch (InterruptedException e) { e.printStackTrace(); }
		return sendRequest(requestWrapper);
	}

	private final String token;

	APIClient(String token, boolean bot) {
		Objects.requireNonNull(token);
		this.token = bot ? ("Bot " + token) : token;
	}

	// public void setToken(String token, boolean bot) {
	// 	Objects.requireNonNull(token);
	// 	this.token = bot ? ("Bot " + token) : token;
	// }

	private HttpRequestWithBody buildRequest(HttpMethod method, String path, String body) {
		final var requestBuilder = HttpRequest.newBuilder(URI.create(BASE_URL + path));
		BodyPublisher bp = null;
		if (body != null) {
			requestBuilder.header("Content-Type", "application/json");
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

	private CompletableFuture<JsonResponse> buildAndSend(HttpMethod method, String path, String body) {
		return sendRequest(buildRequest(method, path, body));
	}

	public CompletableFuture<JsonResponse> get(String path) throws DiscordAPIException {
		return buildAndSend(HttpMethod.GET, path, null);
	}

	public CompletableFuture<JsonResponse> post(String path, String body) throws DiscordAPIException {
		return buildAndSend(HttpMethod.POST, path, body);
	}

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
