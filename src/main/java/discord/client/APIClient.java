package discord.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import sj.Sj;
import sj.SjObject;

/**
 * An HttpClient wrapper for making requests to the Discord API.
 */
public final class APIClient {
	private static class DiscordAPIException extends RuntimeException {
		DiscordAPIException(String message) {
			super(message);
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

	public static class JsonHttpResponse {
		public final String body;

		private JsonHttpResponse(String body) {
			this.body = body;
		}

		public SjObject toJsonObject() {
			return Sj.parseObject(body);
		}

		public List<SjObject> toJsonObjectArray() {
			return Sj.parseObjectArray(body);
		}
	}

	private static CompletableFuture<JsonHttpResponse> sendRequest(HttpRequestWithBody requestWrapper) {
		final var request = requestWrapper.request;

		return HTTP_CLIENT.sendAsync(request, BODY_HANDLER)
			.thenApply(response -> {
				final var statusCode = response.statusCode();
				final var responseBody = response.body();

				if (statusCode == 429)
					return retryAfter(requestWrapper, responseBody).join();
				else if (statusCode >= 400) {
					throw new DiscordAPIException(request.method() + ' ' + requestWrapper.path + " -> " + response.statusCode()
						+ "\nResponse body: " + response.body()
						+ "\nRequest body: " + requestWrapper.body);
				}

				log(request.method() + ' ' + request.uri().getPath().replace("/api/v10", "") + " -> " + statusCode);

				return new JsonHttpResponse(response.body());
			});
	}

	private static CompletableFuture<JsonHttpResponse> retryAfter(HttpRequestWithBody requestWrapper, String responseBody) {
		final var retryAfter = (int) (1000 * Sj.parseObject(responseBody).getDouble("retry_after"));

		log("Being rate limited for " + retryAfter + "ms");

		try { Thread.sleep(retryAfter); }
		catch (InterruptedException e) { e.printStackTrace(); }

		return sendRequest(requestWrapper);
	}

	private String token;

	public void setToken(String token, boolean bot) {
		Objects.requireNonNull(token);
		this.token = bot ? ("Bot " + token) : token;
	}

	private HttpRequestWithBody buildRequest(HttpMethod method, String path, String requestBody) {
		final var requestBuilder = HttpRequest.newBuilder(URI.create(BASE_URL + path));

		BodyPublisher bp = null;

		if (requestBody != null) {
			requestBuilder.header("Content-Type", "application/json");
			bp = BodyPublishers.ofString(requestBody);
		}

		switch (method) {
			case GET -> requestBuilder.GET();
			case POST -> requestBuilder.POST(bp);
			case PUT -> requestBuilder.PUT(bp);
			case PATCH -> requestBuilder.method("PATCH", bp);
			case DELETE -> requestBuilder.DELETE();
		}

		requestBuilder.header("authorization", token);

		return new HttpRequestWithBody(requestBuilder.build(), path, requestBody);
	}

	private CompletableFuture<JsonHttpResponse> buildAndSend(HttpMethod method, String path, String body) {
		return sendRequest(buildRequest(method, path, body));
	}

	public CompletableFuture<JsonHttpResponse> get(String path) {
		return buildAndSend(HttpMethod.GET, path, null);
	}

	public CompletableFuture<JsonHttpResponse> post(String path, String body) {
		return buildAndSend(HttpMethod.POST, path, body);
	}

	public CompletableFuture<JsonHttpResponse> put(String path, String body) {
		return buildAndSend(HttpMethod.PUT, path, body);
	}

	public CompletableFuture<JsonHttpResponse> patch(String path, String body) {
		return buildAndSend(HttpMethod.PATCH, path, body);
	}

	public CompletableFuture<JsonHttpResponse> delete(String path) {
		return buildAndSend(HttpMethod.DELETE, path, null);
	}
}
