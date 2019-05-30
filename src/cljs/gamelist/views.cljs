(ns gamelist.views
  (:require [cljs.core :as core]
            [re-frame.core :as rf]
            [gamelist.subs :as subs]
            [gamelist.views.chat :as chat]
            [goog.string :as gstring]
            [goog.string.format]
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

(defn game-col-width-px
  "Retrieve width for game column"
  [index]
  (let [column-widths [200 200 30 30 50]]
    (str (nth column-widths index) "px")))

(defn game-rate-box
  [game
   user]
  (let [delete-icon "zmdi-delete"
        save-icon "zmdi-save"
        curr-rating (subs/get-rating game user)
        slider-val (reagent/atom (if curr-rating curr-rating 50))]
    [h-box
     :width (game-col-width-px 1)
     :children [[slider
                 :width  "140px"
                 :model  slider-val
                 :min 0, :max 100, :step 10
                 :on-change #(reset! slider-val %)
                 :disabled? false]
                [md-icon-button
                 :md-icon-name save-icon
                 :tooltip      "Spara"
                 :size         :smaller
                 :on-click #(rf/dispatch [::events/set-rating game @slider-val])]
                [gap :size "20px"]
                [md-icon-button
                 :md-icon-name delete-icon
                 :tooltip      "Ta bort spel"
                 :size         :smaller
                 :on-click #(rf/dispatch [::events/remove-selected-game game])]]]))

(defn game-box
  [game
   user]
  (let [id (:_id game)
        name (:name game)
        selected? (-> game :volatile :selected)
        rating (-> game :volatile :rating-user (/ 10) int)
        total-rating (-> game :volatile :rating-total (/ 10))
        rating-count (-> game :volatile :rating-count str)]
    [h-box
     ;; :padding "2px"
     :style { :background-color (if selected? "#EEEFFE" "#FFFFFF")}
     :gap "10px"
     :attr {:on-click #(rf/dispatch [::events/set-selected-game game])}
     :children [[box
                 :width (game-col-width-px 0)
                 :child name]
                (if selected?
                  (game-rate-box game user)
                  [box
                   :width (game-col-width-px 1)
                   :child ""])

                [box
                 :width (game-col-width-px 2)
                 :child (if (> rating 0) [:p rating] "-")]
                [box
                 :width (game-col-width-px 3)
                 :child (if total-rating [:p (gstring/format "%.1f" total-rating)] "-")]
                [box
                 :width (game-col-width-px 4)
                 :child rating-count]
                ]]))


;;--------------------------------------------------------------------------------
;; Panels
;;--------------------------------------------------------------------------------
(defn about-panel
  []
  [box :child
   (str "Spellistan i digitalt format. En liten sida för ett stort nöje.")])

(defn sorter
  "TODO: add possibility to deflect sorter to main generator, depending
  on which column is selected for sorting"
  [item]
  (-> item :volatile :rating-total))

;; TODO: make TR's
(defn games-panel
  []
  (let [games (rf/subscribe [::subs/games])
        user-sub   (rf/subscribe [::subs/user])
        user @user-sub
        sorted-games (reverse (sort-by #(-> % sorter) @games))]
    [v-box
     :children [[:h3 "Listan över alla spel"]
                [v-box
                 :children [[h-box
                             :gap "10px"
                             :height "45px"
                             :style { :background-color "#EEEEEE"}
                             :children [[box :width (game-col-width-px 0) :child "Namn"]
                                        [box :width (game-col-width-px 1) :child ""]
                                        [box :width (game-col-width-px 2) :child "Mitt betyg"]
                                        [box :width (game-col-width-px 3) :child "Snitt"]
                                        [box :width (game-col-width-px 4) :child "Antal betyg"]]]
                            (for [game sorted-games]
                              ^{:key (:_id game)} [:div (game-box game user)])]]]]))

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
                                   (reset! text-val ""))]]]))

(defn session-panel
  []
  (let [selected-session (reagent/atom nil)]
    [:p "TODO: lista spel inför spelkväll och rösta"] ))


;; Vector of all panels
(def panels [{:id 0 :name "Spellistan" :render games-panel }
             {:id 1 :name "Lägg till spel" :render add-game-panel }
             {:id 2 :name "Spelkväll" :render session-panel }
             {:id 3 :name "Notiser" :render #(chat/chat-panel 'main)}
             {:id 4 :name "test" :render #(chat/chat-panel 'test2)}
             {:id 5 :name "Om sidan" :render about-panel }])

;; (rf/dispatch [::chat/get-chat session])


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
                                     :background-color "#CCFFCC"})
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
      user (rf/subscribe [::subs/user])
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
                     :children [[box :style {:font-weight "bold"}
                                 :child (str @page-name " (" @user ")")]
                                (navigation-panel)]]]
   :panel-2 [scroller
             :attr  {:id "right-panel"}
             :child [v-box
                     :size  "1"
                     :children [(loading-popover)
                                [box
                                 :padding "0px 0px 0px 50px"
                                 :child [(:render selected-panel)]]]]]]))
