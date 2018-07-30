(ns game-list.views
  (:require
   [re-frame.core :as re-frame]
   [game-list.subs :as subs]
   ))

(defn apan
  []
  2)

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " @name]
     ]))
