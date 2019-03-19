(ns game-list.games
  (:require [clojure.set :as set]))

(def rank [4 3 1 1])

(def syn-list
#{"Mombasa"
  "Throughi the ages"
  "Le Havre"})

(def aan-list
#{"Eclipse"
  "Mombasa"
  "Alchemists"
  "Stock-Pile"})

(def dbe-list
#{"Mombasa"
  "Terraformingi Mars"
  "Eclipse"
  "GloomHaven"})

(def mco-list (take 8
                    #{"Alchemists"
                      "Terraforming Mars"
                      "Mombasa"
                      "Roll for the Galaxy"
                      "Expedition"
                      "Eclipse"
                      "Terra Mystica"
                      "Stock-Pile"
                      "Stone age"
                      "Citadels"}))


mco-list

(set/union dbe-list syn-list aan-list mco-list)


(let [lists [syn-list mco-list aan-list dbe-list ]
      a (zipmap dbe-list rank)
      b (zipmap syn-list rank)
      common-games (reduce (set/union) lists)
      res (for [l lists]
            (zipmap l rank))
      ]

common-games
)



(let [amp-hour 70.0
      watt 48.0
      volt 12.0
      efficiency 0.7
      time (* efficiency (/ (* amp-hour volt) watt))]
time)





(def game-list
  ["The ancient world" ; 2 hours
   "Panamax"
   "Andor"
   "Through the ages"
   "Terraforming Mars"
   "Expedition"
   "Mombasa" ; 5 Hours
   "Pandemic Ibera"
   "GloomHaven"
   "PowerGrid"
   "Eclipse"
   "Black fleet"
   "Seven wonders"
   "Civilization"
   "Stone age"
   "Terra Mystica"
   "SmallWorld"
   "Descent"
   "Roll for the Galaxy"
   "Pandemic Legacy"
   "Le Havre"
   "Dominon"
   "Planet steam"
   "Scythe"
   "Citadels"
   "Betrayal of the house on the hill"
   "Champions of Midgard"
   "Dead of Winter"
   "Robinsson"
   "Alchemists"
   "Giljotin"
   "Rune Bound"
   "Stock-Pile"
   "Epic SpellWars"
   "Star Realms"])


(let [top-tree {:Martin ["Alchemists" ]}
      player-names   [ "Anna" "Martin" "Simon" "David" ]

      ratings [{:game-name "PowerGrid", :Anna 9, :Martin 7, :Simon 8, :David 8}
               {:game-name "Eclipse", :Anna 8, :Martin 8, :Simon 5, :David 9}
               {:game-name "Black fleet", :Anna 5, :Martinleet 3, :Simon 4, :David 2}
               {:game-name "Seven wonders", :Anna 4, :Martinonders 3, :Simon 6, :David 4}
               {:game-name "Civilization", :Anna 5, :Martin  4, :Simon 4, :David 6}
               {:game-name "Stone age", :Anna  6, :Martin  5, :Simon 5, :David 6}
               {:game-name "Terra Mystica", :Anna  6, :Martin  6, :Simon 6, :David 8}
               {:game-name "SmallWorld", :Anna -, :Martin  6, :Simon 8, :David 5}
               {:game-name "Descent", :Anna  6, :Martin  7, :Simon 7, :David 7}
               {:game-name "Roll for the Galaxy", :Anna  4, :Martin  8, :Simon 7, :David 7}
               {:game-name "PandemicLegacy", :Anna 8, :Martin  9, :Simon 1, :David0  10}
               {:game-name "Le Havre", :Anna 5, :Martin  2, :Simon 8, :David 6}
               {:game-name "Dominon", :Anna  7, :Martin  5, :Simon 4, :David 7}
               {:game-name "Planet steam", :Anna 5, :Martin  5, :Simon 7, :David 6}
               {:game-name "Scythe", :Anna 7, :Martin  6, :Simon 9, :David 9}
               {:game-name "Citadels", :Anna 6, :Martin  5, :Simon 7, :David 6}
               {:game-name "Betrayal of the house on the hill", :Anna  1, :Martin  1, :Simon 3, :David 1}
               {:game-name "Champions of x", :Anna 7, :Martinidgard  9, :Simon 10, :David 9}
               {:game-name "Dead of Winter", :Anna 6, :Martin  7, :Simon 7, :David 7}
               {:game-name "Robinsson", :Anna  7, :Martin  6, :Simon 9, :David 7}
               {:game-name "Alchemists", :Anna 1, :Martin0 9, :Simon 9, :David 9}
               {:game-name "Giljotin", :Anna -, :Martin 4, :Simon  5, :David 5}
               {:game-name "RuneBound", :Anna  4, :Martin  4, :Simon 4, :David 4}
               {:game-name "Stock-Pile", :Anna 6, :Martin  4, :Simon 6, :David 6}
               {:game-name "Epic SpellWars", :Anna 3, :Martin  5, :Simon 5, :David 5}
               {:game-name "Star Realms", :Anna 6, :Martin 3, :Simon 5, :David 6}
               ]]
  (for [g ratings]
    (println "\"" (:game-name g) "\""))
  )
