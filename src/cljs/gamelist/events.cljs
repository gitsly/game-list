(ns gamelist.events
  (:require
   [re-frame.core :as rf]
   [gamelist.db :as db]
   ;; [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
   ))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))


(rf/reg-event-db
 ::test
 (fn [db
      [event-name params]]
   (println event-name "button pressed: " params)
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
