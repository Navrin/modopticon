package uk.co.samwho.modopticon.storage;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public final class Members {
  public static long id(Member member) {
    return Users.id(member.getUser());
  }

  public static long id(GenericGuildMemberEvent event) {
    return id(event.getMember());
  }

  public static long id(GuildMessageReceivedEvent event) {
    return id(event.getMember());
  }
}