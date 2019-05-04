(ns gamelist.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core :as re-com :refer [h-box v-box box]]))

(defn main-panel []
  [v-box
   :style {:background-color "#AAAAAA" }
   :gap "2px"
   :children [[box :child "Da Main header"]
              [h-box
               :style {:background-color "#AAAAFF" }
               :height "100px"

               :children [[box :size "70px" :child "Nav"]
                          [box
                           :style {:background-color "#AAFFAA" }
                           :size "1" :child "Content"]]]
              [box
               :style {:background-color "#FFAAAA" }
               :child "Footer"]]])
