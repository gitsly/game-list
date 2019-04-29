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
 ::user
 (fn [db]
   (:user db)))

(rf/reg-sub
 ::loading?
 (fn [db]
   (:loading? db)))
