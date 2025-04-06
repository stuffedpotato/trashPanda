package com.trashpanda.ShareList;
import com.google.gson.Gson;
import static spark.Spark.*;

public class ShareListController {
    public static void initializeRoutes() {
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
