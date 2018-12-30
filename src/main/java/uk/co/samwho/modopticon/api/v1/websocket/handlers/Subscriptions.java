package uk.co.samwho.modopticon.api.v1.websocket.handlers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.samwho.modopticon.api.v1.websocket.EntityObserver;
import uk.co.samwho.modopticon.api.v1.websocket.Message;

@Singleton
public final class Subscriptions implements MessageHandler {
  @Inject
  Subscriptions() {}

  @Override
  public Message handle(EntityObserver eo, List<String> args) throws Exception {
    if (args.size() != 1) {
      return Message.error("subscriptions takes no arguments");
    }

    return Message.subscriptions(eo.observing());
  }
}