(ns gamelist.utils)

(defn log
  [msg]
  (spit "server.log" (str msg "\n") :append true))
