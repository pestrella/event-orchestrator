;; Author: Paolo Estrella
(ns noble.orcehstrator.core
  (import [com.thisisnoble.javatest Orchestrator]))


(def processors (atom []))
(def publisher (atom nil))

(def orchestrator
  (reify Orchestrator
    (register [this processor]
      (swap! processors conj processor))
    (receive [this event]
      (prn "Recieved event:" (.getId event))
      (.publish @publisher event))
    (setup [this pub]
      (reset! publisher pub))))

(import '[com.thisisnoble.javatest SimpleOrchestratorTest])
(doto (SimpleOrchestratorTest. )
  (.setOrchestrator orchestrator)
  (.tradeEventShouldTriggerAllProcessors))
