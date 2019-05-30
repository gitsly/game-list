(ns gamelist.events.chat
  (:require
   [re-frame.core :as rf]
   [gamelist.db :as db]
   [gamelist.utils :as utils]
   [cljs-http.client :as http]
   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
   [cljs.core.async :as async :refer [<! >!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

;; TODO: move to events/chat.cljs
(defn get-chat
  "takes id of the chat to fetch single string
  stores result in 'chat' subscription of app-db"
  [session]
  (let [payload (utils/json-request {:session session})
        url (str "list/chat/" session)]
    ;; (println "GET-CHAT:" url)
    (go (let [response (<! (http/get url))]
          (rf/dispatch [::get-chat-response response])))))

;; Get this to work...
(rf/reg-event-db
  ::get-chat
  (fn [db
       [event-name session]]
    (println "Getting chat content: " session)
    (get-chat session)
    db))

(rf/reg-event-db
::get-chat-response
(fn
  [db [_ response]]
  (let [body (-> response :body)
        chat body]
    (println "client: get-chat-response: " body)
    (-> db
        (assoc :chat chat)))))


(rf/reg-event-db
::add-chat
(fn [db
     [event-name
      session
      content]]
  (println "add-chat-event: " session ", content: " content)
  (let [msg {:entry {:content content :user (:user db) }
             :session session}
        url "list/addchat"
        payload (utils/json-request msg)]
    (go (let [response (<! (http/put url payload))]
          (rf/dispatch [::add-chat-response response])))
    (-> db
        (assoc :loading? true)))))

(rf/reg-event-db
::add-chat-response
(fn
  [db [_ response]]
  (let [chat (:body response)]
    ;; (println "client: add-chat-response: " chat)
    (-> db
        (assoc :loading? false)
        (assoc :chat chat)))))
