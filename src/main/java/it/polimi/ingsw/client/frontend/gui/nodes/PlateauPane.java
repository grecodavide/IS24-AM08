package it.polimi.ingsw.client.frontend.gui.nodes;

import it.polimi.ingsw.gamemodel.Color;
import it.polimi.ingsw.utils.GuiUtil;
import it.polimi.ingsw.utils.Pair;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

import java.util.HashMap;

public class PlateauPane extends Pane {
    public static double pawnSize = 64;
    public static double positionOffset = -6;
    HashMap<Integer, Pair<Double, Double>> positions;
    HashMap<String, ImageView> players;
    HashMap<String, Integer> points;

    public PlateauPane() {
        players = new HashMap<>();
        positions = new HashMap<>();
        points = new HashMap<>();
        positions.put(0, new Pair<>(104.0, 743.0));
        positions.put(1, new Pair<>(197.0, 741.0));
        positions.put(2, new Pair<>(291.0, 742.0));
        positions.put(3, new Pair<>(342.0, 656.0));
        positions.put(4, new Pair<>(246.0, 654.0));
        positions.put(5, new Pair<>(154.0, 652.0));
        positions.put(6, new Pair<>(56.0, 656.0));
        positions.put(7, new Pair<>(57.0, 571.0));
        positions.put(8, new Pair<>(152.0, 570.0));
        positions.put(9, new Pair<>(247.0, 570.0));
        positions.put(10, new Pair<>(338.0, 569.0));
        positions.put(11, new Pair<>(340.0, 482.0));
        positions.put(12, new Pair<>(243.0, 482.0));
        positions.put(13, new Pair<>(150.0, 483.0));
        positions.put(14, new Pair<>(55.0, 486.0));
        positions.put(15, new Pair<>(56.0, 397.0));
        positions.put(16, new Pair<>(151.0, 397.0));
        positions.put(17, new Pair<>(244.0, 395.0));
        positions.put(18, new Pair<>(340.0, 398.0));
        positions.put(19, new Pair<>(340.0, 313.0));
        positions.put(20, new Pair<>(200.0, 270.0));
        positions.put(21, new Pair<>(60.0, 315.0));
        positions.put(22, new Pair<>(59.0, 227.0));
        positions.put(23, new Pair<>(60.0, 140.0));
        positions.put(24, new Pair<>(114.0, 71.0));
        positions.put(25, new Pair<>(198.0, 54.0));
        positions.put(26, new Pair<>(287.0, 70.0));
        positions.put(27, new Pair<>(342.0, 141.0));
        positions.put(28, new Pair<>(342.0, 225.0));
        positions.put(29, new Pair<>(199.0, 158.0));
        // TODO Remove testing method
        this.setOnMousePressed((e) -> {
            for (String p : this.points.keySet()) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    this.setPoints(p, this.points.get(p) + 1);
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    this.setPoints(p, this.points.get(p) - 1);
                }
                return;
            }
        });
    }

    /**
     * Set the color of a player
     *
     * @param player username of the player
     * @param color  color of the player's pawn
     */
    public void setColor(String player, Color color) {
        ImageView img = new ImageView(new Image(GuiUtil.getPawnImagePath(color)));
        img.setFitWidth(pawnSize);
        img.setFitHeight(pawnSize);
        players.put(player, img);
        setPoints(player, 0);
        this.getChildren().add(img);
    }

    /**
     * Set the amount of points of a player and move its pawn
     *
     * @param player username of the player
     * @param points current number of points he has
     */
    public void setPoints(String player, int points) {
        if (points > 29) {
            return;
        }
        Pair<Double, Double> position = convertCoords(positions.get(points));
        // Calculate offset because of players in the same position
        int offset = this.playersAtPosition(points);
        this.points.put(player, points);

        ImageView playerPawn = players.get(player);
        playerPawn.setLayoutX(position.first());
        playerPawn.setLayoutY(position.second() + offset * positionOffset);
    }

    /**
     * Get the amount of players in a certain point
     *
     * @param points number of points
     * @return number of players in that position
     */
    private int playersAtPosition(int points) {
        int p = 0;
        for (String player : this.points.keySet()) {
            if (points == this.points.get(player)) {
                p++;
            }
        }
        return p;
    }

    /**
     * Convert relative coordinates of the pane to coordinates
     * corrected to the pawn size
     *
     * @param coord coordinate to convert
     * @return the converted coordinates
     */
    private Pair<Double, Double> convertCoords(Pair<Double, Double> coord) {
        return new Pair<>(coord.first() - pawnSize / 2 + 2, coord.second() - pawnSize / 2);
    }

}
