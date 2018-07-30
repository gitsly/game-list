(ns game-list.views
  (:require
   [re-frame.core :as rf]
   [game-list.subs :as subs]
   [game-list.events :as events]

   [clojure.string :as string]))

;; '#'+(Math.random()*0xFFFFFF<<0).toString(16);
(defn rand-color
  []
  nil)

(defn div-game-selected
  [game]
  (let [id (:id game)
        name (:name game)]
    [:div {:style {:font-weight "bold"}} (string/upper-case name)]))

(defn div-game
  [game
   selected-game-id]
  (let [id (:id game)
        name (:name game)]
    (if (= id selected-game-id)
      ^{:key id} [:div (div-game-selected game)]
      ^{:key id} [:div name])))



(defn div-game-list
  []
  (let [games (rf/subscribe [::subs/games])
        selected-game (rf/subscribe [::subs/selected-game])
        selected-game-id (:id @selected-game)]
    (println "selected game id: "selected-game-id)
    [:div
     (for [g @games]
       (div-game g selected-game-id))]))


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
