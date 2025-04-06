package com.trashpanda;

import com.trashpanda.ShareList.ShareListEntry;
import com.trashpanda.ShareList.ShareListService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Initialize the database first
        Database.initializeDatabase();
        
        ShareListService service = new ShareListService();
        List<ShareListEntry> shareList = service.getShareListEntries("christine");

        if (shareList.isEmpty()) {
            System.out.println("No ingredients found for christine.");
        } else {
            String recipes = RecipeGenerator.generateRecipesFromShareList(shareList);
            System.out.println(recipes);
        }
    }
}