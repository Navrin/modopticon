package uk.co.samwho.modopticon.api.v1;

import static spark.Spark.*;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.flogger.FluentLogger;
import com.google.gson.Gson;

import graphql.ExecutionResult;
import uk.co.samwho.modopticon.api.auth.ApiKeyAuth;
import uk.co.samwho.modopticon.api.v1.graphql.Executor;
import uk.co.samwho.modopticon.api.v1.websocket.WebSocketServer;
import uk.co.samwho.modopticon.storage.Guild;
import uk.co.samwho.modopticon.storage.Storage;

@Singleton
public final class Server implements Runnable {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final Storage storage;
  private final Gson gson;
  private final WebSocketServer webSocketServer;
  private final Executor graphQLExecutor;
  private final ApiKeyAuth apiKeyAuth;

  @Inject
  public Server(Storage storage, Gson gson, WebSocketServer webSocketServer, Executor graphQLExecutor,
      ApiKeyAuth apiKeyAuth) {
    this.storage = storage;
    this.gson = gson;
    this.webSocketServer = webSocketServer;
    this.graphQLExecutor = graphQLExecutor;
    this.apiKeyAuth = apiKeyAuth;
  }

  @Override
  public void run() {
    initExceptionHandler(e -> {
      throw new RuntimeException(e);
    });

    webSocket("/api/v1/websocket", webSocketServer);

    port(8080);

    staticFiles.location("/www");
    // TODO(samwho): add in some dev environment flag to toggle this
    // staticFiles.externalLocation("/home/sam/code/java/modopticon/src/main/resources/www");

    path("/api/v1", () -> {
      before("/*", (req, res) -> {
        if (req.pathInfo().equals("/api/v1/websocket")) {
          return;
        }

        res.header("Content-Encoding", "gzip");
        res.header("Content-Type", "application/json");

        String apiKey = req.headers("X-Modopticon-Apikey");

        if (Strings.isNullOrEmpty(apiKey)) {
          halt(403, "{\"error\":\"no api key found, please add it in the X-Modopticon-Apikey header\"}");
        }

        if (!apiKeyAuth.isValid(apiKey)) {
          halt(403, "{\"error\":\"invalid api key\"}");
        }
      });

      get("/graphql", (req, res) -> {
        String query = req.queryParams("q");
        if (Strings.isNullOrEmpty(query)) {
          halt(400, "{\"error\":\"no query specified\"}");
        }

        ExecutionResult result = graphQLExecutor.execute(query);

        if (!result.getErrors().isEmpty()) {
          halt(400, "{\"error\":\" " + Joiner.on(", ").join(result.getErrors()) + "\"}");
        }

        return gson.toJson(result.toSpecification().get("data"));
      });

      path("/rest", () -> {
        notFound((req, res) -> {
          return "{\"error\": \"resource not found\"}";
        });

        internalServerError((req, res) -> {
          return "{\"error\": \"internal server error\"}";
        });

        get("/everything", (req, res) -> {
          return gson.toJson(storage);
        });

        get("/users", (req, res) -> {
          return gson.toJson(storage.users());
        });

        get("/users/:uid", (req, res) -> {
          long uid = Long.valueOf(req.params(":uid"));

          if (!storage.userExists(uid)) {
            halt(404, "{\"error\": \"user not found\"}");
          }

          return gson.toJson(storage.user(uid));
        });

        get("/guilds", (req, res) -> {
          return gson.toJson(storage.guilds());
        });

        get("/guilds/:gid", (req, res) -> {
          long gid = Long.valueOf(req.params(":gid"));

          if (!storage.guildExists(gid)) {
            halt(404, "{\"error\": \"guild not found\"}");
          }

          return gson.toJson(storage.guild(gid));
        });

        get("/guilds/:gid/channels", (req, res) -> {
          long gid = Long.valueOf(req.params(":gid"));

          if (!storage.guildExists(gid)) {
            halt(404, "{\"error\": \"guild not found\"}");
          }

          Guild guild = storage.guild(gid);

          return gson.toJson(guild.channels());
        });

        get("/guilds/:gid/channels/:cid", (req, res) -> {
          long gid = Long.valueOf(req.params(":gid"));
          long cid = Long.valueOf(req.params(":cid"));

          if (!storage.guildExists(gid)) {
            halt(404, "{\"error\": \"guild not found\"}");
          }

          Guild guild = storage.guild(gid);

          if (!guild.channelExists(cid)) {
            halt(404, "{\"error\": \"channel not found\"}");
          }

          return gson.toJson(guild.channel(cid));
        });

        get("/guilds/:gid/members", (req, res) -> {
          long gid = Long.valueOf(req.params(":gid"));

          if (!storage.guildExists(gid)) {
            halt(404, "{\"error\": \"guild not found\"}");
          }

          Guild guild = storage.guild(gid);

          return gson.toJson(guild.members());
        });

        get("/guilds/:gid/members/:mid", (req, res) -> {
          long gid = Long.valueOf(req.params(":gid"));
          long mid = Long.valueOf(req.params(":mid"));

          if (!storage.guildExists(gid)) {
            halt(404, "{\"error\": \"guild not found\"}");
          }

          Guild guild = storage.guild(gid);

          if (!guild.memberExists(mid)) {
            halt(404, "{\"error\": \"member not found\"}");
          }

          return gson.toJson(guild.member(mid));
        });

        exception(NumberFormatException.class, (e, req, res) -> {
          halt(400, "{\"error\": \"unable to parse id as number\"}");
        });
      });
    });
  }
}