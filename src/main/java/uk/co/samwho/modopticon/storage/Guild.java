package uk.co.samwho.modopticon.storage;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

@ThreadSafe
public final class Guild {
  private final Long id;
  private final Map<Long, Channel> channels;
  private final Map<Long, Member> members;

  Guild(Long id) {
    this.id = id;
    this.channels = Collections.synchronizedMap(new HashMap<>());
    this.members = Collections.synchronizedMap(new HashMap<>());
  }

  public Channel channel(long id) {
    return channels.computeIfAbsent(id, k -> new Channel(id));
  }

  public boolean channelExists(long id) {
    return channels.containsKey(id);
  }

  public Collection<Channel> channels() {
    return channels.values();
  }

  public Member member(Long id) {
    return members.computeIfAbsent(id, k -> new Member(id));
  }

  public boolean memberExists(long id) {
    return members.containsKey(id);
  }

  public Collection<Member> members() {
    return members.values();
  }
}