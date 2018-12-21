package uk.co.samwho.modopticon.entitymodifiers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import graphql.schema.GraphQLOutputType;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.samwho.modopticon.storage.Entity;

public abstract class EntityModifier extends ListenerAdapter {
  private final Map<Class<? extends Entity>, Map<String, GraphQLOutputType>> definitions;

  protected EntityModifier() {
    this.definitions = new HashMap<>();
  }

  protected void extend(Class<? extends Entity> eClass, String name, GraphQLOutputType type) {
    Map<String, GraphQLOutputType> fields = definitions.computeIfAbsent(eClass, k -> new HashMap<>());
    if (fields.containsKey(name)) {
      throw new IllegalArgumentException("cannot overwrite entity attribute extensions");
    }
    fields.put(name, type);
  }

  public Map<String, GraphQLOutputType> extensionsFor(Class<? extends Entity> eClass) {
    return Collections.unmodifiableMap(definitions.getOrDefault(eClass, Collections.emptyMap()));
  }
}