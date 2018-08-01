(ns game-list.views
  (:require
   [re-frame.core :as rf]
   [game-list.subs :as subs]
   [game-list.events :as events]
   [clojure.string :as string]))



;; TODO (ideas for re-frame components)
;; - Make a selected div component group

(def delete-game-text "Remove")
(def add-game-text "Add game")

;; '#'+(Math.random()*0xFFFFFF<<0).toString(16);
(defn rand-color
  []
  nil)

(defn div-game-selected
  [game]
  (let [id (:id game)
        name (:name game)]
    [:div {:style {:font-weight "bold"
                   :background-color "#ebe6e0"}}
     name
     [:div
      {:on-click #(rf/dispatch [::events/delete-selected-game game])}
      delete-game-text]
     ]))

(defn div-game
  [game
   selected-game-id]
  (let [id (:id game)
        name (:name game)]
    (if (= id selected-game-id)
      ^{:key id} [:div (div-game-selected game)]
      ^{:key id} [:div
                  {:on-click #(rf/dispatch [::events/set-selected-game game])}
                  name])))

(defn div-game-list
  []
  (let [games (rf/subscribe [::subs/games])
        selected-game (rf/subscribe [::subs/selected-game])
        selected-game-id (:id @selected-game)]
    (println "selected game: " @selected-game)
    [:div
     (for [game @games]
       (div-game game selected-game-id))]))

(defn div-add-game
  []
  [:div {:style {:background-color "#f3e0bb"}}

   [:input {:id "div-add-game-name" :class "text" :value "Hero Quest"}]
   [:button
    {:on-click #(rf/dispatch [::events/add-game (.getElementById js/document "div-add-game-name") ])}
    add-game-text]
   ;; (-> % .-target .-value)
   ])

;; [:input {:class "text"
;;          :value "name"}
;;  [:button {:on-click #(rf/dispatch [::events/add-game .-value])}
;;   add-game-text]
;;  ]])

(defn main-panel []
  (let [name (rf/subscribe [::subs/name])]
    [:div
     [:h1 "Game list: " @name]
     (div-game-list)
     (div-add-game)
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
