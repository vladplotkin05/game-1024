package sk.tuke.kpi.kp.gamestudio.service;

import sk.tuke.kpi.kp.gamestudio.entity.Rating;

public interface RatingService {
    void setRating(Rating rating) throws RatingException;
    int getAverageRating(String game) throws RatingException;
    int getRating(String player, String game) throws RatingException;
    void reset() throws RatingException;
}
