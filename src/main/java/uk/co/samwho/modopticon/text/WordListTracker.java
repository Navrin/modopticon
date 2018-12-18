package uk.co.samwho.modopticon.text;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.flogger.FluentLogger;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import uk.co.samwho.modopticon.util.EventTracker;
import uk.co.samwho.modopticon.text.WordList;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * A ListenerAdapter that tracks how often users say words in a given word list and fires callbacks when they say more
 * than a given number of times in a given amount of time.
 * <p>
 * <p>Example:
 * <p>
 * <pre>
 *   WordListTracker.builder()
 *     .wordList(WordList.from(Stream.of("foo")))
 *     .duration(Duration.ofSeconds(60))
 *     .threshold(5)
 *     .addCallback((user) -> System.out.println(user.getName() + " is saying foo a lot!"))
 *     .build();
 * </pre>
 */
public final class WordListTracker {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private final WordList wordList;
    private final Duration duration;
    private final int threshold;
    private final Clock clock;
    private final Collection<Consumer<User>> overThresholdCallbacks;
    private final Collection<Consumer<User>> underThresholdCallbacks;
    private final Cache<User, EventTracker> cache;

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private WordList wordList;
        private Duration duration;
        private int threshold = 0;
        private Clock clock;
        private Collection<Consumer<User>> overThresholdCallbacks = Lists.newLinkedList();
        private Collection<Consumer<User>> underThresholdCallbacks = Lists.newLinkedList();

        public Builder wordList(WordList wordList) {
            this.wordList = wordList;
            return this;
        }

        public Builder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public Builder threshold(int threshold) {
            this.threshold = threshold;
            return this;
        }

        public Builder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder addOverThresholdCallback(Consumer<User> callback) {
            this.overThresholdCallbacks.add(callback);
            return this;
        }

        public Builder addUnderThresholdCallback(Consumer<User> callback) {
            this.underThresholdCallbacks.add(callback);
            return this;
        }

        public WordListTracker build() {
            Preconditions.checkNotNull(wordList, "you must supply a wordList");
            Preconditions.checkNotNull(duration, "you must supply a duration");
            Preconditions.checkArgument(threshold > 0, "you must supply a non-zero threshold");
            Preconditions.checkArgument(!overThresholdCallbacks.isEmpty(), "you must supply at least one callback for going over the threshold");

            if (clock == null) {
                clock = Clock.systemDefaultZone();
            }

            return new WordListTracker(
                wordList,
                duration,
                threshold,
                clock,
                Collections.unmodifiableCollection(overThresholdCallbacks),
                Collections.unmodifiableCollection(underThresholdCallbacks));
        }
    }

    private WordListTracker(
        WordList wordList,
        Duration duration,
        int threshold,
        Clock clock,
        Collection<Consumer<User>> overThresholdCallbacks,
        Collection<Consumer<User>> underThresholdCallbacks
    ) {
        this.wordList = wordList;
        this.duration = duration;
        this.threshold = threshold;
        this.clock = clock;
        this.overThresholdCallbacks = overThresholdCallbacks;
        this.underThresholdCallbacks = underThresholdCallbacks;

        this.cache = CacheBuilder.newBuilder()
                .expireAfterAccess(duration.getSeconds(), TimeUnit.SECONDS)
                .removalListener((info) -> {
                    logger.atFine().log("removed key %s (cause: %s)", info.getKey(), info.getCause());

                    // Not sure what the guarantees are on this being called, but it should mean in all
                    // cases that a user hasn't said anything on the list in a while.
                    underThresholdCallbacks.forEach(callback -> callback.accept((User)info.getKey()));
                })
                .build();
    }

    public void pushEvent(Message event) {
        EventTracker tracker;
        try {
            tracker = cache.get(
                    event.getAuthor(), () -> EventTracker.builder().clock(clock).duration(duration).build());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        int numMatches = wordList.numMatches(event.getContentRaw());
        Instant time = event.getCreationTime().toInstant();
        tracker.inc(numMatches, time);

        logger.atFine().log("added %d to counter for %s at %s", numMatches, event.getAuthor().getName(), time);

        if (tracker.count() >= threshold) {
            for (Consumer<User> callback : overThresholdCallbacks) {
                callback.accept(event.getAuthor());
            }
        } else {
            for (Consumer<User> callback : underThresholdCallbacks) {
                callback.accept(event.getAuthor());
            }
        }
    }

    /**
     * Returns users that are currently on the radar for this word list. It doesn't necessarily mean that they have
     * triggered their threshold, just that they've mentioned some of the words in your list recently.
     */
    public Collection<User> currentlyTrackedUsers() {
        Set<User> users = Sets.newHashSet();
        cache.asMap().forEach((user, tracker) -> {
            if (tracker.count() > 0) {
               users.add(user);
            }
        });
        return users;
    }
}
