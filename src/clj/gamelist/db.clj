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
