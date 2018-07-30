(ns game-list.db
  (:require [game-list.games :as games]))


(def default-games
  (let [cnt (count games/game-list)
        names games/game-list
        indices (take cnt range)]
    (map #(zipmap [:index :name] [%1 %2]) indices names)))


(def default-db
  {:name "re-frame"})
