package uk.co.samwho.modopticon.entitymodifiers;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import uk.co.samwho.modopticon.storage.Storage;
import uk.co.samwho.modopticon.storage.User;
import uk.co.samwho.modopticon.storage.Users;
import uk.co.samwho.modopticon.text.WordList;
import uk.co.samwho.modopticon.text.WordListTracker;

import java.time.Duration;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import graphql.Scalars;

@Singleton
public class SwearWordTracker extends EntityModifier {
    private static final String KEY = "sweary";

    private final WordListTracker wordListTracker;

    @Inject
    public SwearWordTracker(@Named("bad_words") Stream<String> badWords, Storage storage) {
        this.wordListTracker = WordListTracker.builder().wordList(WordList.from(badWords))
                .duration(Duration.ofMinutes(10L)).threshold(5)
                .addOverThresholdCallback((user) -> storage.user(Users.id(user)).attributes().put(KEY, true))
                .addUnderThresholdCallback((user) -> storage.user(Users.id(user)).attributes().put(KEY, false)).build();

        extend(User.class, KEY, Scalars.GraphQLBoolean);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        wordListTracker.pushEvent(event.getMessage());
    }
}
