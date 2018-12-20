package uk.co.samwho.modopticon.api.v1;

import static spark.Spark.*;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gson.Gson;

import uk.co.samwho.modopticon.storage.Guild;
import uk.co.samwho.modopticon.storage.Storage;

@Singleton
public final class Server implements Runnable {
  private final Storage storage;
  private final Gson gson;

  @Inject
  public Server(Storage storage, Gson gson) {
    this.storage = storage;
    this.gson = gson;
  }

  @Override
  public void run() {
    initExceptionHandler(e -> {
      throw new RuntimeException(e);
    });

    port(8080);

    path("/api/v1", () -> {
      before("/*", (req, res) -> {
        res.header("Content-Encoding", "gzip");
        res.header("Content-Type", "application/json");
      });

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
  }
}