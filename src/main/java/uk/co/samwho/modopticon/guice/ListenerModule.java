package uk.co.samwho.modopticon.guice;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import uk.co.samwho.modopticon.listeners.LastMessageListener;
import uk.co.samwho.modopticon.listeners.SwearWordTracker;

public class ListenerModule extends AbstractModule {
    @Override
    public void configure() {
        Multibinder<ListenerAdapter> listeners = Multibinder.newSetBinder(binder(), ListenerAdapter.class);

        listeners.addBinding().to(LastMessageListener.class);
        listeners.addBinding().to(SwearWordTracker.class);
    }
}
