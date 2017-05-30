(ns larch.core-test
  (:require  [clojure.test :refer [deftest testing is async]]
             [clojure.core.async :refer [chan <! to-chan]]
             [larch.core :refer [actions->transactions!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(deftest test-db-updates
  (testing "when process-event returns a single-element vector, it should put [event db] on the returned chan"
    (let [event-chan (chan)
          transaction-chan (actions->transactions! event-chan (atom 1) {} (fn [evt db] [(inc db)]))]
      (async done
             (go
               (>! event-chan :event)
               (let [[evt db-update] (<! transaction-chan)]
                 (is (= evt :event))
                 (is (= db-update 2))
                 (done)))))))

(deftest test-command-chan
  (testing "when process-event returns a 2-element vector, it should take all events off the command chan and process them"
    (let [event-chan (chan)
          transaction-chan (actions->transactions! event-chan (atom 1) {}
                                                   (fn [evt db] [(inc db) #(to-chan [:command-event])]))]
      (async done
             (go
               (>! event-chan :event)
               (let [first-transaction (<! transaction-chan)
                     [evt db-update] (<! transaction-chan)]
                 (is (= evt :command-event))
                 (is (= db-update 2))
                 (done)))))))
