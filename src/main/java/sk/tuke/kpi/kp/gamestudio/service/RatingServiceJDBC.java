package sk.tuke.kpi.kp.gamestudio.service;

import sk.tuke.kpi.kp.gamestudio.entity.Rating;
import java.sql.*;

public class RatingServiceJDBC implements RatingService {
    private Rating rating;
    public static final String URL = "jdbc:postgresql://localhost/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "P10gle_Pwd";

    public static final String INSERT_RATING = "INSERT INTO rating (player, game, rating, ratedOn) VALUES (?, ?, ?, ?) ON CONFLICT (player, game) DO UPDATE SET rating = EXCLUDED.rating, rated_on = EXCLUDED.rated_on";
    public static final String SELECT_AVERAGE_RATING = "SELECT AVG(rating) FROM Rating";
    public static final String SELECT_PLAYER_RATING = "SELECT rating FROM rating WHERE game = ? AND player = ?";
    public static final String DELETE_RATING = "DELETE FROM rating";

    @Override
    public void setRating(Rating rating) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(INSERT_RATING)
        ) {
            statement.setString(1, rating.getPlayer());
            statement.setString(2, rating.getGame());
            statement.setInt(3, rating.getRating());
            statement.setTimestamp(4, new Timestamp(rating.getRatedOn().getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RatingException("Problem inserting rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_AVERAGE_RATING)
        ) {
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble(1);
                    return (int) Math.round(avg);
                } else {
                    throw new RatingException("No ratings found for game " + game);
                }
            }
        } catch (SQLException e) {
            throw new RatingException("Problem selecting average rating", e);
        }
    }

    @Override
    public int getRating(String player, String game) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_PLAYER_RATING)
        ) {
            statement.setString(1, player);
            statement.setString(2, game);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new RatingException("No rating found for player " + player + " in game " + game);
                }
            }
        } catch (SQLException e) {
            throw new RatingException("Problem selecting player rating", e);
        }
    }

    @Override
    public void reset() throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(DELETE_RATING);
        } catch (SQLException e) {
            throw new RatingException("Problem deleting ratings", e);
        }
    }
}