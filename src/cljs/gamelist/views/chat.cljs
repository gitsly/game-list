(ns gamelist.views.chat
  (:require [cljs.core :as core]
            [re-frame.core :as rf]
            [gamelist.subs :as subs]
            [goog.string :as gstring]
            [goog.string.format]
            [gamelist.events :as events]
            [goog.object :as gobject]
            [clojure.string :as string]
            [re-com.core
             :refer [border alert-box alert-list modal-panel md-icon-button scroller h-split input-text button h-box v-box box gap line label title slider checkbox input-text horizontal-bar-tabs vertical-bar-tabs p]
             :refer-macros [handler-fn]]
            [re-com.misc   :refer [slider-args-desc]]
            [reagent.core :as reagent]))

(defn title2
  "2nd level title"
  [text style]
  [title
   :label text
   :level :level2
   :style style])

(defn chat-entry
  "TODO: spec has: content, user, added"
  [entry]
  (let [user (:user entry)
        content (:content entry)
        added (:added entry)]
    [border
     :border "1px dashed #AAAAAA"
     :child  [v-box :children [[title2 user]
                               [:p content]
                               [:p added]]]]))

(defn sort-entries
  [entries]
  (reverse (sort-by #(-> % :added) entries)))

(defn chat-panel-children
  [entries]
  (for [entry entries]
    ^{:key (:_id entry)} [h-box :children [(chat-entry entry)]]))


(defn chat-panel
  []
  (let [chat-info (rf/subscribe [::subs/chat])
        entries (:entries @chat-info)]
    (println entries)
    [v-box :children [(chat-panel-children (sort-entries entries))]]))

;; (for [entry entries]
;;   [:p "TODO: views.chatish2"]
;;   )
