package uk.co.samwho.modopticon.guice;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public final class GsonModule extends AbstractModule {
    @Provides
    @Singleton
    public Gson gson() {
      GsonBuilder builder = new GsonBuilder();

      builder.registerTypeAdapter(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
        @Override
        public JsonElement serialize(OffsetDateTime src, Type typeOfSrc, JsonSerializationContext context) {
          return new JsonPrimitive(src.format(DateTimeFormatter.ISO_DATE_TIME));
        }
      });

      return builder.create();
    }
}
