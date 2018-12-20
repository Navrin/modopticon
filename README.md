# Modopticon

**NOTE:** This is still very WIP, but the idea is to create a bot that can help with
day-to-day moderation tasks in large Discord servers.

## API

The core of what Modopticon exposes at the moment is two APIs: a REST API over HTTP, and
a WebSockets API.

### REST

Modopticon keeps tracks of four types of entity: Guilds, Users, Channels and Members. The
following relationships exist:

- A guild has many members.
- A guild has many channels.
- A user relates 1:1 with a member, sharing the same numeric ID.

So at the top level you have guilds and users, which you can see if you were
to request `http://localhost:8080/api/v1/rest/everything`.

Each entity has an "id" field on its JSON, which acts as a resource identifier
that can be used in the REST and WebSocket APIs, e.g. `/guilds/1/members/2` where
"1" is the guild ID in Discord and "2" is the member ID in Discord. "2" will also
be the user ID, so "/users/2" and "/guilds/1/members/2" will refer to the same
Discord account.

### WebSockets

To build a real-time dashboard that's useful for moderating, Modopticon
offers a WebSockets API.

```javascript
ws = new WebSocket("ws://localhost:8080/api/v1/websocket")
ws.onmessage = function(m) { console.log(m) }
ws.send("subscribe /guilds/1/members/2")
ws.send("unsubscribe /guilds/1/members/2")
ws.send("subscriptions")
```

The socket will then receive real-time updates on attributes that Modopticon
changes on that member entity. There are multiple types of message that can be
received by the WebSocket, and they're all JSON:

#### SUBSCRIBED

Contains a copy of the state of the entity subscribed to under the "content" key.

```json
{
  "type": "SUBSCRIBED",
  "content": {
    "id": "/guilds/1/channels/2",
    "attributes": {}
  }
}
```

#### UNSUBSCRIBED

Contains a copy of the state of the entity unsubscribed from under the "content" key.

```json
{
  "type": "UNSUBSCRIBED",
  "content": {
    "id": "/guilds/1/channels/2",
    "attributes": {}
  }
}
```

#### ENTITY_CHANGED

Contains a copy of the state of the entity that changed under the "content" key.

```json
{
  "type": "ENTITY_CHANGED",
  "content": {
    "id": "/guilds/1/channels/2",
    "attributes": {}
  }
}
```

#### ERROR

Contains an error message under the "content" key.

```json
{
  "type": "ERROR",
  "content": "you did a bad"
}
```

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

## Contributing

Help is appreciated! I'll check any PR put forward.

To get started in development, all you need to understand is that the
`Storage` object is the key to everything. It's shared globally and
stores metadata about Discord entities, which are then exposed over
an API.

The API can be found in `JSONServer`.

The various things extending `ListenerAdapter` in the `listeners` package
are what modify the `Storage` object.