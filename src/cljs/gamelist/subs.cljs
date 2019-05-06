(ns gamelist.subs
  (:require
   [re-frame.core :as rf]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Utilty functions for transforming subscribed data
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn get-rating
  [game
   user]
  (-> game
      :rating
      (get (keyword user))
      :value))

(defn get-total-rating
  [game]
  (let [ratings (:rating game)
        user-ratings (vals ratings)
        rating-values (map :value user-ratings)]
    (/ (reduce + rating-values) (count rating-values))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Subscriptions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(rf/reg-sub
 ::name
 (fn [db]
   (:name db)))

;; Currently selected panel
(rf/reg-sub
 ::panel
 (fn [db]
   (:panel db)))

(rf/reg-sub
 ::games
 (fn [db]
   (:games db)))

(rf/reg-sub
 ::user
 (fn [db]
   (:user db)))

(rf/reg-sub
 ::loading?
 (fn [db]
   (:loading? db)))
