package uk.co.samwho.modopticon.modules;

import uk.co.samwho.modopticon.annotations.Init;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.LogManager;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

@Module
public final class ConfigModule {
    @Provides
    @Singleton
    @IntoSet
    @Init
    static Runnable provideLoggerConfig(@Named("logging.properties") InputStream config) {
        return () -> {
            try {
                LogManager.getLogManager().readConfiguration(config);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Provides
    @Singleton
    @Named("logging.properties")
    static InputStream loggingProperties() {
        return resourceInputStream("logging.properties");
    }

    @Provides
    @Singleton
    @Named("token")
    static String token() {
        return resource("token.txt");
    }

    @Provides
    @Singleton
    @Named("bad_words")
    static Stream<String> badWords() {
        return resourceStringStream("bad_words.txt");
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
