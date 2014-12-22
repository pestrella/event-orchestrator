(ns noble.orchestrator-test
  (:require [clojure.test :refer :all]
            [noble.orchestrator :refer [orchestrator events]])
  (:import [com.thisisnoble.javatest SimpleOrchestratorTest]))

(deftest simple-orchestrator-test
  (testing "TradeEvent should trigger all processors"
    (doto (SimpleOrchestratorTest. )
      (.setOrchestrator orchestrator)
      (.tradeEventShouldTriggerAllProcessors)))

  (testing "ShippingEvent should trigger only 2 processors"
    (doto (SimpleOrchestratorTest. )
      (.setOrchestrator orchestrator)
      (.shippingEventShouldTriggerOnly2Processors)))

  (testing "All events processed"
    (is (= 9 (count @events)))))
