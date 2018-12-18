package uk.co.samwho.modopticon;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import net.dv8tion.jda.core.JDA;
import uk.co.samwho.modopticon.annotations.Init;
import uk.co.samwho.modopticon.guice.ClockModule;
import uk.co.samwho.modopticon.guice.ConfigModule;
import uk.co.samwho.modopticon.guice.GsonModule;
import uk.co.samwho.modopticon.guice.JDAModule;
import uk.co.samwho.modopticon.guice.ListenerModule;

import java.util.Set;

public class Main {
    private final Injector injector;

    private Main() {
        injector = Guice.createInjector(
            new GsonModule(),
            new ClockModule(),
            new ConfigModule(),
            new ListenerModule(),
            new JDAModule()
        );
    }

    public void run() throws InterruptedException {
        Set<@Init Runnable> inits = injector.getInstance(Key.get(new TypeLiteral<Set<@Init Runnable>>() {}));
        for (Runnable init : inits) {
            init.run();
        }

        injector.getInstance(JDA.class);
        injector.getInstance(JSONServer.class).run();
    }

    public static void main(String... args) throws Exception {
        new Main().run();
    }
}
