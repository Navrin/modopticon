package uk.co.samwho.modopticon.modules;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.security.auth.login.LoginException;

import dagger.Module;
import dagger.Provides;

import java.util.Set;

@Module
public class JDAModule {
    @Provides
    @Singleton
    static JDA jda(@Named("token") String token, Set<? extends ListenerAdapter> listeners) {
        try {
            JDABuilder builder = new JDABuilder(AccountType.BOT);
            builder.setToken(token);
            listeners.forEach(builder::addEventListener);
            return builder.build();
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
    }
}
