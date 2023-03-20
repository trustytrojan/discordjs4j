package discord.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;

import discord.util.JSON;

/**
 * An HttpClient wrapper for making requests to the Discord REST API.
 */
public final class APIClient {

	private static enum HttpMethod { GET, POST, PUT, PATCH, DELETE };

	private static final BodyHandler<String> bodyHandler = BodyHandlers.ofString();
	private static final HttpClient httpClient = HttpClient.newHttpClient();
	private static final String baseURL = "https://discord.com/api/v10";

	private static void checkError(HttpResponse<String> resp) {
		final var statusCode = resp.statusCode();
		if (statusCode >= 400) {
			final var path = resp.uri().getPath();
			final var method = resp.request().method();
			final var body = resp.body();
			throw new DiscordAPIError(method + ' ' + path + " -> " + statusCode + '\n' + body);
		}
	}

	private static void log(String message) {
		System.out.println("[APIClient] " + message);
	}

	private String token;

	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * Sends an HTTP request to the Discord API using {@code method} at endpoint
	 * {@code path} with body {@code requestBody} if not {@code null}.
	 * @param endpoint Discord API endpoint
	 * @param method HTTP method enum
	 * @param requestBody
	 * @return
	 */
	private String sendRequest(HttpMethod method, String endpoint, String requestBody) {
		try {
			var logBeforeResp = "Request: %s %s".formatted(method, endpoint);

			final var url = baseURL + endpoint;
			final var requestBuilder = HttpRequest.newBuilder(new URI(url));
			BodyPublisher bp = null;

			if (requestBody != null) {
				bp = BodyPublishers.ofString(requestBody);
				logBeforeResp += ("  Body: " + requestBody);
			}

			log(logBeforeResp);

			switch (method) {
				case GET: requestBuilder.GET(); break;
				case POST: requestBuilder.POST(bp); break;
				case PUT: requestBuilder.PUT(bp); break;
				case PATCH: requestBuilder.method("PATCH", bp); break;
				case DELETE: requestBuilder.DELETE();
			}

			requestBuilder.header("Content-Type", "application/json");
			if (token != null)
				requestBuilder.header("authorization", token);

			final var response = httpClient.send(requestBuilder.build(), bodyHandler);
			final var statusCode = response.statusCode();
			final var responseBody = response.body();

			// retry on 429
			if (statusCode == 429) {
				final var retryAfter = (int)(1000 * JSON.parseObject(responseBody).getDouble("retry_after"));
				log("Being rate limited for " + retryAfter + "ms");
				Thread.sleep(retryAfter);
				return sendRequest(method, endpoint, requestBody);
			}
			else checkError(response);

			log("Response: %s %s -> %s".formatted(method, endpoint, statusCode));
			return responseBody;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Alias for sendRequest for HTTP methods that do
	 * not require a body.
	 * @param endpoint Discord API endpoint
	 * @param method HTTP method enum
	 * @return Response body
	 */
	private String sendRequest(HttpMethod method, String endpoint) {
		return sendRequest(method, endpoint, null);
	}

	public String get(String endpoint) {
		return sendRequest(HttpMethod.GET, endpoint);
	}

	public String post(String endpoint, String body) {
		return sendRequest(HttpMethod.POST, endpoint, body);
	}

	public String delete(String endpoint) {
		return sendRequest(HttpMethod.DELETE, endpoint);
	}

}
