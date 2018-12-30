package uk.co.samwho.modopticon.api.auth;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

@Singleton
@ThreadSafe
public final class ApiKeyAuth {
  private static final class Entry {
    String key, owner;
  }

  private final Map<String, Entry> keys;

  @Inject
  ApiKeyAuth(Gson gson, @Named("api_keys.json") InputStream apiKeyJson) {
    Type t = new TypeToken<ArrayList<Entry>>(){}.getType();
    List<Entry> es = new Gson().fromJson(new InputStreamReader(apiKeyJson), t);

    ImmutableMap.Builder<String, Entry> builder = ImmutableMap.builder();
    es.forEach(e -> builder.put(e.key, e));
    keys = builder.build();
  }

  public boolean isValid(String key) {
    return keys.containsKey(key);
  }
}