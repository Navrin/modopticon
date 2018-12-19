package uk.co.samwho.modopticon.listeners;

import java.time.OffsetDateTime;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.samwho.modopticon.storage.Channel;
import uk.co.samwho.modopticon.storage.Channels;
import uk.co.samwho.modopticon.storage.Guilds;
import uk.co.samwho.modopticon.storage.Members;
import uk.co.samwho.modopticon.storage.Storage;
import uk.co.samwho.modopticon.storage.Users;

@Singleton
public final class NicknameListener extends ListenerAdapter {
  private static final String NAME = "name";
  private static final String NICKNAME = "nickname";

  private final Storage storage;

  @Inject
  public NicknameListener(Storage storage) {
    this.storage = storage;
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
    if (Users.isDeleted(member)) {
      return;
    }

    storage
      .guild(Guilds.id(member))
      .member(Members.id(member))
      .attributes()
      .put(NICKNAME, member.getNickname());

    processUser(member.getUser());
  }

  private void processUser(User user) {
    if (Users.isDeleted(user)) {
      return;
    }

    storage
      .user(Users.id(user))
      .attributes()
      .put(NAME, user.getName());
  }
}