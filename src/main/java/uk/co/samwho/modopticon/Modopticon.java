package uk.co.samwho.modopticon;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Component;
import net.dv8tion.jda.core.JDA;
import uk.co.samwho.modopticon.annotations.Init;
import uk.co.samwho.modopticon.api.v1.Server;
import uk.co.samwho.modopticon.modules.ClockModule;
import uk.co.samwho.modopticon.modules.ConfigModule;
import uk.co.samwho.modopticon.modules.GsonModule;
import uk.co.samwho.modopticon.modules.JDAModule;
import uk.co.samwho.modopticon.modules.WebSocketHandlersModule;
import uk.co.samwho.modopticon.modules.EntityModifierModule;

@Singleton
@Component(modules = {
  ClockModule.class,
  ConfigModule.class,
  GsonModule.class,
  JDAModule.class,
  WebSocketHandlersModule.class,
  EntityModifierModule.class
})
interface Modopticon {
  Backfiller backfiller();
  Server apiServer();
  JDA jda();
  @Init Set<Runnable> inits();
}