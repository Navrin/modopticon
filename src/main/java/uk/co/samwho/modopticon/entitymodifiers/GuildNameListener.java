package uk.co.samwho.modopticon.entitymodifiers;

import javax.inject.Inject;
import javax.inject.Singleton;

import graphql.Scalars;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildReadyEvent;
import uk.co.samwho.modopticon.storage.Guild;
import uk.co.samwho.modopticon.storage.Guilds;
import uk.co.samwho.modopticon.storage.Storage;

@Singleton
public final class GuildNameListener extends EntityModifier {
  private static final String KEY = "name";

  private final Storage storage;

  @Inject
  GuildNameListener(Storage storage) {
    this.storage = storage;

    extend(Guild.class, KEY, Scalars.GraphQLString);
  }

  @Override
  public void onGuildReady(GuildReadyEvent event) {
    processGuild(event.getGuild());
  }

  @Override
  public void onGuildJoin(GuildJoinEvent event) {
    processGuild(event.getGuild());
  }

  private void processGuild(net.dv8tion.jda.core.entities.Guild guild) {
    storage
      .guild(Guilds.id(guild))
      .attributes()
      .put(KEY, guild.getName());
  }
}