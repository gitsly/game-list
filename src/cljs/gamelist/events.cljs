(ns gamelist.events
  (:require
   [re-frame.core :as rf]
   [gamelist.db :as db]
   [gamelist.events.chat :as chat]
   [gamelist.utils :as utils]
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
    (chat/get-chat 'main)
    db/default-db))

(rf/reg-event-db ;; register-handler has been renamed to reg-event-db
  ::get-all-games-response
  (fn
    [db [_ response]]
    (let [body (-> response :body first)
          games (:games body)
          user (:user body)]
      ;; (println "client: get-all-games-response: " games ", User:" user)
      (-> db
          (assoc :user user)
          (assoc :games games)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Update game and wait for game in response
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn update-game [game]
  "Update game on remote host using http post, get game back (from server)
   with udpated data combined with latest info from server"
  (go (let [url "list/updategame"
            payload (utils/json-request game)
            response (<! (http/put url payload))]
        (rf/dispatch [::update-game-response response]))))
;; TODO: test with response inline above

(rf/reg-event-db
 ::update-game
 (fn [db
      [event-name game]]
   ;; (println "client: update-game" (:name game))
   (update-game game)
   (-> db
       (assoc :loading? true))))

(rf/reg-event-db
  ::update-game-response
  (fn
    [db [_ response]]
    (let [games (:games db)
          updated-game (:body response)
          game-id (:_id updated-game)
          new-game-list (-> (zipmap (map #(:_id %) games) games)
                            (assoc game-id updated-game)
                            vals)]
      ;; (println "client: update-game-response: " new-game-list)
      (-> db
          (assoc :loading? false)
          (assoc :games new-game-list)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(rf/reg-event-db
  ::test
  (fn [db
       [event-name params]]
    (go (let [response (<! (http/get "list/test"))]
          (println event-name "completed request: " params)
          (println  response))
        db)))

(rf/reg-event-db
  ::set-panel
  (fn [db
       [event-name selected-panel]]
    (-> db
        (assoc :panel selected-panel))))

(defn set-as-selected
  [game
   selected-id]
  "TODO: must be an easier way to modify nested ui->selected prop"
  (let [volatile (:volatile game)
        id (:_id game)
        selected? (= id selected-id)
        volatile-new (assoc volatile :selected selected?)]
    (assoc game :volatile volatile-new)))

(defn select-game
  [games
   selected-id]
  "Generate a collection with selected field set, will be true for one game in
  collection where id is matched"
  (map #(set-as-selected % selected-id) games))

(rf/reg-event-db
  ::set-selected-game
  (fn[db
      [event-name game]]
    (let [games (:games db)
          id (:_id game)]
      ;; (println "set selected game: " id)
      (assoc db :games (select-game games id)))))


(defn new-game [name]
  "Perform cljs-http request,
   Create the new game on remote host using http post"
  (go (let [url "list/addgame"
            game {:name name}
            payload (utils/json-request game)
            response (<! (http/put url payload))]
        (rf/dispatch [::add-game-response response]))))

(rf/reg-event-db
  ::add-game
  (fn [db
       [event-name game-name]]
    ;; (println "client: add-game")
    (let [game (new-game game-name)]
      (-> db
          (assoc :loading? true)))))


(rf/reg-event-db
  ::add-game-response 
  (fn
    [db [_ response]]
    (let [games (:games db)
          game (:body response)]
      ;; (println "client: add-game-response: " game)
      (-> db
          (assoc :loading? false)
          (assoc :games (conj games game))))))

;;--------------------

(rf/reg-event-db
  ::remove-selected-game
  (fn [db
       [_ game]]
    ;; remove remotely
    ;; (go (<! (http/put "removegame" (json-request game))))
    (http/put "list/removegame" (utils/json-request game))
    ;; Remove in client app-db (visually)
    (let [pruned-games (remove #(= (:_id game) (:_id %)) (:games db))]
      (-> db
          (assoc :games pruned-games)))))

(rf/reg-event-db
  ::set-rating
  (fn [db
       [_ game value]]
    (let [user (keyword (:user db))
          game-id (:_id game)
          games (:games db)
          old-rating (:rating game)
          new-rating (assoc old-rating user { :value value }) 
          rated-game (assoc game :rating new-rating)]
      ;; (println "Rated game: " rated-game)
      (update-game rated-game)
      (-> db
          (assoc :loading? true)))))
