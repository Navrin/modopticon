package uk.co.samwho.modopticon.storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public final class StorageTest {
  private final Storage storage = new Storage();

  @Test
  public void testGuild() throws Exception {
    assertThat(storage.guildExists(1)).isFalse();
    assertThat(storage.guild(1)).isNotNull();
    assertThat(storage.guild(1)).isEqualTo(storage.guild(1));
    assertThat(storage.guild(1)).isNotEqualTo(storage.guild(2));
    assertThat(storage.guild(1).id()).isEqualTo("/guilds/1");
    assertThat(storage.guildExists(1)).isTrue();

    assertThat(storage.guild(1)).isEqualTo(storage.fromResourceIdentifier("/guilds/1").get());
  }

  @Test
  public void testUser() throws Exception {
    assertThat(storage.userExists(1)).isFalse();
    assertThat(storage.user(1)).isNotNull();
    assertThat(storage.user(1)).isEqualTo(storage.user(1));
    assertThat(storage.user(1)).isNotEqualTo(storage.user(2));
    assertThat(storage.user(1).id()).isEqualTo("/users/1");
    assertThat(storage.userExists(1)).isTrue();

    assertThat(storage.user(1)).isEqualTo(storage.fromResourceIdentifier("/users/1").get());
  }

  @Test
  public void testGuildMember() throws Exception {
    assertThat(storage.guild(1).memberExists(2)).isFalse();
    assertThat(storage.guild(1).member(2)).isNotNull();
    assertThat(storage.guild(1).member(2)).isEqualTo(storage.guild(1).member(2));
    assertThat(storage.guild(1).member(2)).isNotEqualTo(storage.guild(1).member(3));
    assertThat(storage.guild(1).member(2).id()).isEqualTo("/guilds/1/members/2");
    assertThat(storage.guild(1).memberExists(2)).isTrue();

    assertThat(storage.guild(1).member(2)).isEqualTo(storage.fromResourceIdentifier("/guilds/1/members/2").get());
  }

  @Test
  public void testGuildChannel() throws Exception {
    assertThat(storage.guild(1).channelExists(3)).isFalse();
    assertThat(storage.guild(1).channel(3)).isNotNull();
    assertThat(storage.guild(1).channel(3)).isEqualTo(storage.guild(1).channel(3));
    assertThat(storage.guild(1).channel(3)).isNotEqualTo(storage.guild(1).channel(4));
    assertThat(storage.guild(1).channel(3).id()).isEqualTo("/guilds/1/channels/3");
    assertThat(storage.guild(1).channelExists(3)).isTrue();

    assertThat(storage.guild(1).channel(3)).isEqualTo(storage.fromResourceIdentifier("/guilds/1/channels/3").get());
  }
}