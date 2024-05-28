// package it.polimi.ingsw.network.tcp;

// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertTrue;

// import java.io.BufferedReader;
// import java.io.BufferedWriter;
// import java.io.InputStreamReader;
// import java.io.OutputStreamWriter;
// import java.net.Socket;
// import java.util.List;

// import org.junit.Test;

// import it.polimi.ingsw.gamemodel.Match;
// import it.polimi.ingsw.gamemodel.Player;
// import it.polimi.ingsw.network.messages.actions.ActionMessage;
// import it.polimi.ingsw.network.messages.actions.CreateMatchMessage;
// import it.polimi.ingsw.network.messages.actions.JoinMatchMessage;
// import it.polimi.ingsw.network.messages.responses.ResponseMessage;
// import it.polimi.ingsw.network.messages.responses.SomeoneJoinedMessage;
// import it.polimi.ingsw.server.Server;
// import it.polimi.ingsw.utils.MessageJsonParser;

// /**
//  * ClientReceiverTest
//  */

// public class ClientReceiverTest {
//     PlayerStub player1, player2, player3;
//     MessageJsonParser parser;
//     Match match;
//     Server server;

//     public ClientReceiverTest() {
//         try {
//             this.server = new Server(2222, 9999);
//             server.startTCPServer();

//             this.player1 = new PlayerStub("AAAAA");
//             this.player2 = new PlayerStub("BBBBB");
//             this.player3 = new PlayerStub("AAAAA");
//             this.parser = new MessageJsonParser();

//         } catch (Exception e) {
//             assertTrue(false);
//         }
//     }


//     // @Test
//     // public void testGame() {
//     //     try {
//     //         ResponseMessage expected;
//     //         ActionMessage action;
//     //         String matchName = "TestGameMatch";
//     //         Map<String, PlayerStub> players = Map.of(player1.username, player1, player2.username, player2);
//     //         PlayerStub curr;

//     //         action = new CreateMatchMessage(player1.username, matchName, 2);
//     //         player1.sendMessage(action);
//     //         expected = new SomeoneJoinedMessage(player1.username, List.of(new Player(player1.username, null)) , 2);
//     //         assertEquals(parser.toJson(expected), player1.readMessage());
//     //         this.match = this.server.getMatch(matchName);

//     //         action = new JoinMatchMessage(player2.username, matchName);
//     //         player2.sendMessage(action);
//     //         expected = new SomeoneJoinedMessage(player2.username, List.of(new Player(player1.username, null), new Player(player2.username, null)) , 2);
//     //         assertEquals("Wrong SomeoneJoined from second player to first player", parser.toJson(expected), player1.readMessage());
//     //         assertEquals("", parser.toJson(expected), player2.readMessage());

//     //         // while (this.match.getVisibleObjectives() == null) { }
//     //         // expected = new MatchStartedMessage(this.match.getVisibleObjectives(), this.match.getVisiblePlayableCards(), this.match.getDecksTopReigns(), this.match.getPlayers());
//     //         // assertEquals(this.parser.toJson(expected), player1.readMessage());
//     //         
//     //         player1.readMessage();
//     //         player2.readMessage();

//     //         while (this.match.getCurrentPlayer() == null) { }
//     //         curr = players.get(this.match.getCurrentPlayer().getUsername());

//     //         action = new DrawInitialCardMessage(curr.username);
//     //         curr.sendMessage(action);

//     //         System.out.println(player1.readMessage());
//     //         System.out.println(player2.readMessage());
//     //     } catch (Exception e) {
//     //         e.printStackTrace();
//     //         assertTrue(false);
//     //     }
//     // }

//     @Test
//     public void testDuplicate() {
//         try {
//             ResponseMessage expected;
//             ActionMessage action;
//             String matchName = "TestGameMatch2";

//             action = new CreateMatchMessage(player1.username, matchName, 2);
//             player1.sendMessage(action);
//             expected = new SomeoneJoinedMessage(player1.username, List.of(new Player(player1.username, null)) , 2);
//             assertEquals(parser.toJson(expected), player1.readMessage());
//             this.match = this.server.getMatch(matchName);

//             action = new JoinMatchMessage(player3.username, matchName);
//             player3.sendMessage(action);
//             System.out.println(player3.readMessage());
//         } catch (Exception e) {
//             e.printStackTrace();
//             assertTrue(false);
//         }
//     }
// }


// class PlayerStub {
//     public Socket socket;
//     public BufferedReader input;
//     public BufferedWriter output;
//     public String username;
//     private MessageJsonParser parser;

//     public PlayerStub(String username) {
//         try {
//             this.socket = new Socket("localhost", 9999);
//             this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
//             this.output = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
//             this.username = username;
//             this.parser = new MessageJsonParser();
//         } catch (Exception e) {
//             assertTrue(false);
//         }
//     }

//     public void sendMessage(ActionMessage message) {
//         try {
//             this.output.write(this.parser.toJson(message));
//             this.output.newLine();
//             this.output.flush();
//         } catch (Exception e) {
//             assertTrue(false);
//         }
//     }

//     public String readMessage() {
//         try {
//             return this.input.readLine();
//         } catch (Exception e) {
//             assertTrue(false);
//         }

//         return null;
//     }

// }
