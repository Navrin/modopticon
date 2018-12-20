package uk.co.samwho.modopticon.api.v1;

import static spark.Spark.*;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gson.Gson;

import uk.co.samwho.modopticon.api.v1.websocket.WebSocketServer;
import uk.co.samwho.modopticon.storage.Entity;
import uk.co.samwho.modopticon.storage.Guild;
import uk.co.samwho.modopticon.storage.Storage;

@Singleton
public final class Server implements Runnable {
  private final Storage storage;
  private final Gson gson;
  private final WebSocketServer webSocketServer;

  @Inject
  public Server(Storage storage, Gson gson, WebSocketServer webSocketServer) {
    this.storage = storage;
    this.gson = gson;
    this.webSocketServer = webSocketServer;
  }

  @Override
  public void run() {
    initExceptionHandler(e -> {
      throw new RuntimeException(e);
    });

    webSocket("/api/v1/websocket", webSocketServer);

    port(8080);

    path("/api/v1/rest", () -> {
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

      get("/*", (req, res) -> {
        String path = req.pathInfo();
        String resourceIdentifier = path.substring("/api/v1".length(), path.length());

        Optional<Entity> entity = Optional.empty();

        try {
          entity = storage.fromResourceIdentifier(resourceIdentifier);
        } catch (NumberFormatException e) {
          halt(400, "{\"error\": \"error parsing resource identifier\"}");
        } catch (IllegalArgumentException e) {
          halt(404, "{\"error\": \"invalid resource identifier\"}");
        }

        if (!entity.isPresent()) {
          halt(404, "{\"error\": \"entity not found\"}");
        }

        return gson.toJson(entity.get());
      });
    });
  }
}