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
      .then(res => res.json())
      .then(json => {
        json.guilds.forEach((guild, idx) => {
          guild.channels.forEach((channel, cidx) => {
            ws.send("subscribe " + channel.id);
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

var numChannels = 0;
var currentRow = null;

function updateEntity(entity) {
  var col = document.getElementById(entity.id);
  if (col === undefined || col === null) {
    if (numChannels % 3 == 0 || currentRow == null) {
      currentRow = document.createElement("div")
      currentRow.className = "row";
      document.getElementById("main").appendChild(currentRow);
    }
    numChannels += 1;

    col = document.createElement("div")
    col.className = "col s4";
    col.id = entity.id;
    currentRow.appendChild(col);
  }

  var cardColor = "grey lighten-5";
  var textColor = "grey-text darken-4";
  if (entity.attributes.lastMessageReceivedAt !== undefined) {
    let date = Date.parse(entity.attributes.lastMessageReceivedAt);
    let secondsAgo = (new Date().getTime() - date) / 1000;

    if (secondsAgo < 60) {
      cardColor = "red darken-1";
      textColor = "white-text";
    } else if (secondsAgo < 60 * 5) {
      cardColor = "orange darken-2";
      textColor = "white-text";
    } else if (secondsAgo < 60 * 30) {
      cardColor = "orange lighten-4";
    }
  }

  card = document.createElement("div")
  card.className = "card " + cardColor;

  content = document.createElement("div");
  content.className = "card-content " + textColor;
  content.id = entity.id;

  title = document.createElement("span");
  title.className = "card-title";
  title.innerHTML = "#" + entity.attributes.name;

  body = document.createElement("p");
  body.innerHTML += entity.attributes.lastMessageReceivedAt;

  col.children = [];
  col.appendChild(card);
  card.appendChild(content);
  content.appendChild(title);
  content.appendChild(body);
}

window.onload = main;