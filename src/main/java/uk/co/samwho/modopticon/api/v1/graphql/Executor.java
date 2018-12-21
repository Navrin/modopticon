package uk.co.samwho.modopticon.api.v1.graphql;

import javax.inject.Inject;
import javax.inject.Singleton;

import graphql.ExecutionResult;
import graphql.GraphQL;

@Singleton
public final class Executor {
  private final GraphQL gql;

  @Inject
  Executor(Schema schema) {
    this.gql = GraphQL.newGraphQL(schema.getSchema()).build();
  }

  public ExecutionResult execute(String query) {
    return gql.execute(query);
  }
}