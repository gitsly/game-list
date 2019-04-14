(ns gamelist.events
  (:require
   [re-frame.core :as rf]
   [gamelist.db :as db]
   [cljs-http.client :as http]
   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
   [cljs.core.async :as async :refer [<! >!]]) 
  (:require-macros [cljs.core.async.macros :refer [go]]))

;; Core async limitations in cljs
;; https://stackoverflow.com/questions/52375152/core-async-difference-between-clojure-and-clojurescript

;; https://github.com/Day8/re-frame/wiki/Talking-To-Servers


;; TODO: Find out this programatically (if localhost 
(defn base-url
  "prepends base-url to part"
  [part]
  (str "http://localhost:10555/" part))


(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(rf/reg-event-db
 ::test
 (fn [db
      [event-name params]]

   (go (let [response (<! (http/get (base-url "wrap")))]
         (println event-name "completed request: " params)
         (println  response))
       db)))

(rf/reg-event-db
 ::set-selected-game
 (fn [db
      [event-name game]]
   (println "set selected game: " game)
   (assoc db :selected-game game)))


(defn to-json
  "Create json from clojure map"
  [o]
  (.stringify js/JSON (clj->js o)))

(defn json-request
  "Takes a map or hash or vector an constructs a JSON request
  that is parsable at server using compojure route and ring-json lib"
  [data]
  {:body (to-json data)
   :content-type "application/json"
   :json-opts {:date-format "yyyy-MM-dd"}
   :accept :json})

(defn new-game [name]
  "Perform cljs-http request,
   Create the new game on remote host using http post"
  
  (go (let [url (base-url "addgame")
            game {:name name}
            payload (json-request game)
            response (<! (http/post url payload))]
        (rf/dispatch [::add-game-response (assoc game :_id "blarg")]))))

(rf/reg-event-db
 ::add-game
 (fn [db
      [event-name game-name]]
   (println "client: add-game")
   (let [game (new-game game-name)]
     (-> db
         (assoc :loading? true)))))

(rf/reg-event-db
 ::add-game-response 
 (fn-traced
  [db [_ game]]
  (println "client: add-game-response: " game)
  (-> db
      (assoc :loading? false)))) ;; take away that modal 

(rf/reg-event-db
::delete-selected-game
(fn [db
     [_ game]]
  (let [pruned-games (remove #(= (:_id game) (:_id %)) (:games db))]
    (-> db
        (assoc :selected-game nil)
        (assoc :games pruned-games)))))
