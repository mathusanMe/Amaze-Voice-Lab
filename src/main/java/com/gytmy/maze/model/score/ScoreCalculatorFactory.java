package com.gytmy.maze.model.score;

public class ScoreCalculatorFactory {

    private ScoreCalculatorFactory() {
    }

    /**
     * Get the score calculator for the given score type and score info.
     * 
     * @param type the score type
     * @param info the score info
     * @return the score calculator
     */
    public static ScoreCalculator getScoreCalculator(ScoreType type, ScoreInfo info) {
        switch (type) {
            case SIMPLE_KEYBOARD:
                if (!(info instanceof SimpleScoreInfo)) {
                    throw new IllegalArgumentException("Info must be of type SimpleKeyboardScoreInfo");
                }
                return new SimpleKeyboardScoreCalculator((SimpleScoreInfo) info);
            case SIMPLE_VOICE:
                if (!(info instanceof SimpleScoreInfo)) {
                    throw new IllegalArgumentException("Info must be of type SimpleVoiceScoreInfo");
                }
                return new SimpleVoiceScoreCalculator((SimpleScoreInfo) info);
            default:
                throw new IllegalArgumentException("Unknown score type");
        }
    }

    /**
     * Get the score calculator for the given score info.
     * 
     * @param info the score info
     * @return the score calculator
     */
    public static ScoreCalculator getScoreCalculator(ScoreInfo info) {
        if (info instanceof SimpleScoreInfo) {
            return getScoreCalculator(ScoreType.SIMPLE_VOICE, info);
        }
        throw new IllegalArgumentException("Unknown score info type");
    }

}
