(ns larch.core
  (:require [clojure.core.async :refer [chan <! >! pipe go-loop]]))

(defn msgs->updates! [msg-chan model dependencies process-msg]
  "Takes an message channel, the app model, a map of dependencies (which cause side effects) and a process-msg function.
   The process-msg function takes the event and the value of the model, and returns a 2 element tuple of the model update and a function that takes the dependencies and returns a channel of messages."
  (let [update-chan (chan)]
    (go-loop []
      (let [msg (<! msg-chan)
            [model-update command] (process-msg msg @model)]
        (when model-update
          (>! update-chan [msg model-update]))
        (when-let [command-chan (and command (command @model dependencies))]
          (pipe command-chan msg-chan false))
        (recur)))
    update-chan))
