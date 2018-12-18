package uk.co.samwho.modopticon.storage;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.core.events.user.GenericUserEvent;

public final class Users {
  public static Long id(User user) {
    return user.getIdLong();
  }

  public static Long id(GenericUserEvent event) {
    return id(event.getUser());
  }

  public static Long id(Member member) {
    return id(member.getUser());
  }

  public static Long id(GenericGuildMemberEvent event) {
    return id(event.getMember());
  }
}