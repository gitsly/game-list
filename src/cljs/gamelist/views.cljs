(ns gamelist.views
  (:require
   [re-frame.core :as rf]
   [gamelist.games :as games]
   [gamelist.subs :as subs]
   ))

(defn div-game-list
  [games]
  [:p "test: "])

(defn main-panel []
  (let [name (rf/subscribe [::subs/name])]
    [:div
     [:h1 "Game list: " @name]
     (div-game-list games/game-list)
     ]))

(defn list-items
  [data]
  "Generates a sequence of :li elements. Adding value of each item as metadata
  (required for re-frame)"
  (for [i data]
    ^{:key i} [:li i]))
