package uk.co.samwho.modopticon.entitymodifiers;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import graphql.Scalars;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import uk.co.samwho.modopticon.storage.Guilds;
import uk.co.samwho.modopticon.storage.Member;
import uk.co.samwho.modopticon.storage.Members;
import uk.co.samwho.modopticon.storage.Storage;
import uk.co.samwho.modopticon.storage.User;
import uk.co.samwho.modopticon.storage.Users;

@Singleton
public final class UserJoinCreateListener extends EntityModifier {
  private static final String CREATED = "created";
  private static final String JOINED = "joined";
  private static final String PRESENT = "present";

  private final Storage storage;

  @Inject
  public UserJoinCreateListener(Storage storage) {
    this.storage = storage;

    extend(Member.class, JOINED, Scalars.GraphQLString);
    extend(Member.class, PRESENT, Scalars.GraphQLBoolean);
    extend(User.class, CREATED, Scalars.GraphQLString);
  }

  @Override
  public void onGuildReady(GuildReadyEvent event) {
    event.getGuild().getMembers().forEach(this::processMemberJoin);
  }

  @Override
  public void onGuildJoin(GuildJoinEvent event) {
    event.getGuild().getMembers().forEach(this::processMemberJoin);
  }

  @Override
  public void onGuildMemberJoin(GuildMemberJoinEvent event) {
    processMemberJoin(event.getMember());
  }

  @Override
  public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
    processMemberLeave(event.getMember());
  }

  private void processMemberJoin(net.dv8tion.jda.core.entities.Member member) {
    Map<String, Object> memberAttributes = storage.guild(Guilds.id(member)).member(Members.id(member)).attributes();

    memberAttributes.put(JOINED, member.getJoinDate());
    memberAttributes.put(PRESENT, true);

    storage.user(Users.id(member)).attributes().put(CREATED, member.getUser().getCreationTime());
  }

  private void processMemberLeave(net.dv8tion.jda.core.entities.Member member) {
    Map<String, Object> memberAttributes = storage.guild(Guilds.id(member)).member(Members.id(member)).attributes();

    memberAttributes.remove(JOINED);
    memberAttributes.put(PRESENT, false);
  }
}