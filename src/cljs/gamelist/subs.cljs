(ns gamelist.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 ::name
 (fn [db]
   (:name db)))




(defn prep-games
  [db]
  (let [selected (:selected-game db)
        games (:games db)]
    (map #(assoc % :selected
                 (= (:_id %) selected)) games)))

(rf/reg-sub
 ::games
 (fn [db]
   (prep-games db)))

(rf/reg-sub
::loading?
(fn [db]
  (:loading? db)))

(rf/reg-sub
::selected-game
(fn [db]
  (:selected-game db)))
