(ns gamelist.db
  (:require [monger.core :as mg]
            [monger.joda-time :as jt]
            [monger.collection :as mc]
            [gamelist.utils :refer [log]]
            [clj-time.core :as time])
  (:import [com.mongodb MongoOptions ServerAddress]
           [org.bson.types ObjectId]
           [org.joda.time DateTimeZone]))

;;(def db-host "localhost")
;;(def db-name "test")

(def db-host "mongo") ; If using docker-compose, ensure this is referring to image name
(def db-name "live")

(defn connect []
"Connect to mongo db"
(let [^MongoOptions options (mg/mongo-options {:threads-allowed-to-block-for-connection-multiplier 300})
      ^ServerAddress address(mg/server-address db-host 27017)
      conn                  (mg/connect address options)
      db                    (mg/get-db conn db-name)]
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

(defn update-game
[game]
(let [oid (-> game :_id (ObjectId.))
      game-no-id (dissoc game :_id)]
  ;; (log "Db update game: " game-no-id)
  (mc/update-by-id (connect) "games" oid game-no-id)))

(defn remove-game
[game]
(let [oid (-> game :_id (ObjectId.))]
  (mc/remove-by-id (connect) "games" oid)))


;; Test update functionality
;; (let [game {:_id "5ccfdc046734b02fc4acaffc",
;;             :name "Test", :added "2019-05-06T07:02:28Z",
;;             :rating {"Martin" {:value 40}}}]
;;   (update-game game))

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
                   {:user "Anna" :secret "powermästaresill" }
                   {:user "Simon" :secret "zander" }
                   {:user "Martin" :secret "kristallkatarina" :moredata {:strength "testas sub"}}])
;; (map #(mc/insert-and-return (connect) "users" %) bullen-users)

;;------------------------------------------------------------------------------
;; Real life data
;;------------------------------------------------------------------------------

;; Time is in hours
(count [{:date "2019-05-12"
         :game "Innovation"
         :time 1.3
         :winner "Anna"
         :participants ["David" "Anna" "Martin"]}
        {:date "2019-05-12"
         :game "Biblios"
         :time 0.5
         :winner "Martin"
         :participants ["David" "Anna" "Martin"]}
        {:date "2019-05-03"
         :game "Alchemists"
         :time 4.0
         :winner "David"
         :participants ["David" "Anna" "Simon" "Martin"]}
        {:date "2019-05-03"
         :game "Tiny epic defenders"
         :time 1.0
         :winner "all"
         :participants ["David" "Anna" "Simon" "Martin"]
         }
        {:date "2019-03-23" :game "Agricola" :time 3.5
         :participants ["David" "Anna" "Simon" "Martin"]
         }])

;; (def all-games
;;   [{:name "Star Realms",:added "2019-05-06T13:12:34.370Z",:rating {:Martin {:value 50},:David {:value 50}},:updated "2019-05-11T11:39:21.319Z"}
;;    {:name "Sheriff of Nottingham",:added "2019-05-06T13:21:46.864Z",:rating {:Martin {:value 40},:David {:value 60},:Simon {:value 50}},:updated "2019-05-11T12:31:21.006Z"}
;;    {:name "Alchemists",:added "2019-05-06T19:51:27Z",:rating {:Martin {:value 80},:David {:value 70},:Simon {:value 80}},:updated "2019-05-11T12:28:42.666Z"}
;;    {:name "Civilization the Board Game",:added "2019-05-11T11:30:42.352Z",:rating {:David {:value 50},:Simon {:value 40}},:updated "2019-05-11T12:33:49.042Z"}
;;    {:name "Tumult Royal",:added "2019-05-11T11:30:55.950Z"}
;;    {:name "7 Wonders",:added "2019-05-11T11:31:03.156Z",:rating {:David {:value 60},:Simon {:value 70},:Martin {:value 40}},:updated "2019-05-11T18:13:47.850Z"}
;;    {:name "Pandemic Legacy Season 1",:added "2019-05-11T11:31:22.368Z",:rating {:David {:value 80},:Simon {:value 100}},:updated "2019-05-11T12:29:35.965Z"}
;;    {:name "Biblios",:added "2019-05-11T11:31:29.759Z",:rating {:David {:value 70}},:updated "2019-05-11T11:45:32.867Z"}
;;    {:name "Descent: Journeys in the Dark - second edition",:added "2019-05-11T11:32:33.989Z",:rating {:David {:value 40}},:updated "2019-05-11T11:42:50.322Z"}
;;    {:name "Innovation",:added "2019-05-11T11:32:40.478Z",:rating {:David {:value 60}},:updated "2019-05-11T11:44:14.458Z"}
;;    {:name "Agricola",:added "2019-05-11T11:33:05.190Z",:rating {:David {:value 70},:Simon {:value 80}},:updated "2019-05-11T12:33:10.226Z"}
;;    {:name "Core Worlds",:added "2019-05-11T11:33:20.275Z",:rating {:David {:value 60}},:updated "2019-05-11T11:39:45.926Z"}
;;    {:name "Legends of Andor",:added "2019-05-11T11:33:30.411Z",:rating {:David {:value 60}},:updated "2019-05-11T11:42:12.116Z"}
;;    {:name "Charterstone",:added "2019-05-11T11:33:38.770Z",:rating {:David {:value 60},:Martin {:value 60},:Simon {:value 60}},:updated "2019-05-11T12:29:46.864Z"}
;;    {:name "Smallworld",:added "2019-05-11T11:33:49.484Z",:rating {:David {:value 60}},:updated "2019-05-11T11:40:25.258Z"}
;;    {:name "Roll for the Galaxy",:added "2019-05-11T11:33:59.793Z",:rating {:David {:value 60},:Martin {:value 70},:Simon {:value 70}},:updated "2019-05-11T18:13:55.608Z"}
;;    {:name "7th Continent",:added "2019-05-11T11:34:09.642Z",:rating {:David {:value 70},:Simon {:value 70},:Martin {:value 50}},:updated "2019-05-11T18:11:37.767Z"}
;;    {:name "Galaxy Trucker",:added "2019-05-11T11:34:30.182Z"}
;;    {:name "Gloomhaven",:added "2019-05-11T11:34:50.175Z",:rating {:David {:value 90},:Simon {:value 90},:Martin {:value 90}},:updated "2019-05-11T18:13:11.198Z"}
;;    {:name "Terra Mystica",:added "2019-05-11T11:35:17.102Z",:rating {:David {:value 70},:Martin {:value 70}},:updated "2019-05-11T18:11:30.076Z"}
;;    {:name "Stone Age",:added "2019-05-11T11:35:22.908Z",:rating {:David {:value 50},:Martin {:value 60},:Simon {:value 50}},:updated "2019-05-11T12:32:20.987Z"}
;;    {:name "Tiny Epic Defenders",:added "2019-05-11T11:35:42.280Z",:rating {:David {:value 70},:Martin {:value 60},:Simon {:value 70}},:updated "2019-05-11T12:34:10.843Z"}
;;    {:name "Peleponnes Card Game",:added "2019-05-11T11:36:03.681Z",:rating {:David {:value 50},:Simon {:value 60}},:updated "2019-05-11T12:33:45.658Z"}
;;    {:name "Spectaculum",:added "2019-05-11T11:36:14.513Z",:rating {:David {:value 60}},:updated "2019-05-11T11:42:25.691Z"}
;;    {:name "Kepler 3042",:added "2019-05-11T11:36:20.906Z",:rating {:David {:value 70},:Martin {:value 70},:Simon {:value 70}},:updated "2019-05-11T12:37:36.956Z"}
;;    {:name "Dominant Species",:added "2019-05-11T11:36:40.496Z"}
;;    {:name "Black Fleet",:added "2019-05-11T11:36:48.849Z",:rating {:David {:value 50},:Simon {:value 50}},:updated "2019-05-11T12:31:35.415Z"}
;;    {:name "City of Iron ",:added "2019-05-11T11:37:01.303Z",:rating {:David {:value 80}},:updated "2019-05-11T11:46:47.601Z"}
;;    {:name "Above and Below",:added "2019-05-11T11:37:40.820Z",:rating {:David {:value 70}},:updated "2019-05-11T11:43:57.601Z"}
;;    {:name "The Ancient World",:added "2019-05-11T11:37:52.760Z",:rating {:David {:value 80}},:updated "2019-05-11T11:47:29.569Z"}
;;    {:name "Power Grid",:added "2019-05-11T11:37:57.870Z",:rating {:David {:value 70},:Simon {:value 70}},:updated "2019-05-11T12:32:16.748Z"}
;;    {:name "Eclipse",:added "2019-05-11T11:38:04.391Z",:rating {:David {:value 80},:Martin {:value 70},:Simon {:value 40}},:updated "2019-05-11T12:37:22.005Z"}
;;    {:name "Museum",:added "2019-05-11T12:15:24.455Z",:rating {:Simon {:value 80}},:updated "2019-05-11T12:32:57.784Z"}
;;    {:name "Stockpile",:added "2019-05-11T12:15:32.428Z",:rating {:Simon {:value 70},:Martin {:value 60}},:updated "2019-05-11T18:12:44.371Z"}
;;    {:name "Spyfall 2",:added "2019-05-11T12:15:43.999Z",:rating {:Simon {:value 70}},:updated "2019-05-11T12:28:09.119Z"}
;;    {:name "Obsession",:added "2019-05-11T12:15:48.699Z",:rating {:Simon {:value 70}},:updated "2019-05-11T12:28:13.753Z"}
;;    {:name "Mysterium",:added "2019-05-11T12:15:54.358Z",:rating {:Simon {:value 80}},:updated "2019-05-11T12:30:30.003Z"}
;;    {:name "Crypto Currency",:added "2019-05-11T12:16:10.935Z",:rating {:Simon {:value 60}},:updated "2019-05-11T12:30:23.803Z"}
;;    {:name "Secrets",:added "2019-05-11T12:16:14.698Z",:rating {:Simon {:value 70}},:updated "2019-05-11T12:31:49.646Z"}
;;    {:name "The Lost Expedition",:added "2019-05-11T12:16:26.692Z",:rating {:Simon {:value 70},:Martin {:value 70}},:updated "2019-05-11T18:11:51.264Z"}
;;    {:name "Mombasa",:added "2019-05-11T12:16:32.006Z",:rating {:Simon {:value 70},:Martin {:value 70}},:updated "2019-05-11T18:14:05.038Z"}
;;    {:name "Alchemists",:added "2019-05-11T12:16:45.696Z",:rating {:Simon {:value 80}},:updated "2019-05-11T12:32:38.580Z"}
;;    {:name "Scythe",:added "2019-05-11T12:16:57Z",:rating {:Martin {:value 60}},:updated "2019-05-11T18:11:57.008Z"}
;;    {:name "Planet Steam",:added "2019-05-11T12:17:24.262Z",:rating {:Simon {:value 80}},:updated "2019-05-11T12:30:45.694Z"}
;;    {:name "Dead of Winter",:added "2019-05-11T12:17:33.757Z",:rating {:Simon {:value 70},:Martin {:value 50}},:updated "2019-05-11T18:14:00.504Z"}
;;    {:name "Formula D",:added "2019-05-11T12:17:38.903Z",:rating {:Simon {:value 70},:Martin {:value 60}},:updated "2019-05-11T18:13:39.273Z"}
;;    {:name "Concept",:added "2019-05-11T12:17:51.736Z",:rating {:Simon {:value 70}},:updated "2019-05-11T12:34:00.110Z"}
;;    {:name "Panamax",:added "2019-05-11T12:18:02.623Z",:rating {:Simon {:value 70},:Martin {:value 60}},:updated "2019-05-11T18:11:44.011Z"}
;;    {:name "Le Havre",:added "2019-05-11T12:18:13.196Z",:rating {:Simon {:value 70},:Martin {:value 50}},:updated "2019-05-11T18:14:08.147Z"}
;;    {:name "Pandemic",:added "2019-05-11T12:18:20.862Z",:rating {:Simon {:value 70}},:updated "2019-05-11T12:32:33.825Z"}
;;    {:name "Pandemic Iberia",:added "2019-05-11T12:18:33.300Z",:rating {:Simon {:value 80}},:updated "2019-05-11T12:32:51.400Z"}
;;    {:name "Food Chain Magnate",:added "2019-05-11T12:18:41.019Z"}
;;    {:name "Millions of Dollars",:added "2019-05-11T12:18:51.354Z"}
;;    {:name "Avalon",:added "2019-05-11T12:18:57.855Z",:rating {:Simon {:value 80}},:updated "2019-05-11T12:31:27.667Z"}
;;    {:name "Secret Hitler",:added "2019-05-11T12:19:05.274Z",:rating {:Simon {:value 90},:Martin {:value 70}},:updated "2019-05-11T18:12:56.464Z"}
;;    {:name "Mafia de Cuba",:added "2019-05-11T12:19:15.702Z"}
;;    {:name "Deception - Murder in Hong Kong",:added "2019-05-11T12:19:52.152Z",:rating {:Simon {:value 70}},:updated "2019-05-11T12:33:33.914Z"}
;;    {:name "Unlock",:added "2019-05-11T12:19:59.503Z"}
;;    {:name "Champions of Midgard",:added "2019-05-11T12:20:12.076Z",:rating {:Simon {:value 80}},:updated "2019-05-11T12:31:57.866Z"}
;;    {:name "Robinson Crusoe",:added "2019-05-11T12:20:28.159Z",:rating {:Simon {:value 80}},:updated "2019-05-11T12:29:11.243Z"}
;;    {:name "Terraforming Mars",:added "2019-05-11T12:20:35.031Z",:rating {:Simon {:value 60}},:updated "2019-05-11T12:33:25.697Z"}
;;    {:name "Through the Ages",:added "2019-05-11T12:20:46.385Z",:rating {:Simon {:value 70}},:updated "2019-05-11T12:33:02.157Z"}
;;    {:name "Dominion",:added "2019-05-11T12:21:20.359Z",:rating {:Simon {:value 40}},:updated "2019-05-11T12:28:31.997Z"}
;;    {:name "Sagan om Ringen",:added "2019-05-11T12:21:28.220Z",:rating {:Simon {:value 50}},:updated "2019-05-11T12:35:13.266Z"}
;;    {:name "Twilight Struggle",:added "2019-05-11T12:21:46.364Z",:rating {:Simon {:value 90}},:updated "2019-05-11T12:29:17.403Z"}
;;    {:name "Legacy - Duke de Crecy",:added "2019-05-11T12:22:03.223Z",:rating {:Simon {:value 60},:Martin {:value 70}},:updated "2019-05-11T18:12:38.438Z"}
;;    {:name "Svea Rike",:added "2019-05-11T12:22:14.067Z",:rating {:Simon {:value 40}},:updated "2019-05-11T12:29:56.632Z"}
;;    {:name "Betrayal at the House on the Hill",:added "2019-05-11T12:23:04.808Z",:rating {:Simon {:value 20}},:updated "2019-05-11T12:28:02.273Z"}
;;    {:name "Sagan om Ringen Risk",:added "2019-05-11T12:23:20.755Z",:rating {:Simon {:value 50}},:updated "2019-05-11T12:32:27.232Z"}
;;    {:name "Louis XIV",:added "2019-05-11T12:23:46.349Z",:rating {:Simon {:value 70}},:updated "2019-05-11T12:26:54.923Z"}
;;    {:name "Citadels",:added "2019-05-11T12:24:15.989Z",:rating {:Simon {:value 70}},:updated "2019-05-11T12:32:45.196Z"}
;;    {:name "Epic Spell Wars - Skullfyre",:added "2019-05-11T12:24:38.758Z",:rating {:Simon {:value 70}},:updated "2019-05-11T12:32:24.207Z"}
;;    {:name "ONU Werewolf",:added "2019-05-11T12:25:15.646Z",:rating {:Simon {:value 70}},:updated "2019-05-11T12:35:54.072Z"}
;;    {:name "ONU Alien",:added "2019-05-11T12:25:21.568Z"}
;;    {:name "ONU Vampire",:added "2019-05-11T12:25:26.352Z",:rating {:Simon {:value 50}},:updated "2019-05-11T12:32:09.590Z"}
;;    {:name "Noises at Night",:added "2019-05-11T12:25:33.822Z",:rating {:Simon {:value 60}},:updated "2019-05-11T12:30:55.720Z"}])