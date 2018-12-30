package uk.co.samwho.modopticon.api.v1.graphql;

import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import uk.co.samwho.modopticon.storage.Entity;
import uk.co.samwho.modopticon.storage.Guild;
import uk.co.samwho.modopticon.storage.User;
import uk.co.samwho.modopticon.entitymodifiers.EntityModifier;
import uk.co.samwho.modopticon.storage.Channel;
import uk.co.samwho.modopticon.storage.Member;
import uk.co.samwho.modopticon.storage.Storage;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import graphql.Scalars;

@Singleton
public final class Schema {
  private final Storage storage;
  private final Set<EntityModifier> entityModifiers;

  @Inject
  Schema(Storage storage, Set<EntityModifier> entityModifiers) {
    this.storage = storage;
    this.entityModifiers = entityModifiers;
  }

  public GraphQLSchema getSchema() {
    return GraphQLSchema.newSchema()
      .query(getRootObjectType())
      .build();
  }

  public GraphQLObjectType getRootObjectType() {
    return newObject()
        .name("query")
        .field(newFieldDefinition()
          .name("users")
          .type(new GraphQLList(entityType(User.class)))
          .dataFetcher(env -> storage.users()))
        .field(newFieldDefinition()
          .name("guilds")
          .type(new GraphQLList(guildType()))
          .dataFetcher(env -> storage.guilds()))
        .build();
  }

  private GraphQLObjectType guildType() {
    return entityTypeBuilder(Guild.class)
      .field(newFieldDefinition()
        .name("channels")
        .type(new GraphQLList(entityType(Channel.class)))
        .dataFetcher(env -> {
          Guild guild = env.getSource();
          return guild.channels();
        }))
      .field(newFieldDefinition()
        .name("members")
        .type(new GraphQLList(entityType(Member.class)))
        .dataFetcher(env -> {
          Guild guild = env.getSource();
          return guild.members();
        }))
      .build();
  }

  private GraphQLObjectType entityType(Class<? extends Entity> eClass) {
    return entityTypeBuilder(eClass).build();
  }

  private GraphQLObjectType.Builder entityTypeBuilder(Class<? extends Entity> eClass) {
    return newObject()
        .name(eClass.getSimpleName())
        .field(newFieldDefinition()
          .name("id")
          .type(Scalars.GraphQLString)
          .dataFetcher(env -> {
            Entity e = env.getSource();
            return e.id();
          }))
        .field(newFieldDefinition()
          .name("attributes")
          .type(entityAttributesType(eClass))
          .dataFetcher(env -> {
            Entity entity = env.getSource();
            return entity.attributes();
          }));
  }

  private GraphQLObjectType entityAttributesType(Class<? extends Entity> eClass) {
    GraphQLObjectType.Builder attributes =
      newObject().name(eClass.getSimpleName() + "Attributes");

    entityModifiers.forEach(em -> {
      em.extensionsFor(eClass).forEach((name, type) -> {
        attributes.field(
          newFieldDefinition()
            .name(name)
            .type(type)
            .dataFetcher(env -> {
              Map<String, Object> m = env.getSource();
              return m.get(name);
            }));
      });
    });

    return attributes.build();
  }
}