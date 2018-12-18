package uk.co.samwho.modopticon.testutil;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import static net.dv8tion.jda.core.utils.MiscUtil.DISCORD_EPOCH;

public class DiscordUtil {
    private static AtomicLong counter = new AtomicLong();

    /**
     * JDA uses the message ID to determine the creation date. Message IDs use Twitter's Snowflake encoding, which
     * encodes the timestamp into the ID. This function returns a unique ID with a given time encoded into it. Repeated
     * calls to this function will return different values, in order to guarantee ID uniqueness.
     */
    public static long idFromTime(Instant time) {
        long timestamp = time.toEpochMilli() - DISCORD_EPOCH;
        long machineId = 1;
        long id = counter.incrementAndGet() % 4096;

        return (timestamp << 22) + (machineId << 12) + id;
    }
}
