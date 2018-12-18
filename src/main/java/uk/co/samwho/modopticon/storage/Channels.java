package uk.co.samwho.modopticon.storage;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.channel.text.GenericTextChannelEvent;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public final class Channels {
  public static Long id(MessageChannel channel) {
    return channel.getIdLong();
  }

  public static Long id(GenericTextChannelEvent event) {
    return id(event.getChannel());
  }

  public static Long id(GenericMessageEvent event) {
    return id(event.getChannel());
  }
}