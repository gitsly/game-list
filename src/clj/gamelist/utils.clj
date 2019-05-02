(ns gamelist.utils)

;; 2019-04-27T07:30:35.706

(defn log
  [& msg]
  (let [data (conj (vec msg) "\n")]
    (spit "server.log" (apply str data) :append true)))

;; (log "heppas" "was" "a" "ninja")

(defn generate-uuid
  "Generates a unique GUID with format: 'f16f9df5-c9eb-4430-bdef-dcced522c951'"
  []
  (str (java.util.UUID/randomUUID)))

;; Code taken from ring-json middleware impl.
(defn json-request? [request]
  (if-let [type (get-in request [:headers "content-type"])]
    (not (empty? (re-find #"^application/(.+\+)?json" type)))))

;;------------------------------------------------------------------------------
;; To learn
;;------------------------------------------------------------------------------

(when-let [apa 1] apa)

;; Ideomatic way of updating specific item in clojure.
;; https://www.reddit.com/r/Clojure/comments/7wgjty/idiomatic_way_to_find_and_update_an_element_in/
(def users [{:name "James" :age 26}  {:name "John" :age 43}])
;; cheat with javas indexof
(def selected-user (first (filter #(= "John" (:name %)) users)))
(update users (.indexOf users selected-user) assoc :age 8)

;; Better would prob be to use a hash-map instead
(def users2 {"James" {:name "James" :age 26}
             "John" {:name "John" :age 43}})

;;You can then use assoc-in
(assoc-in users2 ["John" :age] 8)




;; Lets build the hash-map
(def some-games [{:_id "5cbc18df8749801ddbd35d49", :name "Alchemists", :rating [{:user "Martin", :value 4, :date "2019-12-20"}
                                                                                {:user "Anna", :value 7, :date "2019-12-21"}]}
                 {:_id "5cbe05788749801ab5520635", :name "Peleponnies"}
                 {:_id "5cbf74b58749804098663fce", :name "Agricola"}
                 {:_id "5cc3e7718749802ff873aa7a", :name "Threader"}
                 {:_id "5cc3f2c2874980367d40cd18", :name "TimeCops", :added "2019-04-27T06:12:18Z" }])

(map #(let [game %]
        {(:name game) game })
     some-games)

(defn make-hash
  [games]
  (-> (map #(let [game %]
              {(:name game) game })
           games)))

(-> some-games make-hash)

(hash-set 1 2 3)
{1 nil 2 nil 3 nil}

(assoc-in (make-hash some-games) ["TimeCops" :added] "2019-04-27")

;;------------------------------------------------------------------------------

(defn make-entry
  [game]
  {(:name game) game})
(map #(make-entry %) some-games)
