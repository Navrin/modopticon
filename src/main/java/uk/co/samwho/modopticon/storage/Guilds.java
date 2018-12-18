package uk.co.samwho.modopticon.storage;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.GenericGuildEvent;
import net.dv8tion.jda.core.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public final class Guilds {
  public static Long id(Guild guild) {
    return guild.getIdLong();
  }

  public static Long id(GenericGuildEvent event) {
    return id(event.getGuild());
  }

  public static Long id(GenericMessageEvent event) {
    return id(event.getGuild());
  }

  public static Long id(Member member) {
    return id(member.getGuild());
  }

  public static Long id(GenericGuildMemberEvent event) {
    return id(event.getGuild());
  }
}