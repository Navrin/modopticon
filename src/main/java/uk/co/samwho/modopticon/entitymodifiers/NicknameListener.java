package uk.co.samwho.modopticon.entitymodifiers;

import javax.inject.Inject;
import javax.inject.Singleton;

import graphql.Scalars;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateNameEvent;
import uk.co.samwho.modopticon.storage.Guilds;
import uk.co.samwho.modopticon.storage.Members;
import uk.co.samwho.modopticon.storage.Storage;
import uk.co.samwho.modopticon.storage.Users;

@Singleton
public final class NicknameListener extends EntityModifier {
  private static final String NAME = "name";
  private static final String NICKNAME = "nickname";

  private final Storage storage;

  @Inject
  public NicknameListener(Storage storage) {
    this.storage = storage;

    extend(uk.co.samwho.modopticon.storage.User.class, NAME, Scalars.GraphQLString);
    extend(uk.co.samwho.modopticon.storage.Member.class, NICKNAME, Scalars.GraphQLString);
  }

  @Override
  public void onGuildReady(GuildReadyEvent event) {
    event.getGuild().getMembers().forEach(this::processMember);
  }

  @Override
  public void onGuildJoin(GuildJoinEvent event) {
    event.getGuild().getMembers().forEach(this::processMember);
  }

  @Override
  public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) {
    processMember(event.getMember());
  }

  @Override
  public void onUserUpdateName(UserUpdateNameEvent event) {
    processUser(event.getUser());
  }

  private void processMember(Member member) {
    processUser(member.getUser());

    if (Users.isDeleted(member) || member.getNickname() == null) {
      return;
    }

    storage
      .guild(Guilds.id(member))
      .member(Members.id(member))
      .attributes()
      .put(NICKNAME, member.getNickname());
  }

  private void processUser(User user) {
    if (Users.isDeleted(user) || user.getName() == null) {
      return;
    }

    storage
      .user(Users.id(user))
      .attributes()
      .put(NAME, user.getName());
  }
}