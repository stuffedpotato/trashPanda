package com.trashpanda.ShareList;
import com.trashpanda.Item;
import com.trashpanda.ItemQuantityType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
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
            System.out.println("Received ShareList POST request body: " + body);
            
            try {
                // Parse the JSON manually to handle date conversion
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                
                // Extract the username, item, and quantity
                String username = jsonObject.get("username").getAsString();
                JsonObject itemObj = jsonObject.getAsJsonObject("item");
                String itemName = itemObj.get("name").getAsString();
                String qtyTypeStr = itemObj.get("qtyType").getAsString();
                double quantity = jsonObject.get("qty").getAsDouble();
                
                // Parse the expirationDate if it exists
                Date expirationDate = null;
                if (jsonObject.has("expirationDate") && !jsonObject.get("expirationDate").isJsonNull()) {
                    String dateStr = jsonObject.get("expirationDate").getAsString();
                    if (dateStr != null && !dateStr.isEmpty()) {
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            java.util.Date parsedDate = format.parse(dateStr);
                            expirationDate = new Date(parsedDate.getTime());
                            System.out.println("Successfully parsed expiration date: " + expirationDate);
                        } catch (ParseException e) {
                            System.out.println("Error parsing date: " + e.getMessage());
                        }
                    }
                }
                
                // Log the parsed data
                System.out.println("Parsed username: " + username);
                System.out.println("Parsed item name: " + itemName);
                System.out.println("Parsed qty type: " + qtyTypeStr);
                System.out.println("Parsed quantity: " + quantity);
                System.out.println("Parsed expiration date: " + expirationDate);
                
                // Create the item object with the appropriate quantity type
                Item item = new Item(itemName, null, ItemQuantityType.valueOf(qtyTypeStr));
                
                // Insert into the database
                ShareListService shareListService = new ShareListService();
                boolean success = shareListService.insertShareListEntry(
                        username,
                        item,
                        quantity,
                        expirationDate
                );

                if (success) {
                    res.status(200);
                    return "{\"message\": \"ShareList entry inserted successfully.\"}";
                } else {
                    res.status(500);
                    return "{\"error\": \"Error inserting ShareList entry.\"}";
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"Error processing request: " + e.getMessage() + "\"}";
            }
        });

        put("/sharelist", (req, res) -> {
            String body = req.body();
            System.out.println("Received ShareList PUT request body: " + body);
            
            try {
                // Parse the JSON manually to handle date conversion
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                
                // Extract the username, item, and quantity
                String username = jsonObject.get("username").getAsString();
                JsonObject itemObj = jsonObject.getAsJsonObject("item");
                String itemName = itemObj.get("name").getAsString();
                String qtyTypeStr = itemObj.get("qtyType").getAsString();
                double quantity = jsonObject.get("qty").getAsDouble();
                
                // Parse the expirationDate if it exists
                Date expirationDate = null;
                if (jsonObject.has("expirationDate") && !jsonObject.get("expirationDate").isJsonNull()) {
                    String dateStr = jsonObject.get("expirationDate").getAsString();
                    if (dateStr != null && !dateStr.isEmpty()) {
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            java.util.Date parsedDate = format.parse(dateStr);
                            expirationDate = new Date(parsedDate.getTime());
                        } catch (ParseException e) {
                            System.out.println("Error parsing date: " + e.getMessage());
                        }
                    }
                }
                
                // Create the item object with the appropriate quantity type
                Item item = new Item(itemName, null, ItemQuantityType.valueOf(qtyTypeStr));
                
                // Update the database
                ShareListService shareListService = new ShareListService();
                boolean success = shareListService.updateShareListEntry(
                        username,
                        item,
                        quantity,
                        expirationDate
                );

                if (success) {
                    res.status(200);
                    return "{\"message\": \"ShareList entry updated successfully.\"}";
                } else {
                    res.status(500);
                    return "{\"error\": \"Error updating ShareList entry.\"}";
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"Error processing request: " + e.getMessage() + "\"}";
            }
        });

        delete("/sharelist", (req, res) -> {
            String body = req.body();
            Gson gson = new Gson();
            
            try {
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                String username = jsonObject.get("username").getAsString();
                JsonObject itemObj = jsonObject.getAsJsonObject("item");
                String itemName = itemObj.get("name").getAsString();
                String qtyTypeStr = itemObj.has("qtyType") ? itemObj.get("qtyType").getAsString() : "UNIT";
                
                Item item = new Item(itemName, null, ItemQuantityType.valueOf(qtyTypeStr));
                
                ShareListService shareListService = new ShareListService();
                boolean success = shareListService.removeShareListEntry(username, item);

                if (success) {
                    res.status(200);
                    return "{\"message\": \"ShareList entry removed successfully.\"}";
                } else {
                    res.status(500);
                    return "{\"error\": \"Error removing ShareList entry.\"}";
                }
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"Error processing request: " + e.getMessage() + "\"}";
            }
        });
    }
}