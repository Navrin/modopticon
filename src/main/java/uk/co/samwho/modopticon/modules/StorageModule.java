package uk.co.samwho.modopticon.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import uk.co.samwho.modopticon.storage.Storage;

@Module
public final class StorageModule {
  @Provides
  @Singleton
  static Storage provideStorage(Storage storage) {
    return storage;
  }
}