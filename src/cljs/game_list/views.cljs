(ns game-list.views
  (:require
   [re-frame.core :as rf]
   [game-list.games :as games]
   [game-list.subs :as subs]
   ))

;; '#'+(Math.random()*0xFFFFFF<<0).toString(16);
(defn rand-color
  []
  nil)

(defn div-game
  [game]
  ^{:key game} [:div game])

(defn list-items
  [data]
  (for [g data]
    (div-game g)))

(defn div-game-list
  [games]
  [:div
   (list-items games)
   ])

(defn main-panel []
  (let [name (rf/subscribe [::subs/name])]
    [:div
     [:h1 "Game list: " @name]
     (div-game-list games/game-list)]))
