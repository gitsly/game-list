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


;; Dont need the baseurl!
;; TODO: Find out this programatically (if localhost 
;; (defn base-url
;;   "prepends base-url to part"
;;   [part]
;;   (str "http://localhost:10555/" part))

(defn get-all-games
  []
  (go (let [response (<! (http/get "list/games"))]
        (rf/dispatch [::get-all-games-response response]))))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   (get-all-games)
   db/default-db))

(rf/reg-event-db ;; register-handler has been renamed to reg-event-db
 ::get-all-games-response 
 (fn
   [db [_ response]]
   (let [body (-> response :body first)
         games (:games body)
         user (:user body)]
     (println "client: get-all-games-response: " games ", User:" user)
     (-> db
         (assoc :user user)
         (assoc :games games)))))

(rf/reg-event-db
 ::test
 (fn [db
      [event-name params]]
   (go (let [response (<! (http/get "list/test"))]
         (println event-name "completed request: " params)
         (println  response))
       db)))

(defn select-game
  [games
   selected-id]
  (map #(assoc % :selected
               (= (:_id %) selected-id)) games))

(rf/reg-event-db
 ::set-selected-game
 (fn[db
     [event-name game]]
   (let [games (:games db)
         id (:_id game)]
     (println "set selected game: " id)
     (assoc db :games (select-game games id)))))

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
  (go (let [url "list/addgame"
            game {:name name}
            payload (json-request game)
            response (<! (http/put url payload))]
        (rf/dispatch [::add-game-response response]))))

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
 (fn
   [db [_ response]]
   (let [games (:games db)
         game (:body response)]
     (println "client: add-game-response: " game)
     (-> db
         (assoc :loading? false)
         (assoc :games (conj games game))))))

(rf/reg-event-db
 ::remove-selected-game
 (fn [db
      [_ game]]
   ;; remove remotely
   ;; (go (<! (http/put "removegame" (json-request game))))
   (http/put "list/removegame" (json-request game))
   ;; Remove in client app-db (visually)
   (let [pruned-games (remove #(= (:_id game) (:_id %)) (:games db))]
     (-> db
         (assoc :games pruned-games)))))

(rf/reg-event-db
 ::set-rating
 (fn [db
      [_ game value]]
   (let [user (:user db)
         game-id (:_id game)
         games (:games db)
         rating {user { :value value }}
         new-game-list (-> (zipmap (map #(:_id %) games) games)
                           (assoc-in [game-id :rating] rating)
                           vals)]
     (-> db
         (assoc :games new-game-list)))))
