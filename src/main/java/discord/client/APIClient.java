package discord.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import simple_json.SimpleJson;
import simple_json.SjObject;;

/**
 * An HttpClient wrapper for making requests to the Discord REST API.
 */
public final class APIClient {
	private static record HttpRequestWithBody(HttpRequest request, String body) {
	}

	private static enum HttpMethod {
		GET, POST, PUT, PATCH, DELETE
	};

	private static final BodyHandler<String> BODY_HANDLER = BodyHandlers.ofString();
	private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
	private static final String BASE_URL = "https://discord.com/api/v10";

	private static void log(String message) {
		System.out.println("[APIClient] " + message);
	}

	private boolean bot;
	private String token;

	public void setBot(boolean bot) {
		this.bot = bot;
	}

	public void setToken(String token) {
		this.token = (bot)
				? "Bot " + token
				: token;
	}

	private HttpRequestWithBody buildRequest(HttpMethod method, String path, String requestBody) {
		HttpRequest.Builder requestBuilder;

		try {
			requestBuilder = HttpRequest.newBuilder(new URI(BASE_URL + path));
		} catch (final URISyntaxException e) {
			throw new RuntimeException(e);
		}

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

		if (token != null)
			requestBuilder.header("authorization", token);

		return new HttpRequestWithBody(requestBuilder.build(), requestBody);
	}

	private static HttpResponse<String> sendRequest(HttpRequestWithBody requestWrapper) {
		final var request = requestWrapper.request;
		HttpResponse<String> response;

		try {
			response = HTTP_CLIENT.send(request, BODY_HANDLER);
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}

		final var statusCode = response.statusCode();
		final var responseBody = response.body();

		if (statusCode == 429)
			return retryAfter(requestWrapper, responseBody);
		else if (statusCode >= 400) {
			log(request.method() + ' ' + request.uri().getPath().replace("/api/v10", "") + " -> "
					+ response.statusCode()
					+ "\nResponse body: " + response.body()
					+ "\nRequest body: " + requestWrapper.body);
			throw new RuntimeException();
		}

		log(request.method() + ' ' + request.uri().getPath().replace("/api/v10", "") + " -> " + statusCode);

		return response;
	}

	private static HttpResponse<String> retryAfter(HttpRequestWithBody requestWrapper, String responseBody) {
		final var retryAfter = (int) (1000 * SimpleJson.parseObject(responseBody).getDouble("retry_after"));
		log("Being rate limited for " + retryAfter + "ms");

		try {
			Thread.sleep(retryAfter);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		return sendRequest(requestWrapper);
	}

	public static class JsonHttpResponse {
		public final String body;

		private JsonHttpResponse(String body) {
			this.body = body;
		}

		public SjObject toJsonObject() {
			return SimpleJson.parseObject(body);
		}

		public List<SjObject> toJsonObjectArray() {
			return SimpleJson.parseObjectArray(body);
		}
	}

	private CompletableFuture<JsonHttpResponse> _sendRequest(HttpMethod method, String path, String body) {
		final var request = buildRequest(method, path, body);
		return CompletableFuture.supplyAsync(() -> {
			final var response = sendRequest(request);
			return new JsonHttpResponse(response.body());
		});
	}

	public CompletableFuture<JsonHttpResponse> get(String path) {
		return _sendRequest(HttpMethod.GET, path, null);
	}

	public CompletableFuture<JsonHttpResponse> post(String path, String body) {
		return _sendRequest(HttpMethod.POST, path, body);
	}

	public CompletableFuture<JsonHttpResponse> put(String path, String body) {
		return _sendRequest(HttpMethod.PUT, path, body);
	}

	public CompletableFuture<JsonHttpResponse> patch(String path, String body) {
		return _sendRequest(HttpMethod.PATCH, path, body);
	}

	public CompletableFuture<JsonHttpResponse> delete(String path) {
		return _sendRequest(HttpMethod.DELETE, path, null);
	}
}
