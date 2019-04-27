(ns gamelist.utils
  )

(defn now
  "Returns joda time now"
  []
  "-")

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
