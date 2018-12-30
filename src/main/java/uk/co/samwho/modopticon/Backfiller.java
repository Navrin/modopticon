package uk.co.samwho.modopticon;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.flogger.FluentLogger;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.samwho.modopticon.storage.Users;

@Singleton
public final class Backfiller implements Runnable {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final JDA jda;
  private final Set<? extends ListenerAdapter> listeners;

  @Inject
  public Backfiller(JDA jda, Set<? extends ListenerAdapter> listeners) {
    this.jda = jda;
    this.listeners = listeners;
  }

  @Override
  public void run() {
    logger.atInfo().log("starting backfill");
    jda.getGuilds().stream().parallel().forEach(guild -> {
      logger.atInfo().log("backfilling channel data for guild %s", guild.getName());

      guild.getTextChannels().stream().parallel().forEach(channel -> {
        if (!guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_HISTORY)) {
          return;
        }

        channel.getIterableHistory().limit(100).cache(false).stream().limit(100).forEach(message -> {
          if (Users.isDeleted(message)) {
            return;
          }

          GuildMessageReceivedEvent event = new GuildMessageReceivedEvent(jda, -1, message);
          listeners.forEach(l -> l.onGuildMessageReceived(event));
        });
      });
    });
    logger.atInfo().log("backfill finished");
  }
}