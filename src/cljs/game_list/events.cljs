(ns game-list.events
  (:require
   [re-frame.core :as rf]
   [game-list.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

(rf/reg-event-db
  ::initialize-db
  (fn-traced [_ _]
    db/default-db))


(rf/reg-event-db
  ::test
  (fn-traced [db
              [event-name params]]
    (println event-name "button pressed: " params)
    db))

(rf/reg-event-db
  ::set-selected-game
  ;; db is the pre-state,
  (fn-traced [db
              [event-name game]]
    (println "set selected game: " game)
    (assoc db :selected-game game)))

(rf/reg-event-db
  ::delete-selected-game
  (fn-traced [db
              [_ game]]
    (let [pruned-games (remove #(= (:id game) (:id %)) (:games db))]
      (-> db
          (assoc :selected-game nil)
          (assoc :games pruned-games)))))
