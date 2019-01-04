package uk.co.samwho.modopticon.entitymodifiers;

import javax.inject.Inject;
import javax.inject.Singleton;

import graphql.Scalars;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildReadyEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateAvatarEvent;
import uk.co.samwho.modopticon.storage.Storage;
import uk.co.samwho.modopticon.storage.Users;

@Singleton
public final class AvatarListener extends EntityModifier {
  private static final String AVATAR = "avatar";

  private final Storage storage;

  @Inject
  public AvatarListener(Storage storage) {
    this.storage = storage;

    extend(uk.co.samwho.modopticon.storage.User.class, AVATAR, Scalars.GraphQLString);
  }

  @Override
  public void onGuildReady(GuildReadyEvent event) {
    event.getGuild().getMembers().forEach(m -> processUser(m.getUser()));
  }

  @Override
  public void onGuildJoin(GuildJoinEvent event) {
    event.getGuild().getMembers().forEach(m -> processUser(m.getUser()));
  }

  @Override
  public void onUserUpdateAvatar(UserUpdateAvatarEvent event) {
    processUser(event.getUser());
  }

  private void processUser(User user) {
    if (Users.isDeleted(user) || user.getEffectiveAvatarUrl() == null) {
      return;
    }

    storage
      .user(Users.id(user))
      .attributes()
      .put(AVATAR, user.getEffectiveAvatarUrl());
  }
}