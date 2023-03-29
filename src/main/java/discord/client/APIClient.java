package discord.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;

import com.alibaba.fastjson2.JSON;

/**
 * An HttpClient wrapper for making requests to the Discord REST API.
 */
public final class APIClient {

	private static enum HttpMethod {
		GET, POST, PUT, PATCH, DELETE
	};

	private static class DiscordAPIException extends RuntimeException {
		public DiscordAPIException(HttpRequest request, HttpResponse<String> response) {
			super(request.method() + ' ' + request.uri().getPath().replace("/api/v10", "") + " -> " + response.statusCode() + '\n' + response.body());
		}
	}

	private static final BodyHandler<String> BODY_HANDLER = BodyHandlers.ofString();
	private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
	private static final String BASE_URL = "https://discord.com/api/v10";

	private static void log(String message) {
		System.out.println("[APIClient] " + message);
	}

	private String token;

	public void setToken(String token) {
		this.token = token;
	}

	private HttpRequest buildRequest(HttpMethod method, String path, String requestBody) {
		HttpRequest.Builder requestBuilder;

		try {
			requestBuilder = HttpRequest.newBuilder(new URI(BASE_URL + path));
		} catch (URISyntaxException e) {
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

		return requestBuilder.build();
	}

	private static HttpResponse<String> sendRequest(HttpRequest request) {
		HttpResponse<String> response;

		try {
			response = HTTP_CLIENT.send(request, BODY_HANDLER);
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}

		final var statusCode = response.statusCode();
		final var responseBody = response.body();

		if (statusCode == 429)
			return retryAfter(request, responseBody);
		else if (statusCode >= 400)
			throw new DiscordAPIException(request, response);

		log(request.method() + ' ' + request.uri().getPath().replace("/api/v10", "") + " -> " + statusCode);

		return response;
	}

	private static HttpResponse<String> retryAfter(HttpRequest request, String responseBody) {
		final var retryAfter = (int) (1000 * JSON.parseObject(responseBody).getDoubleValue("retry_after"));
		log("Being rate limited for " + retryAfter + "ms");

		try {
			Thread.sleep(retryAfter);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		return sendRequest(request);
	}

	private String _sendRequest(HttpMethod method, String path, String body) {
		return sendRequest(buildRequest(method, path, body)).body();
	}

	private String _sendRequest(HttpMethod method, String path) {
		return _sendRequest(method, path, null);
	}

	public String get(String path) {
		return _sendRequest(HttpMethod.GET, path);
	}

	public String post(String path, String body) {
		return _sendRequest(HttpMethod.POST, path, body);
	}

	public String delete(String path) {
		return _sendRequest(HttpMethod.DELETE, path);
	}

}
