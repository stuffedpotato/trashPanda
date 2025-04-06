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
                    // Changed matching logic to be more flexible - only one directional match is needed
                    if (hasAnyMatch(requester, other)) {
                        matches.add(new MatchResult(other, distance));
                    }
                }
            }
        }

        matches.sort(Comparator.comparingDouble(MatchResult::getDistance));

        return matches;
    }

    private static boolean hasAnyMatch(User userA, User userB) {
        // Check if userA wants anything that userB shares
        boolean userAWantsWhatBHas = userA.getWantList().stream().anyMatch(want -> userB.getShareList().stream()
                .anyMatch(share -> namesRoughlyMatch(want.getItem().getName(), share.getItem().getName())));

        // Check if userB wants anything that userA shares
        boolean userBWantsWhatAHas = userB.getWantList().stream().anyMatch(want -> userA.getShareList().stream()
                .anyMatch(share -> namesRoughlyMatch(want.getItem().getName(), share.getItem().getName())));

        // Match if either condition is true (not requiring both anymore)
        return userAWantsWhatBHas || userBWantsWhatAHas;
    }

    private static boolean namesRoughlyMatch(String nameA, String nameB) {
        // Improved matching logic
        String normalizedA = nameA.trim().toLowerCase();
        String normalizedB = nameB.trim().toLowerCase();

        // Exact match check
        if (normalizedA.equals(normalizedB)) return true;
        
        // Basic pattern: match plural endings and handle common variations
        // Remove trailing 's' or 'es' for comparison
        String baseA = normalizedA.replaceAll("(es|s)$", "");
        String baseB = normalizedB.replaceAll("(es|s)$", "");
        
        // Check if one is a substring of the other after normalization
        if (baseA.contains(baseB) || baseB.contains(baseA)) return true;
        
        // Check for similarity with pattern matching
        String patternA = ".*" + baseA + ".*";
        String patternB = ".*" + baseB + ".*";

        return Pattern.matches(patternA, normalizedB) || Pattern.matches(patternB, normalizedA);
    }
}