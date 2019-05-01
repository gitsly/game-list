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
