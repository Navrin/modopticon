package uk.co.samwho.modopticon.storage;

import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.channel.text.GenericTextChannelEvent;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;

public final class Channels {
  public static long id(MessageChannel channel) {
    return channel.getIdLong();
  }

  public static long id(Channel channel) {
    return channel.getIdLong();
  }

  public static long id(TextChannel channel) {
    return channel.getIdLong();
  }

  public static long id(GenericTextChannelEvent event) {
    return id(event.getChannel());
  }

  public static long id(GenericMessageEvent event) {
    return id(event.getChannel());
  }

  public static long id(GenericGuildMessageEvent event) {
    return id(event.getChannel());
  }
}