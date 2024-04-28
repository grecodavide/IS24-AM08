# Codex Naturalis TCP protocol documentation
Messages exchanged between clients and servers in TCP protocols are of three categories:
- [Actions](#Actions): messages sent by clients to the server to express a user intention to do an action
- [Responses](#Responses): messages sent from the server to the clients to update them about another user's move or to the consequence of their action
- [Errors](#Errors): messages sent form the server to the user in case a previous action was not possible
Every message is a JSON file with some properties.
## Actions
Actions always have these parameters:

| Parameter |  Type  | Description |
| :-------- | :----: | :------------------------------------------ |
| action    | String |    String that shows the type of the action |
| player    | String | Nickname of the player that does the action |

action can be:
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
This action does not have additional parameters. Requests an updated version of the lobby. The server returns an [AvailableMatches](#AvailableMatches) response.

### CreateMatch
This action communicates the intention of a player to create a match.

| Parameter  |  Type   | Description                                  |
| :--------- | :-----: | :------------------------------------------- |
| matchName  | String  | Name of the match                            |
| maxPlayers | integer | Number of maximum players, must be 2, 3 or 4 |

### JoinMatch
This action communicates the intention of a player to join a match.

| Parameter |  Type  | Description               |
| :-------- | :----: | :------------------------ |
| matchName | String | Name of the match to join |

### SendText
This action sends a text message in the chat.

| Parameter |        Type         | Description                                                                       |
| :-------- | :-----------------: | :-------------------------------------------------------------------------------- |
| text      |       String        | Content of the message                                                            |
| username  | String (*optional*) | Username of the player the private message is sent to. If ometted then is public. |

### DrawInitialCard
This action does not have additional parameters. Communicates the intention of the player to draw the initial card before the first turn of the game.

After this action, if it is successful, a [SomeoneDrewInitialCard](#SomeoneDrewInitialCard) response is sent to each user in the game.
### ChooseInitialCardSide
Communicates the intention of the player to choose the side of its initial card before the first turn of the game.

| Parameter |  Type  |                           Description |
| :-------- | :----: | ------------------------------------: |
| side      | String | Side chosen, can be "FRONT" or "BACK" |

After this action, if it is successful, a [SomeoneSetInitialSide](#SomeoneSetInitialSide) response is sent to each user in the game.
### DrawSecretObjectives
This action does not have additional parameters. Communicates the intention of the player to draw the two secret objectives before the first turn of the game.

After this action, if it is successful, a [SomeoneDrewInitialCard](#SomeoneDrewSecretObjectives) response is sent to each user in the game.
### ChooseSecretObjective
Communicates the intention of the player to choose his secret objective before the first turn of the game.

| Parameter   |  Type   |                Description |
| :---------- | :-----: | -------------------------: |
| objectiveID | Integer | ID of the objective chosen |

After this action, if it is successful, a [SomeoneChoseSecretObjective](#SomeoneChoseSecretObjective) response is sent to each user in the game.
### PlayCard
Communicates the intention of the player to place a card on its board when it is its turn.

| Parameter |  Type   | Description                                     |
| :-------- | :-----: | :---------------------------------------------- |
| x         | Integer | x coordinate where to play the card             |
| y         | Integer | y coordinate where to play the card             |
| cardID    | Integer | ID of the playableCard chosen                   |
| side      | String  | Side to play the card, can be "FRONT" of "BACK" |

After this action, if it is successful, a [SomeonePlayedCard](#SomeonePlayedCard) response is sent to each user in the game.
### DrawCard
Communicates the intention of the player to draw a card when it is its turn.

| Parameter  |  Type  | Description                                                                                                                                                            |
| :--------- | :----: | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| drawSource | String | Source from which the card is drawn. It can be "FIRST_VISIBLE_CARD", "SECOND_VISIBLE_CARD", THIRD_VISIBLE_CARD", "FOURTH_VISIBLE_CARD", "GOLDS_DECK", "RESOURCES_DECK" |

After this action, if it is successful, a [SomeonePlayedCard](#SomeonePlayedCard) response is sent to each user in the game.
## Responses
Responses always have this parameter:

| Parameter |  Type  | Description                                |
| :-------- | :----: | :----------------------------------------- |
| response  | String | String that shows the type of the response |

response can be:
- [MatchStarted](#MatchStarted)
- [SomeoneDrewInitialCard](#SomeoneDrewInitialCard)
### AvailableMatches
This response is sent when a user is connected to the server.

| Parameter |           Type           | Description                           |
| :-------- | :----------------------: | :------------------------------------ |
| matches   | Array of [Match](#Match) | List of all matches a player can join |

#### Match
Match is a Json object with these parameters:

| Parameter     |  Type   | Description                                       |
| :------------ | :-----: | :------------------------------------------------ |
| name          | String  | Name of the match, is unique for each match       |
| joinedPlayers | Integer | Number of players that currently joined the match |
| maxPlayers    | Integer | Maximum amount of players that can join the match |

### PlayerJoined
This response is sent when a player joined the current match.

| Parameter     |  Type   | Description                                       |
| :------------ | :-----: | :------------------------------------------------ |
| name          | String  | Name of the match the player has joined to        |
| username      | String  | Username of the player that just joined the match |
| joinedPlayers | Integer | Number of players that currently joined the match |
| maxPlayers    | Integer | Maximum amount of players that can join the match |

### PlayerQuit
This response is sent when a player quits the current match.

| Parameter     |  Type   | Description                                       |
| :------------ | :-----: | :------------------------------------------------ |
| username      | String  | Username of the player that just quit the match   |
| joinedPlayers | Integer | Number of players that currently joined the match |

### SomeoneSentText
This response is sent when another player sends a message in the chat. If the message is private, then this is sent only to the interested user.

| Parameter |  Type  | Description                                            |
| :-------- | :----: | :----------------------------------------------------- |
| username  | String | Username of the player that sent the message           |
| text      | String | Text of the message sent                               |
| private   |  bool  | If the message is private to the user that receives it |

### MatchStarted
Sent when the required amount of players is reached and the match is about to start.

| Parameter         |             Type              | Description                                                                                                                                                                                                       |
| :---------------- | :---------------------------: | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| visibleObjectives |       Array of Integers       | IDs of the visible objectives                                                                                                                                                                                     |
| visibleCards      | [VisibleCards](#VisibleCards) | A JSON Object rapresenting the visible cards                                                                                                                                                                      |
| visibleDeckReigns |       Array of Strings        | An array containing at the first position the reign of the card on top of the golds deck, at the second position the reign at the top of resources deck                                                           |
| playerHands       |            Object             | A JSON Object containing the player's username as property and an array of integer containing the cards ids.                                                                                                      |
| playerPawnColors  |            Object             | A JSON Object containing the player's username ad property and a String indicating player's color. It can be "RED", "BLUE", "GREEN", "YELLOW". Note: the order turn always follows RED -> BLUE -> GREEN -> YELLOW |

Following, the specification of the declared types: 
#### VisibleCards

| Parameter           |  Type   | Description                   |
| :------------------ | :-----: | :---------------------------- |
| FIRST_VISIBLE_CARD  | Integer | ID of the first visible card  |
| SECOND_VISIBLE_CARD | Integer | ID of the second visible card |
| THIRD_VISIBLE_CARD  | Integer | ID of the third visible card  |
| FOURTH_VISIBLE_CARD | Integer | ID of the fourth visible card |

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
| player        | String  | Username of the player that did the action |
| initialCardID | Integer | ID of the given initial card               |

### SomeoneSetInitialSide
This response is sent to each user in the match when a user chosees the initial side of a card.

| Parameter |  Type  | Description                                        |
| :-------- | :----: | :------------------------------------------------- |
| player    | String | Username of the player that did the action         |
| side      | String | Side of the initial card, can be "FRONT" or "BACK" |

### SomeoneDrewSecretObjectives
This response is sent to each user in the match when a user draws the two secret objectives.

| Parameter |         Type         | Description                                                                                    |
| :-------- | :------------------: | :--------------------------------------------------------------------------------------------- |
| player    |        String        | Username of the player that did the action                                                     |
| firstID   | Integer (*optional*) | ID of the first objective card drawn, null if the player it is sent to not the current player  |
| secondID  | Integer (*optional*) | ID of the second objective card drawn, null if the player it is sent to not the current player |

### SomeoneChoseSecretObjective
This response is sent to each user in the match when a user chooses his secret objective.

| Parameter   |         Type         | Description                                                                         |
| :---------- | :------------------: | :---------------------------------------------------------------------------------- |
| player      |        String        | Username of the player that did the action                                          |
| objectiveID | Integer (*optional*) | ID of the chosen objective, null if the player it is sent to not the current player |

### SomeonePlayedCardMessage
This response is sent to each user in the match when a user plays a card.

| Parameter |  Type   | Description                                                  |
| :-------- | :-----: | :----------------------------------------------------------- |
| player    | String  | Username of the player that did the action                   |
| x         | Integer | x coordinate of the player card                              |
| y         | Integer | y coordinate of the played card                              |
| cardID    | Integer | ID of the played card                                        |
| side      | String  | Side in which the card has been played. Can be FRONT or BACK |
| points    | Integer | Amount of points earned from the move                        |

### SomeoneDrewCard
| Parameter            |        Type        | Description                                                                                                                                                            |
| :------------------- | :----------------: | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| player               |       String       | Username of the player that did the action                                                                                                                             |
| drawSource           |       String       | Source from which the card is drawn. It can be "FIRST_VISIBLE_CARD", "SECOND_VISIBLE_CARD", THIRD_VISIBLE_CARD", "FOURTH_VISIBLE_CARD", "GOLDS_DECK", "RESOURCES_DECK" |
| cardID               |      Integer       | ID of the card drawn by the player                                                                                                                                     |
| raplacementCardID    | Integer (Optional) | ID of the card that replaced the drawn card, not available if the source is "GOLDS_DECK" or "RESOURCES_DECK"                                                           |
| replacementCardReign |       String       | Reign of the replaced card.                                                                                                                                            |

### MatchFinishedMessage
| Parameter |     Type      | Description                                |
| :-------- | :-----------: | :----------------------------------------- |
| ranking   | array of Rank | Ordered array containing the final ranking |

#### Rank
Rank is a JSON object containing the results of a single player.

| Parameter |  Type   | Description                    |
| :-------- | :-----: | :----------------------------- |
| username  | String  | Username of the player         |
| points    | Integer | Amount of points gained        |
| winner    |  bool   | If the player has won the game |

#### Example
```JSON
{
  "ranking": [
    {
      "username": "Boingo",
      "points": 12,
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
An error has these parameters:

| Parameter |  Type  |                                        Description |
| :-------- | :----: | :------------------------------------------------- |
| error     | String |                                  Code of the error |
| message   | String | A message explaining the error in natural language |
