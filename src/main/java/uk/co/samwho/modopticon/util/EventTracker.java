package uk.co.samwho.modopticon.util;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks the occurrences of things in specific time windows. For example,
 * if you want to keep track how many packets you have received in the last
 * 10 minutes you could do:
 *
 * {@code}
 *   EventTracker et = EventTracker.over(Duration.ofMinutes(10));
 *   et.inc() // every time a packet comes in
 *   et.count() // gives you how many incs you have done in last 10 minutes
 * {/@code}
 */
public class EventTracker {
    @AutoValue
    static abstract class Event implements Comparable<Event> {
        abstract Instant time();
        abstract int count();

        static Event create(Instant time, int count) {
            return new AutoValue_EventTracker_Event(time, count);
        }

        @Override
        public int compareTo(Event o) {
            return time().compareTo(o.time());
        }
    }

    private final Clock clock;
    private final Duration duration;
    private final PriorityQueue<Event> events;
    private final AtomicLong counter;

    public static EventTracker over(Duration duration) {
        return builder().duration(duration).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Duration duration;
        private Clock clock = Clock.systemUTC();

        public Builder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public Builder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public EventTracker build() {
            Preconditions.checkNotNull(duration, "you must specify a duration in which to track events");
            return new EventTracker(clock, duration);
        }
    }

    private EventTracker(Clock clock, Duration duration) {
        this.clock = clock;
        this.duration = duration;

        this.events = new PriorityQueue<>();
        this.counter = new AtomicLong(0);
    }

    public void inc() {
        inc(1);
    }

    public void inc(int by) {
        inc(by, clock.instant());
    }

    public void inc(int by, Instant at) {
        Instant min = clock.instant().minus(duration);
        if (at.isBefore(min)) {
            return;
        }

        addEvent(Event.create(at, by));
        removeOldEvents();
    }

    private synchronized void addEvent(Event e) {
        events.add(e);
        counter.addAndGet(e.count());
    }

    private synchronized void removeOldEvents() {
        Instant min = clock.instant().minus(duration);
        while(true) {
            Event oldest = events.peek();
            if (oldest != null && oldest.time().isBefore(min)) {
                counter.addAndGet(-events.poll().count());
            } else {
                break;
            }
        }
    }

    public synchronized void reset() {
        events.clear();
        counter.set(0);
    }

    public long count() {
        removeOldEvents();
        return counter.get();
    }
}
