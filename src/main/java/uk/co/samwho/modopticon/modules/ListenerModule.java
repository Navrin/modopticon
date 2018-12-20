package uk.co.samwho.modopticon.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.samwho.modopticon.listeners.AuditLogListener;
import uk.co.samwho.modopticon.listeners.LastMessageListener;
import uk.co.samwho.modopticon.listeners.NicknameListener;
import uk.co.samwho.modopticon.listeners.RecentChattersListener;
import uk.co.samwho.modopticon.listeners.SwearWordTracker;
import uk.co.samwho.modopticon.listeners.UserJoinCreateListener;

@Module
public class ListenerModule {
    @Provides
    @Singleton
    @IntoSet
    static ListenerAdapter provideAuditLogListener(AuditLogListener l) {
        return l;
    }

    @Provides
    @Singleton
    @IntoSet
    static ListenerAdapter provideLastMessageListener(LastMessageListener l) {
        return l;
    }

    @Provides
    @Singleton
    @IntoSet
    static ListenerAdapter provideNicknameListener(NicknameListener l) {
        return l;
    }

    @Provides
    @Singleton
    @IntoSet
    static ListenerAdapter provideRecentChattersListener(RecentChattersListener l) {
        return l;
    }

    @Provides
    @Singleton
    @IntoSet
    static ListenerAdapter provideSwearWorkTracker(SwearWordTracker l) {
        return l;
    }

    @Provides
    @Singleton
    @IntoSet
    static ListenerAdapter provideUserJoinCreateListener(UserJoinCreateListener l) {
        return l;
    }
}