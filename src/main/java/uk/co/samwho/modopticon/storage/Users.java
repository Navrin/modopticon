package uk.co.samwho.modopticon.storage;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.GenericUserEvent;

public final class Users {
  public static long id(User user) {
    return user.getIdLong();
  }

  public static long id(GenericUserEvent event) {
    return id(event.getUser());
  }

  public static long id(Member member) {
    return id(member.getUser());
  }

  public static long id(GenericGuildMemberEvent event) {
    return id(event.getMember());
  }

  public static long id(GuildMessageReceivedEvent event) {
    return id(event.getMember());
  }

  public static boolean isDeleted(Member member) {
    return member == null || isDeleted(member.getUser());
  }

  public static boolean isDeleted(User user) {
    return user == null;
  }

  public static boolean isDeleted(GenericGuildMemberEvent event) {
    return isDeleted(event.getMember());
  }

  public static boolean isDeleted(Message message) {
    return isDeleted(message.getMember());
  }
}