package discord.client;

import java.net.HttpRetryException;
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

	private static final BodyHandler<String> body_handler = BodyHandlers.ofString();
	private static final HttpClient http_client = HttpClient.newHttpClient();
	private static final String base_url = "https://discord.com/api/v10";

	private static void check_error(HttpResponse<String> resp) throws Exception {
		final var status_code = resp.statusCode();

		if (status_code >= 400) {
			System.err.printf("[APIClient] Received an error!\n  Status code: %d\n  Body: %s\n", status_code, resp.body());
			// if we get a 429, try again after `retry_after` seconds
			throw new HttpRetryException(resp.uri().getPath(), status_code);
		}
	}

	private boolean bot = false;
	private String token;

	public void setToken(String token) {
		this.token = token;
	}

	public void setBot(boolean bot) {
		this.bot = bot;
	}

	private String send_request(String path, HttpMethod method, String request_body) throws Exception {
		System.out.printf("[APIClient] Request: %s %s\n", method, path);

		final var url = base_url + path;
		final var request_builder = HttpRequest.newBuilder(new URI(url));
		BodyPublisher bp = null;

		if (request_body != null) {
			bp = BodyPublishers.ofString(request_body);
			System.out.printf("  Body: %s\n", request_body);
		}

		switch (method) {
			case GET: request_builder.GET(); break;
			case POST: request_builder.POST(bp); break;
			case PUT: request_builder.PUT(bp); break;
			case PATCH: request_builder.method("PATCH", bp);
			case DELETE: request_builder.DELETE();
		}

		request_builder.header("Content-Type", "application/json");
		if (token != null) {
			String authorization = token;
			if (bot) authorization = String.format("Bot %s", token);
			request_builder.header("authorization", authorization);
		}

		final var response = http_client.send(request_builder.build(), body_handler);
		final var status_code = response.statusCode();
		final var response_body = response.body();

		// retry on 429
		if (status_code == 429) {
			final var retry_after = (int)(1000 * JSON.parseObject(response_body).getDouble("retry_after"));
			System.err.printf("[APIClient] Being rate limited for %dms\n", retry_after);
			System.err.println(response_body);
			Thread.sleep(retry_after);
			return send_request(path, method, request_body);
		}
		else check_error(response);

		System.out.printf("[APIClient] Response: %d for %s %s\n", status_code, method, path);
		return response_body;
	}

	private String send_request(String path, HttpMethod method) throws Exception {
		return send_request(path, method, null);
	}

	public String get(String path) throws Exception {
		return send_request(path, HttpMethod.GET);
	}

	public String post(String path, String body) throws Exception {
		return send_request(path, HttpMethod.POST, body);
	}

	public String delete(String path) throws Exception {
		return send_request(path, HttpMethod.DELETE);
	}

}