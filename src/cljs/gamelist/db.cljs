(ns gamelist.db
  (:require [gamelist.games :as games]))


(def default-games
  (let [cnt (count games/game-list)
        indices (take cnt (range))
        names games/game-list]
    (map #(zipmap [:id :name] [%1 %2]) indices names)))

(def panels [:games, :add-game])

(def default-db
  {:name "Bullen"
   :panel (first panels)
   :games []
   :loading? false
   })

;; "votes": [{"David": "4"},{"Anna": "7"}]
