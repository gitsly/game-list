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
             :refer [modal-panel md-icon-button scroller h-split input-text button h-box v-box box gap line label title slider checkbox input-text horizontal-bar-tabs vertical-bar-tabs p]
             :refer-macros [handler-fn]]
            [re-com.misc   :refer [slider-args-desc]]
            [reagent.core :as reagent]))

(defn chat-panel
  []
  (let [chat-info (rf/subscribe [::subs/chat])]
    [:p "TODO: views.chatish2"]))
