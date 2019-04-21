(ns gamelist.views
  (:require
   [re-frame.core :as rf]
   [gamelist.subs :as subs]
   [gamelist.events :as events]
   [goog.object :as gobject]
   [clojure.string :as string]))

;; TODO (ideas for re-frame components)
;; - Make a selected div component group

(def rate-game-text "Rate")
(def remove-game-text "Remove")
(def add-game-text "Add game")

(def not-nil? (complement nil?))

;; '#'+(Math.random()*0xFFFFFF<<0).toString(16);
(defn rand-color
  []
  nil)


(defn div-game-common
  "Display common game content"
  [game]
  (if (not-nil? (:rating game))
    [:div {:class "game-rating"} "rated"]))

(defn div-game-selected
  [game]
  (let [id (:_id game)
        name (:name game)]
    [:div {:class "game-selected"}
     name
     [:div
      {:class "game-remove"
       :on-click #(rf/dispatch [::events/remove-selected-game game])}
      remove-game-text]
     [:div
      {:class "game-rate"
       :on-click #(rf/dispatch [::events/rate-selected-game game])}
      rate-game-text]
     ]))

(defn div-game
  [game]
  (let [id (:_id game)
        name (:name game)]
    ^{:key (:_id game)} [:div "heppas"]))

(defn div-game-list
  []
  (let [games (rf/subscribe [::subs/games])
        selected-game (rf/subscribe [::subs/selected-game])
        selected-game-id (:_id @selected-game)]
    (println "selected game: " @selected-game)
    [:div
     (for [game @games]
       (div-game game))]))

(defn div-add-game
  []
  [:div {:id "the-div" :style {:background-color "#f3e0bb"}}

   [:input {:id "the-input" :class "text"}]
   [:button
    {:on-click
     ;; Note, the .-target property refers to the event (javascript)
     #(rf/dispatch [::events/add-game
                    (-> %
                        .-target
                        .-parentNode
                        (.querySelector "#the-input")
                        .-value)])}
    add-game-text]
   ])

(defn div-loading
[]
(let [loading (rf/subscribe [::subs/loading?])
      games (rf/subscribe [::subs/games])]
  [:div "Loading: " (str @loading)]))

(defn main-panel []
(let [name (rf/subscribe [::subs/name])]
  [:div
   [:h1 "Game list: " @name]
   (div-game-list)
   (div-add-game)
   (div-loading)
   [:div {:style {:background-color "#e0e0eb"}}
    [:p "Test button"]
    [:button  {:on-click #(rf/dispatch [::events/test "Bullen"])} "Test"]] ; Button should change @name
   ]))



;;--------------- Snippets

(let [a [ 1  2  3]
      b ["a" "b" "c"]]
(map #(zipmap [:digit :letter] [% %2]) a b))

(let [a {:name "ninja" :stamina 18 }
      b {:name "ninja" :stamina 15 }
      [only-a only-b both] (clojure.data/diff a b)]
both)


(let [id 1
      data [{:id 0 :name "bength"}
            {:id 1 :name "alice"}
            {:id 2 :name "lisa"}]
      modded (remove #(= id (:id %)) data)]
modded
)
