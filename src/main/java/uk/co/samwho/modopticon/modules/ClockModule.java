
package uk.co.samwho.modopticon.modules;

import java.time.Clock;

import dagger.Module;
import dagger.Provides;

@Module
public final class ClockModule {
  @Provides
  static Clock provideClock() {
    return Clock.systemUTC();
  }
}
