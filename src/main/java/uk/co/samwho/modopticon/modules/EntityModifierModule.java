package uk.co.samwho.modopticon.modules;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.samwho.modopticon.entitymodifiers.AuditLogListener;
import uk.co.samwho.modopticon.entitymodifiers.EntityModifier;
import uk.co.samwho.modopticon.entitymodifiers.LastMessageListener;
import uk.co.samwho.modopticon.entitymodifiers.NicknameListener;
import uk.co.samwho.modopticon.entitymodifiers.RecentChattersListener;
import uk.co.samwho.modopticon.entitymodifiers.SwearWordTracker;
import uk.co.samwho.modopticon.entitymodifiers.UserJoinCreateListener;

@Module
public class EntityModifierModule {
    @Provides
    @Singleton
    @IntoSet
    static EntityModifier provideAuditLogListener(AuditLogListener l) {
        return l;
    }

    @Provides
    @Singleton
    @IntoSet
    static EntityModifier provideLastMessageListener(LastMessageListener l) {
        return l;
    }

    @Provides
    @Singleton
    @IntoSet
    static EntityModifier provideNicknameListener(NicknameListener l) {
        return l;
    }

    @Provides
    @Singleton
    @IntoSet
    static EntityModifier provideRecentChattersListener(RecentChattersListener l) {
        return l;
    }

    @Provides
    @Singleton
    @IntoSet
    static EntityModifier provideSwearWorkTracker(SwearWordTracker l) {
        return l;
    }

    @Provides
    @Singleton
    @IntoSet
    static EntityModifier provideUserJoinCreateListener(UserJoinCreateListener l) {
        return l;
    }

    @Provides
    @Singleton
    static Set<? extends ListenerAdapter> provideListenerAdapters(Set<EntityModifier> entitymodifiers) {
        return entitymodifiers;
    }
}