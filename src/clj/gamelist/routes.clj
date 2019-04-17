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


(defn db-connect []
  (println "Testing connect to mongo db")
  (let [^MongoOptions options (mg/mongo-options {:threads-allowed-to-block-for-connection-multiplier 300})
        ^ServerAddress address(mg/server-address "localhost" 27017)
        conn                  (mg/connect address options)
        db                    (mg/get-db conn "test")]
    db))




;; Code taken from ring-json middleware impl.
(defn json-request? [request]
  (if-let [type (get-in request [:headers "content-type"])]
    (not (empty? (re-find #"^application/(.+\+)?json" type)))))

(defn log
  [msg]
  (spit "server.log" msg :append true))

(log "------- Start -------\n")

(defn json-response
  "add needed header and set body with json content (json middleware will
  take care of the json formatting (from clj datatype to json string))"
  [content]
  {:headers {"Content-Type" "application/json"}
   :body content})

;;-----------------------------------------------------------------------------
;; Handlers
;;-----------------------------------------------------------------------------

(defn add-game-handler
  [request]
  (log "Yo!")
  (Thread/sleep 1000) ; fake some processing time
  (let [db (db-connect)
        game (:body request)
        db-result (mc/insert-and-return (db-connect) "games" game)]
    (-> db-result
        json-response)))

;; (assoc :headers {"Content-Type" "application/json"})
;; (assoc :body db-result))))


;; Route handler for test button
(defn test-handler
  [request]
  (-> {:hep "test" }
      json-response))

;;-----------------------------------------------------------------------------
;; Define routing
;;-----------------------------------------------------------------------------

(defn home-routes [endpoint]
  (routes

   (POST "/addgame" request
     (-> request
         add-game-handler))

   (GET "/" _
     (-> "public/index.html"
         io/resource
         io/input-stream
         response
         (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))

           (GET "/test" request
             (-> request
                 test-handler))
           
           (resources "/")))

This will work as reload after modifying routes (server-side)
(defn restart-server
  []
  (user/stop)
  (user/go))

(restart-server)
