package uk.co.samwho.modopticon.listeners;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.samwho.modopticon.storage.Storage;

@Singleton
public final class LastMessageListener extends ListenerAdapter {
  private final Clock clock;
  private final Storage storage;

  @Inject
  public LastMessageListener(Clock clock, Storage storage) {
    this.clock = clock;
    this.storage = storage;
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    String isoNow = ZonedDateTime.now(clock).format(DateTimeFormatter.ISO_DATE_TIME);
    storage.channel(event).setAttribute("lastMessage", isoNow);
  }
}