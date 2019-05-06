(ns gamelist.views
  (:require
   [re-frame.core :as rf]
   [gamelist.subs :as subs]
   [gamelist.events :as events]
   [goog.object :as gobject]
   [clojure.string :as string]
   [re-com.core
    :refer [modal-panel md-icon-button scroller h-split input-text button h-box v-box box gap line label title slider checkbox input-text horizontal-bar-tabs vertical-bar-tabs p]
    :refer-macros [handler-fn]]
   [re-com.misc   :refer [slider-args-desc]]
   [reagent.core :as reagent]))

;; Material design icons
(def icons
  [{:id "zmdi-plus"    :label [:i {:class "zmdi zmdi-plus"}]}
   {:id "zmdi-delete"  :label [:i {:class "zmdi zmdi-delete"}]}
   {:id "zmdi-undo"    :label [:i {:class "zmdi zmdi-undo"}]}
   {:id "zmdi-home"    :label [:i {:class "zmdi zmdi-home"}]}
   {:id "zmdi-account" :label [:i {:class "zmdi zmdi-account"}]}
   {:id "zmdi-info"    :label [:i {:class "zmdi zmdi-info"}]}])


(def rate-game-text "Rate")
(def remove-game-text "Remove")
(def add-game-text "Add game")


(def not-nil? (complement nil?))

;; '#'+(Math.random()*0xFFFFFF<<0).toString(16);
(defn rand-color
  []
  nil)


(defn game-selected-box
  [game
   user]
  (let [id (:_id game)
        name (:name game)
        delete-icon "zmdi-delete"
        save-icon "zmdi-save"
        slider-val (reagent/atom 50)]
    [h-box
     ;; :padding "2px"
     :style { :background-color "#EEEEEE" }
     :gap "12px"
     :children [[box :child name]
                [slider
                 :width  "100px"
                 :model  slider-val
                 :min 0, :max 100, :step 10
                 :on-change #(reset! slider-val %)
                 :disabled? false]
                [md-icon-button
                 :md-icon-name save-icon
                 :tooltip      "Spara"
                 :size         :smaller
                 :on-click #(rf/dispatch [::events/set-rating game @slider-val])]
                [md-icon-button
                 :md-icon-name delete-icon
                 :tooltip      "Ta bort spel"
                 :size         :smaller
                 :on-click #(rf/dispatch [::events/remove-selected-game game])]]]))


(defn get-rating
  [game
   user]
  (-> game
      :rating
      (get (keyword user))
      :value
      (/ 10)
      int))

(defn game-box
  [game
   user]
  (let [rating (get-rating game user)]
    (println "Gmo: " rating)
    [h-box
     :style { :background-color "#FFFFFF"}
     :children [[box
                 :width "300px"
                 :child [:div {:on-click #(rf/dispatch [::events/set-selected-game game])} (:name game)]]
                [box
                 :width "20px"
                 :child (if rating [:p rating] "-")]]]))

(defn game-common-box
"Display common game content"
[game
 user]
(if (:selected game)
  (game-selected-box game user)
  (game-box game user)))


;;--------------------------------------------------------------------------------
;; Panels
;;--------------------------------------------------------------------------------
(defn about-panel
[]
[box :child "Spellistan i digitalt format. En liten sida för ett stort nöje"])

;; TODO: make TR's
(defn games-panel
[]
(let [games (rf/subscribe [::subs/games])
      user-sub   (rf/subscribe [::subs/user])
      user @user-sub]
  [v-box
   :children [[:h3 "Listan över alla spel"]
              [v-box
               :children [(for [game @games]
                            ^{:key (:_id game)} [:div (game-common-box game user)])]]]]))

(defn add-game-panel
[]
(let [text-val (reagent/atom "")]
  [v-box
   :children [[:h3 "Lägg till nytt spel"]
              [input-text
               :model            text-val
               :width            "300px"
               :placeholder      "Spelnamn"
               :change-on-blur?  true
               :on-change        #((rf/dispatch [::events/add-game %])
                                   (reset! text-val %))]]]))

;; Vector of all panels
(def panels [{:id 0 :name "Spellistan" :render games-panel }
             {:id 1 :name "Lägg till spel" :render add-game-panel }
             {:id 2 :name "Om sidan" :render about-panel }])

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

(defn loading-popover
  []
  (let [loading? (rf/subscribe [::subs/loading?])]
    (when @loading?
      [modal-panel
       :backdrop-color   "grey"
       :backdrop-opacity 0.4
       ;; :style            {}
       :child            [:p "Laddar..."]])))


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
     :initial-split 20.0
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
                       :children [(loading-popover)
                                  [box
                                   :padding "0px 0px 0px 50px"
                                   :child [(:render selected-panel)]]]]]]))
