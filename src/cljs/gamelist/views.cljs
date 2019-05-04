(ns gamelist.views
  (:require
   [re-frame.core :as rf]
   [gamelist.subs :as subs]
   [gamelist.events :as events]
   [goog.object :as gobject]
   [clojure.string :as string]
   [re-com.core   :refer [h-box v-box box gap line label title slider checkbox input-text horizontal-bar-tabs vertical-bar-tabs p]]
   [re-com.misc   :refer [slider-args-desc]]
   [reagent.core :as reagent]))

(defn main-panel
  []
  [:p "Test"])

