package uk.co.samwho.modopticon.guice;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;

public final class GsonModule extends AbstractModule {
    @Override
    public void configure() {
      bind(Gson.class).toInstance(new Gson());
    }
}
