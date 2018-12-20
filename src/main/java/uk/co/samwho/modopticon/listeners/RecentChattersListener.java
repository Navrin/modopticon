package uk.co.samwho.modopticon.listeners;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.common.flogger.FluentLogger;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.samwho.modopticon.storage.Channels;
import uk.co.samwho.modopticon.storage.Guilds;
import uk.co.samwho.modopticon.storage.Storage;
import uk.co.samwho.modopticon.storage.Users;

@Singleton
public final class RecentChattersListener extends ListenerAdapter {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private static final String KEY = "recentChatters";
  private static final int NUM_CHATTERS = 5;

  private final Storage storage;
  private final Map<Long, SortedSet<MemberWithTime>> channels;

  @Inject
  public RecentChattersListener(Storage storage) {
    this.storage = storage;
    this.channels = Collections.synchronizedMap(new HashMap<>());
  }

  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    SortedSet<MemberWithTime> set = updateChatters(event);

    storage
      .guild(Guilds.id(event))
      .channel(Channels.id(event))
      .attributes()
      .put(KEY, set.stream().map(i -> Users.id(i.member())).collect(Collectors.toList()));
  }

  private final SortedSet<MemberWithTime> updateChatters(GuildMessageReceivedEvent event) {
    SortedSet<MemberWithTime> set =
      channels.computeIfAbsent(
        Channels.id(event),
        k -> Collections.synchronizedSortedSet(new TreeSet<>(Comparator.comparing(MemberWithTime::time))));

    OffsetDateTime ct = event.getMessage().getCreationTime();
    MemberWithTime mwt = new MemberWithTime(event.getMember(), ct);

    if (set.isEmpty()) {
      set.add(mwt);
      return set;
    }

    Optional<MemberWithTime> existing =
      set.stream()
        .filter(i -> Users.id(i.member()) == Users.id(event))
        .findFirst();

    if (existing.isPresent()) {
      if (existing.get().time().isBefore(ct)) {
        set.remove(existing.get());
        set.add(mwt);
      }
      return set;
    }

    if (set.size() < NUM_CHATTERS) {
      set.add(mwt);
      return set;
    }

    if (set.first().time().isAfter(ct)) {
      return set;
    }

    set.remove(set.first());
    set.add(mwt);

    return set;
  }

  private static final class MemberWithTime {
    private final Member member;
    private final OffsetDateTime time;

    MemberWithTime(Member member, OffsetDateTime time) {
      this.member = member;
      this.time = time;
    }

    Member member() {
      return this.member;
    }

    OffsetDateTime time() {
      return this.time;
    }
  }
}