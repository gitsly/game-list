(ns gamelist.views
  (:require
   [re-frame.core :as rf]
   [gamelist.subs :as subs]
   [gamelist.events :as events]
   [goog.object :as gobject]
   [clojure.string :as string]
   [re-com.core   :refer [scroller h-split input-text button h-box v-box box gap line label title slider checkbox input-text horizontal-bar-tabs vertical-bar-tabs p]]
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


(defn div-loading
  []
  (let [loading (rf/subscribe [::subs/loading?])
        games (rf/subscribe [::subs/games])]
    [:div "Loading: " (str @loading)]))

(defn add-game-box
  []
  (let [text-val (reagent/atom "")]
    [input-text
     :model            text-val
     :width            "300px"
     :placeholder      "Game name"
     :change-on-blur?  true
     :on-change        #((rf/dispatch [::events/add-game %])
                         (reset! text-val %))]))

;;--------------------------------------------------------------------------------
;; Panels
;;--------------------------------------------------------------------------------
(defn about-panel
  []
  [box :child "Game list"])

(defn games-panel
  []
  [box :child "Game list"])

;; Vector of all panels
(def panels [{:id 1 :name "Game list" :render games-panel }
             {:id 2 :name "About" :render about-panel }])

;; (defn nav-item
;;   [item]
;;   [box :child (:name item)])

(defn nav-item
  [item]
  (let [label (:name item)
        id  (:id item)
        mouse-over? (reagent/atom false)]
    ^{:key id} [box :child label]))

(defn navigation-panel
  []
  (let [items panels]
    [v-box :children [(for [item items]
                        (nav-item item))]]))

(defn main-panel
  []
  (let [selected-panel (rf/subscribe [::subs/panel])
        page-name (rf/subscribe [::subs/name])]
    [h-split
     ;; Outer-most box height must be 100% to fill the entrie client height.
     ;; This assumes that height of <body> is itself also set to 100%.
     ;; width does not need to be set.
     :height   "100%"
     :initial-split 12
     :margin "0px"
     :panel-1 [scroller
               :v-scroll :auto
               :h-scroll :off
               :child [v-box
                       :size "1"
                       :children [[box :style {:font-weight "bold"} :child @page-name]
                                  (navigation-panel)]]]
     :panel-2 [scroller
               :attr  {:id "right-panel"}
               :child [v-box
                       :size  "1"
                       :children [[box
                                   :padding "0px 0px 0px 50px"
                                   :child "TODO: Selected panel content"]]]]]))


;; Compiling build :app to "dev-target/public/js/compiled/gamelist.js" from
;; ["src/cljs" "src/cljc" "dev"]...

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
