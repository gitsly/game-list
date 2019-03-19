(ns gamelist.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 ::name
 (fn [db]
   (:name db)))

(rf/reg-sub
 ::games
 (fn [db]
   (:games db)))

(rf/reg-sub
 ::selected-game
 (fn [db]
   (:selected-game db)))
