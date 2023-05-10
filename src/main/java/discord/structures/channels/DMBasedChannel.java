package discord.structures.channels;

import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.structures.AbstractDiscordResource;
import simple_json.SjObject;

public abstract class DMBasedChannel extends AbstractDiscordResource implements TextBasedChannel {
    private final MessageManager messages = new MessageManager(client, this);

    protected DMBasedChannel(DiscordClient client, SjObject data) {
        super(client, data);
    }

    @Override
    public String url() {
        return "https://discord.com/channels/@me/" + id();
    }
}
