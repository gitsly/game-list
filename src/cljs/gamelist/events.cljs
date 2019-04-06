(ns gamelist.events
  (:require
   [re-frame.core :as rf]
   [gamelist.db :as db]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

;; TODO: Find out this programatically (if localhost 
(defn base-url
  "prepends base-url to part"
  [part]
  (str "http://localhost:10555/" part))


(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(rf/reg-event-db
 ::test
 (fn [db
      [event-name params]]

   (go (let [response (<! (http/get (base-url "wrap")))]
         (println event-name "completed request: " params)
         (println  response))
       db)))

(rf/reg-event-db
 ::set-selected-game
 (fn [db
      [event-name game]]
   (println "set selected game: " game)
   (assoc db :selected-game game)))


(defn new-game
  [name
   db]
  (let [new-id (count (:games db))]
    {:id new-id :name name}))

(defn tojson
  "Create json from clojure map"
  [o]
  (.stringify js/JSON (clj->js o)))



(def testpayload {:body "{\"json\": \"input\"}"
                  :form-params {:foo "bar"}
                  :content-type :json
                  :json-opts {:date-format "yyyy-MM-dd"}
                  :accept :json})

(def testpayload2 {:body {:mymapkey1 "myval1" }
                   :form-params {:foo "bar"}
                   :content-type "application/json"
                   :json-opts {:date-format "yyyy-MM-dd"}
                   :accept :json})

(defn store-new-game [game]
  "Perform cljs-http request,
   Create the new game on remote host using http post"
  (go (let [game-json (tojson { :key1 "pressminator" :key2 "valior" })
            testprm {:body game-json}
            url (base-url "addgame")
            response (<! (http/post url testpayload2))]
        (println testprm)
        (println (:body response)))))


(rf/reg-event-db
::add-game
(fn [db
     [event-name param]]
  (let [game (new-game param db)
        games (:games db)]
    (store-new-game game) 
    (-> db
        (assoc :games (conj games game) )))))

(rf/reg-event-db
::delete-selected-game
(fn [db
     [_ game]]
  (let [pruned-games (remove #(= (:id game) (:id %)) (:games db))]
    (-> db
        (assoc :selected-game nil)
        (assoc :games pruned-games)))))
