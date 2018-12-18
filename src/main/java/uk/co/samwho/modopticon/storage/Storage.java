package uk.co.samwho.modopticon.storage;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import com.google.inject.Singleton;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

@ThreadSafe
@Singleton
public final class Storage {
  private final Map<Long, Guild> guilds = Collections.synchronizedMap(new HashMap<>());
  private final Map<Long, User> users = Collections.synchronizedMap(new HashMap<>());

  User getOrCreateUser(Long id) {
    return users.computeIfAbsent(id, k -> new User(id));
  }

  User getOrCreateUser(net.dv8tion.jda.core.entities.User user) {
    return getOrCreateUser(user.getIdLong());
  }

  public User user(Long id) {
    return getOrCreateUser(id);
  }

  public User user(Message message) {
    return getOrCreateUser(message.getAuthor());
  }

  public User user(MessageReceivedEvent event) {
    return user(event.getMessage());
  }

  public User user(net.dv8tion.jda.core.entities.User user) {
    return user(user.getIdLong());
  }

  Guild getOrCreateGuild(Long id) {
    return guilds.computeIfAbsent(id, k -> new Guild(id));
  }

  Guild getOrCreateGuild(net.dv8tion.jda.core.entities.Guild guild) {
    return getOrCreateGuild(guild.getIdLong());
  }

  public Guild guild(Long id) {
    return getOrCreateGuild(id);
  }

  public Guild guild(Message message) {
    return getOrCreateGuild(message.getGuild());
  }

  public Guild guild(MessageReceivedEvent event) {
    return guild(event.getMessage());
  }

  public Channel channel(MessageReceivedEvent event) {
    return channel(event.getMessage());
  }

  public Channel channel(Message message) {
    return guild(message).channel(message);
  }

  public Collection<Guild> guilds() {
    return guilds.values();
  }
}