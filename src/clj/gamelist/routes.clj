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
  (spit "server.log" (str msg "\n") :append true))

(log "------- Start -------\n")

(defn json-response
  "add needed header and set body with json content (json middleware will
  take care of the json formatting (from clj datatype to json string))"
  [content]
  {:headers {"Content-Type" "application/json"}
   :body content})

;;(-> (mc/find (db-connect) "games" {:name "Upper2"})
;;    seq)

;;-----------------------------------------------------------------------------
;; Handlers
;;-----------------------------------------------------------------------------

(defn games-handler
  "Get games collection"
  [request]
  (let [db (db-connect)
        collection "games"
        games (mc/find (db-connect) collection)]
    (-> games
        seq
        json-response)))
;; Cannot JSON encode object of class: class org.bson.types.ObjectId
;; https://stackoverflow.com/questions/37860825/how-to-pass-mongodb-objectid-in-http-request-json-body

(defn add-game-handler
  [request]
  (Thread/sleep 1000) ; fake some processing time
  (let [db (db-connect)
        game (:body request)
        db-result (mc/insert-and-return (db-connect) "games" game)]
    (-> db-result
        json-response)))

;; Route handler for test button
(defn test-handler
  [request]
  (-> {:hep "test" }
      json-response))

(defn remove-game-handler
  [request]
  ;; (log (str "remove: " request))
  (Thread/sleep 1000) ; fake some processing time
  (let [db (db-connect)
        game (:body request)
        oid (:_id game)
        db-result (mc/remove-by-id (db-connect) "games" oid)]
    (log (str "OID: " oid ", Db: " db-result))
    (-> db-result
        json-response)))


;; Sample 'full' game entry (for testing etc)
(def test-game {:_id "5cb807a48749801ddbd35cbd", :name "Karlsa", :test "12",
                :rating [{:user "Martin", :value "4", :date "2019-12-20"}
                         {:user "Anna", :value "7", :date "2019-12-21"}]})

;; (mc/insert-and-return (db-connect) "games" (dissoc test-game :_id))


;;------------------------------------------------------------------------------

(def test-data [{:_id 1231 :name "heppas" :age 12}
                {:_id 8981 :name "ninjan" :age 23}])

(map #(assoc % :selected true) test-data)

(let [selected 1231]
  (map #(assoc % :selected
               (= (:_id %) selected)) test-data))

;;-----------------------------------------------------------------------------
;; Define routing
;;-----------------------------------------------------------------------------

(defn home-routes [endpoint]
  (routes

   (GET "/" _
     (-> "public/index.html"
         io/resource
         io/input-stream
         response
         (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))

   (PUT "/addgame" request
     (-> request
         add-game-handler))

   (PUT "/removegame" request
     (-> request
         remove-game-handler))

   (GET "/games" request
     (-> request
         games-handler))


   (GET "/test" request
     (-> request
         test-handler))

   (resources "/")))


;; (defn restart-server
;;   []
;;   (user/stop)
;;   (user/go))

;; (restart-server)
