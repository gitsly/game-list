(ns game-list.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::games
 (fn [db]
   (:games db)))

(re-frame/reg-sub
 ::selected-game
 (fn [db]
   (:selected-game db)))
