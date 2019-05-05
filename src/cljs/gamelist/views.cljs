(ns gamelist.views
  (:require
   [re-frame.core :as rf]
   [gamelist.subs :as subs]
   [gamelist.events :as events]
   [goog.object :as gobject]
   [clojure.string :as string]
   [re-com.core
    :refer [scroller h-split input-text button h-box v-box box gap line label title slider checkbox input-text horizontal-bar-tabs vertical-bar-tabs p]
    :refer-macros [handler-fn]]
   [re-com.misc   :refer [slider-args-desc]]
   [reagent.core :as reagent]))

(def rate-game-text "Rate")
(def remove-game-text "Remove")
(def add-game-text "Add game")


(def not-nil? (complement nil?))

;; '#'+(Math.random()*0xFFFFFF<<0).toString(16);
(defn rand-color
  []
  nil)


(defn game-selected-box
  [game]
  (let [id (:_id game)
        name (:name game)
        slider-val (reagent/atom 50)]
    [h-box
     :padding "2px"
     :gap "12px"
     :children [[box :child name]
                [slider
                 :width  "100px"
                 :model  slider-val
                 :min 0, :max 100, :step 10
                 :on-change #(do (rf/dispatch [::events/set-rating game %])
                                 (reset! slider-val %))
                 :disabled? false]
                [:p {:class "game-remove"
                     :on-click #(rf/dispatch [::events/remove-selected-game game])}
                 remove-game-text]
                ]]))


(defn game-box
  [game]
  [h-box
   :children [[:div {:class "game"
                     :on-click #(rf/dispatch [::events/set-selected-game game])} (:name game)]]])

(defn game-common-box
  "Display common game content"
  [game]
  (if (:selected game)
    (game-selected-box game)
    (game-box game)))


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
  [box :child "Spellistan i digitalt format. En liten sida för ett stort nöje"])

(defn games-panel
  []
  (let [games (rf/subscribe [::subs/games])]
    [v-box
     :children [[:h3 "Listan över alla spel"]

                [v-box
                 :children [(for [game @games]
                              ^{:key (:_id game)} [:div (game-common-box game)])]]]]))


;; Vector of all panels
(def panels [{:id 0 :name "Spellistan" :render games-panel }
             {:id 1 :name "Om sidan" :render about-panel }])

;; (defn nav-item
;;   [item]
;;   [box :child (:name item)])

(defn nav-item
"Returns a function to render a navigation item"
[]
(let [mouse-over? (reagent/atom false)]
  (fn [item]
    (let [label (:name item)
          id  (:id item)]
      [:div
       {:style (when @mouse-over? {:font-weight "bold"
                                   :background-color "#CCCCCC"})
        :on-click      #(rf/dispatch [::events/set-panel id])
        :on-mouse-over #(reset! mouse-over? true)
        :on-mouse-out  #(reset! mouse-over? false)}
       label]))))


(defn navigation-panel
[]
(let [items panels]
  [v-box :children [(for [item items]
                      ^{:key (:id item)} [nav-item item])]]))

(defn main-panel
[]
(let [panel-id (rf/subscribe [::subs/panel])
      page-name (rf/subscribe [::subs/name])
      selected-panel (nth panels @panel-id)
      ;; selected-panel (first panels)
      ]
  (println "panel-id: " @panel-id
           ", main-panel: " (:name selected-panel))
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
                                 :child [(:render selected-panel)]]]]]]))
