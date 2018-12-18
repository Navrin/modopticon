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

  Guild(Long id) {
    this.id = id;
    this.channels = Collections.synchronizedMap(new HashMap<>());
  }

  Channel getOrCreateChannel(Long id) {
    return channels.computeIfAbsent(id, k -> new Channel(id));
  }

  Channel getOrCreateChannel(MessageChannel channel) {
    return getOrCreateChannel(channel.getIdLong());
  }

  Channel getOrCreateChannel(net.dv8tion.jda.core.entities.Channel channel) {
    return getOrCreateChannel(channel.getIdLong());
  }

  public Channel channel(Long id) {
    return getOrCreateChannel(id);
  }

  public Channel channel(Message message) {
    return getOrCreateChannel(message.getChannel());
  }

  public Channel channel(MessageReceivedEvent event) {
    return getOrCreateChannel(event.getChannel());
  }

  public Collection<Channel> channels() {
    return channels.values();
  }
}