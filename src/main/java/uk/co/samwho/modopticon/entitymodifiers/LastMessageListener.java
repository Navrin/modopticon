package uk.co.samwho.modopticon.entitymodifiers;

import java.time.OffsetDateTime;

import javax.inject.Inject;
import javax.inject.Singleton;

import graphql.Scalars;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import uk.co.samwho.modopticon.storage.Channel;
import uk.co.samwho.modopticon.storage.Channels;
import uk.co.samwho.modopticon.storage.Guilds;
import uk.co.samwho.modopticon.storage.Storage;

@Singleton
public final class LastMessageListener extends EntityModifier {
  private static final String KEY = "lastMessageReceivedAt";

  private final Storage storage;

  @Inject
  public LastMessageListener(Storage storage) {
    this.storage = storage;

    extend(Channel.class, KEY, Scalars.GraphQLString);
  }

  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    Channel channel = storage.guild(Guilds.id(event)).channel(Channels.id(event));

    OffsetDateTime curr = event.getMessage().getCreationTime();
    OffsetDateTime prev = (OffsetDateTime)channel.attributes().get(KEY);

    if (prev == null || curr.isAfter(prev)) {
      channel.attributes().put(KEY, curr);
    }
  }
}