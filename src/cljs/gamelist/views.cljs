(ns gamelist.views
  (:require
   [re-frame.core :as rf]
   [gamelist.subs :as subs]
   [gamelist.events :as events]
   [goog.object :as gobject]
   [clojure.string :as string]
   [re-com.core   :refer [button h-box v-box box gap line label title slider checkbox input-text horizontal-bar-tabs vertical-bar-tabs p]]
   [re-com.misc   :refer [slider-args-desc]]
   [reagent.core :as reagent]))

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


(defn div-game-selected
  [game]
  (let [id (:_id game)
        name (:name game)
        slider-val (reagent/atom 50)]
    [h-box
     :children [[box :child name]
                [:p {:class "game-remove"
                     :on-click #(rf/dispatch [::events/remove-selected-game game])}
                 remove-game-text]
                [slider
                 :model     slider-val
                 :min 0, :max 100, :step 10
                 :on-change #(do (rf/dispatch [::events/set-rating game %])
                                 (reset! slider-val %))
                 :disabled? false]]]))

;; (rf/dispatch [::events/set-rating game %])

(defn div-game
  [game]
  [h-box
   :children [[:div {:class "game"
                     :on-click #(rf/dispatch [::events/set-selected-game game])} (:name game)]]])

(defn div-game-common
  "Display common game content"
  [game]
  (if (:selected game)
    (div-game-selected game)
    (div-game game)))

(defn div-game-list
  []
  (let [games (rf/subscribe [::subs/games])]
    [v-box
     :children [(for [game @games]
                  ^{:key (:_id game)} [:div (div-game-common game)])]]))

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


;; (defn main-panel []
;;   (let [name (rf/subscribe [::subs/name])]
;;     [v-box
;;      :children [[:h1 "Game list: " @name]
;;                 (div-game-list)
;;                 (div-add-game)
;;                 (div-loading)
;;                 [v-box :children ["apan " "bepan " "cepan "]]]]))

(defn main-panel
  []
  (let [name (rf/subscribe [::subs/name])
        games (rf/subscribe [::subs/games])
        game (first @games)]
    [v-box
     :style {:background-color "#AAAAAA" }
     :gap "2px"
     :children [[box :child (str "Test:" @name) ]
                [h-box
                 :style {:background-color "#AAAAFF" }
                 :height "100px"

                 :children [[box :size "70px" :child "Nav"]
                            [button
                             :label "Test"
                             :on-click #(rf/dispatch [::events/test "Bullen"])]
                            [box
                             :style {:background-color "#AAFFAA" }
                             :size "1" :child "Content"]]]
                [box
                 :style {:background-color "#FFAAAA" }
                 :child "Footer"]]]))


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
