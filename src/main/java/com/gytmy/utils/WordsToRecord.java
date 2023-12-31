package com.gytmy.utils;

import java.util.ArrayList;
import java.util.List;

public enum WordsToRecord {

    UP,
    DOWN,
    LEFT,
    RIGHT,
    OTHER;

    public static List<String> getWordsToRecord() {
        List<String> values = new ArrayList<>();

        for (WordsToRecord word : WordsToRecord.values()) {
            values.add(word.name());
        }

        return values;
    }

    public static boolean exists(String word) {
        return getWordsToRecord().contains(word);
    }
}