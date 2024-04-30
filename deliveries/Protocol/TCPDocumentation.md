# Codex Naturalis TCP protocol documentation
Messages exchanged between clients and servers in TCP protocols are of three categories:
- [Actions](#Actions): messages sent by clients to the server to express a user intention to do an action
- [Responses](#Responses): messages sent from the server to the clients to update them about another user's move or to the consequence of their action
- [Errors](#Errors): messages sent form the server to the user in case a previous action was not possible
Every message is a JSON file with some properties.

## Actions
Actions always have the following parameters:

| Parameter |  Type  | Description |
| :-------- | :----: | :------------------------------------------ |
| action    | String |    String showing the action types          |
| username    | String | Nickname of the player performing the action |

Implemented actions:
- [GetAvailableMatches](#GetAvailableMatches)
- [CreateMatch](#CreateMatch)
- [JoinMatch](#JoinMatch)
- [SendText](#SendText)
- [DrawInitialCard](#DrawInitialCard)
- [ChooseInitialCardSide](#ChooseInitialCardSide)
- [DrawSecretObjectives](#DrawSecretObjectives)
- [ChooseSecretObjective](#ChooseSecretObjective)
- [PlayCard](#PlayCard)
- [DrawCard](#DrawCard)

### GetAvailableMatches
The action does not need additional parameters. 
- The client asks for an updated version of the lobby; 
- The server returns an [AvailableMatches](#AvailableMatches) response.

### CreateMatch
The action communicates (to the server) the intention of a client to create a new match.

| Parameter  |  Type   | Description                                  |
| :--------- | :-----: | :------------------------------------------- |
| `matchName`  | String  | Name of the match                            |
| `maxPlayers` | Integer | Number of maximum players (must be between 2 and 4) |

### JoinMatch
The action communicates the intention of a client to join a match.

| Parameter |  Type  | Description               |
| :-------- | :----: | :------------------------ |
| `matchName` | String | Name of the match to join |

### SendText
The action sends a text message in the chat.

| Parameter |        Type         | Description                                                                       |
| :-------- | :-----------------: | :-------------------------------------------------------------------------------- |
| `text`      |       String        | Content of the message                                                            |
| `recipient`  | String (*optional*) | Recipient's name of the private message. Otherwise, messages are public by default. |

### DrawInitialCard
The action does not need additional parameters.
- It communicates the intention of a player to draw the initial card. It can only happen before the first turn of the game.
- If the action is successful, a [SomeoneDrewInitialCard](#SomeoneDrewInitialCard) response is sent to every client.

### ChooseInitialCardSide
The action communicates the player's choice of the initial card's side. It can only happen before the first turn of the game.

| Parameter |  Type  |                           Description |
| :-------- | :----: | :------------------------------------ |
| `side`      | String | SSide chosen. Must be either `FRONT` or `BACK`ide chosen. Must be either `FRONT` or `BACK` |

If the action is successful, a [SomeoneSetInitialSide](#SomeoneSetInitialSide) response is sent to every client.

### DrawSecretObjectives
The action does not need additional parameters. 
- It communicates the intention of a player to draw the (2) secret objectives. It can only happen before the first turn of the game.

If the action is successful, a [SomeoneDrewInitialCard](#SomeoneDrewSecretObjectives) response is sent to every client.

### ChooseSecretObjective
The action communicates the intention of a player to choose his secret objective. It can only happen before the first turn of the game.

| Parameter   |  Type   |                Description |
| :---------- | :-----: | :------------------------- |
| `objectiveID` | Integer | ID of the chosen objective |

If the action is successful, a [SomeoneChoseSecretObjective](#SomeoneChoseSecretObjective) response is sent to every client.

### PlayCard
The action communicates the intention of the player to place a card on its board. It can only happen during the player's own turn.

| Parameter |  Type   | Description                                     |
| :-------- | :-----: | :---------------------------------------------- |
| `x`         | Integer | x coordinate where to play the card             |
| `y`         | Integer | y coordinate where to play the card             |
| `cardID`    | Integer | ID of the playableCard chosen                   |
| `side`      | String  | Side chosen. Must be either `FRONT` or `BACK` |

If the action is successful, a [SomeonePlayedCard](#SomeonePlayedCard) response is sent to every client.

### DrawCard
The action communicates the intention of a player to draw a card. It can only happen during the player's own turn.


| Parameter  |  Type  | Description                                                                                                                                                            |
| :--------- | :----: | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `drawSource` | String | Source from which drawing the card. It can be `FIRST_VISIBLE_CARD`, `SECOND_VISIBLE_CARD`, `THIRD_VISIBLE_CARD`, `FOURTH_VISIBLE_CARD`, `GOLDS_DECK`, `RESOURCES_DECK` |

If the action is successful, a [SomeoneDrewInitialCard](#SomeoneDrewInitialCard) response is sent to every client.

## Responses
Responses always have the following parameter:

| Parameter |  Type  | Description                                |
| :-------- | :----: | :----------------------------------------- |
| `response`  | String | String that shows the type of the response |

Response can either be:
- [AvailableMatches](#AvailableMatches)
- [PlayerJoined](#PlayerJoined)
- [PlayerQuit](#PlayerQuit)
- [SomeoneSentText](#SomeoneSentText)
- [MatchStarted](#MatchStarted)
- [SomeoneDrewInitialCard](#SomeoneDrewInitialCard)
- [SomeoneSetInitialSide](#SomeoneSetInitialSide)
- [SomeoneDrewSecretObjectives](#SomeoneDrewSecretObjectives)
- [SomeoneChoseSecretObjective](#SomeoneChoseSecretObjective)
- [SomeonePlayedCardMessage](#SomeonePlayedCardMessage)
- [SomeoneDrewCard](#SomeoneDrewCard)
- [MatchFinishedMessage](#MatchFinishedMessage)
### AvailableMatches
This response is sent when a user is connected to the server.

| Parameter |           Type           | Description                           |
| :-------- | :----------------------: | :------------------------------------ |
| `matches`   | Array of [Match](#Match) | List of all matches a player can join |

#### Match
Match is a Json object the following parameters:

| Parameter     |  Type   | Description                                       |
| :------------ | :-----: | :------------------------------------------------ |
| `name`          | String  | Name of the match. It is unique for each match       |
| `joinedPlayers` | Integer | Number of players that currently joined the match |
| `maxPlayers`    | Integer | Maximum amount of players that can join the match |

### PlayerJoined
This response is sent when a player joins the current match.

| Parameter     |  Type   | Description                                       |
| :------------ | :-----: | :------------------------------------------------ |
| `username`      | String  | Username of the player that just joined the match |
| `joinedPlayers` | Integer | Number of players currently in the match |
| `maxPlayers`    | Integer | Maximum amount of players the match can hold |

### PlayerQuit
This response is sent when a player quits the current match.

| Parameter     |  Type   | Description                                       |
| :------------ | :-----: | :------------------------------------------------ |
| `username`      | String  | Username of the player that just quit the match   |
| `joinedPlayers` | Integer | Number of players that currently joined the match |
| `endMatch`     | bool     | true if the quit caused the match to interrupt, false otherwise|

### SomeoneSentText
This response is sent when another player sends a message in the chat. If the message is set to be private, the response is only sent to the interested user.

| Parameter |  Type  | Description                                            |
| :-------- | :----: | :----------------------------------------------------- |
| `username`  | String | Username of the player that sent the message           |
| `text`      | String | Text of the message sent                               |
| `isPrivate`   |  bool  | If the message is private to the user that receives it |

### MatchStarted
Sent when the required amount of players is reached and the match is about to start.

| Parameter         |             Type              | Description                                                                                                                                                                                                       |
| :---------------- | :---------------------------: | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `visibleObjectives` |       Array of Integers       | IDs of the visible objectives |
| `visibleCards`      |       VisibleCards            | A JSON Object rapresenting the visible cards (see ref.) |
| `visibleDeckReigns` |       Array of Strings        | The array contains the reign of top-card of both the gold and the resource deck, in the first and second slot respectively |
| `playerHands`       |            Object             | The JSON Object contains the player's username (as a property) and the cards ids (an array of integer) |
| `playerPawnColors`  |            Object             | The JSON Object contains the player's username (as a property) and player's color (as a String, containing either `RED`, `BLUE`, `GREEN`, `YELLOW`. Note: the order turn always follows RED -> BLUE -> GREEN -> YELLOW) |

To further specify the idea behind the `VisibleCards` type:

| Parameter           |  Type   | Description                   |
| :------------------ | :-----: | :---------------------------- |
| `FIRST_VISIBLE_CARD`  | Integer | ID of the first visible card  |
| `SECOND_VISIBLE_CARD` | Integer | ID of the second visible card |
| `THIRD_VISIBLE_CARD`  | Integer | ID of the third visible card  |
| `FOURTH_VISIBLE_CARD` | Integer | ID of the fourth visible card |

#### Example
```json
{
  "visibleObjectives": [
    7,
    5
  ],
  "visibleCards": {
    "FOURTH_VISIBLE": 10,
    "SECOND_VISIBLE": 42,
    "FIRST_VISIBLE": 41,
    "THIRD_VISIBLE": 12
  },
  "visibleDeckReigns": [
    "FUNGUS",
    "FUNGUS"
  ],
  "playerHands": {
    "Oingo": [
      19,
      50,
      70
    ],
    "Jotaro": [
      4,
      77,
      51
    ],
    "Boingo": [
      1,
      66,
      68
    ]
  },
  "playerPawnColors": {
    "Oingo": "RED",
    "Jotaro": "GREEN",
    "Boingo": "BLUE"
  },
  "response": "MatchStarted"
}
```

### SomeoneDrewInitialCard
This response is sent to each user in the match when a user draws an initial card.

| Parameter     |  Type   | Description                                |
| :------------ | :-----: | :----------------------------------------- |
| `username`        | String  | Username of the player who performed the action |
| `initialCardID` | Integer | ID of the given initial card               |

### SomeoneSetInitialSide
This response is sent to each user in the match when a user chosees the initial side of a card.

| Parameter |  Type  | Description                                        |
| :-------- | :----: | :------------------------------------------------- |
| `username`    | String | Username of the player who performed the action         |
| `side`      | String | Side of the initial card. It can be either `FRONT` or `BACK` |

### SomeoneDrewSecretObjectives
This response is sent to each user in the match when a user draws the two secret objectives.

| Parameter |         Type         | Description                                                                                    |
| :-------- | :------------------: | :--------------------------------------------------------------------------------------------- |
| `username`    |        String        | Username of the player who performed the action                                                     |
| `firstID`   | Integer (*optional*) | ID of the first objective card drawn. Is `null` if the player it is sent to not the current player  |
| `secondID`  | Integer (*optional*) | ID of the second objective card drawn. Is `null` if the player it is sent to not the current player |

### SomeoneChoseSecretObjective
This response is sent to each user in the match when a user chooses his secret objective.

| Parameter   |         Type         | Description                                                                         |
| :---------- | :------------------: | :---------------------------------------------------------------------------------- |
| `username`      |        String        | Username of the player who performed the action                                          |
| `objectiveID` | Integer (*optional*) | ID of the chosen objective. Is `null` if the player it is sent to not the current player |

### SomeonePlayedCard
This response is sent to each user in the match when a user plays a card.

| Parameter |  Type   | Description                                                  |
| :-------- | :-----: | :----------------------------------------------------------- |
| `username`    | String  | Username of the player who performed the action                   |
| `x`         | Integer | x coordinate of the player card                              |
| `y`         | Integer | y coordinate of the played card                              |
| `cardID`    | Integer | ID of the played card                                        |
| `side`      | String  | Side in which the card has been played. It can be either `FRONT` or `BACK` |
| `points`    | Integer | Amount of points earned from the move                        |

### SomeoneDrewCard
This response is sent to each user in the match when a user plays a card.

| Parameter            |        Type        | Description                                                                                                                                                            |
| :------------------- | :----------------: | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `username`               |       String       | Username of the player who performed the action |
| `drawSource`           |       String       | Source from which the card is drawn. It can be `FIRST_VISIBLE_CARD`, `SECOND_VISIBLE_CARD`, `THIRD_VISIBLE_CARD`, `FOURTH_VISIBLE_CARD`, `GOLDS_DECK`, `RESOURCES_DECK` |
| `cardID`               |      Integer       | ID of the card drawn by the player |
| `raplacementCardID`    | Integer (Optional) | ID of the card that replaced the drawn card. Not available if the source is `GOLDS_DECK` or `RESOURCES_DECK` |
| `replacementCardReign` |       String       | Reign of the replaced card |

### MatchFinishedMessage
| Parameter |     Type      | Description                                |
| :-------- | :-----------: | :----------------------------------------- |
| `ranking`   | array of Rank | Ordered array containing the final ranking |

### Rank
Rank is a JSON object containing the results of a single player.

| Parameter |  Type   | Description                    |
| :-------- | :-----: | :----------------------------- |
| `username`  | String  | Username of the current player         |
| `points`    | Integer | Amount of the final points (after post-match objective counting)        |
| `winner`    |  bool   | If the current player is also the winner of the game |

#### Example
```JSON
{
  "ranking": [
    {
      "username": "Boingo",
      "points": 25,
      "winner": true
    },
    {
      "username": "Oingo",
      "points": 12,
      "winner": false
    }
  ],
  "response": "MatchFinished"
}
```

## Errors
An error always contains these parameters:

| Parameter |  Type  |                                        Description |
| :-------- | :----: | :------------------------------------------------- |
| `error`     | String |                                  Code of the error |
| `message`   | String | A message explaining the error in natural language |
