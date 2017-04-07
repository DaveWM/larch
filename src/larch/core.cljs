(ns larch.core
  (:require [cljs.core.async :refer [chan <! >! pipe]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(defn actions->transactions! [event-chan app-db dependencies process-event]
  "Takes an event channel, the app db/state, a map of dependencies (which cause side effects) and a process-event function"
  (let [transaction-chan (chan)]
    (go-loop []
      (let [event (<! event-chan)
            [db-update command] (process-event event @app-db)]
        (when db-update
          (>! transaction-chan [event db-update]))
        (when-let [command-chan (and command (command @app-db dependencies))]
          (pipe command-chan event-chan false))
        (recur)))
    transaction-chan))
