package uk.co.samwho.modopticon.listeners;

import java.time.OffsetDateTime;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.samwho.modopticon.storage.Channel;
import uk.co.samwho.modopticon.storage.Storage;

@Singleton
public final class LastMessageListener extends ListenerAdapter {
  private static final String KEY = "lastMessage";

  private final Storage storage;

  @Inject
  public LastMessageListener(Storage storage) {
    this.storage = storage;
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    Channel channel = storage.channel(event);

    OffsetDateTime curr = event.getMessage().getCreationTime();
    OffsetDateTime prev = (OffsetDateTime)channel.getAttribute(KEY);

    if (prev == null || curr.isAfter(prev)) {
      channel.setAttribute(KEY, curr);
    }
  }
}