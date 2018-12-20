package uk.co.samwho.modopticon.storage;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import com.google.gson.annotations.Expose;

@ThreadSafe
public final class Guild extends Entity {
  private static final String TYPE = "guild";

  private final long id;
  @Expose private final Map<Long, Channel> channels;
  @Expose private final Map<Long, Member> members;

  Guild(long id) {
    super(TYPE, String.format("/guilds/%d", id));

    this.id = id;
    this.channels = new ConcurrentHashMap<>();
    this.members = new ConcurrentHashMap<>();
  }

  public Channel channel(long id) {
    return channels.computeIfAbsent(id, k -> new Channel(id, this.id));
  }

  public boolean channelExists(long id) {
    return channels.containsKey(id);
  }

  public Collection<Channel> channels() {
    return channels.values();
  }

  public Member member(long id) {
    return members.computeIfAbsent(id, k -> new Member(id, this.id));
  }

  public boolean memberExists(long id) {
    return members.containsKey(id);
  }

  public Collection<Member> members() {
    return members.values();
  }
}