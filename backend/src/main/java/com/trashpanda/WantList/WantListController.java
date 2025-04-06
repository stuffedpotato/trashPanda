package com.trashpanda.WantList;
import com.google.gson.Gson;

import java.util.List;

import static spark.Spark.*;

public class WantListController {
    public static void initializeRoutes() {

        get("/wantlist/:username", (req, res) -> {
            String username = req.params(":username");
            WantListService service = new WantListService();

            // Log the incoming request
            System.out.println("Received request for wantlist of username: " + username);

            try {
                List<WantListEntry> entries = service.getWantListEntries(username);
                if (entries == null || entries.isEmpty()) {
                    res.status(404); // Not found if no entries are found
                    return "{\"error\": \"No want list entries found for user\"}";
                }
                res.type("application/json");
                return new Gson().toJson(entries);
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"An error occurred while retrieving want list entries\"}";
            }
        });

        post("/wantlist", (req, res) -> {
            String body = req.body();
            System.out.println("Received WantList POST request body: " + body);
            
            Gson gson = new Gson();
            WantListEntry wantListEntry = gson.fromJson(body, WantListEntry.class);
            
            // Debug log to check received data
            System.out.println("Parsed username: " + wantListEntry.getUsername());
            System.out.println("Parsed item name: " + (wantListEntry.getItem() != null ? wantListEntry.getItem().getName() : "null"));
            System.out.println("Parsed quantity: " + wantListEntry.getQty());
            
            if (wantListEntry.getUsername() == null || wantListEntry.getUsername().isEmpty()) {
                res.status(400);
                return "Username cannot be null or empty";
            }

            WantListService wantListService = new WantListService();
            boolean success = wantListService.insertWantListEntry(
                    wantListEntry.getUsername(),
                    wantListEntry.getItem(),
                    wantListEntry.getQty()
            );

            if (success) {
                res.status(200);
                return "WantList entry inserted successfully.";
            } else {
                res.status(500);
                return "Error inserting WantList entry.";
            }
        });

        put("/wantlist", (req, res) -> {
            String body = req.body();
            Gson gson = new Gson();
            WantListEntry wantListEntry = gson.fromJson(body, WantListEntry.class);

            WantListService wantListService = new WantListService();
            boolean success = wantListService.updateWantListEntry(
                    wantListEntry.getUsername(),
                    wantListEntry.getItem(),
                    wantListEntry.getQty()
            );

            if (success) {
                res.status(200);
                return "WantList entry updated successfully.";
            } else {
                res.status(500);
                return "Error updating WantList entry.";
            }
        });

        delete("/wantlist", (req, res) -> {
            String body = req.body();
            Gson gson = new Gson();
            WantListEntry wantListEntry = gson.fromJson(body, WantListEntry.class);

            WantListService wantListService = new WantListService();
            boolean success = wantListService.removeWantListEntry(
                    wantListEntry.getUsername(),
                    wantListEntry.getItem()
            );

            if (success) {
                res.status(200);
                return "WantList entry removed successfully.";
            } else {
                res.status(500);
                return "Error removing WantList entry.";
            }
        });
    }
}