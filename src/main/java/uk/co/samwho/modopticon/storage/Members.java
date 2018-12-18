package uk.co.samwho.modopticon.storage;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.member.GenericGuildMemberEvent;

public final class Members {
  public static Long id(Member member) {
    return Users.id(member.getUser());
  }

  public static Long id(GenericGuildMemberEvent event) {
    return id(event.getMember());
  }
}