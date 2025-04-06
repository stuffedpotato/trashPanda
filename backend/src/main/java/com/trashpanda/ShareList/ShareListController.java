package com.trashpanda.ShareList;
import com.google.gson.Gson;

import java.util.List;

import static spark.Spark.*;

public class ShareListController {
    public static void initializeRoutes() {

        get("/sharelist/:username", (req, res) -> {
            String username = req.params(":username");
            ShareListService service = new ShareListService();

            // Log the incoming request
            System.out.println("Received request for username: " + username);

            try {
                List<ShareListEntry> entries = service.getShareListEntries(username);
                if (entries == null || entries.isEmpty()) {
                    res.status(404); // Not found if no entries are found
                    return "{\"error\": \"No share list entries found for user\"}";
                }
                res.type("application/json");
                return new Gson().toJson(entries);
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"An error occurred while retrieving share list entries\"}";
            }
        });

        post("/sharelist", (req, res) -> {
            String body = req.body();
            Gson gson = new Gson();
            ShareListEntry shareListEntry = gson.fromJson(body, ShareListEntry.class);

            ShareListService shareListService = new ShareListService();
            boolean success = shareListService.insertShareListEntry(
                    shareListEntry.getUsername(),
                    shareListEntry.getItem(),
                    shareListEntry.getQty(),
                    shareListEntry.getExpirationDate()
            );

            if (success) {
                res.status(200);
                return "ShareList entry inserted successfully.";
            } else {
                res.status(500);
                return "Error inserting ShareList entry.";
            }
        });

        put("/sharelist", (req, res) -> {
            String body = req.body();
            Gson gson = new Gson();
            ShareListEntry shareListEntry = gson.fromJson(body, ShareListEntry.class);

            ShareListService shareListService = new ShareListService();
            boolean success = shareListService.updateShareListEntry(
                    shareListEntry.getUsername(),
                    shareListEntry.getItem(),
                    shareListEntry.getQty(),
                    shareListEntry.getExpirationDate()
            );

            if (success) {
                res.status(200);
                return "ShareList entry updated successfully.";
            } else {
                res.status(500);
                return "Error updating ShareList entry.";
            }
        });

    delete("/sharelist", (req, res) -> {
        String body = req.body();
        Gson gson = new Gson();
        ShareListEntry shareListEntry = gson.fromJson(body, ShareListEntry.class);

        ShareListService shareListService = new ShareListService();
        boolean success = shareListService.removeShareListEntry(
                shareListEntry.getUsername(),
                shareListEntry.getItem()
        );

        if (success) {
            res.status(200);
            return "ShareList entry removed successfully.";
        } else {
            res.status(500);
            return "Error removing ShareList entry.";
        }
    });
}
}
