(ns gamelist.events
  (:require
   [re-frame.core :as rf]
   [gamelist.db :as db]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))


(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))


(rf/reg-event-db
 ::test
 (fn [db
      [event-name params]]

   (go (let [response (<! (http/get "bla-localhost:10555/wrap"))]
         (println event-name "did request: " params)
         (println (:status response))
         (println (map :login (:body response)))))
   db))

(rf/reg-event-db
::set-selected-game
(fn [db
     [event-name game]]
(println "set selected game: " game)
(assoc db :selected-game game)))

(defn new-game
[name
db]
(let [new-id (count (:games db))]
{:id new-id :name name}))

(rf/reg-event-db
::add-game
(fn [db
     [event-name param]]
(let [game (new-game param db)
      games (:games db)]
  (println "add-game, param: " param " new-game: " game)
  (-> db
      (assoc :games (conj games game) )))))

(rf/reg-event-db
::delete-selected-game
(fn [db
     [_ game]]
(let [pruned-games (remove #(= (:id game) (:id %)) (:games db))]
  (-> db
      (assoc :selected-game nil)
      (assoc :games pruned-games)))))
