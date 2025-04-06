package com.trashpanda;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class Matchmaker {
    public static List<MatchResult> findSortedMatches(User requester, List<User> allUsers) {
        List<MatchResult> matches = new ArrayList<>();

        for (User other : allUsers) {
            if (!other.getUserName().equals(requester.getUserName())) {
                // gets distance between users from the Location class
                double distance = requester.getLocation().distanceTo(other.getLocation());

                // only check distance for one user since they can be the one who does the
                // walking/driving.
                if (distance <= requester.getRadius()) {
                    if (hasMutualMatch(requester, other)) {
                        matches.add(new MatchResult(other, distance));
                    }
                }
            }
        }

        matches.sort(Comparator.comparingDouble(MatchResult::getDistance));

        return matches;
    }

    private static boolean hasMutualMatch(User userA, User userB) {
        boolean userAWantsWhatBHas = userA.getWantList().stream().anyMatch(want -> userB.getShareList().stream()
                .anyMatch(share -> namesRoughlyMatch(want.getItem().getName(), share.getItem().getName())));

        boolean userBWantsWhatAHas = userB.getWantList().stream().anyMatch(want -> userA.getShareList().stream()
                .anyMatch(share -> namesRoughlyMatch(want.getItem().getName(), share.getItem().getName())));

        return userAWantsWhatBHas && userBWantsWhatAHas;
    }

    private static boolean namesRoughlyMatch(String nameA, String nameB) {
        String normalizedA = nameA.trim().toLowerCase();
        String normalizedB = nameB.trim().toLowerCase();

        // Basic pattern: match plural endings
        String patternA = normalizedA.replaceAll("(es|s)?$", ".*");
        String patternB = normalizedB.replaceAll("(es|s)?$", ".*");

        return Pattern.matches(patternA, normalizedB) || Pattern.matches(patternB, normalizedA);
    }
}