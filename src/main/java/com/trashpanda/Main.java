package com.trashpanda;
import com.trashpanda.ShareList.ShareListController;

import static spark.Spark.*;

public class Main {
        public static void main(String[] args) {
                port(4567);

                before((req, res) -> res.header("Access-Control-Allow-Origin", "*"));
                ShareListController.initializeRoutes();
        }
}
