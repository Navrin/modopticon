package uk.co.samwho.modopticon.modules;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import dagger.Module;
import dagger.Provides;

@Module
public final class GsonModule {
    @Provides
    @Singleton
    static Gson gson() {
      GsonBuilder builder = new GsonBuilder();
      builder.setPrettyPrinting();
      builder.excludeFieldsWithoutExposeAnnotation();

      builder.registerTypeAdapter(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
        @Override
        public JsonElement serialize(OffsetDateTime src, Type typeOfSrc, JsonSerializationContext context) {
          return new JsonPrimitive(src.format(DateTimeFormatter.ISO_DATE_TIME));
        }
      });

      return builder.create();
    }
}
