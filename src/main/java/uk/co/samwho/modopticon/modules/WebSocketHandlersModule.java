package uk.co.samwho.modopticon.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import uk.co.samwho.modopticon.api.v1.websocket.handlers.Authenticate;
import uk.co.samwho.modopticon.api.v1.websocket.handlers.MessageHandler;
import uk.co.samwho.modopticon.api.v1.websocket.handlers.Subscribe;
import uk.co.samwho.modopticon.api.v1.websocket.handlers.Subscriptions;
import uk.co.samwho.modopticon.api.v1.websocket.handlers.Unsubscribe;

@Module
public final class WebSocketHandlersModule {
    @Provides
    @Singleton
    @IntoMap @StringKey("authenticate")
    static MessageHandler provideAuthenticationHandler(Authenticate h) {
        return h;
    }

    @Provides
    @Singleton
    @IntoMap @StringKey("subscribe")
    static MessageHandler provideSubscribeHandler(Subscribe h) {
        return h;
    }

    @Provides
    @Singleton
    @IntoMap @StringKey("unsubscribe")
    static MessageHandler provideUnsubscribeHandler(Unsubscribe h) {
        return h;
    }

    @Provides
    @Singleton
    @IntoMap @StringKey("subscriptions")
    static MessageHandler provideSubscriptionsHandler(Subscriptions h) {
        return h;
    }
}