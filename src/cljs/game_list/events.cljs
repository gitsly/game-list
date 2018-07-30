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
            (println "test button pressed: " params)
            db))

(rf/reg-event-db
 ::my-event1
 ;; db is the pre-state,
 (fn-traced [db
             [event-name params]]
            (println "Din mamma was here" event-name "parameters: " params)
            (assoc db :name params)))
