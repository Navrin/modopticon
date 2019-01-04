package uk.co.samwho.modopticon.modules;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.samwho.modopticon.entitymodifiers.AuditLogListener;
import uk.co.samwho.modopticon.entitymodifiers.AvatarListener;
import uk.co.samwho.modopticon.entitymodifiers.ChannelNameListener;
import uk.co.samwho.modopticon.entitymodifiers.EntityModifier;
import uk.co.samwho.modopticon.entitymodifiers.GuildNameListener;
import uk.co.samwho.modopticon.entitymodifiers.LastMessageListener;
import uk.co.samwho.modopticon.entitymodifiers.NicknameListener;
import uk.co.samwho.modopticon.entitymodifiers.RecentMessagesListener;
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
    static EntityModifier provideRecentMessagesListener(RecentMessagesListener l) {
        return l;
    }

    @Provides
    @Singleton
    @IntoSet
    static EntityModifier provideAvatarListener(AvatarListener l) {
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
    @IntoSet
    static EntityModifier provideChannelNameListener(ChannelNameListener l) {
        return l;
    }

    @Provides
    @Singleton
    @IntoSet
    static EntityModifier provideGuildNameListener(GuildNameListener l) {
        return l;
    }

    @Provides
    @Singleton
    static Set<? extends ListenerAdapter> provideListenerAdapters(Set<EntityModifier> entitymodifiers) {
        return entitymodifiers;
    }
}