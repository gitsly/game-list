(ns gamelist.db
  (:require [gamelist.games :as games]))


(def default-games
  (let [cnt (count games/game-list)
        indices (take cnt (range))
        names games/game-list]
    (map #(zipmap [:id :name] [%1 %2]) indices names)))


(def default-db
  {:name "Bullen"
   :games (take 7 default-games)
   :loading? false
   :selected-game nil
   })
