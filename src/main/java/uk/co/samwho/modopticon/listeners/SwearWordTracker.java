package uk.co.samwho.modopticon.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.samwho.modopticon.storage.Storage;
import uk.co.samwho.modopticon.storage.Users;
import uk.co.samwho.modopticon.text.WordList;
import uk.co.samwho.modopticon.text.WordListTracker;

import java.time.Duration;
import java.util.stream.Stream;

@Singleton
public class SwearWordTracker extends ListenerAdapter {
    private static final String KEY = "sweary";

    private final WordListTracker wordListTracker;

    @Inject
    public SwearWordTracker(@Named("bad_words") Stream<String> badWords, Storage storage) {
       this.wordListTracker = WordListTracker.builder()
           .wordList(WordList.from(badWords))
           .duration(Duration.ofMinutes(10L))
           .threshold(5)
           .addOverThresholdCallback((user) -> storage.user(Users.id(user)).attributes().put(KEY, "true"))
           .addUnderThresholdCallback((user) -> storage.user(Users.id(user)).attributes().put(KEY, "false"))
           .build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        wordListTracker.pushEvent(event);
    }
}
