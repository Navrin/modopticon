package uk.co.samwho.modopticon.listeners;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import com.google.common.flogger.FluentLogger;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audit.AuditLogEntry;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.samwho.modopticon.storage.Guilds;
import uk.co.samwho.modopticon.storage.Members;
import uk.co.samwho.modopticon.storage.Storage;

@Singleton
public final class AuditLogListener extends ListenerAdapter {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private static final String BAN = "lastBan";
  private static final String KICK = "lastKick";

  private final Storage storage;
  private final Clock clock;

  @Inject
  public AuditLogListener(Storage storage, Clock clock) {
    this.storage = storage;
    this.clock = clock;
  }

  @Override
  public void onGuildReady(GuildReadyEvent event) {
    updateFromAuditLog(event.getGuild());
  }

  @Override
  public void onGuildJoin(GuildJoinEvent event) {
    updateFromAuditLog(event.getGuild());
  }

  @Override
  public void onGuildBan(GuildBanEvent event) {
    OffsetDateTime now = OffsetDateTime.now(clock);
    storage
      .guild(Guilds.id(event))
      .member(Members.id(event))
      .attributes()
      .compute(BAN, (k, v) -> (v == null || ((OffsetDateTime)v).isBefore(now)) ? now : v);

    // Doesn't seem to be a way to get real-time kicks, so I'm just going
    // to call this method again in here.
    updateFromAuditLog(event.getGuild());
  }

  private void updateFromAuditLog(Guild guild) {
    if (!guild.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS)) {
      logger
        .atWarning()
        .atMostEvery(10, TimeUnit.MINUTES)
        .log("need VIEW_AUDIT_LOGS permission for this listener to work");
      return;
    }

    guild.getAuditLogs().cache(false).forEach(this::processLog);
  }

  private void processLog(AuditLogEntry log) {
    String key;
    switch(log.getType()) {
      case BAN:
        key = BAN;
        break;
      case KICK:
        key = KICK;
        break;
      default:
        return;
    }

    OffsetDateTime lct = log.getCreationTime();
    storage
      .guild(Guilds.id(log.getGuild()))
      .member(log.getTargetIdLong())
      .attributes()
      .compute(key, (k, v) -> (v == null || ((OffsetDateTime)v).isBefore(lct)) ? lct : v);
  }
}