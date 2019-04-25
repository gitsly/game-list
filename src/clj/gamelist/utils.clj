(ns gamelist.utils)

(defn log
  [& msg]
  (let [data (conj (vec msg) "\n")]
    (spit "server.log" (apply str data) :append true)))

;; (log "heppas" "was" "a" "ninja")
