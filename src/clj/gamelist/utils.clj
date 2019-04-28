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
