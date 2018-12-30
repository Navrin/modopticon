package uk.co.samwho.modopticon.storage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;


/**
 * This object is intended to be shared globally and modified by ListenerAdapters.
 * Its purpose is to represents Discord entities (guilds, users, members, etc.) and
 * attach metadata to them to be shown in some UI.
 */
@ThreadSafe
@Singleton
public final class Storage {
  private static final Splitter SPLITTER = Splitter.on('/');

  @Expose private final Map<Long, Guild> guilds = new ConcurrentHashMap<>();
  @Expose private final Map<Long, User> users = new ConcurrentHashMap<>();

  @Inject
  Storage() {}

  public User user(long id) {
    return users.computeIfAbsent(id, k -> new User(id));
  }

  public boolean userExists(long id) {
    return users.containsKey(id);
  }

  public Collection<User> users() {
    return users.values();
  }

  public Guild guild(long id) {
    return guilds.computeIfAbsent(id, k -> new Guild(id));
  }

  public boolean guildExists(long id) {
    return guilds.containsKey(id);
  }

  public Collection<Guild> guilds() {
    return guilds.values();
  }

  public Optional<Entity> fromResourceIdentifier(String resourceIdentifier) throws InvalidResourceIdentifierException {
    if (Strings.isNullOrEmpty(resourceIdentifier)) {
      return Optional.empty();
    }

    List<String> parts = SPLITTER.splitToList(resourceIdentifier);
    parts = parts.subList(1, parts.size());

    if (parts.size() <= 1) {
      return Optional.empty();
    }

    if (parts.size() % 2 != 0) {
      throw new InvalidResourceIdentifierException(resourceIdentifier);
    }

    switch(parts.get(0)) {
      case "users":
        if (parts.size() > 2) {
          break;
        }

        long uid = Long.valueOf(parts.get(1));

        if (!userExists(uid)) {
          return Optional.empty();
        }

        return Optional.of(user(uid));
      case "guilds":
        long gid = Long.valueOf(parts.get(1));

        if (!guildExists(gid)) {
          return Optional.empty();
        }

        Guild guild = guild(gid);

        if (parts.size() == 2) {
          return Optional.of(guild);
        }

        switch(parts.get(2)) {
          case "channels":
            if (parts.size() != 4) {
              break;
            }

            long cid = Long.valueOf(parts.get(3));
            if (!guild.channelExists(cid)) {
              return Optional.empty();
            }
            return Optional.of(guild.channel(cid));
          case "members":
            if (parts.size() != 4) {
              break;
            }

            long mid = Long.valueOf(parts.get(3));
            if (!guild.memberExists(mid)) {
              return Optional.empty();
            }
            return Optional.of(guild.member(mid));
        }
    }

    throw new InvalidResourceIdentifierException(resourceIdentifier);
  }
}