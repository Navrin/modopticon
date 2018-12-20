package uk.co.samwho.modopticon;

public class Main {
    public static void main(String... args) throws Exception {
        Modopticon modopticon = DaggerModopticon.builder().build();

        modopticon.inits().forEach(Runnable::run);

        modopticon.jda().awaitReady();
        modopticon.backfiller().run();
        modopticon.apiServer().run();
    }
}
