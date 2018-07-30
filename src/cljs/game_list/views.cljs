(ns game-list.views
  (:require
   [re-frame.core :as rf]
   [game-list.subs :as subs]
   [game-list.events :as events]
   ))

;; '#'+(Math.random()*0xFFFFFF<<0).toString(16);
(defn rand-color
  []
  nil)

(defn div-game
  [game]
  (let [id (:id game)
        name (:name game)]
    ^{:key id} [:div name]))


(defn div-game-list
[]
(let [games (rf/subscribe [::subs/games])]
  [:div
   (for [g @games]
     (div-game g))]))

;; [:button  {:on-click #(rf/dispatch [::events/my-event1 "Bullen"])} "change list name"]
                                        ; Button should change @name

(defn main-panel []
  (let [name (rf/subscribe [::subs/name])]
    [:div
     [:h1 "Game list: " @name]
     (div-game-list)
     [:div {:style {:background-color "#e0e0eb"}}
      [:p "Test button"]
      [:button  {:on-click #(rf/dispatch [::events/test "Bullen"])} "Test"]] ; Button should change @name

     ]))



;;--------------- Snippets

(let [a [ 1  2  3]
      b ["a" "b" "c"]]
  (map #(zipmap [:digit :letter] [% %2]) a b))
