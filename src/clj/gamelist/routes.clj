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

;; (mc/insert-and-return (test-connect) "games" { :name "Kikana" })
;; (test-connect)

(dissoc {:id 0, :name "Kepler"} :id)
(dissoc {"id" 0, "name" "Kepler"} "id")
(json/read-str "{\"a\":1,\"b\":2}" :key-fn keyword)
(json/read-str (str {"id" 16, "name" "Abatorrr21"}) :key-fn keyword)

;; This will work as reload after modifying routes (server-side)
(defn restart-server
  []
  (user/stop)
  (user/go))

;; (restart-server)

;; Code taken from ring-json middleware impl.
(defn json-request? [request]
  (if-let [type (get-in request [:headers "content-type"])]
    (not (empty? (re-find #"^application/(.+\+)?json" type)))))

(defn log
  [msg]
  (spit "server.log" msg :append true))

(log "------- Start -------\n")

(defn add-game-handler
[request]
(Thread/sleep 1000) ; fake some processing time
(let [db (test-connect)
      game (json/read-str (str (:body request)) :key-fn keyword)
      db-result (mc/insert-and-return (test-connect) "games" game)
      obj-id (:_id db-result)
      logres (log (str game ", Got ID: " obj-id "\n"))]
  response))
;; (assoc :headers {"Content-Type" "application/json"})
;; (assoc :body db-result))))

;; (str "Insert: " ", ID: " obj-id)
(defn wrap-handler
  [request]
  {:json "test"})

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

  (GET "/wrap" request
    (wrap-json-body (wrap-handler request))
    (resources "/")))
