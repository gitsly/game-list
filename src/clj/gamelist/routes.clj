(ns gamelist.routes
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [compojure.core :refer [ANY GET PUT POST DELETE routes]]
            [compojure.handler :as handler]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [response]]
            [monger.core :as mg]
            [monger.collection :as mc])
  (:import [com.mongodb MongoOptions ServerAddress]))

(defn test-connect []
  (println "Testing connect to mongo db")
  (let [^MongoOptions options (mg/mongo-options {:threads-allowed-to-block-for-connection-multiplier 300})
        ^ServerAddress address(mg/server-address "localhost" 27017)
        conn                  (mg/connect address options)
        db                    (mg/get-db conn "test")]
    db))

;; (mc/insert-and-return (test-connect) "documents" {:name "John" :age 30})
;; (test-connect)


;; This will work as reload after modifying routes (server-side)
;; (do
;;   (user/stop)
;;   (user/go))

;; Code taken from ring-json middleware impl.
(defn json-request? [request]
  (if-let [type (get-in request [:headers "content-type"])]
    (not (empty? (re-find #"^application/(.+\+)?json" type)))))

(defn add-game-handler
  [request]
  (let [db (test-connect)
        game (:body request)]
    (mc/insert (test-connect) "games" game)
    (str "JSON request: " game)))

(defn home-routes [endpoint]
  (routes

   (POST "/addgame" request
     (add-game-handler request))

   (GET "/" _
     (-> "public/index.html"
         io/resource
         io/input-stream
         response
         (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))

   (GET "/test/:var1" [var1]
     (str "<html> <body> <p>var1: " var1 "</p> </body> </html>"))

   (resources "/")))
