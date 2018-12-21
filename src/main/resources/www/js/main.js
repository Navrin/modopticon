function webSocketUrl() {
  var loc = window.location, new_uri;
  if (loc.protocol === "https:") {
    new_uri = "wss:";
  } else {
    new_uri = "ws:";
  }
  new_uri += "//" + loc.host;
  new_uri += loc.pathname + "api/v1/websocket";
  return new_uri;
}

function main() {
  let ws = new WebSocket(webSocketUrl());
  ws.onmessage = onSocketMessage;
  ws.onopen = function() {
    fetch("/api/v1/graphql?q=query{guilds{id,channels{id}}}")
      .then(res => {
        res.json().then(json => {
          json.guilds.forEach((guild, idx) => {
            guild.channels.forEach((channel, cidx) => {
              ws.send("subscribe " + channel.id);
            });
          });
        });
      });
  }
}

function onSocketMessage(message) {
  let json = JSON.parse(message.data);

  switch(json.type) {
    case "ENTITY_UPDATE":
      updateEntity(json.content);
      break;
    case "SUBSCRIBED":
      updateEntity(json.content);
      break;
  }
}

function updateEntity(entity) {
  var element = document.getElementById(entity.id);
  if (element === undefined || element === null) {
    element = document.createElement("pre")
    element.id = entity.id;
    document.body.appendChild(element);
  }
  element.innerHTML = JSON.stringify(entity, null, 2);
}

window.onload = main;