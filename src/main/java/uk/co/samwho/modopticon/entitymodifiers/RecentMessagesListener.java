package uk.co.samwho.modopticon.entitymodifiers;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

import graphql.Scalars;
import graphql.schema.GraphQLList;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import uk.co.samwho.modopticon.storage.Channel;
import uk.co.samwho.modopticon.storage.Channels;
import uk.co.samwho.modopticon.storage.Guilds;
import uk.co.samwho.modopticon.storage.Members;
import uk.co.samwho.modopticon.storage.Storage;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Singleton
public final class RecentMessagesListener extends EntityModifier {
  private static final String KEY = "recentMessages";
  private static final int NUM_MESSAGES = 5;

  private final Storage storage;
  private final Map<Long, SortedSet<Message>> channels;

  @Inject
  public RecentMessagesListener(Storage storage) {
    this.storage = storage;
    this.channels = Collections.synchronizedMap(new HashMap<>());

    extend(Channel.class, KEY,
      new GraphQLList(
        newObject()
          .name("Message")
          .field(
            newFieldDefinition()
              .name("memberId")
              .type(Scalars.GraphQLString))
          .field(
            newFieldDefinition()
              .name("message")
              .type(Scalars.GraphQLString))
          .field(
            newFieldDefinition()
              .name("time")
              .type(Scalars.GraphQLString))
          .build()));
  }

  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    SortedSet<Message> set = updateChatters(event);

    storage
      .guild(Guilds.id(event))
      .channel(Channels.id(event))
      .attributes()
      .put(KEY, set.stream().collect(Collectors.toList()));
  }

  private final SortedSet<Message> updateChatters(GuildMessageReceivedEvent event) {
    SortedSet<Message> set =
      channels.computeIfAbsent(
        Channels.id(event),
        k -> Collections.synchronizedSortedSet(
          new TreeSet<>(
            Comparator.comparing(Message::time).reversed())));

    String memberId = storage.guild(Guilds.id(event)).member(Members.id(event)).id();
    OffsetDateTime ct = event.getMessage().getCreationTime();
    Message mwt = new Message(memberId, event.getMessage().getContentDisplay(), ct);

    if (set.isEmpty()) {
      set.add(mwt);
      return set;
    }

    if (set.size() < NUM_MESSAGES) {
      set.add(mwt);
      return set;
    }

    if (set.last().time().isAfter(ct)) {
      return set;
    }

    set.remove(set.last());
    set.add(mwt);

    return set;
  }

  private static final class Message {
    @Expose private final String memberId;
    @Expose private final String message;
    @Expose private final OffsetDateTime time;

    Message(String memberId, String message, OffsetDateTime time) {
      this.memberId = memberId;
      this.message = message;
      this.time = time;
    }

    OffsetDateTime time() {
      return time;
    }
  }
}