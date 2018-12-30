package uk.co.samwho.modopticon.api.v1.websocket.handlers;

import java.util.List;

import uk.co.samwho.modopticon.api.v1.websocket.EntityObserver;
import uk.co.samwho.modopticon.api.v1.websocket.Message;

@FunctionalInterface
public interface MessageHandler {
  Message handle(EntityObserver session, List<String> args) throws Exception;
}