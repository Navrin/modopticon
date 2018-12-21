package uk.co.samwho.modopticon.entitymodifiers;

import javax.inject.Inject;
import javax.inject.Singleton;

import graphql.Scalars;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildReadyEvent;
import uk.co.samwho.modopticon.storage.Channel;
import uk.co.samwho.modopticon.storage.Channels;
import uk.co.samwho.modopticon.storage.Guilds;
import uk.co.samwho.modopticon.storage.Storage;

@Singleton
public final class ChannelNameListener extends EntityModifier {
  private static final String KEY = "name";

  private final Storage storage;

  @Inject
  ChannelNameListener(Storage storage) {
    this.storage = storage;

    extend(Channel.class, KEY, Scalars.GraphQLString);
  }

  @Override
  public void onTextChannelUpdateName(TextChannelUpdateNameEvent event) {
    processChannel(event.getChannel());
  }

  @Override
  public void onTextChannelCreate(TextChannelCreateEvent event) {
    processChannel(event.getChannel());
  }

  @Override
  public void onGuildReady(GuildReadyEvent event) {
    event.getGuild().getTextChannels().forEach(this::processChannel);
  }

  @Override
  public void onGuildJoin(GuildJoinEvent event) {
    event.getGuild().getTextChannels().forEach(this::processChannel);
  }

  private void processChannel(net.dv8tion.jda.core.entities.Channel channel) {
    storage
      .guild(Guilds.id(channel.getGuild()))
      .channel(Channels.id(channel))
      .attributes()
      .put(KEY, channel.getName());
  }
}