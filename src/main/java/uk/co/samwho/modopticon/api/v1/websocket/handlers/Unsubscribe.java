package uk.co.samwho.modopticon.api.v1.websocket.handlers;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.samwho.modopticon.api.v1.websocket.EntityObserver;
import uk.co.samwho.modopticon.api.v1.websocket.Message;
import uk.co.samwho.modopticon.storage.Entity;
import uk.co.samwho.modopticon.storage.InvalidResourceIdentifierException;
import uk.co.samwho.modopticon.storage.Storage;

@Singleton
public final class Unsubscribe implements MessageHandler {
  private final Storage storage;

  @Inject
  Unsubscribe(Storage storage) {
    this.storage = storage;
  }

  @Override
  public Message handle(EntityObserver eo, List<String> args) throws Exception {
    if (args.size() != 2) {
      return Message.error("incorrect number of arguments to unsubscribe");
    }

    try {
      return storage.fromResourceIdentifier(args.get(1))
        .map(e -> {
          eo.stopObserving(e);
          return Message.unsubscribed(e);
        })
        .orElseGet(() -> Message.entityNotFound(args.get(1)));
    } catch (InvalidResourceIdentifierException e) {
      return Message.invalidResourceIdentifier(args.get(1));
    }
  }
}