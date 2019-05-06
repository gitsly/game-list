(ns gamelist.db
  (:require [monger.core :as mg]
            [monger.joda-time :as jt]
            [monger.collection :as mc]
            [clj-time.core :as time])
  (:import [com.mongodb MongoOptions ServerAddress]
           [org.bson.types ObjectId]
           [org.joda.time DateTimeZone]))

(defn connect []
  "Connect to mongo db"
  (let [^MongoOptions options (mg/mongo-options {:threads-allowed-to-block-for-connection-multiplier 300})
        ^ServerAddress address(mg/server-address "localhost" 27017)
        conn                  (mg/connect address options)
        db                    (mg/get-db conn "test")]
    db))

(defn collection
  "Retrieve collection by name"
  [collection]
  (let [db (connect)
        result(mc/find db collection)]
    (-> result
        seq)))

;; (defn by-id
;;   "Get in collection by id, return keywordized"
;;   [collection oid]
;;   (mc/find-one-as-map (connect) collection { :_id (ObjectId. oid)}))

;; (-> "5cc45839874980367d40cd33"
;;     (ObjectId.))

;; (by-id "Users" "5cc45839874980367d40cd33")
;; (user "David")

(defn user
  "Get single user (keywordized)"
  [user]
  (mc/find-one-as-map (connect) "users" {:user user}))



(defn add-game
  [game]
  (mc/insert-and-return (connect) "games" game))

(defn remove-game
  [game]
  (let [oid (-> game :_id (ObjectId.))]
    (mc/remove-by-id (connect) "games" oid)))


;;------------------------------------------------------------------------------
;; Sample data
;;------------------------------------------------------------------------------

;; Sample 'full' game entry (for testing etc)
(def test-game {:_id "5cb807a48749801ddbd35cbd",
                :name "Karlsa",
                :test "12",
                :added (time/now)
                :rating [{ "Martin" { :value "4", :date "2019-12-20"}}
                         { "Anna" { :value "5", :date "2019-09-20"}}]})
;; And it's insertion
;; (mc/insert-and-return (connect) "games" (dissoc test-game :_id))

(def bullen-users [{:user "David" :secret "flink" }
                   {:user "Anna" :secret "powermästare sill" }
                   {:user "Simon" :secret "zander" }
                   {:user "Martin" :secret "kristall katarina" :moredata {:strength "testas sub"}}])
;; (map #(mc/insert-and-return (connect) "users" %) bullen-users)

;;------------------------------------------------------------------------------
;; Real life data
;;------------------------------------------------------------------------------

;; Time is in hours
(count [{:date "2019-05-03"
         :game "Alchemists"
         :time 4.0
         :participants ["David" "Anna" "Simon" "Martin"]}
        {:date "2019-05-03"
         :game "Tiny epic defenders"
         :time 1.0
         :participants ["David" "Anna" "Simon" "Martin"]}
        ])
