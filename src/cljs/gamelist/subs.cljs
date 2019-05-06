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

(defn prep-game
  [game
   user]
  "Attach generated data before passing to views"
  (let [volatile (:volatile game)
        volatile-new (-> volatile
                         (assoc :rating-user (get-rating game user))
                         (assoc :rating-total (get-total-rating game)))]
    (assoc game :volatile volatile-new)))

(rf/reg-sub
 ::games
 (fn [db]
   (let [user (:user db)
         games (:games db)]
     (map #(prep-game % user) games))))

(rf/reg-sub
 ::user
 (fn [db]
   (:user db)))

(rf/reg-sub
::loading?
(fn [db]
  (:loading? db)))
