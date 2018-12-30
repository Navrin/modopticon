package uk.co.samwho.modopticon.api.v1.websocket.handlers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.samwho.modopticon.api.auth.ApiKeyAuth;
import uk.co.samwho.modopticon.api.v1.websocket.EntityObserver;
import uk.co.samwho.modopticon.api.v1.websocket.Message;

@Singleton
public final class Authenticate implements MessageHandler {
  private final ApiKeyAuth apiKeyAuth;

  @Inject
  Authenticate(ApiKeyAuth apiKeyAuth) {
    this.apiKeyAuth = apiKeyAuth;
  }

  @Override
  public Message handle(EntityObserver eo, List<String> args) throws Exception {
    if (args.size() != 2) {
      return Message.error("incorrect number of args to authenticate");
    }

    if (!apiKeyAuth.isValid(args.get(1))) {
      return Message.error("invalid api key");
    }

    eo.authenticate();
    return Message.authenticated();
  }
}