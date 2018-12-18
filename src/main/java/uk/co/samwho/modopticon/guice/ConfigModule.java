package uk.co.samwho.modopticon.guice;

import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import uk.co.samwho.modopticon.annotations.Init;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.LogManager;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ConfigModule extends AbstractModule {
    @Override
    public void configure() {
        Multibinder<@Init Runnable> inits = Multibinder.newSetBinder(binder(),
                Key.get(new TypeLiteral<@Init Runnable>() {
                }));
        inits.addBinding().to(ConfigureLogging.class);
    }

    @Provides
    @Singleton
    @Named("logging.properties")
    private InputStream loggingProperties() {
        return resourceInputStream("logging.properties");
    }

    @Provides
    @Singleton
    @Named("token")
    private String token() {
        return resource("token.txt");
    }

    @Provides
    @Singleton
    @Named("bad_words")
    private Stream<String> badWords() {
        return resourceStringStream("bad_words.txt");
    }

    @Init
    @Singleton
    private static class ConfigureLogging implements Runnable {
        private final InputStream loggingProperties;

        @Inject
        ConfigureLogging(@Named("logging.properties") InputStream loggingProperties) {
            this.loggingProperties = loggingProperties;
        }

        @Override
        public void run() {
            try {
                LogManager.getLogManager().readConfiguration(loggingProperties);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String resource(String path) {
        return resourceStringStream(path).collect(Collectors.joining());
    }

    private static Stream<String> resourceStringStream(String path) {
        return new BufferedReader(new InputStreamReader(resourceInputStream(path))).lines();
    }

    private static InputStream resourceInputStream(String path) {
        InputStream is = ClassLoader.getSystemResourceAsStream(path);

        if (is == null) {
            throw new RuntimeException("couldn't find resource " + path + ". You will need to create it and "
                    + "populate it with the correct information before you can run this code.");
        }

        return is;
    }
}
