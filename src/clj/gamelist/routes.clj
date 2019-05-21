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
            [clj-time.core :as time]
            [buddy.auth :refer [authenticated? throw-unauthorized]]))

(log "------- Start -------")

(defn json-response
  "add needed header and set body with json content (json middleware will
                                                          take care of the json formatting (from clj datatype to json string))"
  [content]
  {:headers {"Content-Type" "application/json"}
   :body content})

;;-----------------------------------------------------------------------------
;; Handlers
;;-----------------------------------------------------------------------------

(defn games-handler
  "Get games collection, for some strange reason leaving out the outer [] in the json causes repl jack-in timout would work if evaling online!"
  [request]
  (let [games (db/collection "games")]
    (-> [{:games games
          :user (:identity request)}]
        json-response)))

(defn add-game-handler
  [request]
  (Thread/sleep 1000) ; fake some processing time
  (-> request
      :body
      (assoc :added (time/now))
      db/add-game
      json-response))

(defn update-game-handler
  [request]
  ;; (Thread/sleep 1000) ; fake some processing time
  (let [updated-game (-> request
                         :body
                         (assoc :updated (time/now)))]
    (log "update-game: " updated-game)
    ;; skip volatile related data when persisting to db
    (db/update-game (dissoc updated-game :volatile))
    (-> updated-game
        json-response)))

;; Route handler for test button
(defn test-handler
  [request]
  (-> {:hep "test" }
      json-response))

(defn remove-game-handler
  [request]
  (-> request
      :body 
      db/remove-game)
  "done")

  (defn main-handler
    [request]
    (-> "public/index.html"
        io/resource
        io/input-stream
        response
        (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))

  (defn login-handler
    [request]
    (let [ident (:identity request)]
      (if-not (authenticated? request)
        (throw-unauthorized)
        (do
          (log "Successfully logged in: " ident)
          (main-handler request)))))

(defn not-auth-handler
  [request value]
  (-> "public/noauth.html"
      io/resource
      io/input-stream
      response
      (assoc :status 403)))

(defn chat-handler
  "return entire chat page from db"
  [request]
  (let [chat (db/collection "chat")]
    (-> [{:chat chat
          :user (:identity request)}]
        json-response)))

;;-----------------------------------------------------------------------------
;; Define routing
;;-----------------------------------------------------------------------------
(defn home-routes [endpoint]
  (routes

   (GET "/" request
     (-> request
         main-handler))

   (PUT "/list/addgame" request
     (-> request
         add-game-handler))

   (PUT "/list/updategame" request
     (-> request
         update-game-handler))

   (PUT "/list/removegame" request
     (-> request
         remove-game-handler))

   (GET "/list/games" request
     (-> request
         games-handler))


   (GET "/list/test" request
     (-> request
         test-handler))

   (GET "/list/chat" request
     (-> request
         chat-handler))

   (GET "/login" request
     (-> request
         login-handler))

   resources "/"))

;; (defn restart-server
;;   []
;;   (user/stop)
;;   (user/go))
;; (restart-server)
