(ns gamelist.routes
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [compojure.core :refer [ANY GET PUT POST DELETE routes]]
            [compojure.handler :as handler]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [response status]]
            [gamelist.utils :refer [log]]
            [gamelist.db :as db]
            [monger.collection :as mc]
            [buddy.auth :refer [authenticated? throw-unauthorized]]))


;; Code taken from ring-json middleware impl.
(defn json-request? [request]
  (if-let [type (get-in request [:headers "content-type"])]
    (not (empty? (re-find #"^application/(.+\+)?json" type)))))

(log "------- Start -------")

(defn json-response
  "add needed header and set body with json content (json middleware will
                                                          take care of the json formatting (from clj datatype to json string))"
  [content]
  {:headers {"Content-Type" "application/json"}
   :body content})

;; Sample 'full' game entry (for testing etc)
(def test-game {:_id "5cb807a48749801ddbd35cbd", :name "Karlsa", :test "12",
                :rating [{:user "Martin", :value "4", :date "2019-12-20"}
                         {:user "Anna", :value "7", :date "2019-12-21"}]})
;; And it's insertion
;; (mc/insert-and-return (db/connect) "games" (dissoc test-game :_id))

;;-----------------------------------------------------------------------------
;; Handlers
;;-----------------------------------------------------------------------------

(defn games-handler
  "Get games collection"
  [request]
  (let [db (db/connect)
        collection "games"
        games (mc/find (db/connect) collection)]
    (-> games
        seq
        json-response)))
;; Cannot JSON encode object of class: class org.bson.types.ObjectId
;; https://stackoverflow.com/questions/37860825/how-to-pass-mongodb-objectid-in-http-request-json-body

(defn add-game-handler
  [request]
  ;; (Thread/sleep 1000) ; fake some processing time
  (let [db (db/connect)
        game (:body request)
        db-result (mc/insert-and-return (db/connect) "games" game)]
    (-> db-result
        json-response)))

;; Route handler for test button
(defn test-handler
  [request]
  (-> {:hep "test" }
      json-response))

(defn remove-game-handler
  [request]
  (let [db (db/connect)
        game (:body request)
        oid (-> game :_id (ObjectId.))
        db-result (mc/remove-by-id (db/connect) "games" oid)]
    (log (str "Remove game with OID: " oid ", Db: " db-result)))
  "done")

(defn buddy-handler
  [request]
  (let [ident (:identity request)]
    (if-not (authenticated? request)
      (throw-unauthorized)
      (str "authed: " ident))))



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

   (GET "/buddy" request
     (-> request
         buddy-handler))

   resources "/"))


;; (defn restart-server
;;   []
;;   (user/stop)
;;   (user/go))

;; (restart-server)
