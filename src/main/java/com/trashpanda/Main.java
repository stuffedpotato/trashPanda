package com.trashpanda;

import java.util.Arrays;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        try {
            // 🔸 1. Create sample share list
            Item tomato = new Item("tomatoes", ItemCategory.VEGETABLE, ItemQuantityType.COUNT);
            Item rice = new Item("rice", ItemCategory.GRAIN, ItemQuantityType.CUP);

            ShareListEntry e1 = new ShareListEntry(tomato, 3, Optional.empty());
            ShareListEntry e2 = new ShareListEntry(rice, 1, Optional.empty());

            // 🔸 2. Call the fetcher
            String recipesJson = RecipeFetcher.getRecipesFromShareList(Arrays.asList(e1, e2));

            // 🔸 3. Print result
            System.out.println("Received recipes:");
            System.out.println(recipesJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
