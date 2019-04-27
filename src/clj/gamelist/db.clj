(ns gamelist.db
  (:require [monger.core :as mg]
            [monger.joda-time :as jt]
            [monger.collection :as mc])
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
    result))

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
                :rating [{:user "Martin", :value "4", :date "2019-12-20"}
                         {:user "Anna", :value "7", :date "2019-12-21"}]})
;; And it's insertion
;; (mc/insert-and-return (connect) "games" (dissoc test-game :_id))

(def bullen-users [{:user "David" :secret "flink" }
                   {:user "Anna" :secret "powermästare sill" }
                   {:user "Simon" :secret "zander" }
                   {:user "Martin" :secret "kristall katarina" :moredata {:strength "testas sub"}}])
;; (map #(mc/insert-and-return (connect) "users" %) bullen-users)
