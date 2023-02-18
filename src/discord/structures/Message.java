package discord.structures;

import java.util.concurrent.CompletableFuture;

import discord.util.BetterJSONObject;
import discord.client.DiscordClient;
import discord.structures.channels.TextBasedChannel;

public class Message implements DiscordObject {

	private final DiscordClient client;
	private BetterJSONObject data;
	private final CompletableFuture<Void> _author;
	private User author;
	private final CompletableFuture<Void> _channel;
	private TextBasedChannel channel;

	public Message(DiscordClient client, BetterJSONObject data) {
		this.client = client;
		this.data = data;
		_author = client.users.fetch(author_id()).thenAccept((user) -> author = user);
		_channel = client.channels.fetch(channel_id()).thenAccept((__) -> {
			final var channel = (this.channel = (TextBasedChannel)__);
			channel.messages().cache(this);
		});
	}

	public String channel_id() {
		return data.getString("channel_id");
	}

	public TextBasedChannel channel() {
		if (channel == null) try { _channel.get(); } catch (Exception e) { throw new RuntimeException(e); }
		return channel;
	}

	public String author_id() {
		return data.getObject("author").getString("id");
	}

	public User author() {
		if (author == null) try { _author.get(); } catch (Exception e) { throw new RuntimeException(e); }
		return author;
	}

	public String content() {
		return data.getString("content");
	}

	public Boolean tts() {
		return data.getBoolean("tts");
	}

	// //public MessageComponent[] components;
	// //public Attachment[] attachments;
	// public long flags;
	// public long type;
	// //public Role[] mention_roles;
	// public Date edited_timestamp;
	// public Message referenced_message;
	// //public MessageMention[] mentions;
	// //public Embed[] embeds;

	public Boolean mention_everyone() {
		return data.getBoolean("mention_everyone");
	}

	public Boolean pinned() {
		return data.getBoolean("pinned");
	}

	@Override
	public BetterJSONObject getData() {
		return data;
	}

	@Override
	public void setData(BetterJSONObject data) {
		this.data = data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}

	@Override
	public String api_path() {
		return String.format("/channels/%s/messages/%s", channel_id(), id());
	}

}
