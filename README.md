# Modopticon

**NOTE:** This is still very WIP, but the idea is to create a bot that can help with
day-to-day moderation tasks in large Discord servers.

## Build and run

You'll need to supply a bot token before it will work. The token is expected to be found
in a file in `src/main/resources/token.txt`. Once you've got a token and put it in the
right place, you can run the project like so:

```
$ mvn clean compile install
$ java -jar target/modopticon-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Creating a DigitalOcean droplet to run on

```
$ docker-machine create --digitalocean-size "s-1vcpu-1gb" --driver digitalocean --digitalocean-access-token API_KEY modopticon
$ eval $(docker-machine env modopticon)
$ docker-compose up -d
```

Very likely that the SSL stuff will break if you try and run verbatim, as the domain is
owned by Sam and pointed at a specific machine. If you want to run this yourself you'll
need to do a little tweaking.