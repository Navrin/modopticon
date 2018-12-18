
package uk.co.samwho.modopticon.guice;

import java.time.Clock;

import com.google.inject.AbstractModule;

public final class ClockModule extends AbstractModule {
    @Override
    public void configure() {
      bind(Clock.class).toInstance(Clock.systemUTC());
    }
}
