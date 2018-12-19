package uk.co.samwho.modopticon;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import uk.co.samwho.modopticon.storage.Storage;

@Singleton
public final class JSONServer implements Runnable {
  private final Storage storage;
  private final Gson gson;

  @Inject
  public JSONServer(Storage storage, Gson gson) {
    this.storage = storage;
    this.gson = gson;
  }

  @Override
  public void run() {
    initExceptionHandler(e -> {
      throw new RuntimeException(e);
    });

    port(8080);

    get("/", (req, res) -> {
      res.header("Content-Encoding", "gzip");
      res.header("Content-Type", "application/json");
      return gson.toJson(storage);
    });
  }
}